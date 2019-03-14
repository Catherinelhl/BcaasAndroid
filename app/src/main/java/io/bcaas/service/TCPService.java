package io.bcaas.service;

import android.app.Service;
import android.content.Context;
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
import io.bcaas.tools.PreferenceTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.tools.language.LanguageTool;
import io.bcaas.vo.WalletVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/17
 * 服務：開啟一個存創建Socket連接，保持TCP數據連線的服務
 */
public class TCPService extends Service {
    private String TAG = TCPService.class.getSimpleName();
    private final IBinder tcpBinder = new TCPBinder();

    /*开启连线
     * 1：通过TCP传给服务器的数据不需要加密
     * 2:开始socket连线之后，然后Http请求该接口，通知服务器可以下发数据了。
     * */
    public void startTcp(TCPRequestListener tcpRequestListener) {
        String requestJson = getRequestJson();
        if (StringTool.notEmpty(requestJson)) {
           new TCPThread(requestJson, tcpRequestListener);
        }
    }

    private String getRequestJson() {
        WalletBean walletBean = BCAASApplication.getWalletBean();
        if (walletBean == null) {
            return null;
        }
        WalletVO walletVO = new WalletVO(
                walletBean.getAddress(),
                BCAASApplication.getBlockService(),
                PreferenceTool.getInstance().getString(Constants.Preference.ACCESS_TOKEN));
        RequestJson requestJson = new RequestJson(walletVO);
        return GsonTool.string(requestJson);
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
        LogTool.d(TAG, MessageConstants.LogInfo.SERVICE_TAG, MessageConstants.UNBIND_SERVICE);
        TCPThread.closeSocket(true, "onUnbind");
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


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageTool.setLocal(base));
    }

}
