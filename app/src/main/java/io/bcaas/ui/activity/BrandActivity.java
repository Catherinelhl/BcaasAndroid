package io.bcaas.ui.activity;

import android.os.Bundle;

import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.constants.Constants;
import io.bcaas.presenter.BrandPresenterImp;
import io.bcaas.tools.BcaasLog;
import io.bcaas.ui.contracts.BrandContracts;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 */
public class BrandActivity extends BaseActivity
        implements BrandContracts.View {

    private String TAG = BrandActivity.class.getSimpleName();
    private BrandContracts.Presenter presenter;

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public int getContentView() {
        return R.layout.activity_brand;
    }

    @Override
    public void initViews() {
        BcaasLog.d(TAG);
        String type = getCurrentLanguage();
        switchingLanguage(type);
        presenter = new BrandPresenterImp(this);
        presenter.checkVersionInfo();
        presenter.queryWalletInfo();
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
}