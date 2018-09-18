package io.bcaas.ui.contracts;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/20
 */
public interface LoginContracts {

    interface View extends VerifyContracts.View {
        void noWalletInfo();//当前没有钱包，需要用户创建或者导入

        void loginFailure();//登录失败

        void loginSuccess();
    }

    interface Presenter {
        void queryWalletFromDB(String password);

        void toLogin();

    }
}
