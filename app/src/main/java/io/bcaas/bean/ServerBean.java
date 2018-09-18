package io.bcaas.bean;

import java.io.Serializable;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/17
 * <p>
 * 存储当前服务器信息使用
 */
public class ServerBean implements Serializable {
    /*id，增序*/
    private int id;
    /*服务器url*/
    private String server;
    /*标注当前选中：用于界面切换服务器*/
    private boolean isChoose;
    /*是否不可用，默认是false，如果在请求超时后，需要将其置为true*/
    private boolean isUnavailable;

    public ServerBean() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ServerBean(int id, String server, boolean isChoose) {
        super();
        this.id = id;
        this.server = server;
        this.isChoose = isChoose;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }

    public boolean isUnavailable() {
        return isUnavailable;
    }

    public void setUnavailable(boolean unavailable) {
        isUnavailable = unavailable;
    }

    @Override
    public String toString() {
        return "ServerBean{" +
                "id=" + id +
                ", server='" + server + '\'' +
                ", isChoose=" + isChoose +
                ", isUnavailable=" + isUnavailable +
                '}';
    }
}
