package io.bcaas.http.thread;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
import io.bcaas.tools.BcaasLog;
import io.bcaas.vo.TransactionChainOpenVO;
import io.bcaas.vo.TransactionChainReceiveVO;
import io.bcaas.vo.TransactionChainSendVO;
import io.bcaas.vo.TransactionChainVO;

/**
 * @author: tianyonghong
 * @date: 2018/8/15
 * @description
 */
public class HandleReceiveThread extends Thread {

    private static String TAG = "HandleReceiveThread";
    //是否存活
    public static boolean alive = true;

    public static Map<String, WalletResponseJson> balanceMap = new HashMap();

    public static Queue<TransactionChainVO> getWalletWaitingToReceiveQueue = new LinkedList<>();

    private Socket socket;

    //服务器地址
    public static String IP = "192.168.31.5";
    //服务器端口号
    public static int RPC_PORT = 18218;
    //发起交易时收款方地址
    public static String destinationWallet;
    //发起交易的金额
    public static String amount;

    public HandleReceiveThread(Socket socket, String ip, int rpcPort) {
        IP = ip;
        RPC_PORT = rpcPort;
        this.socket = socket;
    }

    /**
     * 关闭线程
     */
    public static void kill() {
        alive = false;
        BcaasLog.d(TAG, "HandleReceiveThread close...");
    }

    @Override
    public void run() {
        try {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            //while (alive) {
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
                            BcaasLog.d(TAG, "服务器端receive值是: " + readLine);

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
                    e.printStackTrace();
                } finally {
                    BcaasLog.d(TAG, "关闭socket 连线。。");
                }
            } catch (Exception e) {
                e.printStackTrace();
                // break;
            }
            //  }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

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
        changeLableBalance(balanceMap.get(blockService).getWalletBalance());
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

        //改变余额控件
        changeLableBalance(balanceMap.get(blockService).getWalletBalance());

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
                String apisendurl = "http://" + IP + ":" + RPC_PORT + Constants.RequestUrl.send;
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
            changeBtnSend(false);
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

            String apiUrl = "http://" + IP + ":" + RPC_PORT + Constants.RequestUrl.receive;
            String virtualCoin = ((TransactionChainReceiveVO) transactionChainVO.getTc()).getBlockService();
            BcaasLog.d(TAG, "receive virtualCoin:" + virtualCoin);
// TODO: 2018/8/22 AN receive请求
            MasterServices.receiveAuthNode(apiUrl, previouDoubleHashStr, virtualCoin, doubleHashTc, amount, accessToken, signatureSend, blockType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //更新主页余额
    public void changeLableBalance(String balance) {
        //todo 更新页面
//        //更新控件的线程
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//                WholeController.balanceProperty.set(balance);
//            }
//        });
    }

    //更新主页发送按钮状态
    public void changeBtnSend(boolean disable) {
        // TODO: 2018/8/22
//        //更新控件的线程
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//                WholeController.disableProperty.set(disable);
//            }
//        });
    }

}
