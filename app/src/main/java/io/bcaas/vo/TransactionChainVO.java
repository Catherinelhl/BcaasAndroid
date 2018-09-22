package io.bcaas.vo;

import java.io.Serializable;

/**
 * 交易資料
 * 
 * @since 2018-07-11
 * 
 * @author Costa
 * 
 * @version 1.0.0
 * 
 * @param                <T>
 * 
 * @param _id            The 12-byte ObjectId value
 * @param tc             TC Block type:Send.. Receive.. Open.. Change..
 * @param signature      Use PrivateKey Encryption Tx Double Sha256
 * @param publicKey      User publicKey
 * @param height         Block height
 * @param produceKeyType ECC/Bip39 產生公私鑰種類
 * @param systemTime     System time
 * 
 */

public class TransactionChainVO<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	// The 12-byte ObjectId value consists of:
	// a 4-byte value representing the seconds since the Unix epoch,
	// a 3-byte machine identifier,
	// a 2-byte process id,
	// a 3-byte counter, starting with a random value.
	private String _id;

	// TC Block type:Send.. Receive.. Open.. Change..
	private T tc;

	// 簽名是用[TC]...等鏈的信息去做double sha256, hash在用私鑰加密
	private String signature;

	// for Receive 判斷用
	private String signatureSend;

	// 公鑰
	private String publicKey;

	// block height
	private int height = 0;

	// ECC/Bip39 產生公私鑰種類
	private String produceKeyType;

	// Tc區塊 double SHA-256, Explorer index
	private String txHash;

	// System time
	private String systemTime;

	// 當前區塊哈希值
	private String currentTxhash;

	// 發送人地址：open\receive區塊回傳用
	private String walletSend;

	public TransactionChainVO() {
		super();
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public T getTc() {
		return tc;
	}

	public void setTc(T tc) {
		this.tc = tc;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getSignatureSend() {
		return signatureSend;
	}

	public void setSignatureSend(String signatureSend) {
		this.signatureSend = signatureSend;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getProduceKeyType() {
		return produceKeyType;
	}

	public void setProduceKeyType(String produceKeyType) {
		this.produceKeyType = produceKeyType;
	}

	public String getTxHash() {
		return txHash;
	}

	public void setTxHash(String txHash) {
		this.txHash = txHash;
	}

	public String getSystemTime() {
		return systemTime;
	}

	public void setSystemTime(String systemTime) {
		this.systemTime = systemTime;
	}

	public String getCurrentTxhash() {
		return currentTxhash;
	}

	public void setCurrentTxhash(String currentTxhash) {
		this.currentTxhash = currentTxhash;
	}

	public String getWalletSend() {
		return walletSend;
	}

	public void setWalletSend(String walletSend) {
		this.walletSend = walletSend;
	}
}
