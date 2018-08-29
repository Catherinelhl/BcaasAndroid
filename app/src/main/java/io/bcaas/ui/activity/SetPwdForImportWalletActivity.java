package io.bcaas.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BcaasApplication;
import io.bcaas.event.ToLogin;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.StringTool;
import io.bcaas.view.PrivateKeyEditText;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 为新导入的钱包设置密码
 */
public class SetPwdForImportWalletActivity extends BaseActivity {
    @BindView(R.id.ib_back)
    ImageButton ibBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ib_right)
    ImageButton ibRight;
    @BindView(R.id.rl_header)
    RelativeLayout rlHeader;
    @BindView(R.id.pketPwd)
    PrivateKeyEditText pketPwd;
    @BindView(R.id.pketConfirmPwd)
    PrivateKeyEditText pketConfirmPwd;
    @BindView(R.id.btn_sure)
    Button btnSure;
    private String TAG = SetPwdForImportWalletActivity.class.getSimpleName();

    @Override
    public int getContentView() {
        return R.layout.aty_set_pwd_for_import_wallet;
    }

    @Override
    public void getArgs(Bundle bundle) {
        if (bundle == null) return;

    }

    @Override
    public void initViews() {
        tvTitle.setText(getResources().getString(R.string.import_wallet));


    }

    @Override
    public void initListener() {
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = pketPwd.getPrivateKey();
                String passwordConfirm = pketConfirmPwd.getPrivateKey();
                if (StringTool.equals(password, passwordConfirm)) {
                    BcaasApplication.setPasswordToSP(password);
                    BcaasApplication.insertWalletInDB(BcaasApplication.getWallet());
                    OttoTool.getInstance().post(new ToLogin());
                    finish();
                } else {
                    showToast(getString(R.string.confirm_two_pwd_is_consistent));
                }
            }
        });
    }

}
