package io.bcaas.ui.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import io.bcaas.base.BCAASApplication;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BaseFragment;
import io.bcaas.bean.TransactionDetailBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.event.RefreshTransactionRecordEvent;
import io.bcaas.event.RefreshWalletBalanceEvent;
import io.bcaas.event.RequestBlockServiceEvent;
import io.bcaas.event.ShowMainFragmentGuideEvent;
import io.bcaas.event.ShowSANIPEvent;
import io.bcaas.event.SwitchBlockServiceAndVerifyEvent;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.presenter.MainFragmentPresenterImp;
import io.bcaas.tools.DensityTool;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.ServerTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.ui.activity.MainActivity;
import io.bcaas.ui.activity.TransactionDetailActivity;
import io.bcaas.ui.contracts.MainFragmentContracts;
import io.bcaas.view.guide.GuideView;
import io.bcaas.view.textview.BcaasBalanceTextView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * Fragment:「首页」
 */
public class MainFragment extends BaseFragment implements MainFragmentContracts.View {
    @BindView(R.id.tv_undone)
    TextView tvUndone;
    @BindView(R.id.tv_done)
    TextView tvDone;
    @BindView(R.id.v_symbol)
    View vSymbol;
    private String TAG = MainFragment.class.getSimpleName();


    @BindView(R.id.tv_show_ip)
    TextView tvShowIp;
    @BindView(R.id.tv_balance_key)
    TextView tvBalanceKey;
    @BindView(R.id.tv_check_balance)
    TextView tvCheckBalance;
    @BindView(R.id.rl_main_top)
    RelativeLayout rlMainTop;
    @BindView(R.id.tv_transaction_record_key)
    TextView tvTransactionRecordKey;

    @BindView(R.id.tv_account_address_value)
    TextView tvMyAccountAddressValue;
    @BindView(R.id.bbt_balance)
    BcaasBalanceTextView bbtBalance;
    @BindView(R.id.rv_account_transaction_record)
    RecyclerView rvAccountTransactionRecord;
    @BindView(R.id.rl_transaction)
    RelativeLayout rlTransaction;
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
    @BindView(R.id.pb_loading_more)
    ProgressBar pbLoadingMore;
    @BindView(R.id.tv_address_key)
    TextView tvAddressKey;

    private AccountTransactionRecordAdapter accountTransactionRecordAdapter;
    private List<Object> objects;
    private MainFragmentContracts.Presenter presenter;
    //當前交易紀錄的頁數
    private String nextObjectId;
    //能否加載更多
    private boolean canLoadingMore;
    //是否需要清空當前交易紀錄,默認是false
    private boolean isClearTransactionRecord;
    //是否正在请求交易记录
    private boolean isRequestTransactionRecord;

    //首页「复制」按钮教学页面
    private GuideView guideViewCopy;
    //首页「余额」可点击教学页面
    private GuideView guideViewBalance;

    //存储上一次的币种信息
    private String lastBlockService;
    //表示当前查询的是交易记录是完成还是未完成的，默认是查询完成的
    private boolean checkDone;

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
        checkDone = true;
        presenter = new MainFragmentPresenterImp(this);
        objects = new ArrayList<>();
        tvMyAccountAddressValue.setText(BCAASApplication.getWalletAddress());
        //初始化「交易记录」adapter
        initTransactionsAdapter();
        //隐藏当前的「交易记录」视图
        hideTransactionRecordView();
        // 设置加载按钮的形态
        swipeRefreshLayout.setColorSchemeResources(
                R.color.button_right_color,
                R.color.button_right_color

        );
        // 設置背景顏色
//        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(context.getResources().getColor(R.color.transparent));
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        //加载引导界面
        initCopyGuideView();
        initBalanceGuideView();

