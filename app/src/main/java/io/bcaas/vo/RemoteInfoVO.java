package io.bcaas.vo;

import java.io.Serializable;

/**
 * Remote information
 *
 * @author Costa
 * @version 1.0.0
 * 請求服務器需要傳遞的參數：remoteInfoVO
 * 獲取當前Wallet的IP信息
 * @since 2018-10-17
 */
public class RemoteInfoVO implements Serializable {

	private static final long serialVersionUID = 1l;


	private String realIP;

	public RemoteInfoVO() {
		super();
	}

	public RemoteInfoVO(String realIP) {
		super();
		this.realIP = realIP;
	}


	public String getRealIP() {
		return realIP;
	}

	public void setRealIP(String realIP) {
		this.realIP = realIP;
	}

	@Override
	public String toString() {
		return "RemoteInfoVO{" +
				"realIP='" + realIP + '\'' +
				'}';
	}
}
