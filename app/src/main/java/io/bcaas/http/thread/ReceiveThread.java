package io.bcaas.http.thread;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.ecc.Sha256Tool;
import io.bcaas.gson.ResponseJson;
import io.bcaas.gson.jsonTypeAdapter.GenesisVOTypeAdapter;
import io.bcaas.gson.jsonTypeAdapter.TransactionChainVOTypeAdapter;
import io.bcaas.http.MasterServices;
import io.bcaas.listener.RequestResultListener;
import io.bcaas.listener.TCPReceiveBlockListener;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.GsonTool;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.StringTool;
import io.bcaas.vo.ClientIpInfoVO;
import io.bcaas.vo.DatabaseVO;
import io.bcaas.vo.GenesisVO;
import io.bcaas.vo.PaginationVO;
import io.bcaas.vo.TransactionChainSendVO;
import io.bcaas.vo.TransactionChainVO;
import io.bcaas.vo.WalletVO;

/**
 * TCP请求服务端，请求R区块的数据
 */
public class ReceiveThread extends Thread {
    private static String TAG = "ReceiveThread";
    //rpcPort
    private int rpcPort;
    //向服务器TCP发送的数据
    private String writeStr = null;
    //是否存活
    public static boolean alive = true;
    public static Socket socket = null;
    //得到当前需要去签章的交易区块
    private TransactionChainVO currentSendVO;
    //监听TCP的一些返回，通知界面作出改动
    private TCPReceiveBlockListener tcpReceiveBlockListener;

    private static Queue<TransactionChainVO> getWalletWaitingToReceiveQueue = new LinkedList<>();

    public ReceiveThread(String writeString, TCPReceiveBlockListener tcpReceiveBlockListener) {
        this.rpcPort = BcaasApplication.getRpcPort();
        this.writeStr = writeString;
        this.tcpReceiveBlockListener = tcpReceiveBlockListener;
    }


