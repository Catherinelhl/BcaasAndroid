package io.bcaas.base;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;
import android.view.WindowManager;


import com.google.gson.Gson;

import org.greenrobot.greendao.database.Database;

import io.bcaas.BuildConfig;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.database.AddressDao;
import io.bcaas.database.DaoMaster;
import io.bcaas.database.DaoSession;
import io.bcaas.database.WalletInfo;
import io.bcaas.database.WalletInfoDao;
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
    private static String TAG = "BcaasApplication";
    private static BcaasApplication instance;
    protected static int screenWidth;
    protected static int screenHeight;
    private static WalletInfo walletInfo;
    private static ClientIpInfoVO clientIpInfoVO;
    private static PreferenceTool preferenceTool;
    private static String transactionAmount;//存储当前需要交易的金额
    private static String destinationWallet;//存储当前需要交易的地址信息
    protected static WalletInfoDao walletInfoDao;//钱包信息数据库
    protected static AddressDao addressDao;//地址管理数据库
    protected static BcaasDBHelper bcaasDBHelpr;// 得到数据管理库

    public static String getPublicKey() {
        if (preferenceTool == null) {
            preferenceTool = PreferenceTool.getInstance(context());
        }
        return preferenceTool.getString(Constants.Preference.PUBLIC_KEY);
    }

    public static void setPublicKey(String publicKey) {
        if (preferenceTool == null) {
            preferenceTool = PreferenceTool.getInstance(context());
        }
        preferenceTool.saveString(Constants.Preference.PUBLIC_KEY, publicKey);
    }

    public static String getPrivateKey() {
        if (preferenceTool == null) {
            preferenceTool = PreferenceTool.getInstance(context());
        }
        return preferenceTool.getString(Constants.Preference.PRIVATE_KEY);
    }

    public static void setPrivateKey(String privateKey) {
        if (preferenceTool == null) {
            preferenceTool = PreferenceTool.getInstance(context());
        }
        preferenceTool.saveString(Constants.Preference.PRIVATE_KEY, privateKey);
    }

    public static void setPassword(String password) {
        if (preferenceTool == null) {
            preferenceTool = PreferenceTool.getInstance(context());
        }
        preferenceTool.saveString(Constants.Preference.PASSWORD, password);
    }

    public static String getPassword() {
        if (preferenceTool == null) {
            preferenceTool = PreferenceTool.getInstance(context());
        }
        return preferenceTool.getString(Constants.Preference.PASSWORD);
    }

    public static void setAccessToken(String accessToken) {
        if (preferenceTool == null) {
            preferenceTool = PreferenceTool.getInstance(context());
        }
        BcaasLog.d(TAG, "Token :" + accessToken);
        preferenceTool.saveString(Constants.Preference.ACCESS_TOKEN, accessToken);
    }

    public static String getAccessToken() {
        if (preferenceTool == null) {
            preferenceTool = PreferenceTool.getInstance(context());
        }
        return preferenceTool.getString(Constants.Preference.ACCESS_TOKEN);
    }

    public static String getBlockService() {
        if (preferenceTool == null) {
            preferenceTool = PreferenceTool.getInstance(context());
        }
        return preferenceTool.getString(Constants.Preference.BLOCK_SERVICE);
    }

    public static void setBlockService(String blockSerivce) {
        if (preferenceTool == null) {
            preferenceTool = PreferenceTool.getInstance(context());
        }
        preferenceTool.saveString(Constants.Preference.BLOCK_SERVICE, blockSerivce);
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
            // TODO: 2018/8/21 如果内网Ip为空，是否有个默认的
//            return "192.168.31.5";
            return "";
        }
        return clientIpInfoVO.getExternalIp();
    }

    public static String getInternalIp() {
        if (clientIpInfoVO == null) {
            // TODO: 2018/8/21 如果内网Ip为空，是否有个默认的
//            return "192.168.31.5";
            return "";
        }
        return clientIpInfoVO.getInternalIp();
    }

    //Http需要连接的port
    public static int getRpcPort() {
        if (clientIpInfoVO == null) {
            // TODO: 2018/8/21 如果内网端口为空，是否有个默认的
//            return 57463;
            return 0;
        }
        return clientIpInfoVO.getRpcPort();
    }

    //TCP连接需要的port
    public static int getExternalPort() {
        if (clientIpInfoVO == null) {
            // TODO: 2018/8/21 如果内网端口为空，是否有个默认的
//            return 57463;
            return 0;
        }
        return clientIpInfoVO.getExternalPort();
    }

    //TCP连接需要的port
    public static int getInternalPort() {
        if (clientIpInfoVO == null) {
            // TODO: 2018/8/21 如果内网端口为空，是否有个默认的
//            return 57463;
            return 0;
        }
        return clientIpInfoVO.getInternalPort();
    }

    //获取与AN连线的Http请求
    public static String getANHttpAddress() {
        if (clientIpInfoVO == null) {
            return "";
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
        walletInfo = new WalletInfo();
        preferenceTool = PreferenceTool.getInstance(context());
        getScreenMeasure();
        initDB();
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

    //数据库=================
    /* A flag to show how easily you can switch from standard SQLite to the encrypted SQLCipher. */
    public static final boolean ENCRYPTED = false;
    private static DaoSession daoSession;

    /*初始化数据库*/
    private void initDB() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? "notes-db-encrypted" : "notes-db");
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    private void initDaoData() {
        DaoSession session = getDaoSession();
        walletInfoDao = session.getWalletInfoDao();
        addressDao = session.getAddressDao();
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }

    public static void insertWalletInfoInDB(WalletInfo walletInfo) {
        BcaasLog.d("插入数据：", walletInfo);
        DaoSession session = getDaoSession();
        WalletInfoDao walletDao = session.getWalletInfoDao();
        if (walletDao != null) {
            walletDao.deleteAll();
            walletDao.insert(walletInfo);
        }
    }
    //数据库================

    public static String getWalletAddress() {
        if (walletInfo == null) {
            return null;
        } else {
            return walletInfo.getBitcoinAddressStr();

        }
    }

    public static WalletInfo getWalletInfo() {
        if (walletInfo == null) {
            return new WalletInfo();
        }
        return walletInfo;
    }

    public static void setWalletInfo(WalletInfo walletInfo) {
        BcaasApplication.walletInfo = walletInfo;
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
    public static void insertKeyStoreInDB(Wallet wallet) {
        String keyStore = null;
        if (wallet != null) {
            Gson gson = new Gson();
            try {
                //1:对当前的钱包信息进行加密；AES加密钱包字符串，以密码作为向量
                keyStore = AES.encodeCBC_128(gson.toJson(wallet), BcaasApplication.getPassword());
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
            BcaasLog.d(TAG, "step 3:insertKeystore");
            bcaasDBHelpr.insertKeystore(keyStore);
        } else {
            BcaasLog.d(TAG, "step 3:updateKeystore");
            bcaasDBHelpr.updateKeystore(keyStore);
        }

    }

    /**
     * 删除当前数据库「debug」
     */
    public static void deleteWalletDB() {
        if (BuildConfig.DEBUG) {
            //        bcaasDBHelpr.deleteDB();
        }
    }

    /**
     * 查询当前数据库是否有旧数据
     *
     * @return
     */
    public static String queryKeyStore() {
        String keystore = bcaasDBHelpr.queryKeystoreFromDB();
        BcaasLog.d(TAG, "step 2:query keystore:" + keystore);
        if (StringTool.isEmpty(keystore)) {
            return null;
        }
        parseKeystoreFromDB(keystore);
        return keystore;
    }

    /**
     * 解析来自数据库的keystore文件
     *
     * @param keystore
     */
    private static void parseKeystoreFromDB(String keystore) {
        try {
            String json = AES.decodeCBC_128(keystore, BcaasApplication.getPassword());
            if (StringTool.isEmpty(json)) {
                BcaasLog.d(TAG, MessageConstants.KEYSTORE_IS_NULL);
            }
            Wallet wallet = new Gson().fromJson(json, Wallet.class);
            BcaasLog.d(TAG, wallet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建存储当前钱包「Keystore」的数据库
     */
    private static void createDB() {
        BcaasLog.d(TAG, "createDB");
        bcaasDBHelpr = new BcaasDBHelper(BcaasApplication.context());

    }

    /*查询当前数据库中钱包keystore是否已经有数据了*/
    public static boolean queryIsExistKeystoreInDB() {
        return bcaasDBHelpr.queryIsExistKeyStore();
    }
    //--------------数据库操作---end--------------------------------------
}
