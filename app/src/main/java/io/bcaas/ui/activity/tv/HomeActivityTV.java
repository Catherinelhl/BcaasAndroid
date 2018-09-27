package io.bcaas.ui.activity.tv;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.obt.qrcode.encoding.EncodingUtils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.adapter.TVAccountTransactionRecordAdapter;
import io.bcaas.base.BaseTVActivity;
import io.bcaas.base.BcaasApplication;
import io.bcaas.bean.LanguageSwitchingBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.event.LogoutEvent;
import io.bcaas.event.RefreshWalletBalanceEvent;
import io.bcaas.event.VerifyEvent;
import io.bcaas.http.tcp.TCPThread;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.presenter.MainFragmentPresenterImp;
import io.bcaas.tools.DateFormatTool;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.StringTool;
import io.bcaas.ui.contracts.MainFragmentContracts;
import io.bcaas.view.BcaasBalanceTextView;
import io.bcaas.view.TVTextView;
import io.bcaas.view.tv.FlyBroadLayout;
import io.bcaas.view.tv.MainUpLayout;
import io.bcaas.vo.PublicUnitVO;
import io.reactivex.disposables.Disposable;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/20
 * <p>
 * TV版「總攬」
 * 1: 檢查更新
 * 2:請求幣種
 * 3:根據請求到的幣種前去區塊驗證-verify
 * 4:請求交易紀錄
 * 5:執行TCP
 */
public class HomeActivityTV extends BaseTVActivity implements MainFragmentContracts.View {

    private String TAG = HomeActivityTV.class.getSimpleName();

    @BindView(R.id.iv_no_record)
    ImageView ivNoRecord;
    @BindView(R.id.tv_no_transaction_record)
    TextView tvNoTransactionRecord;
    @BindView(R.id.tv_title)
    TVTextView tvTitle;
    @BindView(R.id.tv_current_time)
    TextView tvCurrentTime;
    @BindView(R.id.ib_right)
    ImageButton ibRight;
    @BindView(R.id.rl_header)
    RelativeLayout rlHeader;
    @BindView(R.id.tv_currency_key)
    TextView tvCurrencyKey;
    @BindView(R.id.tv_currency)
    TextView tvCurrency;
    @BindView(R.id.tv_balance_key)
    TextView tvBalanceKey;
    @BindView(R.id.bbt_balance)
    BcaasBalanceTextView bbtBalance;
    @BindView(R.id.pb_balance)
    ProgressBar pbBalance;
    @BindView(R.id.tv_account_address_key)
    TextView tvAccountAddressKey;
    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;
    @BindView(R.id.tv_my_address)
    TextView tvMyAddress;
    @BindView(R.id.rv_account_transaction_record)
    RecyclerView rvAccountTransactionRecord;
    @BindView(R.id.tv_loading_more)
    TVTextView tvLoadingMore;
    @BindView(R.id.ll_title)
    LinearLayout llTitle;

    @BindView(R.id.block_base_mainup)
    FlyBroadLayout blockBaseMainup;
    @BindView(R.id.block_base_content)
    MainUpLayout blockBaseContent;
    // 得到當前的幣種
    List<PublicUnitVO> publicUnitVOList;


    private TVAccountTransactionRecordAdapter accountTransactionRecordAdapter;
    private List<Object> objects;
    private MainFragmentContracts.Presenter fragmentPresenter;
    //當前交易紀錄的頁數
    private String nextObjectId;
    //能否加載更多
    private boolean canLoadingMore;
    //是否需要清空當前交易紀錄,默認是false
    private boolean isClearTransactionRecord;
    // 紀錄「交易紀錄」顯示最後的位置
    private int lastVisibleItemPosition;


    @Override
    public boolean full() {
        return false;
    }

    @Override
    public int getContentView() {
        return R.layout.tv_activity_home;
    }

    @Override
    public void getArgs(Bundle bundle) {
    }

    @Override
    public void initViews() {
        initData();
        fragmentPresenter = new MainFragmentPresenterImp(this);
        objects = new ArrayList<>();
        publicUnitVOList = new ArrayList<>();
        //2:獲取幣種清單
        fragmentPresenter.getBlockServiceList();
        // 初始化顯示「交易紀錄」適配器
        initTransactionsAdapter();
        //显示月
        setBalance(BcaasApplication.getWalletBalance());
        initData();
        //先显示默认没有交易记录的布局
        hideTransactionRecordView();
        //对交易记录相关变量赋予初始值
        onRefreshTransactionRecord();


    }


    //对当前的余额进行赋值，如果当前没有读取到数据，那么就显示进度条，否则显示余额
    private void setBalance(String balance) {
        if (StringTool.isEmpty(balance)) {
            //隐藏显示余额的文本，展示进度条
            bbtBalance.setVisibility(View.INVISIBLE);
            pbBalance.setVisibility(View.VISIBLE);
        } else {
            pbBalance.setVisibility(View.GONE);
            bbtBalance.setVisibility(View.VISIBLE);
            bbtBalance.setBalance(balance);
        }
    }

    private void initData() {
        tvCurrentTime.setText(DateFormatTool.getCurrentTime());
        tvTitle.setText(getResources().getString(R.string.home));
        tvCurrency.setText(BcaasApplication.getBlockService());
        String address = BcaasApplication.getWalletAddress();
        if (StringTool.isEmpty(address)) {
            showToast(getResources().getString(R.string.account_data_error));
        } else {
            tvMyAddress.setText(address);
            makeQRCodeByAddress(address);
        }
    }

