package io.bcaas.event;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/24
 * <p>
 * 更新send页面的状态
 */
public class RefreshSendStatus {

    private boolean unLock;

    public RefreshSendStatus(boolean unLock) {
        this.unLock = unLock;
    }

    public boolean isUnLock() {
        return unLock;
    }
}
