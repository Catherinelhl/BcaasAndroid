package io.bcaas.ui.activity.tv;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.tools.DateFormatTool;
import io.bcaas.ui.activity.CreateWalletActivity;
import io.bcaas.ui.activity.ImportWalletActivity;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/20
 * <p>
 * TV版首页
 */
public class MainActivityTV extends BaseActivity {

    @BindView(R.id.btn_unlock_wallet)
    Button btnUnlockWallet;
    @BindView(R.id.btn_create_wallet)
    Button btnCreateWallet;
    @BindView(R.id.btn_import_wallet)
    Button btnImportWallet;
    @BindView(R.id.tv_home)
    TextView tvHome;
    @BindView(R.id.cv_home)
    CardView cvHome;
    @BindView(R.id.tv_send)
    TextView tvSend;
    @BindView(R.id.cv_send)
    CardView cvSend;
    @BindView(R.id.tv_setting)
    TextView tvSetting;
    @BindView(R.id.cv_setting)
    CardView cvSetting;
    @BindView(R.id.tv_current_time)
    TextView tvCurrentTime;

    @Override
    public boolean full() {
        return false;
    }

    @Override
    public int getContentView() {
        return R.layout.activity_main_tv;
    }

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public void initViews() {
        tvCurrentTime.setText(DateFormatTool.getCurrentTime());

    }

    @Override
    public void initListener() {
        btnImportWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentToActivity(ImportWalletActivity.class);
            }
        });
        btnCreateWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentToActivity(CreateWalletActivity.class);

            }
        });
    }
}
