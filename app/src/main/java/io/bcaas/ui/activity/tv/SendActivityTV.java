package io.bcaas.ui.activity.tv;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.obt.qrcode.encoding.EncodingUtils;
import com.squareup.otto.Subscribe;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.bcaas.BuildConfig;
import io.bcaas.R;
import io.bcaas.base.BaseTVActivity;
import io.bcaas.base.BcaasApplication;
import io.bcaas.bean.LanguageSwitchingBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.event.BindServiceEvent;
import io.bcaas.event.LogoutEvent;
import io.bcaas.event.RefreshSendStatusEvent;
import io.bcaas.event.RefreshWalletBalanceEvent;
import io.bcaas.event.VerifyEvent;
import io.bcaas.gson.ResponseJson;
import io.bcaas.http.tcp.TCPThread;
import io.bcaas.listener.AmountEditTextFilter;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.presenter.SendConfirmationPresenterImp;
import io.bcaas.tools.DateFormatTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.decimal.DecimalTool;
import io.bcaas.tools.ecc.KeyTool;
import io.bcaas.tools.regex.RegexTool;
import io.bcaas.ui.contracts.SendConfirmationContract;
import io.bcaas.view.BcaasBalanceTextView;
import io.bcaas.view.textview.TVTextView;
import io.bcaas.view.textview.TVWithStarTextView;
import io.bcaas.view.tv.FlyBroadLayout;
import io.bcaas.view.tv.MainUpLayout;
import io.reactivex.disposables.Disposable;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/20
 * TV版發送頁面
 */
public class SendActivityTV extends BaseTVActivity implements SendConfirmationContract.View {
    private String TAG = SendActivityTV.class.getSimpleName();
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

