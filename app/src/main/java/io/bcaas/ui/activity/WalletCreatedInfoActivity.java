package io.bcaas.ui.activity;

import android.os.Bundle;
import android.text.InputType;
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
import butterknife.ButterKnife;
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
    @BindView(R.id.cbPwd)
    CheckBox cbPwd;
    @BindView(R.id.rl_private_key)
    RelativeLayout rlPrivateKey;
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
        etPrivateKey.setText(privateKey);
        etPrivateKey.setFocusable(false);
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
            etPrivateKey.setInputType(isChecked ?
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD | InputType.TYPE_TEXT_FLAG_MULTI_LINE :
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_TEXT_FLAG_MULTI_LINE);//设置当前私钥显示不可见
        });
        etPrivateKey.setOnLongClickListener(view -> {
            // TODO: 2018/9/7 长按复制
            String privateKey = etPrivateKey.getText().toString();
            if (StringTool.notEmpty(privateKey)) {
                if (cbPwd.isChecked()) {
                    showDetailPop(etPrivateKey, privateKey);
                }
            }
            return false;
        });
        Disposable subscribeFinish = RxView.clicks(btnFinish)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    OttoTool.getInstance().post(new LoginEvent());
                    finish();
                });
        ibBack.setOnClickListener(v -> finish());

    }
}
