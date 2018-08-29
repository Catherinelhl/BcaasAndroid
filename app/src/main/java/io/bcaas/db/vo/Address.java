package io.bcaas.db.vo;

import java.io.Serializable;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/29
 * 创建一个管理地址的数据类
 */
public class Address implements Serializable {
    private String addressName;
    private String address;

    public Address(){
        super();
    }
    public Address(String address,String addressName){
        super();
        this.address=address;
        this.addressName=addressName;
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
        return "Address{" +
                "addressName='" + addressName + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
