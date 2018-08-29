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
import io.bcaas.constants.Constants;
import io.bcaas.ecc.Wallet;
import io.bcaas.event.ToLogin;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.WalletTool;
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
    @BindView(R.id.rlHeader)
    RelativeLayout rlHeader;
    @BindView(R.id.pketPwd)
    PrivateKeyEditText pketPwd;
    @BindView(R.id.pketConfirmPwd)
    PrivateKeyEditText pketConfirmPwd;
    @BindView(R.id.btn_sure)
    Button btnSure;
    private String TAG = SetPwdForImportWalletActivity.class.getSimpleName();
    //WIF格式的私钥
    private String WIFPrivateKey;

    @Override
    public int getContentView() {
        return R.layout.aty_set_pwd_for_import_wallet;
    }

    @Override
    public void getArgs(Bundle bundle) {
        if (bundle == null) return;
        WIFPrivateKey = bundle.getString(Constants.WIF_PRIVATE_KEY);

    }

    @Override
    public void initViews() {
        tvTitle.setText(getResources().getString(R.string.import_wallet));
        if (StringTool.isEmpty(WIFPrivateKey)) {
            return;
        }
        parseWIFPrivateKey();


    }

    //解析当前私钥，得到新的钱包地址信息
    private void parseWIFPrivateKey() {
        Wallet wallet = WalletTool.getWalletInfo(WIFPrivateKey);
        BcaasApplication.setBlockServiceToSP(Constants.BlockService.BCC);
        BcaasApplication.setPublicKeyToSP(wallet.getPublicKey());
        BcaasApplication.setPrivateKeyToSP(wallet.getPrivateKey());
        BcaasApplication.setWallet(wallet);//将当前的账户地址赋给Application，这样就不用每次都去操作数据库
        BcaasLog.d(TAG, wallet);
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
