package io.bcaas.vo;

import java.io.Serializable;

/**
 * 
 * Remote information
 * 
 * @since 2018-10-17
 * 
 * @author Costa
 * 
 * @version 1.0.0
 * 
 */
public class RemoteInfoVO implements Serializable {

	private static final long serialVersionUID = 1l;


	private String realIP;

	public RemoteInfoVO() {
		super();
	}

	public String getRealIP() {
		return realIP;
	}

	public void setRealIP(String realIP) {
		this.realIP = realIP;
	}

}
