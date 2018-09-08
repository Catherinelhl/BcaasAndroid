package io.bcaas.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.squareup.otto.Subscribe;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.event.LoginEvent;
import io.bcaas.presenter.LoginPresenterImp;
import io.bcaas.tools.ActivityTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.regex.RegexTool;
import io.bcaas.ui.contracts.BaseContract;
import io.bcaas.ui.contracts.LoginContracts;
import io.bcaas.view.dialog.BcaasDialog;
import io.reactivex.disposables.Disposable;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 是否以LoginActivity为当前账户登录的主要Activity，保持此activity不finish，然后跳转创建、或者导入
 * 钱包的界面，操作结束的时候，返回到当前页面，然后进入MainActivity。
 */
public class LoginActivity extends BaseActivity
        implements BaseContract.HttpView {
    private String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.cbPwd)
    CheckBox cbPwd;
    @BindView(R.id.v_password_line)
    View vPasswordLine;
    @BindView(R.id.btn_unlock_wallet)
    Button btnUnlockWallet;
    @BindView(R.id.tv_create_wallet)
    TextView tvCreateWallet;
    @BindView(R.id.tv_import_wallet)
    TextView tvImportWallet;
    @BindView(R.id.ll_login)
    LinearLayout llLogin;


    private LoginContracts.Presenter presenter;

    @Override
    public boolean full() {
        return false;
    }

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    public void initViews() {
        presenter = new LoginPresenterImp(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initListener() {
        llLogin.setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            return false;
        });
        cbPwd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String text = etPassword.getText().toString();
            if (StringTool.isEmpty(text)) {
                return;
            }
            etPassword.setInputType(isChecked ?
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);//设置当前私钥显示不可见

        });
        Disposable subscribeUnlockWallet = RxView.clicks(btnUnlockWallet)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (BcaasApplication.existKeystoreInDB()) {
                        String password = etPassword.getText().toString();
                        if (StringTool.notEmpty(password)) {
                            if (password.length() >= Constants.PASSWORD_MIN_LENGTH && RegexTool.isCharacter(password)) {
                                presenter.queryWalletFromDB(password);
                            } else {
                                showToast(getResources().getString(R.string.password_rule_of_length));
                            }
                        } else {
                            showToast(getString(R.string.input_password));
                        }
                    } else {
                        noWalletInfo();
                    }
                });
        Disposable subscribeCreateWallet = RxView.clicks(tvCreateWallet)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    //1：若客户没有存储钱包信息，直接进入创建钱包页面
                    //2：若客户端已经存储了钱包信息，需做如下提示
                    if (BcaasApplication.existKeystoreInDB()) {
                        showBcaasDialog(getResources().getString(R.string.warning),
                                getResources().getString(R.string.confirm),
                                getResources().getString(R.string.cancel),
                                getString(R.string.create_wallet_dialog_message), new BcaasDialog.ConfirmClickListener() {
                                    @Override
                                    public void sure() {
                                        intentToActivity(CreateWalletActivity.class);
                                    }

                                    @Override
                                    public void cancel() {

                                    }
                                });
                    } else {
                        intentToActivity(CreateWalletActivity.class);
                    }
                });
        tvImportWallet.setOnClickListener(v -> {
            //1：若客户没有存储钱包信息，直接进入导入钱包页面
            //2：若客户端已经存储了钱包信息，需做如下提示
            if (BcaasApplication.existKeystoreInDB()) {
                showBcaasDialog(getResources().getString(R.string.warning),
                        getResources().getString(R.string.confirm),
                        getResources().getString(R.string.cancel),
                        getResources().getString(R.string.import_wallet_dialog_message), new BcaasDialog.ConfirmClickListener() {
                            @Override
                            public void sure() {
                                intentToActivity(ImportWalletActivity.class);
                            }

                            @Override
                            public void cancel() {

                            }
                        });
            } else {
                intentToActivity(ImportWalletActivity.class);

            }
        });

    }

    @Override
    public void noWalletInfo() {
        showToast(MessageConstants.NO_WALLET);
    }

    @Override
    public void loginSuccess() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KeyMaps.From, Constants.ValueMaps.FROM_LOGIN);
        intentToActivity(bundle, MainActivity.class, true);
    }

    @Override
    public void loginFailure() {
        showToast(getResources().getString(R.string.login_failure));
    }

    @Subscribe
    public void toLoginWallet(LoginEvent loginSuccess) {
        presenter.toLogin();
    }

    @Override
    public void verifySuccess() {
        LogTool.d(TAG, MessageConstants.VERIFY_SUCCESS);
    }

    @Override
    public void verifyFailure() {
        showToast(getResources().getString(R.string.data_acquisition_error));
    }

    @Override
    public void passwordError() {
        showToast(getResources().getString(R.string.password_error));
    }

    @Override
    public void onBackPressed() {
        ActivityTool.getInstance().exit();
        super.onBackPressed();
    }

    @Override
    public void httpGetLatestBlockAndBalanceSuccess() {

    }

    @Override
    public void httpGetLatestBlockAndBalanceFailure() {

    }

    @Override
    public void resetAuthNodeFailure(String message) {

    }

    @Override
    public void resetAuthNodeSuccess() {

    }

    @Override
    public void noData() {
        showToast(getResources().getString(R.string.account_data_error));
    }

    @Override
    public void responseDataError() {
        showToast(getResources().getString(R.string.data_acquisition_error));
    }

}
