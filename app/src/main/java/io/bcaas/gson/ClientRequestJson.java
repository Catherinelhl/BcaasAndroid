package io.bcaas.gson;

import java.io.Serializable;

/**
 * 接收封包格式定義
 */
public class ClientRequestJson implements Serializable {

	private static final long serialVersionUID = 8491898590952783985L;

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

}
