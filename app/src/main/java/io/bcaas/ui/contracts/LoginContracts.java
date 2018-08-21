package io.bcaas.ui.contracts;


import io.bcaas.base.BaseView;
import io.bcaas.vo.WalletVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/20
 */
public interface LoginContracts {

    interface View extends BaseView {
        void noWalletInfo();
        void loginFailure(String message);//登录失败

        void loginSuccess();
    }

    interface Presenter {
        void queryWalletInfo(String password);
        void login(WalletVO walletVO);
    }
}
