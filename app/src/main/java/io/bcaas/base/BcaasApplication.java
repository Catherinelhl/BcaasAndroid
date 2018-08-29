package io.bcaas.base;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;
import android.view.WindowManager;


import com.google.gson.Gson;

import io.bcaas.BuildConfig;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.db.BcaasDBHelper;
import io.bcaas.ecc.Wallet;
import io.bcaas.encryption.AES;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.GsonTool;
import io.bcaas.tools.PreferenceTool;
import io.bcaas.tools.StringTool;
import io.bcaas.vo.ClientIpInfoVO;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 */
public class BcaasApplication extends MultiDexApplication {
    private static String TAG = BcaasApplication.class.getSimpleName();
    private static BcaasApplication instance;
    protected static int screenWidth;
    protected static int screenHeight;
    private static Wallet wallet;//得到当前的钱包
    private static ClientIpInfoVO clientIpInfoVO;
    private static PreferenceTool preferenceTool;
    private static String transactionAmount;//存储当前需要交易的金额
    private static String destinationWallet;//存储当前需要交易的地址信息
    public static BcaasDBHelper bcaasDBHelper;// 得到数据管理库

    public static String getPublicKeyFromSP() {
        if (preferenceTool == null) {
            preferenceTool = PreferenceTool.getInstance(context());
        }
        return preferenceTool.getString(Constants.Preference.PUBLIC_KEY);
    }

    public static void setPublicKeyToSP(String publicKey) {
        if (preferenceTool == null) {
            preferenceTool = PreferenceTool.getInstance(context());
        }
        preferenceTool.saveString(Constants.Preference.PUBLIC_KEY, publicKey);
    }

    public static String getPrivateKeyFromSP() {
        if (preferenceTool == null) {
            preferenceTool = PreferenceTool.getInstance(context());
        }
        return preferenceTool.getString(Constants.Preference.PRIVATE_KEY);
    }

    public static void setPrivateKeyToSP(String privateKey) {
        if (preferenceTool == null) {
            preferenceTool = PreferenceTool.getInstance(context());
        }
        preferenceTool.saveString(Constants.Preference.PRIVATE_KEY, privateKey);
    }

    public static void setPasswordToSP(String password) {
        if (preferenceTool == null) {
            preferenceTool = PreferenceTool.getInstance(context());
        }
        preferenceTool.saveString(Constants.Preference.PASSWORD, password);
    }

    public static String getPasswordFromSP() {
        if (preferenceTool == null) {
            preferenceTool = PreferenceTool.getInstance(context());
        }
        return preferenceTool.getString(Constants.Preference.PASSWORD);
    }

    public static void setAccessTokenToSP(String accessToken) {
        if (preferenceTool == null) {
            preferenceTool = PreferenceTool.getInstance(context());
        }
        preferenceTool.saveString(Constants.Preference.ACCESS_TOKEN, accessToken);
    }

    public static String getAccessTokenFromSP() {
        if (preferenceTool == null) {
            preferenceTool = PreferenceTool.getInstance(context());
        }
        return preferenceTool.getString(Constants.Preference.ACCESS_TOKEN);
    }

    public static String getBlockServiceFromSP() {
        if (preferenceTool == null) {
            preferenceTool = PreferenceTool.getInstance(context());
        }
        return preferenceTool.getString(Constants.Preference.BLOCK_SERVICE);
    }

    public static void setBlockServiceToSP(String blockService) {
        if (preferenceTool == null) {
            preferenceTool = PreferenceTool.getInstance(context());
        }
        preferenceTool.saveString(Constants.Preference.BLOCK_SERVICE, blockService);
    }


    //-------------------------------获取AN相关的参数 start---------------------------

    /*得到新的AN信息*/
    public static void setClientIpInfoVO(ClientIpInfoVO clientIpInfo) {
        if (preferenceTool == null) {
            preferenceTool = PreferenceTool.getInstance(context());
        }
        preferenceTool.saveString(Constants.Preference.CLIENT_IP_INFO, GsonTool.encodeToString(clientIpInfo));
        BcaasApplication.clientIpInfoVO = clientIpInfo;
    }

    public static ClientIpInfoVO getClientIpInfoVO() {
        if (preferenceTool == null) {
            preferenceTool = PreferenceTool.getInstance(context());
        }
        return GsonTool.getGson().fromJson(preferenceTool.getString(Constants.Preference.CLIENT_IP_INFO), ClientIpInfoVO.class);

    }

    public static String getExternalIp() {
        if (clientIpInfoVO == null) {
            return "";
        }
        return clientIpInfoVO.getExternalIp();
    }

    public static String getInternalIp() {
        if (clientIpInfoVO == null) {
            return "";
        }
        return clientIpInfoVO.getInternalIp();
    }

    //Http需要连接的port
    public static int getRpcPort() {
        if (clientIpInfoVO == null) {
            return 0;
        }
        return clientIpInfoVO.getRpcPort();
    }

    //TCP连接需要的port
    public static int getExternalPort() {
        if (clientIpInfoVO == null) {
            return 0;
        }
        return clientIpInfoVO.getExternalPort();
    }

