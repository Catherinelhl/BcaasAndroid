package io.bcaas.bean;

import java.io.Serializable;

/**
 * io.bcaas.bean
 * <p>
 * created by catherine in 九月/06/2018/上午10:57
 * 本地「Wallet」組成屬性信息類
 */
public class WalletBean implements Serializable {

    /*公鑰BitCoin字串 */
    private String publicKey;
    /* 私鑰BitCoin字串*/
    private String privateKey;
    /* 錢包地址*/
    private String address;

    public WalletBean() {
        super();
    }

    public WalletBean(String publicKey, String privateKey, String address) {
        super();
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.address = address;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "WalletBean{" +
                "publicKey='" + publicKey + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
