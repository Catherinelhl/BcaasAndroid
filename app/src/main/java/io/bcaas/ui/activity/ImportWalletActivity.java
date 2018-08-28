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

import com.obt.qrcode.activity.CaptureActivity;

import butterknife.BindView;
import io.bcaas.BuildConfig;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.constants.Constants;
import io.bcaas.tools.StringTool;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 */
public class ImportWalletActivity extends BaseActivity {


    @BindView(R.id.ib_back)
    ImageButton ibBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ib_right)
    ImageButton ibRight;
    @BindView(R.id.rlHeader)
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
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });
        tvTitle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (BuildConfig.DEBUG) {
                    intentToCaptureActivity();
                }
                return false;
            }
        });
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String privateKey = etPrivateKey.getText().toString();
                if (StringTool.isEmpty(privateKey)) {
                    showToast(getResources().getString(R.string.input_private_key));
                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putString(Constants.WIF_PRIVATE_KEY, privateKey);
                intentToActivity(bundle, SetPwdForImportWalletActivity.class, true);
            }
        });
    }

    private void intentToCaptureActivity() {
        startActivityForResult(new Intent(this, CaptureActivity.class), 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) return;
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