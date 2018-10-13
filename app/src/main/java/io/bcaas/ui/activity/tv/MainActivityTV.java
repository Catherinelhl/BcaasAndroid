package io.bcaas.ui.activity.tv;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.squareup.otto.Subscribe;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.bcaas.BuildConfig;
import io.bcaas.R;
import io.bcaas.base.BCAASApplication;
import io.bcaas.base.BaseTVActivity;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.event.BindServiceEvent;
import io.bcaas.event.LogoutEvent;
import io.bcaas.event.ModifyRepresentativeResultEvent;
import io.bcaas.event.NetStateChangeEvent;
import io.bcaas.event.RefreshRepresentativeEvent;
import io.bcaas.event.RefreshSendStatusEvent;
import io.bcaas.event.RefreshTCPConnectIPEvent;
import io.bcaas.event.RefreshTransactionRecordEvent;
import io.bcaas.event.RefreshWalletBalanceEvent;
import io.bcaas.event.VerifyEvent;
import io.bcaas.gson.ResponseJson;
import io.bcaas.http.tcp.TCPThread;
import io.bcaas.listener.TCPRequestListener;
import io.bcaas.presenter.MainPresenterImp;
import io.bcaas.presenter.SettingPresenterImp;
import io.bcaas.service.TCPService;
import io.bcaas.tools.ActivityTool;
import io.bcaas.tools.DateFormatTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.StringTool;
import io.bcaas.ui.activity.MainActivity;
import io.bcaas.ui.contracts.MainContracts;
import io.bcaas.ui.contracts.SettingContract;
import io.bcaas.view.dialog.TVBcaasDialog;
import io.bcaas.view.textview.TVTextView;
import io.bcaas.view.tv.FlyBroadLayout;
import io.bcaas.view.tv.MainUpLayout;
import io.reactivex.disposables.Disposable;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/20
 * <p>
 * TV版首页
 * <p>
 * <p>
 * 1：進行幣種驗證，然後開啟「TCP」連接開始後台服務
 */
public class MainActivityTV extends BaseTVActivity implements MainContracts.View, SettingContract.View {

