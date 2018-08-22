package io.bcaas.db.vo;


/**
 * SQLite Value Object
 *
 * @since 2018-08-22
 *
 * @author Costa
 *
 * @version 1.0.0
 *
 */

public class KeyStore {

    private int uid;
    private String keyStore;
    private String createTime;

    public KeyStore(int uid, String keyStore, String createTime) {
        this.uid = uid;
        this.keyStore = keyStore;
        this.createTime = createTime;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
