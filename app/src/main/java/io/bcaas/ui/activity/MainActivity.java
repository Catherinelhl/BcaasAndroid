package io.bcaas.ui.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.*;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;

import com.obt.qrcode.activity.CaptureActivity;
import com.squareup.otto.Subscribe;

import io.bcaas.BuildConfig;
import io.bcaas.R;
import io.bcaas.adapter.FragmentAdapter;
import io.bcaas.base.BCAASApplication;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BaseFragment;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.event.*;
import io.bcaas.gson.ResponseJson;
import io.bcaas.http.requester.MasterRequester;
import io.bcaas.http.tcp.TCPThread;
import io.bcaas.listener.GetMyIpInfoListener;
import io.bcaas.listener.TCPRequestListener;
import io.bcaas.presenter.MainPresenterImp;
import io.bcaas.service.TCPService;
import io.bcaas.tools.*;
import io.bcaas.tools.gson.JsonTool;
import io.bcaas.ui.contracts.MainContracts;
import io.bcaas.ui.fragment.*;
import io.bcaas.view.BcaasRadioButton;
import io.bcaas.view.BcaasViewpager;
import io.bcaas.view.dialog.BcaasDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * Activity：进入當前Wallet首页
 */
public class MainActivity extends BaseActivity
        implements MainContracts.View {
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

    private List<BaseFragment> fragmentList;
    private FragmentAdapter mainPagerAdapter;
    //记录是从那里跳入到当前的首页
    private String from;
    private MainContracts.Presenter presenter;
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
        tvTitle.setText(getResources().getString(R.string.home));
        initFragment();
        setAdapter();
        isFromLanguageSwitch();
        //绑定下载服务
        bindDownloadService();
        activity = this;
        checkNotificationPermission();
        getCameraPermission();

    }

    /**
     * 檢查當前是否是「切換語言」跳轉進入
     */
    private void isFromLanguageSwitch() {
        if (StringTool.equals(from, Constants.ValueMaps.FROM_LANGUAGE_SWITCH)
                && BCAASApplication.isIsLogin()) {
            //如果當前是切換語言，那麼需要直接重新綁定服務，連接TCP
            getMyIPInfo();
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
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (multipleClickToDo(3)) {
                    OttoTool.getInstance().post(new ShowSANIPEvent(BCAASApplication.getTcpIp() + MessageConstants.REQUEST_COLON + BCAASApplication.getTcpPort(), false));
                }
            }
        });

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
                handler.sendEmptyMessageDelayed(Constants.SWITCH_BLOCK_SERVICE, Constants.ValueMaps.sleepTime200);
                handler.sendEmptyMessageDelayed(Constants.SWITCH_BLOCK_SERVICE, Constants.ValueMaps.sleepTime400);
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
                    OttoTool.getInstance().post(new SwitchBlockServiceAndVerifyEvent(false, false, bvp.getCurrentItem()));
                    break;
                /*更新区块*/
                case Constants.SWITCH_BLOCK_SERVICE:
                    OttoTool.getInstance().post(new SwitchBlockServiceAndVerifyEvent(false, true));
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

    @Override
    public void verifySuccess(String from) {
        LogTool.d(TAG, MessageConstants.VERIFY_SUCCESS + from);
        //1：验证当前from是来自于何处，然后执行不同的操作
        if (StringTool.notEmpty(from)) {
            switch (from) {
                case Constants.Verify.SWITCH_BLOCK_SERVICE:
                    //切换币种的区块verify
                    getMyIPInfo();
                    break;
                case Constants.Verify.SEND_TRANSACTION:
                    //发送交易之前的验证
                    if (TCPThread.isKeepAlive()) {
                        //如果当前TCP还活着，那么就直接开始请求余额
                        presenter.getLatestBlockAndBalance();
                    } else {
                        getMyIPInfo();
                    }
                    break;
                default:
                    getMyIPInfo();
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
        if (JsonTool.isTokenInvalid(code)) {
            showLogoutSingleDialog();
        } else {
            super.httpExceptionStatus(responseJson);
        }
    }

    @Override
    public void resetAuthNodeFailure(String message, String from) {
        super.resetAuthNodeFailure(message, from);
    }

    /**
     * 重置SAN信息成功，这是从TCP没有连接到 & 网络变化连接过来的，所以直接连接TCP即可
     *
     * @param from
     */
    @Override
    public void resetAuthNodeSuccess(String from) {
        LogTool.d(TAG, MessageConstants.RESET_SAN_SUCCESS + from);
        connectTCP();
    }

    @Override
    public void verifySuccessAndResetAuthNode(String from) {
        LogTool.d(TAG, MessageConstants.VERIFY_SUCCESS_AND_RESET_SAN);
        //1：验证当前from是来自于何处，然后执行不同的操作
        if (StringTool.notEmpty(from)) {
            switch (from) {
                case Constants.Verify.SWITCH_BLOCK_SERVICE:
                    //切换币种的区块verify
                    getMyIPInfo();
                    break;
                case Constants.Verify.SEND_TRANSACTION:
                    //发送交易之前的验证
                    if (TCPThread.isKeepAlive()) {
                        //如果当前TCP还活着，那么就直接开始请求余额
                        presenter.getLatestBlockAndBalance();
                    } else {
                        getMyIPInfo();
                    }
                    break;
                default:
                    getMyIPInfo();
                    break;
            }
        }
    }

    @Subscribe
    public void bindTCPServiceEvent(BindTCPServiceEvent bindServiceEvent) {
        if (bindServiceEvent != null) {
            getMyIPInfo();
        }
    }

    //获取当前Wallet的ip信息
    private void getMyIPInfo() {
        MasterRequester.getMyIpInfo(getMyIpInfoListener);
    }

    private GetMyIpInfoListener getMyIpInfoListener = new GetMyIpInfoListener() {
        @Override
        public void responseGetMyIpInfo(boolean isSuccess) {
            LogTool.d(TAG, MessageConstants.socket.WALLET_EXTERNAL_IP + BCAASApplication.getWalletExternalIp());
            if (!checkActivityState()) {
                return;
            }
            //无论返回的结果是否成功，都前去连接
            connectTCP();
        }
    };

    /**
     * 连接TCP信息
     */
    private void connectTCP() {
        if (tcpService != null) {
            LogTool.d(TAG, MessageConstants.Service.TAG, MessageConstants.START_TCP_SERVICE_BY_ALREADY_CONNECTED);
            tcpService.startTcp(tcpRequestListener);
        } else {
            LogTool.d(TAG, MessageConstants.Service.TAG, MessageConstants.BIND_TCP_SERVICE);
            //绑定当前服务
            tcpServiceIntent = new Intent(MainActivity.this, TCPService.class);
            bindService(tcpServiceIntent, tcpConnection, Context.BIND_AUTO_CREATE);
        }
    }

    //监听Tcp数据返回
    TCPRequestListener tcpRequestListener = new TCPRequestListener() {

        @Override
        public void sendTransactionFailure(String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LogTool.d(TAG, message);
                    hideLoadingDialog();
                    showToast(getResources().getString(R.string.transaction_has_failure));
                }
            });
        }

        @Override
        public void sendTransactionSuccess(String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideLoadingDialog();
                    //提示「Send」成功
                    showToast(getResources().getString(R.string.transaction_has_successfully));
                }
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    OttoTool.getInstance().post(new RefreshRepresentativeEvent(representative));
                }
            });
        }

        @Override
        public void modifyRepresentativeResult(String currentStatus, boolean isSuccess, int code) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    OttoTool.getInstance().post(new ModifyRepresentativeResultEvent(currentStatus, isSuccess, code));
                }
            });
        }

        @Override
        public void reLogin() {
            LogTool.d(TAG, MessageConstants.Logout.TAG, logout);
            if (!logout) {
                logout = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showLogoutSingleDialog();
                    }
                });
            }
        }

        @Override
        public void noEnoughBalance() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast(getResources().getString(R.string.insufficient_balance));
                    hideLoading();
                }
            });
        }

        @Override
        public void amountException() {
            hideLoading();
            LogTool.d(TAG, MessageConstants.AMOUNT_EXCEPTION);
        }

        @Override
        public void tcpResponseDataError(String responseDataError) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideLoading();
                    showToast(responseDataError);
                }
            });
        }

        @Override
        public void getDataException(String message) {
            LogTool.d(TAG, MessageConstants.GET_TCP_DATA_EXCEPTION + message);

        }

        @Override
        public void refreshTransactionRecord() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //刷新「交易记录」
                    OttoTool.getInstance().post(new RefreshTransactionRecordEvent());
                }
            });
        }

        @Override
        public void showNotification(String blockService, String amount) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //弹出通知
                    NotificationTool.setNotification(MainActivity.this, String.format(context.getString(R.string.receive_block_notification), blockService), amount + "\r" + blockService);
                    showNotificationToast(String.format(getResources().getString(R.string.receive_block_notification), blockService), amount + "\r" + blockService);

                }
            });
        }

        @Override
        public void refreshTCPConnectIP(String ip) {
            if (BuildConfig.SANIP) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        OttoTool.getInstance().post(new ShowSANIPEvent(ip, true));
                    }
                });
            }

        }

        @Override
        public void resetSuccess() {
            //reset success，直接连接TCP，不用重新获取IP，因为每次reset都将getIP请求过了
            connectTCP();
        }

        @Override
        public void needUnbindService() {
            unBindService();
        }

        @Override
        public void balanceIsSynchronizing() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideLoading();
                    showToast(getResources().getString(R.string.data_synchronizing));
                }
            });
        }
    };

    /**
     * 解绑当前的服务
     */
    public void unBindService() {
        if (tcpService != null) {
            tcpService.onUnbind(tcpServiceIntent);
        }
    }

    @Subscribe
    public void unBindServiceEvent(UnBindTCPServiceEvent unBindServiceEvent) {
        unBindService();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogTool.d(TAG, MessageConstants.DESTROY);
        finishActivity();
    }

    @Override
    public void verifyFailure(String from) {
        LogTool.d(TAG, MessageConstants.VERIFY_FAILURE + from);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //验证失败，需要重新拿去AN的信息
                showToast(getResources().getString(R.string.data_acquisition_error));
            }
        });

    }

    /**
     * 退出当前页面，需要杀掉进程？
     */
