package io.bcaas.http.thread;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
import io.bcaas.http.MasterServices;
import io.bcaas.listener.TCPReceiveBlockListener;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.GsonTool;
import io.bcaas.tools.RegexTool;
import io.bcaas.tools.StringTool;
import io.bcaas.vo.DatabaseVO;
import io.bcaas.vo.GenesisVO;
import io.bcaas.vo.TransactionChainOpenVO;
import io.bcaas.vo.TransactionChainReceiveVO;
import io.bcaas.vo.TransactionChainSendVO;
import io.bcaas.vo.TransactionChainVO;
import io.bcaas.vo.WalletVO;

/**
 * TCP请求服务端，请求R区块的数据
 */
public class ReceiveThread extends Thread {
    private String TAG = "ReceiveThread";
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

    public ReceiveThread(String writeString, TCPReceiveBlockListener tcpReceiveBlockListener) {
        this.ip = BcaasApplication.getExternalIp();
        this.port = BcaasApplication.getExternalPort();
        this.rpcPort = BcaasApplication.getRpcPort();
        this.writeStr = writeString;
        this.tcpReceiveBlockListener = tcpReceiveBlockListener;
        BcaasLog.d(TAG, "socket 请求地址：" + this.ip + ":" + this.port);
    }


    /**
     * 关闭线程
     */
    public void kill() {

        alive = false;
        BcaasLog.d(TAG, "socket close...");
        try {
            socket.close();
        } catch (Exception e) {
            BcaasLog.e(TAG, "socket close Exception..." + e.getMessage());
        }

    }

