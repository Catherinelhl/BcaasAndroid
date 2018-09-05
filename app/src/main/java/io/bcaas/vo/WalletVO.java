package io.bcaas.vo;

import java.io.Serializable;
import java.util.List;

import io.bcaas.bean.SeedFullNodeBean;
import io.bcaas.bean.WalletHeight;

/**
 * Redis wallet 存儲資料(TTL)
 * @author Andy Wang
 */
/**
 * Redis wallet 存儲資料(TTL)
 * @author Andy Wang
 */
public class WalletVO implements Serializable {

	private static final long serialVersionUID = 1l;

	private String walletAddress;

	private String accessToken;

	private String walletBalance;

	private String blockService;

	private ClientIpInfoVO clientIpInfoVO;

	private String MethodName;

	private List<SeedFullNodeBean> seedFullNodeList;

	private String blockType;

	private WalletHeight walletHeight;

	private String representative;

	// ========================================================================================================================
	// Constructors
	// ========================================================================================================================
	public WalletVO() {
		super();
	}

	public WalletVO(String walletAddress, String accessToken, String walletBalance, String blockService,
					ClientIpInfoVO clientIpInfoVO, List<SeedFullNodeBean> seedFullNodeList, String blockType,
					WalletHeight walletHeight) {
		super();
		this.walletAddress = walletAddress;
		this.accessToken = accessToken;
		this.walletBalance = walletBalance;
		this.blockService = blockService;
		this.clientIpInfoVO = clientIpInfoVO;
		this.seedFullNodeList = seedFullNodeList;
		this.blockType = blockType;
		this.walletHeight = walletHeight;
	}

	public WalletVO(String walletAddress) {
		this.walletAddress = walletAddress;
	}
	public WalletVO(String blockService,String walletAddress,String blockType) {
		this.walletAddress = walletAddress;
		this.blockService = blockService;
		this.blockType = blockType;
	}
	public WalletVO(String walletAddress, String accessToken) {
		this.walletAddress = walletAddress;
		this.accessToken = accessToken;
	}

	public WalletVO(String walletAddress, String accessToken, ClientIpInfoVO clientIpInfoVO) {
		this.walletAddress = walletAddress;
		this.accessToken = accessToken;
		this.clientIpInfoVO = clientIpInfoVO;
	}

	public WalletVO(String walletAddress, String accessToken, String blockService, ClientIpInfoVO clientIpInfoVO, List<SeedFullNodeBean> seedFullNodeList) {
		this.walletAddress = walletAddress;
		this.accessToken = accessToken;
		this.blockService = blockService;
		this.clientIpInfoVO = clientIpInfoVO;
		this.seedFullNodeList = seedFullNodeList;
	}

	public WalletVO(String walletAddress, String accessToken, String blockService, ClientIpInfoVO clientIpInfoVO) {
		this.walletAddress = walletAddress;
		this.accessToken = accessToken;
		this.blockService = blockService;
		this.clientIpInfoVO = clientIpInfoVO;
	}


	public WalletVO(String walletAddress, String accessToken, String walletBalance, String blockService,
					ClientIpInfoVO clientIpInfoVO, List<SeedFullNodeBean> seedFullNodeList) {
		super();
		this.walletAddress = walletAddress;
		this.accessToken = accessToken;
		this.walletBalance = walletBalance;
		this.blockService = blockService;
		this.clientIpInfoVO = clientIpInfoVO;
		this.seedFullNodeList = seedFullNodeList;
	}

	// ========================================================================================================================
	// Get & Set
	// ========================================================================================================================



	public String getMethodName() {
		return MethodName;
	}

	public String getRepresentative() {
		return representative;
	}

	public void setRepresentative(String representative) {
		this.representative = representative;
	}

	public void setMethodName(String methodName) {
		MethodName = methodName;
	}

	public String getBlockType() {
		return blockType;
	}

	public void setBlockType(String blockType) {
		this.blockType = blockType;
	}

	public WalletHeight getWalletHeight() {
		return walletHeight;
	}

	public void setWalletHeight(WalletHeight walletHeight) {
		this.walletHeight = walletHeight;
	}

	public String getWalletAddress() {
		return walletAddress;
	}

	public void setWalletAddress(String walletAddress) {
		this.walletAddress = walletAddress;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public ClientIpInfoVO getClientIpInfoVO() {
		return clientIpInfoVO;
	}

	public void setClientIpInfoVO(ClientIpInfoVO clientIpInfoVO) {
		this.clientIpInfoVO = clientIpInfoVO;
	}

	public String getBlockService() {
		return blockService;
	}

	public void setBlockService(String blockService) {
		this.blockService = blockService;
	}

	public List<SeedFullNodeBean> getSeedFullNodeList() {
		return seedFullNodeList;
	}

	public void setSeedFullNodeList(List<SeedFullNodeBean> seedFullNodeList) {
		this.seedFullNodeList = seedFullNodeList;
	}

	public String getWalletBalance() {
		return walletBalance;
	}

	public void setWalletBalance(String walletBalance) {
		this.walletBalance = walletBalance;
	}

}