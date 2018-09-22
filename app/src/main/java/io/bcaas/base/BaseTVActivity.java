package io.bcaas.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import io.bcaas.R;
import io.bcaas.constants.MessageConstants;
import io.bcaas.http.tcp.TCPThread;
import io.bcaas.presenter.SettingPresenterImp;
import io.bcaas.tools.LogTool;
import io.bcaas.ui.activity.LoginActivity;
import io.bcaas.ui.activity.tv.LoginActivityTV;
import io.bcaas.ui.contracts.SettingContract;
import io.bcaas.view.dialog.BcaasDialog;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 */
public abstract class BaseTVActivity extends BaseActivity implements SettingContract.View {

    protected SettingContract.Presenter settingPresenter;

    private String TAG = BaseTVActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingPresenter = new SettingPresenterImp(this);
    }

    public void logout() {
        BcaasApplication.setKeepHttpRequest(false);
        TCPThread.kill(true);
        BcaasApplication.clearAccessToken();
        intentToActivity(LoginActivityTV.class, true);
    }

    @Override
    public void logoutSuccess() {
        LogTool.d(TAG, MessageConstants.LOGOUT_SUCCESSFULLY);
    }

    @Override
    public void logoutFailure(String message) {
        LogTool.d(TAG, message);
        logoutFailure();
    }

    @Override
    public void logoutFailure() {
        showToast(getString(R.string.logout_failure));

    }

    @Override
    public void accountError() {
        showToast(getResources().getString(R.string.account_data_error));
    }

    protected void showLogoutDialog() {
        showBcaasDialog(getResources().getString(R.string.confirm_logout), new BcaasDialog.ConfirmClickListener() {
            @Override
            public void sure() {
                if (checkActivityState()) {
                    logout();
                }
                settingPresenter.logout();
            }

            @Override
            public void cancel() {

            }
        });
    }
}
