package io.bcaas.ui.activity;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.constants.Constants;
import io.bcaas.event.ToLogin;
import io.bcaas.tools.OttoTool;
import io.bcaas.view.LineEditText;
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
    @BindView(R.id.let_private_key)
    LineEditText letPrivateKey;
    @BindView(R.id.cbPwd)
    CheckBox cbPwd;
    @BindView(R.id.btn_finish)
    Button btnFinish;
    private String accountAddress, privateKey;// 账户地址，私钥,区块服务名称

    @Override
    public int getContentView() {
        return R.layout.activity_wallet_created_success;
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
        letPrivateKey.setText(privateKey);
        letPrivateKey.setFocusable(false);
        tvAccountAddress.setFocusable(false);
        tvTitle.setText(getResources().getString(R.string.create_new_wallet));
    }

    @Override
    public void initListener() {
        cbPwd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            letPrivateKey.setInputType(isChecked ?
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);//设置当前私钥显示不可见
        });
        Disposable subscribeFinish = RxView.clicks(btnFinish)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    OttoTool.getInstance().post(new ToLogin());
                    finish();
                });
        ibBack.setOnClickListener(v -> finish());

    }

}
