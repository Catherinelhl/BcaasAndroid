package io.bcaas.ui.activity.tv;

import android.os.Bundle;

import io.bcaas.R;
import io.bcaas.base.BaseActivity;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/20
 */
public class LoginActivityTV extends BaseActivity {
    @Override
    public boolean full() {
        return false;
    }

    @Override
    public int getContentView() {
        return R.layout.tv_activity_login;
    }

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public void initViews() {

    }

    @Override
    public void initListener() {

    }
    @Override
    public void showLoading() {
        if (!checkActivityState()){
            return;
        }
        showLoadingDialog();
    }

    @Override
    public void hideLoading() {
        if (!checkActivityState()){
            return;
        }
        hideLoadingDialog();
    }
}
