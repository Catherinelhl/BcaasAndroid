package io.bcaas.ui.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.obt.qrcode.activity.CaptureActivity;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.bcaas.BuildConfig;
import io.bcaas.R;
import io.bcaas.adapter.FragmentAdapter;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BaseFragment;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.event.CheckVerifyEvent;
import io.bcaas.event.ModifyRepresentativeResultEvent;
import io.bcaas.event.NetStateChangeEvent;
import io.bcaas.event.RefreshSendStatusEvent;
import io.bcaas.event.SwitchTabEvent;
import io.bcaas.event.LoginEvent;
import io.bcaas.event.UpdateAddressEvent;
import io.bcaas.event.UpdateBlockServiceEvent;
import io.bcaas.event.UpdateRepresentativeEvent;
import io.bcaas.event.UpdateTransactionEvent;
import io.bcaas.event.UpdateWalletBalanceEvent;
import io.bcaas.http.tcp.ReceiveThread;
import io.bcaas.listener.RefreshFragmentListener;
import io.bcaas.presenter.MainPresenterImp;
import io.bcaas.tools.ActivityTool;
import io.bcaas.tools.ListTool;
import io.bcaas.ui.contracts.MainContracts;
import io.bcaas.ui.fragment.MainFragment;
import io.bcaas.ui.fragment.ReceiveFragment;
import io.bcaas.ui.fragment.ScanFragment;
import io.bcaas.ui.fragment.SendFragment;
import io.bcaas.ui.fragment.SettingFragment;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.OttoTool;
import io.bcaas.view.BcaasRadioButton;
import io.bcaas.view.BcaasViewpager;
import io.bcaas.vo.PublicUnitVO;
import io.bcaas.vo.TransactionChainVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * 进入当前钱包首页
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
    private Fragment currentFragment;
    private int currentIndex;
    private String from;//记录是从那里跳入到当前的首页
    private MainContracts.Presenter presenter;
    /*用于刷新Fragment*/
    private RefreshFragmentListener refreshFragmentListener;
    private boolean logout;//存储当前是否登出

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
        BcaasApplication.setKeepHttpRequest(true);
        //將當前的activity加入到管理之中，方便「切換語言」的時候進行移除操作
        ActivityTool.getInstance().addActivity(this);
        presenter = new MainPresenterImp(this);
        presenter.checkANClientIPInfo(from);//检查本地当前AN信息
        setMainTitle();
        initFragment();
        getCameraPermission();
        mainPagerAdapter = new FragmentAdapter(getSupportFragmentManager(), fragmentList);
        bvp.setOffscreenPageLimit(fragmentList.size());// 设置预加载Fragment个数
        bvp.setAdapter(mainPagerAdapter);
        bvp.setCurrentItem(0);// 设置当前显示标签页为第一页
        //获取第一个单选按钮，并设置其为选中状态
        rbHome.setChecked(true);
        bvp.setCanScroll(false);
    }

    private void stopSocket() {
        presenter.stopTCP();
    }

    @Override
    public void initListener() {
        tvTitle.setOnClickListener(v -> {
            if (BuildConfig.DEBUG) {
                stopSocket();
            }
        });

        rbHome.setOnClickListener(view -> {
            switchTab(0);
        });
        rbReceive.setOnClickListener(view -> {
            switchTab(1);


        });
        rbScan.setOnClickListener(view -> {
            switchTab(2);

        });
        rbSend.setOnClickListener(view -> {
            switchTab(3);
        });
        rbSetting.setOnClickListener(view -> {
            switchTab(4);
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

    public void intentToCaptureAty() {
        startActivityForResult(new Intent(this, CaptureActivity.class), 0);

    }

    //切换当前底部栏的tab
    public void switchTab(int position) {
        if (bvp == null) {
            return;
        }
        resetRadioButton();
        bvp.setCurrentItem(position);
        switch (position) {
            case 0:
                setMainTitle();
                rbHome.setChecked(true);
                handler.sendEmptyMessageDelayed(Constants.UPDATE_BLOCK_SERVICE, Constants.ValueMaps.sleepTime400);
                tvTitle.setText(getResources().getString(R.string.home));
                break;
            case 1:
                rbReceive.setChecked(true);
                bvp.setCurrentItem(1);
                tvTitle.setText(getResources().getString(R.string.receive));
                break;
            case 2:
                rbScan.setChecked(true);
                tvTitle.setText(getResources().getString(R.string.scan));
                intentToCaptureAty();
                handler.sendEmptyMessageDelayed(Constants.SWITCH_TAB, Constants.ValueMaps.sleepTime500);
                break;
            case 3:
                rbSend.setChecked(true);
                /*如果当前点击的是「发送页面」，应该通知其更新余额显示*/
                handler.sendEmptyMessageDelayed(Constants.UPDATE_BLOCK_SERVICE, Constants.ValueMaps.sleepTime400);
                handler.sendEmptyMessageDelayed(Constants.UPDATE_WALLET_BALANCE, Constants.ValueMaps.sleepTime400);
                tvTitle.setText(getResources().getString(R.string.send));
                break;
            case 4:
                rbSetting.setChecked(true);
                tvTitle.setText(getResources().getString(R.string.settings));
                break;

        }
    }

    private void setMainTitle() {
        tvTitle.setText(getResources().getString(R.string.home));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String result = bundle.getString(Constants.RESULT);
                BcaasApplication.setDestinationWallet(result);
                switchTab(3);//扫描成功，然后将当前扫描数据存储，然后跳转到发送页面
                handler.sendEmptyMessageDelayed(Constants.RESULT_CODE, Constants.ValueMaps.sleepTime800);

            }
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
                    String result = BcaasApplication.getDestinationWallet();
                    OttoTool.getInstance().post(new UpdateAddressEvent(result));
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

    @Subscribe
    public void switchTab(SwitchTabEvent switchTab) {
        if (switchTab == null) {
            return;
        }
        switchTab(switchTab.getPosition());
    }

    private void initFragment() {
        //tab 和 fragment 联动
        MainFragment mainFragment = MainFragment.newInstance();
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
        OttoTool.getInstance().post(new UpdateWalletBalanceEvent(BcaasApplication.getWalletBalance()));
    }

    /*发出更新区块服务的通知*/
    private void updateBlockService() {
        OttoTool.getInstance().post(new UpdateBlockServiceEvent());
    }

    /**
     * 登出
     */
    public void logoutDialog() {
        LogTool.d(TAG, logout);
        if (!logout) {
            logout = true;
            handler.post(() -> showBcaasSingleDialog(getString(R.string.warning),
                    getString(R.string.please_login_again), () -> logout()));
        }
    }

    public void logout() {
        BcaasApplication.setKeepHttpRequest(false);
        ReceiveThread.stopSocket = true;
        ReceiveThread.kill();
        clearLocalData();
        intentToActivity(LoginActivity.class, true);
    }

    //清空当前的本地数据
    private void clearLocalData() {
        BcaasApplication.clearAccessToken();

    }

    @Override
    public void httpGetLatestBlockAndBalanceSuccess() {
        LogTool.d(TAG, MessageConstants.SUCCESS_GET_WALLET_RECEIVE_BLOCK);

    }

    @Override
    public void httpGetLatestBlockAndBalanceFailure() {
        LogTool.d(TAG, MessageConstants.FAILURE_GET_LATESTBLOCK_AND_BALANCE);
    }

    @Override
    public void resetAuthNodeFailure(String message) {
        presenter.onResetAuthNodeInfo();
    }

    @Override
    public void resetAuthNodeSuccess() {
        presenter.startTCP();
    }

    @Override
    public void noData() {
        showToast(getResources().getString(R.string.account_data_error));

    }

    @Override
    public void noAnClientInfo() {
        //需要重新reset
        presenter.onResetAuthNodeInfo();
    }

    @Override
    public void showTransactionChainView(final List<TransactionChainVO> transactionChainVOList) {
        this.runOnUiThread(() -> OttoTool.getInstance().post(new UpdateTransactionEvent(transactionChainVOList)));

    }

    @Override
    public void hideTransactionChainView() {
        this.runOnUiThread(() -> OttoTool.getInstance().post(new UpdateTransactionEvent(new ArrayList<TransactionChainVO>())));

    }

    //得到当前已经签章的区块，进行首页的刷新
    @Override
    public void signatureTransaction(TransactionChainVO transactionChain) {
        handler.post(() -> OttoTool.getInstance().post(new UpdateTransactionEvent(transactionChain)));

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
    protected void onDestroy() {
        super.onDestroy();
        presenter.unSubscribe();
        finishActivity();
    }

    @Override
    public void noWalletInfo() {

    }

    @Override
    public void loginFailure() {
        showToast(getResources().getString(R.string.password_error));

    }

    @Override
    public void loginSuccess() {

    }

    @Override
    public void verifySuccess() {

    }

    @Override
    public void verifyFailure() {
        showToast(getResources().getString(R.string.data_acquisition_error));
    }

    @Override
    public void onBackPressed() {
        ActivityTool.getInstance().exit();
        finishActivity();
        super.onBackPressed();

    }

    // 关闭当前页面，中断所有请求
    private void finishActivity() {
        stopSocket();
    }

    /**
     * 每次选择blockService之后，进行余额以及AN信息的拿取
     * 且要暫停當前socket的請求
     */
    public void verify() {
        presenter.stopTCP();
        presenter.checkVerify();
    }


    @Subscribe
    public void toLoginWallet(LoginEvent loginSuccess) {
        presenter.unSubscribe();
    }

    /*獲得照相機權限*/
    private void getCameraPermission() {
        if (Build.VERSION.SDK_INT > 22) { //这个说明系统版本在6.0之下，不需要动态获取权限。
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //先判断有没有权限 ，没有就在这里进行权限的申请,否则说明已经获取到摄像头权限了 想干嘛干嘛
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.CAMERA}, Constants.KeyMaps.CAMERA_OK);

            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.KeyMaps.CAMERA_OK:
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
    public void showWalletBalance(final String walletBalance) {
        String balance = walletBalance;
        LogTool.d(TAG, MessageConstants.BALANCE + balance);
        BcaasApplication.setWalletBalance(balance);
        runOnUiThread(() -> OttoTool.getInstance().post(new UpdateWalletBalanceEvent(balance)));
    }

    /*设置刷新*/
    public void setRefreshFragmentListener(RefreshFragmentListener refreshFragmentListener) {
        this.refreshFragmentListener = refreshFragmentListener;
        presenter.getBlockServiceList();

    }

    @Override
    public void getBlockServicesListSuccess(List<PublicUnitVO> publicUnitVOList) {
        if (ListTool.isEmpty(publicUnitVOList)) {
            return;
        }
        if (refreshFragmentListener != null) {
            refreshFragmentListener.refreshBlockService(publicUnitVOList);
        }
    }

    @Override
    public void noBlockServicesList() {
        LogTool.d(TAG, MessageConstants.NO_BLOCK_SERVICE);
    }

    /*不能发起修改授权*/
    @Override
    public void canNotModifyRepresentative() {
        handler.post(() -> showToast(getResources().getString(R.string.authorized_representative_can_not_be_modified)));
    }

    @Override
    public void toModifyRepresentative(String representative) {
        LogTool.d(TAG,"toModifyRepresentative");
        handler.post(() -> OttoTool.getInstance().post(new UpdateRepresentativeEvent(representative)));
    }

    @Override
    public void modifyRepresentativeResult(String currentStatus,boolean isSuccess, int code) {
        handler.post(() -> OttoTool.getInstance().post(new ModifyRepresentativeResultEvent(currentStatus,isSuccess, code)));
    }

    @Override
    public void passwordError() {
        showToast(getResources().getString(R.string.password_error));

    }

    @Override
    public void responseDataError() {
        showToast(getResources().getString(R.string.data_acquisition_error));

    }

    @Override
    public void toLogin() {
        logoutDialog();
    }

    @Override
    public void noEnoughBalance() {
        handler.post(() -> showToast(getResources().getString(R.string.insufficient_balance)));
    }

    @Override
    public void tcpResponseDataError(String nullWallet) {
        handler.post(() -> showToast(nullWallet));
    }

    /*收到订阅，然后进行区块验证*/
    @Subscribe
    public void CheckVerifyEvent(CheckVerifyEvent checkVerifyEvent) {
        verify();
    }

    @Subscribe
    public void netStateChange(NetStateChangeEvent netStateChangeEvent) {
        if (netStateChangeEvent != null) {
            if (netStateChangeEvent.isConnect()) {
                if (ReceiveThread.stopSocket) {
                    presenter.checkANClientIPInfo(from);//检查本地当前AN信息
                }
            } else {
                presenter.stopTCP();
                showToast(getResources().getString(R.string.network_not_reachable));
            }
        }
    }

    @Override
    public void noNetWork() {
        showToast(getResources().getString(R.string.network_not_reachable));

    }
}
