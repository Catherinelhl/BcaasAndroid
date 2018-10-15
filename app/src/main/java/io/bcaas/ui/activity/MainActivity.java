package io.bcaas.ui.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.obt.qrcode.activity.CaptureActivity;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.bcaas.BuildConfig;
import io.bcaas.R;
import io.bcaas.adapter.FragmentAdapter;
import io.bcaas.base.BCAASApplication;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BaseFragment;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.event.BindServiceEvent;
import io.bcaas.event.ModifyRepresentativeResultEvent;
import io.bcaas.event.NetStateChangeEvent;
import io.bcaas.event.RefreshAddressEvent;
import io.bcaas.event.RefreshBlockServiceEvent;
import io.bcaas.event.RefreshRepresentativeEvent;
import io.bcaas.event.RefreshSendStatusEvent;
import io.bcaas.event.RefreshTransactionRecordEvent;
import io.bcaas.event.RefreshWalletBalanceEvent;
import io.bcaas.event.SendTransactionEvent;
import io.bcaas.event.VerifyEvent;
import io.bcaas.gson.ResponseJson;
import io.bcaas.http.tcp.TCPThread;
import io.bcaas.listener.TCPRequestListener;
import io.bcaas.presenter.MainPresenterImp;
import io.bcaas.presenter.SendConfirmationPresenterImp;
import io.bcaas.service.TCPService;
import io.bcaas.tools.ActivityTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.StringTool;
import io.bcaas.ui.contracts.MainContracts;
import io.bcaas.ui.contracts.SendConfirmationContract;
import io.bcaas.ui.fragment.MainFragment;
import io.bcaas.ui.fragment.ReceiveFragment;
import io.bcaas.ui.fragment.ScanFragment;
import io.bcaas.ui.fragment.SendFragment;
import io.bcaas.ui.fragment.SettingFragment;
import io.bcaas.view.BcaasRadioButton;
import io.bcaas.view.BcaasViewpager;
import io.bcaas.view.dialog.BcaasDialog;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * 进入当前钱包首页
 */
