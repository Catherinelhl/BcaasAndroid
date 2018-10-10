package io.bcaas.http.tcp;


import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import io.bcaas.base.BCAASApplication;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.gson.ResponseJson;
import io.bcaas.gson.jsonTypeAdapter.GenesisVOTypeAdapter;
import io.bcaas.gson.jsonTypeAdapter.TransactionChainVOTypeAdapter;
import io.bcaas.http.MasterServices;
import io.bcaas.listener.HttpASYNTCPResponseListener;
import io.bcaas.listener.TCPRequestListener;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.NetWorkTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.decimal.DecimalTool;
import io.bcaas.tools.ecc.Sha256Tool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.tools.gson.JsonTool;
import io.bcaas.vo.ClientIpInfoVO;
import io.bcaas.vo.DatabaseVO;
import io.bcaas.vo.GenesisVO;
import io.bcaas.vo.PaginationVO;
import io.bcaas.vo.TransactionChainChangeVO;
import io.bcaas.vo.TransactionChainOpenVO;
import io.bcaas.vo.TransactionChainReceiveVO;
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
    private static TransactionChainVO currentSendVO;
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
    /*得到當前建立長鏈接的Handler*/
    private static TCPReceiveThread tcpReceiveThread;
    /*得到當前建立的長鏈接的looper*/
    private static Looper TCPReceiveLooper;
    /*得到當前网络请求Socket连接的Handler*/
    private static SocketThread socketThread;
    /*得到當前网络请求Socket的looper*/
    private static Looper socketLooper;
    /*存儲當前「Send」之後，自己計算的balance*/
    private String balanceAfterSend = "";
    private boolean created;

    public TCPThread(String writeString, TCPRequestListener tcpRequestListener) {
        this.writeStr = writeString;
        this.tcpRequestListener = tcpRequestListener;
    }

    @Override
    public final void run() {
        /*1:創建socket*/
        created = false;
        stopSocket = false;
        socket = new Socket();
        compareWalletExternalIpWithSANExternalIp();
        //连接socket
        createSocketAndBuild();

    }

    /*創建一個socket連接*/
    private void createSocketAndBuild() {
        if (created) {
            return;
        }
        created = true;
        LogTool.d(TAG, MessageConstants.socket.CREATE_SOCKET);
        SocketAddress socAddress = new InetSocketAddress(BCAASApplication.getTcpIp(), BCAASApplication.getTcpPort());
        LogTool.d(TAG, MessageConstants.socket.TAG + socAddress);
        tcpRequestListener.refreshTCPConnectIP(BCAASApplication.getTcpIp() + MessageConstants.REQUEST_COLON + BCAASApplication.getTcpPort());
        //设置socket连接超时时间，如果是内网的话，那么5s之后重连，如果是外网10s之后重连
        try {
            socket.connect(socAddress,
                    isInternal ? Constants.ValueMaps.INTERNET_TIME_OUT_TIME
                            : Constants.ValueMaps.EXTERNAL_TIME_OUT_TIME);
            socket.setKeepAlive(true);//让其在建立连接的时候保持存活
            keepAlive = true;
            buildSocket();
        } catch (Exception e) {
            LogTool.e(TAG, e.toString() + NetWorkTool.tcpConnectTimeOut(e));
            if (e.getMessage() != null) {
//                if (NetWorkTool.tcpConnectTimeOut(e)) {
//                //如果当前连接不上，代表需要重新设置AN,内网5s，外网10s
                resetSAN();

//                }
            }

        }
//        socketThread = new SocketThread();
//        socketThread.start();
    }

    /* 對連接到的socket進行訪問，並且開啟一個線程來接收TCP返回的數據*/
    private void buildSocket() {
        LogTool.d(TAG, MessageConstants.socket.BUILD_SOCKET + stopSocket);
        //当前stopSocket为false的时候才能允许连接
        if (!stopSocket) {
            isResetExceedTheLimit();
            resetCount++;
            if (socket != null) {
                if (socket.isConnected()) {
                    writeTOSocket(socket, writeStr);
                    /*2:开启接收线程*/
                    tcpReceiveThread = new TCPReceiveThread(socket);
                    tcpReceiveThread.start();
                }
            }
        }
    }

    /*判断重置是否超过限定,重置次数已经5次了，那么让他睡10s，然后继续*/
    private void isResetExceedTheLimit() {
        if (resetCount >= MessageConstants.socket.RESET_MAX_COUNT) {
            resetCount = 0;
            try {
                LogTool.d(TAG, MessageConstants.socket.OVER_FIVE_TIME_TO_RESET);
                Thread.sleep(Constants.ValueMaps.sleepTime10000);
            } catch (InterruptedException e) {
                LogTool.d(TAG, e.getMessage());
            }
        }
    }

    /*重新连接SAN*/
    private void resetSAN() {
//        tcpRequestListener.stopToHttpToRequestReceiverBlock();
        kill(false);
        tcpRequestListener.needUnbindService();
        LogTool.d(TAG, MessageConstants.socket.RESET_AN + stopSocket);
        //当前stopSocket为false的时候才继续重连
        MasterServices.reset(httpASYNTCPResponseListener);
    }

    /**
     * 比对当前设备的外网IP与SAN的外网IP
     *
     * @return
     */
    private void compareWalletExternalIpWithSANExternalIp() {
        /*得到当前设备的外网IP*/
        String walletExternalIp = BCAASApplication.getWalletExternalIp();
        /*得到当前服务器返回的可以连接的SAN的内外网IP&Port*/
        clientIpInfoVO = BCAASApplication.getClientIpInfoVO();
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
        BCAASApplication.setTcpIp(clientIpInfoVO.getExternalIp());
        BCAASApplication.setTcpPort(clientIpInfoVO.getExternalPort());
        BCAASApplication.setHttpPort(clientIpInfoVO.getRpcPort());
    }

    /*连接外网IP&Port*/
    private void connectInternalIP() {
        isInternal = true;
        LogTool.d(TAG, MessageConstants.socket.CONNECT_INTERNAL_IP);
        BCAASApplication.setTcpIp(clientIpInfoVO.getInternalIp());
        BCAASApplication.setTcpPort(clientIpInfoVO.getInternalPort());
        BCAASApplication.setHttpPort(clientIpInfoVO.getInternalRpcPort());
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


    private class SocketThread extends Thread {
        @Override
        public void run() {
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            socketLooper = Looper.myLooper();
            SocketAddress socAddress = new InetSocketAddress(BCAASApplication.getTcpIp(), BCAASApplication.getTcpPort());
            LogTool.d(TAG, MessageConstants.socket.TAG + socAddress);
            tcpRequestListener.refreshTCPConnectIP(BCAASApplication.getTcpIp() + MessageConstants.REQUEST_COLON + BCAASApplication.getTcpPort());
            //设置socket连接超时时间，如果是内网的话，那么5s之后重连，如果是外网10s之后重连
            try {
                socket.connect(socAddress,
                        isInternal ? Constants.ValueMaps.INTERNET_TIME_OUT_TIME
                                : Constants.ValueMaps.EXTERNAL_TIME_OUT_TIME);
                socket.setKeepAlive(true);//让其在建立连接的时候保持存活
                keepAlive = true;
                buildSocket();
            } catch (Exception e) {
                LogTool.e(TAG, e.toString() + NetWorkTool.tcpConnectTimeOut(e));
                if (e.getMessage() != null) {
//                if (NetWorkTool.tcpConnectTimeOut(e)) {
//                //如果当前连接不上，代表需要重新设置AN,内网5s，外网10s
                    resetSAN();

//                }
                }

            }
            Looper.loop();
        }
    }

    /*接受服务端响应数据*/
    private class TCPReceiveThread extends Thread {
        private Socket socket;

        public TCPReceiveThread(Socket client) {
            socket = client;
        }

        public final void run() {
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            TCPReceiveLooper = Looper.myLooper();
            Gson gson = GsonTool.getGson();
            //判斷當前是活著且非阻塞的狀態下才能繼續前行
            while (isKeepAlive() && !isInterrupted()) {
                tcpRequestListener.httpToRequestReceiverBlock();
                LogTool.d(TAG, MessageConstants.socket.TAG + socket + stopSocket);
                try {
                    //读取服务器端数据
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    try {
                        while (socket.isConnected() && isKeepAlive()) {
                            try {
                                // 發送心跳包
                                socket.sendUrgentData(MessageConstants.socket.HEART_BEAT);
                            } catch (Exception e) {
                                LogTool.d(TAG, MessageConstants.socket.CONNECT_EXCEPTION + e.getMessage());
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
                                            || code == MessageConstants.CODE_3008
                                            || code == MessageConstants.CODE_2029) {
                                        //Redis data not found,need logout
                                        LogTool.d(TAG, MessageConstants.socket.STOP_SOCKET_TO_LOGIN);
                                        if (bufferedReader != null) {
                                            bufferedReader.close();
                                        }
                                        if (!stopSocket) {
                                            tcpRequestListener.reLogin();
                                            stopSocket = true;
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
                                            /*获取余额*/
                                            case MessageConstants.socket.GETBALANCE_SC:
                                                getBalance_SC(responseJson);
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
                        keepAlive = false;
                        LogTool.e(TAG, e.getMessage());
                        break;
                    } finally {
                        if (bufferedReader != null) {
                            bufferedReader.close();
                        }
//                        tcpRequestListener.stopToHttpToRequestReceiverBlock();
                    }
                } catch (Exception e) {
                    keepAlive = false;
                    LogTool.e(TAG, e.getMessage());
//                    tcpRequestListener.stopToHttpToRequestReceiverBlock();
                    break;
                } finally {
                    resetSAN();
                    break;
                }
            }
            Looper.loop();
        }
    }

    /*获取余额*/
    private void getBalance_SC(ResponseJson responseJson) {
        LogTool.d(TAG, "step 2:" + MessageConstants.socket.GETBALANCE_SC);
        if (responseJson.isSuccess()) {
            LogTool.d(TAG, MessageConstants.socket.SUCCESS_GET_WALLET_GETBALANCE);
            if (responseJson.getCode() == MessageConstants.CODE_200) {
                parseWalletVoTOGetBalance(responseJson.getWalletVO());
            }
        } else {
            LogTool.d(TAG, MessageConstants.socket.FAILURE_GET_WALLET_GETBALANCE);
        }

    }

    /*解析錢包信息。得到服務器返回的餘額*/
    private void parseWalletVoTOGetBalance(WalletVO walletVO) {
        if (walletVO != null) {
            //判斷當前服務器返回的區塊是否和本地的區塊相對應，如果是，才顯示新獲取的餘額
            String blockService = walletVO.getBlockService();
            if (BCAASApplication.getBlockService().equals(blockService)) {
                String walletBalance = walletVO.getWalletBalance();
                //現在Receive區塊沒有返回餘額了。所以判斷但錢餘額為空，就不用顯示，當然，R區塊返回也不用調用這個方法了
                if (StringTool.notEmpty(walletBalance)) {
                    tcpRequestListener.showWalletBalance(walletBalance);

                }
            }
        } else {
            LogTool.d(TAG, MessageConstants.socket.FAILURE_GET_WALLET_GETBALANCE);
        }
    }

    /**
     * "取得未簽章R區塊的Send區塊 & 取最新的R區塊 & wallet餘額"
     *
     * @param responseJson
     */
    public void getWalletWaitingToReceiveBlock_SC(ResponseJson responseJson) {
        LogTool.d(TAG, "step 2:" + MessageConstants.socket.GETWALLETWAITINGTORECEIVEBLOCK_SC);
        Gson gson = GsonTool.getGson();
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
            BCAASApplication.setNextObjectId(paginationVO.getNextObjectId());
        }
    }

    /**
     * 处理线程下面需要处理的R区块
     *
     * @param responseJson
     */
    public void getReceiveTransactionData_SC(ResponseJson responseJson) {
        int code = responseJson.getCode();
        //如果當前是2028：{"databaseVO":{},"walletVO":{"blockService":"COS"},"success":false,"code":2028,"message":"Transaction already exists.","methodName":"getReceiveTransactionData_SC","size":0}
        if (code == MessageConstants.CODE_200) {
            tcpRequestListener.refreshTransactionRecord();
            //同時本地計算餘額
            calculateAfterReceiveBalance(responseJson);
            //签章返回成功，将当前的send块置空
            currentSendVO = null;
            getTransactionVOOfQueue(responseJson, true);
        } else if (code == MessageConstants.CODE_2028) {
            //签章返回成功，将当前的send块置空
            currentSendVO = null;
            getTransactionVOOfQueue(responseJson, true);
        } else {
            LogTool.d(TAG, MessageConstants.socket.SIGNATURE_FAILED + responseJson);
        }
    }

    //簽章成功之後，通知更新當前的餘額
    private void calculateAfterReceiveBalance(ResponseJson responseJson) {
        DatabaseVO databaseVO = responseJson.getDatabaseVO();
        if (databaseVO != null) {
            TransactionChainVO transactionChainVONew = databaseVO.getTransactionChainVO();
            if (transactionChainVONew != null) {
                Object object = transactionChainVONew.getTc();
                String objectStr = GsonTool.getGson().toJson(object);
                // 如果當前是Receive Block，那麼需要餘額與交易額度相加得到當前需要顯示的金額
                if (JsonTool.isReceiveBlock(objectStr)) {
                    TransactionChainReceiveVO transactionChainReceiveVO = GsonTool.convert(objectStr, TransactionChainReceiveVO.class);
                    if (transactionChainReceiveVO != null) {
                        String amount = transactionChainReceiveVO.getAmount();
                        if (StringTool.notEmpty(amount)) {
                            String walletBalance = BCAASApplication.getWalletBalance();
                            if (StringTool.notEmpty(walletBalance)) {
                                String newBalance = DecimalTool.calculateFirstAddSecondValue(walletBalance, amount);
                                LogTool.d(TAG, MessageConstants.socket.CALCULATE_AFTER_RECEIVE_BALANCE + newBalance);
                                tcpRequestListener.showWalletBalance(newBalance);
                            }
                        }
                    }
                } else if (JsonTool.isOpenBlock(objectStr)) {
                    //如果當前是Open區塊，則不需要去檢查本地餘額是否是空，直接顯示交易額度
                    TransactionChainOpenVO transactionChainOpenVO = GsonTool.convert(objectStr, TransactionChainOpenVO.class);
                    if (transactionChainOpenVO != null) {
                        String amount = transactionChainOpenVO.getAmount();
                        if (StringTool.notEmpty(amount)) {
                            LogTool.d(TAG, MessageConstants.socket.CALCULATE_AFTER_RECEIVE_BALANCE + amount);
                            tcpRequestListener.showWalletBalance(amount);
                        }
                    }
                }
            }
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
            //重新取得线程池里面的数据,判断当前签章块是否回传结果
            if (currentSendVO == null) {
                LogTool.d(TAG, MessageConstants.socket.CURRENT_RECEIVEQUEUE_SIZE + getWalletWaitingToReceiveQueue.size());
                currentSendVO = getWalletWaitingToReceiveQueue.poll();
                if (currentSendVO != null) {
                    String amount = gson.fromJson(gson.toJson(currentSendVO.getTc()), TransactionChainSendVO.class).getAmount();
                    receiveTransaction(amount, currentSendVO, responseJson);
                }
            } else {
                LogTool.d(TAG, MessageConstants.socket.SIGNATUREING);
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
        // 置空「發送」之後需要計算得到的餘額值
        balanceAfterSend = "";
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
        String destinationWallet = BCAASApplication.getDestinationWallet();
        String transactionAmount = BCAASApplication.getTransactionAmount();
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
                balanceAfterSend = DecimalTool.calculateFirstSubtractSecondValue(walletVO.getWalletBalance(), transactionAmount);
                if (StringTool.equals(balanceAfterSend, MessageConstants.NO_ENOUGH_BALANCE)) {
                    tcpRequestListener.noEnoughBalance();
                    balanceAfterSend = "";
                    return;
                }
                parseWalletVoTOGetBalance(walletVO);
                String previousBlockStr = gson.toJson(databaseVO.getTransactionChainVO());
                LogTool.d(TAG, previousBlockStr);
                String previous = Sha256Tool.doubleSha256ToString(previousBlockStr);
                // 2018/8/22请求AN send请求
                responseJson = MasterServices.sendAuthNode(previous, walletVO.getBlockService(), destinationWallet, balanceAfterSend, transactionAmount, walletVO.getRepresentative(), httpASYNTCPResponseListener);

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
            //發送成功，直接顯示當前自己發送錢計算出來的balance，服務器現在不作餘額返回，為了加快返回數據的速度。
            if (StringTool.notEmpty(balanceAfterSend)) {
                LogTool.d(TAG, MessageConstants.socket.BALANCE_AFTER_SEND + balanceAfterSend);
                tcpRequestListener.showWalletBalance(balanceAfterSend);
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
        //添加receiverAmount
        String receiverAmount = null;
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(GenesisVO.class, new GenesisVOTypeAdapter())//初始块有序
                .registerTypeAdapter(TransactionChainVO.class, new TransactionChainVOTypeAdapter())
                .create();
        WalletVO walletVO = responseJson.getWalletVO();
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
                    Object object = transactionChainVONew.getTc();
                    String objectStr = GsonTool.getGson().toJson(object);
                    if (JsonTool.isReceiveBlock(objectStr)) {
                        // Receive Block
                        TransactionChainReceiveVO transactionChainReceiveVO = GsonTool.convert(objectStr, TransactionChainReceiveVO.class);
                        if (transactionChainReceiveVO != null) {
                            receiverAmount = transactionChainReceiveVO.getReceiveAmount();
                        }
                    }
                    if (JsonTool.isOpenBlock(objectStr)) {
                        // Open Block
                        TransactionChainOpenVO transactionChainOpenVO = GsonTool.convert(objectStr, TransactionChainOpenVO.class);
                        if (transactionChainOpenVO != null) {
                            receiverAmount = transactionChainOpenVO.getReceiveAmount();
                        }
                    }
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
            receiverAmount = DecimalTool.calculateFirstAddSecondValue(receiverAmount, amount);
            if (StringTool.equals(receiverAmount, MessageConstants.AMOUNT_EXCEPTION_CODE)) {
                tcpRequestListener.amountException();
            } else {
                MasterServices.receiveAuthNode(previousDoubleHashStr, walletVO.getBlockService(),
                        sourceTXHash, amount, signatureSend, blockType,
                        representative, receiverAmount, httpASYNTCPResponseListener);
            }
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
            tcpRequestListener.getPreviousModifyRepresentative(representative);

        } else if (JsonTool.isChangeBlock(objectStr)) {
            /*「Change」區塊*/
            changeStatus = Constants.CHANGE;
            /*1:取得当前用户输入的代表人的地址*/
            representative = BCAASApplication.getRepresentative();
            LogTool.d(TAG, representative);
            if (StringTool.isEmpty(representative)) {
                /*2：解析返回的数据，取出上一个授权代表*/
                TransactionChainChangeVO transactionChainChangeVO = GsonTool.convert(objectStr, TransactionChainChangeVO.class);
                String representativePrevious = transactionChainChangeVO.getRepresentative();
                tcpRequestListener.getPreviousModifyRepresentative(representativePrevious);
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
            MasterServices.change(previousDoubleHashStr, representative, httpASYNTCPResponseListener);
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
        if (responseJson.isSuccess()) {
            String representative = BCAASApplication.getRepresentative();
            if (StringTool.notEmpty(representative)) {
                //代表当前是点击「change」返回
                tcpRequestListener.modifyRepresentativeResult(changeStatus, responseJson.isSuccess(), responseJson.getCode());

            }
        }
        BCAASApplication.setRepresentative("");
        /*当前授权人地址与上一次一致*/
        /*当前授权人地址错误*/
        if (code == MessageConstants.CODE_2030 || code == MessageConstants.CODE_2033) {
            tcpRequestListener.modifyRepresentativeResult(changeStatus, responseJson.isSuccess(), responseJson.getCode());
            return;
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
        //重置数据
        BCAASApplication.setNextObjectId("");
        currentSendVO = null;
        if (getWalletWaitingToReceiveQueue != null) {
            getWalletWaitingToReceiveQueue.clear();
        }
        destroyTCPReceiveThread();
        destroySocketThread();
    }

    private static void destroyTCPReceiveThread() {
        LogTool.d(TAG, "destroyTCPReceiveThread");
        if (TCPReceiveLooper != null) {
            TCPReceiveLooper.quit();
            TCPReceiveLooper = null;
        }
        tcpReceiveThread = null;
    }

    private static void destroySocketThread() {
        LogTool.d(TAG, "destroySocketThread");
        if (socketLooper != null) {
            socketLooper.quit();
            socketLooper = null;
        }
        socketThread = null;
    }

    public static boolean isKeepAlive() {
        try {
            return keepAlive && socket.getKeepAlive();
        } catch (SocketException e) {
            e.printStackTrace();

        }
        return false;
    }

    private HttpASYNTCPResponseListener httpASYNTCPResponseListener = new HttpASYNTCPResponseListener() {
        @Override
        public void getLatestChangeBlockSuccess() {

        }

        @Override
        public void getLatestChangeBlockFailure(String failure) {

        }

        @Override
        public void resetSuccess(ClientIpInfoVO clientIpInfoVO) {
            //获取到新的SAN位置
            BCAASApplication.setClientIpInfoVO(clientIpInfoVO);
            compareWalletExternalIpWithSANExternalIp();
            //连接socket
            tcpRequestListener.resetSuccess();
        }

        @Override
        public void resetFailure() {

        }

        @Override
        public void logout() {
            tcpRequestListener.reLogin();
            stopSocket = true;
        }

        @Override
        public void sendFailure() {
            tcpRequestListener.sendTransactionFailure("");

        }
    };
}
