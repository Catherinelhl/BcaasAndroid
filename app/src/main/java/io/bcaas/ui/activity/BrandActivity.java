package io.bcaas.ui.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BCAASApplication;
import io.bcaas.constants.Constants;
import io.bcaas.tools.DeviceTool;
import io.bcaas.ui.activity.tv.MainActivityTV;
import io.bcaas.view.BcaasBalanceTextView;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 */
public class BrandActivity extends BaseActivity {

    private String TAG = BrandActivity.class.getSimpleName();

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (BCAASApplication.isIsPhone()) {
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

    private void setOrientation() {
        /*如果当前为手机，强制设为竖屏 */
        if (BCAASApplication.isIsPhone()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public int getContentView() {
        return BCAASApplication.isIsPhone() ? R.layout.activity_brand : R.layout.tv_activity_brand;
    }

    @Override
    public void initViews() {
        setOrientation();
        String type = getCurrentLanguage();
        switchingLanguage(type);
        handler.sendEmptyMessageDelayed(1, Constants.ValueMaps.sleepTime2000);
    }

    @Override
    public boolean full() {
        return true;
    }

    @Override
    public void initListener() {

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