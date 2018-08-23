package io.bcaas.gson;

import java.io.Serializable;

/**
 * Server 回應使用
 *
 * @author Costa Peng
 * @version 1.0.0
 * @since 2018/06/10
 */
public class ServerResponseJson implements Serializable {

    private static final long serialVersionUID = 5536980246241945546L;

    // 是否成功
    private boolean success;
    // 狀態碼
    private int code;
    // Debug訊息
    private String message;
    // 方法名稱
    private String methodName;
    // client端的 網卡編號 + 外網IP, double Shr256之後所獲得的值。
    private String macAddressAndExternalIp;
    // 拆送封包,大於0的時候表示後面還有n筆資訊
    private int size;

    // ==================================================================================================
    // constructors
    // ==================================================================================================

    public ServerResponseJson() {
        super();
    }

    public ServerResponseJson(boolean success, int code, String message, String methodName,
                              String macAddressAndExternalIp) {
        super();
        this.success = success;
        this.code = code;
        this.message = message;
        this.methodName = methodName;
        this.macAddressAndExternalIp = macAddressAndExternalIp;
    }

    public ServerResponseJson(boolean success, int code, String message, String methodName) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.methodName = methodName;
    }

    public ServerResponseJson(String methodName) {
        this.methodName = methodName;
    }

    public ServerResponseJson(boolean success, int code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    // ==================================================================================================
    // getter & setter
    // ==================================================================================================

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

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

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
