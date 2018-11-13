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
    }

    interface Presenter {
        void queryWalletFromDB(String password);

        void login();

    }
}
