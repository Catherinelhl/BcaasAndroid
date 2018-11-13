package io.bcaas.ui.contracts;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 * 連接界面和數據操作互動：「設置」
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
