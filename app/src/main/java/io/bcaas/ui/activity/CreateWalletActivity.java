package io.bcaas.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.ecc.Wallet;
import io.bcaas.tools.RegexTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.WalletTool;
import io.bcaas.view.PrivateKeyEditText;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 创建新钱包
 */
public class CreateWalletActivity extends BaseActivity {

    @BindView(R.id.ib_back)
    ImageButton ibBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ib_right)
    ImageButton ibRight;
    @BindView(R.id.rl_header)
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
        ibBack.setOnClickListener(v -> finish());
        Disposable subscribeSure = RxView.clicks(btnSure)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    String pwd = pketPwd.getPrivateKey();
                    String confirmPwd = pketConfirmPwd.getPrivateKey();
                    if (StringTool.isEmpty(pwd) || StringTool.isEmpty(confirmPwd)) {
                        showToast(getString(R.string.confirm_pwd_not_null));
                    } else {
                        if (pwd.length() == Constants.PWD_LENGTH && confirmPwd.length() == Constants.PWD_LENGTH) {

                            if (RegexTool.isCharacter(pwd) && RegexTool.isCharacter(confirmPwd)) {
                                if (StringTool.equals(pwd, confirmPwd)) {
                                    createAndSaveWallet(pwd);
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
                });
    }

    /**
     * 保存当前的钱包信息
     *
     * @param password
     */
    private void createAndSaveWallet(String password) {
        //1:创建钱包
        Wallet wallet = WalletTool.getWalletInfo();
        //2:并且保存钱包的公钥，私钥，地址，密码
        String walletAddress = wallet.getAddress();
        // TODO: 2018/8/29 暂时将区块服务存储为BCC
        BcaasApplication.setBlockServiceToSP(Constants.BlockService.BCC);
        BcaasApplication.setPasswordToSP(password);
        BcaasApplication.setPublicKeyToSP(wallet.getPublicKey());
        BcaasApplication.setPrivateKeyToSP(wallet.getPrivateKey());
        BcaasApplication.setWallet(wallet);//将当前的账户地址赋给Application，这样就不用每次都去操作数据库
        BcaasApplication.insertWalletInDB(wallet);
        intentToCheckWalletInfo(walletAddress, wallet.getPrivateKey());

    }

    /**
     * 跳转到显示钱包创建成功之后的信息显示页面
     *
     * @param walletAddress
     * @param privateKey
     */
    private void intentToCheckWalletInfo(String walletAddress, String privateKey) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KeyMaps.WALLET_ADDRESS, walletAddress);
        bundle.putString(Constants.KeyMaps.PRIVATE_KEY, privateKey);
        intentToActivity(bundle, WalletCreatedInfoActivity.class, true);
    }
}