public class MainActivity extends BaseActivity
        implements MainContracts.View, SendConfirmationContract.View {
    private String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rb_home)
    BcaasRadioButton rbHome;
    @BindView(R.id.rb_receive)
    BcaasRadioButton rbReceive;
    @BindView(R.id.rb_scan)
    BcaasRadioButton rbScan;
    @BindView(R.id.rb_send)
    BcaasRadioButton rbSend;
    @BindView(R.id.rb_setting)
    BcaasRadioButton rbSetting;
    @BindView(R.id.bvp)
    BcaasViewpager bvp;
    @BindView(R.id.tv_toast)
    TextView tvToast;

    private List<BaseFragment> fragmentList;
    private FragmentAdapter mainPagerAdapter;
    //记录是从那里跳入到当前的首页
    private String from;
    private MainContracts.Presenter presenter;
    private SendConfirmationContract.Presenter sendPresenter;
    //存储当前是否登出
    private boolean logout;
    private TCPService tcpService;
    //得到当前连接service的Intent
    private Intent tcpServiceIntent;


    @Override
    public boolean full() {
        return false;
    }

    @Override
    public void getArgs(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        from = bundle.getString(Constants.KeyMaps.From);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    public void initViews() {
        fragmentList = new ArrayList<>();
        logout = false;
        BCAASApplication.setKeepHttpRequest(true);
        //將當前的activity加入到管理之中，方便「切換語言」的時候進行移除操作
        ActivityTool.getInstance().addActivity(this);
        presenter = new MainPresenterImp(this);
        sendPresenter = new SendConfirmationPresenterImp(this);
        tvTitle.setText(getResources().getString(R.string.home));
        initFragment();
        setAdapter();
        isFromLanguageSwitch();
        //绑定下载服务
        bindDownloadService();
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

    private void setAdapter() {
        mainPagerAdapter = new FragmentAdapter(getSupportFragmentManager(), fragmentList);
        bvp.setOffscreenPageLimit(fragmentList.size());// 设置预加载Fragment个数
        bvp.setAdapter(mainPagerAdapter);
        bvp.setCurrentItem(0);// 设置当前显示标签页为第一页
        //获取第一个单选按钮，并设置其为选中状态
        rbHome.setChecked(true);
        bvp.setCanScroll(false);
    }

    @Override
    public void initListener() {
        rbHome.setOnClickListener(view -> switchTab(0));
        rbReceive.setOnClickListener(view -> switchTab(1));
        rbScan.setOnClickListener(view -> switchTab(2));
        rbSend.setOnClickListener(view -> switchTab(3));
        rbSetting.setOnClickListener(view -> switchTab(4));

    }

    //重置所有文本的选中状ra态
    private void resetRadioButton() {
        rbHome.setChecked(false);
        rbReceive.setChecked(false);
        rbScan.setChecked(false);
        rbSend.setChecked(false);
        rbSetting.setChecked(false);

    }

    //跳转打开相机进行扫描
    public void intentToCaptureActivity() {
        startActivityForResult(new Intent(this, CaptureActivity.class), Constants.KeyMaps.REQUEST_CODE_CAMERA_OK);
    }

    //切换当前底部栏的tab
    public void switchTab(int position) {
        if (!checkActivityState()) {
            return;
        }
        if (bvp == null) {
            return;
        }
        resetRadioButton();
        bvp.setCurrentItem(position);
        switch (position) {
            case 0:
                tvTitle.setText(getResources().getString(R.string.home));
                rbHome.setChecked(true);
                handler.sendEmptyMessageDelayed(Constants.UPDATE_BLOCK_SERVICE, Constants.ValueMaps.sleepTime200);
                handler.sendEmptyMessageDelayed(Constants.UPDATE_BLOCK_SERVICE, Constants.ValueMaps.sleepTime400);
                tvTitle.setText(getResources().getString(R.string.home));
                break;
            case 1:
                rbReceive.setChecked(true);
                bvp.setCurrentItem(1);
                tvTitle.setText(getResources().getString(R.string.receive));
                break;
            case 2:
                intentToCaptureActivity();
                rbScan.setChecked(true);
                tvTitle.setText(getResources().getString(R.string.scan));
                handler.sendEmptyMessageDelayed(Constants.SWITCH_TAB, Constants.ValueMaps.sleepTime500);
                break;
            case 3:
                rbSend.setChecked(true);
                /*如果当前点击的是「发送页面」，应该通知其更新余额显示*/
                handler.sendEmptyMessageDelayed(Constants.UPDATE_BLOCK_SERVICE, Constants.ValueMaps.sleepTime200);
                handler.sendEmptyMessageDelayed(Constants.UPDATE_BLOCK_SERVICE, Constants.ValueMaps.sleepTime400);
                handler.sendEmptyMessageDelayed(Constants.UPDATE_WALLET_BALANCE, Constants.ValueMaps.sleepTime200);
                handler.sendEmptyMessageDelayed(Constants.UPDATE_WALLET_BALANCE, Constants.ValueMaps.sleepTime400);
                tvTitle.setText(getResources().getString(R.string.send));
                break;
            case 4:
                rbSetting.setChecked(true);
                tvTitle.setText(getResources().getString(R.string.settings));
                break;

        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case Constants.RESULT_CODE:
                    String result = BCAASApplication.getDestinationWallet();
                    OttoTool.getInstance().post(new RefreshAddressEvent(result));
                    break;
                case Constants.UPDATE_WALLET_BALANCE:
                    updateWalletBalance();
                    break;
                /*更新区块*/
                case Constants.UPDATE_BLOCK_SERVICE:
                    updateBlockService();
                    break;
                case Constants.SWITCH_TAB:
                    switchTab(0);
                    break;
            }
        }
    };

    private void initFragment() {
        //tab 和 fragment 联动
        MainFragment mainFragment = MainFragment.newInstance(from);
        fragmentList.add(0, mainFragment);
        ReceiveFragment receiveFragment = ReceiveFragment.newInstance();
        fragmentList.add(1, receiveFragment);
        ScanFragment scanFragment = ScanFragment.newInstance();
        fragmentList.add(2, scanFragment);
        SendFragment sendFragment = SendFragment.newInstance();
        fragmentList.add(3, sendFragment);
        SettingFragment settingFragment = SettingFragment.newInstance();
        fragmentList.add(4, settingFragment);
    }

    /*发出更新余额的通知*/
    private void updateWalletBalance() {
        OttoTool.getInstance().post(new RefreshWalletBalanceEvent());
    }

    /*发出更新区块服务的通知*/
    private void updateBlockService() {
        OttoTool.getInstance().post(new RefreshBlockServiceEvent());
    }

    @Override
    public void verifySuccess(String from) {
        LogTool.d(TAG, MessageConstants.VERIFY_SUCCESS + from);
        //1：验证当前from是来自于何处，然后执行不同的操作
        if (StringTool.notEmpty(from)) {
            switch (from) {
                case Constants.Verify.RESET:
                    //verify接口返回3003，重置SAN操作
                    bindTcpService();
                    break;
                case Constants.Verify.SEND_TRANSACTION:
                    //发送交易之前的验证
                    if (TCPThread.isKeepAlive()) {
                        //如果当前TCP还活着，那么就直接开始请求余额
                        sendPresenter.getLatestBlockAndBalance();
                    } else {
                        bindTcpService();
                    }
                    break;
                case Constants.Verify.SWITCH_BLOCK_SERVICE:
                    //切换币种的区块verify
                    bindTcpService();
                    break;
                case Constants.Verify.VERIFY_FAILURE:
                    //verify接口请求失败的重试
                    bindTcpService();
                    break;

            }
        }
    }

    @Override
    public void httpExceptionStatus(ResponseJson responseJson) {
        if (responseJson == null) {
            return;
        }
        int code = responseJson.getCode();
        if (code == MessageConstants.CODE_3006
                || code == MessageConstants.CODE_3008
                || code == MessageConstants.CODE_2029) {
            showLogoutSingleDialog();
        } else {
            super.httpExceptionStatus(responseJson);
        }
    }

    @Override
    public void resetAuthNodeFailure(String message, String from) {
        super.resetAuthNodeFailure(message, from);
        BCAASApplication.setIsTrading(false);
        OttoTool.getInstance().post(new RefreshSendStatusEvent(false));
    }

    @Override
    public void resetAuthNodeSuccess(String from) {
        LogTool.d(TAG, MessageConstants.RESET_SAN_SUCCESS + from);
        bindTcpService();
    }

    @Subscribe
    public void bindTCPServiceEvent(BindServiceEvent bindServiceEvent) {
        if (bindServiceEvent != null) {
            if (tcpService != null) {
                tcpService.startTcp(tcpRequestListener);
            }
        }
    }


    /*绑定当前TCP服务*/
    private void bindTcpService() {
        LogTool.d(TAG, MessageConstants.BIND_TCP_SERVICE);
        if (tcpService != null) {
            tcpService.startTcp(tcpRequestListener);
        } else {
            //绑定当前服务
            tcpServiceIntent = new Intent(this, TCPService.class);
            bindService(tcpServiceIntent, tcpConnection, Context.BIND_AUTO_CREATE);
        }
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
                BCAASApplication.setIsTrading(false);
                showToast(getResources().getString(R.string.transaction_has_failure));
                OttoTool.getInstance().post(new RefreshSendStatusEvent(false));
            });
        }

        @Override
        public void sendTransactionSuccess(String message) {
            handler.post(() -> {
                hideLoadingDialog();
                switchTab(0);
                //提示「Send」成功
                BCAASApplication.setIsTrading(false);
                showToast(getResources().getString(R.string.transaction_has_successfully));
                //發出訂閱，刷新「Send」結果返回之後需要做出改變的界面
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
            LogTool.d(TAG, MessageConstants.GET_PREVIOUS_MODIFY_REPRESENTATIVE);
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
                handler.post(() -> showLogoutSingleDialog());
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
            handler.post(() -> OttoTool.getInstance().post(new RefreshTransactionRecordEvent()));

        }

        @Override
        public void refreshTCPConnectIP(String ip) {
            if (BuildConfig.DEBUG) {
                handler.post(() -> {
                    if (tvToast != null) {
                        tvToast.setVisibility(View.VISIBLE);
                        tvToast.setText(ip);
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


    @Override
    public void noData() {
        showToast(getResources().getString(R.string.account_data_error));

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogTool.d(TAG, MessageConstants.DESTROY);
        finishActivity();

    }

    @Override
    public void verifyFailure(String from) {
        LogTool.d(TAG, MessageConstants.VERIFY_FAILURE + from);
        //验证失败，需要重新拿去AN的信息
        showToast(getResources().getString(R.string.data_acquisition_error));
        if (StringTool.notEmpty(from)) {
            switch (from) {
                case Constants.Verify.SEND_TRANSACTION:
                    BCAASApplication.setIsTrading(false);
                    OttoTool.getInstance().post(new RefreshSendStatusEvent(false));
                    break;
            }
        }
    }

    @Override
    public void failure(String message, String from) {
        super.failure(message, from);
        verifyFailure(from);
    }

    @Override
    public void onBackPressed() {
        ActivityTool.getInstance().exit();
        finishActivity();
        super.onBackPressed();

    }

    // 关闭当前页面，中断所有请求
    private void finishActivity() {
        if (presenter != null) {
            presenter.unSubscribe();
        }
        if (tcpService != null) {
            tcpService.onUnbind(tcpServiceIntent);
        }
        BCAASApplication.setKeepHttpRequest(false);
        // 置空当前余额
        BCAASApplication.resetWalletBalance();
    }

    /**
     * 每次选择blockService之后，进行余额以及AN信息的拿取
     * 且要暫停當前socket的請求
     */
    public void verify() {
        if (presenter != null) {
            TCPThread.closeSocket(false, "verify");
            presenter.checkVerify(Constants.Verify.SWITCH_BLOCK_SERVICE);
        }
    }

    /*獲得照相機權限*/
    private void getCameraPermission() {
        if (Build.VERSION.SDK_INT > 22) { //这个说明系统版本在6.0之下，不需要动态获取权限。
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //先判断有没有权限 ，没有就在这里进行权限的申请,否则说明已经获取到摄像头权限了 想干嘛干嘛
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.CAMERA}, Constants.KeyMaps.REQUEST_CODE_CAMERA_OK);

            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.KeyMaps.REQUEST_CODE_CAMERA_OK:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //这里已经获取到了摄像头的权限，想干嘛干嘛了可以
                } else {
                    //这里是拒绝给APP摄像头权限，给个提示什么的说明一下都可以。
                    showToast(getString(R.string.to_setting_grant_permission));
                }
                break;
        }
    }

    @Override
    public void passwordError() {
        hideLoading();
        BCAASApplication.setIsTrading(false);
        showToast(getResources().getString(R.string.password_error));

    }

    @Override
    public void responseDataError() {
        hideLoading();
        showToast(getResources().getString(R.string.data_acquisition_error));

    }

    /*收到订阅，然后进行区块验证*/
    @Subscribe
    public void verifyEvent(VerifyEvent verifyEvent) {
        updateBlockService();
        TCPThread.setActiveDisconnect(true);
        verify();
    }

    @Subscribe
    public void netStateChange(NetStateChangeEvent netStateChangeEvent) {
        if (netStateChangeEvent != null) {
            if (netStateChangeEvent.isConnect()) {
                if (TCPThread.allowConnect()) {
                    if (presenter != null) {
                        presenter.onResetAuthNodeInfo(Constants.Reset.NET_CHANGE);
                    }
                }
            } else {
                if (presenter != null) {
                    if (tcpService != null) {
                        tcpService.onUnbind(tcpServiceIntent);
                    }
                }
                showToast(getResources().getString(R.string.network_not_reachable));
            }
        }
    }

    @Override
    public void noNetWork() {
        showToast(getResources().getString(R.string.network_not_reachable));

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
            showBcaasSingleDialog(getResources().getString(R.string.app_need_update), () -> {
                // 开始后台执行下载应用，或许直接跳转应用商店
                intentToGooglePlay(appStoreUrl);
            });
        } else {
            showBcaasDialog(getResources().getString(R.string.app_need_update), new BcaasDialog.ConfirmClickListener() {
                @Override
                public void sure() {
                    // 开始后台执行下载应用，或许直接跳转应用商店
                    intentToGooglePlay(appStoreUrl);
                }

                @Override
                public void cancel() {

                }
            });
        }
    }

    @Override
    public void getAndroidVersionInfoFailure() {

    }

    /**
     * 跳转google商店
     *
     * @param appStoreUrl
     */
    private void intentToGooglePlay(String appStoreUrl) {
        LogTool.d(TAG, MessageConstants.INTENT_GOOGLE_PLAY + appStoreUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(MessageConstants.GOOGLE_PLAY_MARKET + getPackageName()));
        LogTool.d(TAG, Uri.parse(MessageConstants.GOOGLE_PLAY_MARKET + getPackageName()));
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
                //否则跳转应用内下载
                startAppSYNCDownload();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_HOME == keyCode) {
            LogTool.d(TAG, keyCode);
            finishActivity();
        }
        return super.onKeyDown(keyCode, event);
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

    @Override
    protected void onResume() {
        // 如果是從登錄進來，就需要拿去Android版本信息
        if (from.equals(Constants.ValueMaps.FROM_LOGIN)) {
            showLoadingDialog();
            presenter.getAndroidVersionInfo();
            getCameraPermission();
        }
        super.onResume();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }

            if (requestCode == Constants.KeyMaps.REQUEST_CODE_SEND_CONFIRM_ACTIVITY) {
                //判断当前是发送页面进行返回的
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    String result = bundle.getString(Constants.KeyMaps.ACTIVITY_STATUS);
                    switch (result) {
                        case Constants.ValueMaps.ACTIVITY_STATUS_DONE:
                            //上个页面完成，返回到首页
                            switchTab(0);
                            BCAASApplication.setIsTrading(false);
                            break;
                        case Constants.ValueMaps.ACTIVITY_STATUS_TODO:
                            //当前没有交易正在发送
                            BCAASApplication.setIsTrading(false);
                            break;
                        case Constants.ValueMaps.ACTIVITY_STATUS_TRADING:
                            //上个页面正在交易，锁住当前fragment的状态
                            BCAASApplication.setIsTrading(true);
                            break;
                    }
                }
            } else if (requestCode == Constants.KeyMaps.REQUEST_CODE_CAMERA_OK) {
                // 如果当前是照相机扫描回来
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    String result = bundle.getString(Constants.RESULT);
                    BCAASApplication.setDestinationWallet(result);
                    switchTab(3);//扫描成功，然后将当前扫描数据存储，然后跳转到发送页面
                    handler.sendEmptyMessageDelayed(Constants.RESULT_CODE, Constants.ValueMaps.sleepTime800);

                }
            }

        }
    }

    @Subscribe
    public void sendTransactionEvent(SendTransactionEvent sendTransactionEvent) {
        if (sendTransactionEvent != null) {
            String status = sendTransactionEvent.getStatus();
            switch (status) {
                case Constants.Transaction.SEND:
                    //接收到当前点击「发送」的动作
                    String password = sendTransactionEvent.getPassword();
                    sendPresenter.sendTransaction(password);
                    break;
            }
        }
    }

    @Override
    public void lockView(boolean lock) {
        LogTool.d(TAG, "lockView：" + lock);
    }

    @Override
    public void httpGetLastestBlockAndBalanceFailure() {
        super.httpGetLastestBlockAndBalanceFailure();
    }

    @Override
    public void httpGetLastestBlockAndBalanceSuccess() {
        super.httpGetLastestBlockAndBalanceSuccess();
    }
}
