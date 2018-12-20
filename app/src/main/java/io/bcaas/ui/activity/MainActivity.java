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
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.obt.qrcode.activity.CaptureActivity;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.adapter.FragmentAdapter;
import io.bcaas.base.BCAASApplication;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BaseFragment;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.event.BindTCPServiceEvent;
import io.bcaas.event.ModifyRepresentativeResultEvent;
import io.bcaas.event.NetStateChangeEvent;
import io.bcaas.event.RefreshRepresentativeEvent;
import io.bcaas.event.RefreshSendFragmentEvent;
import io.bcaas.event.RefreshTransactionRecordEvent;
import io.bcaas.event.RefreshWalletBalanceEvent;
import io.bcaas.event.ShowMainFragmentGuideEvent;
import io.bcaas.event.ShowSANIPEvent;
import io.bcaas.event.SwitchBlockServiceAndVerifyEvent;
import io.bcaas.event.UnBindTCPServiceEvent;
import io.bcaas.event.VerifySuccessAndResetAuthNodeEvent;
import io.bcaas.gson.ResponseJson;
import io.bcaas.http.requester.MasterRequester;
import io.bcaas.http.tcp.TCPThread;
import io.bcaas.listener.GetMyIpInfoListener;
import io.bcaas.listener.TCPRequestListener;
import io.bcaas.presenter.BlockServicePresenterImp;
import io.bcaas.presenter.MainPresenterImp;
import io.bcaas.service.TCPService;
import io.bcaas.tools.ActivityTool;
import io.bcaas.tools.DensityTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.NotificationTool;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.gson.JsonTool;
import io.bcaas.ui.contracts.BlockServiceContracts;
import io.bcaas.ui.contracts.MainContracts;
import io.bcaas.ui.fragment.MainFragment;
import io.bcaas.ui.fragment.ReceiveFragment;
import io.bcaas.ui.fragment.ScanFragment;
import io.bcaas.ui.fragment.SendFragment;
import io.bcaas.ui.fragment.SettingFragment;
import io.bcaas.view.BcaasRadioButton;
import io.bcaas.view.BcaasViewpager;
import io.bcaas.view.dialog.BcaasDialog;
import io.bcaas.view.guide.GuideView;
import io.bcaas.vo.PublicUnitVO;
import io.bcaas.BuildConfig;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * Activity：进入當前Wallet首页
 */
