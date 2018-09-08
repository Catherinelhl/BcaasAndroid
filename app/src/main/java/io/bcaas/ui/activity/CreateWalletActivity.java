package io.bcaas.ui.activity;

import android.annotation.SuppressLint;
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
import io.bcaas.bean.WalletBean;
import io.bcaas.constants.Constants;
import io.bcaas.listener.PasswordWatcherListener;
import io.bcaas.listener.SoftKeyBroadManager;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.ecc.WalletTool;
import io.bcaas.tools.regex.RegexTool;
import io.bcaas.view.PasswordEditText;
import io.reactivex.disposables.Disposable;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 创建新钱包
 */
public class CreateWalletActivity extends BaseActivity {
    @BindView(R.id.pketConfirmPwd)
    PasswordEditText pketConfirmPwd;
    private String TAG = CreateWalletActivity.class.getSimpleName();
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
    PasswordEditText pketPwd;
    @BindView(R.id.ll_create_wallet)
    LinearLayout llCreateWallet;
    @BindView(R.id.v_space)
    View vSpace;

    @Override
    public int getContentView() {
        return R.layout.activity_create_wallet;
    }

    @Override
    public boolean full() {
        return false;
    }

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public void initViews() {
        ibBack.setVisibility(View.VISIBLE);
        tvTitle.setText(getResources().getString(R.string.create_new_wallet));
        pketPwd.setOnPasswordWatchListener(passwordWatcherListener);
        pketConfirmPwd.setOnPasswordWatchListener(passwordconfirmWatcherListener);
        softKeyBroadManager = new SoftKeyBroadManager(llCreateWallet, vSpace);
        softKeyBroadManager.addSoftKeyboardStateListener(softKeyboardStateListener);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initListener() {
        llCreateWallet.setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            return false;
        });
        ibBack.setOnClickListener(v -> finish());
        Disposable subscribeSure = RxView.clicks(btnSure)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    String pwd = pketPwd.getPassword();
                    String confirmPwd = pketConfirmPwd.getPassword();
                    if (StringTool.isEmpty(pwd) || StringTool.isEmpty(confirmPwd)) {
                        showToast(getString(R.string.input_password));
                    } else {
                        if (pwd.length() >= Constants.PASSWORD_MIN_LENGTH && confirmPwd.length() >= Constants.PASSWORD_MIN_LENGTH) {
                            if (RegexTool.isCharacter(pwd) && RegexTool.isCharacter(confirmPwd)) {
                                if (StringTool.equals(pwd, confirmPwd)) {
                                    createAndSaveWallet(pwd);
                                } else {
                                    showToast(getResources().getString(R.string.password_entered_not_match));
                                }

                            } else {
                                showToast(getResources().getString(R.string.password_rule_of_length));

                            }

                        } else {
                            showToast(getResources().getString(R.string.password_rule_of_length));
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
        WalletBean walletBean = WalletTool.getWalletInfo();
        //2:并且保存钱包的公钥，私钥，地址，密码
        String walletAddress = walletBean.getAddress();
        BcaasApplication.setBlockService(Constants.BLOCKSERVICE_BCC);
        BcaasApplication.setStringToSP(Constants.Preference.PASSWORD, password);
        BcaasApplication.setStringToSP(Constants.Preference.PUBLIC_KEY, walletBean.getPublicKey());
        BcaasApplication.setStringToSP(Constants.Preference.PRIVATE_KEY, walletBean.getPrivateKey());
        BcaasApplication.setWalletBean(walletBean);//将当前的账户地址赋给Application，这样就不用每次都去操作数据库
        BcaasApplication.insertWalletInDB(walletBean);
        intentToCheckWalletInfo(walletAddress, walletBean.getPrivateKey());

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

    private PasswordWatcherListener passwordWatcherListener = password -> {
        String passwordConfirm = pketConfirmPwd.getPassword();
        if (StringTool.equals(password, passwordConfirm)) {
            hideSoftKeyboard();
        }

    };
    private PasswordWatcherListener passwordconfirmWatcherListener = password -> {
        String passwordConfirm = pketPwd.getPassword();
        if (StringTool.equals(password, passwordConfirm)) {
            hideSoftKeyboard();
        }

    };

}

