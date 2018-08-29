package io.bcaas.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.base.BaseHttpActivity;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.event.ToLogin;
import io.bcaas.presenter.LoginPresenterImp;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.StringTool;
import io.bcaas.ui.contracts.BaseContract;
import io.bcaas.ui.contracts.LoginContracts;
import io.bcaas.view.LineEditText;
import io.bcaas.view.dialog.BcaasDialog;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 是否以LoginActivity为当前账户登录的主要Activity，保持此activity不finish，然后跳转创建、或者导入
 * 钱包的界面，操作结束的时候，返回到当前页面，然后进入MainActivity。
 */
public class LoginActivity extends BaseHttpActivity
        implements BaseContract.HttpView {
    private String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.let_private_key)
    LineEditText letPrivateKey;
    @BindView(R.id.cbPwd)
    CheckBox cbPwd;
    @BindView(R.id.btn_unlock_wallet)
    Button btnUnlockWallet;
    @BindView(R.id.tv_create_wallet)
    TextView tvCreateWallet;
    @BindView(R.id.tv_import_wallet)
    TextView tvImportWallet;


    private LoginContracts.Presenter presenter;

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public int getContentView() {
        return R.layout.aty_login;
    }

    @Override
    public void initViews() {
        presenter = new LoginPresenterImp(this);
    }

    @Override
    public void initListener() {
        letPrivateKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String pwd = s.toString();
                btnUnlockWallet.setPressed(StringTool.notEmpty(pwd));

            }
        });
        cbPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                letPrivateKey.setInputType(isChecked ?
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);//设置当前私钥显示不可见

            }
        });
        btnUnlockWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BcaasApplication.existKeystoreInDB()) {
                    String password = letPrivateKey.getText().toString();
                    if (StringTool.notEmpty(password)) {
                        presenter.queryWalletFromDB(password);
                    } else {
                        showToast(getString(R.string.walletinfo_must_not_null));
                    }
                } else {
                    noWalletInfo();
                }

            }
        });
        tvCreateWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1：若客户没有存储钱包信息，直接进入创建钱包页面
                //2：若客户端已经存储了钱包信息，需做如下提示
                if (BcaasApplication.existKeystoreInDB()) {
                    showBcaasDialog(getResources().getString(R.string.warning),
                            getResources().getString(R.string.sure),
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
            }
        });
        tvImportWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1：若客户没有存储钱包信息，直接进入导入钱包页面
                //2：若客户端已经存储了钱包信息，需做如下提示
                if (BcaasApplication.existKeystoreInDB()) {
                    showBcaasDialog(getResources().getString(R.string.warning),
                            getResources().getString(R.string.sure),
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
    public void loginFailure(String message) {
        BcaasLog.d(TAG, message);
    }

    @Subscribe
    public void toLoginWallet(ToLogin loginSuccess) {
        presenter.toLogin();
    }

    @Override
    public void verifySuccess() {
        BcaasLog.d(TAG, getString(R.string.verify_success));
    }

    @Override
    public void verifyFailure(String message) {
        BcaasLog.d(TAG, message);
    }
}