public class MainActivity extends BaseActivity
        implements MainContracts.View, BlockServiceContracts.View {
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
    private BlockServiceContracts.Presenter blockServicePresenter;
    //存储当前是否登出
    private boolean logout;
    private TCPService tcpService;
    //得到当前连接service的Intent
    private Intent tcpServiceIntent;
    //如果调用扫描地址，用来存储当前的扫描信息
    private String scanAddress;

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
        activity = this;
        BCAASApplication.setKeepHttpRequest(true);
        tvTitle.setText(getResources().getString(R.string.home));
        //將當前的activity加入到管理之中，方便「切換語言」的時候進行移除操作
        ActivityTool.getInstance().addActivity(this);
        presenter = new MainPresenterImp(this);
        // 初始化获取币种信息的逻辑类
        blockServicePresenter = new BlockServicePresenterImp(this);
        getBlockServiceList(Constants.from.INIT_VIEW);
        initFragment();
        setAdapter();
        isFromLanguageSwitch();
        //绑定下载服务
        bindDownloadService();
        checkNotificationPermission();
        getCameraPermission();
        initCurrencyGuideView();
        if (from.equals(Constants.ValueMaps.FROM_LOGIN)) {
            //清空当前的币种信息
            BCAASApplication.setBlockService(MessageConstants.Empty);
        } else {
            setTitleToBlockService(BCAASApplication.getBlockService(), true);
        }

    }

    /**
     * 开始请求币种信息
     *
     * @param from
     */
    public void getBlockServiceList(String from) {
        blockServicePresenter.getBlockServiceList(from);
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
                //如果当前不是显示的「首页」/「Home」或者当前没有币种已经选择了，那么需要弹出更换币种
                String title = tvTitle.getText().toString();
                if (StringTool.equals(title, getResources().getString(R.string.home))
                        || StringTool.equals(title, getResources().getString(R.string.settings))
                        || StringTool.equals(title, getResources().getString(R.string.send))
                        || StringTool.equals(title, getResources().getString(R.string.scan))
                        || StringTool.equals(title, getResources().getString(R.string.receive))
                        || StringTool.isEmpty(BCAASApplication.getBlockService())) {
                    return;
                }
                showCurrencyListPopWindow(Constants.from.SELECT_CURRENCY);

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

    /**
     * 切换当前底部栏的tab
     *
     * @param position
     */
    public void switchTab(int position) {
        if (!checkActivityState()) {
            return;
        }
        if (bvp == null) {
            return;
        }
        resetRadioButton();
        bvp.setCurrentItem(position);
        // 检查当前是否选择币种，如果有，那么就显示币种信息
        String blockService = BCAASApplication.getBlockService();
        boolean showBlockService = StringTool.notEmpty(blockService);
        switch (position) {
            case 0:
                rbHome.setChecked(true);
                setTitleToBlockService(showBlockService ? blockService : getResources().getString(R.string.home), showBlockService);
                if (showBlockService) {
                    handler.sendEmptyMessageDelayed(Constants.SWITCH_BLOCK_SERVICE, Constants.ValueMaps.sleepTime200);
                    handler.sendEmptyMessageDelayed(Constants.SWITCH_BLOCK_SERVICE, Constants.ValueMaps.sleepTime400);
                }
                break;
            case 1:
                rbReceive.setChecked(true);
                bvp.setCurrentItem(1);
                setTitleToBlockService(getResources().getString(R.string.receive), false);
                break;
            case 2:
                intentToCaptureActivity();
                rbScan.setChecked(true);
                setTitleToBlockService(getResources().getString(R.string.scan), false);
                handler.sendEmptyMessageDelayed(Constants.SWITCH_TAB, Constants.ValueMaps.sleepTime500);
                break;
            case 3:
                rbSend.setChecked(true);
                /*如果当前点击的是「发送页面」，应该通知其更新余额显示*/
                handler.sendEmptyMessageDelayed(Constants.REFRESH_SEND_FRAGMENT, Constants.ValueMaps.sleepTime200);
                handler.sendEmptyMessageDelayed(Constants.REFRESH_SEND_FRAGMENT, Constants.ValueMaps.sleepTime400);
                setTitleToBlockService(getResources().getString(R.string.send), false);
                break;
            case 4:
                rbSetting.setChecked(true);
                setTitleToBlockService(getResources().getString(R.string.settings), false);
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
                    break;
                case Constants.REFRESH_SEND_FRAGMENT:
                    /*发出「发送」页面的通知*/
                    OttoTool.getInstance().post(new RefreshSendFragmentEvent());
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

    /**
     * 如果选择了币种之后
     * 将当前「首页」标题设置为当前的币种
     *
     * @param showBlockService 是否是显示币种信息
     * @Param title 需要显示的title
     */
    private void setTitleToBlockService(String title, boolean showBlockService) {
        if (tvTitle != null) {
            //  判断是否显示币种
            if (showBlockService) {
                if (StringTool.notEmpty(title)) {
                    tvTitle.setText(title);
                    tvTitle.setBackground(context.getResources().getDrawable(R.drawable.stroke_border_black));
                    tvTitle.setCompoundDrawablePadding(5);
                    tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.mipmap.icon_select_black), null);

                    //显示引导页面
                    guideViewCurrency.show(Constants.Preference.GUIDE_MAIN_CURRENCY);
                } else {
                    tvTitle.setBackground(null);
                    tvTitle.setText(getResources().getString(R.string.home));
                    tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

                }
            } else {
                tvTitle.setBackground(null);
                tvTitle.setText(title);
                tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

            }
        }
    }

    //首页「币种」可切换教学页面
    private GuideView guideViewCurrency;

    /**
     * 选择币种的引导页面
     */
    private void initCurrencyGuideView() {
        View view = LayoutInflater.from(this).inflate(R.layout.help_view_main, null);
        LinearLayout linearLayout = view.findViewById(R.id.ll_guide);
        linearLayout.setGravity(Gravity.CENTER);
        TextView textView = view.findViewById(R.id.tv_content);
        textView.setText(context.getResources().getString(R.string.touch_can_change_blockservice));
        ImageView imageView = view.findViewById(R.id.iv_gesture);
        imageView.setImageResource(R.drawable.icon_help_gesture);
        Button button = view.findViewById(R.id.btn_next);
        button.setText(context.getResources().getString(R.string.next));
        guideViewCurrency = GuideView.Builder
                .newInstance(this)
                .setTargetView(tvTitle)//设置目标
                .setIsDraw(true)
                .setCustomGuideView(view)
                .setDirction(GuideView.Direction.CENTER_BOTTOM)
                .setShape(GuideView.MyShape.RECTANGULAR)
                .setRadius(DensityTool.dip2px(BCAASApplication.context(), 6))
                .setBgColor(getResources().getColor(R.color.black80))
                .build();
        guideViewCurrency.setOnClickListener(v -> {
            guideViewCurrency.hide();
            OttoTool.getInstance().post(new ShowMainFragmentGuideEvent());
        });

        button.setOnClickListener(v -> {
            guideViewCurrency.hide();
            OttoTool.getInstance().post(new ShowMainFragmentGuideEvent());
        });

    }

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

    @Subscribe
    public void VerifySuccessAndResetAuthNode(VerifySuccessAndResetAuthNodeEvent verifySuccessAndResetAuthNodeEvent) {
        if (verifySuccessAndResetAuthNodeEvent != null) {
            String from = verifySuccessAndResetAuthNodeEvent.getFrom();
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
            runOnUiThread(() -> OttoTool.getInstance().post(new RefreshWalletBalanceEvent(Constants.EventSubscriber.ALL)));
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
            if (BuildConfig.SANIP || BCAASApplication.showSANIP()) {
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
            String from = switchBlockServiceAndVerifyEvent.getFrom();
            //只有当前是从首页的选择币种或者查看余额点击的切换币种，那么去更新首页标题信息
            if (StringTool.equals(from, Constants.from.SELECT_CURRENCY)
                    || StringTool.equals(from, Constants.from.CHECK_BALANCE)) {
                LogTool.d(TAG, "SwitchBlockServiceAndVerifyEvent" + BCAASApplication.getBlockService());
                //更新首页标题显示的币种信息
                setTitleToBlockService(BCAASApplication.getBlockService(), true);
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
                    //扫描成功，得到当前扫描数据
                    String result = bundle.getString(Constants.RESULT);
                    // 判断当前是否有币种，如果有，那么就直接跳转到发送填写信息的页面
                    if (StringTool.notEmpty(BCAASApplication.getBlockService())) {
                        intentToSendFillInActivity(result);
                    } else {
                        switchTab(3);
                        setScanAddress(result);
                    }

                }
            } else if (requestCode == Constants.KeyMaps.REQUEST_CODE_SEND_FILL_IN_ACTIVITY) {
                //判断当前是发送页面进行返回的
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    String result = bundle.getString(Constants.KeyMaps.ACTIVITY_STATUS);
                    switch (result) {
                        case Constants.ValueMaps.ACTIVITY_STATUS_TRADING:
                            //上个页面正在交易跳转到首页，并且开始verify请求
                            if (activity != null) {
                                switchTab(0);
                                sendTransaction();
                            }
                            break;
                        case Constants.ValueMaps.ACTIVITY_STATUS_TODO:
                            //当前没有交易正在发送
                            break;
                    }
                }
            }

        }
    }

    public void intentToSendFillInActivity(String result) {
        if (StringTool.isEmpty(result)) {
            result = getScanAddress();
        }
        Bundle bundleSend = new Bundle();
        bundleSend.putString(Constants.KeyMaps.SCAN_ADDRESS, result);
        Intent intent = new Intent();
        intent.putExtras(bundleSend);
        intent.setClass(MainActivity.this, SendInfoFillInActivity.class);
        startActivityForResult(intent, Constants.KeyMaps.REQUEST_CODE_SEND_FILL_IN_ACTIVITY);
        // 重置扫码数据
        ((MainActivity) activity).setScanAddress(MessageConstants.Empty);
    }

    public void setScanAddress(String scanAddress) {
        this.scanAddress = scanAddress;
    }

    public String getScanAddress() {
        return scanAddress;
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

    @Override
    public void getBlockServicesListSuccess(String from, List<PublicUnitVO> publicUnitVOList) {
        // 这里from会有三种情况，如果是initView，那么不需要做任何操作；
        // 如果是selectCurrency，代表已经选择过了，那么通知所有地方更新最新币种；
        //如果是CheckBalance，代表是首页点击「CheckBalance」、发送页面「首次选择币种」、查看钱包信息首次选择币种
        switch (from) {
            case Constants.from.INIT_VIEW:
                break;
            case Constants.from.CHECK_BALANCE:
                //将当前的币种显示在标题上面，并且通知其他地方进行币种的更新
                break;
            case Constants.from.SELECT_CURRENCY:
                //更新存储的币种值，然后通知其他地方进行币种的更新
                break;
            case Constants.from.SEND_FRAGMENT:
                //通知Send页面可以更新数据
                runOnUiThread(() -> OttoTool.getInstance().post(new RefreshSendFragmentEvent()));
                break;
        }
    }

    @Override
    public void getBlockServicesListFailure(String from) {
        //请求币种信息失败，默认设置BCC
    }

    @Override
    public void noBlockServicesList(String from) {
        //当前没有币种信息，也默认设置BCC
    }
}
