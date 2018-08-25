package io.bcaas.base;

import io.bcaas.ui.contracts.BaseContract;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 */
public abstract class BaseHttpActivity extends BaseActivity implements BaseContract.HttpView {

    private String TAG = "BaseHttpActivity";

    @Override
    public void httpGetLatestBlockAndBalanceFailure() {

    }

    @Override
    public void httpGetLatestBlockAndBalanceSuccess() {

    }

    @Override
    public void resetAuthNodeSuccess() {

    }

    @Override
    public void resetAuthNodeFailure(String message) {

    }

    @Override
    public void noWalletInfo() {

    }

    @Override
    public void loginSuccess() {

    }

    @Override
    public void loginFailure(String message) {

    }

    @Override
    public void verifySuccess() {

    }
}
