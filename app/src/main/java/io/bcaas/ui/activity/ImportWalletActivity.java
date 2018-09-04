package io.bcaas.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.obt.qrcode.activity.CaptureActivity;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.bcaas.BuildConfig;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.ecc.Wallet;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.WalletTool;
import io.reactivex.disposables.Disposable;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 */
public class ImportWalletActivity extends BaseActivity {

    private String TAG = ImportWalletActivity.class.getSimpleName();

    @BindView(R.id.ib_back)
    ImageButton ibBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ib_right)
    ImageButton ibRight;
    @BindView(R.id.rl_header)
    RelativeLayout rlHeader;
    @BindView(R.id.ll_private_key)
    LinearLayout llPrivateKey;
    @BindView(R.id.et_privatekey)
    EditText etPrivateKey;
    @BindView(R.id.btn_sure)
    Button btnSure;
    @BindView(R.id.rl_import_wallet)
    RelativeLayout rlImportWallet;

    @Override
    public int getContentView() {
        return R.layout.activity_import_wallet;
    }

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public void initViews() {
        tvTitle.setText(getResources().getString(R.string.import_wallet));
        ibBack.setVisibility(View.VISIBLE);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initListener() {
        rlImportWallet.setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            return false;
        });
        ibBack.setOnClickListener(v -> finishActivity());
        tvTitle.setOnLongClickListener(v -> {
            if (BuildConfig.DEBUG) {
                getCameraPermission();
            }
            return false;
        });

        etPrivateKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String privateKey = s.toString();
                if (StringTool.notEmpty(privateKey)) {
                    if (privateKey.length() == 51) {
                        btnSure.setEnabled(true);
                    }
                }
            }
        });
        Disposable subscribeSure = RxView.clicks(btnSure)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    String privateKey = etPrivateKey.getText().toString();
                    if (StringTool.isEmpty(privateKey)) {
                        showToast(getResources().getString(R.string.enter_private_key));
                        return;
                    }
                    if (parseWIFPrivateKey(privateKey)) {
                        intentToActivity(SetPasswordForImportWalletActivity.class, true);
                    } else {
                        showToast(getString(R.string.private_key_error));
                    }
                });
    }

    /**
     * 解析当前私钥，得到新的钱包地址信息
     *
     * @param WIFPrivateKey
     * @return 如果返回false，代表不通过，需要用户重新输入
     */
    private boolean parseWIFPrivateKey(String WIFPrivateKey) {
        Wallet wallet = WalletTool.getWalletInfo(WIFPrivateKey);
        if (wallet == null) {
            //数据解析异常，可能是私钥格式不正确，提示其重新输入
            return false;
        }
        BcaasApplication.setStringToSP(Constants.Preference.BLOCK_SERVICE,  Constants.BLOCKSERVICE_BCC);
        BcaasApplication.setStringToSP(Constants.Preference.PUBLIC_KEY, wallet.getPublicKey());
        BcaasApplication.setStringToSP(Constants.Preference.PRIVATE_KEY, wallet.getPrivateKey());
        BcaasApplication.setWallet(wallet);//将当前的账户地址赋给Application，这样就不用每次都去操作数据库
        BcaasLog.d(TAG, wallet);
        return true;
    }

    private void intentToCaptureActivity() {
        startActivityForResult(new Intent(this, CaptureActivity.class), 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String result = bundle.getString(Constants.RESULT);
                etPrivateKey.setText(result);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishActivity();
    }

    private void finishActivity() {
        finish();
    }


    /*獲得照相機權限*/
    private void getCameraPermission() {
        BcaasLog.d(TAG, Build.VERSION.SDK_INT > 22);
        if (Build.VERSION.SDK_INT > 22) {
            if (ContextCompat.checkSelfPermission(ImportWalletActivity.this,
                    android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //先判断有没有权限 ，没有就在这里进行权限的申请
                ActivityCompat.requestPermissions(ImportWalletActivity.this,
                        new String[]{android.Manifest.permission.CAMERA}, Constants.KeyMaps.CAMERA_OK);

            } else {
                //说明已经获取到摄像头权限了 想干嘛干嘛
                intentToCaptureActivity();

            }
        } else {
            //这个说明系统版本在6.0之下，不需要动态获取权限。
            BcaasLog.d(TAG);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.KeyMaps.CAMERA_OK:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //这里已经获取到了摄像头的权限，想干嘛干嘛了可以
                    intentToCaptureActivity();
                } else {
                    //这里是拒绝给APP摄像头权限，给个提示什么的说明一下都可以。
                    showToast(getString(R.string.please_open_camera_permission));
                }
                break;
        }
    }
}
