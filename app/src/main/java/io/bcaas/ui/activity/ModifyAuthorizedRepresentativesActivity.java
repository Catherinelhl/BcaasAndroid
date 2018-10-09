package io.bcaas.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.obt.qrcode.activity.CaptureActivity;
import com.squareup.otto.Subscribe;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BCAASApplication;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.event.ModifyRepresentativeResultEvent;
import io.bcaas.event.RefreshRepresentativeEvent;
import io.bcaas.gson.ResponseJson;
import io.bcaas.http.MasterServices;
import io.bcaas.listener.HttpResponseListener;
import io.bcaas.listener.SoftKeyBroadManager;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.ecc.KeyTool;
import io.bcaas.tools.regex.RegexTool;
import io.reactivex.disposables.Disposable;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/1
 * <p>
 * 修改授权代表
 */
public class ModifyAuthorizedRepresentativesActivity extends BaseActivity {
    private String TAG = ModifyAuthorizedRepresentativesActivity.class.getSimpleName();
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
    @BindView(R.id.et_input_representatives)
    EditText etInputRepresentatives;
    @BindView(R.id.btn_sure)
    Button btnSure;
    @BindView(R.id.v_space)
    View vSpace;
    @BindView(R.id.ll_modify_authorized_representatives)
    LinearLayout llModifyAuthorizedRepresentatives;

    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.rl_content)
    RelativeLayout rlContent;
    @BindView(R.id.ib_scan_representative)
    ImageButton ibScanRepresentative;
    //结束当前页面
    private int FINISH_ACTIVITY = 0x11;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            if (what == FINISH_ACTIVITY) {
                finish();
            }

        }
    };

    @Override
    public int getContentView() {
        return R.layout.activity_modify_authorized_presentatives;
    }

    @Override
    public boolean full() {
        return false;
    }

    @Override
    public void getArgs(Bundle bundle) {
    }

    @Override
    public void initViews() {
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(getResources().getString(R.string.change_representatives));
        tvAccountAddress.setText(BCAASApplication.getWalletAddress());
        ibBack.setVisibility(View.VISIBLE);
        addSoftKeyBroadManager();
        etInputRepresentatives.setEnabled(false);
        showLoading();
        if (!BCAASApplication.isRealNet()) {
            hideLoadingDialog();
            noNetWork();
        } else {
            //請求getLastChangeBlock接口，取得更換委託人區塊
            MasterServices.getLatestChangeBlock(httpResponseListener);
        }

    }

    private void setPreviousRepresentative(String representative) {
        if (StringTool.notEmpty(representative)) {
            etInputRepresentatives.setText(representative);
            etInputRepresentatives.setSelection(representative.length());
        }
    }

    /**
     * 添加软键盘监听
     */
    private void addSoftKeyBroadManager() {
        softKeyBroadManager = new SoftKeyBroadManager(llModifyAuthorizedRepresentatives, vSpace);
        softKeyBroadManager.addSoftKeyboardStateListener(softKeyboardStateListener);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initListener() {
        llModifyAuthorizedRepresentatives.setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            return false;
        });
        llContent.setOnTouchListener((v, event) -> true);
        rlContent.setOnTouchListener((v, event) -> true);
        ibBack.setOnClickListener(v -> {
            hideSoftKeyboard();
            finish();
        });
        Disposable subscribeSure = RxView.clicks(btnSure)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    hideSoftKeyboard();
                    String representative = RegexTool.replaceBlank(etInputRepresentatives.getText().toString());
                    if (StringTool.notEmpty(representative)) {
                        /*检测当前地址格式*/
                        if (KeyTool.validateBitcoinAddress(representative)) {
                            BCAASApplication.setRepresentative(representative);
                            showLoadingDialog();
                            if (!BCAASApplication.isRealNet()) {
                                hideLoadingDialog();
                                showToast(getResources().getString(R.string.network_not_reachable));
                            } else {
                                //請求getLastChangeBlock接口，取得更換委託人區塊
                                MasterServices.getLatestChangeBlock(httpResponseListener);
                            }
                        } else {
                            showToast(getResources().getString(R.string.address_format_error));
                        }

                    } else {
                        showToast(getResources().getString(R.string.enter_address_of_the_authorized));
                    }
                });
        Disposable subscribeInputRepresentative = RxView.clicks(ibScanRepresentative)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    startActivityForResult(new Intent(this, CaptureActivity.class), 0);
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String result = bundle.getString(Constants.RESULT);
                etInputRepresentatives.setText(result);
                if (StringTool.notEmpty(result)) {
                    etInputRepresentatives.setSelection(result.length());
                }
            }
        }
    }

    @Subscribe
    public void modifyRepresentativeSuccessfully(ModifyRepresentativeResultEvent modifyRepresentativeResultEvent) {
        LogTool.d(TAG, modifyRepresentativeResultEvent);
        if (modifyRepresentativeResultEvent != null) {
            hideLoading();
            int code = modifyRepresentativeResultEvent.getCode();
            String currentStatus = modifyRepresentativeResultEvent.getCurrentStatus();
            switch (code) {
                case MessageConstants.CODE_200:
                    if (StringTool.equals(currentStatus, Constants.CHANGE_OPEN)) {
                        //如果当前是open块
                        etInputRepresentatives.setEnabled(true);
                        etInputRepresentatives.requestFocus();
                        etInputRepresentatives.setFocusable(true);
                        etInputRepresentatives.setFocusableInTouchMode(true);
                    } else {
                        showToast(getResources().getString(R.string.change_successfully));
                        handler.sendEmptyMessageDelayed(FINISH_ACTIVITY, Constants.ValueMaps.sleepTime1000);
                    }
                    break;
                case MessageConstants.CODE_2030:
                    etInputRepresentatives.setEnabled(true);
                    showToast(getResources().getString(R.string.address_repeat));
                    break;
                case MessageConstants.CODE_2026:
                    showToast(getResources().getString(R.string.authorized_representative_can_not_be_modified), Constants.ValueMaps.TOAST_LONG);
                    handler.sendEmptyMessageDelayed(FINISH_ACTIVITY, Constants.ValueMaps.STAY_AUTH_ACTIVITY_TIME);
                    break;
                case MessageConstants.CODE_2033:
                    etInputRepresentatives.setEnabled(true);
                    showToast(getResources().getString(R.string.address_format_error));
                    break;
                default:
                    if (StringTool.equals(currentStatus, Constants.CHANGE_OPEN)) {
                        //如果当前是open块
                        etInputRepresentatives.setEnabled(true);
                        etInputRepresentatives.requestFocus();
                        etInputRepresentatives.setFocusable(true);
                        etInputRepresentatives.setFocusableInTouchMode(true);
                    } else {
                        boolean isSuccess = modifyRepresentativeResultEvent.isSuccess();
                        etInputRepresentatives.setEnabled(true);
                        showToast(getResources().getString(isSuccess ? R.string.change_successfully :
                                R.string.change_failed));
                        if (isSuccess) {
                            finish();
                        }
                    }
                    break;

            }

        }

    }

    @Subscribe
    public void updateRepresentative(RefreshRepresentativeEvent updateRepresentativeEvent) {
        hideLoadingDialog();
        LogTool.d(TAG, "updateRepresentative");
        if (updateRepresentativeEvent != null) {
            etInputRepresentatives.setEnabled(true);
            String representative = updateRepresentativeEvent.getRepresentative();
            if (StringTool.notEmpty(representative)) {
                setPreviousRepresentative(representative);

            }
        }
    }


    @Override
    public void httpExceptionStatus(ResponseJson responseJson) {
        if (responseJson == null) {
            return;
        }
        int code = responseJson.getCode();
        if (code == MessageConstants.CODE_3006
                || code == MessageConstants.CODE_3008
                || code == MessageConstants.CODE_2029) {
            showLogoutSingleDialog();
        } else {
            super.httpExceptionStatus(responseJson);
        }
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


    private HttpResponseListener httpResponseListener = new HttpResponseListener() {
        @Override
        public void getLatestChangeBlockSuccess() {

        }

        @Override
        public void getLatestChangeBlockFailure(String failure) {
            hideLoading();
            etInputRepresentatives.setEnabled(true);
            showToast(getResources().getString(R.string.change_failed));
        }
    };
}