//    public void onBackPressed() {
//        ActivityTool.getInstance().exit();
//        finishActivity();
//        super.onBackPressed();
//
//    }

    // 关闭当前页面，中断所有请求
    private void finishActivity() {
        //取消所有网络请求订阅
        if (presenter != null) {
            presenter.unSubscribe();
        }
        //清除存储的背景执行任务
        cleanQueueTask();
        //解绑当前的tcpService
        unBindService();
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

    /*收到订阅，然后进行区块验证*/
    @Subscribe
    public void switchBlockService(SwitchBlockServiceAndVerifyEvent switchBlockServiceAndVerifyEvent) {
        if (switchBlockServiceAndVerifyEvent != null) {
            boolean isVerify = switchBlockServiceAndVerifyEvent.isVerify();
            if (isVerify) {
                TCPThread.setActiveDisconnect(true);
                verify();
            }
        }

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
                unBindService();
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

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (KeyEvent.KEYCODE_HOME == keyCode) {
//            LogTool.d(TAG, keyCode);
//            finishActivity();
//        }
//        return super.onKeyDown(keyCode, event);
//    }

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
            if (requestCode == Constants.KeyMaps.REQUEST_CODE_CAMERA_OK) {
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

    public void sendTransaction() {
        LogTool.d(TAG, "sendTransactionEvent");
        showLoading();
        //请求SFN的「verify」接口，返回成功方可进行AN的「获取余额」接口以及「发起交易」
        presenter.checkVerify(Constants.Verify.SEND_TRANSACTION);
    }

    private void checkNotificationPermission() {
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            //检查当前是否开启通知权限
            showBcaasDialog(getString(R.string.no_notification_permission), new BcaasDialog.ConfirmClickListener() {
                @Override
                public void sure() {
                    Intent localIntent = new Intent();
                    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (Build.VERSION.SDK_INT >= 9) {
                        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        localIntent.setData(Uri.fromParts("package", MainActivity.this.getPackageName(), null));
                    }
                    //else if (Build.VERSION.SDK_INT <= 8) {
//                    localIntent.setAction(Intent.ACTION_VIEW);
//
//                    localIntent.setClassName("com.android.settings",
//                            "com.android.settings.InstalledAppDetails");
//
//                    localIntent.putExtra("com.android.settings.ApplicationPkgName",
//                            LoginActivity.this.getPackageName());
//                }

                    startActivity(localIntent);
                }

                @Override
                public void cancel() {

                }
            });
        }

    }
}
