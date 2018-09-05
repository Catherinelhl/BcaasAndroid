package io.bcaas.ui.activity;

import android.annotation.SuppressLint;
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
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.event.ToLogin;
import io.bcaas.listener.PasswordWatcherListener;
import io.bcaas.listener.SoftKeyBroadManager;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.StringTool;
import io.bcaas.view.PasswordEditText;
import io.reactivex.disposables.Disposable;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 为新导入的钱包设置密码
 */
public class SetPasswordForImportWalletActivity extends BaseActivity {
    private String TAG = SetPasswordForImportWalletActivity.class.getSimpleName();

    @BindView(R.id.ib_back)
    ImageButton ibBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ib_right)
    ImageButton ibRight;
    @BindView(R.id.rl_header)
    RelativeLayout rlHeader;
    @BindView(R.id.pketPwd)
    PasswordEditText pketPwd;
    @BindView(R.id.pketConfirmPwd)
    PasswordEditText pketConfirmPwd;
    @BindView(R.id.btn_sure)
    Button btnSure;
    @BindView(R.id.ll_set_pwd_for_import_wallet)
    LinearLayout llSetPwdForImportWallet;
    @BindView(R.id.tv_password_rule)
    TextView tvPasswordRule;
    @BindView(R.id.v_space)
    View vSpace;

    @Override
    public int getContentView() {
        return R.layout.activity_set_pwd_for_import_wallet;
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
    }

    /**
     * 添加软键盘监听
     */
    private void addSoftKeyBroadManager() {
        softKeyBroadManager = new SoftKeyBroadManager(llSetPwdForImportWallet, vSpace);
        softKeyBroadManager.addSoftKeyboardStateListener(softKeyboardStateListener);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initListener() {
        llSetPwdForImportWallet.setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            return false;
        });
        Disposable subscribeSure = RxView.clicks(btnSure)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    String password = pketPwd.getPrivateKey();
                    String passwordConfirm = pketConfirmPwd.getPrivateKey();
                    if (StringTool.equals(password, passwordConfirm)) {
                        BcaasApplication.setStringToSP(Constants.Preference.PASSWORD, password);
                        BcaasApplication.insertWalletInDB(BcaasApplication.getWallet());
                        OttoTool.getInstance().post(new ToLogin());
                        finish();
                    } else {
                        showToast(getString(R.string.password_entered_not_match));
                    }
                });
    }

    private PasswordWatcherListener passwordWatcherListener = password -> {
        String passwordConfirm = pketConfirmPwd.getPrivateKey();
        if (StringTool.equals(password, passwordConfirm)) {
            tvPasswordRule.setVisibility(View.VISIBLE);
            btnSure.setEnabled(true);
            hideSoftKeyboard();

        } else {
            tvPasswordRule.setVisibility(View.INVISIBLE);
            btnSure.setEnabled(false);

        }

    };
    private PasswordWatcherListener passwordConfirmWatcherListener = passwordConfirm -> {
        String password = pketPwd.getPrivateKey();
        if (StringTool.equals(password, passwordConfirm)) {
            tvPasswordRule.setVisibility(View.VISIBLE);
            btnSure.setEnabled(true);
            hideSoftKeyboard();

        } else {
            tvPasswordRule.setVisibility(View.INVISIBLE);
            btnSure.setEnabled(false);

        }

    };

}
