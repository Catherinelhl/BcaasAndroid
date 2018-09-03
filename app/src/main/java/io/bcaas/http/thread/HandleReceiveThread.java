package io.bcaas.http.thread;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.APIURLConstants;
import io.bcaas.constants.Constants;
import io.bcaas.ecc.Sha256Tool;
import io.bcaas.gson.ResponseJson;
import io.bcaas.http.MasterServices;
import io.bcaas.tools.BcaasLog;
import io.bcaas.vo.TransactionChainSendVO;
import io.bcaas.vo.TransactionChainVO;


/**
 * @author: tianyonghong
 * @date: 2018/8/15
 * @description
 */

@Deprecated
public class HandleReceiveThread extends Thread {
    private static String TAG = HandleReceiveThread.class.getSimpleName();
    //是否存活
    public static boolean alive = true;

    public static Queue<TransactionChainVO> getWalletWaitingToReceiveQueue = new LinkedList<>();

    public static Socket socket;

    //服务器地址
    public static String IP;
    //服务器端口号
    public static int RPC_PORT;
    //发起交易时收款方地址
    public static String destinationWallet;
    //发起交易的金额
    public static String amount;

    public HandleReceiveThread(Socket socketClient, String ip, int rpcPort) {
        IP = ip;
        RPC_PORT = rpcPort;
        socket = socketClient;
    }

    /**
     * 关闭线程
     */
    public static void kill() {
        alive = false;
        BcaasLog.d(TAG, "HandleReceiveThread kill...");
    }

