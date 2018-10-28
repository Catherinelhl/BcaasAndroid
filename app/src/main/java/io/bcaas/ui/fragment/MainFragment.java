package io.bcaas.ui.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.*;

import butterknife.BindView;

import com.jakewharton.rxbinding2.view.RxView;
import com.squareup.otto.Subscribe;

import io.bcaas.R;
import io.bcaas.adapter.AccountTransactionRecordAdapter;
import io.bcaas.base.BCAASApplication;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BaseFragment;
import io.bcaas.bean.GuideViewBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.event.SwitchBlockServiceAndVerifyEvent;
import io.bcaas.event.RefreshTransactionRecordEvent;
import io.bcaas.event.RefreshWalletBalanceEvent;
import io.bcaas.http.tcp.TCPThread;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.presenter.MainFragmentPresenterImp;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.ecc.WalletTool;
import io.bcaas.ui.activity.MainActivity;
import io.bcaas.ui.contracts.MainFragmentContracts;
import io.bcaas.view.BcaasBalanceTextView;
import io.bcaas.vo.PublicUnitVO;
import io.reactivex.disposables.Disposable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 「首页」
 */
public class MainFragment extends BaseFragment implements MainFragmentContracts.View {
    private String TAG = MainFragment.class.getSimpleName();


    @BindView(R.id.tv_currency)
    TextView tvCurrency;
    @BindView(R.id.tv_account_address_value)
    TextView tvMyAccountAddressValue;
    @BindView(R.id.bbt_balance)
    BcaasBalanceTextView bbtBalance;
    @BindView(R.id.rv_account_transaction_record)
    RecyclerView rvAccountTransactionRecord;
    @BindView(R.id.rl_transaction)
    RelativeLayout rlTransaction;
    @BindView(R.id.ll_select_currency)
    LinearLayout llSelectCurrency;
    @BindView(R.id.iv_no_record)
    ImageView ivNoRecord;
    @BindView(R.id.ib_copy)
    ImageButton ibCopy;
    @BindView(R.id.tv_no_transaction_record)
    TextView tvNoTransactionRecord;
    @BindView(R.id.srl_account_transaction_record)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.pb_balance)
    ProgressBar progressBar;

    private AccountTransactionRecordAdapter accountTransactionRecordAdapter;
    private List<Object> objects;
    private MainFragmentContracts.Presenter presenter;
    //當前交易紀錄的頁數
    private String nextObjectId;
    //能否加載更多
    private boolean canLoadingMore;
    //是否需要清空當前交易紀錄,默認是false
    private boolean isClearTransactionRecord;

    public static MainFragment newInstance(String isFrom) {
        MainFragment mainFragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KeyMaps.IS_FROM, isFrom);
        mainFragment.setArguments(bundle);
        return mainFragment;
    }

    @Override
    public void getArgs(Bundle bundle) {
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_main;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void initViews(View view) {
        presenter = new MainFragmentPresenterImp(this);
        presenter.getBlockServiceList();
        objects = new ArrayList<>();
        tvMyAccountAddressValue.setText(BCAASApplication.getWalletAddress());
        initTransactionsAdapter();
        setBalance(BCAASApplication.getWalletBalance());
        setCurrency();
        onRefreshTransactionRecord("initViews");
        swipeRefreshLayout.setColorSchemeResources(
                R.color.button_right_color,
                R.color.button_right_color

        );
        // 設置背景顏色
//        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(context.getResources().getColor(R.color.transparent));
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        initGuideView();
    }

    private void initGuideView() {
        List<GuideViewBean> guideViewBeans = new ArrayList<>();
        GuideViewBean guideViewBeanMainCurrency = new GuideViewBean(R.drawable.img_help_main_currency,
                context.getResources().getString(R.string.touch_can_change_blockservice),
                Constants.Preference.GUIDE_MAIN_CURRENCY, context.getResources().getString(R.string.yes));
        guideViewBeans.add(guideViewBeanMainCurrency);
        GuideViewBean guideViewBeanMainBalance = new GuideViewBean(R.drawable.img_help_main_balance,
                context.getResources().getString(R.string.touch_number_can_show_complete_amount),
                Constants.Preference.GUIDE_MAIN_BALANCE, context.getResources().getString(R.string.next));
        guideViewBeans.add(guideViewBeanMainBalance);
        GuideViewBean guideViewBeanMainCopy = new GuideViewBean(R.drawable.img_help_main_copy,
                context.getResources().getString(R.string.touch_copy_your_wallet_address),
                Constants.Preference.GUIDE_MAIN_COPY, context.getResources().getString(R.string.next));
        guideViewBeans.add(guideViewBeanMainCopy);
        ((BaseActivity) activity).setGuideView(guideViewBeans);


    }

    /*没有交易记录*/
    private void hideTransactionRecordView() {
        if (!checkActivityState()) {
            return;
        }
        if (ivNoRecord != null) {
            ivNoRecord.setVisibility(View.VISIBLE);
        }
        if (rvAccountTransactionRecord != null) {
            rvAccountTransactionRecord.setVisibility(View.GONE);
        }
        if (tvNoTransactionRecord != null) {
            tvNoTransactionRecord.setVisibility(View.VISIBLE);
        }
    }

    /*进入界面隐藏所有的视图*/
    private void hideAllTransactionView() {
        if (!checkActivityState()) {
            return;
        }
        if (ivNoRecord != null) {
            ivNoRecord.setVisibility(View.VISIBLE);
        }
        if (rvAccountTransactionRecord != null) {
            rvAccountTransactionRecord.setVisibility(View.GONE);
        }
        if (tvNoTransactionRecord != null) {
            tvNoTransactionRecord.setVisibility(View.GONE);
        }
    }

    /*显示交易记录*/
    private void showTransactionRecordView() {
        if (!checkActivityState()) {
            return;
        }
        if (ivNoRecord != null) {
            ivNoRecord.setVisibility(View.GONE);
        }
        if (rvAccountTransactionRecord != null) {
            rvAccountTransactionRecord.setVisibility(View.VISIBLE);
        }
        if (tvNoTransactionRecord != null) {
            tvNoTransactionRecord.setVisibility(View.GONE);
        }
    }

    /*显示当前币种*/
    private void setCurrency() {
        if (activity == null || tvCurrency == null) {
            return;
        }
        tvCurrency.setText(WalletTool.getDisplayBlockService());
    }

    //对当前的余额进行赋值，如果当前没有读取到数据，那么就显示进度条，否则显示余额
    private void setBalance(String balance) {
        if (!checkActivityState()) {
            return;
        }
        if (bbtBalance == null || progressBar == null) {
            return;
        }
        if (StringTool.isEmpty(balance)) {
            //隐藏显示余额的文本，展示进度条
            bbtBalance.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            bbtBalance.setVisibility(View.VISIBLE);
            bbtBalance.setBalance(balance);
        }
    }

    private void initTransactionsAdapter() {
        accountTransactionRecordAdapter = new AccountTransactionRecordAdapter(this.context, objects);
        rvAccountTransactionRecord.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false);
        rvAccountTransactionRecord.setLayoutManager(linearLayoutManager);
        rvAccountTransactionRecord.setAdapter(accountTransactionRecordAdapter);
    }


    @Override
    public void initListener() {
        ibCopy.setOnClickListener(v -> {
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText(Constants.KeyMaps.COPY_ADDRESS, BCAASApplication.getWalletAddress());
            // 将ClipData内容放到系统剪贴板里。
            if (cm != null) {
                cm.setPrimaryClip(mClipData);
                showToast(getString(R.string.successfully_copied));
            }
        });
        Disposable subscribe = RxView.clicks(llSelectCurrency)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    showCurrencyListPopWindow(onItemSelectListener);

                });
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            onRefreshTransactionRecord("swipeRefreshLayout");
        });
        rvAccountTransactionRecord.addOnScrollListener(scrollListener);
    }

    private int mLastVisibleItemPosition;
    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                mLastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }
            if (accountTransactionRecordAdapter != null) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && mLastVisibleItemPosition + 1 == accountTransactionRecordAdapter.getItemCount()) {
                    LogTool.d(TAG, MessageConstants.LOADING_MORE + canLoadingMore);

                    //发送网络请求获取更多数据
                    if (canLoadingMore) {
                        isClearTransactionRecord = false;
                        presenter.getAccountDoneTC(nextObjectId);
                    }
                }
            }
        }
    };

    /*更新钱包余额*/
    @Subscribe
    public void UpdateWalletBalance(RefreshWalletBalanceEvent updateWalletBalanceEvent) {
        if (updateWalletBalanceEvent == null) {
            return;
        }
        setBalance(BCAASApplication.getWalletBalance());
    }


    /*币种重新选择返回*/
    private OnItemSelectListener onItemSelectListener = new OnItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type, String from) {
            if (type != null) {
                /*显示币种*/
                tvCurrency.setText(type.toString());
                /*存储币种*/
                BCAASApplication.setBlockService(type.toString());
                /*重新verify，获取新的区块数据*/
                TCPThread.setActiveDisconnect(true);
                verify();
                onRefreshTransactionRecord("onItemSelect");
                /*重置余额*/
                BCAASApplication.resetWalletBalance();
                bbtBalance.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void changeItem(boolean isChange) {

        }
    };

    private void onRefreshTransactionRecord(String from) {
        hideAllTransactionView();
        //开始加载数据，直到结果返回设为false
        swipeRefreshLayout.setRefreshing(true);
        LogTool.d(TAG, "onRefreshTransactionRecord:" + from);
        isClearTransactionRecord = true;
        presenter.getAccountDoneTC(Constants.ValueMaps.DEFAULT_PAGINATION);
    }

    @Subscribe
    public void switchBlockServiceAndVerify(SwitchBlockServiceAndVerifyEvent switchBlockServiceAndVerifyEvent) {
        if (activity != null && tvCurrency != null) {
            tvCurrency.setText(BCAASApplication.getBlockService());
            setBalance(BCAASApplication.getWalletBalance());
            //如果当前是需要verify区块服务，那么就刷新当前交易记录
            if (switchBlockServiceAndVerifyEvent != null) {
                boolean isRefreshTransactionRecord = switchBlockServiceAndVerifyEvent.isRefreshTransactionRecord();
                if (isRefreshTransactionRecord) {
                    onRefreshTransactionRecord("SwitchBlockServiceAndVerifyEvent");
                }
            }
        }

    }


    @Override
    public void getAccountDoneTCFailure(String message) {
        swipeRefreshLayout.setRefreshing(false);
        hideTransactionRecordView();
        LogTool.i(TAG, MessageConstants.getAccountDoneTCFailure + message);
    }

    @Override
    public void getAccountDoneTCSuccess(List<Object> objectList) {
        //隐藏加载框
        swipeRefreshLayout.setRefreshing(false);
        LogTool.d(TAG, MessageConstants.GET_ACCOUNT_DONE_TC_SUCCESS + objectList.size());
        showTransactionRecordView();
        if (isClearTransactionRecord) {
            this.objects.clear();
        }
        this.objects.addAll(objectList);
        accountTransactionRecordAdapter.addAll(objects);
    }

    @Override
    public void noAccountDoneTC() {
        swipeRefreshLayout.setRefreshing(false);
        LogTool.d(TAG, MessageConstants.NO_TRANSACTION_RECORD);
        hideTransactionRecordView();
        objects.clear();
        accountTransactionRecordAdapter.notifyDataSetChanged();
    }

    @Override
    public void getNextObjectId(String nextObjectId) {
        if (!checkActivityState()) {
            return;
        }
        // 置空當前數據
        this.nextObjectId = nextObjectId;
        canLoadingMore = !StringTool.equals(nextObjectId, MessageConstants.NEXT_PAGE_IS_EMPTY);
    }


    @Override
    public void getBlockServicesListSuccess(List<PublicUnitVO> publicUnitVOList) {
        setCurrency();
        verify();
    }

    /**
     * 通知Activity重新Verify
     */
    private void verify() {
        if (activity != null) {
            ((MainActivity) activity).verify();
        }
    }

    @Override
    public void getBlockServicesListFailure() {
        setCurrency();
        verify();
    }

    @Override
    public void noBlockServicesList() {
        LogTool.d(TAG, MessageConstants.NO_BLOCK_SERVICE);
        verify();
    }

    @Subscribe
    public void refreshTransactionRecord(RefreshTransactionRecordEvent refreshTransactionRecordEvent) {
        if (checkActivityState()) {
            onRefreshTransactionRecord("RefreshTransactionRecordEvent");
        }

    }
}