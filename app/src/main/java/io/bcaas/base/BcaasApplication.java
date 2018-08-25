package io.bcaas.base;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;
import android.view.WindowManager;


import org.greenrobot.greendao.database.Database;

import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.database.DaoMaster;
import io.bcaas.database.DaoSession;
import io.bcaas.database.WalletInfo;
import io.bcaas.database.WalletInfoDao;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.GsonTool;
import io.bcaas.tools.PreferenceTool;
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
            return "1KxM6id36DxSf6UmKQq9Js4Tky8F3dy2Ck";
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
}
