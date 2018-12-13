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
import io.bcaas.base.BaseFragment;
import io.bcaas.bean.TransactionDetailBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.event.RefreshTransactionRecordEvent;
import io.bcaas.event.RefreshWalletBalanceEvent;
import io.bcaas.event.ShowSANIPEvent;
import io.bcaas.event.SwitchBlockServiceAndVerifyEvent;
import io.bcaas.http.tcp.TCPThread;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.presenter.MainFragmentPresenterImp;
import io.bcaas.tools.DensityTool;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.ecc.WalletTool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.ui.activity.MainActivity;
import io.bcaas.ui.activity.TransactionDetailActivity;
import io.bcaas.ui.contracts.MainFragmentContracts;
import io.bcaas.view.guide.GuideView;
import io.bcaas.view.textview.BcaasBalanceTextView;
import io.bcaas.vo.PublicUnitVO;
import io.reactivex.disposables.Disposable;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * Fragment:「首页」
 */
public class MainFragment extends BaseFragment implements MainFragmentContracts.View {
    @BindView(R.id.tv_show_ip)
    TextView tvShowIp;
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
    //是否正在请求交易记录
    private boolean isRequestTransactionRecord;

    //首页「复制」按钮教学页面
    private GuideView guideViewCopy;
    //首页「余额」可点击教学页面
    private GuideView guideViewBalance;
    //首页「币种」可切换教学页面
    private GuideView guideViewCurrency;

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
        presenter.getBlockServiceList(Constants.from.INIT_VIEW);
        objects = new ArrayList<>();
        tvMyAccountAddressValue.setText(BCAASApplication.getWalletAddress());
        initTransactionsAdapter();
        setBalance(BCAASApplication.getWalletBalance());
        setCurrency();
        hideAllTransactionView();
        onRefreshTransactionRecord("initViews");
        swipeRefreshLayout.setColorSchemeResources(
                R.color.button_right_color,
                R.color.button_right_color

        );
        // 設置背景顏色
//        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(context.getResources().getColor(R.color.transparent));
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        initGuideViewNew();
    }

    public void initGuideViewNew() {
        initCopyGuideView();
        initBalanceGuideView();
        initCurrencyGuideView();
    }

    private void initCurrencyGuideView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.help_view_main, null);
        LinearLayout linearLayout = view.findViewById(R.id.ll_guide);
        linearLayout.setGravity(Gravity.LEFT);
        TextView textView = view.findViewById(R.id.tv_content);
        textView.setText(context.getResources().getString(R.string.touch_can_change_blockservice));
        ImageView imageView = view.findViewById(R.id.iv_gesture);
        imageView.setImageResource(R.drawable.icon_help_arrow_two);
        Button button = view.findViewById(R.id.btn_next);
        button.setText(context.getResources().getString(R.string.yes));
        guideViewCurrency = GuideView.Builder
                .newInstance(getActivity())
                .setTargetView(llSelectCurrency)//设置目标
                .setIsDraw(true)
                .setCustomGuideView(view)
                .setDirction(GuideView.Direction.LEFT_ALIGN_BOTTOM)
                .setShape(GuideView.MyShape.RECTANGULAR)
                .setRadius(DensityTool.dip2px(BCAASApplication.context(), 6))
                .setBgColor(getResources().getColor(R.color.black80))
                .build();
        guideViewCurrency.setOnClickListener(v -> guideViewCurrency.hide());

        button.setOnClickListener(v -> guideViewCurrency.hide());
    }

    private void initCopyGuideView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.help_view_main, null);
        LinearLayout linearLayout = view.findViewById(R.id.ll_guide);
        linearLayout.setGravity(Gravity.RIGHT);
        TextView textView = view.findViewById(R.id.tv_content);
        textView.setText(context.getResources().getString(R.string.touch_copy_your_wallet_address));
        ImageView imageView = view.findViewById(R.id.iv_gesture);
        imageView.setImageResource(R.drawable.icon_help_arrow_one);
        Button button = view.findViewById(R.id.btn_next);
        button.setText(context.getResources().getString(R.string.next));
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
            guideViewBalance.show(Constants.Preference.GUIDE_MAIN_BALANCE);
            bbtBalance.performClick();
        });
        button.setOnClickListener(v -> {
            guideViewCopy.hide();
            guideViewBalance.show(Constants.Preference.GUIDE_MAIN_BALANCE);
            bbtBalance.performClick();
        });


    }

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
        button.setText(context.getResources().getString(R.string.next));
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
            guideViewCurrency.show(Constants.Preference.GUIDE_MAIN_CURRENCY);
        });
        button.setOnClickListener(v -> {
            guideViewBalance.hide();
            guideViewCurrency.show(Constants.Preference.GUIDE_MAIN_CURRENCY);
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
        accountTransactionRecordAdapter.setOnItemSelectListener(onItemSelectListener);
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
                    //重新获取「getList」币种信息，以防服务器中途 add or delete currency
                    presenter.getBlockServiceList(Constants.from.SELECT_CURRENCY);
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
                        if (!isRequestTransactionRecord) {
                            isClearTransactionRecord = false;
                            isRequestTransactionRecord = true;
                            presenter.getAccountDoneTC(nextObjectId);
                        }
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
                if (StringTool.equals(from, Constants.ACCOUNT_TRANSACTION)) {
                    TransactionDetailBean transactionDetailBean = (TransactionDetailBean) type;
                    LogTool.d(TAG, transactionDetailBean);
                    //跳轉交易詳情
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.TRANSACTION_STR, GsonTool.string(type));
                    intentToActivity(bundle, TransactionDetailActivity.class, false);

                } else {
                    //显示币种
                    tvCurrency.setText(type.toString());
                    //比较当前选择的币种是否和现在已经有的币种一致。如果一致，就不做重新请求刷新操作
                    if (!StringTool.equals(type.toString(), BCAASApplication.getBlockService())) {
                        //判断当前币种下的「交易记录」是否有数据，如果有才清空当前数据
                        if (ListTool.noEmpty(objects)) {
                            objects.clear();
                            accountTransactionRecordAdapter.notifyDataSetChanged();
                            hideAllTransactionView();
                        }
                        //存储币种
                        BCAASApplication.setBlockService(type.toString());
                    }
                    //将TCP连接断开方式设置为主动断开，避免此时因为主动断开引起的读取异常而开始重新reset SAN信息
                    TCPThread.setActiveDisconnect(true);
                    //重新Verify
                    verify();
                    //请求「交易记录」数据
                    onRefreshTransactionRecord("onItemSelect");
                    //重置余额
                    BCAASApplication.resetWalletBalance();
                    if (bbtBalance != null) {
                        bbtBalance.setVisibility(View.INVISIBLE);
                    }
                    if (progressBar != null) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        @Override
        public void changeItem(boolean isChange) {

        }
    };

    private void onRefreshTransactionRecord(String from) {
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

            presenter.getAccountDoneTC(Constants.ValueMaps.DEFAULT_PAGINATION);
        }
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
        isRequestTransactionRecord = false;
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
        LogTool.i(TAG, MessageConstants.getAccountDoneTCFailure + message);
    }

    @Override
    public void getAccountDoneTCSuccess(List<Object> objectList) {
        isRequestTransactionRecord = false;
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


    @Override
    public void getBlockServicesListSuccess(String from, List<PublicUnitVO> publicUnitVOList) {
        setCurrency();
        //判断当前from是从那里返回的
        if (StringTool.equals(from, Constants.from.SELECT_CURRENCY)) {
            //如果当前是「币种」点击请求的，那么就需要弹出币种弹框
        } else {
            // 否则就是进入页面初始化所得，那么直接开始验证就可以了
            verify();
        }
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
    public void getBlockServicesListFailure(String from) {
        setCurrency();
        //判断当前from是从那里返回的
        if (StringTool.equals(from, Constants.from.SELECT_CURRENCY)) {
            //如果当前是「币种」点击请求的，那么就需要弹出币种弹框
        } else {
            // 否则就是进入页面初始化所得，那么直接开始验证就可以了
            verify();
        }
    }

    @Override
    public void noBlockServicesList(String from) {
        LogTool.d(TAG, MessageConstants.NO_BLOCK_SERVICE);
        //判断当前from是从那里返回的
        if (StringTool.equals(from, Constants.from.SELECT_CURRENCY)) {
            //如果当前是「币种」点击请求的，那么就需要弹出币种弹框
        } else {
            // 否则就是进入页面初始化所得，那么直接开始验证就可以了
            verify();
        }
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
                tvShowIp.setVisibility(isShow ? View.VISIBLE : View.GONE);
                if (isShow) {
                    if (StringTool.notEmpty(info)) {
                        tvShowIp.setText(info);
                    } else {
                        tvShowIp.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accountTransactionRecordAdapter != null && ListTool.noEmpty(objects)) {
            accountTransactionRecordAdapter.notifyDataSetChanged();
        }
    }
}