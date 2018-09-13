package io.bcaas.tools;

import com.google.gson.Gson;

import io.bcaas.BuildConfig;
import io.bcaas.base.BcaasApplication;
import io.bcaas.bean.WalletBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.tools.encryption.AESTool;
import io.bcaas.tools.gson.GsonTool;

/**
 * @projectName: BcaasAndroid
 * @packageName: io.bcaas.tools
 * @author: catherine
 * @time: 2018/9/11
 * <p>
 * 钱包数据库操作
 */
public class WalletDBTool {
    private static String TAG = WalletDBTool.class.getSimpleName();

    /**
     * 将当前钱包存储到数据库
     *
     * @param walletBean
     */
    public static void insertWalletInDB(WalletBean walletBean) {
        String keyStore = null;
        if (walletBean != null) {
            Gson gson = new Gson();
            try {
                //1:对当前的钱包信息进行加密；AES加密钱包字符串，以密码作为向量
                keyStore = AESTool.encodeCBC_128(gson.toJson(walletBean), BcaasApplication.getStringFromSP(Constants.Preference.PASSWORD));
                LogTool.d(TAG, "step 1:encode keystore:" + keyStore);
            } catch (Exception e) {
                LogTool.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        }
        //2：得到当前不为空的keystore，进行数据库操作
        if (StringTool.isEmpty(keyStore)) {
            LogTool.d(TAG, MessageConstants.KEYSTORE_IS_NULL);
            return;
        }
        //3：查询当前数据库是否已经存在旧数据,如果没有就插入，否者进行条件查询更新操作，保持数据库数据只有一条
        if (StringTool.isEmpty(queryKeyStore())) {
            LogTool.d(TAG, MessageConstants.INSERT_KEY_STORE);
            BcaasApplication.bcaasDBHelper.insertKeyStore(keyStore);
        } else {
            LogTool.d(TAG, MessageConstants.UPDATE_KEY_STORE);
            BcaasApplication.bcaasDBHelper.updateKeyStore(keyStore);
        }

    }

    //--------------------------数据库操作---start-----------------------------------------


    /**
     * 删除当前数据库「debug」
     */
    public static void clearWalletTable() {
        if (BuildConfig.DEBUG) {
            BcaasApplication.bcaasDBHelper.clearKeystore();
        }
    }

    /**
     * 查询当前数据库得到存储的Keystore
     *
     * @return
     */
    public static String queryKeyStore() {
        String keystore = BcaasApplication.bcaasDBHelper.queryKeyStore();
        LogTool.d(TAG, "step 2:query keystore:" + keystore);
        if (StringTool.isEmpty(keystore)) {
            return null;
        }
        return keystore;
    }


    /*查询当前数据库中钱包keystore是否已经有数据了*/
    public static boolean existKeystoreInDB() {
        return BcaasApplication.bcaasDBHelper.queryIsExistKeyStore();
    }

    /**
     * 解析来自数据库的keystore文件
     *
     * @param keystore
     */
    public static WalletBean parseKeystore(String keystore) {
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
        LogTool.d(TAG, MessageConstants.WALLET_INFO + walletBean);
        return walletBean;
    }

}
