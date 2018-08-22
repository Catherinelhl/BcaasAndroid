package io.bcaas.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import io.bcaas.event.SwitchTab;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.StringTool;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 * <p>
 * <p>
 * 发送页面点击「发送」然后跳转到此页面进行密码到确认，点击「确认」，进行网络的请求，然后返回到「首页」
 */
public class SendToConfirmPwdActivity extends BaseActivity {


    @BindView(R.id.ibBack)
    ImageButton ibBack;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ibRight)
    ImageButton ibRight;
    @BindView(R.id.rlHeader)
    RelativeLayout rlHeader;
    @BindView(R.id.tvTransactionDetailKey)
    TextView tvTransactionDetailKey;
    @BindView(R.id.tvTransactionDetail)
    TextView tvTransactionDetail;
    @BindView(R.id.tvReceiveAccountKey)
    TextView tvReceiveAccountKey;
    @BindView(R.id.tvReceiveAccountValue)
    TextView tvReceiveAccountValue;
    @BindView(R.id.et_private_key)
    EditText etPrivateKey;
    @BindView(R.id.cbPwd)
    CheckBox cbPwd;
    @BindView(R.id.btnSend)
    Button btnSend;
    private String receiveCurrency, receiveAddress, transactionAmount;//获取上一个页面传输过来的接收方的币种以及地址信息,以及交易数额

    @Override
    public int getContentView() {
        return R.layout.aty_send_toconfirm_pwd;
    }

    @Override
    public void getArgs(Bundle bundle) {
        if (bundle == null) return;
        receiveCurrency = bundle.getString(Constants.KeyMaps.RECEIVECURRENCY);
        receiveAddress = bundle.getString(Constants.KeyMaps.RECEIVEADDRESS);
        transactionAmount = bundle.getString(Constants.KeyMaps.TRANSACTIONAMOUNT);


    }

    @Override
    public void initViews() {
        ibBack.setVisibility(View.VISIBLE);
        tvTitle.setText(getResources().getString(R.string.send));
        tvTransactionDetailKey.setText(String.format("向%s转账", transactionAmount));
        tvReceiveAccountValue.setHint(receiveAddress);
        tvTransactionDetail.setText(receiveCurrency);
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
        etPrivateKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String pwd = s.toString();
                btnSend.setPressed(StringTool.notEmpty(pwd));
            }
        });
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 存储当前的信息，并且返回到首页
                String pwd = etPrivateKey.getText().toString();
                if (StringTool.isEmpty(pwd)) {
                    showToast("请确认密码的输入！");
                } else {
                    OttoTool.getInstance().post(new SwitchTab(0));
                    finish();
                }
            }
        });

    }
}
