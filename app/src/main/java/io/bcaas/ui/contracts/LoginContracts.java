package io.bcaas.ui.contracts;


import io.bcaas.base.BaseView;
import io.bcaas.vo.WalletVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/20
 */
public interface LoginContracts {

    interface View extends BaseView {
        void noWalletInfo();//当前没有钱包，需要用户创建或者导入
        void loginFailure(String message);//登录失败
        void loginSuccess();

        void verifySuccess();//验证通过
    }

    interface Presenter {
        void queryWalletInfoFromDB(String password);
        void onLogin(WalletVO walletVO);
    }
}