        //判断当前是否有币种信息
        String blockService = BCAASApplication.getBlockService();
        if (StringTool.notEmpty(blockService)) {
            if (tvCheckBalance != null) {
                tvCheckBalance.setVisibility(View.GONE);
            }
            if (tvBalanceKey != null) {
                tvBalanceKey.setVisibility(View.VISIBLE);
            }
            if (bbtBalance != null) {
                bbtBalance.setVisibility(View.INVISIBLE);
            }
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
            //        刷新当前数据
            onRefreshTransactionRecord("initViews");
            if (activity != null) {
                ((MainActivity) activity).verify();
            }
            hideTransactionActionText(false);
        } else {
            hideTransactionActionText(true);
        }

    }

    /**
     * 复制按钮的引导页面
     */
    private void initCopyGuideView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.help_view_main, null);
        LinearLayout linearLayout = view.findViewById(R.id.ll_guide);
        linearLayout.setGravity(Gravity.RIGHT);
        TextView textView = view.findViewById(R.id.tv_content);
        textView.setText(context.getResources().getString(R.string.touch_copy_your_wallet_address));
        ImageView imageView = view.findViewById(R.id.iv_gesture);
        imageView.setImageResource(R.drawable.icon_help_arrow_one);
        Button button = view.findViewById(R.id.btn_next);
        button.setText(context.getResources().getString(R.string.yes));
        guideViewCopy = GuideView.Builder
                .newInstance(getActivity())
                .setTargetView(ibCopy)//设置目标
                .setIsDraw(true)
                .setCustomGuideView(view)
                .setDirction(GuideView.Direction.MAIN_COPY)
                .setShape(GuideView.MyShape.SQUARE)
                .setRadius(DensityTool.dip2px(BCAASApplication.context(), 6))
                .setBgColor(getResources().getColor(R.color.black80))
                .build();
        guideViewCopy.show(Constants.Preference.GUIDE_MAIN_COPY);
        guideViewCopy.setOnClickListener(v -> {
            guideViewCopy.hide();
        });
        button.setOnClickListener(v -> {
            guideViewCopy.hide();
        });


    }

    @Subscribe
    public void ShowMainFragmentGuide(ShowMainFragmentGuideEvent showMainFragmentGuideEvent) {
        if (showMainFragmentGuideEvent != null) {
            if (guideViewBalance != null) {
                bbtBalance.performClick();
                guideViewBalance.show(Constants.Preference.GUIDE_MAIN_BALANCE);
            }
        }
    }

    /**
     * 初始化余额引导
     */
    private void initBalanceGuideView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.help_view_main, null);
        LinearLayout linearLayout = view.findViewById(R.id.ll_guide);
        linearLayout.setGravity(Gravity.LEFT);
        TextView textView = view.findViewById(R.id.tv_content);
        textView.setText(context.getResources().getString(R.string.touch_number_can_show_complete_amount));
        ImageView imageView = view.findViewById(R.id.iv_gesture);
        int width = getResources().getDimensionPixelOffset(R.dimen.d40);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, width);
        layoutParams.setMargins(0, getResources().getDimensionPixelOffset(R.dimen.d20), 0, 0);
        imageView.setLayoutParams(layoutParams);
        imageView.setImageResource(R.drawable.icon_help_gesture);
        Button button = view.findViewById(R.id.btn_next);
        button.setText(context.getResources().getString(R.string.yes));
        guideViewBalance = GuideView.Builder
                .newInstance(getActivity())
                .setTargetView(bbtBalance)//设置目标
                .setIsDraw(true)
                .setCustomGuideView(view)
                .setDirction(GuideView.Direction.LEFT_ALIGN_BOTTOM)
                .setShape(GuideView.MyShape.RECTANGULAR)
                .setRadius(DensityTool.dip2px(BCAASApplication.context(), 6))
                .setBgColor(getResources().getColor(R.color.black80))
                .build();
        guideViewBalance.setOnClickListener(v -> {
            guideViewBalance.hide();

        });
        button.setOnClickListener(v -> {
            guideViewBalance.hide();
        });
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

    //对当前的余额进行赋值，如果当前没有读取到数据，那么就显示进度条，否则显示余额
    private void setBalance(String balance) {
        if (!checkActivityState()) {
            return;
        }
        if (bbtBalance == null || progressBar == null) {
            return;
        }
        if (tvCheckBalance != null) {
            tvCheckBalance.setVisibility(View.GONE);
        }
        if (tvBalanceKey != null) {
            tvBalanceKey.setVisibility(View.VISIBLE);
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
        accountTransactionRecordAdapter.setOnItemSelectListener(onItemSelectListener);
        rvAccountTransactionRecord.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false);
        rvAccountTransactionRecord.setLayoutManager(linearLayoutManager);
        rvAccountTransactionRecord.setAdapter(accountTransactionRecordAdapter);
    }


    @Override
    public void initListener() {
        tvAddressKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((BaseActivity) activity).multipleClickToDo(3)) {
                    showSANIP(new ShowSANIPEvent(BCAASApplication.getTcpIp() + Constants.HTTP_COLON + BCAASApplication.getTcpPort(), false));
                }
            }
        });
        /**
         * 获取当前的未完成交易
         */
        RxView.clicks(tvUndone).throttleFirst(Constants.Time.sleep800, TimeUnit.MILLISECONDS)
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        checkDone = false;
                        switchTextStyle();
                        onRefreshTransactionRecord("tvUndone");

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogTool.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        /**
         * 获取当前的已完成交易
         */
        RxView.clicks(tvDone).throttleFirst(Constants.Time.sleep800, TimeUnit.MILLISECONDS)
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        checkDone = true;
                        switchTextStyle();
                        onRefreshTransactionRecord("tvDone");
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogTool.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        RxView.clicks(tvCheckBalance).throttleFirst(Constants.Time.sleep800, TimeUnit.MILLISECONDS)
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        if (activity != null) {
                            //展现币种选择界面
                            ((BaseActivity) activity).showCurrencyListPopWindow(Constants.From.CHECK_BALANCE);
                            //通知Activity重新请求数据
                            ((MainActivity) activity).requestBlockService(new RequestBlockServiceEvent(Constants.From.CHECK_BALANCE));

                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogTool.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
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
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            //判断如果当前没有币种，那么就暂时不能刷新数据
            if (StringTool.isEmpty(BCAASApplication.getBlockService())) {
                return;
            }
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
                        if (!isRequestTransactionRecord) {
                            isClearTransactionRecord = false;
                            isRequestTransactionRecord = true;
                            presenter.getAccountTransactions(nextObjectId, checkDone);
                            if (pbLoadingMore != null) {
                                pbLoadingMore.setVisibility(View.VISIBLE);
                            }

                        }
                    }
                }
            }
        }
    };

    /**
     * 根据是否是isDone来改变当前文本的样式
     */
    private void switchTextStyle() {
        //改变当前的tab文本样式显示
        if (tvUndone != null) {
            tvUndone.setTextColor(getResources().getColor(checkDone ? R.color.black_1d2124 : R.color.bcaas_color));
            tvUndone.setTextSize(checkDone ? 14 : 16);
        }
        if (tvDone != null) {
            tvDone.setTextColor(getResources().getColor(checkDone ? R.color.bcaas_color : R.color.black_1d2124));
            tvDone.setTextSize(checkDone ? 16 : 14);

        }
    }

    /**
     * 隐藏交易动作的文本
     *
     * @param isHide
     */
    private void hideTransactionActionText(boolean isHide) {
        //step 1:判断当前服务器是否是Bcaas SIT，如果不是，就一直隐藏
        if (StringTool.equals(ServerTool.getServerType(), Constants.ServerType.INTERNATIONAL_SIT)) {
            // 隐藏当前的文本
            if (tvUndone != null) {
                tvUndone.setVisibility(isHide ? View.INVISIBLE : View.VISIBLE);
            }
            if (tvDone != null) {
                tvDone.setVisibility(isHide ? View.INVISIBLE : View.VISIBLE);
            }
            if (vSymbol != null) {
                vSymbol.setVisibility(isHide ? View.INVISIBLE : View.VISIBLE);
            }
            if (tvTransactionRecordKey != null) {
                tvTransactionRecordKey.setText(getResources().getString(isHide ? R.string.transaction_records : R.string.transaction_records_symbol));
            }
        }

    }

    /*更新钱包余额*/
    @Subscribe
    public void UpdateWalletBalance(RefreshWalletBalanceEvent updateWalletBalanceEvent) {
        if (updateWalletBalanceEvent == null) {
            return;
        }
        Constants.EventSubscriber subscriber = updateWalletBalanceEvent.getSubscriber();
        if (subscriber == Constants.EventSubscriber.HOME || subscriber == Constants.EventSubscriber.ALL) {
            setBalance(BCAASApplication.getWalletBalance());
        }
    }


    /*币种重新选择返回*/
    private OnItemSelectListener onItemSelectListener = new OnItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type, String from) {
            if (type != null) {
                if (StringTool.equals(from, Constants.ACCOUNT_TRANSACTION)) {
                    TransactionDetailBean transactionDetailBean = (TransactionDetailBean) type;
                    LogTool.d(TAG, transactionDetailBean);
                    //跳轉交易詳情
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.TRANSACTION_STR, GsonTool.string(type));
                    intentToActivity(bundle, TransactionDetailActivity.class, false);

                }
            }
        }

        @Override
        public void changeItem(boolean isChange) {

        }
    };

    /**
     * 更新当前的余额信息根据切换的币种信息
     *
     * @param switchBlockServiceAndVerifyEvent
     */
    @Subscribe
    public void refreshBalanceBySwitchBlockService(SwitchBlockServiceAndVerifyEvent switchBlockServiceAndVerifyEvent) {
        if (switchBlockServiceAndVerifyEvent != null && activity != null) {
            setBalance(BCAASApplication.getWalletBalance());
            //判断是否是当前界面点击的变化
            String from = switchBlockServiceAndVerifyEvent.getFrom();
            LogTool.d(TAG, "SwitchBlockServiceAndVerifyEvent:" + lastBlockService);
            //比对当前的币种信息，如果当前的币种与上一次的不一致，那么就需要更新交易记录信息
            String currentBlockService = BCAASApplication.getBlockService();
            if (!StringTool.equals(lastBlockService, currentBlockService)) {
                lastBlockService = currentBlockService;
                //如果当前是需要verify区块服务，那么就刷新当前交易记录
                boolean isRefreshTransactionRecord = switchBlockServiceAndVerifyEvent.isRefreshTransactionRecord();
                if (isRefreshTransactionRecord) {
                    onRefreshTransactionRecord("SwitchBlockServiceAndVerifyEvent");
                }
                //判断当前币种下的「交易记录」是否有数据，如果有才清空当前数据
                if (ListTool.noEmpty(objects)) {
                    objects.clear();
                    accountTransactionRecordAdapter.notifyDataSetChanged();
                    hideAllTransactionView();
                }
            }

            //判断是否需要重新Verify
            boolean isVerify = switchBlockServiceAndVerifyEvent.isVerify();
            if (isVerify) {
                //重置余额
                BCAASApplication.resetWalletBalance();
                if (tvCheckBalance != null) {
                    tvCheckBalance.setVisibility(View.GONE);
                }
                if (tvBalanceKey != null) {
                    tvBalanceKey.setVisibility(View.VISIBLE);
                }
                if (bbtBalance != null) {
                    bbtBalance.setVisibility(View.INVISIBLE);
                }
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void onRefreshTransactionRecord(String from) {
        //如果当前没有币种，那么就不需要请求
        if (StringTool.isEmpty(BCAASApplication.getBlockService())) {
            showToast(getString(R.string.please_choose));
            hideTransactionActionText(true);
            return;
        }
        hideTransactionActionText(false);
        if (!isRequestTransactionRecord) {
            isRequestTransactionRecord = true;
            if (swipeRefreshLayout != null) {
                if (ListTool.isEmpty(objects)) {
                    //开始加载数据，直到结果返回设为false
                    swipeRefreshLayout.setRefreshing(true);
                }
            }
            LogTool.d(TAG, "onRefreshTransactionRecord:" + from);
            isClearTransactionRecord = true;
            presenter.getAccountTransactions(Constants.ValueMaps.DEFAULT_PAGINATION, checkDone);
        }
    }

    @Override
    public void getAccountDoneTCFailure(String message) {
        isRequestTransactionRecord = false;
        if (pbLoadingMore != null) {
            pbLoadingMore.setVisibility(View.GONE);
        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
        LogTool.i(TAG, MessageConstants.getAccountDoneTCFailure + message);
    }

    @Override
    public void getAccountDoneTCSuccess(List<Object> objectList) {
        isRequestTransactionRecord = false;
        if (pbLoadingMore != null) {
            pbLoadingMore.setVisibility(View.GONE);
        }
        if (swipeRefreshLayout != null) {
            //隐藏加载框
            swipeRefreshLayout.setRefreshing(false);
        }
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
        isRequestTransactionRecord = false;
        if (pbLoadingMore != null) {
            pbLoadingMore.setVisibility(View.GONE);
        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
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

    @Subscribe
    public void refreshTransactionRecord(RefreshTransactionRecordEvent refreshTransactionRecordEvent) {
        if (checkActivityState()) {
            onRefreshTransactionRecord("RefreshTransactionRecordEvent");
        }

    }

    @Subscribe
    public void showSANIP(ShowSANIPEvent showSANIPEvent) {
        if (showSANIPEvent != null) {
            String info = showSANIPEvent.getIp();
            boolean isMultipleClick = showSANIPEvent.isMultipleClick();
            if (tvShowIp != null) {
                boolean isShow = isMultipleClick;
                if (!isMultipleClick) {
                    isShow = tvShowIp.getVisibility() == View.GONE;
                }
                BCAASApplication.setShowSANIP(isShow);
                tvShowIp.setVisibility(isShow ? View.VISIBLE : View.GONE);
                if (isShow) {
                    if (StringTool.notEmpty(info)) {
                        tvShowIp.setText(info);
                    } else {
                        BCAASApplication.setShowSANIP(false);
                        tvShowIp.setVisibility(View.GONE);
                    }
                }
            }
        }
    }
}