package io.bcaas.ui.aty;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.constants.Constants;
import io.bcaas.event.ToLogin;
import io.bcaas.presenter.LoginPresenterImp;
import io.bcaas.ui.contracts.LoginContracts;
import io.bcaas.utils.StringU;
import io.bcaas.vo.WalletVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 是否以LoginActivity为当前账户登录的主要Activity，保持此activity不finish，然后跳转创建、或者导入
 * 钱包的界面，操作结束的时候，返回到当前页面，然后进入MainActivity。
 */
public class LoginActivity extends BaseActivity
        implements LoginContracts.View {

    @BindView(R.id.tv_info)
    TextView tvInfo;
    @BindView(R.id.et_private_key)
    EditText etPrivateKey;
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
        etPrivateKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String pwd = s.toString();
                btnUnlockWallet.setPressed(StringU.notEmpty(pwd));

            }
        });
        cbPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etPrivateKey.setInputType(isChecked ?
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);//设置当前私钥显示不可见

            }
        });
        btnUnlockWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPrivateKey.getText().toString();
                if (StringU.notEmpty(password)) {
                    presenter.queryWalletInfo(password);
                } else {
                    showToast(getString(R.string.walletinfo_must_not_null));
                }

            }
        });
        tvCreateWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentToActivity(CreateWalletActivity.class);
            }
        });
        tvImportWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentToActivity(ImportWalletActivity.class);
            }
        });

    }

    @Override
    public void noWalletInfo() {
        // TODO: 2018/8/20  当前没有可用的钱包提示
        showToast(getString(R.string.no_wallet));
    }

    @Override
    public void loginSuccess() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KeyMaps.From, Constants.ValueMaps.FROM_LOGIN);
        intentToActivity(bundle, MainActivity.class, true);
    }

    @Override
    public void loginFailure(String message) {
        showToast(message);
    }

    @Subscribe
    public void loginWalletSuccess(ToLogin loginSuccess) {
        if (loginSuccess == null) return;
        WalletVO walletVO = loginSuccess.getWalletVO();
        presenter.login(walletVO);
    }


}
