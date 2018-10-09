package io.bcaas.ui.contracts;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/21
 */
public interface MainContracts {
    interface View extends BaseContract.HttpView {
        /*更新版本，是否强制更新*/
        void updateVersion(boolean forceUpgrade, String appStoreUrl, String updateUrl);

        void getAndroidVersionInfoFailure();//檢查更新失敗
    }

    interface Presenter extends BaseContract.HttpPresenter {

        void unSubscribe();

        //获取Bcaas Android版本信息，查看是否需要更新
        void getAndroidVersionInfo();
    }
}