    //TCP连接需要的port
    public static int getInternalPort() {
        if (clientIpInfoVO == null) {
            return 0;
        }
        return clientIpInfoVO.getInternalPort();
    }

    //获取与AN连线的Http请求
    public static String getANHttpAddress() {
        return MessageConstants.REQUEST_HTTP + getExternalIp() + MessageConstants.REQUEST_COLON + getRpcPort();
    }

    //获取与AN连线的TCP请求地址
    public static String getANTCPAddress() {
        if (clientIpInfoVO == null) {
            return "";

        }
        return MessageConstants.REQUEST_HTTP + getExternalIp() + MessageConstants.REQUEST_COLON + getExternalPort();
    }

    //获取当前的余额
    public static String getWalletBalance() {
        if (preferenceTool == null) {
            preferenceTool = PreferenceTool.getInstance(context());
        }
        return preferenceTool.getString(Constants.Preference.WALLET_BALANCE);

    }

    //存储当前的余额
    public static void setWalletBalance(String walletBalance) {
        if (preferenceTool == null) {
            preferenceTool = PreferenceTool.getInstance(context());
        }
        preferenceTool.getString(Constants.Preference.WALLET_BALANCE, walletBalance);
    }

    //-------------------------------获取AN相关的参数 end---------------------------
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        wallet = new Wallet();
        preferenceTool = PreferenceTool.getInstance(context());
        getScreenMeasure();
        createDB();

    }


    /*得到当前屏幕的尺寸*/
    private void getScreenMeasure() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        assert windowManager != null;
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
    }

    public static Context context() {
        return instance.getApplicationContext();
    }

    public static String getWalletAddress() {
        if (wallet == null) {
            return null;
        } else {
            return wallet.getAddress();

        }
    }

    public static Wallet getWallet() {
        if (wallet == null) {
            return new Wallet();
        }
        return wallet;
    }

    public static void setWallet(Wallet wallet) {
        BcaasLog.d(TAG, wallet);
        BcaasApplication.wallet = wallet;
    }

    //存储当前的交易金额，可能方式不是很好，需要考虑今后换种方式传给send请求
    public static void setTransactionAmount(String transactionAmount) {
        BcaasApplication.transactionAmount = transactionAmount;

    }

    public static String getTransactionAmount() {
        return transactionAmount;
    }

    public static String getDestinationWallet() {
        return destinationWallet;
    }

    public static void setDestinationWallet(String destinationWallet) {
        BcaasApplication.destinationWallet = destinationWallet;
    }

    //清空当前Token信息
    public static void clearAccessToken() {
        if (preferenceTool == null) {
            preferenceTool = PreferenceTool.getInstance(context());
        }
        preferenceTool.clear(Constants.Preference.ACCESS_TOKEN);
    }
    //--------------------------数据库操作---start-----------------------------------------

    /**
     * 将当前钱包存储到数据库
     *
     * @param wallet
     */
    public static void insertWalletInDB(Wallet wallet) {
        String keyStore = null;
        if (wallet != null) {
            Gson gson = new Gson();
            try {
                //1:对当前的钱包信息进行加密；AES加密钱包字符串，以密码作为向量
                keyStore = AES.encodeCBC_128(gson.toJson(wallet), BcaasApplication.getPasswordFromSP());
                BcaasLog.d(TAG, "step 1:encode keystore:" + keyStore);
            } catch (Exception e) {
                BcaasLog.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        }
        //2：得到当前不为空的keystore，进行数据库操作
        if (StringTool.isEmpty(keyStore)) {
            BcaasLog.d(TAG, MessageConstants.KEYSTORE_IS_NULL);
            return;
        }
        //3：查询当前数据库是否已经存在旧数据,如果没有就插入，否者进行条件查询更新操作，保持数据库数据只有一条
        if (StringTool.isEmpty(queryKeyStore())) {
            BcaasLog.d(TAG, "step 3:insertKeyStore");
            bcaasDBHelper.insertKeyStore(keyStore);
        } else {
            BcaasLog.d(TAG, "step 3:updateKeyStore");
            bcaasDBHelper.updateKeyStore(keyStore);
        }

    }

    /**
     * 删除当前数据库「debug」
     */
    public static void clearWalletTable() {
        if (BuildConfig.DEBUG) {
            bcaasDBHelper.clearKeystore();
        }
    }

    /**
     * 查询当前数据库是否有旧数据
     *
     * @return
     */
    public static String queryKeyStore() {
        String keystore = bcaasDBHelper.queryKeyStore();
        BcaasLog.d(TAG, "step 2:query keystore:" + keystore);
        if (StringTool.isEmpty(keystore)) {
            return null;
        }
        return keystore;
    }

    /**
     * 创建存储当前钱包「Keystore」的数据库
     */
    private static void createDB() {
        BcaasLog.d(TAG, "createDB");
        bcaasDBHelper = new BcaasDBHelper(BcaasApplication.context());

    }

    /*查询当前数据库中钱包keystore是否已经有数据了*/
    public static boolean existKeystoreInDB() {
        return bcaasDBHelper.queryIsExistKeyStore();
    }

    //--------------数据库操作---end--------------------------------------
}
