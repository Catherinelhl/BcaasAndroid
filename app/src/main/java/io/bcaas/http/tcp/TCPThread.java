package io.bcaas.http.tcp;


import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.gson.ResponseJson;
import io.bcaas.gson.jsonTypeAdapter.GenesisVOTypeAdapter;
import io.bcaas.gson.jsonTypeAdapter.TransactionChainVOTypeAdapter;
import io.bcaas.http.MasterServices;
import io.bcaas.listener.TCPRequestListener;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.ecc.Sha256Tool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.tools.gson.JsonTool;
import io.bcaas.vo.ClientIpInfoVO;
import io.bcaas.vo.DatabaseVO;
import io.bcaas.vo.GenesisVO;
import io.bcaas.vo.PaginationVO;
import io.bcaas.vo.TransactionChainChangeVO;
import io.bcaas.vo.TransactionChainSendVO;
import io.bcaas.vo.TransactionChainVO;
import io.bcaas.vo.WalletVO;

/**
 * @author catherine.brainwilliam
 * update 2018/08/31
 * TCP请求服务端，请求R区块的数据
 * <p>
 * 连续重试5次，进行休眠10s，再继续；防止应用死循环导致退出
 */
public class TCPThread extends Thread {
    private static String TAG = TCPThread.class.getSimpleName();

    /*向服务器TCP发送的数据*/
    private String writeStr;
    /*是否存活*/
    public static volatile boolean keepAlive = true;
    /*建立連結的socket*/
    public static volatile Socket socket = null;
    /*得到当前需要去签章的交易区块 */
    private TransactionChainVO currentSendVO;
    /*监听TCP的一些返回，通知界面作出改动 */
    private TCPRequestListener tcpRequestListener;
    /*存儲當前請求回來的需要簽章的交易區塊，做一個現城池，異步處理*/
    private static Queue<TransactionChainVO> getWalletWaitingToReceiveQueue = new LinkedList<>();
    /*声明一个参数用来存储更改授权代表的返回状态，默认是「change」*/
    private static String changeStatus = Constants.CHANGE;
    /*用来停止socket请求,这个比kill()大*/
    private static boolean stopSocket = false;
    /*当前重连的次数*/
    private int resetCount;
    /*当前TCP连接的SAN地址信息*/
    private static ClientIpInfoVO clientIpInfoVO;
    /*当前连接的网络是否是内网*/
    private boolean isInternal;
    /*是否開啟接收線程*/
    private boolean isStartReceive;
    /*得到當前建立長鏈接的Handler*/
    private static TCPReceiveThread tcpReceiveThread;
    /*得到當前建立的長鏈接的looper*/
    private static Looper TCPReceiveLooper;

    public TCPThread(String writeString, TCPRequestListener tcpRequestListener) {
        this.writeStr = writeString;
        this.tcpRequestListener = tcpRequestListener;
    }

    @Override
    public final void run() {
        isStartReceive = false;
        LogTool.d(MessageConstants.socket.TAG);
        /*1:創建socket*/
        stopSocket = false;
        compareWalletExternalIpWithSANExternalIp();
        //连接socket
        socket = buildSocket();
    }

