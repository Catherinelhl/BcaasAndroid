package io.bcaas.ui.contracts;


import java.util.List;

import io.bcaas.base.BaseView;
import io.bcaas.bean.SettingTypeBean;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 */
public interface SettingContract {
    interface View  extends BaseView {
        void logoutSuccess();

        void logoutFailure(String message);
    }

    interface Presenter {
        List<SettingTypeBean> initSettingTypes();

        void logout(String WalletAddress);
    }
}