    @Override
    public void run() {
        BcaasLog.d(TAG, "HandleReceiveThread start...");
        try {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            while (alive) {
                try {
                    //读取服务器端数据
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    try {
                        while (socket.isConnected() && alive) {
//                            try {
//                                socket.sendUrgentData(0xFF); // 發送心跳包
//                            } catch (Exception e) {
//                                Constants.LOGGER_INFO.info("HandleReceiveThread Socket Exception :{}",e.getMessage());
//                                socket.close();
//                                break;
//                            }
                            String readLine = bufferedReader.readLine();
                            if (readLine != null && readLine.trim().length() != 0) {
                                BcaasLog.d(TAG, "=== TCP Receive Message :{}" + readLine);

                                ResponseJson responseJson = gson.fromJson(readLine, ResponseJson.class);

                                String methodName = responseJson.getMethodName();

                                //呼叫HTTP API: /wallet/getLatestBlockAndBalance
                                if (APIURLConstants.TCP_GETLATESTBLOCKANDBALANCE_SC.equals(methodName)) {
                                    getLatestBlockAndBalance_SC(responseJson);
                                }
                                //呼叫HTTP API: /transactionChain/send
                                else if (APIURLConstants.TCP_GETSENDTRANSACTIONDATA_SC.equals(methodName)) {
                                    getSendTransactionData_SC(responseJson);
                                }
                                //呼叫HTTP API: /transactionChain/receive
                                else if (APIURLConstants.TCP_GETRECEIVETRANSACTIONDATA_SC.equals(methodName)) {
                                    getReceiveTransactionData_SC(responseJson);
                                }
                                //呼叫HTTP API: /wallet/getWalletWaitingToReceiveBlock
                                else if (APIURLConstants.TCP_GETWALLETWAITINGTORECEIVEBLOCK_SC.equals(methodName)) {
                                    getWalletWaitingToReceiveBlock_SC(responseJson);
                                } else {
                                    BcaasLog.d(TAG, "HandleReceiveThread receive methodName error:" + readLine);
                                }
                            }
                        }
                    } catch (Exception e) {
                        BcaasLog.d(TAG, "socket Exception :" + e.getMessage());
                        e.printStackTrace();
                    } finally {
                        alive = false;
                        BcaasLog.d(TAG, "关闭socket 连线。。");
                        socket.close();

                    }
                } catch (Exception e) {
                    BcaasLog.d(TAG, "socket Exception !!!:" + e.getMessage());
                    e.printStackTrace();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //取得未签章的R区块
    public void getWalletWaitingToReceiveBlock_SC(ResponseJson responseJson) {
        Long start = System.currentTimeMillis();
        InitDataThread.tcpReturnTime = start;
        if (responseJson.getPaginationVO() != null) {
            InitDataThread.nextObjectId = responseJson.getPaginationVO().getNextObjectId();
            System.out.println("InitDataThread.nextObjectId :" + InitDataThread.nextObjectId);
        }

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String blockService = responseJson.getWalletVO().getBlockService();
        if (responseJson.getPaginationVOList() != null) {
            List<Object> objList = responseJson.getPaginationVOList().get(0).getObjectList();
            for (Object obj : objList) {
                TransactionChainVO transactionChainVO = gson.fromJson(gson.toJson(obj), TransactionChainVO.class);
                getWalletWaitingToReceiveQueue.offer(transactionChainVO);
            }
            TransactionChainVO sendChainVO = getWalletWaitingToReceiveQueue.poll();
            amount = gson.fromJson(gson.toJson(sendChainVO.getTc()), TransactionChainSendVO.class).getAmount();

            receiveTransaction(amount, BcaasApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN), sendChainVO, responseJson);
        }
//        }
        System.out.println("blockService:" + blockService + ",余额：" + responseJson.getWalletVO().getWalletBalance());
        //改变余额控件
        changeLableBalance(responseJson.getWalletVO().getWalletBalance());
    }

    //
    public void getReceiveTransactionData_SC(ResponseJson responseJson) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        if (responseJson.getCode() == 200) {
            try {
//                String blockService = responseJson.getWalletVO().getStringFromSP(Constants.Preference.BLOCK_SERVICE);
                String blockService = "BCC";

                System.out.println("blockService:" + blockService + ",余额：" + responseJson.getWalletVO().getWalletBalance());
                //改变余额控件
                changeLableBalance(responseJson.getWalletVO().getWalletBalance());

                TransactionChainVO sendVO = getWalletWaitingToReceiveQueue.poll();
                if (sendVO != null) {
                    amount = gson.fromJson(gson.toJson(sendVO.getTc()), TransactionChainSendVO.class).getAmount();
                    receiveTransaction(amount, BcaasApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN), sendVO, responseJson);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //取得上一笔交易信息 用于发送交易之前获取余额
    public void getLatestBlockAndBalance_SC(ResponseJson responseJson) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        //TODO
//        String blockService = responseJson.getWalletVO().getStringFromSP(Constants.Preference.BLOCK_SERVICE);
        String blockService = "BCC";

        System.out.println("getLatestBlockAndBalance_SC blockService：" + blockService + "===余额:" + responseJson.getWalletVO().getWalletBalance());
        //改变余额控件
        changeLableBalance(responseJson.getWalletVO().getWalletBalance());

        if (destinationWallet != null && amount != null) {
            try {
                System.out.println("amountMoney:" + amount + ",destinationWallet:" + destinationWallet);
                long balanceAfterAmount = Integer.parseInt(responseJson.getWalletVO().getWalletBalance()) - Integer.parseInt(amount);
                if (balanceAfterAmount < 0) {
                    System.out.println("发送失败。交易金额有误");
                    return;
                }
                String previousBlockStr = gson.toJson(responseJson.getDatabaseVO().getTransactionChainVO());
                String previous = Sha256Tool.doubleSha256ToString(previousBlockStr);

                //blockService = ((TransactionChainReceiveVO) serverResponseJson.getTransactionChainVO().getTc()).getStringFromSP(Constants.Preference.BLOCK_SERVICE);
                System.out.println("receive virtualCoin:" + blockService);

                responseJson = MasterServices.sendAuthNode(previous, blockService, destinationWallet, balanceAfterAmount, amount, BcaasApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN));

                if (responseJson != null && responseJson.getCode() == 200) {
                    System.out.println("交易发送成功，等待处理中。");
                } else {
                    System.out.println("交易发送失败。");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                destinationWallet = null;
                amount = null;
            }
        }
    }

    //交易完成返回信息
    public void getSendTransactionData_SC(ResponseJson responseJson) {
        if (responseJson.getCode() == 200) {
            System.out.println("交易成功。");
            String blockService = responseJson.getWalletVO().getWalletBalance();
            System.out.println("blockService:" + blockService + ",余额：" + responseJson.getWalletVO().getWalletBalance());
            //改变余额控件
            changeLableBalance(responseJson.getWalletVO().getWalletBalance());
        } else {
            System.out.println("交易失败。" + responseJson.getMessage());
        }
        //发送按钮解锁状态
        changeBtnSend(false);

    }

    //R签章
    public void receiveTransaction(String amount, String accessToken, TransactionChainVO transactionChainVO, ResponseJson responseJson) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        try {
            String doubleHashTc = Sha256Tool.doubleSha256ToString(transactionChainVO.getTc().toString());
            String blockType = Constants.ValueMaps.BLOCK_TYPE_RECEIVE;
            String previouDoubleHashStr = "";

            if (responseJson.getDatabaseVO().getTransactionChainVO() != null) {

                String tcStr = gson.toJson(responseJson.getDatabaseVO().getTransactionChainVO());
                previouDoubleHashStr = Sha256Tool.doubleSha256ToString(tcStr);

            } else {
                previouDoubleHashStr = Sha256Tool.doubleSha256ToString(gson.toJson(responseJson.getDatabaseVO().getGenesisVO()));
                blockType = Constants.ValueMaps.BLOCK_TYPE_OPEN;
            }
            String signatureSend = transactionChainVO.getSignature();

            MasterServices.receiveAuthNode(previouDoubleHashStr,  Constants.BLOCKSERVICE_BCC, doubleHashTc, amount, accessToken, signatureSend, blockType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //更新主页余额
    public void changeLableBalance(String balance) {
        //更新控件的线程
    }

    //更新主页发送按钮状态
    public void changeBtnSend(boolean disable) {
        //更新控件的线程
    }

}
