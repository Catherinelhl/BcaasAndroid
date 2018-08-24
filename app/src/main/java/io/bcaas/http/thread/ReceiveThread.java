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
import io.bcaas.http.JsonTypeAdapter.GenesisVOTypeAdapter;
import io.bcaas.http.JsonTypeAdapter.TransactionChainVOTypeAdapter;
import io.bcaas.http.MasterServices;
import io.bcaas.listener.RequestResultListener;
import io.bcaas.listener.TCPReceiveBlockListener;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.GsonTool;
import io.bcaas.tools.StringTool;
import io.bcaas.vo.ClientIpInfoVO;
import io.bcaas.vo.DatabaseVO;
import io.bcaas.vo.GenesisVO;
import io.bcaas.vo.TransactionChainReceiveVO;
import io.bcaas.vo.TransactionChainSendVO;
import io.bcaas.vo.TransactionChainVO;
import io.bcaas.vo.WalletVO;

/**
 * TCP请求服务端，请求R区块的数据
 */
public class ReceiveThread extends Thread {
    private static String TAG = "ReceiveThread";
    //服务器地址
    public String ip;
    //服务器端口号
    public int port;
    //rpcPort
    public int rpcPort;

    public String writeStr = null;
    //是否存活
    public static boolean alive = true;
    public static Socket socket = null;
    private TCPReceiveBlockListener tcpReceiveBlockListener;

    public static Queue<TransactionChainVO> getWalletWaitingToReceiveQueue = new LinkedList<>();
    private MasterServices masterServices;

    public ReceiveThread(String writeString, TCPReceiveBlockListener tcpReceiveBlockListener) {
        this.ip = BcaasApplication.getExternalIp();
        this.port = BcaasApplication.getExternalPort();
        this.rpcPort = BcaasApplication.getRpcPort();
        this.writeStr = writeString;
        this.tcpReceiveBlockListener = tcpReceiveBlockListener;
        masterServices = new MasterServices(requestResultListener);
    }


    /**
     * 关闭线程
     */
    public static void kill() {
        alive = false;
        BcaasLog.d(TAG, "socket close...");
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
        BcaasLog.d(TAG, "初始化连接socket..." + ip + ":" + port);
        buildSocket();
    }


