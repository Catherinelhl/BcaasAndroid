package io.bcaas.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.tools.DeviceTool;
import io.bcaas.tools.LogTool;
import io.bcaas.ui.activity.tv.MainActivityTV;
import io.bcaas.ui.contracts.BrandContracts;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 */
public class BrandActivity extends BaseActivity
        implements BrandContracts.View {

    private String TAG = BrandActivity.class.getSimpleName();
    private BrandContracts.Presenter presenter;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            boolean isPhone = DeviceTool.checkIsPhone(BcaasApplication.context());
            if (isPhone) {
                intentToActivity(LoginActivity.class, true);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.KeyMaps.From, Constants.ValueMaps.FROM_BRAND);
                intentToActivity(bundle, MainActivityTV.class, true);

            }

        }
    };

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public int getContentView() {
        return R.layout.activity_brand;
    }

    @Override
    public void initViews() {
        String type = getCurrentLanguage();
        switchingLanguage(type);
        handler.sendEmptyMessageDelayed(1, Constants.ValueMaps.sleepTime2000);
//        presenter = new BrandPresenterImp(this);
//        presenter.checkVersionInfo();
//        presenter.queryWalletInfo();

    }

    @Override
    public boolean full() {
        return true;
    }

    @Override
    public void initListener() {

    }

    @Override
    public void noWalletInfo() {
        intentToActivity(LoginActivity.class, true);
    }

    @Override
    public void online() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KeyMaps.From, Constants.ValueMaps.FROM_BRAND);
        intentToActivity(bundle, MainActivity.class, true);
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

}