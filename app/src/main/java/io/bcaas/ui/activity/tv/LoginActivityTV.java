package io.bcaas.ui.activity.tv;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.squareup.otto.Subscribe;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.bcaas.BuildConfig;
import io.bcaas.R;
import io.bcaas.base.BaseTVActivity;
import io.bcaas.base.BcaasApplication;
import io.bcaas.bean.LanguageSwitchingBean;
import io.bcaas.bean.WalletBean;
import io.bcaas.constants.Constants;
import io.bcaas.event.LoginEvent;
import io.bcaas.event.NetStateChangeEvent;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.presenter.LoginPresenterImp;
import io.bcaas.tools.DateFormatTool;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.ecc.WalletTool;
import io.bcaas.tools.regex.RegexTool;
import io.bcaas.tools.wallet.WalletDBTool;
import io.bcaas.ui.contracts.LoginContracts;
import io.bcaas.view.textview.TVTextView;
import io.bcaas.view.edittext.TVPasswordEditText;
import io.bcaas.view.tv.FlyBroadLayout;
import io.bcaas.view.tv.MainUpLayout;
import io.reactivex.disposables.Disposable;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/20
 * <p>
 * <p>
 * TV登录界面：
 * 1：登录
 * 2：创建钱包，切换当前卡片内容，显示当前创建的钱包的私钥信息
 * 3：导入钱包，点击"确认"，为导入的私钥设置密码
 */
