package io.bcaas.gson;


import java.io.Serializable;

import io.bcaas.vo.TransactionChainVO;

@SuppressWarnings("rawtypes")
public class WalletRequestJson implements Serializable {

	private static final long serialVersionUID = 1L;

	//Access Token 
	private String accessToken;
	// 服務的區塊名稱
	private String blockService;
	// 錢包地址
	private String walletAddress;
	// 交易鏈
	private TransactionChainVO transactionChainVO;

	public WalletRequestJson() {
		super();
	}

	public WalletRequestJson(String blockService, String walletAddress) {
		super();
		this.blockService = blockService;
		this.walletAddress = walletAddress;
	}

	public WalletRequestJson(String accessToken,String blockService, String walletAddress) {
		super();
		this.accessToken = accessToken;
		this.blockService = blockService;
		this.walletAddress = walletAddress;
	}

	public WalletRequestJson(String accessToken,String blockService, String walletAddress, TransactionChainVO transactionChainVO) {
		super();
		this.accessToken = accessToken;
		this.blockService = blockService;
		this.walletAddress = walletAddress;
		this.transactionChainVO = transactionChainVO;
	}
	
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getBlockService() {
		return blockService;
	}

	public void setBlockService(String blockService) {
		this.blockService = blockService;
	}

	public String getWalletAddress() {
		return walletAddress;
	}

	public void setWalletAddress(String walletAddress) {
		this.walletAddress = walletAddress;
	}

	public TransactionChainVO getTransactionChainVO() {
		return transactionChainVO;
	}

	public void setTransactionChainVO(TransactionChainVO transactionChainVO) {
		this.transactionChainVO = transactionChainVO;
	}

	@Override
	public String toString() {
		return "WalletRequestJson{" +
				"accessToken='" + accessToken + '\'' +
				", blockService='" + blockService + '\'' +
				", walletAddress='" + walletAddress + '\'' +
				", transactionChainVO=" + transactionChainVO +
				'}';
	}
}
