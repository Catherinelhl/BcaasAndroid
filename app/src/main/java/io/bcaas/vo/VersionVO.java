package io.bcaas.vo;

import java.io.Serializable;

/**
 * @author Costa Peng
 * @version 1.0.0
 * @since 2018/06/10
 * 請求服務器需要傳遞的參數：versionVO
 * 「Version」版本信息
 */
public class VersionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // Object id
    private String _id;
    // Authorize key
    private String authKey;
    // Platform version
    private String version;
    // 此次版本是否要強迫更新
    private int forceUpgrade;
    // 安装档下载
    private String updateUrl;
    //应用程序内部资源下载
    private String updateSourceUrl;
    //应用商店连结
    private String appStoreUrl;
    // Platform type
    private String type;
    // Version Modify time
    private String motifyTime;
    // Record system time
    private String systemTime;

    public VersionVO() {
        super();
    }

    public VersionVO(String authKey) {
        super();
        this.authKey = authKey;
    }

    public VersionVO(String _id, String authKey, String version, int forceUpgrade, String updateUrl, String type, String motifyTime,
                     String systemTime) {
        super();
        this._id = _id;
        this.authKey = authKey;
        this.version = version;
        this.forceUpgrade = forceUpgrade;
        this.updateUrl = updateUrl;
        this.type = type;
        this.motifyTime = motifyTime;
        this.systemTime = systemTime;
    }

    public int getForceUpgrade() {
        return forceUpgrade;
    }

    public void setForceUpgrade(int forceUpgrade) {
        this.forceUpgrade = forceUpgrade;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMotifyTime() {
        return motifyTime;
    }

    public void setMotifyTime(String motifyTime) {
        this.motifyTime = motifyTime;
    }

    public String getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(String systemTime) {
        this.systemTime = systemTime;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }


    public String getUpdateSourceUrl() {
        return updateSourceUrl;
    }

    public void setUpdateSourceUrl(String updateSourceUrl) {
        this.updateSourceUrl = updateSourceUrl;
    }

    public String getAppStoreUrl() {
        return appStoreUrl;
    }

    public void setAppStoreUrl(String appStoreUrl) {
        this.appStoreUrl = appStoreUrl;
    }

    @Override
    public String toString() {
        return "VersionVO{" +
                "_id='" + _id + '\'' +
                ", authKey='" + authKey + '\'' +
                ", version='" + version + '\'' +
                ", forceUpgrade=" + forceUpgrade +
                ", updateUrl='" + updateUrl + '\'' +
                ", updateSourceUrl='" + updateSourceUrl + '\'' +
                ", appStoreUrl='" + appStoreUrl + '\'' +
                ", type='" + type + '\'' +
                ", motifyTime='" + motifyTime + '\'' +
                ", systemTime='" + systemTime + '\'' +
                '}';
    }
}