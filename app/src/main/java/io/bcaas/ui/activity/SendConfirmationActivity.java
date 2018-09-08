package io.bcaas.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.squareup.otto.Subscribe;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.bcaas.BuildConfig;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.event.LoginEvent;
import io.bcaas.event.RefreshSendStatusEvent;
import io.bcaas.event.SwitchTabEvent;
import io.bcaas.listener.SoftKeyBroadManager;
import io.bcaas.presenter.SendConfirmationPresenterImp;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.TextTool;
import io.bcaas.ui.contracts.SendConfirmationContract;
import io.reactivex.disposables.Disposable;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 * <p>
 * <p>
 * 「发送」二级页面
 * 点击「确认」，进行网络的请求，当前请求如果没有返回数据，则不能操作本页面，返回结果后，结束当前页面，然后返回到「首页」
 */
public class SendConfirmationActivity extends BaseActivity implements SendConfirmationContract.View {
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.cbPwd)
    CheckBox cbPwd;
    @BindView(R.id.v_password_line)
    View vPasswordLine;
    private String TAG = SendConfirmationActivity.class.getSimpleName();
    @BindView(R.id.ib_back)
    ImageButton ibBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ib_right)
    ImageButton ibRight;
    @BindView(R.id.rl_header)
    RelativeLayout rlHeader;
    @BindView(R.id.tvTransactionDetailKey)
    TextView tvTransactionDetailKey;
    @BindView(R.id.tvTransactionDetail)
    TextView tvTransactionDetail;
    @BindView(R.id.tvReceiveAccountKey)
    TextView tvReceiveAccountKey;
    @BindView(R.id.tv_destination_wallet)
    TextView tvDestinationWallet;
    @BindView(R.id.ll_send_confirm)
    LinearLayout llSendConfirm;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.v_space)
    View vSpace;
    private String transactionAmount, addressName, destinationWallet;//获取上一个页面传输过来的接收方的币种以及地址信息,以及交易数额

    private String currentStatus = Constants.ValueMaps.STATUS_DEFAULT;//得到当前的状态,默认
    private SendConfirmationContract.Presenter presenter;

    @Override
    public int getContentView() {
        return R.layout.activity_send_toconfirm_pwd;
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
        addressName = bundle.getString(Constants.KeyMaps.ADDRESS_NAME);
        destinationWallet = bundle.getString(Constants.KeyMaps.DESTINATION_WALLET);
        transactionAmount = bundle.getString(Constants.KeyMaps.TRANSACTION_AMOUNT);
    }

    @Override
    public void initViews() {
        ibBack.setVisibility(View.VISIBLE);
        tvTitle.setText(getResources().getString(R.string.send));
        tvTransactionDetailKey.setText(String.format(getString(R.string.transaction_to), addressName != null ? addressName : TextTool.keepFourText(destinationWallet)));
        tvDestinationWallet.setHint(destinationWallet);

        tvTransactionDetail.setText(transactionAmount + " " + BcaasApplication.getBlockService());
        presenter = new SendConfirmationPresenterImp(this);
        addSoftKeyBroadManager();
    }

    /**
     * 添加软键盘监听
     */
    private void addSoftKeyBroadManager() {
        softKeyBroadManager = new SoftKeyBroadManager(llSendConfirm, vSpace);
        softKeyBroadManager.addSoftKeyboardStateListener(softKeyboardStateListener);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initListener() {
        llSendConfirm.setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            return false;
        });
        cbPwd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String text = etPassword.getText().toString();
            if (StringTool.isEmpty(text)) {
                return;
            }
            etPassword.setInputType(isChecked ?
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);//设置当前私钥显示不可见

        });
        ibBack.setOnClickListener(v -> {
            hideLoadingDialog();
            if (BuildConfig.DEBUG) {
                finish();
            } else {
                if (StringTool.equals(currentStatus, Constants.ValueMaps.STATUS_SEND)) {
                    showToast(getString(R.string.on_transaction));
                } else {
                    finish();
                }
            }
        });
        Disposable subscribeSend = RxView.clicks(btnSend)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    String password = etPassword.getText().toString();
                    /*判断密码是否为空*/
                    if (StringTool.isEmpty(password)) {
                        showToast(getResources().getString(R.string.enter_password));
                    } else {
                        if (StringTool.equals(currentStatus, Constants.ValueMaps.STATUS_SEND)) {
                            showToast(getString(R.string.on_transaction));
                        } else {
                            lockView(true);
                            presenter.sendTransaction(password);
                        }
                    }
                });
    }

    /**
     * 是否 锁定当前页面，
     * 将其状态置为「STATUS_SEND」
     * 存储当前的交易数据
     *
     * @param lock
     */
    @Override
    public void lockView(boolean lock) {
        currentStatus = lock ? Constants.ValueMaps.STATUS_SEND : Constants.ValueMaps.STATUS_DEFAULT;
        BcaasApplication.setTransactionAmount(transactionAmount);
        BcaasApplication.setDestinationWallet(destinationWallet);
    }

    /**
     * 结束当前页面,并显示到首页
     */
    private void finishActivity() {
        OttoTool.getInstance().post(new SwitchTabEvent(0));
        finish();
    }

    @Override
    public void onBackPressed() {
        if (StringTool.equals(currentStatus, Constants.ValueMaps.STATUS_SEND)) {
            showToast(getString(R.string.on_transaction));
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void httpGetLatestBlockAndBalanceSuccess() {
        LogTool.d(TAG, MessageConstants.SUCCESS_GET_LATESTBLOCK_AND_BALANCE);

    }

    @Override
    public void httpGetLatestBlockAndBalanceFailure() {
        lockView(false);
        showToast(getResources().getString(R.string.data_acquisition_error));
        LogTool.d(TAG, MessageConstants.FAILURE_GET_LATESTBLOCK_AND_BALANCE);
    }

    @Override
    public void resetAuthNodeFailure(String message) {

    }

    @Override
    public void resetAuthNodeSuccess() {

    }

    @Override
    public void noData() {
        showToast(getResources().getString(R.string.account_data_error));

    }

    @Subscribe
    public void RefreshSendStatus(RefreshSendStatusEvent refreshSendStatusEvent) {
        if (refreshSendStatusEvent == null) {
            return;
        }
        boolean isUnlock = refreshSendStatusEvent.isUnLock();
        if (isUnlock) {
            currentStatus = Constants.ValueMaps.STATUS_DEFAULT;
            finishActivity();
        }
        LogTool.d(TAG, MessageConstants.SEND_TRANSACTION_SATE + isUnlock);

    }

    @Override
    public void noWalletInfo() {

    }

    @Override
    public void loginFailure() {
        showToast(getResources().getString(R.string.login_failure));
        OttoTool.getInstance().post(new LoginEvent());
        finishActivity();
    }

    @Override
    public void loginSuccess() {

    }

    @Override
    public void verifySuccess() {
        //验证成功，开始请求最新余额
        presenter.getLatestBlockAndBalance();

    }

    @Override
    public void verifyFailure() {
        lockView(false);
        //验证失败，需要重新拿去AN的信息
        showToast(getResources().getString(R.string.data_acquisition_error));
        finish();

    }

    @Override
    public void failure(String message) {
        super.failure(message);
        verifyFailure();
    }

    @Override
    public void passwordError() {
        lockView(false);
        showToast(getResources().getString(R.string.password_error));
    }

    @Override
    public void responseDataError() {
        showToast(getResources().getString(R.string.data_acquisition_error));

    }

}
