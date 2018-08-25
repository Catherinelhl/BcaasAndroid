package io.bcaas.ui.activity;


import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.obt.qrcode.activity.CaptureActivity;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BaseFragment;
import io.bcaas.base.BcaasApplication;
import io.bcaas.bean.TransactionsBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.event.RefreshSendStatus;
import io.bcaas.event.SwitchTab;
import io.bcaas.event.UpdateAddressEvent;
import io.bcaas.event.UpdateReceiveBlock;
import io.bcaas.event.UpdateWalletBalance;
import io.bcaas.http.thread.ReceiveThread;
import io.bcaas.presenter.MainPresenterImp;
import io.bcaas.tools.ListTool;
import io.bcaas.ui.contracts.BaseContract;
import io.bcaas.ui.contracts.MainContracts;
import io.bcaas.ui.fragment.MainFragment;
import io.bcaas.ui.fragment.ReceiveFragment;
import io.bcaas.ui.fragment.ScanFragment;
import io.bcaas.ui.fragment.SendFragment;
import io.bcaas.ui.fragment.SettingFragment;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.OttoTool;
import io.bcaas.vo.PaginationVO;
import io.bcaas.vo.TransactionChainVO;
import io.bcaas.vo.WalletVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 进入当前钱包首页
 */
public class MainActivity extends BaseActivity
        implements MainContracts.View {

    private String TAG = "MainActivity";

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tab_bar)
    BottomNavigationBar tabBar;
    private List<String> currency;//获取所有的币种
    private List<TransactionsBean> allCurrency;//获取所有的币种以及相关的交易信息

    private List<BaseFragment> mFragmentList;
    private Fragment currentFragment;
    private int currentIndex;

    private String from;//记录是从那里跳入到当前的首页

    private MainContracts.Presenter presenter;


    @Override
    public void getArgs(Bundle bundle) {
        if (bundle == null) return;
        from = bundle.getString(Constants.KeyMaps.From);
    }

    @Override
    public int getContentView() {
        return R.layout.aty_main;
    }

    @Override
    public void initViews() {
        // TODO: 2018/8/25 如果是到首页去「verify」，那么就需要在首页获取到「blockService」
        BcaasApplication.setBlockService(Constants.BlockService.BCC);
        presenter = new MainPresenterImp(this);
        mFragmentList = new ArrayList<>();
        presenter.checkANClientIPInfo(from);//检查本地当前AN信息
        initCurrency();
        initCurrencyData();
        initFragment();
        initNavigation();
        setMainTitle();
        replaceFragment(0);

    }

    private void initNavigation() {
        tabBar.clearAll();
        tabBar.addItem(new BottomNavigationItem(R.mipmap.icon_home_f, getString(R.string.main)).setInactiveIconResource(R.mipmap.icon_home))
                .addItem(new BottomNavigationItem(R.mipmap.icon_receive_f, getString(R.string.receive)).setInactiveIconResource(R.mipmap.icon_receive))
                .addItem(new BottomNavigationItem(R.mipmap.icon_scan_f, getString(R.string.scan)).setInactiveIconResource(R.mipmap.icon_scan))
                .addItem(new BottomNavigationItem(R.mipmap.icon_send_f, getString(R.string.send)).setInactiveIconResource(R.mipmap.icon_send))
                .addItem(new BottomNavigationItem(R.mipmap.icon_setting_f, getString(R.string.setting)).setInactiveIconResource(R.mipmap.icon_setting))
                .setFirstSelectedPosition(0)
                .initialise();
        tabBar.selectTab(0, true);
    }

    private void initCurrency() {
        currency = new ArrayList<>();
        // TODO: 2018/8/25 待定
        currency.add(Constants.BlockService.BCC);
    }

    private void initCurrencyData() {
        allCurrency = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < getCurrency().size(); i++) {
            int rand = random.nextInt(9999) + 10000;
            TransactionsBean transactionsBean = new TransactionsBean("asdfafas==", String.valueOf(rand), getCurrency().get(i));
            allCurrency.add(transactionsBean);
        }

    }

    private void stopSocket() {
        showToast("stop socket");
        ReceiveThread.kill();
        presenter.onResetAuthNodeInfo();
    }

    @Override
    public void initListener() {
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSocket();
            }
        });
        tabBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                //未选中->选中
                replaceFragment(position);
                switch (position) {
                    case 0:
                        tvTitle.setText(getResources().getString(R.string.main));
                        break;
                    case 1:
                        tvTitle.setText(getResources().getString(R.string.receive));
                        break;
                    case 2:
                        tvTitle.setText(getResources().getString(R.string.scan));
                        switchTab(0);
                        setMainTitle();
                        intentToCaptureAty();
                        break;
                    case 3:
                        tvTitle.setText(getResources().getString(R.string.send));
                        break;
                    case 4:
                        tvTitle.setText(getResources().getString(R.string.setting));
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

    private void intentToCaptureAty() {
        startActivityForResult(new Intent(this, CaptureActivity.class), 0);

    }

    public List<String> getCurrency() {
        return currency;
    }

    public List<TransactionsBean> getAllCurrencyData() {
        return allCurrency;
    }

    //切换当前底部栏的tab
    public void switchTab(int position) {
        if (tabBar == null) return;
        tabBar.selectTab(position);
        if (position == 0) {
            setMainTitle();
        }
    }

    private void setMainTitle() {
        tvTitle.setText(getResources().getString(R.string.bcaas_u));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (data == null) return;
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String result = bundle.getString("result");
                //TODO 存储当前的扫描结果？
                switchTab(3);//扫描成功，然后将当前扫描数据存储，然后跳转到发送页面
                OttoTool.getInstance().post(new UpdateAddressEvent(result));
            }
        }
    }

    @Subscribe
    public void updateAddressEvent(UpdateAddressEvent updateAddressEvent) {
        System.out.println("UpdateAddressEvent" + updateAddressEvent);
        if (updateAddressEvent == null) return;
        String result = updateAddressEvent.getResult();
        showToast(result);
    }

    @Subscribe
    public void switchTab(SwitchTab switchTab) {
        if (switchTab == null) return;
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
        ft.replace(R.id.fl_module, currentFragment);
        ft.commitAllowingStateLoss();
    }

    public void logout() {
        intentToActivity(LoginActivity.class, true);
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
        showToast(message);
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
    public void showPaginationVoList(final List<TransactionChainVO> transactionChainVOList) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO: 2018/8/23 更新签章
                UpdateReceiveBlock(transactionChainVOList);
            }
        });

    }

    private void UpdateReceiveBlock(List<TransactionChainVO> transactionChainVOList) {
        OttoTool.getInstance().post(new UpdateReceiveBlock(transactionChainVOList));

    }

    @Override
    public void sendTransactionFailure(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                OttoTool.getInstance().post(new RefreshSendStatus(true));

            }
        });
    }

    @Override
    public void sendTransactionSuccess(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                OttoTool.getInstance().post(new RefreshSendStatus(true));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unSubscribe();
        finishActivity();
    }

    @Override
    public void showWalletBalance(final String walletBalance) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BcaasApplication.setWalletBalance(walletBalance);
                OttoTool.getInstance().post(new UpdateWalletBalance(walletBalance));
                BcaasLog.d(TAG, "当前可用余额：" + walletBalance);
            }
        });
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
    public void onBackPressed() {
        super.onBackPressed();
        finishActivity();
    }

    private void finishActivity() {
        // 关闭当前页面，中断所有请求
        stopSocket();
    }

    /**
     * 每次选择blockService之后，进行余额以及AN信息的拿取
     */
    public void verify() {
        String blockService = BcaasApplication.getBlockService();
        if (!ListTool.isEmpty(getCurrency())) {
            blockService = getCurrency().get(0);
        }
        WalletVO walletVO = new WalletVO();
        walletVO.setWalletAddress(BcaasApplication.getWalletAddress());
        walletVO.setBlockService(blockService);
        walletVO.setAccessToken(BcaasApplication.getAccessToken());
        presenter.checkVerify(walletVO);

    }
}
