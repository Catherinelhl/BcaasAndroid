package io.bcaas.event;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/24
 * <p>
 * 更新send页面的状态
 */
public class RefreshSendStatusEvent {

    private boolean unLock;

    public RefreshSendStatusEvent(boolean unLock) {
        this.unLock = unLock;
    }

    public boolean isUnLock() {
        return unLock;
    }
}
