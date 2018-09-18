package io.bcaas.tools.ecc;


import com.google.gson.reflect.TypeToken;

import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import io.bcaas.base.BcaasApplication;
import io.bcaas.bean.WalletBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.vo.PublicUnitVO;

import static org.bitcoinj.core.Utils.HEX;


/**
 * 钱包
 *
 * @date 2018/06/25
 * <p>
 * 创建钱包的配置
 */
public class WalletTool {

    private static String TAG = WalletTool.class.getSimpleName();

    public WalletTool() {
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
            LogTool.d(TAG, MessageConstants.WALLET_CREATE_EXCEPTION + e.getMessage());
        }

        return null;


    }

    /* 自动创建钱包信息*/
    public static WalletBean getWalletInfo() {
        return getWalletInfo("");
    }

    /*通过WIF格式的私钥来创建钱包*/
    public static WalletBean getWalletInfo(String privateKeyWIFStr) {
        if (StringTool.isEmpty(privateKeyWIFStr)) {
            return WalletTool.createWallet();
        } else {
            return WalletTool.createWallet(privateKeyWIFStr);

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
     * 获取blockService
     *
     * @return
     */
    public static List<PublicUnitVO> getPublicUnitVO() {
        List<PublicUnitVO> publicUnitVOS = new ArrayList<>();
        String blockServiceStr = BcaasApplication.getStringFromSP(Constants.Preference.BLOCK_SERVICE_LIST);
        //如果当前获取的数据列表为空，那么设置默认的币种信息
        if (StringTool.notEmpty(blockServiceStr)) {
            publicUnitVOS = GsonTool.convert(blockServiceStr, new TypeToken<List<PublicUnitVO>>() {
            }.getType());
        } else {
            //设置默认的BlockService
            PublicUnitVO publicUnitVO = new PublicUnitVO();
            publicUnitVO.setBlockService(Constants.BlockService.BCC);
            publicUnitVO.setStartup(Constants.BlockService.OPEN);
            publicUnitVOS.add(publicUnitVO);
        }
        return publicUnitVOS;
    }

    /**
     * 得到当前需要显示的币种
     *
     * @return
     */
    public static String getDisplayBlockService(List<PublicUnitVO> publicUnitVOS) {
        //1:设置默认币种
        String blockService = Constants.BlockService.BCC;
        if (ListTool.noEmpty(publicUnitVOS)) {
            //2:比对默认BCC的币种是否关闭，否则重新赋值
            String isStartUp = Constants.BlockService.CLOSE;
            for (PublicUnitVO publicUnitVO : publicUnitVOS) {
                if (StringTool.equals(blockService, publicUnitVO.getBlockService())) {
                    isStartUp = publicUnitVO.isStartup();
                    break;
                }
            }
            if (StringTool.equals(isStartUp, Constants.BlockService.OPEN)) {
                return blockService;
            } else {
                return publicUnitVOS.get(0).getBlockService();

            }
        } else {
            return blockService;
        }
    }


}
