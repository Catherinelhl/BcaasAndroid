package io.bcaas.event;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/20
 *
 * 發出此訂閱：綁定TCP服務
 */
public class BindTCPServiceEvent {
    private boolean isUnBind;

    public BindTCPServiceEvent(boolean isUnBind) {
        this.isUnBind = isUnBind;
    }

    public boolean isUnBind() {
        return isUnBind;
    }
}
