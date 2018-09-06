package io.bcaas.tools.ecc;


import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;

import java.io.Serializable;
import java.math.BigInteger;

import io.bcaas.base.BcaasApplication;
import io.bcaas.bean.WalletBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.encryption.AESTool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.vo.PublicUnitVO;

import static org.bitcoinj.core.Utils.HEX;


/**
 * 钱包
 *
 * @date 2018/06/25
 * 操作钱包的工具类
 */
public class Wallet implements Serializable {

    private static String TAG = Wallet.class.getSimpleName();

    private static final long serialVersionUID = 1L;

    public Wallet() {
        super();
    }

    public static WalletBean createWallet() {

        try {

            // 比特幣主網參數
            NetworkParameters mainNetParams = MainNetParams.get();
            // 取得私鑰WIF格式
            String privateKeyAsHex = new ECKey().getPrivateKeyAsHex();
            BigInteger privateKeyInt = new BigInteger(1, HEX.decode(privateKeyAsHex.toLowerCase()));
            // 未壓縮
            ECKey privateKey = ECKey.fromPrivate(privateKeyInt, false);

            WalletBean walletBean = new WalletBean();
            walletBean.setPrivateKey(privateKey.getPrivateKeyAsWiF(mainNetParams));
            // 公鑰(長度130)
            walletBean.setPublicKey(privateKey.getPublicKeyAsHex());
            // 產生地址
            walletBean.setAddress(privateKey.toAddress(mainNetParams).toBase58());

            return walletBean;

        } catch (Exception e) {
            LogTool.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        return null;

    }

    public static WalletBean createWallet(String privateKeyAsWiFStr) {

        try {

            if (KeyTool.validateBitcoinPrivateKeyWIFStr(privateKeyAsWiFStr)) {
                // 比特幣主網參數
                NetworkParameters mainNetParams = MainNetParams.get();
                // 私鑰WIF格式字串取得ECKey
                ECKey privateKey = DumpedPrivateKey.fromBase58(mainNetParams, privateKeyAsWiFStr).getKey();

                WalletBean wallet = new WalletBean();
                wallet.setPrivateKey(privateKey.getPrivateKeyAsWiF(mainNetParams));
                // 公鑰(長度130)
                wallet.setPublicKey(privateKey.getPublicKeyAsHex());
                // 產生地址
                wallet.setAddress(privateKey.toAddress(mainNetParams).toBase58());

                return wallet;
            }

        } catch (Exception e) {
            LogTool.d(TAG, "Use PrivateKey WIFStr Create Exception " + e);
        }

        return null;

    }

//---------------------Android insert start---------------------------

    /* 自动创建钱包信息*/
    public static WalletBean getWalletInfo() {
        return getWalletInfo("");
    }

    /*通过WIF格式的私钥来创建钱包*/
    public static WalletBean getWalletInfo(String privateKeyWIFStr) {
        if (StringTool.isEmpty(privateKeyWIFStr)) {
            return Wallet.createWallet();
        } else {
            return Wallet.createWallet(privateKeyWIFStr);

        }

    }

    //通过默认的方式来获取钱包地址
    public static String getWalletAddress() {
        return getWalletInfo("").getAddress();
    }

    //通过WIF格式的私钥来获取钱包地址信息
    public static String getWalletAddress(String privateKeyWIFStr) {
        return getWalletInfo(privateKeyWIFStr).getAddress();
    }

    /**
     * 解析来自数据库的keystore文件
     *
     * @param keystore
     */
    public static WalletBean parseKeystoreFromDB(String keystore) {
        WalletBean walletBean = null;
        try {
            String json = AESTool.decodeCBC_128(keystore, BcaasApplication.getStringFromSP(Constants.Preference.PASSWORD));
            if (StringTool.isEmpty(json)) {
                LogTool.d(TAG, MessageConstants.KEYSTORE_IS_NULL);
            } else {
                walletBean = GsonTool.convert(json, WalletBean.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return walletBean;
    }

    /**
     * 设置默认的BlockService
     *
     * @return
     */
    public static PublicUnitVO getDefaultBlockService() {
        PublicUnitVO publicUnitVO = new PublicUnitVO();
        publicUnitVO.setBlockService(Constants.BlockService.BCC);
        publicUnitVO.setStartup(Constants.BlockService.OPEN);
        return publicUnitVO;
    }

    //---------------------Android insert end---------------------------

}
