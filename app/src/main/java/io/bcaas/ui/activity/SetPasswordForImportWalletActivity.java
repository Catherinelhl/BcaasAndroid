package io.bcaas.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
import io.bcaas.base.BCAASApplication;
import io.bcaas.base.BaseActivity;
import io.bcaas.constants.Constants;
import io.bcaas.listener.PasswordWatcherListener;
import io.bcaas.listener.SoftKeyBroadManager;
import io.bcaas.tools.PreferenceTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.regex.RegexTool;
import io.bcaas.tools.wallet.WalletDBTool;
import io.bcaas.view.edittext.PasswordEditText;
import io.reactivex.disposables.Disposable;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * Activity：「導入錢包」二級頁面，为新导入的钱包设置密码
 */
public class SetPasswordForImportWalletActivity extends BaseActivity {

    @BindView(R.id.ib_back)
    ImageButton ibBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ib_right)
    ImageButton ibRight;
    @BindView(R.id.rl_header)
    RelativeLayout rlHeader;
    @BindView(R.id.pket_pwd)
    PasswordEditText pketPwd;
    @BindView(R.id.pket_confirm_pwd)
    PasswordEditText pketConfirmPwd;
    @BindView(R.id.btn_sure)
    Button btnSure;
    @BindView(R.id.ll_set_pwd_for_import_wallet)
    LinearLayout llSetPwdForImportWallet;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.tv_password_rule)
    TextView tvPasswordRule;
    @BindView(R.id.v_space)
    View vSpace;

    @Override
    public int getContentView() {
        return R.layout.activity_set_pwd_for_import_wallet;
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
    }

    @Override
    public void initViews() {
        tvTitle.setText(getResources().getString(R.string.import_wallet));
        pketPwd.setOnPasswordWatchListener(passwordWatcherListener);
        pketConfirmPwd.setOnPasswordWatchListener(passwordConfirmWatcherListener);
        addSoftKeyBroadManager();
        ibBack.setVisibility(View.VISIBLE);
    }

    /**
     * 添加软键盘监听
     */
    private void addSoftKeyBroadManager() {
        softKeyBroadManager = new SoftKeyBroadManager(llSetPwdForImportWallet, vSpace);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initListener() {
        llSetPwdForImportWallet.setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            return false;
        });
        llContent.setOnTouchListener((v, event) -> true);
        Disposable subscribeSure = RxView.clicks(btnSure)
                .throttleFirst(Constants.Time.sleep800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    hideSoftKeyboard();
                    String password = pketPwd.getPassword();
                    String passwordConfirm = pketConfirmPwd.getPassword();
                    if (StringTool.isEmpty(password) || StringTool.isEmpty(passwordConfirm)) {
                        showToast(getString(R.string.enter_password));
                    } else {
                        if (password.length() >= Constants.PASSWORD_MIN_LENGTH && passwordConfirm.length() >= Constants.PASSWORD_MIN_LENGTH) {
                            if (RegexTool.isCharacter(password) && RegexTool.isCharacter(passwordConfirm)) {
                                if (StringTool.equals(password, passwordConfirm)) {
                                    PreferenceTool.getInstance().saveString(Constants.Preference.PASSWORD, password);
                                    WalletDBTool.insertWalletInDB(BCAASApplication.getWalletBean());
                                    setResult(false);
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
        Disposable subscribeBack = RxView.clicks(ibBack)
                .throttleFirst(Constants.Time.sleep800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    setResult(true);
                });
    }

    private void setResult(boolean isBack) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.KeyMaps.From, isBack);
        intent.putExtras(bundle);
        this.setResult(RESULT_OK, intent);
        this.finish();
    }

    private PasswordWatcherListener passwordWatcherListener = password -> {
        String passwordConfirm = pketConfirmPwd.getPassword();
        if (StringTool.equals(password, passwordConfirm)) {
            hideSoftKeyboard();
        }
    };
    private PasswordWatcherListener passwordConfirmWatcherListener = passwordConfirm -> {
        String password = pketPwd.getPassword();
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
