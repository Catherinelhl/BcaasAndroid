package io.bcaas.ui.activity.tv;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import butterknife.BindView;
import io.bcaas.BuildConfig;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BcaasApplication;
import io.bcaas.bean.WalletBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.event.BindServiceEvent;
import io.bcaas.event.ModifyRepresentativeResultEvent;
import io.bcaas.event.NetStateChangeEvent;
import io.bcaas.event.RefreshRepresentativeEvent;
import io.bcaas.event.RefreshSendStatusEvent;
import io.bcaas.event.RefreshTransactionRecordEvent;
import io.bcaas.event.RefreshWalletBalanceEvent;
import io.bcaas.event.VerifyEvent;
import io.bcaas.http.tcp.TCPThread;
import io.bcaas.listener.TCPRequestListener;
import io.bcaas.presenter.LoginPresenterImp;
import io.bcaas.presenter.MainPresenterImp;
import io.bcaas.service.TCPService;
import io.bcaas.tools.ActivityTool;
import io.bcaas.tools.DateFormatTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.wallet.WalletDBTool;
import io.bcaas.ui.activity.ChangeServerActivity;
import io.bcaas.ui.activity.MainActivity;
import io.bcaas.ui.contracts.LoginContracts;
import io.bcaas.ui.contracts.MainContracts;
import io.bcaas.view.dialog.BcaasDialog;
import io.bcaas.view.tv.FlyBroadLayout;
import io.bcaas.view.tv.MainUpLayout;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/20
 * <p>
 * TV版首页
 * <p>
 * <p>
 * 1：進行幣種驗證，然後開啟「TCP」連接開始後台服務
 */
public class MainActivityTV extends BaseActivity implements MainContracts.View {

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
    @BindView(R.id.btn_login)
    Button btnLogin;
    private MainContracts.Presenter presenter;

    private TCPService tcpService;

    private boolean isLogin;


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

    }

    @Override
    public void initViews() {
        presenter = new MainPresenterImp(this);
        //1:檢查更新
        presenter.checkUpdate();
        initData();
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
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentToActivity(LoginActivityTV.class);
            }
        });
        llHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLogin) {
                    showToast(getResources().getString(R.string.please_log_in_first));
                }
                intentToActivity(HomeActivityTV.class);
            }
        });

        llSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLogin) {
                    showToast(getResources().getString(R.string.please_log_in_first));
                }
                intentToActivity(SendActivityTV.class);
            }
        });
        llSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLogin) {
                    showToast(getResources().getString(R.string.please_log_in_first));
                }
                intentToActivity(SettingActivityTV.class);
            }
        });
    }

    @Override
    public void checkUpdateFailure() {

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
            BcaasApplication.setWalletBalance(balance);
            runOnUiThread(() -> OttoTool.getInstance().post(new RefreshWalletBalanceEvent()));
        }

        @Override
        public void stopToHttpToRequestReceiverBlock() {
            presenter.removeGetWalletWaitingToReceiveBlockRunnable();
        }

        @Override
        public void toModifyRepresentative(String representative) {
            LogTool.d(TAG, "toModifyRepresentative");
            handler.post(() -> OttoTool.getInstance().post(new RefreshRepresentativeEvent(representative)));
        }

        @Override
        public void modifyRepresentativeResult(String currentStatus, boolean isSuccess, int code) {
            handler.post(() -> OttoTool.getInstance().post(new ModifyRepresentativeResultEvent(currentStatus, isSuccess, code)));
        }

        @Override
        public void toLogin() {
        }

        @Override
        public void noEnoughBalance() {
            handler.post(() -> showToast(getResources().getString(R.string.insufficient_balance)));

        }

        @Override
        public void tcpResponseDataError(String nullWallet) {
            handler.post(() -> showToast(nullWallet));

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
            if (tcpService != null && tcpService.isRestricted()) {
                unbindService(tcpConnection);
            }
            presenter.stopTCP();
            presenter.checkVerify();
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
        presenter.unSubscribe();
        TCPThread.kill(true);
        // 置空数据
        BcaasApplication.resetWalletBalance();
        presenter.stopTCP();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogTool.d(TAG, MessageConstants.DESTROY);
        if (presenter != null) {
            presenter.unSubscribe();
        }
        finishActivity();
    }

    /**
     * 强制更新
     *
     * @param forceUpgrade
     */
    @Override
    public void updateVersion(boolean forceUpgrade) {
        if (forceUpgrade) {
            showBcaasSingleDialog(getResources().getString(R.string.app_need_update), () -> {
                // 开始后台执行下载应用，或许直接跳转应用商店
                intentGooglePlay();
            });
        } else {
            showBcaasDialog(getResources().getString(R.string.app_need_update), new BcaasDialog.ConfirmClickListener() {
                @Override
                public void sure() {
                    // 开始后台执行下载应用，或许直接跳转应用商店
                    intentGooglePlay();
                }

                @Override
                public void cancel() {

                }
            });
        }
    }

    /*跳转google商店*/
    private void intentGooglePlay() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //跳转到应用市场
        intent.setData(Uri.parse(MessageConstants.GOOGLE_PLAY_MARKET + getPackageName()));
        //存在手机里没安装应用市场的情况，跳转会包异常，做一个接收判断
        if (intent.resolveActivity(getPackageManager()) != null) { //可以接收
            startActivity(intent);
        } else {
            //没有应用市场，我们通过浏览器跳转到Google Play
            intent.setData(Uri.parse(MessageConstants.GOOGLE_PLAY_URI + getPackageName()));
            //这里存在一个极端情况就是有些用户浏览器也没有，再判断一次
            if (intent.resolveActivity(getPackageManager()) != null) { //有浏览器
                startActivity(intent);
            } else {
                //天哪，这还是智能手机吗？
                showToast(getString(R.string.install_failed));
            }
        }
    }

    @Override
    public void onBackPressed() {
        BcaasApplication.setKeepHttpRequest(false);
        ActivityTool.getInstance().exit();
        finishActivity();
        super.onBackPressed();

    }

    @Override
    public void resetAuthNodeSuccess() {
        bindTcpService();
    }

    /*绑定当前TCP服务*/
    private void bindTcpService() {
        LogTool.d(TAG, MessageConstants.BIND_TCP_SERVICE);
        if (tcpService != null) {
            tcpService.startTcp(tcpRequestListener);
        } else {
            //绑定当前服务
            Intent intent = new Intent(this, TCPService.class);
            bindService(intent, tcpConnection, Context.BIND_AUTO_CREATE);
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
            BcaasApplication.setRealNet(netStateChangeEvent.isConnect());

        }
    }

    @Override
    public void verifySuccess(boolean isReset) {
        super.verifySuccess(isReset);
        if (!isReset) {
            bindTcpService();
        }

    }

    @Override
    public void showLoading() {
        if (!checkActivityState()) {
            return;
        }
        showLoadingDialog();
    }

    @Override
    public void hideLoading() {
        if (!checkActivityState()) {
            return;
        }
        hideLoadingDialog();
    }
}