    /* 重新建立socket连接*/
    private Socket buildSocket() {
        //当前stopSocket为false的时候才能允许连接
        if (!stopSocket) {
            isResetExceedTheLimit();
            resetCount++;
            try {
                Socket socket = new Socket();
                SocketAddress socAddress = new InetSocketAddress(BcaasApplication.getTcpIp(), BcaasApplication.getTcpPort());
                //设置socket连接超时时间，如果是内网的话，那么5s之后重连，如果是外网10s之后重连
                socket.connect(socAddress, isInternal ? Constants.ValueMaps.sleepTime50000 : Constants.ValueMaps.sleepTime100000);
                socket.setKeepAlive(true);//让其在建立连接的时候保持存活
                keepAlive = true;
                if (socket.isConnected()) {
                    writeTOSocket(socket, writeStr);
                    if (isStartReceive) {
                        keepAlive = true;
                    } else {
                        /*2:开启接收线程*/
                        if (tcpReceiveThread == null) {
                            tcpReceiveThread = new TCPReceiveThread(socket);
                        }
                        tcpReceiveThread.start();
                    }
                }
                return socket;
            } catch (Exception e) {
                e.printStackTrace();
                LogTool.e(TAG, MessageConstants.socket.RESET_AN + e.getMessage());
                if (e instanceof ConnectException) {
                    //如果当前连接不上，代表需要重新设置AN,内网5s，外网10s
                    resetSAN();

                }
                tcpRequestListener.stopToHttpToRequestReceiverBlock();
            }
        }
        return null;

    }

