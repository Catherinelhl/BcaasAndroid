package io.bcaas.ui.activity.tv;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextWatcher;
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
import io.bcaas.base.BCAASApplication;
import io.bcaas.base.BaseHttpPresenterImp;
import io.bcaas.base.BaseTVActivity;
import io.bcaas.bean.TypeSwitchingBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.event.*;
import io.bcaas.gson.ResponseJson;
import io.bcaas.http.tcp.TCPThread;
import io.bcaas.listener.AmountEditTextFilter;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.tools.DateFormatTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.decimal.DecimalTool;
import io.bcaas.tools.ecc.KeyTool;
import io.bcaas.tools.gson.JsonTool;
import io.bcaas.tools.regex.RegexTool;
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
public class SendActivityTV extends BaseTVActivity {
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
    @BindView(R.id.rl_set_transaction_info)
    RelativeLayout rlSetTransactionInfo;
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
    @BindView(R.id.tv_amount_hint)
    TextView tvAmountHint;
    @BindView(R.id.tv_toast)
    TextView tvToast;

    // 得到當前的幣種
    private BaseHttpPresenterImp presenter;
    private String currentStatus = Constants.ValueMaps.STATUS_DEFAULT;//得到当前的状态,默认
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
        return R.layout.tv_activity_send;
    }

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public void initViews() {
        presenter = new BaseHttpPresenterImp(this);
        //初始化所有輸入框的初始狀態，设置弹出的键盘类型为空
        etInputDestinationAddress.setInputType(EditorInfo.TYPE_NULL);
        etTransactionAmount.setInputType(EditorInfo.TYPE_NULL);
        etPassword.setInputType(EditorInfo.TYPE_NULL);
        tstTransactionAmountKey.setTextWithStar(getResources().getString(R.string.transaction_amount));
        tstReceiveAccountAddressKey.setTextWithStar(getResources().getString(R.string.receive_account));
        tstCurrencyKey.setTextWithStar(getResources().getString(R.string.token));
        initData();
        if (StringTool.notEmpty(BCAASApplication.getTcpIp())) {
            showTCPConnectIP(BCAASApplication.getTcpIp() + MessageConstants.REQUEST_COLON + BCAASApplication.getTcpPort());
        }
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
//        if (BuildConfig.DEBUG) {
        etInputDestinationAddress.setText("1DgmLGA3tXQLbp6pJBZYyZ8PjhpG6xMtmY");
//        }
        tvCurrentTime.setText(DateFormatTool.getCurrentTime());
        tvTitle.setText(getResources().getString(R.string.send));
        setBalance(BCAASApplication.getWalletBalance());
        tvCurrency.setText(BCAASApplication.getBlockService());
        String address = BCAASApplication.getWalletAddress();
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
                context.getResources().getDimensionPixelOffset(R.dimen.d200), null, foregroundColorOfQRCode, backgroundColorOfQRCode);
        ivQrCode.setImageBitmap(qrCode);
    }

    @Override
    public void initListener() {
        etTransactionAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvAmountHint.setVisibility(StringTool.notEmpty(s.toString()) ? View.INVISIBLE : View.VISIBLE);
            }
        });
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
                    showTVCurrencySwitchDialog(onItemSelectListener);
                    // 重置当前界面数据
                    if (etTransactionAmount != null) {
                        etTransactionAmount.setText(MessageConstants.Empty);
                    }
                    if (etPassword != null) {
                        etPassword.setText(MessageConstants.Empty);
                    }

                    // TODO: 2018/10/10
