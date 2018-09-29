package io.bcaas.bean;

import java.io.Serializable;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/17
 * <p>
 * 存储当前服务器信息使用
 */
public class ServerBean implements Serializable {
    /*id，增序,默认为-1*/
    private int id = -1;
    /*服务器sfn_url*/
    private String sfnServer;
    /*服务器 api_url*/
    private String apiServer;
    /*服务器 update_url*/
    private String updateServer;
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

    public ServerBean(int id, String sfnServer, boolean isChoose) {
        super();
        this.id = id;
        this.sfnServer = sfnServer;
        this.isChoose = isChoose;
    }

    public ServerBean(int id, String sfnServer, String apiServer, String updateServer, boolean isChoose) {
        super();
        this.id = id;
        this.sfnServer = sfnServer;
        this.apiServer = apiServer;
        this.updateServer = updateServer;
        this.isChoose = isChoose;
    }

    public String getSfnServer() {
        return sfnServer;
    }

    public void setSfnServer(String sfnServer) {
        this.sfnServer = sfnServer;
    }

    public String getApiServer() {
        return apiServer;
    }

    public void setApiServer(String apiServer) {
        this.apiServer = apiServer;
    }

    public String getUpdateServer() {
        return updateServer;
    }

    public void setUpdateServer(String updateServer) {
        this.updateServer = updateServer;
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
                ", sfnServer='" + sfnServer + '\'' +
                ", apiServer='" + apiServer + '\'' +
                ", updateServer='" + updateServer + '\'' +
                ", isChoose=" + isChoose +
                ", isUnavailable=" + isUnavailable +
                '}';
    }
}
