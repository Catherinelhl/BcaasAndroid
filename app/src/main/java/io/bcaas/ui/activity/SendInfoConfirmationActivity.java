package io.bcaas.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.*;

import butterknife.BindView;

import com.jakewharton.rxbinding2.view.RxView;

import io.bcaas.R;
import io.bcaas.base.BCAASApplication;
import io.bcaas.base.BaseActivity;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.gson.ResponseJson;
import io.bcaas.listener.SoftKeyBroadManager;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.TextTool;
import io.bcaas.tools.decimal.DecimalTool;
import io.bcaas.tools.gson.JsonTool;
import io.reactivex.disposables.Disposable;

import java.util.concurrent.TimeUnit;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 * Activity：「发送/SendFragment」二级页面；点击「确认」，进行网络的请求，關閉當前頁面，返回到首頁，背景執行「Send」交易
 */
public class SendInfoConfirmationActivity extends BaseActivity {
    private String TAG = SendInfoConfirmationActivity.class.getSimpleName();


    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.cb_pwd)
    CheckBox cbPwd;
    @BindView(R.id.v_password_line)
    View vPasswordLine;
    @BindView(R.id.ll_password_key)
    LinearLayout llPasswordKey;
    @BindView(R.id.ib_back)
    ImageButton ibBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ib_right)
    ImageButton ibRight;
    @BindView(R.id.rl_header)
    RelativeLayout rlHeader;
    @BindView(R.id.tv_transaction_detail_key)
    TextView tvTransactionDetailKey;
    @BindView(R.id.tv_transaction_detail)
    TextView tvTransactionDetail;
    @BindView(R.id.tv_receive_account_key)
    TextView tvReceiveAccountKey;
    @BindView(R.id.tv_destination_wallet)
    TextView tvDestinationWallet;
    @BindView(R.id.ll_send_confirm)
    LinearLayout llSendConfirm;
    @BindView(R.id.sv_send_confirm)
    ScrollView svSendConfirm;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.v_space)
    View vSpace;
    private String transactionAmount, addressName, destinationWallet;//获取上一个页面传输过来的接收方的币种以及地址信息,以及交易数额

    @Override
    public int getContentView() {
        return R.layout.activity_send_toconfirm_pwd;
    }

    @Override
    public boolean full() {
        return false;
    }

    @Override
    public void getArgs(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        addressName = bundle.getString(Constants.KeyMaps.ADDRESS_NAME);
        destinationWallet = bundle.getString(Constants.KeyMaps.DESTINATION_WALLET);
        transactionAmount = bundle.getString(Constants.KeyMaps.TRANSACTION_AMOUNT);
    }

    @Override
    public void initViews() {
        ibBack.setVisibility(View.VISIBLE);
        tvTitle.setText(getResources().getString(R.string.send));
        //给文本预设两边距离为16
        int margin = getResources().getDimensionPixelOffset(R.dimen.d20);
        //获取当前text view占用的布局
        float textPaintWidth = TextTool.getViewWidth(tvTransactionDetailKey, getString(R.string.transaction_to));
        double width = BCAASApplication.getScreenWidth() - getResources().getDimensionPixelOffset(R.dimen.d60) - margin - textPaintWidth;

        tvTransactionDetailKey.setText(TextTool.intelligentOmissionText(tvTransactionDetailKey, (int) width, String.format(getString(R.string.transaction_to),
                addressName != null ? addressName : destinationWallet), true));
        tvDestinationWallet.setHint(destinationWallet);
        vPasswordLine.setVisibility(View.GONE);
        tvTransactionDetail.setText(StringTool.removeIllegalSpace(String.format(getString(R.string.tv_transaction_detail), DecimalTool.transferDisplay(transactionAmount), BCAASApplication.getBlockService())));
        addSoftKeyBroadManager();
    }

    /**
     * 添加软键盘监听
     */
    private void addSoftKeyBroadManager() {
        softKeyBroadManager = new SoftKeyBroadManager(svSendConfirm, vSpace);
        softKeyBroadManager.addSoftKeyboardStateListener(softKeyboardStateListener);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initListener() {
        svSendConfirm.setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            return false;
        });
        llSendConfirm.setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            return false;
        });
        llContent.setOnTouchListener((v, event) -> true);
        cbPwd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String text = etPassword.getText().toString();
            if (StringTool.isEmpty(text)) {
                return;
            }
            etPassword.setInputType(isChecked ?
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);//设置当前私钥显示不可见

        });
        ibBack.setOnClickListener(v -> {
            hideLoadingDialog();
            setResult(Constants.ValueMaps.ACTIVITY_STATUS_TODO);
        });
        Disposable subscribeSend = RxView.clicks(btnSend)
                .throttleFirst(Constants.Time.sleep1000, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    hideSoftKeyboard();
                    String password = etPassword.getText().toString();
                    /*判断密码是否为空*/
                    if (StringTool.isEmpty(password)) {
                        showToast(getResources().getString(R.string.enter_password));
                    } else {
                        showLoading();
                        //1:获取到用户的正确密码，判断与当前输入密码是否匹配
                        String passwordUser = BCAASApplication.getStringFromSP(Constants.Preference.PASSWORD);
                        if (StringTool.equals(passwordUser, password)) {
                            //保存当前输入要交易的金额以及接收账户地址
                            BCAASApplication.setTransactionAmount(transactionAmount);
                            BCAASApplication.setDestinationWallet(destinationWallet);
                            LogTool.d(TAG, "点击发送");
                            etPassword.setText(MessageConstants.Empty);
                            setResult(Constants.ValueMaps.ACTIVITY_STATUS_TRADING);
                        } else {
                            hideLoading();
                            showToast(getResources().getString(R.string.password_error));
                        }
                    }
                });
    }

    @Override
    public void connectFailure() {
        super.connectFailure();
    }


    @Override
    public void noNetWork() {
        showToast(getResources().getString(R.string.network_not_reachable));
    }

    @Override
    public void showLoading() {
        if (!checkActivityState()) {
            return;
        }
        showLoadingDialog();
    }

    @Override
    public void hideLoading() {
        if (!checkActivityState()) {
            return;
        }
        hideLoadingDialog();
    }

    @Override
    public void httpExceptionStatus(ResponseJson responseJson) {
        if (responseJson == null) {
            return;
        }
        int code = responseJson.getCode();
        if (JsonTool.isTokenInvalid(code)) {
            showLogoutSingleDialog();
        } else {
            super.httpExceptionStatus(responseJson);
        }
    }

    /**
     * 退出当前界面
     *
     * @param status
     */
    private void setResult(String status) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KeyMaps.ACTIVITY_STATUS, status);
        intent.putExtras(bundle);
        this.setResult(RESULT_OK, intent);
        this.finish();
    }
}
