package io.bcaas.bean;

import java.io.Serializable;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 * <p>
 * 地址管理数据类
 */
public class AddressBean implements Serializable {

    private String accountAliases;
    private String accountAddress;
    private int postion;

    public AddressBean(String accountAliases, String accountAddress) {
        super();
        this.accountAddress = accountAddress;
        this.accountAliases = accountAliases;
    }

    public void setPostion(int postion) {
        this.postion = postion;
    }

    public int getPostion() {
        return postion;
    }

    public String getAccountAliases() {
        return accountAliases;
    }

    public void setAccountAliases(String accountAliases) {
        this.accountAliases = accountAliases;
    }

    public String getAccountAddress() {
        return accountAddress;
    }

    public void setAccountAddress(String accountAddress) {
        this.accountAddress = accountAddress;
    }
}