    //发送确认密码页面
    @BindView(R.id.ll_set_transaction_info)
    LinearLayout llSetTransactionInfo;
    @BindView(R.id.tv_transaction_detail)
    TextView tvTransactionDetail;
    @BindView(R.id.tv_receive_account_key)
    TextView tvReceiveAccountKey;
    @BindView(R.id.tv_destination_wallet)
    TextView tvDestinationWallet;
    @BindView(R.id.tst_receive_account_address_key)
    TVWithStarTextView tstReceiveAccountAddressKey;
    @BindView(R.id.tst_transaction_amount_key)
    TVWithStarTextView tstTransactionAmountKey;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.cb_pwd)
    CheckBox cbPwd;
    @BindView(R.id.v_password_line)
    View vPasswordLine;
    @BindView(R.id.ll_password_key)
    LinearLayout llPasswordKey;
    @BindView(R.id.ll_send_info)
    LinearLayout llSendInfo;

    // 得到當前的幣種
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
        //初始化所有輸入框的初始狀態，设置弹出的键盘类型为空
        etInputDestinationAddress.setInputType(EditorInfo.TYPE_NULL);
        etTransactionAmount.setInputType(EditorInfo.TYPE_NULL);
        etPassword.setInputType(EditorInfo.TYPE_NULL);
        tstTransactionAmountKey.setTextWithStar(getResources().getString(R.string.transaction_amount));
        tstReceiveAccountAddressKey.setTextWithStar(getResources().getString(R.string.receive_account));
        tstCurrencyKey.setTextWithStar(getResources().getString(R.string.token));
        initData();
        setEditHintTextSize();
    }

    /*设置输入框的hint的大小而不影响text size*/
    private void setEditHintTextSize() {
        SpannableString spannableString = new SpannableString(getResources().getString(R.string.please_enter_transaction_amount));//定义hint的值
        AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(12, true);//设置字体大小 true表示单位是sp
        spannableString.setSpan(absoluteSizeSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        etTransactionAmount.setHint(new SpannedString(spannableString));
    }

    private void initData() {
        etTransactionAmount.setFilters(new InputFilter[]{new AmountEditTextFilter().setDigits(8)});
        // TODO: 2018/9/22 暂时先默认一个账户
        if (BuildConfig.DEBUG) {
            etInputDestinationAddress.setText("1DgmLGA3tXQLbp6pJBZYyZ8PjhpG6xMtmY");
        }
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
                context.getResources().getDimensionPixelOffset(R.dimen.d200), null, 0x00000000, 0xfff1f1f1);
        ivQrCode.setImageBitmap(qrCode);
    }

    @Override
    public void initListener() {
        setEditTextInputMethodManager(etInputDestinationAddress, false);
        setEditTextInputMethodManager(etTransactionAmount, false);
        setEditTextInputMethodManager(etPassword, true);
        cbPwd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String text = etPassword.getText().toString();
            if (StringTool.isEmpty(text)) {
                return;
            }
            etPassword.setInputType(isChecked ?
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);//设置当前私钥显示不可见

        });
        Disposable subscribeRight = RxView.clicks(ibRight)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    showTVLanguageSwitchDialog(onItemSelectListener);
                });
        Disposable subscribeTitle = RxView.clicks(tvTitle)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    finish();
                });
        Disposable subscribe = RxView.clicks(tvCurrency)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    showTVCurrencyListPopWindow(onItemSelectListener);

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
                    // 判断当前是「发送」还是「确定」；前者需要跳转到输入密码页面；后者开始网络请求「发送」
                    String btnString = btnSend.getText().toString();
                    if (btnString.equals(getResources().getString(R.string.send))) {
                        /*点击「发送」，本地做一些网络请求前的规范判断*/
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
                        // 隐藏当前输入交易的信息视图
                        llSetTransactionInfo.setVisibility(View.GONE);
                        //显示当前需要输入密码的视图
                        llSendInfo.setVisibility(View.VISIBLE);
                        // 得到交易信息，对下一个视图进行赋值
                        String transactionAmount = etTransactionAmount.getText().toString();
                        // 设置按钮变换为「确定」
                        btnSend.setText(getResources().getString(R.string.confirm));
                        // 设置目标地址
                        tvDestinationWallet.setHint(destinationWallet);
                        tvTransactionDetail.setText(String.format(getString(R.string.tv_transaction_detail), DecimalTool.transferDisplay(transactionAmount), BcaasApplication.getBlockService()));
                    } else {
                        //檢查當前TCP的狀態
                        if (TCPThread.keepAlive) {
                            lockView(true);
                            presenter.checkVerify();
                        } else {
                            TCPThread.kill(true);
                            //進行重新連接
                            OttoTool.getInstance().post(new BindServiceEvent(true));
                        }
                    }

                });
    }

    //设置输入框的软键盘弹出
    private void setEditTextInputMethodManager(EditText editText, boolean isPassword) {
        editText.setOnClickListener(v -> {
            if (isPassword) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
            }
            String content = editText.getText().toString();
            if (StringTool.notEmpty(content)) {
                editText.setSelection(content.length());
            }
            editText.requestFocus();
            InputMethodManager inputMethodManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(editText, 0);
        });
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String content = editText.getText().toString();
                if (StringTool.isEmpty(content) || !isPassword) {
                    editText.setInputType(InputType.TYPE_NULL);

                }
            }

        });
    }

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
            if (BuildConfig.DEBUG) {
                currentStatus = Constants.ValueMaps.STATUS_DEFAULT;
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void httpGetWalletWaitingToReceiveBlockSuccess() {
        LogTool.d(TAG, MessageConstants.SUCCESS_GET_WALLET_RECEIVE_BLOCK);

    }

    @Override
    public void getBalanceFailure() {
        LogTool.d(TAG, MessageConstants.FAILURE_GET_WALLET_GETBALANCE);
    }

    @Override
    public void getBalanceSuccess() {
        LogTool.d(TAG, MessageConstants.SUCCESS_GET_WALLET_GETBALANCE);

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
        etTransactionAmount.setText("");
        hideLoadingDialog();
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
    public void connectFailure() {
        super.connectFailure();
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
            showTVLogoutSingleDialog();
        } else {
            super.httpExceptionStatus(responseJson);
        }
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

    /*更新钱包余额*/
    @Subscribe
    public void refreshWalletBalance(RefreshWalletBalanceEvent refreshWalletBalanceEvent) {
        if (refreshWalletBalanceEvent == null) {
            return;
        }
        setBalance(BcaasApplication.getWalletBalance());
    }

    @Override
    protected void onDestroy() {
        hideTVLanguageSwitchDialog();
        super.onDestroy();
    }

    @Subscribe
    public void logoutEvent(LogoutEvent logoutEvent) {
        handler.post(() -> showTVLogoutSingleDialog());
    }


}
