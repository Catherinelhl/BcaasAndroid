package io.bcaas.vo;

import io.bcaas.bean.WalletHeight;
import java.util.List;

/**
 * 
 * @since 2018-07-11
 * 
 * @author Costa
 * 
 * @version 1.0.0
 * 
 */
@SuppressWarnings("rawtypes")
public class DatabaseVO {

	private String database;
	private String collection;
	private String ownerWallet;
	private String blockType;
	private WalletHeight walletHeight;
	// 创世区
	private GenesisVO genesisVO;
	// 智能合約
	private SmartContractVO smartContractVO;
	private List<SmartContractVO> smartContractVOList;
	// 朔源鏈
	private DataChainVO dataChainVO;
	private List<DataChainVO> dataChainVOList;
	// 交易鏈
	private TransactionChainVO transactionChainVO;
	private List<TransactionChainVO> transactionChainVOList;


	public DatabaseVO() {
		super();
	}

	public WalletHeight getWalletHeight() {
		return walletHeight;
	}

	public void setWalletHeight(WalletHeight walletHeight) {
		this.walletHeight = walletHeight;
	}

	public DatabaseVO(String database, String collection, String ownerWallet, String blockType,
			WalletHeight walletHeight, GenesisVO genesisVO, SmartContractVO smartContractVO,
			List<SmartContractVO> smartContractVOList, DataChainVO dataChainVO, List<DataChainVO> dataChainVOList,
			TransactionChainVO transactionChainVO, List<TransactionChainVO> transactionChainVOList) {
		super();
		this.database = database;
		this.collection = collection;
		this.ownerWallet = ownerWallet;
		this.blockType = blockType;
		this.walletHeight = walletHeight;
		this.genesisVO = genesisVO;
		this.smartContractVO = smartContractVO;
		this.smartContractVOList = smartContractVOList;
		this.dataChainVO = dataChainVO;
		this.dataChainVOList = dataChainVOList;
		this.transactionChainVO = transactionChainVO;
		this.transactionChainVOList = transactionChainVOList;
	}

	public DatabaseVO(String database, String collection, String ownerWallet, String blockType, GenesisVO genesisVO,
			SmartContractVO smartContractVO, List<SmartContractVO> smartContractVOList, DataChainVO dataChainVO,
			List<DataChainVO> dataChainVOList, TransactionChainVO transactionChainVO,
			List<TransactionChainVO> transactionChainVOList) {
		super();
		this.database = database;
		this.collection = collection;
		this.ownerWallet = ownerWallet;
		this.blockType = blockType;
		this.genesisVO = genesisVO;
		this.smartContractVO = smartContractVO;
		this.smartContractVOList = smartContractVOList;
		this.dataChainVO = dataChainVO;
		this.dataChainVOList = dataChainVOList;
		this.transactionChainVO = transactionChainVO;
		this.transactionChainVOList = transactionChainVOList;
	}


	public DatabaseVO(DataChainVO dataChainVO) {
		super();
		this.dataChainVO = dataChainVO;
	}

	public DatabaseVO(DataChainVO dataChainVO, List<DataChainVO> dataChainVOList) {
		super();
		this.dataChainVO = dataChainVO;
		this.dataChainVOList = dataChainVOList;
	}

	public DatabaseVO(String database, String collection) {
		super();
		this.database = database;
		this.collection = collection;
	}

	public DatabaseVO(String database, String collection, String ownerWallet) {
		super();
		this.database = database;
		this.collection = collection;
		this.ownerWallet = ownerWallet;
	}

	public DatabaseVO(String database, String collection, GenesisVO genesisVO) {
		super();
		this.database = database;
		this.collection = collection;
		this.genesisVO = genesisVO;
	}

	public DatabaseVO(String database, String collection, SmartContractVO smartContractVO) {
		super();
		this.database = database;
		this.collection = collection;
		this.smartContractVO = smartContractVO;
	}

	public DatabaseVO(String database, String collection, SmartContractVO smartContractVO,
			List<SmartContractVO> smartContractVOList) {
		super();
		this.database = database;
		this.collection = collection;
		this.smartContractVO = smartContractVO;
		this.smartContractVOList = smartContractVOList;
	}

