package io.bcaas.event;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/24
 *
 * 更新当前的余额
 */
public class UpdateWalletBalance {

    private String walletBalance;
    public UpdateWalletBalance(String walletBalance){
        this.walletBalance=walletBalance;
    }

    public String getWalletBalance() {
        return walletBalance;
    }
}
