package io.bcaas.base;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;
import android.view.WindowManager;


import org.greenrobot.greendao.database.Database;

import io.bcaas.database.DaoMaster;
import io.bcaas.database.DaoSession;
import io.bcaas.database.WalletInfo;
import io.bcaas.utils.L;
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

    /*得到新的AN信息*/
    public static void setClientIpInfoVO(ClientIpInfoVO clientIpInfo) {
        BcaasApplication.clientIpInfoVO = clientIpInfo;
        L.d(TAG, BcaasApplication.clientIpInfoVO);
    }

    public static ClientIpInfoVO getClientIpInfoVO() {
        if (clientIpInfoVO == null) {
            return new ClientIpInfoVO();
        }
        return clientIpInfoVO;
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

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        walletInfo = new WalletInfo();
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
    private DaoSession daoSession;

    /*初始化数据库*/
    private void initDB() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? "notes-db-encrypted" : "notes-db");
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
    //数据库================

    public static String getAccessToken() {
        if (walletInfo == null) {
            return null;
        } else {
            return walletInfo.getAccessToken();

        }
    }

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
}
