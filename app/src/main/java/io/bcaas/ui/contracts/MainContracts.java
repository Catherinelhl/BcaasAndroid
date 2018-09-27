package io.bcaas.ui.contracts;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/21
 */
public interface MainContracts {
    interface View extends BaseContract.HttpView {
        void updateVersion(boolean forceUpgrade);//更新版本，是否强制更新呢

        void checkUpdateFailure();//檢查更新失敗
    }

    interface Presenter extends BaseContract.HttpPresenter {

        void unSubscribe();

        void checkUpdate();
    }
}

