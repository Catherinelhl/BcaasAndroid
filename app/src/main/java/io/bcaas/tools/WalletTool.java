package io.bcaas.tools;


import com.google.gson.Gson;

import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.tools.ecc.Wallet;
import io.bcaas.tools.encryption.AESTool;
import io.bcaas.vo.PublicUnitVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/20
 * <p>
 * <p>
 * 钱包信息的相关取得
 */
public class WalletTool {
    private static String TAG = WalletTool.class.getSimpleName();

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
    public static Wallet parseKeystoreFromDB(String keystore) {
        Wallet wallet = null;
        try {
            String json = AESTool.decodeCBC_128(keystore, BcaasApplication.getStringFromSP(Constants.Preference.PASSWORD));
            if (StringTool.isEmpty(json)) {
                LogTool.d(TAG, MessageConstants.KEYSTORE_IS_NULL);
            } else {
                wallet = new Gson().fromJson(json, Wallet.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wallet;
    }

    /**
     * 设置默认的BlockService
     * @return
     */
    public static PublicUnitVO getDefaultBlockService() {
        PublicUnitVO publicUnitVO = new PublicUnitVO();
        publicUnitVO.setBlockService(Constants.BlockService.BCC);
        publicUnitVO.setStartup(Constants.BlockService.OPEN);
        return publicUnitVO;
    }
}
