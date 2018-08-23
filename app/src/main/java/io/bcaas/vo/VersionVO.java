package io.bcaas.vo;

import java.io.Serializable;

/**
 * @author Costa Peng
 * @version 1.0.0
 * @since 2018/06/10
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
    // Platform update url
    private String updateUrl;
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

}