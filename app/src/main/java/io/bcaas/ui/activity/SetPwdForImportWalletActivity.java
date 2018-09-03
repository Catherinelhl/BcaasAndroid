package io.bcaas.ui.activity;

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
public class SetPwdForImportWalletActivity extends BaseActivity {
    private String TAG = SetPwdForImportWalletActivity.class.getSimpleName();

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
    @BindView(R.id.tc_password_warning)
    TextView tvPasswordWarning;

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
    }

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
                        showToast(getString(R.string.confirm_two_pwd_is_consistent));
                    }
                });
    }

    private PasswordWatcherListener passwordWatcherListener = password -> {
        String passwordConfirm = pketConfirmPwd.getPrivateKey();
        if (StringTool.equals(password, passwordConfirm)) {
            tvPasswordWarning.setVisibility(View.VISIBLE);
            btnSure.setEnabled(true);
            hideSoftKeyboard();

        }

    };
    private PasswordWatcherListener passwordConfirmWatcherListener = passwordConfirm -> {
        String password = pketPwd.getPrivateKey();
        if (StringTool.equals(password, passwordConfirm)) {
            tvPasswordWarning.setVisibility(View.VISIBLE);
            btnSure.setEnabled(true);
            hideSoftKeyboard();

        }

    };

}
