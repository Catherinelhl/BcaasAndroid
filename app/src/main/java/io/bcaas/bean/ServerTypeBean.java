package io.bcaas.bean;

import java.io.Serializable;

/**
 * @author catherine.brainwilliam
 * @since 2018/10/8
 * <p>
 * 存储当前所有的服务器类别
 */
public class ServerTypeBean implements Serializable {

    private String serverType;
    private String serverName;
    private boolean isChoose;

    public ServerTypeBean() {
        super();
    }

    public ServerTypeBean(String serverType, String serverName) {
        super();
        this.serverName = serverName;
        this.serverType = serverType;
    }

    public ServerTypeBean(String serverType, String serverName, boolean isChoose) {
        super();
        this.serverName = serverName;
        this.serverType = serverType;
        this.isChoose = isChoose;
    }


    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public String toString() {
        return "ServerTypeBean{" +
                "serverType='" + serverType + '\'' +
                ", serverName='" + serverName + '\'' +
                ", isChoose=" + isChoose +
                '}';
    }
}
