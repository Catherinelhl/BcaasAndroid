package io.bcaas.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.squareup.otto.Subscribe;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.event.RefreshSendStatus;
import io.bcaas.event.SwitchTab;
import io.bcaas.event.ToLogin;
import io.bcaas.presenter.SendConfirmationPresenterImp;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.StringTool;
import io.bcaas.ui.contracts.SendConfirmationContract;
import io.bcaas.view.LineEditText;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 * <p>
 * <p>
 * 「发送」二级页面
 * 点击「确认」，进行网络的请求，当前请求如果没有返回数据，则不能操作本页面，返回结果后，结束当前页面，然后返回到「首页」
 */
public class SendConfirmationActivity extends BaseActivity implements SendConfirmationContract.View {
    private String TAG = SendConfirmationActivity.class.getSimpleName();
    @BindView(R.id.ib_back)
    ImageButton ibBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ib_right)
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
    @BindView(R.id.let_private_key)
    LineEditText letPrivateKey;
    @BindView(R.id.cbPwd)
    CheckBox cbPwd;
    @BindView(R.id.btnSend)
    Button btnSend;
    private String  destinationWallet, transactionAmount;//获取上一个页面传输过来的接收方的币种以及地址信息,以及交易数额

    private String currentStatus = Constants.ValueMaps.STATUS_DEFAULT;//得到当前的状态,默认
    private SendConfirmationContract.Presenter presenter;

    @Override
    public int getContentView() {
        return R.layout.aty_send_toconfirm_pwd;
    }

    @Override
    public void getArgs(Bundle bundle) {
        if (bundle == null) return;
        destinationWallet = bundle.getString(Constants.KeyMaps.DESTINATION_WALLET);
        transactionAmount = bundle.getString(Constants.KeyMaps.TRANSACTION_AMOUNT);


    }

    @Override
    public void initViews() {
        ibBack.setVisibility(View.VISIBLE);
        tvTitle.setText(getResources().getString(R.string.send));
        tvTransactionDetailKey.setText(String.format("向%s转账", destinationWallet));
        tvDestinationWallet.setHint(destinationWallet);
        tvTransactionDetail.setText(transactionAmount);
        presenter = new SendConfirmationPresenterImp(this);
    }

    @Override
    public void initListener() {
        cbPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                letPrivateKey.setInputType(isChecked ?
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);//设置当前私钥显示不可见

            }
        });
        letPrivateKey.addTextChangedListener(new TextWatcher() {
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
                String password = letPrivateKey.getText().toString();
                presenter.sendTransaction(password);
            }
        });

    }

    /***
     * 锁定当前页面，将其状态置为「STATUS_SEND」
     * 存储当前的交易数据
     */
    @Override
    public void lockView() {
        BcaasLog.d(TAG, "lockView");
        currentStatus = Constants.ValueMaps.STATUS_SEND;
        BcaasApplication.setTransactionAmount(transactionAmount);
        BcaasApplication.setDestinationWallet(destinationWallet);
    }

    //结束当前页面
    private void finishActivity() {
        OttoTool.getInstance().post(new SwitchTab(0));
        finish();
    }

    @Override
    public void onBackPressed() {
        if (StringTool.equals(currentStatus, Constants.ValueMaps.STATUS_SEND)) {
            showToast(getString(R.string.transactioning));
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void httpGetLatestBlockAndBalanceSuccess() {
        BcaasLog.d(TAG, MessageConstants.SUCCESS_GET_LATESTBLOCK_AND_BALANCE);

    }

    @Override
    public void httpGetLatestBlockAndBalanceFailure() {
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
            currentStatus = Constants.ValueMaps.STATUS_DEFAULT;
            finishActivity();
        }
    }

    @Override
    public void noWalletInfo() {

    }

    @Override
    public void loginFailure(String message) {
        showToast(message);
        OttoTool.getInstance().post(new ToLogin());
        finishActivity();
    }

    @Override
    public void loginSuccess() {

    }

    @Override
    public void verifySuccess() {
    }

    @Override
    public void verifyFailure(String message) {
        BcaasLog.d(TAG, message);
    }
}
