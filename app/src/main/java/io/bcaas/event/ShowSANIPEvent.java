package io.bcaas.event;

/**
 * @author catherine.brainwilliam
 * @since 2018/12/12
 * <p>
 * <p>
 * 發出此訂閱：通知显示当前需要显示的SAN IP
 */
public class ShowSANIPEvent {
    //SAN的IP
    private String ip;
    //是否是多次点击
    private boolean isMultipleClick;

    public ShowSANIPEvent(String ip, boolean isMultipleClick) {
        this.ip = ip;
        this.isMultipleClick = isMultipleClick;
    }

    public String getIp() {
        return ip;
    }

    public boolean isMultipleClick() {
        return isMultipleClick;
    }
}
