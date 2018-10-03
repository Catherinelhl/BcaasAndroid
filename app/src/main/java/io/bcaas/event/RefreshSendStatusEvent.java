package io.bcaas.event;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/24
 * <p>
 * 發送成功，通知訂閱，刷新當前頁面；
 * 更新send页面的状态
 */
public class RefreshSendStatusEvent {

    //發送是否成功
    private boolean isSuccess;

    public RefreshSendStatusEvent(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
