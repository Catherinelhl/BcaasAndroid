package io.bcaas.event;


import java.io.Serializable;

import io.bcaas.vo.WalletVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/20
 * 钱包创建成功，需要去登录
 */
public class ToLogin implements Serializable {

    private WalletVO walletVO;

    public ToLogin(WalletVO walletVO) {
        this.walletVO = walletVO;
    }

    public WalletVO getWalletVO() {
        return walletVO;
    }
}
