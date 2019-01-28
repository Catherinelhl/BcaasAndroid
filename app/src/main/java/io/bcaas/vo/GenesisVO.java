package io.bcaas.vo;

import java.io.Serializable;

/**
 * 創世塊
 * 
 * @since 2015-08-23
 * 
 * @author Costa
 * 
 * @version 1.0.0
 * 請求服務器需要傳遞的參數：genesisVO 創世塊
 */

public class GenesisVO implements Serializable {

	private static final long serialVersionUID = 1L;

	// The 12-byte ObjectId value consists of:
	// a 4-byte value representing the seconds since the Unix epoch,
	// a 3-byte machine identifier,
	// a 2-byte process id,
	// a 3-byte counter, starting with a random value.
	private String _id;
	// 前區塊的hash
	private String previous;
	// 發行單位
	private String publicUnit;
	// 服務的區塊名稱
	private String blockService;
	// 服務的幣種簡稱
	private String currencyUnit;
	// 總發行量
	private String circulation;
	// 獎勵金額
	private String coinBase;
	// 創世塊生成的第一個錢包地址
	private String genesisBlockAccount;
	// 獎勵的帳戶地址
	private String coinBaseAccount;
	// 利息率
	private String interestRate;
	// 工作證明 用blake2b(nonce || genesis_block_hash) >= threshold 得到在交易前要完成才可以發區塊
	private String work;
	// 發行系統時間 UTC
	private String createTime;
	// 系統時間 ISO Date '字串格式'
	private String systemTime;

	public GenesisVO(String _id, String previous, String publicUnit, String blockService, String currencyUnit,
			String circulation, String coinBase, String genesisBlockAccount, String coinBaseAccount, String work,
			String createTime, String systemTime) {
		super();
		this._id = _id;
		this.previous = previous;
		this.publicUnit = publicUnit;
		this.blockService = blockService;
		this.currencyUnit = currencyUnit;
		this.circulation = circulation;
		this.coinBase = coinBase;
		this.genesisBlockAccount = genesisBlockAccount;
		this.coinBaseAccount = coinBaseAccount;
		this.work = work;
		this.createTime = createTime;
		this.systemTime = systemTime;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getPrevious() {
		return previous;
	}

	public void setPrevious(String previous) {
		this.previous = previous;
	}

	public String getPublicUnit() {
		return publicUnit;
	}

	public void setPublicUnit(String publicUnit) {
		this.publicUnit = publicUnit;
	}

	public String getBlockService() {
		return blockService;
	}

	public void setBlockService(String blockService) {
		this.blockService = blockService;
	}

	public String getCurrencyUnit() {
		return currencyUnit;
	}

	public void setCurrencyUnit(String currencyUnit) {
		this.currencyUnit = currencyUnit;
	}

	public String getCirculation() {
		return circulation;
	}

	public void setCirculation(String circulation) {
		this.circulation = circulation;
	}

	public String getCoinBase() {
		return coinBase;
	}

	public void setCoinBase(String coinBase) {
		this.coinBase = coinBase;
	}

	public String getGenesisBlockAccount() {
		return genesisBlockAccount;
	}

	public void setGenesisBlockAccount(String genesisBlockAccount) {
		this.genesisBlockAccount = genesisBlockAccount;
	}

	public String getCoinBaseAccount() {
		return coinBaseAccount;
	}

	public void setCoinBaseAccount(String coinBaseAccount) {
		this.coinBaseAccount = coinBaseAccount;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getSystemTime() {
		return systemTime;
	}

	public void setSystemTime(String systemTime) {
		this.systemTime = systemTime;
	}

	public String getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(String interestRate) {
		this.interestRate = interestRate;
	}

	@Override
	public String toString() {
		return "GenesisVO{" +
				"_id='" + _id + '\'' +
				", previous='" + previous + '\'' +
				", publicUnit='" + publicUnit + '\'' +
				", blockService='" + blockService + '\'' +
				", currencyUnit='" + currencyUnit + '\'' +
				", circulation='" + circulation + '\'' +
				", coinBase='" + coinBase + '\'' +
				", genesisBlockAccount='" + genesisBlockAccount + '\'' +
				", coinBaseAccount='" + coinBaseAccount + '\'' +
				", interestRate='" + interestRate + '\'' +
				", work='" + work + '\'' +
				", createTime='" + createTime + '\'' +
				", systemTime='" + systemTime + '\'' +
				'}';
	}
}
