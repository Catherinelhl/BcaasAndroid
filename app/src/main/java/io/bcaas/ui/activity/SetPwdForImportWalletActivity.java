package io.bcaas.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.database.DaoSession;
import io.bcaas.database.WalletInfo;
import io.bcaas.database.WalletInfoDao;
import io.bcaas.event.ToLogin;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.StringTool;
import io.bcaas.vo.WalletVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 为新导入的钱包设置密码
 */
public class SetPwdForImportWalletActivity extends BaseActivity {
    @BindView(R.id.ibBack)
    ImageButton ibBack;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ibRight)
    ImageButton ibRight;
    @BindView(R.id.rlHeader)
    RelativeLayout rlHeader;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.tv_password_rule)
    TextView tvPasswordRule;
    @BindView(R.id.et_password_confirm)
    EditText etPasswordConfirm;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_sure)
    Button btnSure;

    @Override
    public int getContentView() {
        return R.layout.aty_set_pwd_for_import_wallet;
    }

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public void initViews() {
        tvTitle.setText(getResources().getString(R.string.import_wallet));


    }

    @Override
    public void initListener() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 清空所有的数据还是停留在当前页面？
            }
        });
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                String passwordConfirm = etPasswordConfirm.getText().toString();
                if (StringTool.equals(password, passwordConfirm)) {
//                    String address = "1KxM6id36DxSf6UmKQq9Js4Tky8F3dy2Ck";
//                    String privateKey = "5KCpjpDsec5Y9kConNaiDRCZVLFQcoaXctX6zKsidtaKn9R9oFN";
//                    String publicKey = "04814d0fcc3824f3bc6a7955139f5bc998d9dbde8b38c274761bce4b85fc514a78c5c585bc8bf5843428e6414363980d9c4ffd9bb29e667ac063758bd37bc0af4f";
                  String address = "194nd3nQ4rfPwHL5cyrFwu53TWAZca99yi";
                    String privateKey = "5JwAdJEHG5rp3yt9bZzj5E4c2oxE3heqxhuQnC536wbDXYFd3qF";
                    String publicKey = "04259b2943d2ecbe300aea876e76952567b54858f9c39c72b3966dfb40aa47db8023f9c2546cce7748cace8c76bbfc8f7a16f66ee8bef4c751289a1e5fc51908f0";
                    WalletInfo walletInfo = new WalletInfo();
                    walletInfo.setBitcoinAddressStr(address);
                    walletInfo.setBitcoinPrivateKeyWIFStr(privateKey);
                    walletInfo.setBitcoinPublicKeyStr(publicKey);
                    BcaasApplication.setBlockService(Constants.BlockService.BCC);
                    BcaasApplication.setPassword(password);
                    BcaasApplication.setPublicKey(publicKey);
                    BcaasApplication.setPrivateKey(privateKey);
                    BcaasApplication.setWalletInfo(walletInfo);//将当前的账户地址赋给Application，这样就不用每次都去操作数据库
                    BcaasApplication.insertWalletInfoInDB(walletInfo);
                    OttoTool.getInstance().post(new ToLogin());
                    finish();
                } else {
                    showToast(getString(R.string.confirm_two_pwd_is_consistent));
                }
            }
        });

    }

}
