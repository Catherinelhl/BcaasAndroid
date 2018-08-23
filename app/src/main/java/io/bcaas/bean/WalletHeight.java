package io.bcaas.bean;

import java.io.Serializable;

public class WalletHeight implements Serializable {

	private static final long serialVersionUID = 1L;
	private int sendHeight;
	private int receiveHeight;

	public WalletHeight() {
		super();
	}

	public WalletHeight(int sendHeight, int receiveHeight) {
		super();
		this.sendHeight = sendHeight;
		this.receiveHeight = receiveHeight;
	}

	public int getSendHeight() {
		return sendHeight;
	}

	public void setSendHeight(int sendHeight) {
		this.sendHeight = sendHeight;
	}

	public int getReceiveHeight() {
		return receiveHeight;
	}

	public void setReceiveHeight(int receiveHeight) {
		this.receiveHeight = receiveHeight;
	}

}