    /*判断重置是否超过限定,重置次数已经5次了，那么让他睡10s，然后继续*/
    private void isResetExceedTheLimit() {
        if (resetCount >= MessageConstants.socket.RESET_MAX_COUNT) {
            resetCount = 0;
            try {
                Thread.sleep(Constants.ValueMaps.sleepTime10000);
            } catch (InterruptedException e) {
                LogTool.d(TAG, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /*重新连接SAN*/
    private void resetSAN() {
        LogTool.d(TAG, MessageConstants.socket.RESET_AN);
        //当前stopSocket为false的时候才继续重连
        if (!stopSocket) {
            MasterServices.reset();
            compareWalletExternalIpWithSANExternalIp();
            //连接socket
            socket = buildSocket();
        }
    }

    /**
     * 比对当前设备的外网IP与SAN的外网IP
     *
     * @return
     */
    private void compareWalletExternalIpWithSANExternalIp() {
        /*得到当前设备的外网IP*/
        String walletExternalIp = BcaasApplication.getWalletExternalIp();
        /*得到当前服务器返回的可以连接的SAN的内外网IP&Port*/
        clientIpInfoVO = BcaasApplication.getClientIpInfoVO();
        if (clientIpInfoVO == null) {
            tcpRequestListener.getDataException(MessageConstants.socket.CLIENT_INFO_NULL);
            return;
        }
        /*比对当前的APP的外网IP与服务器返回的外网IP，如果相同， 那么就连接内网，否则连接外网*/
        if (StringTool.equals(walletExternalIp, clientIpInfoVO.getExternalIp())) {
            //连接内网IP&Port
            connectInternalIP();
        } else {
            //连接外网IP&Port
            connectExternalIP();
        }
    }

    /*连接内网IP&Port*/
    private void connectExternalIP() {
        isInternal = false;
        LogTool.d(TAG, MessageConstants.socket.CONNECT_EXTERNAL_IP);
        BcaasApplication.setTcpIp(clientIpInfoVO.getExternalIp());
        BcaasApplication.setTcpPort(clientIpInfoVO.getExternalPort());
        BcaasApplication.setHttpPort(clientIpInfoVO.getRpcPort());
    }

    /*连接外网IP&Port*/
    private void connectInternalIP() {
        isInternal = true;
        LogTool.d(TAG, MessageConstants.socket.CONNECT_INTERNAL_IP);
        BcaasApplication.setTcpIp(clientIpInfoVO.getInternalIp());
        BcaasApplication.setTcpPort(clientIpInfoVO.getInternalPort());
        BcaasApplication.setHttpPort(clientIpInfoVO.getInternalRpcPort());
    }

    /**
     * 用于向服务端写入数据
     *
     * @param socket   socket对象
     * @param writeStr 写入字符串
     */
    public void writeTOSocket(Socket socket, String writeStr) {
        PrintWriter printWriter;
        try {
            if (socket.isConnected()) {
                //向服务器端发送数据
                printWriter = new PrintWriter(socket.getOutputStream());
                printWriter.write(writeStr + Constants.CHANGE_LINE);
                printWriter.flush();
                LogTool.d(TAG, MessageConstants.socket.SEND_DATA + writeStr);
            }
        } catch (Exception e) {
            LogTool.e(TAG, MessageConstants.socket.CONNECT_EXCEPTION);
            e.printStackTrace();
        }
    }

    public static boolean allowConnect() {
        return !stopSocket;
    }

    /*接受服务端响应数据*/
    private class TCPReceiveThread extends Thread {
        private Socket socket;

        public TCPReceiveThread(Socket client) {
            socket = client;
        }

        public final void run() {
            Looper.prepare();
            TCPReceiveLooper = Looper.myLooper();
            Gson gson = GsonTool.getGson();
            //判斷當前是活著且非阻塞的狀態下才能繼續前行
            while (keepAlive && !isInterrupted()) {
                isStartReceive = true;
                tcpRequestListener.httpToRequestReceiverBlock();
                LogTool.d(TAG, MessageConstants.socket.TAG + socket + stopSocket);
                try {
                    //读取服务器端数据
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    try {
                        while (socket.isConnected() && keepAlive) {
                            try {
                                // 發送心跳包
                                socket.sendUrgentData(MessageConstants.socket.HEART_BEAT);
                            } catch (Exception e) {
                                LogTool.d(TAG, MessageConstants.socket.CONNECT_EXCEPTION + e.getMessage());
                                socket.close();
                                break;
                            }
                            String readLine = bufferedReader.readLine();
                            if (readLine != null && readLine.trim().length() != 0) {
                                LogTool.d(TAG, MessageConstants.socket.TCP_RESPONSE + readLine);
                                ResponseJson responseJson = gson.fromJson(readLine, ResponseJson.class);
                                if (responseJson != null) {
                                    int code = responseJson.getCode();
                                    /*匹配异常code，如果是3006||3008，则是token过期，需要提示其重新登录*/
                                    if (code == MessageConstants.CODE_3006
                                            || code == MessageConstants.CODE_3008) {
                                        LogTool.d(TAG, MessageConstants.socket.STOP_SOCKET_TO_LOGIN);
                                        if (bufferedReader != null) {
                                            bufferedReader.close();
                                        }
                                        if (!stopSocket) {
                                            //Redis data not found,need logout
                                            tcpRequestListener.toLogin();
                                            stopSocket = true;
                                            BcaasApplication.setKeepHttpRequest(false);
                                        }
                                        break;
                                    }
                                    if (code != MessageConstants.CODE_200) {
                                        tcpRequestListener.getDataException(responseJson.getMessage());
                                        LogTool.d(TAG, MessageConstants.socket.CODE_EXCEPTION + responseJson.getMessage());
                                    }
                                    String methodName = responseJson.getMethodName();
                                    if (StringTool.isEmpty(methodName)) {
                                        LogTool.d(TAG, MessageConstants.METHOD_NAME_IS_NULL);
                                    } else {
                                        switch (methodName) {
                                            /*得到最新的余额*/
                                            case MessageConstants.socket.GETLATESTBLOCKANDBALANCE_SC:
                                                getLatestBlockAndBalance_SC(responseJson);
                                                break;
                                            /*发送*/
                                            case MessageConstants.socket.GETSENDTRANSACTIONDATA_SC:
                                                getSendTransactionData_SC(responseJson);
                                                break;
                                            /*签章Receive*/
                                            case MessageConstants.socket.GETRECEIVETRANSACTIONDATA_SC:
                                                getReceiveTransactionData_SC(responseJson);
                                                break;
                                            /*得到最新的R区块*/
                                            case MessageConstants.socket.GETWALLETWAITINGTORECEIVEBLOCK_SC:
                                                getWalletWaitingToReceiveBlock_SC(responseJson);
                                                break;
                                            /*获取最新的Change区块*/
                                            case MessageConstants.socket.GETLATESTCHANGEBLOCK_SC:
                                                getLatestChangeBlock_SC(responseJson);
                                                break;
                                            /*响应Change区块数据*/
                                            case MessageConstants.socket.GETCHANGETRANSACTIONDATA_SC:
                                                getChangeTransactionData_SC(responseJson);
                                                break;
                                            /*需要重置AN*/
                                            case MessageConstants.socket.CLOSESOCKET_SC:
                                                resetSAN();
                                                break;
                                            default:
                                                LogTool.d(TAG, MessageConstants.METHOD_NAME_ERROR + methodName);
                                                break;
                                        }
                                    }
                                } else {
                                    LogTool.d(TAG, MessageConstants.RESPONSE_IS_NULL);
                                }

                            }
                        }
                    } catch (Exception e) {
                        LogTool.e(TAG, e.getMessage());
                        e.printStackTrace();
                        break;
                    } finally {
                        if (bufferedReader != null) {
                            bufferedReader.close();
                        }
                        tcpRequestListener.stopToHttpToRequestReceiverBlock();
                        kill(false);
                        socket = buildSocket();
                        break;
                    }
                } catch (Exception e) {
                    LogTool.e(TAG, e.getMessage());
                    tcpRequestListener.stopToHttpToRequestReceiverBlock();
                    e.printStackTrace();
                    break;
                }
            }
            Looper.loop();
        }
    }

    /**
     * "取得未簽章R區塊的Send區塊 & 取最新的R區塊 & wallet餘額"
     *
     * @param responseJson
     */
    public void getWalletWaitingToReceiveBlock_SC(ResponseJson responseJson) {
        LogTool.d(TAG, "step 2:getWalletWaitingToReceiveBlock_SC");
        Gson gson = GsonTool.getGson();
        if (responseJson == null) {
            return;
        } else {
            List<PaginationVO> paginationVOList = responseJson.getPaginationVOList();
            if (paginationVOList != null) {
                PaginationVO paginationVO = paginationVOList.get(0);
                List<Object> objList = paginationVO.getObjectList();
                if (ListTool.noEmpty(objList)) {
                    //有未签章的区块
                    for (Object obj : objList) {
                        TransactionChainVO transactionChainVO = gson.fromJson(gson.toJson(obj), TransactionChainVO.class);
                        getWalletWaitingToReceiveQueue.offer(transactionChainVO);
                        getTransactionVOOfQueue(responseJson, false);
                    }
                }
                BcaasApplication.setNextObjectId(paginationVO.getNextObjectId());
            }
            WalletVO walletVO = responseJson.getWalletVO();
            String walletBalance = walletVO != null ? walletVO.getWalletBalance() : "0";
            tcpRequestListener.showWalletBalance(walletBalance);//通知页面更新当前的余额
        }
    }

    /**
     * 处理线程下面需要处理的R区块
     *
     * @param responseJson
     */
    public void getReceiveTransactionData_SC(ResponseJson responseJson) {
        if (responseJson.getCode() == MessageConstants.CODE_200) {
            getTransactionVOOfQueue(responseJson, true);
        }
    }

    /**
     * 取得线程池里面需要签章的交易
     *
     * @param responseJson
     * @param isReceive    是否是Receive请求进入,如果是，就需要向首页更新上一笔签收成功的数据
     */
    private void getTransactionVOOfQueue(ResponseJson responseJson, boolean isReceive) {
        Gson gson = GsonTool.getGson();
        try {
            if (isReceive){
                tcpRequestListener.refreshTransactionRecord();
            }
            //重新取得线程池里面的数据
            currentSendVO = getWalletWaitingToReceiveQueue.poll();
            if (currentSendVO != null) {
                String amount = gson.fromJson(gson.toJson(currentSendVO.getTc()), TransactionChainSendVO.class).getAmount();
                receiveTransaction(amount, currentSendVO, responseJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * "取最新的區塊 &wallet餘額"
     *
     * @param responseJson
     */
    public void getLatestBlockAndBalance_SC(ResponseJson responseJson) {
        LogTool.d(TAG, "step 2:getLatestBlockAndBalance_SC");
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                // 可能是Send/open区块
                .registerTypeAdapter(TransactionChainVO.class, new TransactionChainVOTypeAdapter())
                .create();
        DatabaseVO databaseVO = responseJson.getDatabaseVO();
        if (databaseVO == null) {
            return;
        }
        String destinationWallet = BcaasApplication.getDestinationWallet();
        String transactionAmount = BcaasApplication.getTransactionAmount();
        if (StringTool.isEmpty(destinationWallet)) {
            LogTool.d(TAG, MessageConstants.DESTINATIONWALLET_IS_NULL);
            return;
        }
        if (StringTool.isEmpty(transactionAmount)) {
            LogTool.d(TAG, MessageConstants.AMOUNT_IS_NULL);
            return;
        }
        try {
            LogTool.d(TAG, "transactionAmount:" + transactionAmount + ",destinationWallet:" + destinationWallet);
            WalletVO walletVO = responseJson.getWalletVO();
            if (walletVO != null) {
                long balanceAfterAmount = Long.parseLong(walletVO.getWalletBalance()) - Long.parseLong(transactionAmount);
                if (balanceAfterAmount < 0) {
                    tcpRequestListener.noEnoughBalance();
                    return;
                }
                tcpRequestListener.showWalletBalance(walletVO.getWalletBalance());//通知页面更新当前的余额
                String previousBlockStr = gson.toJson(databaseVO.getTransactionChainVO());
                LogTool.d(TAG, previousBlockStr);
                String previous = Sha256Tool.doubleSha256ToString(previousBlockStr);
                // 2018/8/22请求AN send请求
                responseJson = MasterServices.sendAuthNode(previous, walletVO.getBlockService(), destinationWallet, balanceAfterAmount, transactionAmount, walletVO.getRepresentative());

                if (responseJson != null) {
                    int code = responseJson.getCode();
                    if (code == MessageConstants.CODE_200) {
                        LogTool.d(TAG, MessageConstants.HTTP_SEND_SUCCESS);
                    } else if (code == MessageConstants.CODE_2002) {
                        tcpRequestListener.sendTransactionFailure(responseJson.getMessage());
                    } else {
                        tcpRequestListener.sendTransactionFailure(MessageConstants.SEND_HTTP_FAILED);

                    }
                }

            } else {
                tcpRequestListener.tcpResponseDataError(MessageConstants.NULL_WALLET);
                return;
            }

        } catch (Exception e) {
            LogTool.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * TCP響應「發送」交易的結果返回
     *
     * @param walletResponseJson
     */
    public void getSendTransactionData_SC(ResponseJson walletResponseJson) {
        if (walletResponseJson.getCode() == MessageConstants.CODE_200) {
            WalletVO walletVO = walletResponseJson.getWalletVO();
            if (walletVO != null) {
                tcpRequestListener.showWalletBalance(walletVO.getWalletBalance());
            }
            tcpRequestListener.sendTransactionSuccess(MessageConstants.socket.TCP_TRANSACTION_SUCCESS);
        } else {
            tcpRequestListener.sendTransactionFailure(MessageConstants.socket.TCP_TRANSACTION_FAILURE + walletResponseJson.getMessage());
        }

    }

    /**
     * 处理线程下面签章区块
     *
     * @param amount
     * @param transactionChainVO
     * @param responseJson
     */
    public void receiveTransaction(String amount, TransactionChainVO transactionChainVO, ResponseJson responseJson) {
        LogTool.d(TAG, "step 3:" + responseJson);
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(GenesisVO.class, new GenesisVOTypeAdapter())//初始块有序
                .registerTypeAdapter(TransactionChainVO.class, new TransactionChainVOTypeAdapter())
                .create();
        WalletVO walletVO = responseJson.getWalletVO();
        if (walletVO != null) {
            tcpRequestListener.showWalletBalance(walletVO.getWalletBalance());
        }
        //如果当前是「open」需要将其「genesisBlockAccount」取出，然后传递给要签章的Representative
        String representative = walletVO.getRepresentative();
        try {
            String tc = gson.toJson(transactionChainVO.getTc());
            LogTool.d(TAG, tc);
            String sourceTXHash = Sha256Tool.doubleSha256ToString(tc);
            LogTool.d(TAG, "step 4:sourceTXHash:" + sourceTXHash);

            String blockType = Constants.ValueMaps.BLOCK_TYPE_RECEIVE;
            String previousDoubleHashStr = "";
            DatabaseVO databaseVO = responseJson.getDatabaseVO();
            if (databaseVO != null) {
                TransactionChainVO transactionChainVONew = databaseVO.getTransactionChainVO();
                if (transactionChainVONew != null) {
                    String tcStr = gson.toJson(transactionChainVONew);
                    LogTool.d(TAG, tcStr);
                    previousDoubleHashStr = Sha256Tool.doubleSha256ToString(tcStr);
                } else {
                    GenesisVO genesisVONew = databaseVO.getGenesisVO();
                    if (genesisVONew != null) { //这里用Gson转化的时候，会有乱序的现象。现在添加一个TypeAdapter来进行顺序的排列，因为这里是直接toJson的「GenesisVO」，所以
                        String str = gson.toJson(genesisVONew);
                        LogTool.d(TAG, str);
                        previousDoubleHashStr = Sha256Tool.doubleSha256ToString(str);
                        blockType = Constants.ValueMaps.BLOCK_TYPE_OPEN;
                        representative = genesisVONew.getGenesisBlockAccount();
                    }

                }

            }
            LogTool.d(TAG, "step 5:previousDoubleHashStr:" + previousDoubleHashStr);

            if (StringTool.isEmpty(previousDoubleHashStr)) {
                LogTool.d(TAG, MessageConstants.PREVIOUS_IS_NULL);
                return;
            }
            String signatureSend = transactionChainVO.getSignature();
            MasterServices.receiveAuthNode(previousDoubleHashStr, walletVO.getBlockService(), sourceTXHash, amount, signatureSend, blockType, representative);
        } catch (Exception e) {
            LogTool.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * /wallet/getLatestChangeBlock
     * 取最新的更換委託人區塊
     * <p>
     * <p>
     * 1.如果没有Change区块，回传Open区块
     * 2.如果有Change区块则回传Change区块
     * TCP封包回传名称：
     * getLatestChangeBlock_SC
     *
     * @param responseJson
     */
    public void getLatestChangeBlock_SC(ResponseJson responseJson) {
        LogTool.d(TAG, "step 2:getLatestChangeBlock_SC");
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                // 可能是change/open区块
                .registerTypeAdapter(TransactionChainVO.class, new TransactionChainVOTypeAdapter())
                .create();
        if (responseJson == null) {
            return;
        }
        /*1：检测当前code，如果是2026，代表没有创世块,用户不能进行授权代表操作*/
        int code = responseJson.getCode();
        if (code == MessageConstants.CODE_2026) {
            tcpRequestListener.modifyRepresentativeResult(changeStatus, responseJson.isSuccess(), code);
            return;
        }
        /*2：否则獲取交易塊*/
        DatabaseVO databaseVO = responseJson.getDatabaseVO();
        if (databaseVO == null) {
            return;
        }
        TransactionChainVO transactionChainVO = databaseVO.getTransactionChainVO();
        if (transactionChainVO == null) {
            return;
        }
        Object tc = transactionChainVO.getTc();
        if (tc == null) {
            return;
        }
        String representative = null;
        //3：判断tc性質 ,檢查blockType是「Open」還是「Change」 ;根據區塊性質，確認representative的值
        String objectStr = GsonTool.getGson().toJson(tc);
        if (JsonTool.isOpenBlock(objectStr)) {
            /*「open」區塊*/
            //标示当前的状态，如果当前是「open」区块，需要在「change」之后再去拉取本接口以获得同AN相同的height、系统时间
            changeStatus = Constants.CHANGE_OPEN;
            /* 「open」區塊，那麼需要從GenesisVO提取genesisBlockAccount這個數據，得到帳戶*/
            GenesisVO genesisVO = databaseVO.getGenesisVO();
            if (genesisVO == null) {
                representative = Constants.ValueMaps.DEFAULT_REPRESENTATIVE;
            } else {
                representative = genesisVO.getGenesisBlockAccount();
                if (StringTool.isEmpty(representative)) {
                    representative = Constants.ValueMaps.DEFAULT_REPRESENTATIVE;
                }
            }
            tcpRequestListener.toModifyRepresentative(representative);

        } else if (JsonTool.isChangeBlock(objectStr)) {
            /*「Change」區塊*/
            changeStatus = Constants.CHANGE;
            /*1:取得当前用户输入的代表人的地址*/
            representative = BcaasApplication.getRepresentative();
            LogTool.d(TAG, representative);
            if (StringTool.isEmpty(representative)) {
                /*2：解析返回的数据，取出上一个授权代表*/
                TransactionChainChangeVO transactionChainChangeVO = GsonTool.convert(objectStr, TransactionChainChangeVO.class);
                String representativePrevious = transactionChainChangeVO.getRepresentative();
                tcpRequestListener.toModifyRepresentative(representativePrevious);
                return;
            }

        }
        // 4：previousDoubleHashStr 交易块
        try {
            String transactionGson = gson.toJson(transactionChainVO);
            LogTool.d(TAG, transactionGson);
            String previousDoubleHashStr = Sha256Tool.doubleSha256ToString(transactionGson);
            LogTool.d(TAG, "step 4:previousDoubleHashStr:" + previousDoubleHashStr);
            if (StringTool.isEmpty(previousDoubleHashStr)) {
                LogTool.d(TAG, MessageConstants.PREVIOUS_IS_NULL);
                return;
            }
            /*5：调用change*/
            MasterServices.change(previousDoubleHashStr, representative);
        } catch (Exception e) {
            LogTool.e(TAG, e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * 响应change
     *
     * @param responseJson
     */
    private void getChangeTransactionData_SC(ResponseJson responseJson) {
        LogTool.d(TAG, "step 2:getChangeTransactionData_SC");
        if (responseJson == null) {
            return;
        }
        int code = responseJson.getCode();
        /*当前授权人地址与上一次一致*/
        /*当前授权人地址错误*/
        if (code == MessageConstants.CODE_2030 || code == MessageConstants.CODE_2033) {
            tcpRequestListener.modifyRepresentativeResult(changeStatus, responseJson.isSuccess(), responseJson.getCode());
            return;
        }

        if (responseJson.isSuccess()) {
            String representative = BcaasApplication.getRepresentative();
            if (StringTool.isEmpty(representative)) {
            } else {
                BcaasApplication.setRepresentative("");
                tcpRequestListener.modifyRepresentativeResult(changeStatus, responseJson.isSuccess(), responseJson.getCode());
            }
        }

    }


    /**
     * 殺掉线程連接
     */
    public static void kill(boolean isStopSocket) {
        stopSocket = isStopSocket;
        keepAlive = false;
        LogTool.d(TAG, MessageConstants.socket.KILL + currentThread());
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            LogTool.e(TAG, MessageConstants.socket.EXCEPTION + e.getMessage());
        }

        if (TCPReceiveLooper != null) {
            TCPReceiveLooper.quit();
            tcpReceiveThread = null;
        }
        tcpReceiveThread = null;
    }
}
