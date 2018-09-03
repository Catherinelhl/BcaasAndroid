package io.bcaas.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import io.bcaas.http.MasterServices;
import io.bcaas.tools.StringTool;
import io.reactivex.disposables.Disposable;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/1
 * <p>
 * 修改授权代表
 */
public class ModifyAuthorizedRepresentativesActivity extends BaseActivity {
    private String TAG = ModifyAuthorizedRepresentativesActivity.class.getSimpleName();
    @BindView(R.id.ib_back)
    ImageButton ibBack;
    @BindView(R.id.ib_close)
    ImageButton ibClose;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ib_right)
    ImageButton ibRight;
    @BindView(R.id.rl_header)
    RelativeLayout rlHeader;
    @BindView(R.id.tv_account_address)
    TextView tvAccountAddress;
    @BindView(R.id.et_input_representatives)
    EditText etInputRepresentatives;
    @BindView(R.id.btn_sure)
    Button btnSure;
    @BindView(R.id.ll_modify_authorized_representatives)
    LinearLayout llModifyAuthorizedRepresentatives;

    @Override
    public int getContentView() {
        return R.layout.activity_modify_authorized_presentatives;
    }

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public void initViews() {
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(getResources().getString(R.string.modify_authorized_representatives));
        tvAccountAddress.setText(BcaasApplication.getWalletAddress());
        ibBack.setVisibility(View.VISIBLE);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initListener() {
        llModifyAuthorizedRepresentatives.setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            return false;
        });
        ibBack.setOnClickListener(v -> {
            finish();
        });
        tvTitle.setOnLongClickListener(v -> {
            if (BuildConfig.DEBUG) {
                startActivityForResult(new Intent(this, CaptureActivity.class), 0);

            }
            return false;
        });

        Disposable subscribeSure = RxView.clicks(btnSure)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    String representative = etInputRepresentatives.getText().toString();
                    if (StringTool.notEmpty(representative)) {
                        //請求getLastChangeBlock接口，取得更換委託人區塊
                        MasterServices.getLatestChangeBlock();
                    }
                });
        etInputRepresentatives.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String representative = s.toString();
                if (StringTool.notEmpty(representative)) {
                    btnSure.setEnabled(true);
                }
            }
        });

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
                etInputRepresentatives.setText(result);
            }
        }
    }
}