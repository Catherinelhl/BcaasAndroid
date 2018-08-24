package io.bcaas.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.tools.StringTool;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 */
public class ImportWalletActivity extends BaseActivity {


    @BindView(R.id.ibBack)
    ImageButton ibBack;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ibRight)
    ImageButton ibRight;
    @BindView(R.id.rlHeader)
    RelativeLayout rlHeader;
    @BindView(R.id.ll_private_key)
    LinearLayout llPrivateKey;
    @BindView(R.id.et_privatekey)
    EditText etPrivateKey;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
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
                finish();
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
                // TODO: 2018/8/23 进行私钥的判断，然后进入首页
                intentToActivity(SetPwdForImportWalletActivity.class, true);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//TODO 回到登录界面
            }
        });

    }
}