	public DatabaseVO(String database, String collection, DataChainVO dataChainVO) {
		super();
		this.database = database;
		this.collection = collection;
		this.dataChainVO = dataChainVO;
	}

	public DatabaseVO(String database, String collection, DataChainVO dataChainVO, List<DataChainVO> dataChainVOList) {
		super();
		this.database = database;
		this.collection = collection;
		this.dataChainVO = dataChainVO;
		this.dataChainVOList = dataChainVOList;
	}

	public DatabaseVO(TransactionChainVO transactionChainVO) {
		super();
		this.transactionChainVO = transactionChainVO;
	}

	public DatabaseVO(TransactionChainVO transactionChainVO, List<TransactionChainVO> transactionChainVOList) {
		super();
		this.transactionChainVO = transactionChainVO;
		this.transactionChainVOList = transactionChainVOList;
	}

	public DatabaseVO(String database, String collection, TransactionChainVO transactionChainVO) {
		super();
		this.database = database;
		this.collection = collection;
	}

	public DatabaseVO(String database, String collection, String ownerWallet, TransactionChainVO transactionChainVO) {
		super();
		this.database = database;
		this.collection = collection;
		this.ownerWallet = ownerWallet;
		this.transactionChainVO = transactionChainVO;
	}

	public DatabaseVO(String database, String collection, TransactionChainVO transactionChainVO,
			List<TransactionChainVO> transactionChainVOList) {
		super();
		this.database = database;
		this.collection = collection;
		this.transactionChainVO = transactionChainVO;
		this.transactionChainVOList = transactionChainVOList;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getCollection() {
		return collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public String getOwnerWallet() {
		return ownerWallet;
	}

	public void setOwnerWallet(String ownerWallet) {
		this.ownerWallet = ownerWallet;
	}




	public SmartContractVO getSmartContractVO() {
		return smartContractVO;
	}

	public void setSmartContractVO(SmartContractVO smartContractVO) {
		this.smartContractVO = smartContractVO;
	}

	public List<SmartContractVO> getSmartContractVOList() {
		return smartContractVOList;
	}

	public void setSmartContractVOList(List<SmartContractVO> smartContractVOList) {
		this.smartContractVOList = smartContractVOList;
	}

	public DataChainVO getDataChainVO() {
		return dataChainVO;
	}

	public void setDataChainVO(DataChainVO dataChainVO) {
		this.dataChainVO = dataChainVO;
	}

	public List<DataChainVO> getDataChainVOList() {
		return dataChainVOList;
	}

	public void setDataChainVOList(List<DataChainVO> dataChainVOList) {
		this.dataChainVOList = dataChainVOList;
	}

	public TransactionChainVO getTransactionChainVO() {
		return transactionChainVO;
	}

	public void setTransactionChainVO(TransactionChainVO transactionChainVO) {
		this.transactionChainVO = transactionChainVO;
	}

	public List<TransactionChainVO> getTransactionChainVOList() {
		return transactionChainVOList;
	}

	public void setTransactionChainVOList(List<TransactionChainVO> transactionChainVOList) {
		this.transactionChainVOList = transactionChainVOList;
	}


	public GenesisVO getGenesisVO() {
		return genesisVO;
	}

	public void setGenesisVO(GenesisVO genesisVO) {
		this.genesisVO = genesisVO;
	}


	public String getBlockType() {
		return blockType;
	}

	public void setBlockType(String blockType) {
		this.blockType = blockType;
	}

	@Override
	public String toString() {
		return "DatabaseVO{" +
				"database='" + database + '\'' +
				", collection='" + collection + '\'' +
				", ownerWallet='" + ownerWallet + '\'' +
				", blockType='" + blockType + '\'' +
				", walletHeight=" + walletHeight +
				", genesisVO=" + genesisVO +
				", smartContractVO=" + smartContractVO +
				", smartContractVOList=" + smartContractVOList +
				", dataChainVO=" + dataChainVO +
				", dataChainVOList=" + dataChainVOList +
				", transactionChainVO=" + transactionChainVO +
				", transactionChainVOList=" + transactionChainVOList +
				'}';
	}
}
