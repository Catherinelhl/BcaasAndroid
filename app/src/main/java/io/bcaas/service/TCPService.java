package io.bcaas.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import io.bcaas.base.BCAASApplication;
import io.bcaas.bean.WalletBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.gson.RequestJson;
import io.bcaas.http.tcp.TCPThread;
import io.bcaas.listener.TCPRequestListener;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.vo.WalletVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/17
 * 定义一个服务器来启动与服务器的TCP连线
 */
public class TCPService extends Service {
    private String TAG = TCPService.class.getSimpleName();
    private final IBinder tcpBinder = new TCPBinder();

    /*开启连线
     * 1：通过TCP传给服务器的数据不需要加密
     * 2:开始socket连线之后，然后Http请求该接口，通知服务器可以下发数据了。
     * */
    public void startTcp(TCPRequestListener tcpRequestListener) {
        WalletBean walletBean = BCAASApplication.getWalletBean();
        if (walletBean == null) {
            return;
        }
        WalletVO walletVO = new WalletVO(
                walletBean.getAddress(),
                BCAASApplication.getBlockService(),
                BCAASApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN));
        RequestJson requestJson = new RequestJson(walletVO);
        String json = GsonTool.string(requestJson);
        TCPThread tcpThread = new TCPThread(json + "\n", tcpRequestListener);
        tcpThread.start();
    }

    public class TCPBinder extends Binder {
        public TCPService getService() {
            // Return this instance of LocalService so clients can call public methods
            return TCPService.this;
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return tcpBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        TCPThread.kill(true);
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
