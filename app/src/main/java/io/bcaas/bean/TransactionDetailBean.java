package io.bcaas.bean;

import java.io.Serializable;

/**
 * @author catherine.brainwilliam
 * @since 2018/12/10
 * 显示数据详情
 */
public class TransactionDetailBean implements Serializable {

    //发送方
    private String sendAccount;
    //接受方
    private String receiveAccount;
    //交易hash，可以用于查询
    private String txHash;
    //区块高度
    private String height;
    //交易时间
    private String transactionTime;
    //交易金额
    private String balance;
    //交易币种
    private String blockService;
    //当前hash的类型，open/receive/send
    private String hashType;
    //是否是发送
    private boolean isSend;

    public String getSendAccount() {
        return sendAccount;
    }

    public void setSendAccount(String sendAccount) {
        this.sendAccount = sendAccount;
    }

    public String getReceiveAccount() {
        return receiveAccount;
    }

    public void setReceiveAccount(String receiveAccount) {
        this.receiveAccount = receiveAccount;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }


    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

    @Override
    public String toString() {
        return "TransactionDetailBean{" +
                "sendAccount='" + sendAccount + '\'' +
                ", receiveAccount='" + receiveAccount + '\'' +
                ", txHash='" + txHash + '\'' +
                ", height='" + height + '\'' +
                ", transactionTime='" + transactionTime + '\'' +
                ", balance='" + balance + '\'' +
                ", blockService='" + blockService + '\'' +
                ", hashType='" + hashType + '\'' +
                ", isSend=" + isSend +
                '}';
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getBlockService() {
        return blockService;
    }

    public void setBlockService(String blockService) {
        this.blockService = blockService;
    }

    public String getHashType() {
        return hashType;
    }

    public void setHashType(String hashType) {
        this.hashType = hashType;
    }
}
