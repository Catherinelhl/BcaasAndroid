package io.bcaas.ui.activity;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.constants.Constants;
import io.bcaas.event.ToLogin;
import io.bcaas.tools.OttoTool;
import io.bcaas.vo.WalletVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * 钱包创建成功
 */
public class WalletCreatedSuccessActivity extends BaseActivity {


    @BindView(R.id.ibBack)
    ImageButton ibBack;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ibRight)
    ImageButton ibRight;
    @BindView(R.id.rlHeader)
    RelativeLayout rlHeader;
    @BindView(R.id.tv_account_address)
    TextView tvAccountAddress;
    @BindView(R.id.et_private_key)
    EditText etPrivateKey;
    @BindView(R.id.cbPwd)
    CheckBox cbPwd;
    @BindView(R.id.btn_finish)
    Button btnFinish;
    private String accountAddress, privateKey, blockService;// 账户地址，私钥,区块服务名称
    private WalletVO walletVO;

    @Override
    public int getContentView() {
        return R.layout.aty_wallet_created_success;
    }

    @Override
    public void getArgs(Bundle bundle) {
        if (bundle == null) return;
        accountAddress = bundle.getString(Constants.KeyMaps.AccountAddress);
        privateKey = bundle.getString(Constants.KeyMaps.PrivateKey);
        blockService = bundle.getString(Constants.KeyMaps.BlockService);

    }

    @Override
    public void initViews() {
        walletVO = new WalletVO();
        walletVO.setWalletAddress(accountAddress);
//        walletVO.setBlockService(blockService); //08-21 「登入」去掉此参数
        ibBack.setVisibility(View.VISIBLE);
        tvAccountAddress.setHint(accountAddress);
        etPrivateKey.setText(privateKey);
        etPrivateKey.setFocusable(false);
        tvAccountAddress.setFocusable(false);
        tvTitle.setText(getResources().getString(R.string.create_new_wallet));
    }

    @Override
    public void initListener() {
        cbPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etPrivateKey.setInputType(isChecked ?
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);//设置当前私钥显示不可见
            }
        });
        btnFinish.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                OttoTool.getInstance().post(new ToLogin(walletVO));
                finish();
            }
        });
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
