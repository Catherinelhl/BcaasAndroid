package io.bcaas.event;

import java.io.Serializable;

/**
 * @author catherine.brainwilliam
 * @since 2018/10/10
 * <p>
 * Debug模式下接收TCP的连接信息
 */
public class RefreshTCPConnectIPEvent implements Serializable {
    private String tcpConnectIP;

    public RefreshTCPConnectIPEvent(String tcpConnectIP) {
        this.tcpConnectIP = tcpConnectIP;
    }

    public String getTcpconnectIP() {
        return tcpConnectIP;
    }
}
