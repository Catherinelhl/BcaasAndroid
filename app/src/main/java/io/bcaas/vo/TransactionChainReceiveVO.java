package io.bcaas.vo;

import java.io.Serializable;

/**
 * Receive Tx Block 接收交易區塊
 * 
 * @since 2018-07-25
 * 
 * @author Costa
 * 
 * @version 1.0.0
 * 
 */

public class TransactionChainReceiveVO implements Serializable {

	private static final long serialVersionUID = 1L;
	// The 12-byte ObjectId value consists of:
	// a 4-byte value representing the seconds since the Unix epoch,
	// a 3-byte machine identifier,
	// a 2-byte process id,
	// a 3-byte counter, starting with a random value.
	// 前區塊的hash
	private String previous;
	// 服務的區塊名稱
	private String blockService;
	// 區塊功能 key:[Receive]
	private String blockType;
	// [Matrix/IPOS] [KeepAcc] 交易的方式有一般交易/持有證明/以及記帳
	private String blockTxType;
	// 接收交易前區塊的Hash
	private String sourceTxhash;
	// 接收到的金額
	private String amount;
	// 代理人錢包、也可用自已的錢包 做為投票的委託人
	private String representative;
	// 錢包地址
	private String wallet;
	// 工作證明 用blake2b(nonce || genesis_block_hash) >= threshold 得到在交易前要完成才可以發區塊
	private String work;
	// 建立日期 Timestamp
	private String date;

	public TransactionChainReceiveVO() {
		super();
	}

	public TransactionChainReceiveVO(String wallet) {
		super();
		this.wallet = wallet;
	}

	public TransactionChainReceiveVO(String previous, String blockService, String blockType, String blockTxType,
			String sourceTxhash, String amount, String representative, String wallet, String work, String date) {
		super();
		this.previous = previous;
		this.blockService = blockService;
		this.blockType = blockType;
		this.blockTxType = blockTxType;
		this.sourceTxhash = sourceTxhash;
		this.amount = amount;
		this.representative = representative;
		this.wallet = wallet;
		this.work = work;
		this.date = date;
	}

	public String getPrevious() {
		return previous;
	}

	public void setPrevious(String previous) {
		this.previous = previous;
	}

	public String getBlockService() {
		return blockService;
	}

	public void setBlockService(String blockService) {
		this.blockService = blockService;
	}

	public String getBlockType() {
		return blockType;
	}

	public void setBlockType(String blockType) {
		this.blockType = blockType;
	}

	public String getBlockTxType() {
		return blockTxType;
	}

	public void setBlockTxType(String blockTxType) {
		this.blockTxType = blockTxType;
	}

	public String getSourceTxhash() {
		return sourceTxhash;
	}

	public void setSourceTxhash(String sourceTxhash) {
		this.sourceTxhash = sourceTxhash;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getRepresentative() {
		return representative;
	}

	public void setRepresentative(String representative) {
		this.representative = representative;
	}

	public String getWallet() {
		return wallet;
	}

	public void setWallet(String wallet) {
		this.wallet = wallet;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
