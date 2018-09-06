package io.bcaas.base;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;
import android.view.WindowManager;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import io.bcaas.BuildConfig;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.db.BcaasDBHelper;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.ecc.Wallet;
import io.bcaas.tools.encryption.AESTool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.tools.PreferenceTool;
import io.bcaas.tools.StringTool;
import io.bcaas.vo.ClientIpInfoVO;
import io.bcaas.vo.PublicUnitVO;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 */
public class BcaasApplication extends MultiDexApplication {
    private static String TAG = BcaasApplication.class.getSimpleName();
    private static BcaasApplication instance;
    /*屏幕的寬*/
    protected static int screenWidth;
    /*屏幕的高*/
    protected static int screenHeight;
    /*当前登錄的钱包信息*/
    private static Wallet wallet;
    /*當前AN信息*/
    private static ClientIpInfoVO clientIpInfoVO;
    /*SP存儲工具類*/
    private static PreferenceTool preferenceTool;
    /*当前需要交易的金额*/
    private static String transactionAmount;
    /*当前需要交易的地址信息*/
    private static String destinationWallet;
    /*数据管理库*/
    public static BcaasDBHelper bcaasDBHelper;
    /*当前授权的账户代表*/
    private static String representative;
    /*当前账户的余额*/
    private static String walletBalance;
    /**
     * 從SP裡面獲取數據
     *
     * @param key
     * @return
     */
    public static String getStringFromSP(String key) {
        if (preferenceTool == null) {
            preferenceTool = PreferenceTool.getInstance(context());
        }
        return preferenceTool.getString(key);
    }

    /**
     * 往SP裡面存儲數據
     *
     * @param key
     * @param value
     */
    public static void setStringToSP(String key, String value) {
        if (preferenceTool == null) {
            preferenceTool = PreferenceTool.getInstance(context());
        }
        preferenceTool.saveString(key, value);
    }

    //-------------------------------获取AN相关的参数 start---------------------------

    /*得到新的AN信息*/
    public static void setClientIpInfoVO(ClientIpInfoVO clientIpInfo) {
        setStringToSP(Constants.Preference.CLIENT_IP_INFO, GsonTool.string(clientIpInfo));
        BcaasApplication.clientIpInfoVO = clientIpInfo;
    }

    public static ClientIpInfoVO getClientIpInfoVO() {
        return GsonTool.convert(getStringFromSP(Constants.Preference.CLIENT_IP_INFO), ClientIpInfoVO.class);

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
        if (StringTool.isEmpty(getExternalIp()) || getRpcPort() == 0) {
            return null;
        }
        return MessageConstants.REQUEST_HTTP + getExternalIp() + MessageConstants.REQUEST_COLON + getRpcPort();
    }

    //获取与AN连线的TCP请求地址
    public static String getANTCPAddress() {
        if (clientIpInfoVO == null) {
            return "";

        }
        return MessageConstants.REQUEST_HTTP + getExternalIp() + MessageConstants.REQUEST_COLON + getExternalPort();
    }

    public static void setRepresentative(String representative) {
        BcaasApplication.representative = representative;
    }

    public static String getRepresentative() {
        return representative;
    }

    public static String getWalletBalance() {
        return walletBalance;

    }

    public static void setWalletBalance(String walletBalance) {
        BcaasApplication.walletBalance = walletBalance;
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

    public static int getScreenWidth() {
        return screenWidth;
    }

    public static int getScreenHeight() {
        return screenHeight;
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
        LogTool.d(TAG, wallet);
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
                keyStore = AESTool.encodeCBC_128(gson.toJson(wallet), BcaasApplication.getStringFromSP(Constants.Preference.PASSWORD));
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
            LogTool.d(TAG, "step 3:insertKeyStore");
            bcaasDBHelper.insertKeyStore(keyStore);
        } else {
            LogTool.d(TAG, "step 3:updateKeyStore");
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
     * 查询当前数据库得到存储的Keystore
     *
     * @return
     */
    public static String queryKeyStore() {
        String keystore = bcaasDBHelper.queryKeyStore();
        LogTool.d(TAG, "step 2:query keystore:" + keystore);
        if (StringTool.isEmpty(keystore)) {
            return null;
        }
        return keystore;
    }

    /**
     * 创建存储当前钱包「Keystore」的数据库
     */
    private static void createDB() {
        LogTool.d(TAG, "createDB");
        bcaasDBHelper = new BcaasDBHelper(BcaasApplication.context());

    }

    /*查询当前数据库中钱包keystore是否已经有数据了*/
    public static boolean existKeystoreInDB() {
        return bcaasDBHelper.queryIsExistKeyStore();
    }

    //--------------数据库操作---end--------------------------------------

    /**
     * 获取blockService
     *
     * @return
     */
    public static List<PublicUnitVO> getPublicUnitVO() {
        List<PublicUnitVO> publicUnitVOS = new ArrayList<>();
        String blockServiceStr = BcaasApplication.getStringFromSP(Constants.Preference.BLOCK_SERVICE_LIST);
        if (StringTool.notEmpty(blockServiceStr)) {
            publicUnitVOS = GsonTool.convert(blockServiceStr, new TypeToken<List<PublicUnitVO>>() {
            }.getType());
        }
        return publicUnitVOS;
    }

    public static String getBlockService() {
        String blockService = getStringFromSP(Constants.Preference.BLOCK_SERVICE);
        if (StringTool.isEmpty(blockService)) {
            return Constants.BLOCKSERVICE_BCC;
        }
        return blockService;
    }
}
