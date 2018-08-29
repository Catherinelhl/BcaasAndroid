package io.bcaas.http.thread;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.bcaas.base.BcaasApplication;
import io.bcaas.bean.SeedFullNodeBean;
import io.bcaas.constants.APIURLConstants;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.http.MasterServices;
import io.bcaas.tools.BcaasLog;
import io.bcaas.vo.ClientIpInfoVO;

import java.io.PrintWriter;
import java.net.Socket;

import io.bcaas.constants.SystemConstants;
import io.bcaas.vo.WalletVO;

import java.util.List;

/**
 * @author: tianyonghong
 * @date: 2018/8/15
 * @description
 */
public class InitDataThread extends Thread {

    private String TAG = "InitDataThread";

    private boolean alive = true;

    private ClientIpInfoVO clientIpInfoVO;

    //存放下次请求waitingReceive 的时间(5 min)
    public static Long tcpReturnTime;
    public static String nextObjectId = "";

    //进入钱包初始化数据
    @Override
    public void run() {
        //登录 获取 SFN 地址
        if (BcaasApplication.getAccessTokenFromSP() == null) {
            List<SeedFullNodeBean> seedFullNodeBeanList = MasterServices.login();

            if (seedFullNodeBeanList == null) {
                return;
            }

            for (SeedFullNodeBean seedList : seedFullNodeBeanList) {
                String seedFullNodeUrl = "http://" + seedList.getIp() + ":" + seedList.getPort();
                System.out.println("seedFullNodeUrl====" + seedFullNodeUrl);
                SystemConstants.seedFullNodeList.add(seedFullNodeUrl);
            }
        }

        //验证钱包地址,获取 AN 地址
        clientIpInfoVO = MasterServices.verify();
        if (clientIpInfoVO == null) {
            BcaasLog.d(TAG, "验证钱包地址失败。。。");
            return;
        }
        String externalIp = clientIpInfoVO.getExternalIp();
        String internalIp = clientIpInfoVO.getInternalIp();
        int internalPort = clientIpInfoVO.getInternalPort();
        int externalPort = clientIpInfoVO.getExternalPort();
        int rpcPort = clientIpInfoVO.getRpcPort();

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        //拼接socket_writerStr
        WalletVO walletVO = new WalletVO();
        walletVO.setBlockService(BcaasApplication.getBlockServiceFromSP());
        walletVO.setWalletAddress(BcaasApplication.getWalletAddress());
        walletVO.setAccessToken(BcaasApplication.getAccessTokenFromSP());

        RequestJson requestJson = new RequestJson();
        requestJson.setWalletVO(walletVO);

        String sendToSocketWrite = gson.toJson(requestJson);
        //启动socket
        Socket socket = startSocket(externalIp, externalPort, sendToSocketWrite);

        if (socket == null || !socket.isConnected()) {
            return;
        }

        //开启接收socket线程
        HandleReceiveThread.IP = clientIpInfoVO.getExternalIp();
        HandleReceiveThread.RPC_PORT = clientIpInfoVO.getRpcPort();
        HandleReceiveThread handleReceiveThread = new HandleReceiveThread(socket, externalIp, rpcPort);
        handleReceiveThread.start();

        while (alive) {
            try {
                Long start = System.currentTimeMillis();
                BcaasLog.d(TAG, "查询余额成功.....start:" + start);
                //Receive
                String walletAddress = BcaasApplication.getWalletAddress();
                String getBalanceApiUrl = APIURLConstants.API_WALLET_GETWALLETWAITINGTORECEIVEBLOCK;

                ResponseJson responseJson = MasterServices.getWalletWaiting(getBalanceApiUrl, BcaasApplication.getBlockServiceFromSP(), walletAddress, BcaasApplication.getAccessTokenFromSP(), nextObjectId);

                if (responseJson.getCode() != MessageConstants.CODE_200) {
                    BcaasLog.d(TAG, "查询余额失败");
                }

                //tcp返回时间到http请求时间小于5分钟则睡眠,否则不休眠
                if (tcpReturnTime != null && tcpReturnTime - start < Constants.ValueMaps.REQUEST_RECEIVE_TIME) {
                    try {
                        Thread.sleep(Constants.ValueMaps.REQUEST_RECEIVE_TIME - (tcpReturnTime - start));
                        System.out.println("查询余额成功..等待TCP返回...sleep:" + (Constants.ValueMaps.REQUEST_RECEIVE_TIME - (tcpReturnTime - start)));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (tcpReturnTime == null) {
                    Thread.sleep(Constants.ValueMaps.REQUEST_RECEIVE_TIME);
                    System.out.println("查询余额成功..等待TCP返回...sleep:" + Constants.ValueMaps.REQUEST_RECEIVE_TIME);
                }
            } catch (Exception e) {
                e.printStackTrace();
                alive = false;
            }
        }
    }

    public void kill() {
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

                BcaasLog.d(TAG, "Init Socket:{} , sendToSocketWrite Json :{}" + socket + ":" + sendToSocketWrite);
            }
            return socket;
        } catch (Exception e) {
            BcaasLog.d(TAG, "初始化socket连接失败 :" + e.getMessage() + ",开始调用reset方法...");

            //连接失败，调用reset
//            clientIpInfoVO = MasterServices.reset();

            BcaasLog.d(TAG, "开始重连socket.......");
            //重连socket
            startSocket(clientIpInfoVO.getExternalIp(), clientIpInfoVO.getExternalPort(), sendToSocketWrite);

            return null;
        }
    }

}
