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


import com.squareup.otto.Subscribe;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BaseAuthNodePresenterImp;
import io.bcaas.base.BaseAuthNodeView;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.event.RefreshSendStatus;
import io.bcaas.event.SwitchTab;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.StringTool;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 * <p>
 * <p>
 * 发送页面点击「发送」然后跳转到此页面进行密码到确认，点击「确认」，进行网络的请求，然后返回到「首页」
 */
public class SendToConfirmPwdActivity extends BaseActivity implements BaseAuthNodeView {


    private String TAG = "SendToConfirmPwdActivity";
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
    @BindView(R.id.tv_destination_wallet)
    TextView tvDestinationWallet;
    @BindView(R.id.et_private_key)
    EditText etPrivateKey;
    @BindView(R.id.cbPwd)
    CheckBox cbPwd;
    @BindView(R.id.btnSend)
    Button btnSend;
    private String receiveCurrency, destinationWallet, transactionAmount;//获取上一个页面传输过来的接收方的币种以及地址信息,以及交易数额

    private String currentStatus = Constants.STATUS_DEFAULT;//得到当前的状态,默认
    private BaseAuthNodePresenterImp baseAuthNodePresenterImp;

    @Override
    public int getContentView() {
        return R.layout.aty_send_toconfirm_pwd;
    }

    @Override
    public void getArgs(Bundle bundle) {
        if (bundle == null) return;
        receiveCurrency = bundle.getString(Constants.KeyMaps.RECEIVECURRENCY);
        destinationWallet = bundle.getString(Constants.KeyMaps.DESTINATIONWALLET);
        transactionAmount = bundle.getString(Constants.KeyMaps.TRANSACTIONAMOUNT);


    }

    @Override
    public void initViews() {
        ibBack.setVisibility(View.VISIBLE);
        tvTitle.setText(getResources().getString(R.string.send));
        tvTransactionDetailKey.setText(String.format("向%s转账", destinationWallet));
        tvDestinationWallet.setHint(destinationWallet);
        tvTransactionDetail.setText(transactionAmount);
        baseAuthNodePresenterImp = new BaseAuthNodePresenterImp(this);
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
                String password = BcaasApplication.getPassword();
                if (StringTool.isEmpty(pwd)) {
                    showToast(getResources().getString(R.string.input_pwd));
                } else {
                    //比对当前的密码是否匹配
                    if (StringTool.isEmpty(password)) {
                        showToast("交易无法进行，存储密码不正确。");
                        return;
                    }
                    if (StringTool.equals(pwd, password)) {
                        // TODO: 2018/8/22 获取当前的余额，如果允许交易，那么就send，其过程不允许用户操作其他界面
                        currentStatus = Constants.STATUS_SEND;
                        BcaasApplication.setTransactionAmount(transactionAmount);
                        BcaasApplication.setDestinationWallet(destinationWallet);
                        baseAuthNodePresenterImp.getLatestBlockAndBalance();
                    }

                }
            }
        });

    }

    //结束当前页面
    private void finishActivity() {
        OttoTool.getInstance().post(new SwitchTab(0));
        finish();
    }

    @Override
    public void onBackPressed() {
        if (StringTool.equals(currentStatus, Constants.STATUS_SEND)) {
            showToast(getString(R.string.transactioning));
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void httpANSuccess() {
        BcaasLog.d(TAG, MessageConstants.SUCCESS_GET_LATESTBLOCK_AND_BALANCE);

    }

    @Override
    public void httpANFailure() {
        BcaasLog.d(TAG, MessageConstants.FAILURE_GET_LATESTBLOCK_AND_BALANCE);
    }

    @Override
    public void resetAuthNodeFailure(String message) {

    }

    @Override
    public void resetAuthNodeSuccess() {

    }

    @Subscribe
    public void RefreshSendStatus(RefreshSendStatus refreshSendStatus) {
        if (refreshSendStatus == null) return;
        if (refreshSendStatus.isUnLock()) {
            currentStatus = Constants.STATUS_DEFAULT;
            finishActivity();
        }
    }
}
