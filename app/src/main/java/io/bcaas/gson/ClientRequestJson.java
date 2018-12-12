package io.bcaas.gson;

import java.io.Serializable;

/**
 * @author Costa Peng
 * @version 1.0.0
 * @since 2018/06/10
 * Client 請求使用, 接收封包格式定義
 */
public class ClientRequestJson implements Serializable {

    private static final long serialVersionUID = 1L;

    // 方法名稱
    private String methodName;
    // client端的 網卡編號 + 外網IP, double Shr256之後所獲得的值。
    private String macAddressAndExternalIp;

    // ==================================================================================================
    // constructors
    // ==================================================================================================

    public ClientRequestJson() {
        super();
    }

    public ClientRequestJson(String methodName) {
        this.methodName = methodName;
    }

    public ClientRequestJson(String methodName, String macAddressAndExternalIp) {
        this.methodName = methodName;
        this.macAddressAndExternalIp = macAddressAndExternalIp;
    }

    // ==================================================================================================
    // getter & setter
    // ==================================================================================================

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMacAddressAndExternalIp() {
        return macAddressAndExternalIp;
    }

    public void setMacAddressAndExternalIp(String macAddressAndExternalIp) {
        this.macAddressAndExternalIp = macAddressAndExternalIp;
    }

    @Override
    public String toString() {
        return "ClientRequestJson{" +
                "methodName='" + methodName + '\'' +
                ", macAddressAndExternalIp='" + macAddressAndExternalIp + '\'' +
                '}';
    }
}
