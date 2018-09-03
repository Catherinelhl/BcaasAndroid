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
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.obt.qrcode.activity.CaptureActivity;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BaseFragment;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.event.RefreshSendStatus;
import io.bcaas.event.SwitchTab;
import io.bcaas.event.ToLogin;
import io.bcaas.event.UpdateAddressEvent;
import io.bcaas.event.UpdateTransactionData;
import io.bcaas.event.UpdateWalletBalance;
import io.bcaas.http.thread.ReceiveThread;
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
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.OttoTool;
import io.bcaas.vo.PublicUnitVO;
import io.bcaas.vo.TransactionChainVO;
import io.bcaas.vo.WalletVO;

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
    @BindView(R.id.tab_bar)
    BottomNavigationBar tabBar;
    private List<BaseFragment> mFragmentList;
    private Fragment currentFragment;
    private int currentIndex;
    private String from;//记录是从那里跳入到当前的首页
    private MainContracts.Presenter presenter;
    /*當前的狀態是否是登出*/
    private boolean isLogout;
    /*用于刷新Fragment*/
    private RefreshFragmentListener refreshFragmentListener;


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
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //將當前的activity加入到管理之中，方便「切換語言」的時候進行移除操作
        ActivityTool.getInstance().addActivity(this);
        BcaasApplication.setStringToSP(Constants.Preference.BLOCK_SERVICE, Constants.BLOCKSERVICE_BCC);
        presenter = new MainPresenterImp(this);
        mFragmentList = new ArrayList<>();
        presenter.checkANClientIPInfo(from);//检查本地当前AN信息
        initFragment();
        initNavigation();
        setMainTitle();
        replaceFragment(0);
        getCameraPermission();
    }

    private void initNavigation() {
        tabBar.clearAll();
        tabBar.addItem(new BottomNavigationItem(R.mipmap.icon_home_f, getString(R.string.home)).setInactiveIconResource(R.mipmap.icon_home))
                .addItem(new BottomNavigationItem(R.mipmap.icon_receive_f, getString(R.string.receive)).setInactiveIconResource(R.mipmap.icon_receive))
                .addItem(new BottomNavigationItem(R.mipmap.icon_scan_f, getString(R.string.scan)).setInactiveIconResource(R.mipmap.icon_scan))
                .addItem(new BottomNavigationItem(R.mipmap.icon_send_f, getString(R.string.send)).setInactiveIconResource(R.mipmap.icon_send))
                .addItem(new BottomNavigationItem(R.mipmap.icon_setting_f, getString(R.string.settings)).setInactiveIconResource(R.mipmap.icon_setting))
                .setFirstSelectedPosition(0)
                .initialise();
        tabBar.selectTab(0, true);
    }

    private void stopSocket() {
        presenter.stopThread();
    }

    @Override
    public void initListener() {
        tvTitle.setOnClickListener(v -> stopSocket());
        tabBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                //未选中->选中
                replaceFragment(position);
                switch (position) {
                    case 0:
                        tvTitle.setText(getResources().getString(R.string.home));
                        break;
                    case 1:
                        tvTitle.setText(getResources().getString(R.string.receive));
                        break;
                    case 2:
                        tvTitle.setText(getResources().getString(R.string.scan));
                        intentToCaptureAty();
                        handler.sendEmptyMessageDelayed(Constants.SWITCH_TAB, Constants.ValueMaps.sleepTime500);
                        break;
                    case 3:
                        tvTitle.setText(getResources().getString(R.string.send));
                        handler.sendEmptyMessageDelayed(Constants.UPDATE_WALLET_BALANCE, Constants.ValueMaps.sleepTime800);
                        break;
                    case 4:
                        tvTitle.setText(getResources().getString(R.string.settings));
                        break;
                }
            }

            @Override
            public void onTabUnselected(int position) {
                //选中->未选中
            }

            @Override
            public void onTabReselected(int position) {
                //选中->选中
            }
        });
    }

    public void intentToCaptureAty() {
        startActivityForResult(new Intent(this, CaptureActivity.class), 0);

    }

    //切换当前底部栏的tab
    public void switchTab(int position) {
        if (tabBar == null) {
            return;
        }
        tabBar.selectTab(position);
        if (position == 0) {
            setMainTitle();
        } else if (position == 3) {
            handler.sendEmptyMessageDelayed(Constants.UPDATE_WALLET_BALANCE, Constants.ValueMaps.sleepTime800);
        }
    }

    private void setMainTitle() {
        tvTitle.setText(getResources().getString(R.string.app_name));
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
                case Constants.SWITCH_TAB:
                    switchTab(0);
                    break;
            }
        }
    };

    @Subscribe
    public void switchTab(SwitchTab switchTab) {
        if (switchTab == null) {
            return;
        }
        switchTab(switchTab.getPosition());
    }

    private void initFragment() {
        //tab 和 fragment 联动
        MainFragment mainFragment = MainFragment.newInstance();
        mFragmentList.add(mainFragment);
        ReceiveFragment receiveFragment = ReceiveFragment.newInstance();
        mFragmentList.add(receiveFragment);
        ScanFragment scanFragment = ScanFragment.newInstance();
        mFragmentList.add(scanFragment);
        SendFragment sendFragment = SendFragment.newInstance();
        mFragmentList.add(sendFragment);
        SettingFragment settingFragment = SettingFragment.newInstance();
        mFragmentList.add(settingFragment);
    }

    private void replaceFragment(int position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        currentFragment = mFragmentList.get(position);
        if (!currentFragment.isAdded()) {
            ft.add(R.id.fl_module, currentFragment);
        }
        ft.show(currentFragment);
        if (currentIndex != position) {
            ft.hide(mFragmentList.get(currentIndex));
            currentIndex = position;
        }
        //如果当前点击的是「发送页面」，应该通知其更新余额显示
        if (position == 3) {
            handler.sendEmptyMessageDelayed(Constants.UPDATE_WALLET_BALANCE, Constants.ValueMaps.sleepTime800);
        }
//        ft.replace(R.id.fl_module, currentFragment);
        ft.commitAllowingStateLoss();
    }

    private void updateWalletBalance() {
        OttoTool.getInstance().post(new UpdateWalletBalance(BcaasApplication.getStringFromSP(Constants.Preference.WALLET_BALANCE)));
    }

    public void logout() {
        isLogout = true;
        clearLocalData();
        intentToActivity(LoginActivity.class, true);
    }

    //清空当前的本地数据
    private void clearLocalData() {
        BcaasApplication.clearAccessToken();

    }

    @Override
    public void httpGetLatestBlockAndBalanceSuccess() {
        BcaasLog.d(TAG, MessageConstants.SUCCESS_GET_WALLET_RECEIVE_BLOCK);

    }

    @Override
    public void httpGetLatestBlockAndBalanceFailure() {
        BcaasLog.d(TAG, MessageConstants.FAILURE_GET_LATESTBLOCK_AND_BALANCE);
    }

    @Override
    public void resetAuthNodeFailure(String message) {
        presenter.onResetAuthNodeInfo();
    }

    @Override
    public void resetAuthNodeSuccess() {
        presenter.startTCPConnectToGetReceiveBlock();
    }

    @Override
    public void noAnClientInfo() {
        //需要重新reset
        presenter.onResetAuthNodeInfo();
    }

    @Override
    public void showTransactionChainView(final List<TransactionChainVO> transactionChainVOList) {
        this.runOnUiThread(() -> OttoTool.getInstance().post(new UpdateTransactionData(transactionChainVOList)));

    }

    @Override
    public void hideTransactionChainView() {
        this.runOnUiThread(() -> OttoTool.getInstance().post(new UpdateTransactionData(new ArrayList<TransactionChainVO>())));

    }

    //得到当前已经签章的区块，进行首页的刷新
    @Override
    public void signatureTransaction(TransactionChainVO transactionChain) {
        OttoTool.getInstance().post(new UpdateTransactionData(transactionChain));

    }

    @Override
    public void sendTransactionFailure(String message) {
        runOnUiThread(() -> OttoTool.getInstance().post(new RefreshSendStatus(true)));
    }

    @Override
    public void sendTransactionSuccess(String message) {
        runOnUiThread(() -> OttoTool.getInstance().post(new RefreshSendStatus(true)));
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
    public void loginFailure(String message) {

    }

    @Override
    public void loginSuccess() {

    }

    @Override
    public void verifySuccess() {

    }

    @Override
    public void verifyFailure(String message) {
        BcaasLog.d(TAG, message);
    }

    @Override
    public void onBackPressed() {
        if (!doubleClickForExit()) {
            onTip(getString(R.string.double_click_for_exit));
        } else {
            super.onBackPressed();
            ReceiveThread.stopSocket = true;
            finishActivity();
        }
    }

    // 关闭当前页面，中断所有请求
    private void finishActivity() {
        stopSocket();
        // 如果當前是登出，那麼不用殺掉所有的進程
        if (!isLogout) {
//            ActivityTool.getInstance().exit();
        }
    }

    /**
     * 每次选择blockService之后，进行余额以及AN信息的拿取
     */
    public void verify() {
        String blockService = BcaasApplication.getBlockService();
        WalletVO walletVO = new WalletVO();
        walletVO.setWalletAddress(BcaasApplication.getWalletAddress());
        walletVO.setBlockService(blockService);
        walletVO.setAccessToken(BcaasApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN));
        presenter.checkVerify(walletVO);

    }


    @Subscribe
    public void toLoginWallet(ToLogin loginSuccess) {
        presenter.unSubscribe();
    }

    /*獲得照相機權限*/
    private void getCameraPermission() {
        if (Build.VERSION.SDK_INT > 22) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //先判断有没有权限 ，没有就在这里进行权限的申请
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.CAMERA}, Constants.KeyMaps.CAMERA_OK);

            } else {
                //说明已经获取到摄像头权限了 想干嘛干嘛
                BcaasLog.d(TAG);
            }
        } else {
            //这个说明系统版本在6.0之下，不需要动态获取权限。
            BcaasLog.d(TAG);
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
                    showToast(getString(R.string.please_open_camera_permission));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void showWalletBalance(final String walletBalance) {
        String balance = walletBalance;
        BcaasLog.d(TAG, "餘額：" + balance);
        BcaasApplication.setStringToSP(Constants.Preference.WALLET_BALANCE, balance);
        runOnUiThread(() -> OttoTool.getInstance().post(new UpdateWalletBalance(balance)));
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
        BcaasLog.d(TAG, getString(R.string.no_block_service));
    }
}
