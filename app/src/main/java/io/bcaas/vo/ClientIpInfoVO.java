package io.bcaas.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ClientIpInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // MAC address + External Ip 再 double sha256 後所取得的值
    private String macAddressExternalIp;
    // 外網IP
    private String externalIp;
    // 內網IP
    private String internalIp;
    // Client類型. Ex. wallet, authNode
    private String clientType;
    // 外網埠號(端口)
    private int externalPort;
    // 內網埠號(端口)
    private int internalPort;
    // 虛擬幣代號  Ex. [{"BCC":"API-key"},{"PC":"API-key"},{"TC":"API-key"} ]
    private ArrayList<LinkedHashMap<String, String>> virtualCoin;
    // RPC port
    private int rpcPort;

    // ==================================================================================================
    // constructors
    // ==================================================================================================

    public ClientIpInfoVO() {
        super();
    }

    public ClientIpInfoVO(String macAddressExternalIp, String externalIp, String internalIp, String clientType,
                          int externalPort, int internalPort, ArrayList<LinkedHashMap<String, String>> virtualCoin, int rpcPort) {
        super();
        this.macAddressExternalIp = macAddressExternalIp;
        this.externalIp = externalIp;
        this.internalIp = internalIp;
        this.clientType = clientType;
        this.externalPort = externalPort;
        this.internalPort = internalPort;
        this.virtualCoin = virtualCoin;
        this.rpcPort = rpcPort;
    }

    // ==================================================================================================
    // get & set
    // ==================================================================================================

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

    public int getExternalPort() {
        return externalPort;
    }

    public void setExternalPort(int externalPort) {
        this.externalPort = externalPort;
    }

    public int getInternalPort() {
        return internalPort;
    }

    public void setInternalPort(int internalPort) {
        this.internalPort = internalPort;
    }

    public ArrayList<LinkedHashMap<String, String>> getVirtualCoin() {
        return virtualCoin;
    }

    public void setVirtualCoin(ArrayList<LinkedHashMap<String, String>> virtualCoin) {
        this.virtualCoin = virtualCoin;
    }

    public int getRpcPort() {
        return rpcPort;
    }

    public void setRpcPort(int rpcPort) {
        this.rpcPort = rpcPort;
    }

    @Override
    public String toString() {
        return "ClientIpInfoVO{" +
                "macAddressExternalIp='" + macAddressExternalIp + '\'' +
                ", externalIp='" + externalIp + '\'' +
                ", internalIp='" + internalIp + '\'' +
                ", clientType='" + clientType + '\'' +
                ", externalPort=" + externalPort +
                ", internalPort=" + internalPort +
                ", virtualCoin=" + virtualCoin +
                ", rpcPort=" + rpcPort +
                '}';
    }
}
