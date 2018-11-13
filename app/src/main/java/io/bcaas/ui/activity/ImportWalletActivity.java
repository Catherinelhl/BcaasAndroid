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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.obt.qrcode.activity.CaptureActivity;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.constants.Constants;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.ecc.WalletTool;
import io.reactivex.disposables.Disposable;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * Activity：「導入錢包」
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
    @BindView(R.id.ib_scan)
    ImageButton ibScan;
    @BindView(R.id.et_private_key)
    EditText etPrivateKey;
    @BindView(R.id.btn_sure)
    Button btnSure;
    @BindView(R.id.rl_import_wallet)
    RelativeLayout rlImportWallet;
    @BindView(R.id.rl_private_key)
    RelativeLayout rlPrivateKey;
    // 跳轉拍照
    private int CAPTURE = 0x11;
    //跳轉設置密碼
    private int SET_PASSWORD = 0x12;

    @Override
    public int getContentView() {
        return R.layout.activity_import_wallet;
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
        rlPrivateKey.setOnTouchListener((v, event) -> true);
        ibBack.setOnClickListener(v -> setResult(true));
        Disposable subscribeSure = RxView.clicks(btnSure)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    hideSoftKeyboard();
                    String privateKey = etPrivateKey.getText().toString();
                    if (StringTool.isEmpty(privateKey)) {
                        showToast(getResources().getString(R.string.enter_private_key));
                    } else {
                        if (WalletTool.parseWIFPrivateKey(privateKey)) {
                            startActivityForResult(new Intent(this, SetPasswordForImportWalletActivity.class), SET_PASSWORD);
                        } else {
                            showToast(getString(R.string.private_key_error));
                        }
                    }

                });
        Disposable subscribeScan = RxView.clicks(ibScan)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    getCameraPermission();
                });
    }


    private void intentToCaptureActivity() {
        startActivityForResult(new Intent(this, CaptureActivity.class), CAPTURE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            if (requestCode == CAPTURE) {
                // 跳轉「拍照」返回
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    String result = bundle.getString(Constants.RESULT);
                    etPrivateKey.setText(result);
                    if (StringTool.notEmpty(result)) {
                        etPrivateKey.setSelection(result.length());
                    }
                }
            } else if (requestCode == SET_PASSWORD) {
                //跳轉「為導入的錢包設置密碼」返回
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    boolean isBack = bundle.getBoolean(Constants.KeyMaps.From);
                    LogTool.d(TAG, isBack);
                    if (!isBack) {
                        //點擊「導入」按鈕，那麼應該關閉當前頁面，然後進行登錄
                        setResult(false);
                    }
                }
            }
        }
    }

    private void setResult(boolean isBack) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.KeyMaps.From, isBack);
        intent.putExtras(bundle);
        this.setResult(RESULT_OK, intent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /*獲得照相機權限*/
    private void getCameraPermission() {
        if (Build.VERSION.SDK_INT > 22) {//这个说明系统版本在6.0之下，不需要动态获取权限
            if (ContextCompat.checkSelfPermission(ImportWalletActivity.this,
                    android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //先判断有没有权限 ，没有就在这里进行权限的申请
                ActivityCompat.requestPermissions(ImportWalletActivity.this,
                        new String[]{android.Manifest.permission.CAMERA}, Constants.KeyMaps.REQUEST_CODE_CAMERA_OK);

            } else {
                //说明已经获取到摄像头权限了 想干嘛干嘛
                intentToCaptureActivity();

            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.KeyMaps.REQUEST_CODE_CAMERA_OK:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //这里已经获取到了摄像头的权限，想干嘛干嘛了可以
                    intentToCaptureActivity();
                } else {
                    //这里是拒绝给APP摄像头权限，给个提示什么的说明一下都可以。
                    showToast(getString(R.string.to_setting_grant_permission));
                }
                break;
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

}
