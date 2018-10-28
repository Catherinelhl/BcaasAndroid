package io.bcaas.tools;

import io.bcaas.base.BCAASApplication;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;

/**
 * @projectName: BcaasAndroid
 * @packageName: io.bcaas.tools
 * @author: catherine
 * @time: 2018/9/11
 * <p>
 * 管理文件地址
 */
public class FilePathTool {

    //"/data/data/i0.bcaas/walletAddress.txt"

    /**
     * 根据传入的钱包地址返回keystore存储的值的地址
     *
     * @param walletAddress
     * @return
     */
    public static String getKeyStoreFileUrl(String walletAddress) {
        return Constants.ValueMaps.PACKAGE_URL + getKeyStoreFileName(walletAddress);
    }

    /**
     * KeyStore file 檔名改成
     * <p>
     * [format]
     * BlockService_WalletAddress_TimeStamp
     * <p>
     * [example]
     * BCC_13EyT3RdzaPoHUkUxeC9Ng572vuCVvoifF_1536308977392
     */
    public static String getKeyStoreFileName(String walletAddress) {
        try {
            return BCAASApplication.getBlockService() + "_"
                    + walletAddress + "_" + DateFormatTool.getUTCTimeStamp() + Constants.ValueMaps.FILE_STUFF;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return MessageConstants.Empty;
    }
}