    // 重新建立socket连接
    private void buildSocket() {
        socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(ip, port), Constants.ValueMaps.sleepTime30000);
            socket.setKeepAlive(true);
            alive = true;
            writeTOSocket(socket, writeStr);
            //开启接收线程
            new HandlerThread(socket);
            if (socket.isConnected()) {
                tcpReceiveBlockListener.httpToRequestReceiverBlock();
            }
        } catch (Exception e) {
            e.printStackTrace();
            BcaasLog.e(TAG, " 初始化socket失败，请求「sfn」resetAN:" + e.getMessage());
            if (e instanceof ConnectException) {
                //如果当前连接不上，代表需要重新设置AN
                masterServices.reset();
            } else {
                tcpReceiveBlockListener.restartSocket();
            }
            tcpReceiveBlockListener.stopToHttpToRequestReceiverBlock();

        }

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
    public class HandlerThread implements Runnable {
        private Socket socket;

        public HandlerThread(Socket client) {
            socket = client;
            new Thread(this).start();
        }

        public final void run() {
            Gson gson = GsonTool.getGson();
            while (alive) {
                BcaasLog.d(TAG, "+++++++++++" + socket);
                try {
                    //读取服务器端数据
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    try {
                        while (socket.isConnected() && alive) {
                            try {
                                socket.sendUrgentData(0xFF); // 發送心跳包
                            } catch (Exception e) {
                                BcaasLog.d(TAG, "socket连接异常。。");
                                socket.close();
                                break;
                            }
                            String readLine = bufferedReader.readLine();
                            if (readLine != null && readLine.trim().length() != 0) {
                                BcaasLog.d(TAG, "step 1: 服务器端receive值是: " + readLine);
                                ResponseJson responseJson = gson.fromJson(readLine, ResponseJson.class);
                                if (responseJson != null) {
                                    String methodName = responseJson.getMethodName();

                                    if ("getLatestBlockAndBalance_SC".equals(methodName)) {

                                        getLatestBlockAndBalance_SC(responseJson);

                                    } else if ("getSendTransactionData_SC".equals(methodName)) {

                                        getSendTransactionData_SC(responseJson);

                                    } else if ("getReceiveTransactionData_SC".equals(methodName)) {

                                        getReceiveTransactionData_SC(responseJson);

                                    } else if ("getWalletWaitingToReceiveBlock_SC".equals(methodName)) {

                                        getWalletWaitingToReceiveBlock_SC(responseJson);

                                    } else {
                                        BcaasLog.d(TAG, "methodName error." + methodName);
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

    //"取得未簽章R區塊的Send區塊 & 取最新的R區塊 & wallet餘額"
    public void getWalletWaitingToReceiveBlock_SC(ResponseJson responseJson) {
        BcaasLog.d(TAG, "step 2:getWalletWaitingToReceiveBlock_SC");
        Gson gson = GsonTool.getGson();
        if (responseJson.getPaginationVOList() != null && responseJson.getPaginationVOList().get(0).getObjectList().size() == 0) {
            //没有未签章的区块
            BcaasLog.d(TAG, "没有需要签章的区块");

        } else {
            //有未签章的区块
            BcaasLog.d(TAG, "有需要签章的区块");
            if (responseJson.getPaginationVOList() != null) {
                List<TransactionChainVO> transactionChainVOList = new ArrayList<>();//存储当前需要显示在主页的未签章的R区块信息
                List<Object> objList = responseJson.getPaginationVOList().get(0).getObjectList();
                for (Object obj : objList) {
                    TransactionChainVO transactionChainVO = gson.fromJson(gson.toJson(obj), TransactionChainVO.class);
                    transactionChainVOList.add(transactionChainVO);//将当前遍历得到的单笔R区块存储起来
                    getWalletWaitingToReceiveQueue.offer(transactionChainVO);
                }
                tcpReceiveBlockListener.receiveBlockData(transactionChainVOList);
                TransactionChainVO sendChainVO = getWalletWaitingToReceiveQueue.poll();
                String amount = gson.fromJson(gson.toJson(sendChainVO.getTc()), TransactionChainSendVO.class).getAmount();
                BcaasLog.d(TAG, sendChainVO);
                receiveTransaction(amount, BcaasApplication.getAccessToken(), sendChainVO, responseJson);
            }
        }

        WalletVO walletVO = responseJson.getWalletVO();
        String walletBalance = walletVO != null ? walletVO.getWalletBalance() : "0";
        BcaasApplication.setWalletBalance(walletBalance);//存储当前的余额值
        tcpReceiveBlockListener.showWalletBalance(walletBalance);//通知页面更新当前的余额
    }

    //处理线程下面需要处理的R区块
    public void getReceiveTransactionData_SC(ResponseJson responseJson) {
        Gson gson = GsonTool.getGson();
        if (responseJson == null) return;
        if (responseJson.getCode() == 200) {
            try {
                TransactionChainVO sendVO = getWalletWaitingToReceiveQueue.poll();
                if (sendVO != null) {
                    String amount = gson.fromJson(gson.toJson(sendVO.getTc()), TransactionChainSendVO.class).getAmount();
                    receiveTransaction(amount, BcaasApplication.getAccessToken(), sendVO, responseJson);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //"取最新的區塊 &wallet餘額"
    public void getLatestBlockAndBalance_SC(ResponseJson responseJson) {
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                // 可能是Send/open区块
                .registerTypeAdapter(TransactionChainVO.class, new TransactionChainVOTypeAdapter("TransactionChainSendVO"))
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
                    BcaasLog.d(TAG, "发送失败。交易金额有误");
                    return;
                }
                tcpReceiveBlockListener.showWalletBalance(walletVO.getWalletBalance());//通知页面更新当前的余额
                String previousBlockStr = gson.toJson(databaseVO.getTransactionChainVO());
                BcaasLog.d(TAG, previousBlockStr);
                String previous = Sha256Tool.doubleSha256ToString(previousBlockStr);
                String apisendurl = "http://" + ip + ":" + rpcPort + Constants.RequestUrl.send;

                Object o = databaseVO.getTransactionChainVO().getTc();
                //json 出错，所以先gson.tojson() and then gson.fromJson()
                TransactionChainSendVO transactionChainSendVO = GsonTool.getGson().fromJson(GsonTool.getGson().toJson(o), TransactionChainSendVO.class);
                String virtualCoin = walletVO.getBlockService();
                BcaasLog.d(TAG, "receive virtualCoin:" + virtualCoin);
                // 2018/8/22请求AN send请求
                responseJson = MasterServices.sendAuthNode(apisendurl, previous, virtualCoin, destinationWallet, balanceAfterAmount, transactionAmount, BcaasApplication.getAccessToken());

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

    // 发送结果
    public void getSendTransactionData_SC(ResponseJson walletResponseJson) {
        if (walletResponseJson.getCode() == 200) {
            WalletVO walletVO = walletResponseJson.getWalletVO();
            if (walletVO != null) {
                tcpReceiveBlockListener.showWalletBalance(walletVO.getWalletBalance());
            }
            tcpReceiveBlockListener.sendTransactionSuccess("tcp 交易成功。");
        } else {
            tcpReceiveBlockListener.sendTransactionFailure("tcp 交易失败。" + walletResponseJson.getMessage());
        }

    }

    //处理签章区块
    public void receiveTransaction(String amount, String accessToken, TransactionChainVO transactionChainVO, ResponseJson walletResponseJson) {
        BcaasLog.d(TAG, "step 3:" + walletResponseJson);
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(GenesisVO.class, new GenesisVOTypeAdapter())//初始块有序
                .registerTypeAdapter(TransactionChainVO.class, new TransactionChainVOTypeAdapter("TransactionChainOpenVO"))
                .create();
        WalletVO walletVO = walletResponseJson.getWalletVO();
        if (walletVO != null) {
            tcpReceiveBlockListener.showWalletBalance(walletVO.getWalletBalance());
        }
        try {
            BcaasLog.d(TAG, "transactionChainVO.getTc():" + gson.toJson(transactionChainVO.getTc()));
            String doubleHashTc = Sha256Tool.doubleSha256ToString(gson.toJson(transactionChainVO.getTc()));
            BcaasLog.d(TAG, "step 4:doubleHashTc:" + doubleHashTc);

            String blockType = Constants.BLOCK_TYPE_RECEIVE;
            String previousDoubleHashStr = "";
            DatabaseVO databaseVO = walletResponseJson.getDatabaseVO();
            if (databaseVO != null) {
                TransactionChainVO transactionChainVONew = databaseVO.getTransactionChainVO();
                if (transactionChainVONew != null) {
                    String tcStr = gson.toJson(transactionChainVONew);
                    BcaasLog.d(TAG, tcStr);
                    previousDoubleHashStr = Sha256Tool.doubleSha256ToString(tcStr);
                } else {
                    GenesisVO genesisVONew = databaseVO.getGenesisVO();
                    String str = gson.toJson(genesisVONew);
                    BcaasLog.d(TAG, str);
                    previousDoubleHashStr = Sha256Tool.doubleSha256ToString(str);
                    blockType = Constants.BLOCK_TYPE_OPEN;
                }

            }
            BcaasLog.d(TAG, "step 5:previousDoubleHashStr:" + previousDoubleHashStr);

            if (StringTool.isEmpty(previousDoubleHashStr)) {
                BcaasLog.d(TAG, MessageConstants.PREVIOUS_IS_NULL);
                return;
            }
            String signatureSend = transactionChainVO.getSignature();
            String apiUrl = "http://" + ip + ":" + rpcPort + Constants.RequestUrl.receive;
            BcaasLog.d(TAG, apiUrl);
            Object tcObject = transactionChainVO.getTc();
            TransactionChainReceiveVO transactionChainReceiveVO = GsonTool.getGson().fromJson(GsonTool.getGson().toJson(tcObject), TransactionChainReceiveVO.class);
            String virtualCoin = transactionChainReceiveVO.getBlockService();
            if (StringTool.isEmpty(virtualCoin)) {
                BcaasLog.d(TAG, MessageConstants.VIRTUALCOIN_IS_NULL);
                virtualCoin = Constants.BlockService.BCC;
            }
            MasterServices.receiveAuthNode(apiUrl, previousDoubleHashStr, virtualCoin, doubleHashTc, amount, accessToken, signatureSend, blockType);
        } catch (Exception e) {
            BcaasLog.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    RequestResultListener requestResultListener = new RequestResultListener() {

        @Override
        public void resetAuthNodeFailure(String message) {
            BcaasLog.d(TAG, message);
            masterServices.reset();
        }

        @Override
        public void resetAuthNodeSuccess(ClientIpInfoVO clientIpInfoVO) {
            BcaasApplication.setClientIpInfoVO(clientIpInfoVO);//存储当前的AN信息
            buildSocket();
        }
    };

}