    @Override
    public final void run() {
        try {
            BcaasLog.d(TAG, "初始化连接socket..." + ip + ":" + port);
            //初始化连接socket
            socket = new Socket();
            //new InetSocketAddress（）这个后面可以设置超时时间，默认的超时时间可能会久一点
            int timeout = Constants.ValueMaps.sleepTime30000;
            socket.connect(new InetSocketAddress(ip, port), timeout);
            socket.setKeepAlive(true);
            alive = true;
            writeTOSocket(socket, writeStr);
            //开启接收线程
            new HandlerThread(socket);
            if (socket.isConnected()) {

                tcpReceiveBlockListener.httpToRequestReceiverBlock();
            }
        } catch (Exception e) {// TODO: 2018/8/23 直接在这里好像会循环
//            tcpReceiveBlockListener.resetANAddress();
            BcaasLog.e(TAG, " 初始化socket失败。。");
            e.printStackTrace();
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
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            while (alive) {
                BcaasLog.d(TAG, "+++++++++++"+socket);
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
                                BcaasLog.d(TAG, " 服务器端receive值是: " + readLine);
                                ResponseJson walletResponseJson = gson.fromJson(readLine, ResponseJson.class);
                                String methodName = walletResponseJson.getMethodName();

                                if ("getLatestBlockAndBalance_SC".equals(methodName)) {

                                    getLatestBlockAndBalance_SC(walletResponseJson);

                                } else if ("getSendTransactionData_SC".equals(methodName)) {

                                    getSendTransactionData_SC(walletResponseJson);

                                } else if ("getReceiveTransactionData_SC".equals(methodName)) {

                                    getReceiveTransactionData_SC(walletResponseJson);

                                } else if ("getWalletWaitingToReceiveBlock_SC".equals(methodName)) {

                                    getWalletWaitingToReceiveBlock_SC(walletResponseJson);

                                } else {
                                    BcaasLog.d(TAG, "methodName error." + methodName);
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
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    //"取得未簽章R區塊的Send區塊 & 取最新的R區塊 & wallet餘額"
    public void getWalletWaitingToReceiveBlock_SC(ResponseJson responseJson) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String blockService = "";
        if (responseJson.getPaginationVOList() != null && responseJson.getPaginationVOList().get(0).getObjectList().size() == 0) {
//没有未签章的区块
            DatabaseVO databaseVO = responseJson.getDatabaseVO();
            if (databaseVO != null) {
                Object tcObject = databaseVO.getTransactionChainVO().getTc();
                if (tcObject instanceof TransactionChainSendVO) {
                    blockService = ((TransactionChainSendVO) tcObject).getBlockService();
                } else if (tcObject instanceof TransactionChainOpenVO) {
                    blockService = ((TransactionChainOpenVO) tcObject).getBlockService();
                }
            }

        } else {
            //有未签章的区块
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
                BcaasLog.d(TAG, responseJson);
                receiveTransaction(amount, BcaasApplication.getAccessToken(), sendChainVO, responseJson);
            }
        }

        WalletVO walletVO = responseJson.getWalletVO();
        BcaasLog.d(TAG, "余额：" + walletVO != null ? walletVO.getWalletBalance() : "0");
    }

    //处理线程下面需要处理的R区块
    public void getReceiveTransactionData_SC(ResponseJson walletResponseJson) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        if (walletResponseJson.getCode() == 200) {

            try {
                TransactionChainVO sendVO = getWalletWaitingToReceiveQueue.poll();
                if (sendVO != null) {
                    String amount = gson.fromJson(gson.toJson(sendVO.getTc()), TransactionChainSendVO.class).getAmount();
                    receiveTransaction(amount, BcaasApplication.getAccessToken(), sendVO, walletResponseJson);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //"取最新的區塊 &wallet餘額"
    public void getLatestBlockAndBalance_SC(ResponseJson responseJson) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        DatabaseVO databaseVO = responseJson.getDatabaseVO();
        if (databaseVO == null) return;
        Object tcObj = databaseVO.getTransactionChainVO().getTc();
        String blockService = "";
        if (tcObj instanceof TransactionChainSendVO) {
            blockService = ((TransactionChainSendVO) tcObj).getBlockService();
        } else if (tcObj instanceof TransactionChainOpenVO) {
            blockService = ((TransactionChainOpenVO) tcObj).getBlockService();
        }
        BcaasLog.d(TAG, "getLatestBlockAndBalance_SC:余额：" + responseJson.getWalletVO().getWalletBalance());

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
                String previousBlockStr = gson.toJson(databaseVO.getTransactionChainVO());
                String previous = Sha256Tool.doubleSha256ToString(previousBlockStr);
                String apisendurl = "http://" + ip + ":" + rpcPort + Constants.RequestUrl.send;

                Object o = databaseVO.getTransactionChainVO().getTc();
                //json 出错，所以先gson.tojson() and then gson.fromJson()
                TransactionChainSendVO transactionChainSendVO = GsonTool.getGson().fromJson(GsonTool.getGson().toJson(o), TransactionChainSendVO.class);
                String virtualCoin = transactionChainSendVO.getBlockService();
                BcaasLog.d(TAG, "receive virtualCoin:" + virtualCoin);
                // 2018/8/22请求AN send请求
                responseJson = MasterServices.sendAuthNode(apisendurl, previous, virtualCoin, destinationWallet, balanceAfterAmount, transactionAmount, BcaasApplication.getAccessToken());

                if (responseJson != null && responseJson.getCode() == 200) {
                    tcpReceiveBlockListener.sendTransactionSuccess("交易发送成功，等待处理中。");
                } else {
                    tcpReceiveBlockListener.sendTransactionFailure("交易发送失败。");
                }
            } else {
                tcpReceiveBlockListener.sendTransactionFailure("发送失败。交易金额有误");
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 发送结果
    public void getSendTransactionData_SC(ResponseJson walletResponseJson) {
        if (walletResponseJson.getCode() == 200) {
            BcaasLog.d(TAG, "交易成功。");
        } else {
            BcaasLog.d(TAG, "交易失败。" + walletResponseJson.getMessage());
        }

    }

    //处理签章区块
    public void receiveTransaction(String amount, String accessToken, TransactionChainVO transactionChainVO, ResponseJson walletResponseJson) {
        BcaasLog.d(TAG, "receiveTransaction" + walletResponseJson);
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(GenesisVO.class, new GenesisVOTypeAdapter())
                .create();
        try {
            String doubleHashTc = Sha256Tool.doubleSha256ToString(transactionChainVO.getTc().toString());
            String blockType = Constants.BLOCK_TYPE_RECEIVE;
            String previousDoubleHashStr = "";
            DatabaseVO databaseVO = walletResponseJson.getDatabaseVO();
            if (databaseVO != null) {
                TransactionChainVO transactionChainVONew = databaseVO.getTransactionChainVO();
                if (transactionChainVONew != null) {
                    String tcStr = gson.toJson(transactionChainVONew);
                    previousDoubleHashStr = Sha256Tool.doubleSha256ToString(tcStr);
                } else {
                    GenesisVO genesisVONew = databaseVO.getGenesisVO();
                    previousDoubleHashStr = Sha256Tool.doubleSha256ToString(RegexTool.replaceBlank(gson.toJson(genesisVONew)));
                    blockType = Constants.BLOCK_TYPE_OPEN;
                }

            }
            BcaasLog.d(TAG, previousDoubleHashStr);

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
            // todo 2018/8/22 AN receive请求  打开·信息不对，错误数据干扰willie
//            MasterServices.receiveAuthNode(apiUrl, previousDoubleHashStr, virtualCoin, doubleHashTc, amount, accessToken, signatureSend, blockType);
        } catch (Exception e) {
            BcaasLog.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }


}
