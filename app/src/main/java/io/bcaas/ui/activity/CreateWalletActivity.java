package io.bcaas.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.bean.WalletBean;
import io.bcaas.constants.Constants;
import io.bcaas.listener.PasswordWatcherListener;
import io.bcaas.listener.SoftKeyBroadManager;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.ecc.WalletTool;
import io.bcaas.tools.regex.RegexTool;
import io.bcaas.view.edittext.PasswordEditText;
import io.reactivex.disposables.Disposable;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * Activity：「创建钱包」
 */
public class CreateWalletActivity extends BaseActivity {
    @BindView(R.id.pket_confirm_pwd)
    PasswordEditText pketConfirmPwd;
    private String TAG = CreateWalletActivity.class.getSimpleName();
    @BindView(R.id.ib_back)
    ImageButton ibBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ib_right)
    ImageButton ibRight;
    @BindView(R.id.rl_header)
    RelativeLayout rlHeader;
    @BindView(R.id.tv_password_rule)
    TextView tvPasswordRule;
    @BindView(R.id.btn_sure)
    Button btnSure;
    @BindView(R.id.pket_pwd)
    PasswordEditText pketPwd;
    @BindView(R.id.ll_create_wallet)
    LinearLayout llCreateWallet;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.v_space)
    View vSpace;

    //跳轉「WalletCreatedInfoActivity」的Request code
    private int CREATED_WALLET_INFO_REQUEST_CODE = 0x11;

    @Override
    public int getContentView() {
        return R.layout.activity_create_wallet;
    }

    @Override
    public boolean full() {
        return false;
    }

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public void initViews() {
        ibBack.setVisibility(View.VISIBLE);
        tvTitle.setText(getResources().getString(R.string.create_new_wallet));
        pketPwd.setOnPasswordWatchListener(passwordWatcherListener);
        pketConfirmPwd.setOnPasswordWatchListener(passwordconfirmWatcherListener);
        softKeyBroadManager = new SoftKeyBroadManager(llCreateWallet, vSpace);
        softKeyBroadManager.addSoftKeyboardStateListener(softKeyboardStateListener);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initListener() {
        llCreateWallet.setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            return false;
        });
        llContent.setOnTouchListener((v, event) -> true);
        ibBack.setOnClickListener(v -> setResult(true));
        Disposable subscribeSure = RxView.clicks(btnSure)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    hideSoftKeyboard();
                    String password = pketPwd.getPassword();
                    String confirmPwd = pketConfirmPwd.getPassword();
                    if (StringTool.isEmpty(password) || StringTool.isEmpty(confirmPwd)) {
                        showToast(getString(R.string.enter_password));
                    } else {
                        if (password.length() >= Constants.PASSWORD_MIN_LENGTH && confirmPwd.length() >= Constants.PASSWORD_MIN_LENGTH) {
                            if (RegexTool.isCharacter(password) && RegexTool.isCharacter(confirmPwd)) {
                                if (StringTool.equals(password, confirmPwd)) {
                                    WalletBean walletBean = WalletTool.createAndSaveWallet(password);
                                    if (walletBean != null) {
                                        intentToCheckWalletInfo(walletBean.getAddress(), walletBean.getPrivateKey());
                                    }
                                } else {
                                    showToast(getResources().getString(R.string.password_entered_not_match));
                                }

                            } else {
                                showToast(getResources().getString(R.string.password_rule_of_length));

                            }

                        } else {
                            showToast(getResources().getString(R.string.password_rule_of_length));
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            if (requestCode == CREATED_WALLET_INFO_REQUEST_CODE) {
                //跳轉「為導入的錢包設置密碼」返回
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    boolean isBack = bundle.getBoolean(Constants.KeyMaps.From);
                    LogTool.d(TAG, isBack);
                    if (!isBack) {
                        //否則是點擊「導入」按鈕，那麼應該關閉當前頁面，然後進行登錄
                        setResult(false);
                    }
                }
            }
        }
    }

    private void setResult(boolean isBack) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.KeyMaps.From, isBack);
        intent.putExtras(bundle);
        this.setResult(RESULT_OK, intent);
        this.finish();
    }


    /**
     * 跳转到显示钱包创建成功之后的信息显示页面
     *
     * @param walletAddress
     * @param privateKey
     */
    private void intentToCheckWalletInfo(String walletAddress, String privateKey) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KeyMaps.WALLET_ADDRESS, walletAddress);
        bundle.putString(Constants.KeyMaps.PRIVATE_KEY, privateKey);
        intent.putExtras(bundle);
        intent.setClass(this, WalletCreatedInfoActivity.class);
        startActivityForResult(intent, CREATED_WALLET_INFO_REQUEST_CODE);
    }

    private PasswordWatcherListener passwordWatcherListener = password -> {
        String passwordConfirm = pketConfirmPwd.getPassword();
        if (StringTool.equals(password, passwordConfirm)) {
            hideSoftKeyboard();
        }

    };
    private PasswordWatcherListener passwordconfirmWatcherListener = password -> {
        String passwordConfirm = pketPwd.getPassword();
        if (StringTool.equals(password, passwordConfirm)) {
            hideSoftKeyboard();
        }

    };

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

}

