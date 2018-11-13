package io.bcaas.event;

import java.io.Serializable;

/**
 * @author catherine.brainwilliam
 * @since 2018/10/10
 * <p>
 * 發出此訂閱：Debug模式下接收TCP的连接的IP信息並將其顯示在界面上
 */
public class RefreshTCPConnectIPEvent implements Serializable {
    private String tcpConnectIP;

    public RefreshTCPConnectIPEvent(String tcpConnectIP) {
        this.tcpConnectIP = tcpConnectIP;
    }

    public String getTcpConnectIP() {
        return tcpConnectIP;
    }
}
