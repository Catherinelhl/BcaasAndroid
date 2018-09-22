package io.bcaas.ui.activity.tv;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.obt.qrcode.encoding.EncodingUtils;
import com.squareup.otto.Subscribe;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.adapter.TVPopListCurrencyAdapter;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.event.BindServiceEvent;
import io.bcaas.event.LogoutEvent;
import io.bcaas.event.RefreshSendStatusEvent;
import io.bcaas.event.VerifyEvent;
import io.bcaas.gson.ResponseJson;
import io.bcaas.http.tcp.TCPThread;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.presenter.SendConfirmationPresenterImp;
import io.bcaas.tools.DateFormatTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.decimal.DecimalTool;
import io.bcaas.tools.ecc.KeyTool;
import io.bcaas.tools.ecc.WalletTool;
import io.bcaas.tools.regex.RegexTool;
import io.bcaas.ui.contracts.SendConfirmationContract;
import io.bcaas.view.BcaasBalanceTextView;
import io.bcaas.view.tv.FlyBroadLayout;
import io.bcaas.view.tv.MainUpLayout;
import io.bcaas.vo.PublicUnitVO;
import io.reactivex.disposables.Disposable;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/20
 * TV版發送頁面
 */
public class SendActivityTV extends BaseActivity implements SendConfirmationContract.View {

