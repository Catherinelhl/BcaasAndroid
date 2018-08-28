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
import io.bcaas.database.WalletInfo;
import io.bcaas.ecc.Wallet;
import io.bcaas.tools.RegexTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.WalletTool;
import io.bcaas.view.PrivateKeyEditText;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 创建新钱包
 */
public class CreateWalletActivity extends BaseActivity {


    @BindView(R.id.ibBack)
    ImageButton ibBack;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ibRight)
    ImageButton ibRight;
    @BindView(R.id.rlHeader)
    RelativeLayout rlHeader;
    @BindView(R.id.tv_password_rule)
    TextView tvPasswordRule;
    @BindView(R.id.btn_sure)
    Button btnSure;
    @BindView(R.id.pketPwd)
    PrivateKeyEditText pketPwd;
    @BindView(R.id.pketConfirmPwd)
    PrivateKeyEditText pketConfirmPwd;


    @Override
    public int getContentView() {
        return R.layout.aty_create_wallet;
    }

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public void initViews() {
        ibBack.setVisibility(View.VISIBLE);
        tvTitle.setText(getResources().getString(R.string.create_new_wallet));

    }

    @Override
    public void initListener() {
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO  点击取消，回到「登录钱包」的页面？
                finish();
            }
        });
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String pwd = pketPwd.getPrivateKey();
                String confirmPwd = pketConfirmPwd.getPrivateKey();
                if (StringTool.isEmpty(pwd) || StringTool.isEmpty(confirmPwd)) {
                    showToast(getString(R.string.confirm_pwd_not_null));
                } else {
                    if (pwd.length() == Constants.PWD_LENGTH && confirmPwd.length() == Constants.PWD_LENGTH) {

                        if (RegexTool.isCharacter(pwd) && RegexTool.isCharacter(confirmPwd)) {
                            if (StringTool.equals(pwd, confirmPwd)) {
                                createWalletInfo(pwd);
                            } else {
                                showToast(getResources().getString(R.string.confirm_two_pwd_is_consistent));
                            }

                        } else {
                            showToast(getResources().getString(R.string.setpwd));

                        }

                    } else {
                        showToast(getResources().getString(R.string.setpwd));
                    }
                }

            }
        });

    }

    private void createWalletInfo(String password) {
        //创建钱包，并且保存钱包的公钥，私钥，地址，密码
        Wallet wallet = WalletTool.getWalletInfo();
        WalletInfo walletInfo = new WalletInfo();
        String walletAddress = wallet.getBitcoinAddressStr();
        walletInfo.setBitcoinAddressStr(walletAddress);
        walletInfo.setBitcoinPrivateKeyWIFStr(wallet.getBitcoinPrivateKeyWIFStr());
        walletInfo.setBitcoinPublicKeyStr(wallet.getBitcoinPublicKeyStr());
        BcaasApplication.setBlockService(Constants.BlockService.BCC);
        BcaasApplication.setPassword(password);
        BcaasApplication.setPublicKey(wallet.getBitcoinPublicKeyStr());
        BcaasApplication.setPrivateKey(wallet.getBitcoinPrivateKeyWIFStr());
        BcaasApplication.setWalletInfo(walletInfo);//将当前的账户地址赋给Application，这样就不用每次都去操作数据库
        BcaasApplication.insertWalletInfoInDB(walletInfo);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KeyMaps.WALLET_ADDRESS, walletAddress);
        bundle.putString(Constants.KeyMaps.PRIVATE_KEY, wallet.getBitcoinPrivateKeyWIFStr());
        intentToActivity(bundle, WalletCreatedSuccessActivity.class, true);
    }
}
