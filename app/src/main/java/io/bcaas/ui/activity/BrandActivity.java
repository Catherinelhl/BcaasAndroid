package io.bcaas.ui.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import io.bcaas.R;
import io.bcaas.base.BCAASApplication;
import io.bcaas.base.BaseActivity;
import io.bcaas.constants.Constants;
import io.bcaas.listener.ObservableTimerListener;
import io.bcaas.tools.ObservableTimerTool;
import io.bcaas.ui.activity.tv.MainActivityTV;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * Activity:啟動頁面
 */
public class BrandActivity extends BaseActivity {
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
        ObservableTimerTool.countDownTimerBySetTime(Constants.ValueMaps.STAY_BRAND_ACTIVITY_TIME, observableTimerListener);
    }

    private ObservableTimerListener observableTimerListener = new ObservableTimerListener() {
        @Override
        public void timeUp(String from) {
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