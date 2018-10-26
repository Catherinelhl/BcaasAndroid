package io.bcaas.ui.activity.tv;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.*;
import butterknife.BindView;
import com.jakewharton.rxbinding2.view.RxView;
import com.obt.qrcode.encoding.EncodingUtils;
import com.squareup.otto.Subscribe;
import io.bcaas.BuildConfig;
import io.bcaas.R;
import io.bcaas.adapter.TVAccountTransactionRecordAdapter;
import io.bcaas.base.BCAASApplication;
import io.bcaas.base.BaseTVActivity;
import io.bcaas.bean.TypeSwitchingBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.event.*;
import io.bcaas.gson.ResponseJson;
import io.bcaas.http.tcp.TCPThread;
import io.bcaas.listener.AdapterNotifyFinishListener;
import io.bcaas.listener.ObservableTimerListener;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.presenter.MainFragmentPresenterImp;
import io.bcaas.tools.*;
import io.bcaas.tools.gson.JsonTool;
import io.bcaas.ui.contracts.MainFragmentContracts;
import io.bcaas.view.BcaasBalanceTextView;
import io.bcaas.view.textview.TVTextView;
import io.bcaas.view.textview.TVWithStarTextView;
import io.bcaas.view.tv.FlyBroadLayout;
import io.bcaas.view.tv.MainUpLayout;
import io.bcaas.view.tv.TVButton;
import io.bcaas.vo.PublicUnitVO;
import io.reactivex.disposables.Disposable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    @BindView(R.id.tst_currency_key)
    TVWithStarTextView tstCurrencyKey;
    @BindView(R.id.tv_currency)
    TVTextView tvCurrency;
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
    @BindView(R.id.tv_toast)
    TextView tvToast;
    @BindView(R.id.rv_account_transaction_record)
    RecyclerView rvAccountTransactionRecord;
    @BindView(R.id.ll_title)
    LinearLayout llTitle;
    @BindView(R.id.sv_account_transaction_record)
    ScrollView svAccountTransactionRecord;

    @BindView(R.id.block_base_mainup)
    FlyBroadLayout blockBaseMainup;
    @BindView(R.id.block_base_content)
    MainUpLayout blockBaseContent;
    @BindView(R.id.ll_home)
    LinearLayout llHome;
    @BindView(R.id.rl_guide)
    RelativeLayout rlGuide;

    @BindView(R.id.btn_next)
    TVButton btnNext;
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
    //紀錄當前交易紀錄最後一條獲取焦點的position
    private int currentLastSelectPositionOfTransactionRecord;
    //二維碼渲染的前景色
    private int foregroundColorOfQRCode = 0x00000000;
    //二維碼渲染的背景色
    private int backgroundColorOfQRCode = 0xfff1f1f1;


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
        tstCurrencyKey.setTextWithStar(getResources().getString(R.string.token));
        fragmentPresenter = new MainFragmentPresenterImp(this);
        objects = new ArrayList<>();
        //2:獲取幣種清單
        fragmentPresenter.getBlockServiceList();
        // 初始化顯示「交易紀錄」適配器
        initTransactionsAdapter();
        //显示月
        setBalance(BCAASApplication.getWalletBalance());
        initData();
        //先显示默认没有交易记录的布局
        hideTransactionRecordView();
        //对交易记录相关变量赋予初始值
        onRefreshTransactionRecord();

        if (StringTool.notEmpty(BCAASApplication.getTcpIp())) {
            showTCPConnectIP(BCAASApplication.getTcpIp() + MessageConstants.REQUEST_COLON + BCAASApplication.getTcpPort());
        }
        initGuideView();
    }

    private void initGuideView() {

        String tag = Constants.Preference.GUIDE_TV_HOME_CURRENCY;
        boolean shown = BCAASApplication.getBooleanFromSP(tag);
        if (!shown || BuildConfig.DEBUG) {
            rlGuide.setVisibility(View.VISIBLE);
            llHome.setVisibility(View.GONE);
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BCAASApplication.setBooleanToSP(tag, true);
                    ObservableTimerTool.resetRequestFocus(observableTimerListener);
                    rlGuide.setVisibility(View.GONE);
                    llHome.setVisibility(View.VISIBLE);
                    llHome.setFocusable(true);
                    llHome.setFocusableInTouchMode(true);
                }
            });
        } else {
            rlGuide.setVisibility(View.GONE);
        }
    }

    private ObservableTimerListener observableTimerListener = new ObservableTimerListener() {
        @Override
        public void timeUp(String from) {
            if (StringTool.equals(from, Constants.TimerType.COUNT_DOWN_REFRESH_VIEW)) {
                tvTitle.requestFocus();
                tvTitle.setFocusable(true);
                tvTitle.setFocusableInTouchMode(true);
            }
        }
    };

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
        tvCurrency.setText(BCAASApplication.getBlockService());
        String address = BCAASApplication.getWalletAddress();
        if (StringTool.isEmpty(address)) {
            showToast(getResources().getString(R.string.account_data_error));
        } else {
            tvMyAddress.setText(address);
            makeQRCodeByAddress(address);
        }
    }

    private void initTransactionsAdapter() {
        accountTransactionRecordAdapter = new TVAccountTransactionRecordAdapter(this.context, objects, true);
        accountTransactionRecordAdapter.setAdapterNotifyFinishListener(adapterNotifyFinishListener);
        rvAccountTransactionRecord.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false);
        rvAccountTransactionRecord.setLayoutManager(linearLayoutManager);
        rvAccountTransactionRecord.setAdapter(accountTransactionRecordAdapter);
        rvAccountTransactionRecord.setItemAnimator(null);
    }

    private void makeQRCodeByAddress(String address) {
        Bitmap qrCode = EncodingUtils.createQRCode(address, context.getResources().getDimensionPixelOffset(R.dimen.d200),
                context.getResources().getDimensionPixelOffset(R.dimen.d200), null, foregroundColorOfQRCode, backgroundColorOfQRCode);
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
                    showTVCurrencySwitchDialog(onItemSelectListener);
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
                // 如果當前是最後一條
                int allCount = accountTransactionRecordAdapter.getItemCount();
                LogTool.d(TAG, allCount);
                //* The RecyclerView is not currently scrolling.（静止没有滚动）
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (lastVisibleItemPosition == allCount - 1) {
                        LogTool.d(TAG, MessageConstants.LOADING_MORE + canLoadingMore);

                        //发送网络请求获取更多数据
                        if (canLoadingMore) {
                            isClearTransactionRecord = false;
                            fragmentPresenter.getAccountDoneTC(nextObjectId);
                        }
                    }
                }
            }
        }
    };


    /*币种重新选择返回*/
    private OnItemSelectListener onItemSelectListener = new OnItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type, String from) {
            if (type == null) {
                return;
            }
            //如果当前是「语言切换」
            if (StringTool.equals(from, Constants.KeyMaps.LANGUAGE_SWITCH)) {
                hideTVLanguageSwitchDialog();
                switchLanguage(type);
            } else {
                //否則是幣種切換
                TypeSwitchingBean typeSwitchingBean = (TypeSwitchingBean) type;
                if (typeSwitchingBean == null) {
                    return;
                }
                //關閉當前Dialog
                hideTVCurrencySwitchDialog();
                String blockService = typeSwitchingBean.getType();
                /*显示币种*/
                tvCurrency.setText(blockService);
                /*存储币种*/
                BCAASApplication.setBlockService(blockService);
                /*重新verify，获取新的区块数据*/
                TCPThread.setActiveDisconnect(true);
                checkVerify();
                onRefreshTransactionRecord();
                /*重置余额*/
                BCAASApplication.resetWalletBalance();
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
    public void verifyFailure(String from) {
        showToast(getResources().getString(R.string.data_acquisition_error));
    }

    @Override
    public void getBlockServicesListSuccess(List<PublicUnitVO> publicUnitVOList) {
        checkVerify();
    }

    @Override
    public void getBlockServicesListFailure() {
        checkVerify();
    }

    //3:檢查驗證
    private void checkVerify() {
        /*切换当前的区块服务并且更新；重新verify，获取新的区块数据*/
        OttoTool.getInstance().post(new SwitchBlockServiceAndVerifyEvent(true, false));
    }

    @Override
    public void noBlockServicesList() {
        LogTool.d(TAG, MessageConstants.NO_BLOCK_SERVICE);
        checkVerify();
    }


    @Subscribe
    public void logoutEvent(LogoutEvent logoutEvent) {
        handler.post(() -> showTVLogoutSingleDialog());
    }

    @Override
    public void getAccountDoneTCFailure(String message) {
        LogTool.i(TAG, MessageConstants.getAccountDoneTCFailure + message);
    }

    @Override
    public void getAccountDoneTCSuccess(List<Object> objectList) {
        hideLoading();
        LogTool.d(TAG, MessageConstants.GET_ACCOUNT_DONE_TC_SUCCESS + objectList.size());
        showTransactionRecordView();
        if (isClearTransactionRecord) {
            this.objects.clear();
        }
        this.objects.addAll(objectList);
        accountTransactionRecordAdapter.addAll(objects);
    }

    private AdapterNotifyFinishListener adapterNotifyFinishListener = new AdapterNotifyFinishListener() {
        @Override
        public void notifyFinish(boolean isFinish) {
            if (isClearTransactionRecord) {
                return;
            }
            //發送消息，重新定位焦點
            handler.sendEmptyMessageDelayed(1, Constants.ValueMaps.sleepTime100);
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //判斷當前  currentLastSelectPositionOfTransactionRecord 是否大於0，如果是，就讓recycle view最後焦點停留在此位置
            if (currentLastSelectPositionOfTransactionRecord > 0) {
                RecyclerView.LayoutManager layoutManager = rvAccountTransactionRecord.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    View view = rvAccountTransactionRecord.getChildAt(currentLastSelectPositionOfTransactionRecord);
                    if (view != null) {
                        view.requestFocus();
                        view.setFocusable(true);
                        view.setFocusableInTouchMode(true);
                    }
                }
            }
        }
    };

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

    @Override
    public void noAccountDoneTC() {
        hideLoading();
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
        canLoadingMore = !StringTool.equals(nextObjectId, MessageConstants.NEXT_PAGE_IS_EMPTY);
    }

    /*更新钱包余额*/
    @Subscribe
    public void refreshWalletBalance(RefreshWalletBalanceEvent refreshWalletBalanceEvent) {
        if (refreshWalletBalanceEvent == null) {
            return;
        }
        setBalance(BCAASApplication.getWalletBalance());
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

    @Subscribe
    public void refreshTransactionRecord(RefreshTransactionRecordEvent refreshTransactionRecordEvent) {
        onRefreshTransactionRecord();
    }

    @Override
    protected void onDestroy() {
        hideTVLanguageSwitchDialog();
        super.onDestroy();
    }

    @Override
    public void httpExceptionStatus(ResponseJson responseJson) {
        if (responseJson == null) {
            return;
        }
        int code = responseJson.getCode();
        if (JsonTool.isTokenInvalid(code)) {
            showTVLogoutSingleDialog();
        } else {
            super.httpExceptionStatus(responseJson);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            //判断RecyclerView是否得到焦点
            if (rvAccountTransactionRecord.hasFocus()) {
                if (objects.size() > 0) {
                    //得到item的数量
                    int allItem = accountTransactionRecordAdapter.getItemCount();
                    RecyclerView.LayoutManager layoutManager = rvAccountTransactionRecord.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                        lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                    }
                    //如果當前選中最後一個就開始加載新數據
                    if (lastVisibleItemPosition == allItem - 1) {
                        LogTool.d(TAG, MessageConstants.LOADING_MORE + canLoadingMore);
                        //发送网络请求获取更多数据
                        if (canLoadingMore) {
                            currentLastSelectPositionOfTransactionRecord = lastVisibleItemPosition;
                            showLoading();
                            isClearTransactionRecord = false;
                            fragmentPresenter.getAccountDoneTC(nextObjectId);
                        }
                    }
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Subscribe
    public void refreshTCPConnectIP(RefreshTCPConnectIPEvent refreshTCPConnectIPEvent) {
        if (refreshTCPConnectIPEvent != null) {
            String ip = refreshTCPConnectIPEvent.getTcpconnectIP();
            showTCPConnectIP(ip);
        }
    }

    private void showTCPConnectIP(String IP) {
        if (BuildConfig.DEBUG) {
            if (tvToast != null) {
                tvToast.setVisibility(View.VISIBLE);
                tvToast.setText(IP);
            }
        }
    }

    @Subscribe
    public void showNotificationEvent(ShowNotificationEvent showNotificationEvent) {
        if (showNotificationEvent != null) {
            String blockService = showNotificationEvent.getBlockService();
            String amount = showNotificationEvent.getAmount();
            showNotificationToast(String.format(context.getString(R.string.receive_block_notification), blockService), amount + "\r" + blockService);
        }
    }
}