//                    if (etInputDestinationAddress != null) {
//                        etInputDestinationAddress.setText("");
//                    }
                    if (btnSend != null) {
                        btnSend.setText(getResources().getString(R.string.send));
                    }
                    currentStatus = Constants.ValueMaps.STATUS_DEFAULT;
                    //刷新當前的界面，清空控件的所有內容
                    showInputPasswordForSendView(false);

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
                        if (StringTool.equals(destinationWallet, BCAASApplication.getWalletAddress())) {
                            showToast(getResources().getString(R.string.sending_wallet_same_as_receiving_wallet));
                            return;
                        }
                        /*4：检测交易数额长度*/
                        if (StringTool.isEmpty(amount)) {
                            showToast(getResources().getString(R.string.please_enter_transaction_amount));
                            return;
                        }
                        /*5：判断余额是否获取成功*/
                        String balance = BCAASApplication.getWalletBalance();
                        if (StringTool.isEmpty(balance)) {
                            showToast(getResources().getString(R.string.unable_to_trade_at_present));
                            return;
                        }
                        /*6：判断余额是否>0*/
                        if (StringTool.equals(balance, "0") || DecimalTool.compareFirstEqualSecondValue(amount, "0")) {
                            showToast(getResources().getString(R.string.transaction_cannot_be_zero));
                            return;
                        }
                        /*7：判断余额是否足够发送*/
                        if (StringTool.equals(DecimalTool.calculateFirstSubtractSecondValue(balance, amount), MessageConstants.NO_ENOUGH_BALANCE)) {
                            showToast(getResources().getString(R.string.insufficient_balance));
                            return;
                        }
                        // 得到交易信息，对下一个视图进行赋值
                        String transactionAmount = etTransactionAmount.getText().toString();
                        // 设置按钮变换为「确定」
                        btnSend.setText(getResources().getString(R.string.confirm));
                        // 设置目标地址
                        tvDestinationWallet.setHint(destinationWallet);
                        tvTransactionDetail.setText(String.format(getString(R.string.tv_transaction_detail), DecimalTool.transferDisplay(transactionAmount), BCAASApplication.getBlockService()));
                        showInputPasswordForSendView(true);
                    } else {
                        //檢查當前密碼是否正確
                        String password = etPassword.getText().toString();
                        /*判断密码是否为空*/
                        if (StringTool.isEmpty(password)) {
                            showToast(getResources().getString(R.string.enter_password));
                        } else {
                            //1:获取到用户的正确密码，判断与当前输入密码是否匹配
                            String passwordUser = BCAASApplication.getStringFromSP(Constants.Preference.PASSWORD);
                            if (StringTool.equals(passwordUser, password)) {
                                if (StringTool.equals(currentStatus, Constants.ValueMaps.STATUS_SEND)) {
                                    showToast(getString(R.string.on_transaction));
                                } else {
                                    showLoading();
                                    BCAASApplication.setTransactionAmount(etTransactionAmount.getText().toString());
                                    BCAASApplication.setDestinationWallet(etInputDestinationAddress.getText().toString());
                                    //通知MainActivity更新界面
                                    OttoTool.getInstance().post(new SendTransactionEvent(Constants.Transaction.SEND, password));
                                    //清空当前「发送确认」页面
                                    showInputPasswordForSendView(false);
                                }
                            } else {
                                hideLoading();
                                showToast(getResources().getString(R.string.password_error));
                            }

                        }
                    }

                });
    }

    //顯示為發送交易輸入密碼的頁面
    private void showInputPasswordForSendView(boolean isShow) {
        // 隐藏当前输入交易的信息视图
        rlSetTransactionInfo.setVisibility(isShow ? View.GONE : View.VISIBLE);
        //显示当前需要输入密码的视图
        llSendInfo.setVisibility(isShow ? View.VISIBLE : View.GONE);
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
        public <T> void onItemSelect(T type, String from) {
            if (type == null) {
                return;
            }
            //如果当前是「语言切换」
            if (StringTool.equals(from, Constants.KeyMaps.LANGUAGE_SWITCH)) {
                /*断开连接设为主动*/
                TCPThread.setActiveDisconnect(true);
                hideTVLanguageSwitchDialog();
                switchLanguage(type);
            } else {
                //否則是幣種切換
                TypeSwitchingBean typeSwitchingBean = (TypeSwitchingBean) type;
                if (typeSwitchingBean == null) {
                    return;
                }
                /*断开连接设为主动*/
                TCPThread.setActiveDisconnect(true);
                //關閉當前彈框
                hideTVCurrencySwitchDialog();
                String blockService = typeSwitchingBean.getType();
                /*显示币种*/
                tvCurrency.setText(blockService);
                /*存储币种*/
                BCAASApplication.setBlockService(blockService);
                /*切换当前的区块服务并且更新；重新verify，获取新的区块数据*/
                OttoTool.getInstance().post(new SwitchBlockServiceAndVerifyEvent(true, false));
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

//    @Override
//    public void onBackPressed() {
//        if (StringTool.equals(currentStatus, Constants.ValueMaps.STATUS_SEND)) {
//            showToast(getString(R.string.on_transaction));
//            currentStatus = Constants.ValueMaps.STATUS_DEFAULT;
//        } else {
//            super.onBackPressed();
//        }
//    }

    @Override
    public void noData() {
        showToast(getResources().getString(R.string.account_data_error));

    }

    @Subscribe
    public void RefreshSendStatus(RefreshSendStatusEvent refreshSendStatusEvent) {
        if (refreshSendStatusEvent == null) {
            return;
        }
        // 隱藏加載彈框
        hideLoadingDialog();
        //得到訂閱的值，是否需要解鎖=發送成功
        boolean isSuccess = refreshSendStatusEvent.isSuccess();
        LogTool.d(TAG, MessageConstants.SEND_TRANSACTION_SATE + isSuccess);
        if (isSuccess) {
            clearSendViewInfo();
            currentStatus = Constants.ValueMaps.STATUS_DEFAULT;
            LogTool.d(TAG, getResources().getString(R.string.transaction_has_successfully));
            //刷新當前的界面，清空控件的所有內容
            showInputPasswordForSendView(false);
        } else {
            LogTool.d(TAG, getResources().getString(R.string.transaction_has_failure));
            finish();
        }

    }

    /**
     * 清空发送页面填写信息
     */
    private void clearSendViewInfo() {
        //清空界面的所有信息
        if (etTransactionAmount != null) {
            etTransactionAmount.setText(MessageConstants.Empty);
        }
        if (etPassword != null) {
            etPassword.setText(MessageConstants.Empty);
        }
        // TODO: 2018/10/10
//            if (etInputDestinationAddress != null) {
//                etInputDestinationAddress.setText("");
//            }
        if (btnSend != null) {
            btnSend.setText(getResources().getString(R.string.send));
        }
    }


    @Override
    public void verifyFailure(String from) {
        //验证失败，需要重新拿去AN的信息
        showToast(getResources().getString(R.string.data_acquisition_error));
        finish();

    }

    @Override
    public void connectFailure() {
        super.connectFailure();
    }

    @Override
    public void passwordError() {
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
        if (JsonTool.isTokenInvalid(code)) {
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
        setBalance(BCAASApplication.getWalletBalance());
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
