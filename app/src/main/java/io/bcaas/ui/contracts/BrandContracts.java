package io.bcaas.ui.contracts;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/20
 */
public interface BrandContracts {

    interface View extends BaseContract.View {
        void noWalletInfo();

        void online();
    }

    interface Presenter {
        void queryWalletInfo();

        void checkVersionInfo();
    }
}
