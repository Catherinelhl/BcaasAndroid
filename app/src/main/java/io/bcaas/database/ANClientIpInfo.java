package io.bcaas.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "ANCLIENT_IP_INFO".
 */
@Entity
public class ANClientIpInfo {

    @Id
    private Long id;
    private String macAddressExternalIp;
    private String externalIp;
    private String internalIp;
    private String clientType;
    private Integer externalPort;
    private Integer internalPort;
    private String virtualCoin;
    private Integer rpcPort;

    @Generated(hash = 1218311147)
    public ANClientIpInfo() {
    }

    public ANClientIpInfo(Long id) {
        this.id = id;
    }

    @Generated(hash = 171066738)
    public ANClientIpInfo(Long id, String macAddressExternalIp, String externalIp, String internalIp, String clientType, Integer externalPort, Integer internalPort, String virtualCoin, Integer rpcPort) {
        this.id = id;
        this.macAddressExternalIp = macAddressExternalIp;
        this.externalIp = externalIp;
        this.internalIp = internalIp;
        this.clientType = clientType;
        this.externalPort = externalPort;
        this.internalPort = internalPort;
        this.virtualCoin = virtualCoin;
        this.rpcPort = rpcPort;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMacAddressExternalIp() {
        return macAddressExternalIp;
    }

    public void setMacAddressExternalIp(String macAddressExternalIp) {
        this.macAddressExternalIp = macAddressExternalIp;
    }

    public String getExternalIp() {
        return externalIp;
    }

    public void setExternalIp(String externalIp) {
        this.externalIp = externalIp;
    }

    public String getInternalIp() {
        return internalIp;
    }

    public void setInternalIp(String internalIp) {
        this.internalIp = internalIp;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public Integer getExternalPort() {
        return externalPort;
    }

    public void setExternalPort(Integer externalPort) {
        this.externalPort = externalPort;
    }

    public Integer getInternalPort() {
        return internalPort;
    }

    public void setInternalPort(Integer internalPort) {
        this.internalPort = internalPort;
    }

    public String getVirtualCoin() {
        return virtualCoin;
    }

    public void setVirtualCoin(String virtualCoin) {
        this.virtualCoin = virtualCoin;
    }

    public Integer getRpcPort() {
        return rpcPort;
    }

    public void setRpcPort(Integer rpcPort) {
        this.rpcPort = rpcPort;
    }

    @Override
    public String toString() {
        return "ANClientIpInfo{" +
                "id=" + id +
                ", macAddressExternalIp='" + macAddressExternalIp + '\'' +
                ", externalIp='" + externalIp + '\'' +
                ", internalIp='" + internalIp + '\'' +
                ", clientType='" + clientType + '\'' +
                ", externalPort=" + externalPort +
                ", internalPort=" + internalPort +
                ", virtualCoin='" + virtualCoin + '\'' +
                ", rpcPort=" + rpcPort +
                '}';
    }
}
