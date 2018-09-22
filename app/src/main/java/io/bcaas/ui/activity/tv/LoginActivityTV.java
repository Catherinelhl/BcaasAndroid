package io.bcaas.ui.activity.tv;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.constants.Constants;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.wallet.WalletDBTool;
import io.bcaas.view.edittext.PasswordEditText;
import io.bcaas.view.tv.FlyBroadLayout;
import io.bcaas.view.tv.MainUpLayout;
import io.reactivex.disposables.Disposable;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/20
 */
public class LoginActivityTV extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_private_key)
    EditText etPrivateKey;
    @BindView(R.id.pketPwd)
    PasswordEditText pketPwd;
    @BindView(R.id.pketConfirmPwd)
    PasswordEditText pketConfirmPwd;
    @BindView(R.id.tv_password_rule)
    TextView tvPasswordRule;
    @BindView(R.id.btn_import_wallet)
    Button btnImportWallet;
    @BindView(R.id.ll_import_wallet)
    LinearLayout llImportWallet;
    @BindView(R.id.btn_unlock_wallet)
    Button btnUnlockWallet;
    @BindView(R.id.ll_unlock_wallet)
    LinearLayout llUnlockWallet;
    @BindView(R.id.pket_create_pwd)
    PasswordEditText pketCreatePwd;
    @BindView(R.id.pket_create_confirm_pwd)
    PasswordEditText pketCreateConfirmPwd;
    @BindView(R.id.btn_create_wallet)
    Button btnCreateWallet;
    @BindView(R.id.ll_create_wallet)
    LinearLayout llCreateWallet;

    @BindView(R.id.block_base_mainup)
    FlyBroadLayout blockBaseMainup;
    @BindView(R.id.block_base_content)
    MainUpLayout blockBaseContent;
    @Override
    public boolean full() {
        return false;
    }

    @Override
    public int getContentView() {
        return R.layout.tv_activity_login;
    }

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public void initViews() {

    }

    @Override
    public void initListener() {
        blockBaseContent.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                blockBaseMainup.setFocusView(newFocus, oldFocus, 1.2f);
            }
        });

//        Disposable subscribeUnlockWallet = RxView.clicks(btnUnlockWallet)
//                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
//                .subscribe(o -> {
//                    hideSoftKeyboard();
//                    if (WalletDBTool.existKeystoreInDB()) {
//                        String password = etPassword.getText().toString();
//                        if (StringTool.notEmpty(password)) {
//                            presenter.queryWalletFromDB(password);
//                        } else {
//                            showToast(getString(R.string.enter_password));
//                        }
//                    } else {
//                        noWalletInfo();
//                    }
//                });
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

}
