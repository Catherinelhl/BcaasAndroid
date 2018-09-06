package io.bcaas.ui.contracts;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/25
 * <p>
 * 「发送确认」
 */
public interface SendConfirmationContract {

    interface View extends BaseContract.HttpView {
        void lockView(boolean lock);// 「交易」请求正在发起，是否锁定当前页面
    }

    interface Presenter extends BaseContract.HttpPresenter {
        void sendTransaction(String password);
    }
}
