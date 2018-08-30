package io.bcaas.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import io.bcaas.db.vo.Address;
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

    @Override
    public int getContentView() {
        return R.layout.aty_import_wallet;
    }

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public void initViews() {
        tvTitle.setText(getResources().getString(R.string.import_wallet));
        ibBack.setVisibility(View.VISIBLE);

    }

    @Override
    public void initListener() {
        ibBack.setOnClickListener(v -> finishActivity());
        tvTitle.setOnLongClickListener(v -> {
            if (BuildConfig.DEBUG) {
                intentToCaptureActivity();
            }
            return false;
        });

        Disposable subscribeSure = RxView.clicks(btnSure)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    String privateKey = etPrivateKey.getText().toString();
                    if (StringTool.isEmpty(privateKey)) {
                        showToast(getResources().getString(R.string.input_private_key));
                        return;
                    }
                    if (parseWIFPrivateKey(privateKey)) {
                        intentToActivity(SetPwdForImportWalletActivity.class, true);
                    } else {
                        showToast(getString(R.string.private_key_format_exception));
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
        BcaasApplication.setBlockServiceToSP(Constants.BlockService.BCC);
        BcaasApplication.setPublicKeyToSP(wallet.getPublicKey());
        BcaasApplication.setPrivateKeyToSP(wallet.getPrivateKey());
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
}
