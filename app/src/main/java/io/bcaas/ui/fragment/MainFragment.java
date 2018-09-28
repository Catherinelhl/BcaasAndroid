package io.bcaas.ui.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.adapter.AccountTransactionRecordAdapter;
import io.bcaas.base.BaseFragment;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.event.RefreshBlockServiceEvent;
import io.bcaas.event.RefreshTransactionRecordEvent;
import io.bcaas.event.RefreshWalletBalanceEvent;
import io.bcaas.event.RefreshTransactionEvent;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.presenter.MainFragmentPresenterImp;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.ecc.WalletTool;
import io.bcaas.ui.activity.ChangeServerActivity;
import io.bcaas.ui.activity.MainActivity;
import io.bcaas.ui.contracts.MainFragmentContracts;
import io.bcaas.view.BcaasBalanceTextView;
import io.bcaas.vo.PublicUnitVO;
import io.reactivex.disposables.Disposable;

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
    @BindView(R.id.tv_loading_more)
    TextView tvLoadingMore;
    @BindView(R.id.pb_balance)
    ProgressBar progressBar;

    private AccountTransactionRecordAdapter accountTransactionRecordAdapter;
    private List<Object> objects;
    private List<PublicUnitVO> publicUnitVOList;
    private MainFragmentContracts.Presenter presenter;
    //當前交易紀錄的頁數
    private String nextObjectId;
    //能否加載更多
    private boolean canLoadingMore;
    //是否需要清空當前交易紀錄,默認是false
    private boolean isClearTransactionRecord;
    //标记上一个页面
    private String isFrom;

    public static MainFragment newInstance(String isFrom) {
        MainFragment mainFragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KeyMaps.IS_FROM, isFrom);
        mainFragment.setArguments(bundle);
        return mainFragment;
    }

    @Override
    public void getArgs(Bundle bundle) {
        if (bundle != null) {
            isFrom = bundle.getString(Constants.KeyMaps.IS_FROM);
        }

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
        tvMyAccountAddressValue.setText(BcaasApplication.getWalletAddress());
        initTransactionsAdapter();
        setBalance(BcaasApplication.getWalletBalance());
        publicUnitVOList = WalletTool.getPublicUnitVO();
        setCurrency();
        hideTransactionRecordView();
        onRefreshTransactionRecord();
    }

    /*没有交易记录*/
    private void hideTransactionRecordView() {
        ivNoRecord.setVisibility(View.VISIBLE);
        rvAccountTransactionRecord.setVisibility(View.GONE);
        tvNoTransactionRecord.setVisibility(View.VISIBLE);
        tvLoadingMore.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.GONE);
    }

    /*显示交易记录*/
    private void showTransactionRecordView() {
        ivNoRecord.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        rvAccountTransactionRecord.setVisibility(View.VISIBLE);
        tvNoTransactionRecord.setVisibility(View.GONE);
    }

    /*显示当前币种*/
    private void setCurrency() {
        if (activity == null || tvCurrency == null) {
            return;
        }
        tvCurrency.setText(WalletTool.getDisplayBlockService(publicUnitVOList));
    }

    //对当前的余额进行赋值，如果当前没有读取到数据，那么就显示进度条，否则显示余额
    private void setBalance(String balance) {
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
            ClipData mClipData = ClipData.newPlainText(Constants.KeyMaps.COPY_ADDRESS, BcaasApplication.getWalletAddress());
            // 将ClipData内容放到系统剪贴板里。
            if (cm != null) {
                cm.setPrimaryClip(mClipData);
                showToast(getString(R.string.successfully_copied));
            }
        });
        Disposable subscribe = RxView.clicks(llSelectCurrency)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    showCurrencyListPopWindow(onItemSelectListener, publicUnitVOList);

                });
        tvNoTransactionRecord.setOnLongClickListener(v -> {
            intentToActivity(ChangeServerActivity.class);
            return false;
        });
        Disposable subscribeLoadingMore = RxView.clicks(tvLoadingMore)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    isClearTransactionRecord = false;
                    presenter.getAccountDoneTC(nextObjectId);
                });
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            onRefreshTransactionRecord();
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
        setBalance(BcaasApplication.getWalletBalance());
    }


    /*币种重新选择返回*/
    private OnItemSelectListener onItemSelectListener = new OnItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type) {
            if (type != null) {
                /*显示币种*/
                tvCurrency.setText(type.toString());
                /*存储币种*/
                BcaasApplication.setBlockService(type.toString());
                /*重新verify，获取新的区块数据*/
                if (activity != null) {
                    ((MainActivity) activity).verify();
                }
                onRefreshTransactionRecord();
                /*重置余额*/
                BcaasApplication.resetWalletBalance();
                bbtBalance.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void changeItem(boolean isChange) {

        }
    };

    private void onRefreshTransactionRecord() {
        isClearTransactionRecord = true;
        presenter.getAccountDoneTC(Constants.ValueMaps.DEFAULT_PAGINATION);
    }

    @Subscribe
    public void updateBlockService(RefreshBlockServiceEvent updateBlockServiceEvent) {
        if (activity != null && tvCurrency != null) {
            tvCurrency.setText(BcaasApplication.getBlockService());
            setBalance(BcaasApplication.getWalletBalance());
            onRefreshTransactionRecord();
        }
    }

    @Subscribe
    public void refreshTransactionRecord(RefreshTransactionEvent refreshTransactionEvent) {
        if (checkActivityState()) {
            onRefreshTransactionRecord();
        }
    }

    @Override
    public void getAccountDoneTCFailure(String message) {
        showToast(getResources().getString(R.string.account_data_error));

    }

    @Override
    public void getAccountDoneTCSuccess(List<Object> objectList) {
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
        LogTool.d(TAG, MessageConstants.NO_TRANSACTION_RECORD);
        hideTransactionRecordView();
        objects.clear();
        accountTransactionRecordAdapter.notifyDataSetChanged();
    }

    @Override
    public void noResponseData() {
        showToast(getResources().getString(R.string.account_data_error));
    }

    @Override
    public void getNextObjectId(String nextObjectId) {
        // 置空當前數據
        this.nextObjectId = nextObjectId;
        if (StringTool.equals(nextObjectId, MessageConstants.NEXT_PAGE_IS_EMPTY)) {
            tvLoadingMore.setVisibility(View.GONE);
            canLoadingMore = false;
        } else {
            tvLoadingMore.setVisibility(View.VISIBLE);
            canLoadingMore = true;
        }
    }

    @Override
    public void getBlockServicesListSuccess(List<PublicUnitVO> publicUnitVOList) {
        if (ListTool.noEmpty(publicUnitVOList)) {
            this.publicUnitVOList = publicUnitVOList;
        }
        setCurrency();
        if (activity != null) {

        }
        if (activity != null) {
            ((MainActivity) activity).verify();
        }
    }

    @Override
    public void noBlockServicesList() {
        LogTool.d(TAG, MessageConstants.NO_BLOCK_SERVICE);
        if (activity != null) {
            ((MainActivity) activity).verify();
        }
    }

    @Subscribe
    public void refreshTransactionRecord(RefreshTransactionRecordEvent refreshTransactionRecordEvent) {
        onRefreshTransactionRecord();
    }

}