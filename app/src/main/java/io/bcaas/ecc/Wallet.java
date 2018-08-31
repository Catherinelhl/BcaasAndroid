package io.bcaas.ecc;


import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;

import java.io.Serializable;
import java.math.BigInteger;

import io.bcaas.tools.BcaasLog;

import static org.bitcoinj.core.Utils.HEX;


/**
 * 钱包
 *
 * @date 2018/06/25
 */
public class Wallet implements Serializable {

    private static String TAG = "Wallet";

    private static final long serialVersionUID = 1L;
    /**
     * 公鑰Bitcoin字串
     */
    private String publicKey;
    /**
     * 私鑰Bitcoin字串
     */
    private String privateKey;
    /**
     * 錢包地址
     */
    private String address;

    public Wallet() {
        super();
    }

    public Wallet(String publicKey, String privateKey, String address) {
        super();
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.address = address;
    }

    public static Wallet createWallet() {

        try {

            // 比特幣主網參數
            NetworkParameters mainNetParams = MainNetParams.get();
            // 取得私鑰WIF格式
            String privateKeyAsHex = new ECKey().getPrivateKeyAsHex();
            BigInteger privateKeyInt = new BigInteger(1, HEX.decode(privateKeyAsHex.toLowerCase()));
            // 未壓縮
            ECKey privateKey = ECKey.fromPrivate(privateKeyInt, false);

            Wallet wallet = new Wallet();
            wallet.setPrivateKey(privateKey.getPrivateKeyAsWiF(mainNetParams));
            // 公鑰(長度130)
            wallet.setPublicKey(privateKey.getPublicKeyAsHex());
            // 產生地址
            wallet.setAddress(privateKey.toAddress(mainNetParams).toBase58());

            return wallet;

        } catch (Exception e) {
            BcaasLog.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        return null;

    }

    public static Wallet createWallet(String privateKeyAsWiFStr) {

        try {

            if (KeyTool.validateBitcoinPrivateKeyWIFStr(privateKeyAsWiFStr)) {
                // 比特幣主網參數
                NetworkParameters mainNetParams = MainNetParams.get();
                // 私鑰WIF格式字串取得ECKey
                ECKey privateKey = DumpedPrivateKey.fromBase58(mainNetParams, privateKeyAsWiFStr).getKey();

                Wallet wallet = new Wallet();
                wallet.setPrivateKey(privateKey.getPrivateKeyAsWiF(mainNetParams));
                // 公鑰(長度130)
                wallet.setPublicKey(privateKey.getPublicKeyAsHex());
                // 產生地址
                wallet.setAddress(privateKey.toAddress(mainNetParams).toBase58());

                return wallet;
            }

        } catch (Exception e) {
            BcaasLog.d(TAG, "Use PrivateKey WIFStr Create Exception " + e);
        }

        return null;

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
        return "Wallet{" +
                "publicKey='" + publicKey + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
