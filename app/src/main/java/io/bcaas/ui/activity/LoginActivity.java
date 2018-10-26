package io.bcaas.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import com.jakewharton.rxbinding2.view.RxView;
import com.squareup.otto.Subscribe;
import io.bcaas.BuildConfig;
import io.bcaas.R;
import io.bcaas.base.BCAASApplication;
import io.bcaas.base.BaseActivity;
import io.bcaas.constants.Constants;
import io.bcaas.event.NetStateChangeEvent;
import io.bcaas.listener.SoftKeyBroadManager;
import io.bcaas.presenter.LoginPresenterImp;
import io.bcaas.tools.ActivityTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.VersionTool;
import io.bcaas.tools.wallet.WalletDBTool;
import io.bcaas.ui.contracts.LoginContracts;
import io.bcaas.view.dialog.BcaasDialog;
import io.bcaas.view.guide.GuideView;
import io.reactivex.disposables.Disposable;

import java.util.concurrent.TimeUnit;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 是否以LoginActivity为当前账户登录的主要Activity，保持此activity不finish，然后跳转创建、或者导入
 * 钱包的界面，操作结束的时候，返回到当前页面，然后进入MainActivity。
 */
public class LoginActivity extends BaseActivity
        implements LoginContracts.View {
    private String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.cb_pwd)
    CheckBox cbPwd;
    @BindView(R.id.v_password_line)
    View vPasswordLine;
    @BindView(R.id.btn_unlock_wallet)
    Button btnUnlockWallet;
    @BindView(R.id.tv_create_wallet)
    TextView tvCreateWallet;
    @BindView(R.id.tv_import_wallet)
    TextView tvImportWallet;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.ll_login)
    LinearLayout llLogin;
    @BindView(R.id.ll_password_key)
    LinearLayout llPasswordKey;

    private LoginContracts.Presenter presenter;

    private GuideView guideViewUnlock;
    private GuideView guideViewCreate;
    private GuideView guideViewImport;

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
        addSoftKeyBroadManager();
        vPasswordLine.setVisibility(View.GONE);
        presenter = new LoginPresenterImp(this);
        getAppVersion();
        setGuideView();

    }

    private void getAppVersion() {
        tvVersion.setText(String.format(getString(R.string.two_place_holders),
                getResources().getString(R.string.version_name),
                VersionTool.getVersionName(this) + "(" + VersionTool.getVersionCode(this) + ")"));
    }

    /**
     * 添加软键盘监听
     */
    private void addSoftKeyBroadManager() {
        softKeyBroadManager = new SoftKeyBroadManager(btnUnlockWallet, tvImportWallet);
        softKeyBroadManager.addSoftKeyboardStateListener(softKeyboardStateListener);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initListener() {
        llLogin.setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            return false;
        });
        llPasswordKey.setOnTouchListener((v, event) -> true);
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
                    hideSoftKeyboard();
                    if (WalletDBTool.existKeystoreInDB()) {
                        String password = etPassword.getText().toString();
                        if (StringTool.notEmpty(password)) {
                            presenter.queryWalletFromDB(password);
                        } else {
                            showToast(getString(R.string.enter_password));
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
                    if (WalletDBTool.existKeystoreInDB()) {
                        showBcaasDialog(getResources().getString(R.string.warning),
                                getResources().getString(R.string.confirm),
                                getResources().getString(R.string.cancel),
                                getString(R.string.create_wallet_dialog_message), new BcaasDialog.ConfirmClickListener() {
                                    @Override
                                    public void sure() {
                                        startActivityForResult(new Intent(BCAASApplication.context(), CreateWalletActivity.class), Constants.KeyMaps.REQUEST_CODE_CREATE);
                                    }

                                    @Override
                                    public void cancel() {

                                    }
                                });
                    } else {
                        startActivityForResult(new Intent(BCAASApplication.context(), CreateWalletActivity.class), Constants.KeyMaps.REQUEST_CODE_CREATE);
                    }
                });
        tvImportWallet.setOnClickListener(v -> {
            //1：若客户没有存储钱包信息，直接进入导入钱包页面
            //2：若客户端已经存储了钱包信息，需做如下提示
            if (WalletDBTool.existKeystoreInDB()) {
                showBcaasDialog(getResources().getString(R.string.warning),
                        getResources().getString(R.string.confirm),
                        getResources().getString(R.string.cancel),
                        getResources().getString(R.string.import_wallet_dialog_message), new BcaasDialog.ConfirmClickListener() {
                            @Override
                            public void sure() {
                                startActivityForResult(new Intent(BCAASApplication.context(), ImportWalletActivity.class), Constants.KeyMaps.REQUEST_CODE_IMPORT);
                            }

                            @Override
                            public void cancel() {

                            }
                        });
            } else {
                startActivityForResult(new Intent(BCAASApplication.context(), ImportWalletActivity.class), Constants.KeyMaps.REQUEST_CODE_IMPORT);
            }
        });
        tvVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BuildConfig.ChangeServer) {
                    intentToActivity(ChangeServerActivity.class);
                }
            }
        });

    }


    @Override
    public void noWalletInfo() {
        showToast(getResources().getString(R.string.no_wallet));
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
    public void noData() {
        showToast(getResources().getString(R.string.account_data_error));
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
            BCAASApplication.setRealNet(netStateChangeEvent.isConnect());

        }
    }

    @Override
    public void showLoading() {
        if (!checkActivityState()) {
            return;
        }
        showLoadingDialog();
    }

    @Override
    public void hideLoading() {
        if (!checkActivityState()) {
            return;
        }
        hideLoadingDialog();
    }

    @Override
    protected void onPause() {
        hideLoadingDialog();
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            LogTool.d(TAG, requestCode);
            if (requestCode == Constants.KeyMaps.REQUEST_CODE_IMPORT) {
                // 跳轉「導入」返回
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    boolean isBack = bundle.getBoolean(Constants.KeyMaps.From);
                    if (!isBack) {
                        //點擊導入回來，然後進行登錄
                        loginWallet();
                    }
                }
            } else if (requestCode == Constants.KeyMaps.REQUEST_CODE_CREATE) {
                //跳轉「創建」返回
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    boolean isBack = bundle.getBoolean(Constants.KeyMaps.From);
                    LogTool.d(TAG, isBack);
                    if (!isBack) {
                        loginWallet();
                    }
                }
            }
        }
    }

    //「導入」、「創建」、「解鎖」點擊之後前去請求「登錄」
    private void loginWallet() {
        //點擊創建回來，然後進行登錄
        if (presenter != null) {
            presenter.login();
        }
    }

    public void setGuideView() {
        initCreateWalletGuideView();
        initImportWalletGuideView();
        initUnLockGuideView();

    }

    private void initUnLockGuideView() {
        View view = LayoutInflater.from(this).inflate(R.layout.help_view_login, null);
        Button button = view.findViewById(R.id.btn_next);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideViewUnlock.hide();
            }
        });

        guideViewUnlock = GuideView.Builder
                .newInstance(this)
                .setTargetView(btnUnlockWallet)//设置目标
                .setIsDraw(false)
                .setCustomGuideView(view)
                .setDirction(GuideView.Direction.BOTTOM)
                .setShape(GuideView.MyShape.RECTANGULAR)
                .setRadius(18)
                .setBgColor(getResources().getColor(R.color.black80))
                .build();

    }

    private void initCreateWalletGuideView() {
        View view = LayoutInflater.from(this).inflate(R.layout.help_view_login_create, null);
        Button button = view.findViewById(R.id.btn_next);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideViewCreate.hide();
                guideViewImport.show(Constants.Preference.GUIDE_IMPORT);

            }
        });
        guideViewCreate = GuideView.Builder
                .newInstance(this)
                .setTargetView(tvCreateWallet)//设置目标
                .setIsDraw(false)
                .setCustomGuideView(view)
                .setDirction(GuideView.Direction.BOTTOM)
                .setShape(GuideView.MyShape.RECTANGULAR)
                .setRadius(18)
                .setBgColor(getResources().getColor(R.color.black80))
                .build();
        guideViewCreate.show(Constants.Preference.GUIDE_CREATE);


    }

    private void initImportWalletGuideView() {
        View view = LayoutInflater.from(this).inflate(R.layout.help_view_login_import, null);
        Button button = view.findViewById(R.id.btn_next);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideViewImport.hide();
                guideViewUnlock.show(Constants.Preference.GUIDE_UNLOCK);
            }
        });

        guideViewImport = GuideView.Builder
                .newInstance(this)
                .setTargetView(tvImportWallet)//设置目标
                .setIsDraw(false)
                .setCustomGuideView(view)
                .setDirction(GuideView.Direction.BOTTOM)
                .setShape(GuideView.MyShape.RECTANGULAR)
                .setRadius(18)
                .setBgColor(getResources().getColor(R.color.black80))
                .build();
    }
}
