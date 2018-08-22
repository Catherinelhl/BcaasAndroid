package io.bcaas.bean;

import java.io.Serializable;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 待处理交易的数据类
 */
public class TransactionsBean implements Serializable {

    private String accountAddress;//账户地址
    private String balance;//余额
    private String currency;//币种
    private String status;//此笔交易的状态

    public TransactionsBean() {
        super();
    }

    public TransactionsBean(String accountAddress, String balance, String currency) {
        super();
        this.accountAddress = accountAddress;
        this.balance = balance;
        this.currency = currency;
    }

    public String getAccountAddress() {
        return accountAddress;
    }

    public void setAccountAddress(String accountAddress) {
        this.accountAddress = accountAddress;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TransactionsBean{" +
                "accountAddress='" + accountAddress + '\'' +
                ", balance='" + balance + '\'' +
                ", currency='" + currency + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
