package io.bcaas.tools;

import io.bcaas.constants.Constants;

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

    public static String getKeyStoreFileName(String walletAddress) {
        return walletAddress + Constants.ValueMaps.FILE_STUFF;
    }
}
