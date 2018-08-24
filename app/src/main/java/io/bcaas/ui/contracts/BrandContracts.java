package io.bcaas.ui.contracts;


import io.bcaas.base.BaseView;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/20
 */
public interface BrandContracts {

    interface View extends BaseView {
        void noWalletInfo();
        void online();
    }

    interface Presenter {
        void queryWalletInfo();
    }
}