    private void initTransactionsAdapter() {
        accountTransactionRecordAdapter = new TVAccountTransactionRecordAdapter(this.context, objects, true);
        rvAccountTransactionRecord.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false);
        rvAccountTransactionRecord.setLayoutManager(linearLayoutManager);
        rvAccountTransactionRecord.setAdapter(accountTransactionRecordAdapter);
    }

    private void makeQRCodeByAddress(String address) {
        Bitmap qrCode = EncodingUtils.createQRCode(address, context.getResources().getDimensionPixelOffset(R.dimen.d200),
                context.getResources().getDimensionPixelOffset(R.dimen.d200), null, 0xfff1f1f1);
        ivQrCode.setImageBitmap(qrCode);
    }

    @Override
    public void initListener() {
        blockBaseContent.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                blockBaseMainup.setFocusView(newFocus, oldFocus, 1.2f);
            }
        });
        Disposable subscribe = RxView.clicks(tvCurrency)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    showTVCurrencyListPopWindow(onItemSelectListener, publicUnitVOList);
                });
        Disposable subscribeTitle = RxView.clicks(tvTitle)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    finish();
                });
        Disposable subscribeRight = RxView.clicks(ibRight)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    showTVLanguageSwitchDialog(onItemSelectListener);
                });
        Disposable subscribeLoadingMore = RxView.clicks(tvLoadingMore)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    isClearTransactionRecord = false;
                    fragmentPresenter.getAccountDoneTC(nextObjectId);
                });
        rvAccountTransactionRecord.addOnScrollListener(scrollListener);
    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }
            if (accountTransactionRecordAdapter != null) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItemPosition + 1 == accountTransactionRecordAdapter.getItemCount()) {
                    LogTool.d(TAG, MessageConstants.LOADING_MORE + canLoadingMore);

                    //发送网络请求获取更多数据
                    if (canLoadingMore) {
                        isClearTransactionRecord = false;
                        fragmentPresenter.getAccountDoneTC(nextObjectId);
                    }
                }
            }
        }
    };


    /*币种重新选择返回*/
    private OnItemSelectListener onItemSelectListener = new OnItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type) {
            if (type == null) {
                return;
            }
            //如果当前是「语言切换」
            if (type instanceof LanguageSwitchingBean) {
                switchLanguage(type);
            } else {
                /*显示币种*/
                tvCurrency.setText(type.toString());
                /*存储币种*/
                BcaasApplication.setBlockService(type.toString());
                /*重新verify，获取新的区块数据*/
                OttoTool.getInstance().post(new VerifyEvent());
                onRefreshTransactionRecord();
                /*重置余额*/
                BcaasApplication.resetWalletBalance();
                bbtBalance.setVisibility(View.INVISIBLE);
                pbBalance.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void changeItem(boolean isChange) {
        }
    };

    /*刷新當前「交易紀錄」*/
    private void onRefreshTransactionRecord() {
        isClearTransactionRecord = true;
        fragmentPresenter.getAccountDoneTC(Constants.ValueMaps.DEFAULT_PAGINATION);
    }

    @Override
    public void verifyFailure() {
        showToast(getResources().getString(R.string.data_acquisition_error));
    }

    @Override
    public void getBlockServicesListSuccess(List<PublicUnitVO> publicUnitVOList) {
        if (ListTool.noEmpty(publicUnitVOList)) {
            this.publicUnitVOList = publicUnitVOList;
            //存储当前的所有币种
            BcaasApplication.setPublicUnitVOList(this.publicUnitVOList);
        }
        checkVerify();
    }

    //3:檢查驗證
    private void checkVerify() {
        OttoTool.getInstance().post(new VerifyEvent());
    }

    @Override
    public void noBlockServicesList() {
        LogTool.d(TAG, MessageConstants.NO_BLOCK_SERVICE);
        checkVerify();
    }


    @Subscribe
    public void logoutEvent(LogoutEvent logoutEvent) {
        handler.post(() -> showBcaasSingleDialog(getString(R.string.warning),
                getString(R.string.please_login_again), () -> {
                    BcaasApplication.setKeepHttpRequest(false);
                    TCPThread.kill(true);
                    BcaasApplication.clearAccessToken();
                    intentToActivity(LoginActivityTV.class, true);
                }));


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

    /*没有交易记录*/
    private void hideTransactionRecordView() {
        ivNoRecord.setVisibility(View.VISIBLE);
        rvAccountTransactionRecord.setVisibility(View.GONE);
        tvNoTransactionRecord.setVisibility(View.VISIBLE);
        tvLoadingMore.setVisibility(View.GONE);
        llTitle.setVisibility(View.GONE);
    }

    /*显示交易记录*/
    private void showTransactionRecordView() {
        ivNoRecord.setVisibility(View.GONE);
        rvAccountTransactionRecord.setVisibility(View.VISIBLE);
        tvNoTransactionRecord.setVisibility(View.GONE);
        llTitle.setVisibility(View.VISIBLE);

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

    /*更新钱包余额*/
    @Subscribe
    public void refreshWalletBalance(RefreshWalletBalanceEvent refreshWalletBalanceEvent) {
        if (refreshWalletBalanceEvent == null) {
            return;
        }
        setBalance(BcaasApplication.getWalletBalance());
    }

    @Override
    public void showLoading() {
        if (!checkActivityState()) {
            return;
        }
        showLoadingDialog(getResources().getColor(R.color.orange_FC9003));
    }

    @Override
    public void hideLoading() {
        if (!checkActivityState()) {
            return;
        }
        hideLoadingDialog();
    }

}
