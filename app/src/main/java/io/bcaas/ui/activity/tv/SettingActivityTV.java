package io.bcaas.ui.activity.tv;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.squareup.otto.Subscribe;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BcaasApplication;
import io.bcaas.bean.WalletBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.event.LogoutEvent;
import io.bcaas.event.ModifyRepresentativeResultEvent;
import io.bcaas.event.UpdateRepresentativeEvent;
import io.bcaas.gson.ResponseJson;
import io.bcaas.http.MasterServices;
import io.bcaas.tools.DateFormatTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.ecc.KeyTool;
import io.bcaas.tools.regex.RegexTool;
import io.bcaas.view.tv.FlyBroadLayout;
import io.bcaas.view.tv.MainUpLayout;
import io.reactivex.disposables.Disposable;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/20
 * TV版設置頁面
 */
public class SettingActivityTV extends BaseActivity {
    private String TAG = SettingActivityTV.class.getSimpleName();
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_current_time)
    TextView tvCurrentTime;
    @BindView(R.id.tv_logout)
    TextView tvLogout;
    @BindView(R.id.rl_header)
    RelativeLayout rlHeader;
    @BindView(R.id.tv_private_key)
    TextView tvPrivateKey;
    @BindView(R.id.tv_my_address_key)
    TextView tvMyAddressKey;
    @BindView(R.id.tv_account_address)
    TextView tvAccountAddress;
    @BindView(R.id.et_input_representatives)
    EditText etInputRepresentatives;
    @BindView(R.id.btn_sure)
    Button btnSure;
    @BindView(R.id.cb_pwd)
    CheckBox cbPwd;
    @BindView(R.id.block_base_mainup)
    FlyBroadLayout blockBaseMainup;
    @BindView(R.id.block_base_content)
    MainUpLayout blockBaseContent;
    /*可见的私钥*/
    private String visiblePrivateKey;

    @Override
    public boolean full() {
        return false;
    }

    @Override
    public int getContentView() {
        return R.layout.tv_activity_setting;
    }

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public void initViews() {
        tvCurrentTime.setText(DateFormatTool.getCurrentTime());
        tvTitle.setText(getResources().getString(R.string.settings));
        initData();

        showLoadingDialog();
        if (!BcaasApplication.isRealNet()) {
            hideLoadingDialog();
            noNetWork();
        } else {
            //請求getLastChangeBlock接口，取得更換委託人區塊
            MasterServices.getLatestChangeBlock();
        }
    }


    @Subscribe
    public void updateRepresentative(UpdateRepresentativeEvent updateRepresentativeEvent) {
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

    private void setPreviousRepresentative(String representative) {
        if (StringTool.notEmpty(representative)) {
            etInputRepresentatives.setText(representative);
            etInputRepresentatives.setSelection(representative.length());
        }
    }


    private void initData() {
        //展示账户地址
        String address = BcaasApplication.getWalletAddress();
        if (StringTool.isEmpty(address)) {
            showToast(getResources().getString(R.string.account_data_error));
        } else {
            tvAccountAddress.setText(address);
        }
        WalletBean walletBean = BcaasApplication.getWalletBean();
        if (walletBean != null) {
            visiblePrivateKey = walletBean.getPrivateKey();
            if (StringTool.notEmpty(visiblePrivateKey)) {
                tvPrivateKey.setText(Constants.ValueMaps.PRIVATE_KEY);
            }

        }

    }

    @Override
    public void initListener() {
        blockBaseContent.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                blockBaseMainup.setFocusView(newFocus, oldFocus, 1.2f);
            }
        });
        cbPwd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String text = tvPrivateKey.getText().toString();
            if (StringTool.isEmpty(text)) {
                return;
            }
            if (isChecked) {
                tvPrivateKey.setText(visiblePrivateKey);
            } else {
                tvPrivateKey.setText(Constants.ValueMaps.PRIVATE_KEY);
            }
        });
        Disposable subscribeSure = RxView.clicks(btnSure)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    hideSoftKeyboard();
                    String representative = RegexTool.replaceBlank(etInputRepresentatives.getText().toString());
                    if (StringTool.notEmpty(representative)) {
                        /*检测当前地址格式*/
                        if (KeyTool.validateBitcoinAddress(representative)) {
                            BcaasApplication.setRepresentative(representative);
                            showLoadingDialog();
                            if (!BcaasApplication.isRealNet()) {
                                hideLoadingDialog();
                                showToast(getResources().getString(R.string.network_not_reachable));
                            } else {
                                //請求getLastChangeBlock接口，取得更換委託人區塊
                                MasterServices.getLatestChangeBlock();
                            }
                        } else {
                            showToast(getResources().getString(R.string.address_format_error));
                        }

                    } else {
                        showToast(getResources().getString(R.string.enter_address_of_the_authorized));
                    }
                });
    }

    @Subscribe
    public void modifyRepresentativeSuccessfully(ModifyRepresentativeResultEvent modifyRepresentativeResultEvent) {
        if (modifyRepresentativeResultEvent != null) {
            hideLoadingDialog();
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
                        finish();
                    }
                    break;
                case MessageConstants.CODE_2030:
                    etInputRepresentatives.setEnabled(true);
                    showToast(getResources().getString(R.string.address_repeat));
                    break;
                case MessageConstants.CODE_2026:
                    showToast(getResources().getString(R.string.authorized_representative_can_not_be_modified), Constants.ValueMaps.TOAST_LONG);
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

    @Override
    public void httpExceptionStatus(ResponseJson responseJson) {
        if (responseJson == null) {
            return;
        }
        int code = responseJson.getCode();
        if (code == MessageConstants.CODE_3006
                || code == MessageConstants.CODE_3008) {
            showBcaasSingleDialog(getString(R.string.warning),
                    getString(R.string.please_login_again), () -> {
                        finish();
                        OttoTool.getInstance().post(new LogoutEvent());
                    });
        } else {
            super.httpExceptionStatus(responseJson);
        }
    }

}
