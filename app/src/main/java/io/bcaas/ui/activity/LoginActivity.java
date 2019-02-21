package io.bcaas.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.Gravity;
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
import io.bcaas.constants.MessageConstants;
import io.bcaas.event.NetStateChangeEvent;
import io.bcaas.listener.SoftKeyBroadManager;
import io.bcaas.presenter.LoginPresenterImp;
import io.bcaas.tools.*;
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
 * Activity：登錄界面
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
        bindDownloadService();
        presenter = new LoginPresenterImp(this);
        //设置打开后100ms后再弹出教学页面，否则会有渲染差异
        ObservableTimerTool.countDownTimerBySetTime(Constants.Time.sleep100, TimeUnit.MILLISECONDS, from -> initGuideView());
        setAppVersion();
    }

    /**
     * APP当前的版本信息
     */
    private void setAppVersion() {
        LogTool.d(TAG, "当前Build ：" + VersionTool.getVersionCode(this));
        tvVersion.setText(String.format(getString(R.string.two_place_holders),
                getResources().getString(R.string.version_name),
                VersionTool.getVersionName(this)));
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
                .throttleFirst(Constants.Time.sleep800, TimeUnit.MILLISECONDS)
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
                .throttleFirst(Constants.Time.sleep800, TimeUnit.MILLISECONDS)
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
                                        startActivityForResult(new Intent(BCAASApplication.context(), CreateWalletActivity.class), Constants.REQUEST_CODE_CREATE);
                                    }

                                    @Override
                                    public void cancel() {

                                    }
                                });
                    } else {
                        startActivityForResult(new Intent(BCAASApplication.context(), CreateWalletActivity.class), Constants.REQUEST_CODE_CREATE);
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
                                startActivityForResult(new Intent(BCAASApplication.context(), ImportWalletActivity.class), Constants.REQUEST_CODE_IMPORT);
                            }

                            @Override
                            public void cancel() {

                            }
                        });
            } else {
                startActivityForResult(new Intent(BCAASApplication.context(), ImportWalletActivity.class), Constants.REQUEST_CODE_IMPORT);
            }
        });
        tvVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BuildConfig.ChangeServer) {
                    if (multipleClickToDo(2)) {
                        intentToActivity(ChangeServerActivity.class);

                    }
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
            if (requestCode == Constants.REQUEST_CODE_IMPORT) {
                // 跳轉「導入」返回
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    boolean isBack = bundle.getBoolean(Constants.KeyMaps.From);
                    if (!isBack) {
                        //點擊導入回來，然後進行登錄
                        loginWallet();
                    }
                }
            } else if (requestCode == Constants.REQUEST_CODE_CREATE) {
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
            presenter.getRealIpForLoginRequest();
        }
    }

    public void initGuideView() {
        initCreateWalletGuideView();
        initImportWalletGuideView();
        initUnLockGuideView();

    }

    private void initUnLockGuideView() {
        View view = LayoutInflater.from(this).inflate(R.layout.help_view_login, null);
        LinearLayout linearLayout = view.findViewById(R.id.ll_guide);
        TextView textView = view.findViewById(R.id.tv_content);
        textView.setText(getResources().getString(R.string.input_correct_password_unlock));
        linearLayout.setGravity(Gravity.CENTER);
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
                .setDirction(GuideView.Direction.CENTER_BOTTOM)
                .setShape(GuideView.MyShape.NO_LIGHT)
                .setRadius(DensityTool.dip2px(BCAASApplication.context(), 6))
                .setBgColor(getResources().getColor(R.color.black80))
                .build();
        guideViewUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideViewUnlock.hide();
            }
        });

    }

    private void initCreateWalletGuideView() {
        View view = LayoutInflater.from(this).inflate(R.layout.help_view_login, null);
        LinearLayout linearLayout = view.findViewById(R.id.ll_guide);
        linearLayout.setGravity(Gravity.LEFT);
        TextView textView = view.findViewById(R.id.tv_content);
        textView.setText(getResources().getString(R.string.touch_can_create_new_wallet));
        ImageView imageView = view.findViewById(R.id.iv_gesture);
        int width = getResources().getDimensionPixelOffset(R.dimen.d40);
        int margin = getResources().getDimensionPixelOffset(R.dimen.d40);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, width);
        layoutParams.setMargins(margin, 0, 0, 0);
        imageView.setLayoutParams(layoutParams);
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
                .setDirction(GuideView.Direction.CENTER_BOTTOM)
                .setShape(GuideView.MyShape.NO_LIGHT)
                .setRadius(DensityTool.dip2px(BCAASApplication.context(), 6))
                .setBgColor(getResources().getColor(R.color.black80))
                .build();
        guideViewCreate.show(Constants.Preference.GUIDE_CREATE);
        guideViewCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideViewCreate.hide();
                guideViewImport.show(Constants.Preference.GUIDE_IMPORT);

            }
        });

    }

    private void initImportWalletGuideView() {
        View view = LayoutInflater.from(this).inflate(R.layout.help_view_login, null);
        LinearLayout linearLayout = view.findViewById(R.id.ll_guide);
        TextView textView = view.findViewById(R.id.tv_content);
        textView.setText(getResources().getString(R.string.can_import_wallet));
        linearLayout.setGravity(Gravity.RIGHT);
        ImageView imageView = view.findViewById(R.id.iv_gesture);
        int width = getResources().getDimensionPixelOffset(R.dimen.d40);
        int margin = getResources().getDimensionPixelOffset(R.dimen.d40);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, width);
        layoutParams.setMargins(0, 0, margin, 0);
        imageView.setLayoutParams(layoutParams);
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
                .setDirction(GuideView.Direction.CENTER_BOTTOM)
                .setShape(GuideView.MyShape.NO_LIGHT)
                .setRadius(DensityTool.dip2px(BCAASApplication.context(), 6))
                .setBgColor(getResources().getColor(R.color.black80))
                .build();
        guideViewImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideViewImport.hide();
                guideViewUnlock.show(Constants.Preference.GUIDE_UNLOCK);

            }
        });
    }

    /**
     * 更新版本
     *
     * @param forceUpgrade 是否强制更新
     * @param appStoreUrl  APP store 下载地址
     * @param updateUrl    程序内部下载地址
     */
    @Override
    public void updateVersion(boolean forceUpgrade, String appStoreUrl, String updateUrl) {
        updateAndroidAPKURL = updateUrl;
        if (forceUpgrade) {
            showBcaasSingleDialog(getResources().getString(R.string.app_need_update), () -> {
                // 开始后台执行下载应用，或许直接跳转应用商店
                intentToGooglePlay(appStoreUrl);
            });
        } else {
            showBcaasDialog(getResources().getString(R.string.app_need_update), new BcaasDialog.ConfirmClickListener() {
                @Override
                public void sure() {
                    // 开始后台执行下载应用，或许直接跳转应用商店
                    intentToGooglePlay(appStoreUrl);
                }

                @Override
                public void cancel() {

                }
            });
        }
    }

    @Override
    public void getAndroidVersionInfoFailure() {

    }

    /**
     * 跳转google商店
     *
     * @param appStoreUrl
     */
    private void intentToGooglePlay(String appStoreUrl) {
        LogTool.d(TAG, MessageConstants.INTENT_GOOGLE_PLAY + appStoreUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 打开google应用市场
        intent.setPackage("com.android.vending");
        LogTool.d(TAG, Uri.parse(MessageConstants.GOOGLE_PLAY_MARKET + getPackageName()));
        //存在手机里没安装应用市场的情况，跳转会包异常，做一个接收判断
        if (intent.resolveActivity(getPackageManager()) != null) { //可以接收
            startActivity(intent);
        } else {
            //没有应用市场，我们通过浏览器跳转到Google Play
            intent.setData(Uri.parse(appStoreUrl));
            //这里存在一个极端情况就是有些用户浏览器也没有，再判断一次
            if (intent.resolveActivity(getPackageManager()) != null) { //有浏览器
                startActivity(intent);
            } else {
                //否则跳转应用内下载
                startAppSYNCDownload();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (presenter != null) {
            // 检查当前版本信息
            presenter.getAndroidVersionInfo();
        }
    }
}
