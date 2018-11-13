package io.bcaas.vo;

import java.io.Serializable;

/**
 * 請求服務器需要傳遞的參數：walletHeight
 * 用於當前Transaction block's height
 */

public class WalletHeightVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private int sendHeight;
    private int receiveHeight;

    public WalletHeightVO() {
        super();
    }

    public WalletHeightVO(int sendHeight, int receiveHeight) {
        super();
        this.sendHeight = sendHeight;
        this.receiveHeight = receiveHeight;
    }

    public int getSendHeight() {
        return sendHeight;
    }

    public void setSendHeight(int sendHeight) {
        this.sendHeight = sendHeight;
    }

    public int getReceiveHeight() {
        return receiveHeight;
    }

    public void setReceiveHeight(int receiveHeight) {
        this.receiveHeight = receiveHeight;
    }

}