    /**
     * 关闭线程
     */
    public static void kill() {
        alive = false;
        BcaasLog.d(TAG, "socket kill...");
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            BcaasLog.e(TAG, "socket close Exception..." + e.getMessage());
        }
    }

    @Override
    public final void run() {
        BcaasLog.d(TAG, "初始化连接socket..." + BcaasApplication.getExternalIp() + ":" + BcaasApplication.getExternalPort());
        socket = buildSocket();
        //开启接收线程
        new HandlerThread(socket).start();
    }

    // 重新建立socket连接
    private Socket buildSocket() {
        System.gc();
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(BcaasApplication.getExternalIp(),
                    BcaasApplication.getExternalPort()), Constants.ValueMaps.sleepTime30000);
            socket.setKeepAlive(true);
            alive = true;

            if (socket.isConnected()) {
                writeTOSocket(socket, writeStr);
                tcpReceiveBlockListener.httpToRequestReceiverBlock();
            }
            return socket;
        } catch (Exception e) {
            e.printStackTrace();
            BcaasLog.e(TAG, " 初始化socket失败，请求「sfn」resetAN:" + e.getMessage());
            if (e instanceof ConnectException) {
                //如果当前连接不上，代表需要重新设置AN
//                tcpReceiveBlockListener.resetANAddress();
                ClientIpInfoVO clientIpInfoVO = MasterServices.reset();
                BcaasApplication.setClientIpInfoVO(clientIpInfoVO);
                buildSocket();
            } else {
                tcpReceiveBlockListener.restartSocket();
            }
            tcpReceiveBlockListener.stopToHttpToRequestReceiverBlock();
        }
        return null;
    }


    /**
     * 用于向服务端写入数据
     *
     * @param socket   socket对象
     * @param writeStr 写入字符串
     */
    public void writeTOSocket(Socket socket, String writeStr) {
        PrintWriter printWriter = null;
        try {
            if (socket.isConnected()) {
                //向服务器端发送数据
                printWriter = new PrintWriter(socket.getOutputStream());
                printWriter.write(writeStr + " \n");
                printWriter.flush();

                BcaasLog.d(TAG, "已发送socket数据：" + writeStr);
            } else {
                BcaasLog.d(TAG, "socket closed..");
            }
        } catch (Exception e) {
            BcaasLog.e(TAG, "receive client exception");
            e.printStackTrace();
        }
    }

    /**
     * 用于接受服务端响应数据
     */
    public class HandlerThread extends Thread {
        private Socket socket;

        public HandlerThread(Socket client) {
            socket = client;
        }

        public final void run() {
            Gson gson = GsonTool.getGson();
            while (alive) {
                BcaasLog.d(TAG, MessageConstants.socket.TAG + socket);
                try {
                    //读取服务器端数据
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    try {
                        while (socket.isConnected() && alive) {
                            try {
                                socket.sendUrgentData(0xFF); // 發送心跳包
                            } catch (Exception e) {
                                BcaasLog.d(TAG, "socket connect exception");
                                socket.close();
                                break;
                            }
                            String readLine = bufferedReader.readLine();
                            if (readLine != null && readLine.trim().length() != 0) {
                                BcaasLog.d(TAG, "step 1: tcp 返回数据: " + readLine);
                                ResponseJson responseJson = gson.fromJson(readLine, ResponseJson.class);
                                if (responseJson != null) {
                                    String methodName = responseJson.getMethodName();
                                    if (StringTool.isEmpty(methodName)) {
                                        BcaasLog.d(TAG, MessageConstants.METHOD_NAME_IS_NULL);
                                    } else {
                                        switch (methodName) {
                                            case MessageConstants.GETLATESTBLOCKANDBALANCE_SC:
                                                getLatestBlockAndBalance_SC(responseJson);
                                                break;
                                            case MessageConstants.GETSENDTRANSACTIONDATA_SC:
                                                getSendTransactionData_SC(responseJson);
                                                break;
                                            case MessageConstants.GETRECEIVETRANSACTIONDATA_SC:
                                                getReceiveTransactionData_SC(responseJson);
                                                break;
                                            case MessageConstants.GETWALLETWAITINGTORECEIVEBLOCK_SC:
                                                getWalletWaitingToReceiveBlock_SC(responseJson);
                                                break;
                                            default:
                                                BcaasLog.d(TAG, MessageConstants.METHOD_NAME_ERROR + methodName);
                                                break;
                                        }
                                    }
                                } else {
                                    BcaasLog.d(TAG, MessageConstants.RESPONSE_IS_NULL);
                                }

                            }
                        }
                    } catch (Exception e) {
                        BcaasLog.e(TAG, e.getMessage());
                        e.printStackTrace();
                    } finally {
                        if (bufferedReader != null) {
                            bufferedReader.close();
                        }
                        try {
                            if (socket != null) {
                                socket.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        tcpReceiveBlockListener.restartSocket();
                        BcaasLog.d(TAG, " 关闭socket 连线。。");
                    }
                } catch (Exception e) {
                    BcaasLog.e(TAG, e.getMessage());
                    tcpReceiveBlockListener.stopToHttpToRequestReceiverBlock();
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    /**
     * "取得未簽章R區塊的Send區塊 & 取最新的R區塊 & wallet餘額"
     *
     * @param responseJson
     */
    public void getWalletWaitingToReceiveBlock_SC(ResponseJson responseJson) {
        BcaasLog.d(TAG, "step 2:getWalletWaitingToReceiveBlock_SC");
        Gson gson = GsonTool.getGson();
        if (responseJson == null) {
            return;
        } else {
            List<PaginationVO> paginationVOList = responseJson.getPaginationVOList();
            if (paginationVOList != null) {
                PaginationVO paginationVO = paginationVOList.get(0);
                List<TransactionChainVO> transactionChainVOList = new ArrayList<>();//存储当前需要显示在主页的未签章的R区块信息
                List<Object> objList = paginationVO.getObjectList();
                if (ListTool.noEmpty(objList)) {
                    //有未签章的区块
                    if (responseJson.getPaginationVOList() != null) {
                        BcaasLog.d(TAG, "有需要签章的区块");
                        for (Object obj : objList) {
                            TransactionChainVO transactionChainVO = gson.fromJson(gson.toJson(obj), TransactionChainVO.class);
                            transactionChainVOList.add(transactionChainVO);//将当前遍历得到的单笔R区块存储起来
                            getWalletWaitingToReceiveQueue.offer(transactionChainVO);
                        }
                        tcpReceiveBlockListener.haveTransactionChainData(transactionChainVOList);
                        getTransactionVOOfQueue(responseJson, false);
                    }
                } else {
                    BcaasLog.d(TAG, "没有需要签章的区块");
                    tcpReceiveBlockListener.noTransactionChainData();

                }
            } else {
                tcpReceiveBlockListener.noTransactionChainData();
            }
            WalletVO walletVO = responseJson.getWalletVO();
            String walletBalance = walletVO != null ? walletVO.getWalletBalance() : "0";
            tcpReceiveBlockListener.showWalletBalance(walletBalance);//通知页面更新当前的余额
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
            //1：如果当前是签章回来，并且已发送的交易还在
            if (isReceive && currentSendVO != null) {
                tcpReceiveBlockListener.signatureTransaction(currentSendVO);
            }
            //2：重新取得线程池里面的数据
            currentSendVO = getWalletWaitingToReceiveQueue.poll();
            if (currentSendVO != null) {
                String amount = gson.fromJson(gson.toJson(currentSendVO.getTc()), TransactionChainSendVO.class).getAmount();
                receiveTransaction(amount, BcaasApplication.getAccessTokenFromSP(), currentSendVO, responseJson);
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
        BcaasLog.d(TAG, "step 2:getLatestBlockAndBalance_SC");
//        Gson gson = GsonTool.getGsonBuilderTypeAdapterForResponseJson();
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                // 可能是Send/open区块
                .registerTypeAdapter(TransactionChainVO.class, new TransactionChainVOTypeAdapter())
                .create();
        DatabaseVO databaseVO = responseJson.getDatabaseVO();
        if (databaseVO == null) return;
        String destinationWallet = BcaasApplication.getDestinationWallet();
        String transactionAmount = BcaasApplication.getTransactionAmount();
        if (StringTool.isEmpty(destinationWallet)) {
            BcaasLog.d(TAG, MessageConstants.DESTINATIONWALLET_IS_NULL);
            return;
        }
        if (StringTool.isEmpty(transactionAmount)) {
            BcaasLog.d(TAG, MessageConstants.AMOUNT_IS_NULL);
            return;
        }
        try {
            BcaasLog.d(TAG, "transactionAmount:" + transactionAmount + ",destinationWallet:" + destinationWallet);
            WalletVO walletVO = responseJson.getWalletVO();
            if (walletVO != null) {
                int balanceAfterAmount = Integer.parseInt(walletVO.getWalletBalance()) - Integer.parseInt(transactionAmount);
                if (balanceAfterAmount < 0) {
                    BcaasLog.d(TAG, "餘額不足，無法成功發送");
                    return;
                }
                tcpReceiveBlockListener.showWalletBalance(walletVO.getWalletBalance());//通知页面更新当前的余额
                String previousBlockStr = gson.toJson(databaseVO.getTransactionChainVO());
                BcaasLog.d(TAG, previousBlockStr);
                String previous = Sha256Tool.doubleSha256ToString(previousBlockStr);
                String apisendurl = "http://" + BcaasApplication.getExternalIp() + ":" + rpcPort + Constants.RequestUrl.send;

                Object o = databaseVO.getTransactionChainVO().getTc();
                //json 出错，所以先gson.tojson() and then gson.fromJson()
                TransactionChainSendVO transactionChainSendVO = GsonTool.getGson().fromJson(GsonTool.getGson().toJson(o), TransactionChainSendVO.class);
                String blockService = walletVO.getBlockService();
                // TODO: 2018/8/27 获取区块服务
                String virtualCoin = walletVO.getBlockService();
                BcaasLog.d(TAG, "receive virtualCoin:" + virtualCoin);
                // 2018/8/22请求AN send请求
                responseJson = MasterServices.sendAuthNode(apisendurl, previous, virtualCoin, destinationWallet, balanceAfterAmount, transactionAmount, BcaasApplication.getAccessTokenFromSP());

                if (responseJson != null && responseJson.getCode() == 200) {
                    BcaasLog.d(TAG, "http 交易信息发送成功，等待处理中...");
                } else {
                    tcpReceiveBlockListener.sendTransactionFailure("交易发送失败。");
                }
            } else {
                tcpReceiveBlockListener.sendTransactionFailure("发送失败。交易金额有误");
                return;
            }

        } catch (Exception e) {
            BcaasLog.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 发送结果
     *
     * @param walletResponseJson
     */
    public void getSendTransactionData_SC(ResponseJson walletResponseJson) {
        if (walletResponseJson.getCode() == MessageConstants.CODE_200) {
            WalletVO walletVO = walletResponseJson.getWalletVO();
            if (walletVO != null) {
                tcpReceiveBlockListener.showWalletBalance(walletVO.getWalletBalance());
            }
            tcpReceiveBlockListener.sendTransactionSuccess("tcp 交易成功。");
        } else {
            tcpReceiveBlockListener.sendTransactionFailure("tcp 交易失败。" + walletResponseJson.getMessage());
        }

    }

    /**
     * 处理线程下面签章区块
     *
     * @param amount
     * @param accessToken
     * @param transactionChainVO
     * @param responseJson
     */
    public void receiveTransaction(String amount, String accessToken, TransactionChainVO transactionChainVO, ResponseJson responseJson) {
        BcaasLog.d(TAG, "step 3:" + responseJson);
//         Gson gson = GsonTool.getGsonBuilderTypeAdapterForResponseJson();
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(GenesisVO.class, new GenesisVOTypeAdapter())//初始块有序
                .registerTypeAdapter(TransactionChainVO.class, new TransactionChainVOTypeAdapter())
                .create();
        WalletVO walletVO = responseJson.getWalletVO();
        if (walletVO != null) {
            tcpReceiveBlockListener.showWalletBalance(walletVO.getWalletBalance());
        }
        try {
            BcaasLog.d(TAG, "transactionChainVO.getTc():" + gson.toJson(transactionChainVO.getTc()));
            String doubleHashTc = Sha256Tool.doubleSha256ToString(gson.toJson(transactionChainVO.getTc()));
            BcaasLog.d(TAG, "step 4:doubleHashTc:" + doubleHashTc);

            String blockType = Constants.ValueMaps.BLOCK_TYPE_RECEIVE;
            String previousDoubleHashStr = "";
            DatabaseVO databaseVO = responseJson.getDatabaseVO();
            if (databaseVO != null) {
                TransactionChainVO transactionChainVONew = databaseVO.getTransactionChainVO();
                if (transactionChainVONew != null) {
                    String tcStr = gson.toJson(transactionChainVONew);
                    BcaasLog.d(TAG, tcStr);
                    previousDoubleHashStr = Sha256Tool.doubleSha256ToString(tcStr);
                } else {
                    GenesisVO genesisVONew = databaseVO.getGenesisVO();
                    //这里用Gson转化的时候，会有乱序的现象。现在添加一个TypeAdapter来进行顺序的排列，因为这里是直接toJson的「GenesisVO」，所以
                    String str = gson.toJson(genesisVONew);
                    BcaasLog.d(TAG, str);
                    previousDoubleHashStr = Sha256Tool.doubleSha256ToString(str);
                    blockType = Constants.ValueMaps.BLOCK_TYPE_OPEN;
                }

            }
            BcaasLog.d(TAG, "step 5:previousDoubleHashStr:" + previousDoubleHashStr);

            if (StringTool.isEmpty(previousDoubleHashStr)) {
                BcaasLog.d(TAG, MessageConstants.PREVIOUS_IS_NULL);
                return;
            }
            String signatureSend = transactionChainVO.getSignature();
            String apiUrl = "http://" + BcaasApplication.getExternalIp() + ":" + rpcPort + Constants.RequestUrl.receive;
            BcaasLog.d(TAG, apiUrl);
            // TODO: 2018/8/27  获取区块服务
//            Object tcObject = transactionChainVO.getTc();
//            TransactionChainReceiveVO transactionChainReceiveVO = GsonTool.getGson().fromJson(GsonTool.getGson().toJson(tcObject), TransactionChainReceiveVO.class);
            String blockService = walletVO.getBlockService();
            if (StringTool.isEmpty(blockService)) {
                blockService = Constants.BlockService.BCC;
            }
            MasterServices.receiveAuthNode(apiUrl, previousDoubleHashStr, blockService, doubleHashTc, amount, accessToken, signatureSend, blockType);
        } catch (Exception e) {
            BcaasLog.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    RequestResultListener requestResultListener = new RequestResultListener() {

        @Override
        public void resetAuthNodeFailure(String message) {
            BcaasLog.d(TAG, message);
            // TODO: 2018/8/25 暂时不循环， dubug时点击首页标题循环
//            masterServices.reset();
        }

        @Override
        public void resetAuthNodeSuccess(ClientIpInfoVO clientIpInfoVO) {
            BcaasApplication.setClientIpInfoVO(clientIpInfoVO);//存储当前的AN信息
            buildSocket();
        }
    };

}
