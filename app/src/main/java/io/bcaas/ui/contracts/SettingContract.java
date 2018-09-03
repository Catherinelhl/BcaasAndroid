package io.bcaas.ui.contracts;


import java.util.List;

import io.bcaas.bean.SettingsBean;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 */
public interface SettingContract {
    interface View  extends BaseContract.View {
        void logoutSuccess();
        void logoutFailure(String message);
    }

    interface Presenter {
        List<SettingsBean> initSettingTypes();

        void logout();

    }
}
