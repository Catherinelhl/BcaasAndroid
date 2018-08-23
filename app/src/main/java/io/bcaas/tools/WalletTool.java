package io.bcaas.tools;


import io.bcaas.database.WalletInfo;
import io.bcaas.ecc.Wallet;
import io.bcaas.vo.WalletVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/20
 * <p>
 * <p>
 * 钱包信息的相关取得
 */
public class WalletTool {

    /* 自动创建钱包信息*/
    public static Wallet getWalletInfo() {
        return getWalletInfo("");
    }

    /*通过WIF格式的私钥来创建钱包*/
    public static Wallet getWalletInfo(String privateKeyWIFStr) {
        if (StringTool.isEmpty(privateKeyWIFStr)) {
            return Wallet.createWallet();
        } else {
            return Wallet.createWallet(privateKeyWIFStr);

        }

    }

    //通过默认的方式来获取钱包地址
    public static String getWalletAddress() {
        return getWalletInfo("").getBitcoinAddressStr();
    }

    //通过WIF格式的私钥来获取钱包地址信息
    public static String getWalletAddress(String privateKeyWIFStr) {
        return getWalletInfo(privateKeyWIFStr).getBitcoinAddressStr();
    }

}
