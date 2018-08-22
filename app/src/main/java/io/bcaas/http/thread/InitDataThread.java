package io.bcaas.http.thread;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.gson.WalletRequestJson;
import io.bcaas.gson.WalletResponseJson;
import io.bcaas.http.MasterServices;
import io.bcaas.tools.BcaasLog;

import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author: tianyonghong
 * @date: 2018/8/15
 * @description
 */
public class InitDataThread extends Thread {

    private String TAG = "InitDataThread";

    // 存放 virtualCoin
    //public static Map<String, String> virtualCoinHashMap = new HashMap();
    private String virtualCoin = "BCC";

    private boolean alive = true;

    //进入钱包初始化数据
    @Override
    public void run() {

        String externalIp = BcaasApplication.getExternalIp();
        String internalIp = BcaasApplication.getInternalIp();
        int internalPort = BcaasApplication.getInternalPort();
        int externalPort = BcaasApplication.getExternalPort();
        int rpcPort = BcaasApplication.getRpcPort();

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        //拼接socket_writerStr
        WalletRequestJson ｗalletRequestJson = new WalletRequestJson(BcaasApplication.getAccessToken(), virtualCoin, BcaasApplication.getWalletAddress());
        String sendToSocketWrite = gson.toJson(ｗalletRequestJson);
        //启动socket
        Socket socket = startSocket(externalIp, externalPort, sendToSocketWrite);

        if (socket == null || !socket.isConnected()) {
            return;
        }
        //开启接收socket线程
        HandleReceiveThread handleReceiveThread = new HandleReceiveThread(socket, externalIp, rpcPort);
        handleReceiveThread.start();

        while (alive) {
            try {
                //Receive
                String walletAddress = BcaasApplication.getWalletAddress();
                String getBalanceApiurl = "http://" + externalIp + ":" + rpcPort + Constants.RequestUrl.getWalletWaitingToReceiveBlock;
                WalletResponseJson walletResponseJson = MasterServices.getWalletBalance(getBalanceApiurl, "BCC", walletAddress, BcaasApplication.getAccessToken());
                if (walletResponseJson.getCode() != 200) {
                    BcaasLog.d(TAG, "查询余额失败");
                }
                Thread.sleep(100000);
                BcaasLog.d(TAG, "查询余额成功。。。");
            } catch (Exception e) {
                e.printStackTrace();
                alive = false;
            }
        }
    }

    void kill() {
        alive = false;

    }

    //开启socket连线
    private Socket startSocket(String socketIp, int socketPort, String sendToSocketWrite) {
        try {
            BcaasLog.d(TAG, "初始化连接socket....:IP :{} , PORT :{}" + socketIp + ":" + socketPort);
            Socket socket = new Socket(socketIp, socketPort);
            socket.setKeepAlive(true);

            if (socket.isConnected()) {
                //向服务器端发送数据
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                printWriter.write(sendToSocketWrite + "\n");
                printWriter.flush();

                BcaasLog.d(TAG, "已发送socket数据 json:" + sendToSocketWrite);
            }
            return socket;
        } catch (Exception e) {
            BcaasLog.d(TAG, "初始化socket连接失败..." + e.getMessage());
            return null;
        }
    }

}
