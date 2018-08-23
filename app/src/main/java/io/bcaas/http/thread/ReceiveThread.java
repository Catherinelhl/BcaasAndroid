package io.bcaas.http.thread;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.ecc.Sha256Tool;
import io.bcaas.gson.WalletResponseJson;
import io.bcaas.http.MasterServices;
import io.bcaas.listener.TCPReceiveBlockListener;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.StringTool;
import io.bcaas.vo.TransactionChainOpenVO;
import io.bcaas.vo.TransactionChainReceiveVO;
import io.bcaas.vo.TransactionChainSendVO;
import io.bcaas.vo.TransactionChainVO;

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

    //发起交易时收款方地址
    public static String destinationWallet;
    //发起交易的金额
    public static String amount;

    //是否存活
    public static boolean alive = true;
    public static Socket socket = null;
    private TCPReceiveBlockListener tcpReceiveBlockListener;
    public static Map<String, WalletResponseJson> balanceMap = new HashMap();

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
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        try {
            BcaasLog.d(TAG, "初始化连接socket..." + ip + ":" + port);
            //  socket = new Socket(ip, port);
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
            //为了能让http 请求提醒在socket之后，所以这里暂时让其睡眠1500；
            Thread.sleep(Constants.ValueMaps.sleepTime1500);

            if (socket.isConnected()) {
                BcaasLog.d(TAG, "发送Http+++++++++++");
                tcpReceiveBlockListener.httpToRequestReceiverBlock();
            }
        } catch (Exception e) {
            // TODO: 2018/8/22 初始化TCp失败，是否需要重连？
//            tcpReceiveBlockListener.resetANSocket();
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

                BcaasLog.d(TAG, "已发送socket数据 json:" + writeStr);
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

            BcaasLog.d(TAG, socket);
            while (alive) {
                BcaasLog.d(TAG, "+++++++++++");
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
                                WalletResponseJson walletResponseJson = gson.fromJson(readLine, WalletResponseJson.class);
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
                        tcpReceiveBlockListener.resetANSocket();
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
    public void getWalletWaitingToReceiveBlock_SC(WalletResponseJson walletResponseJson) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String blockService = "";
        if (walletResponseJson.getPaginationVOList() != null && walletResponseJson.getPaginationVOList().get(0).getObjectList().size() == 0) {
//没有send区块
            Object tcObject = walletResponseJson.getTransactionChainVO().getTc();
            if (tcObject instanceof TransactionChainSendVO) {
                blockService = ((TransactionChainSendVO) tcObject).getBlockService();
            } else if (tcObject instanceof TransactionChainOpenVO) {
                blockService = ((TransactionChainOpenVO) tcObject).getBlockService();
            }
        } else {

            if (walletResponseJson.getPaginationVOList() != null) {
                tcpReceiveBlockListener.receiveBlockData(walletResponseJson.getPaginationVOList());
                List<Object> objList = walletResponseJson.getPaginationVOList().get(0).getObjectList();
                for (Object obj : objList) {
                    TransactionChainVO transactionChainVO = gson.fromJson(gson.toJson(obj), TransactionChainVO.class);
                    getWalletWaitingToReceiveQueue.offer(transactionChainVO);
                }
                TransactionChainVO sendChainVO = getWalletWaitingToReceiveQueue.poll();
                amount = gson.fromJson(gson.toJson(sendChainVO.getTc()), TransactionChainSendVO.class).getAmount();

                receiveTransaction(amount, BcaasApplication.getAccessToken(), sendChainVO, walletResponseJson);
            }
        }

        balanceMap.put(blockService, walletResponseJson);
        BcaasLog.d(TAG, "余额：" + balanceMap.get(blockService).getWalletBalance());
    }

    public void getReceiveTransactionData_SC(WalletResponseJson walletResponseJson) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        if (walletResponseJson.getCode() == 200) {

            try {
                TransactionChainVO sendVO = getWalletWaitingToReceiveQueue.poll();
                if (sendVO != null) {
                    amount = gson.fromJson(gson.toJson(sendVO.getTc()), TransactionChainSendVO.class).getAmount();
                    receiveTransaction(amount, BcaasApplication.getAccessToken(), sendVO, walletResponseJson);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //"取最新的區塊 &wallet餘額"
    public void getLatestBlockAndBalance_SC(WalletResponseJson walletResponseJson) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        Object tcObj = walletResponseJson.getTransactionChainVO().getTc();
        String blockService = "";
        if (tcObj instanceof TransactionChainSendVO) {
            blockService = ((TransactionChainSendVO) tcObj).getBlockService();
        } else if (tcObj instanceof TransactionChainOpenVO) {
            blockService = ((TransactionChainOpenVO) tcObj).getBlockService();
        }
        balanceMap.put(blockService, walletResponseJson);
        BcaasLog.d(TAG, "getLatestBlockAndBalance_SC:余额：" + balanceMap.get(blockService).getWalletBalance());

        if (StringTool.isEmpty(destinationWallet)) {
            destinationWallet = "15kep79cnyP2hCSokvT2fjo95FcdPMuRcG";
        }
        if (StringTool.isEmpty(amount)) {
            amount = "10";
        }
        if (destinationWallet != null && amount != null) {
            try {
                BcaasLog.d(TAG, "amountMoney:" + amount + ",destinationWallet:" + destinationWallet);
                int balanceAfterAmount = Integer.parseInt(balanceMap.get(blockService).getWalletBalance()) - Integer.parseInt(amount);
                if (balanceAfterAmount < 0) {
                    BcaasLog.d(TAG, "发送失败。交易金额有误");
                    return;
                }
                String previousBlockStr = gson.toJson(walletResponseJson.getTransactionChainVO());
                String previous = Sha256Tool.doubleSha256ToString(previousBlockStr);
                String apisendurl = "http://" + ip + ":" + rpcPort + Constants.RequestUrl.send;
                String virtualCoin = ((TransactionChainReceiveVO) walletResponseJson.getTransactionChainVO().getTc()).getBlockService();
                BcaasLog.d(TAG, "receive virtualCoin:" + virtualCoin);
// TODO: 2018/8/22请求AN send请求
                walletResponseJson = MasterServices.sendAuthNode(apisendurl, previous, virtualCoin, destinationWallet, balanceAfterAmount, amount, BcaasApplication.getAccessToken());

                if (walletResponseJson != null && walletResponseJson.getCode() == 200) {
                    BcaasLog.d(TAG, "交易发送成功，等待处理中。");
                } else {
                    BcaasLog.d(TAG, "交易发送失败。");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                destinationWallet = null;
                amount = null;
            }
        }
    }

    public void getSendTransactionData_SC(WalletResponseJson walletResponseJson) {
        if (walletResponseJson.getCode() == 200) {
            BcaasLog.d(TAG, "交易成功。");
        } else {
            BcaasLog.d(TAG, "交易失败。" + walletResponseJson.getMessage());
        }

    }

    public void receiveTransaction(String amount, String accessToken, TransactionChainVO transactionChainVO, WalletResponseJson walletResponseJson) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        try {
            String doubleHashTc = Sha256Tool.doubleSha256ToString(transactionChainVO.getTc().toString());
            String blockType = Constants.BLOCK_TYPE_RECEIVE;
            String previouDoubleHashStr = "";

            if (walletResponseJson.getTransactionChainVO() != null) {

                String tcStr = gson.toJson(walletResponseJson.getTransactionChainVO());
                previouDoubleHashStr = Sha256Tool.doubleSha256ToString(tcStr);

            } else {
                previouDoubleHashStr = Sha256Tool.doubleSha256ToString(gson.toJson(walletResponseJson.getGenesisVO()));
                blockType = Constants.BLOCK_TYPE_OPEN;
            }
            String signatureSend = transactionChainVO.getSignature();

            String apiUrl = "http://" + ip + ":" + rpcPort + Constants.RequestUrl.receive;
            String virtualCoin = ((TransactionChainReceiveVO) transactionChainVO.getTc()).getBlockService();
            BcaasLog.d(TAG, "receive virtualCoin:" + virtualCoin);
// TODO: 2018/8/22 AN receive请求
            MasterServices.receiveAuthNode(apiUrl, previouDoubleHashStr, virtualCoin, doubleHashTc, amount, accessToken, signatureSend, blockType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