    private String TAG = SendActivityTV.class.getSimpleName();
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_current_time)
    TextView tvCurrentTime;
    @BindView(R.id.tv_logout)
    TextView tvLogout;
    @BindView(R.id.rl_header)
    RelativeLayout rlHeader;
    @BindView(R.id.tv_star_need)
    TextView tvStarNeed;
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
    @BindView(R.id.et_input_destination_address)
    EditText etInputDestinationAddress;
    @BindView(R.id.et_transaction_amount)
    EditText etTransactionAmount;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.block_base_mainup)
    FlyBroadLayout blockBaseMainup;
    @BindView(R.id.block_base_content)
    MainUpLayout blockBaseContent;
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.ll_show_currency)
    LinearLayout llShowCurrency;
    // 得到當前的幣種
    List<PublicUnitVO> publicUnitVOList;
    private SendConfirmationContract.Presenter presenter;
    private String currentStatus = Constants.ValueMaps.STATUS_DEFAULT;//得到当前的状态,默认

    @Override
    public boolean full() {
        return false;
    }

    @Override
    public int getContentView() {
        return R.layout.tv_activity_send;
    }

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public void initViews() {
        presenter = new SendConfirmationPresenterImp(this);
        //获取所有的清单
        publicUnitVOList = WalletTool.getPublicUnitVO();
        initData();
    }

    private void initData() {
        // TODO: 2018/9/22 暂时先默认一个账户
        etInputDestinationAddress.setText("1DgmLGA3tXQLbp6pJBZYyZ8PjhpG6xMtmY");
        tvCurrentTime.setText(DateFormatTool.getCurrentTime());
        tvTitle.setText(getResources().getString(R.string.send));
        setBalance(BcaasApplication.getWalletBalance());
        tvCurrency.setText(BcaasApplication.getBlockService());
        String address = BcaasApplication.getWalletAddress();
        if (StringTool.isEmpty(address)) {
            showToast(getResources().getString(R.string.account_data_error));
        } else {
            tvMyAddress.setText(address);
            makeQRCodeByAddress(address);
        }
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

    private void makeQRCodeByAddress(String address) {
        Bitmap qrCode = EncodingUtils.createQRCode(address, context.getResources().getDimensionPixelOffset(R.dimen.d200),
                context.getResources().getDimensionPixelOffset(R.dimen.d200), null, 0xffffffff);
        ivQrCode.setImageBitmap(qrCode);
    }

    @Override
    public void initListener() {
        Disposable subscribe = RxView.clicks(tvCurrency)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    isShowCurrencyListView(true);
                    TVPopListCurrencyAdapter adapter = new TVPopListCurrencyAdapter(context, publicUnitVOList);
                    adapter.setOnItemSelectListener(onItemSelectListener);
                    rvList.setAdapter(adapter);
                    rvList.setHasFixedSize(true);
                    rvList.setLayoutManager(new LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false));

                });
        blockBaseContent.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                blockBaseMainup.setFocusView(newFocus, oldFocus, 1.2f);
            }
        });
        Disposable subscribeSend = RxView.clicks(btnSend)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    hideSoftKeyboard();
                    /*点击发送，本地做一些网络请求前的规范判断*/
                    String amount = etTransactionAmount.getText().toString();
                    /*去掉地址里面的空格清空，以防在验证地址格式的时候，报异常情况*/
                    String destinationWallet = RegexTool.replaceBlank(etInputDestinationAddress.getText().toString());
                    /*1：检测当前地址长度*/
                    if (StringTool.isEmpty(destinationWallet)) {
                        showToast(getResources().getString(R.string.the_address_of_receiving_account_is_empty));
                        return;
                    }
                    /*2：检测当前地址是否有效*/
                    if (!KeyTool.validateBitcoinAddress(destinationWallet)) {
                        showToast(getResources().getString(R.string.address_format_error));
                        return;
                    }
                    /*3：检测当前输入交易地址是否是自己*/
                    if (StringTool.equals(destinationWallet, BcaasApplication.getWalletAddress())) {
                        showToast(getResources().getString(R.string.sending_wallet_same_as_receiving_wallet));
                        return;
                    }
                    /*4：检测交易数额长度*/
                    if (StringTool.isEmpty(amount)) {
                        showToast(getResources().getString(R.string.please_enter_transaction_amount));
                        return;
                    }
                    /*5：判断余额是否获取成功*/
                    String balance = BcaasApplication.getWalletBalance();
                    if (StringTool.isEmpty(balance)) {
                        showToast(getResources().getString(R.string.unable_to_trade_at_present));
                        return;
                    }
                    /*6：判断余额是否>0*/
                    if (StringTool.equals(balance, "0")) {
                        showToast(getResources().getString(R.string.insufficient_balance));
                        return;
                    }
                    /*7：判断余额是否足够发送*/
                    if (StringTool.equals(DecimalTool.calculateFirstSubtractSecondValue(balance, amount), MessageConstants.NO_ENOUGH_BALANCE)) {
                        showToast(getResources().getString(R.string.insufficient_balance));
                        return;
                    }
                    //檢查當前TCP的狀態
                    if (TCPThread.keepAlive) {
                        lockView(true);
                        presenter.checkVerify();
                    } else {
                        TCPThread.kill(true);
                        //進行重新連接
                        OttoTool.getInstance().post(new BindServiceEvent(true));
                    }

                });
    }

    /*币种重新选择返回*/
    private OnItemSelectListener onItemSelectListener = new OnItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type) {
            if (type != null) {
                isShowCurrencyListView(false);
                /*显示币种*/
                tvCurrency.setText(type.toString());
                /*存储币种*/
                BcaasApplication.setBlockService(type.toString());
                /*重新verify，获取新的区块数据*/
                OttoTool.getInstance().post(new VerifyEvent());
                /*重置余额*/
                BcaasApplication.resetWalletBalance();
                bbtBalance.setVisibility(View.INVISIBLE);
                pbBalance.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public void lockView(boolean lock) {
        currentStatus = lock ? Constants.ValueMaps.STATUS_SEND : Constants.ValueMaps.STATUS_DEFAULT;
        BcaasApplication.setTransactionAmount(etTransactionAmount.getText().toString());
        BcaasApplication.setDestinationWallet(etInputDestinationAddress.getText().toString());
    }

    @Override
    public void verifySuccess(boolean isReset) {
        LogTool.d(TAG, MessageConstants.VERIFY_SUCCESS + isReset);
        super.verifySuccess(isReset);
        if (TCPThread.keepAlive) {
            //验证成功，开始请求最新余额
            lockView(true);
            presenter.getLatestBlockAndBalance();
        } else {
            //將其狀態設為默認
            currentStatus = Constants.ValueMaps.STATUS_DEFAULT;
            TCPThread.kill(true);
            //進行重新連接
            OttoTool.getInstance().post(new BindServiceEvent(true));
        }

    }

    @Override
    public void onBackPressed() {
        if (StringTool.equals(currentStatus, Constants.ValueMaps.STATUS_SEND)) {
            showToast(getString(R.string.on_transaction));
        } else {
            super.onBackPressed();
        }
    }


    private void canNotExit() {
        if (StringTool.equals(currentStatus, Constants.ValueMaps.STATUS_SEND)) {
            showToast(getString(R.string.on_transaction));
        } else {
            finish();
        }
    }

    @Override
    public void httpGetWalletWaitingToReceiveBlockSuccess() {
        LogTool.d(TAG, MessageConstants.SUCCESS_GET_WALLET_RECEIVE_BLOCK);

    }

    @Override
    public void httpGetWalletWaitingToReceiveBlockFailure() {
        lockView(false);
        showToast(getResources().getString(R.string.data_acquisition_error));
        LogTool.d(TAG, MessageConstants.FAILURE_GET_WALLET_RECEIVE_BLOCK);
    }

    @Override
    public void resetAuthNodeSuccess() {
        //重新連接
        OttoTool.getInstance().post(new BindServiceEvent(true));
    }

    @Override
    public void noData() {
        showToast(getResources().getString(R.string.account_data_error));

    }

    @Subscribe
    public void RefreshSendStatus(RefreshSendStatusEvent refreshSendStatusEvent) {
        if (refreshSendStatusEvent == null) {
            return;
        }
        boolean isUnlock = refreshSendStatusEvent.isUnLock();
        if (isUnlock) {
            currentStatus = Constants.ValueMaps.STATUS_DEFAULT;
            LogTool.d(TAG, getResources().getString(R.string.transaction_has_successfully));
        } else {
            LogTool.d(TAG, getResources().getString(R.string.transaction_has_failure));
            finish();
        }

        LogTool.d(TAG, MessageConstants.SEND_TRANSACTION_SATE + isUnlock);

    }


    @Override
    public void verifyFailure() {
        lockView(false);
        //验证失败，需要重新拿去AN的信息
        showToast(getResources().getString(R.string.data_acquisition_error));
        finish();

    }

    @Override
    public void failure(String message) {
        super.failure(message);
        verifyFailure();
    }

    @Override
    public void passwordError() {
        lockView(false);
        showToast(getResources().getString(R.string.password_error));
    }

    @Override
    public void responseDataError() {
        showToast(getResources().getString(R.string.data_acquisition_error));

    }

    @Override
    public void noNetWork() {
        showToast(getResources().getString(R.string.network_not_reachable));
    }

    @Override
    public void httpExceptionStatus(ResponseJson responseJson) {
        if (responseJson == null) {
            return;
        }
        int code = responseJson.getCode();
        if (code == MessageConstants.CODE_3006
                || code == MessageConstants.CODE_3008) {
            showBcaasSingleDialog(getString(R.string.warning),
                    getString(R.string.please_login_again), () -> {
                        finish();
                        OttoTool.getInstance().post(new LogoutEvent());
                    });
        } else {
            super.httpExceptionStatus(responseJson);
        }
    }

    /* 是否展示币种的list*/
    private void isShowCurrencyListView(boolean isShow) {
        llShowCurrency.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}
