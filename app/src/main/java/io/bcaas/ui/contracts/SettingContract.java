package io.bcaas.ui.contracts;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 */
public interface SettingContract {
    interface View  extends BaseContract.View {
        void logoutSuccess();
        void logoutFailure(String message);
        void logoutFailure();
        // 账户异常
        void accountError();
    }

    interface Presenter {
        void logout();

    }
}
