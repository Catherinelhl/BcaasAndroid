package io.bcaas.vo;

import java.io.Serializable;

/**
 * Send Tx Block 發送交易區塊
 *
 * @author Costa
 * @version 1.0.0
 * @since 2018-07-25
 * 請求服務器需要傳遞的參數：transactionChainSendVO
 * 「Send」區塊
 */
public class TransactionChainSendVO implements Serializable {

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
    // 區塊功能 key:[Send]
    private String blockType;
    // [Matrix/IPOS] [KeepAcc] 交易的方式有一般交易/持有證明/以及記帳
    private String blockTxType;
    // 目標的錢包
    private String destination_wallet;
    // 交易的餘額
    private String balance;
    // 交易的金額
    private String amount;
    // 代理人錢包、也可用自已的錢包 做為投票的委託人
    private String representative;
    // 錢包地址
    private String wallet;
    // 工作證明 用blake2b(nonce || genesis_block_hash) >= threshold 得到在交易前要完成才可以發區塊
    private String work;
    // 建立日期 Timestamp
    private String date;

    public TransactionChainSendVO() {
        super();
    }

    public TransactionChainSendVO(String wallet) {
        super();
        this.wallet = wallet;
    }

    public TransactionChainSendVO(String previous, String blockService, String blockType, String blockTxType,
                                  String destination_wallet, String balance, String representative, String wallet, String work, String date) {
        super();
        this.previous = previous;
        this.blockService = blockService;
        this.blockType = blockType;
        this.blockTxType = blockTxType;
        this.destination_wallet = destination_wallet;
        this.balance = balance;
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

    public String getDestination_wallet() {
        return destination_wallet;
    }

    public void setDestination_wallet(String destination_wallet) {
        this.destination_wallet = destination_wallet;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
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

    @Override
    public String toString() {
        return "TransactionChainSendVO{" +
                "previous='" + previous + '\'' +
                ", blockService='" + blockService + '\'' +
                ", blockType='" + blockType + '\'' +
                ", blockTxType='" + blockTxType + '\'' +
                ", destination_wallet='" + destination_wallet + '\'' +
                ", balance='" + balance + '\'' +
                ", amount='" + amount + '\'' +
                ", representative='" + representative + '\'' +
                ", wallet='" + wallet + '\'' +
                ", work='" + work + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
