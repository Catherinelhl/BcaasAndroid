package io.bcaas.gson;


import java.io.Serializable;
import java.util.List;

import io.bcaas.vo.GenesisVO;
import io.bcaas.vo.PaginationVO;
import io.bcaas.vo.TransactionChainVO;

@SuppressWarnings("rawtypes")
public class WalletResponseJson implements Serializable {

	private static final long serialVersionUID = 1L;

	// 是否成功
	private boolean success;
	// 狀態碼
	private int code;
	// 回應訊息
	private String message;
	// 映射方法名稱
	private String methodName;
	// 錢包餘額
	private String walletBalance;
	// 錢包地址
	private String walletAddress;
	//創世塊
	private GenesisVO genesisVO;
	// 最新交易訊息
	private TransactionChainVO transactionChainVO;
	// 尚未產生Receive的區塊
	private List<PaginationVO> paginationVOList;

	public WalletResponseJson() {
		super();
	}

	public WalletResponseJson(boolean success, int code, String message, String methodName) {
		super();
		this.success = success;
		this.code = code;
		this.message = message;
		this.methodName = methodName;
	}

	public WalletResponseJson(boolean success, int code, String message, String walletBalance, String walletAddress, TransactionChainVO transactionChainVO, List<PaginationVO> paginationVOList) {
		super();
		this.success = success;
		this.code = code;
		this.message = message;
		this.walletBalance = walletBalance;
		this.walletAddress = walletAddress;
		this.transactionChainVO = transactionChainVO;
		this.paginationVOList = paginationVOList;
	}

	public WalletResponseJson(boolean success, int code, String message, List<PaginationVO> paginationVOList) {
		super();
		this.success = success;
		this.code = code;
		this.message = message;
		this.paginationVOList = paginationVOList;
	}

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

	public String getWalletBalance() {
		return walletBalance;
	}

	public void setWalletBalance(String walletBalance) {
		this.walletBalance = walletBalance;
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

	public List<PaginationVO> getPaginationVOList() {
		return paginationVOList;
	}

	public void setPaginationVOList(List<PaginationVO> paginationVOList) {
		this.paginationVOList = paginationVOList;
	}

	public GenesisVO getGenesisVO() {
		return genesisVO;
	}

	public void setGenesisVO(GenesisVO genesisVO) {
		this.genesisVO = genesisVO;
	}

	@Override
	public String toString() {
		return "WalletResponseJson{" +
				"success=" + success +
				", code=" + code +
				", message='" + message + '\'' +
				", methodName='" + methodName + '\'' +
				", walletBalance='" + walletBalance + '\'' +
				", walletAddress='" + walletAddress + '\'' +
				", genesisVO=" + genesisVO +
				", transactionChainVO=" + transactionChainVO +
				", paginationVOList=" + paginationVOList +
				'}';
	}
}
