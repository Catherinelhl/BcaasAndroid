package io.bcaas.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.constants.Constants;
import io.bcaas.event.LoginEvent;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.StringTool;
import io.reactivex.disposables.Disposable;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * 钱包创建成功显示钱包信息
 */
public class WalletCreatedInfoActivity extends BaseActivity {

    @BindView(R.id.ib_back)
    ImageButton ibBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ib_right)
    ImageButton ibRight;
    @BindView(R.id.rl_header)
    RelativeLayout rlHeader;
    @BindView(R.id.tv_account_address)
    TextView tvAccountAddress;
    @BindView(R.id.btn_finish)
    Button btnFinish;
    @BindView(R.id.et_private_key)
    EditText etPrivateKey;
    @BindView(R.id.cb_pwd)
    CheckBox cbPwd;
    @BindView(R.id.rl_private_key)
    RelativeLayout rlPrivateKey;
    private String accountAddress, privateKey;// 账户地址，私钥,区块服务名称

    @Override
    public int getContentView() {
        return R.layout.activity_wallet_created_success;
    }

    @Override
    public boolean full() {
        return false;
    }

    @Override
    public void getArgs(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        accountAddress = bundle.getString(Constants.KeyMaps.WALLET_ADDRESS);
        privateKey = bundle.getString(Constants.KeyMaps.PRIVATE_KEY);

    }

    @Override
    public void initViews() {
        ibBack.setVisibility(View.VISIBLE);
        tvAccountAddress.setHint(accountAddress);
        if (StringTool.notEmpty(privateKey)) {
            cbPwd.setChecked(true);
            etPrivateKey.setText(privateKey);
            //设置editText不可编辑，但是可以复制
            etPrivateKey.setKeyListener(null);
            etPrivateKey.setSelection(privateKey.length());
        }

        tvAccountAddress.setFocusable(false);
        tvTitle.setText(getResources().getString(R.string.create_new_wallet));
    }

    @Override
    public void initListener() {
        cbPwd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String text = etPrivateKey.getText().toString();
            if (StringTool.isEmpty(text)) {
                return;
            }
            if (isChecked) {
                etPrivateKey.setText(privateKey);
            } else {
                etPrivateKey.setText(Constants.ValueMaps.DEFAULT_PRIVATE_KEY);
            }
        });
        Disposable subscribeFinish = RxView.clicks(btnFinish)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    OttoTool.getInstance().post(new LoginEvent());
                    finish();
                });
        ibBack.setOnClickListener(v -> finish());

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
