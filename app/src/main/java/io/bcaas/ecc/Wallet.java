package io.bcaas.ecc;


import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;

import java.io.Serializable;
import java.math.BigInteger;

import io.bcaas.constants.Constants;
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
    private String bitcoinPublicKeyStr;
    /**
     * 私鑰Bitcoin字串
     */
    private String bitcoinPrivateKeyWIFStr;
    /**
     * 錢包地址
     */
    private String bitcoinAddressStr;

    private Wallet() {
        super();
    }

    public Wallet(String bitcoinPublicKeyStr, String bitcoinPrivateKeyWIFStr, String bitcoinAddressStr) {
        super();
        this.bitcoinPublicKeyStr = bitcoinPublicKeyStr;
        this.bitcoinPrivateKeyWIFStr = bitcoinPrivateKeyWIFStr;
        this.bitcoinAddressStr = bitcoinAddressStr;
    }

    public static Wallet createWallet() {

        try {

            // 比特幣主網參數
            NetworkParameters mainNetParams = MainNetParams.get();
            // 取得私鑰WIF格式
            String privateKeyAsHex = new ECKey().getPrivateKeyAsHex();
            BigInteger privkey = new BigInteger(1, HEX.decode(privateKeyAsHex.toLowerCase()));
            // 未壓縮
            ECKey privateKey = ECKey.fromPrivate(privkey, false);

            Wallet wallet = new Wallet();
            wallet.setBitcoinPrivateKeyWIFStr(privateKey.getPrivateKeyAsWiF(mainNetParams));
            // 公鑰(長度130)
            wallet.setBitcoinPublicKeyStr(privateKey.getPublicKeyAsHex());
            // 產生地址
            wallet.setBitcoinAddressStr(privateKey.toAddress(mainNetParams).toBase58());

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
                wallet.setBitcoinPrivateKeyWIFStr(privateKey.getPrivateKeyAsWiF(mainNetParams));
                // 公鑰(長度130)
                wallet.setBitcoinPublicKeyStr(privateKey.getPublicKeyAsHex());
                // 產生地址
                wallet.setBitcoinAddressStr(privateKey.toAddress(mainNetParams).toBase58());

                return wallet;
            }

        } catch (Exception e) {
            BcaasLog.d(TAG, "Use PrivateKey WIFStr Create Exception "+ e);
        }

        return null;

    }

    public String getBitcoinPublicKeyStr() {
        return bitcoinPublicKeyStr;
    }

    public void setBitcoinPublicKeyStr(String bitcoinPublicKeyStr) {
        this.bitcoinPublicKeyStr = bitcoinPublicKeyStr;
    }

    public String getBitcoinPrivateKeyWIFStr() {
        return bitcoinPrivateKeyWIFStr;
    }

    public void setBitcoinPrivateKeyWIFStr(String bitcoinPrivateKeyWIFStr) {
        this.bitcoinPrivateKeyWIFStr = bitcoinPrivateKeyWIFStr;
    }

    public String getBitcoinAddressStr() {
        return bitcoinAddressStr;
    }

    public void setBitcoinAddressStr(String bitcoinAddressStr) {
        this.bitcoinAddressStr = bitcoinAddressStr;
    }
}
