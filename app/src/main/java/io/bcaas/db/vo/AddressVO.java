package io.bcaas.db.vo;

import java.io.Serializable;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/29
 * 用於本地數據庫管理Wallet 地址數據類
 */
public class AddressVO implements Serializable {
    private String addressName;
    private String address;
    private int uid;
    private long createTime;

    public AddressVO() {
        super();
    }

    public AddressVO(int uid, long createTime, String address, String addressName) {
        super();
        this.uid = uid;
        this.createTime = createTime;
        this.address = address;
        this.addressName = addressName;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "AddressVO{" +
                "addressName='" + addressName + '\'' +
                ", address='" + address + '\'' +
                ", uid=" + uid +
                ", createTime=" + createTime +
                '}';
    }
}
