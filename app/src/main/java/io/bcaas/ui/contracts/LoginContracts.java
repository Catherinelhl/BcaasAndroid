package io.bcaas.ui.contracts;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/20
 * 連接界面和數據操作互動：「登錄」
 */
public interface LoginContracts {

    interface View extends BaseContract.View {
        void noWalletInfo();//当前没有钱包，需要用户创建或者导入

        void loginFailure();//登录失败

        void loginSuccess();

        void passwordError();

        /*更新版本，是否强制更新*/
        void updateVersion(boolean forceUpgrade, String appStoreUrl, String updateUrl);

        void getAndroidVersionInfoFailure();//檢查更新失敗
    }

    interface Presenter {
        void queryWalletFromDB(String password);

        void getRealIpForLoginRequest();

        //获取BCAASC Android版本信息，查看是否需要更新
        void getAndroidVersionInfo();

    }
}

