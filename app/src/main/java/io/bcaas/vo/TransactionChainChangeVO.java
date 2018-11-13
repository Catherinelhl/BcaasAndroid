package io.bcaas.vo;

import java.io.Serializable;

/**
 * Change representative Block 更換委託人區塊
 *
 * @author Costa
 * @version 1.0.0
 * @since 2018-07-25
 * 請求服務器需要傳遞的參數：transactionChainChangeVO
 * 「Change」區塊信息
 */

public class TransactionChainChangeVO implements Serializable {

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
    // 區塊功能 key:[Change]
    private String blockType;
    // 代理人錢包、也可用自已的錢包 做為投票的委託人
    private String representative;
    // 錢包地址
    private String wallet;
    // 工作證明 用blake2b(nonce || genesis_block_hash) >= threshold 得到在交易前要完成才可以發區塊
    private String work;
    // 建立日期 Timestamp
    private String date;

    public TransactionChainChangeVO() {
        super();
    }

    public TransactionChainChangeVO(String wallet) {
        super();
        this.wallet = wallet;
    }

    public TransactionChainChangeVO(String previous, String blockService, String blockType, String representative,
                                    String wallet, String work, String date) {
        super();
        this.previous = previous;
        this.blockService = blockService;
        this.blockType = blockType;
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
        return "TransactionChainChangeVO{" +
                "previous='" + previous + '\'' +
                ", blockService='" + blockService + '\'' +
                ", blockType='" + blockType + '\'' +
                ", representative='" + representative + '\'' +
                ", wallet='" + wallet + '\'' +
                ", work='" + work + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