public class LoginActivityTV extends BaseTVActivity
        implements LoginContracts.View {

    @BindView(R.id.block_base_mainup)
    FlyBroadLayout blockBaseMainup;
    @BindView(R.id.block_base_content)
    MainUpLayout blockBaseContent;

    //标题
    @BindView(R.id.tv_title)
    TVTextView tvTitle;
    @BindView(R.id.tv_current_time)
    TextView tvCurrentTime;
    @BindView(R.id.ib_right)
    ImageButton ibRight;
    @BindView(R.id.rl_header)
    RelativeLayout rlHeader;

    //解鎖錢包
    @BindView(R.id.et_unlock_pwd)
    TVPasswordEditText etUnlockPwd;
    @BindView(R.id.btn_unlock_wallet)
    Button btnUnlockWallet;
    @BindView(R.id.rl_unlock_wallet)
    RelativeLayout rlUnlockWallet;

    //创建钱包
    @BindView(R.id.pket_create_pwd)
    TVPasswordEditText pketCreatePwd;
    @BindView(R.id.pket_create_confirm_pwd)
    TVPasswordEditText pketCreateConfirmPwd;
    @BindView(R.id.ll_create_set_pwd)
    LinearLayout llCreateSetPwd;
    @BindView(R.id.btn_create_wallet)
    Button btnCreateWallet;
    @BindView(R.id.rl_create_wallet)
    RelativeLayout rlCreateWallet;
    @BindView(R.id.ll_create_show_wallet_info)
    LinearLayout llCreateShowWalletInfo;
    @BindView(R.id.tv_private_key)
    TextView tvPrivateKey;
    @BindView(R.id.cb_private_key)
    CheckBox cbPrivateKey;
    @BindView(R.id.v_password_line)
    View vPasswordLine;
    @BindView(R.id.ll_private_key)
    LinearLayout llPrivateKey;

    //導入錢包
    @BindView(R.id.et_import_private_key)
    EditText etImportPrivateKey;
    @BindView(R.id.rl_import_set_private_key)
    RelativeLayout rlImportSetPrivateKey;
    @BindView(R.id.pket_import_pwd)
    TVPasswordEditText pketImportPwd;
    @BindView(R.id.pket_import_confirm_pwd)
    TVPasswordEditText pketImportConfirmPwd;
    @BindView(R.id.tv_password_rule)
    TextView tvPasswordRule;
    @BindView(R.id.ll_import_set_pwd)
    LinearLayout llImportSetPwd;
    @BindView(R.id.btn_import_wallet)
    Button btnImportWallet;
    @BindView(R.id.rl_import_wallet)
    RelativeLayout rlImportWallet;
    @BindView(R.id.sv_import_wallet_set_pwd)
    ScrollView svRlImportWallet;
    @BindView(R.id.sv_create_wallet_set_pwd)
    ScrollView svRlCreateWallet;
    @BindView(R.id.tv_account_address)
    TextView tvAccountAddress;


    private LoginContracts.Presenter presenter;
    //可見的創建錢包生成的私鑰
    private String visibleCreatePrivateKey;


    String privateKey = "5KEJMiY5LskP3S54hcuVKD9zJmb24EYNSi6vGTnEPvve7vMzGCq";

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
        tvTitle.setText(getResources().getString(R.string.login));
        tvCurrentTime.setText(DateFormatTool.getCurrentTime());
        presenter = new LoginPresenterImp(this);
        if (BuildConfig.DEBUG) {
            etImportPrivateKey.setText(privateKey);
            if (StringTool.notEmpty(privateKey)) {
                etImportPrivateKey.setSelection(privateKey.length());
            }
        }
        initEditTextStatus();
    }

    //初始化所有輸入框的初始狀態
    private void initEditTextStatus() {
        //设置弹出的键盘类型为空
        String content = etImportPrivateKey.getText().toString();
        if (StringTool.isEmpty(content)) {
            etImportPrivateKey.setInputType(EditorInfo.TYPE_NULL);
        } else {
            etImportPrivateKey.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        }
    }

    @Override
    public void initListener() {
        blockBaseContent.getViewTreeObserver().addOnGlobalFocusChangeListener((oldFocus, newFocus) -> blockBaseMainup.setFocusView(newFocus, oldFocus, 1.2f));
        Disposable subscribeRight = RxView.clicks(ibRight)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    showTVLanguageSwitchDialog(onItemSelectListener);
                });
        unlockListener();
        createListener();
        importListener();

    }

    //解鎖錢包畫面監聽
    private void unlockListener() {
        Disposable subscribeUnlockWallet = RxView.clicks(btnUnlockWallet)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    hideSoftKeyboard();
                    if (WalletDBTool.existKeystoreInDB()) {
                        String password = etUnlockPwd.getPassword();
                        if (StringTool.notEmpty(password)) {
                            presenter.queryWalletFromDB(password);
                        } else {
                            showToast(getString(R.string.enter_password));
                        }
                    } else {
                        noWalletInfo();
                    }
                });

        Disposable subscribeTitle = RxView.clicks(tvTitle)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    finish();
                });
    }

    //創建錢包畫面監聽
    private void createListener() {
        cbPrivateKey.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (StringTool.isEmpty(visibleCreatePrivateKey)) {
                return;
            }
            tvPrivateKey.setText(isChecked ? visibleCreatePrivateKey : Constants.ValueMaps.DEFAULT_PRIVATE_KEY);

        });
        Disposable subscribeSure = RxView.clicks(btnCreateWallet)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    hideSoftKeyboard();
                    String btnString = btnCreateWallet.getText().toString();
                    // 檢查當前按鈕顯示的文本，如果為「完成」那麼點擊進入總攬
                    if (btnString.equals(getResources().getString(R.string.finish))) {
                        //开始「登入」
                        presenter.login();
                    } else {
                        String pwd = pketCreatePwd.getPassword();
                        String confirmPwd = pketCreateConfirmPwd.getPassword();
                        if (StringTool.isEmpty(pwd) || StringTool.isEmpty(confirmPwd)) {
                            showToast(getString(R.string.enter_password));
                        } else {
                            if (pwd.length() >= Constants.PASSWORD_MIN_LENGTH && confirmPwd.length() >= Constants.PASSWORD_MIN_LENGTH) {
                                if (RegexTool.isCharacter(pwd) && RegexTool.isCharacter(confirmPwd)) {
                                    if (StringTool.equals(pwd, confirmPwd)) {
                                        WalletBean walletBean = WalletTool.createAndSaveWallet(pwd);
                                        if (walletBean != null) {
                                            //顯示錢包信息，隱藏創建頁面
                                            svRlCreateWallet.setVisibility(View.VISIBLE);
                                            llCreateSetPwd.setVisibility(View.GONE);
                                            tvAccountAddress.setText(walletBean.getAddress());
                                            visibleCreatePrivateKey = walletBean.getPrivateKey();
                                            tvPrivateKey.setText(visibleCreatePrivateKey);
                                            btnCreateWallet.setText(getResources().getString(R.string.finish));

                                        }
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
                    }

                });

    }

    /*币种重新选择返回*/
    private OnItemSelectListener onItemSelectListener = new OnItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type) {
            if (type == null) {
                return;
            }
            //如果当前是「语言切换」
            if (type instanceof LanguageSwitchingBean) {
                switchLanguage(type);

            }
        }

        @Override
        public void changeItem(boolean isChange) {
        }
    };

    //導入錢包畫面監聽
    private void importListener() {

        etImportPrivateKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etImportPrivateKey.getText().toString();
                if (StringTool.notEmpty(content)) {
                    etImportPrivateKey.setSelection(content.length());
                }
                etImportPrivateKey.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                etImportPrivateKey.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) etImportPrivateKey.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(etImportPrivateKey, 0);
            }
        });
        etImportPrivateKey.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String content = etImportPrivateKey.getText().toString();
                    if (StringTool.isEmpty(content)) {
                        etImportPrivateKey.setInputType(InputType.TYPE_NULL);
                    }
                }

            }
        });
        Disposable subscribeImport = RxView.clicks(btnImportWallet)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    hideSoftKeyboard();
                    String btnString = btnImportWallet.getText().toString();
                    // 檢查當前按鈕顯示的文本，如果為「完成」那麼點擊進入總攬
                    if (btnString.equals(getResources().getString(R.string.finish))) {
                        String password = pketImportPwd.getPassword();
                        String passwordConfirm = pketImportConfirmPwd.getPassword();
                        if (StringTool.isEmpty(password) || StringTool.isEmpty(passwordConfirm)) {
                            showToast(getString(R.string.enter_password));
                        } else {
                            if (password.length() >= Constants.PASSWORD_MIN_LENGTH && passwordConfirm.length() >= Constants.PASSWORD_MIN_LENGTH) {
                                if (RegexTool.isCharacter(password) && RegexTool.isCharacter(passwordConfirm)) {
                                    if (StringTool.equals(password, passwordConfirm)) {
                                        BcaasApplication.setStringToSP(Constants.Preference.PASSWORD, password);
                                        WalletDBTool.insertWalletInDB(BcaasApplication.getWalletBean());
                                        // 开始「登入」
                                        presenter.login();
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

                    } else {
                        String privateKey = etImportPrivateKey.getText().toString();
                        if (StringTool.isEmpty(privateKey)) {
                            showToast(getResources().getString(R.string.enter_private_key));
                        } else {
                            if (WalletTool.parseWIFPrivateKey(privateKey)) {
                                //更換當前導入界面，顯示為錢包設置密碼
                                svRlImportWallet.setVisibility(View.VISIBLE);
                                rlImportSetPrivateKey.setVisibility(View.GONE);
                                btnImportWallet.setText(getResources().getString(R.string.finish));
                                btnImportWallet.requestFocus();
                                btnImportWallet.setFocusable(true);

                            } else {
                                showToast(getString(R.string.private_key_error));
                            }
                        }
                    }
                });
    }

    @Override
    public void showLoading() {
        if (!checkActivityState()) {
            return;
        }
        showLoadingDialog(getResources().getColor(R.color.orange_FC9003));
    }

    @Override
    public void hideLoading() {
        if (!checkActivityState()) {
            return;
        }
        hideLoadingDialog();
    }

    @Override
    public void noWalletInfo() {
        showToast(getResources().getString(R.string.no_wallet));
    }

    @Override
    public void loginFailure() {
        showToast(getResources().getString(R.string.login_failure));
    }

    @Override
    public void loginSuccess() {
        intentToHomeTv();

    }

    private void intentToHomeTv() {
        //通知訂閱，登錄成功
        OttoTool.getInstance().post(new LoginEvent());
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KeyMaps.From, Constants.ValueMaps.FROM_LOGIN);
        intentToActivity(bundle, HomeActivityTV.class, true);
    }

    @Override
    public void passwordError() {
        super.passwordError();
        showToast(getResources().getString(R.string.password_error));
    }

    @Override
    public void responseDataError() {
        showToast(getResources().getString(R.string.data_acquisition_error));
    }


    @Subscribe
    public void netStateChange(NetStateChangeEvent netStateChangeEvent) {
        if (netStateChangeEvent != null) {
            if (!netStateChangeEvent.isConnect()) {
                showToast(getResources().getString(R.string.network_not_reachable));
            }
            BcaasApplication.setRealNet(netStateChangeEvent.isConnect());

        }
    }
}