    private String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.block_base_mainup)
    FlyBroadLayout blockBaseMainup;
    @BindView(R.id.block_base_content)
    MainUpLayout blockBaseContent;
    @BindView(R.id.tv_home)
    TextView tvHome;
    @BindView(R.id.ll_home)
    LinearLayout llHome;
    @BindView(R.id.tv_send)
    TextView tvSend;
    @BindView(R.id.ll_send)
    LinearLayout llSend;
    @BindView(R.id.tv_setting)
    TextView tvSetting;
    @BindView(R.id.ll_setting)
    LinearLayout llSetting;
    @BindView(R.id.tv_current_time)
    TextView tvCurrentTime;
    @BindView(R.id.tv_login)
    TVTextView tvLogin;
    @BindView(R.id.tv_change_server)
    TVTextView tvChangeServer;
    @BindView(R.id.ib_logout)
    ImageButton ibLogout;
    private MainContracts.Presenter presenter;
    protected SettingContract.Presenter settingPresenter;

    private TCPService tcpService;

    private String from;//记录是从那里跳入到当前的首页
    private boolean logout;//存储当前是否登出
    //得到当前连接service的Intent
    private Intent tcpServiceIntent;

    @Override
    public boolean full() {
        return false;
    }

    @Override
    public int getContentView() {
        return R.layout.tv_activity_main;
    }

    @Override
    public void getArgs(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        from = bundle.getString(Constants.KeyMaps.From);
    }

    @Override
    public void initViews() {
        //將當前的activity加入到管理之中，方便「切換語言」的時候進行移除操作
        ActivityTool.getInstance().addActivity(this);
        settingPresenter = new SettingPresenterImp(this);
        presenter = new MainPresenterImp(this);
        initData();
        //如果當前是從「切換語言」進入且是登錄的狀態，那麼應該重新連接TCP
        isFromLanguageSwitch();
    }

    /**
     * 檢查當前是否是「切換語言」跳轉進入
     */
    private void isFromLanguageSwitch() {
        if (StringTool.equals(from, Constants.ValueMaps.FROM_LANGUAGE_SWITCH)
                && BCAASApplication.isIsLogin()) {
            //如果當前是切換語言，那麼需要直接重新綁定服務，連接TCP
            bindTcpService();
        }
    }

    private void initData() {
        tvCurrentTime.setText(DateFormatTool.getCurrentTime());
    }

    @Override
    public void initListener() {
        blockBaseContent.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                blockBaseMainup.setFocusView(newFocus, oldFocus, 1.2f);
            }
        });

        Disposable subscribeLogin = RxView.clicks(tvLogin)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    intentToActivity(LoginActivityTV.class);
                });
        Disposable subscribeChangeServer = RxView.clicks(tvChangeServer)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    intentToActivity(ChangeServerActivityTV.class);
                });
        Disposable subscribeLogout = RxView.clicks(ibLogout)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    //顯示退出當前的彈框
                    showTVBcaasDialog(getResources().getString(R.string.warning),
                            getResources().getString(R.string.cancel),
                            getResources().getString(R.string.confirm),
                            getResources().getString(R.string.confirm_logout), new TVBcaasDialog.ConfirmClickListener() {
                                @Override
                                public void sure() {
                                    if (checkActivityState()) {
                                        logoutTV();
                                        isShowLogout(false);
                                    }
                                    settingPresenter.logout();
                                }

                                @Override
                                public void cancel() {

                                }
                            });
                });
        Disposable subscribeHome = RxView.clicks(llHome)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (!BCAASApplication.isIsLogin()) {
                        showToast(getResources().getString(R.string.please_log_in_first));
                    } else {
                        intentToActivity(HomeActivityTV.class);
                    }
                });
        Disposable subscribeSend = RxView.clicks(llSend)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (!BCAASApplication.isIsLogin()) {
                        showToast(getResources().getString(R.string.please_log_in_first));
                    } else {
                        intentToActivity(SendActivityTV.class);
                    }
                });
        Disposable subscribeSetting = RxView.clicks(llSetting)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (!BCAASApplication.isIsLogin()) {
                        showToast(getResources().getString(R.string.please_log_in_first));
                    } else {
                        intentToActivity(SettingActivityTV.class);
                    }
                });

    }

    @Override
    public void getAndroidVersionInfoFailure() {

    }

    //监听Tcp数据返回
    TCPRequestListener tcpRequestListener = new TCPRequestListener() {
        @Override
        public void httpToRequestReceiverBlock() {
            presenter.startToGetWalletWaitingToReceiveBlockLoop();
        }

        @Override
        public void sendTransactionFailure(String message) {
            handler.post(() -> {
                LogTool.d(TAG, message);
                hideLoadingDialog();
                showToast(getResources().getString(R.string.transaction_has_failure));
                OttoTool.getInstance().post(new RefreshSendStatusEvent(false));
            });
        }

        @Override
        public void sendTransactionSuccess(String message) {
            handler.post(() -> {
                hideLoadingDialog();
                showToast(getResources().getString(R.string.transaction_has_successfully));
                OttoTool.getInstance().post(new RefreshSendStatusEvent(true));
            });
        }

        @Override
        public void showWalletBalance(String walletBalance) {
            String balance = walletBalance;
            LogTool.d(TAG, MessageConstants.BALANCE + balance);
            BCAASApplication.setWalletBalance(balance);
            runOnUiThread(() -> OttoTool.getInstance().post(new RefreshWalletBalanceEvent()));
        }

        @Override
        public void getPreviousModifyRepresentative(String representative) {
            LogTool.d(TAG, "getPreviousModifyRepresentative");
            handler.post(() -> OttoTool.getInstance().post(new RefreshRepresentativeEvent(representative)));
        }

        @Override
        public void modifyRepresentativeResult(String currentStatus, boolean isSuccess, int code) {
            handler.post(() -> OttoTool.getInstance().post(new ModifyRepresentativeResultEvent(currentStatus, isSuccess, code)));
        }

        @Override
        public void reLogin() {
            LogTool.d(TAG, logout);
            if (!logout) {
                logout = true;
                handler.post(() -> {
                    isShowLogout(false);
                    //判斷當前頁面是否就是這個頁面，如果是，直接彈出對話框，如果不是，發送訂閱，彈出登出操作
                    if (ActivityTool.isTopActivity(TAG, BCAASApplication.context())) {
                        showTVLogoutSingleDialog();
                    } else {
                        OttoTool.getInstance().post(new LogoutEvent());
                    }
                });
            }
        }

        @Override
        public void noEnoughBalance() {
            handler.post(() -> {
                showToast(getResources().getString(R.string.insufficient_balance));
                hideLoading();
            });
        }

        @Override
        public void amountException() {
            hideLoading();
            LogTool.d(TAG, MessageConstants.AMOUNT_EXCEPTION);
        }

        @Override
        public void tcpResponseDataError(String responseDataError) {
            handler.post(() -> {
                hideLoading();
                showToast(responseDataError);
            });

        }

        @Override
        public void getDataException(String message) {
            LogTool.d(TAG, MessageConstants.GET_TCP_DATA_EXCEPTION + message);

        }

        @Override
        public void refreshTransactionRecord() {
            handler.post(() -> {
                //刷新當前的交易紀錄
                OttoTool.getInstance().post(new RefreshTransactionRecordEvent());
            });

        }

        @Override
        public void refreshTCPConnectIP(String ip) {
            if (BuildConfig.DEBUG) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        OttoTool.getInstance().post(new RefreshTCPConnectIPEvent(ip));
                    }
                });
            }
        }

        @Override
        public void resetSuccess() {
            bindTcpService();
        }

        @Override
        public void needUnbindService() {
            if (tcpService != null) {
                tcpService.onUnbind(tcpServiceIntent);
            }
        }
    };

    /**
     * 每次选择blockService之后，进行余额以及AN信息的拿取
     * 且要暫停當前socket的請求
     */
    @Subscribe
    public void verifyEvent(VerifyEvent verifyEvent) {
        checkVerify();
    }

    private void checkVerify() {
        if (presenter != null) {
            TCPThread.closeSocket(true,"checkVerify");
            presenter.checkVerify(false);
        }
    }


    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection tcpConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            TCPService.TCPBinder binder = (TCPService.TCPBinder) service;
            tcpService = binder.getService();
            tcpService.startTcp(tcpRequestListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            //断开连接，如果是异常断开，应该让其重新连接上
            LogTool.d(TAG, MessageConstants.SERVICE_DISCONNECTED);
        }
    };


    // 关闭当前页面，中断所有请求
    private void finishActivity() {
        if (tcpService != null) {
            tcpService.onUnbind(tcpServiceIntent);
        }
        // 置空数据
        BCAASApplication.resetWalletBalance();
        presenter.unSubscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideTVBcaasDialog();
        finishActivity();
    }


    /**
     * 更新版本
     *
     * @param forceUpgrade 是否强制更新
     * @param appStoreUrl  APP store 下载地址
     * @param updateUrl    程序内部下载地址
     */
    @Override
    public void updateVersion(boolean forceUpgrade, String appStoreUrl, String updateUrl) {
        updateAndroidAPKURL = updateUrl;
        if (forceUpgrade) {
            showTVBcaasSingleDialog(getResources().getString(R.string.app_need_update), () -> {
                // 开始后台执行下载应用，或许直接跳转应用商店
                intentGooglePlay(appStoreUrl);
            });
        } else {
            showTVBcaasDialog(getResources().getString(R.string.app_need_update), new TVBcaasDialog.ConfirmClickListener() {
                @Override
                public void sure() {
                    // 开始后台执行下载应用，或许直接跳转应用商店
                    intentGooglePlay(appStoreUrl);
                }

                @Override
                public void cancel() {

                }
            });
        }
    }

    /**
     * 跳转google商店
     *
     * @param appStoreUrl
     */
    private void intentGooglePlay(String appStoreUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //跳转到应用市场
        intent.setData(Uri.parse(MessageConstants.GOOGLE_PLAY_MARKET + getPackageName()));
        //存在手机里没安装应用市场的情况，跳转会包异常，做一个接收判断
        if (intent.resolveActivity(getPackageManager()) != null) { //可以接收
            startActivity(intent);
        } else {
            //没有应用市场，我们通过浏览器跳转到Google Play
            intent.setData(Uri.parse(appStoreUrl));
            //这里存在一个极端情况就是有些用户浏览器也没有，再判断一次
            if (intent.resolveActivity(getPackageManager()) != null) { //有浏览器
                startActivity(intent);
            } else {
                startAppSYNCDownload();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LogTool.d(TAG, MessageConstants.ONBACKPRESSED);
        BCAASApplication.setKeepHttpRequest(false);
        ActivityTool.getInstance().exit();
        finishActivity();

    }

    @Override
    public void resetAuthNodeSuccess() {
        LogTool.d(TAG, MessageConstants.RESET_SAN_SUCCESS);
        bindTcpService();
    }

    /*绑定当前TCP服务*/
    private void bindTcpService() {
        if (!checkActivityState()) {
            return;
        }
        LogTool.d(TAG, MessageConstants.BIND_TCP_SERVICE);
        if (tcpService != null) {
            tcpService.startTcp(tcpRequestListener);
        } else {
            //绑定当前服务
            tcpServiceIntent = new Intent(this, TCPService.class);
            bindService(tcpServiceIntent, tcpConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Subscribe
    public void bindTCPServiceEvent(BindServiceEvent bindServiceEvent) {
        if (bindServiceEvent != null) {
            if (tcpService != null) {
                tcpService.startTcp(tcpRequestListener);
            } else {
                bindTcpService();
            }
        }
    }

    @Override
    public void noData() {
        showToast(getResources().getString(R.string.account_data_error));
    }

    @Override
    public void responseDataError() {
        showToast(getResources().getString(R.string.data_acquisition_error));
    }

    @Subscribe
    public void netStateChange(NetStateChangeEvent netStateChangeEvent) {
        if (netStateChangeEvent != null) {
            if (!netStateChangeEvent.isConnect()) {
                showToast(getResources().getString(R.string.network_not_reachable));
            }
            BCAASApplication.setRealNet(netStateChangeEvent.isConnect());

        }
    }

    @Override
    public void verifySuccess(boolean isReset) {
        super.verifySuccess(isReset);
        LogTool.d(TAG, MessageConstants.VERIFY_SUCCESS + isReset);
        if (!isReset) {
            bindTcpService();
        }

    }

    @Override
    public void showLoading() {
        if (!checkActivityState()) {
            return;
        }
        showLoadingDialog(getResources().getColor(R.color.orange_FC9003));
    }

    @Override
    public void hideLoading() {
        if (!checkActivityState()) {
            return;
        }
        hideLoadingDialog();
    }

    @Override
    public void logoutSuccess() {
        BCAASApplication.setIsLogin(false);
        LogTool.d(TAG, MessageConstants.LOGOUT_SUCCESSFULLY);
    }

    @Override
    public void logoutFailure(String message) {
        LogTool.d(TAG, message);
        logoutFailure();
    }

    @Override
    public void logoutFailure() {
        LogTool.d(TAG, getString(R.string.logout_failure));

    }

    private void isShowLogout(boolean show) {
        tvLogin.setVisibility(show ? View.GONE : View.VISIBLE);
        ibLogout.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void accountError() {
        showToast(getResources().getString(R.string.account_data_error));
    }

    private void logoutTV() {
        BCAASApplication.setKeepHttpRequest(false);
        TCPThread.closeSocket(true,"logoutTV");
        BCAASApplication.clearAccessToken();
        intentToActivity(LoginActivityTV.class, false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            // 當前點擊菜單鍵
            if (BuildConfig.DEBUG) {
                tvChangeServer.setVisibility(tvChangeServer.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        } else if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_BACK) {
            //當前點擊首頁按鍵\返回按鍵，退出當前程序
            finishActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        LogTool.d(TAG, MessageConstants.ONRESUME + from);
        // 如果當前是從啟動頁進入，就需要重新獲取版本信息
        if (StringTool.equals(from, Constants.ValueMaps.FROM_BRAND)) {
            showLoadingDialog(getResources().getColor(R.color.orange_FC9003));
            presenter.getAndroidVersionInfo();
        }
        isShowLogout(BCAASApplication.isIsLogin());

        super.onResume();
    }

    @Override
    public void httpExceptionStatus(ResponseJson responseJson) {
        LogTool.d(TAG, MessageConstants.HTTPEXCEPTIONSTATUS);
        if (responseJson == null) {
            return;
        }
        int code = responseJson.getCode();
        if (code == MessageConstants.CODE_3006
                || code == MessageConstants.CODE_3008
                || code == MessageConstants.CODE_2029) {
            isShowLogout(false);
            //判斷當前頁面是否就是這個頁面，如果是，直接彈出對話框，如果不是，發送訂閱，彈出登出操作
            if (ActivityTool.isTopActivity(TAG, BCAASApplication.context())) {
                showTVLogoutSingleDialog();
            } else {
                OttoTool.getInstance().post(new LogoutEvent());
            }
        } else {
            super.httpExceptionStatus(responseJson);
        }
    }
}
