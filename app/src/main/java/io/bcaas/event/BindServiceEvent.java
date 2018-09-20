package io.bcaas.event;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/20
 * 提示綁定服務
 */
public class BindServiceEvent {
    private boolean isUnBind;

    public BindServiceEvent(boolean isUnBind) {
        this.isUnBind = isUnBind;
    }

    public boolean isUnBind() {
        return isUnBind;
    }
}
