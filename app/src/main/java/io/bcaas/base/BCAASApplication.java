package io.bcaas.base;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.squareup.otto.Subscribe;

import java.util.List;

import io.bcaas.bean.WalletBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.db.BcaasDBHelper;
import io.bcaas.event.NetStateChangeEvent;
import io.bcaas.receiver.NetStateReceiver;
import io.bcaas.tools.DeviceTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.PreferenceTool;
import io.bcaas.tools.ServerTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.language.LanguageTool;
import io.bcaas.vo.ClientIpInfoVO;
import io.bcaas.vo.PublicUnitVO;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 當前APP's Application,一些整個APP需要公用的常數、變量、SP相關的存儲統一在此類。也包括獲取當前設備的一些尺寸以及硬件信息
 */
public class BCAASApplication extends MultiDexApplication {
    private static String TAG = BCAASApplication.class.getSimpleName();
    private static BCAASApplication instance;
    /*屏幕的寬*/
    protected static int screenWidth;
    /*屏幕的高*/
    protected static int screenHeight;
    /*当前登錄的钱包信息*/
    private static WalletBean walletBean;
    /*當前AN信息*/
    private static ClientIpInfoVO clientIpInfoVO;
    /*当前需要交易的金额*/
    private static String transactionAmount;
    /*当前需要交易的地址信息*/
    private static String destinationWallet;
    /*数据管理库*/
    public static BcaasDBHelper bcaasDBHelper;
    /*当前账户的余额*/
    private static String walletBalance;
    /*当前账户的币种*/
    private static String blockService;
    /*监听当前程序是否保持继续网络请求*/
    private static boolean keepHttpRequest;
    /*判断当前程序是否真的有网*/
    private static boolean realNet = true;
    /*存储当前要访问的TCP ip & port*/
    private static String tcpIp;
    private static int tcpPort;
    private static int httpPort;
    /*当前设备的外网IP，由服务器返回*/
    private static String walletExternalIp;
    /*當前請求R區塊的分頁信息*/
    private static String nextObjectId;
    /*当前的语言环境,默认是英文*/
    private static boolean isZH;
    //存儲當前是否登錄，如果登錄，首頁「登錄」按鈕變為「登出」
    private static boolean isLogin;
    /*是否是手机版*/
    private static boolean isPhone;
    /*定义一个需要显示SANIP的变量*/
    private static boolean showSANIP;
    //是否可以登陆
    private static boolean canLogin = true;

    public static boolean isCanLogin() {
        return canLogin;
    }

    public static void setCanLogin(boolean canLogin) {
        BCAASApplication.canLogin = canLogin;
    }

    public static boolean isIsPhone() {
        return isPhone;
    }

    public static void setIsPhone(boolean isPhone) {
        BCAASApplication.isPhone = isPhone;
    }

    public static boolean isIsLogin() {
        return isLogin;
    }

    public static void setIsLogin(boolean isLogin) {
        BCAASApplication.isLogin = isLogin;
    }

    public static boolean isIsZH() {
        return isZH;
    }

    public static void setIsZH(boolean isZH) {
        BCAASApplication.isZH = isZH;
    }

    /*得到所有的币种*/
    private static List<PublicUnitVO> publicUnitVOList;

    public static String getNextObjectId() {
        if (StringTool.isEmpty(nextObjectId)
                || StringTool.equals(nextObjectId, MessageConstants.NEXT_PAGE_IS_EMPTY)) {
            //默認第一次穿空字符串
            return MessageConstants.Empty;
        }
        return nextObjectId;
    }

    public static void setNextObjectId(String nextObjectId) {
        BCAASApplication.nextObjectId = nextObjectId;
    }

    public static List<PublicUnitVO> getPublicUnitVOList() {
        return publicUnitVOList;
    }

    public static void setPublicUnitVOList(List<PublicUnitVO> publicUnitVOList) {
        BCAASApplication.publicUnitVOList = publicUnitVOList;
    }
    //-------------------------------获取AN相关的参数 start---------------------------

    /*得到新的AN信息*/
    public static void setClientIpInfoVO(ClientIpInfoVO clientIpInfo) {
        LogTool.d(TAG, MessageConstants.UPDATE_CLIENT_IP_INFO + clientIpInfo);
        BCAASApplication.clientIpInfoVO = clientIpInfo;
    }

    public static ClientIpInfoVO getClientIpInfoVO() {
        return clientIpInfoVO;

    }

    public static String getTcpIp() {
        return tcpIp;
    }

    public static void setTcpIp(String tcpIp) {
        BCAASApplication.tcpIp = tcpIp;
    }

    public static int getTcpPort() {
        return tcpPort;
    }

    public static int getHttpPort() {
        return httpPort;
    }

    public static void setHttpPort(int httpPort) {
        BCAASApplication.httpPort = httpPort;
    }

    public static void setTcpPort(int tcpPort) {
        BCAASApplication.tcpPort = tcpPort;
    }

    //获取与AN连线的Http请求
    public static String getANHttpAddress() {
        if (StringTool.isEmpty(getTcpIp()) || getTcpPort() == 0) {
            return null;
        }
        return Constants.SPLICE_CONVERTER(getTcpIp(), getHttpPort());
    }

    public static String getWalletBalance() {
        return walletBalance;

    }

    public static void setWalletBalance(String walletBalance) {
        BCAASApplication.walletBalance = walletBalance;
    }

    /* 重置当前余额*/
    public static void resetWalletBalance() {
        BCAASApplication.walletBalance = MessageConstants.Empty;
    }

    public static String getWalletExternalIp() {
        return walletExternalIp;
    }

    public static void setWalletExternalIp(String walletExternalIp) {
        BCAASApplication.walletExternalIp = walletExternalIp;
    }

    /**
     * 返回当前的token是否为空
     *
     * @return
     */
    public static boolean tokenIsNull() {
        String accessToken = PreferenceTool.getInstance().getString(Constants.Preference.ACCESS_TOKEN);
        return StringTool.isEmpty(accessToken);
    }

    public static boolean showSANIP() {
        return showSANIP;
    }

    public static void setShowSANIP(boolean showSANIP) {
        BCAASApplication.showSANIP = showSANIP;
    }

    //-------------------------------获取AN相关的参数 end---------------------------
    @Override

    public void onCreate() {
        super.onCreate();
        instance = this;
        walletBean = new WalletBean();
        getScreenMeasure();
        createDB();
        registerNetStateReceiver();
        ServerTool.initServerData();
    }

    /**
     * 创建存储当前钱包「Keystore」的数据库
     */
    private static void createDB() {
        LogTool.d(TAG, MessageConstants.CREATE_DB);
        bcaasDBHelper = new BcaasDBHelper(BCAASApplication.context());

    }

    /*注册网络变化的监听*/
    private void registerNetStateReceiver() {
        NetStateReceiver netStateReceiver = new NetStateReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(netStateReceiver, intentFilter);
    }


    /*得到当前屏幕的尺寸*/
    private void getScreenMeasure() {
        DisplayMetrics displayMetrics = getDisplayMetrics();
        if (displayMetrics != null) {
            screenWidth = displayMetrics.widthPixels;
            screenHeight = displayMetrics.heightPixels;
            // 屏幕密度（1.0 / 1.5 / 2.0）
            float density = displayMetrics.density;
            // 屏幕密度DPI（160 / 240 / 320）
            int densityDpi = displayMetrics.densityDpi;
            String info = " 设备型号: " + android.os.Build.MODEL
                    + ",\nSDK版本:" + android.os.Build.VERSION.SDK
                    + ",\n系统版本:" + android.os.Build.VERSION.RELEASE + "\n "
                    + MessageConstants.SCREEN_WIDTH + screenWidth
                    + "\n " + MessageConstants.SCREEN_HEIGHT + screenHeight
                    + "\n屏幕密度:  " + density
                    + "\n屏幕密度DPI: " + densityDpi;
            LogTool.d(TAG, MessageConstants.DEVICE_INFO + info);
        }
        LogTool.d(TAG, DeviceTool.getCpuInfo());
        DeviceTool.checkIsTV();
        setIsPhone(DeviceTool.checkIsPhone(context()));
    }

    public static DisplayMetrics getDisplayMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context().getSystemService(WINDOW_SERVICE);
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics;
        } else {
            return null;
        }
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
        if (walletBean == null) {
            return null;
        } else {
            return walletBean.getAddress();

        }
    }

    public static WalletBean getWalletBean() {
        if (walletBean == null) {
            return new WalletBean();
        }
        return walletBean;
    }

    public static void setWalletBean(WalletBean walletBean) {
        LogTool.d(TAG, walletBean);
        BCAASApplication.walletBean = walletBean;
    }

    //存储当前的交易金额，可能方式不是很好，需要考虑今后换种方式传给send请求
    public static void setTransactionAmount(String transactionAmount) {
        BCAASApplication.transactionAmount = transactionAmount;

    }

    public static String getTransactionAmount() {
        return transactionAmount;
    }

    public static String getDestinationWallet() {
        return destinationWallet;
    }

    public static void setDestinationWallet(String destinationWallet) {
        BCAASApplication.destinationWallet = destinationWallet;
    }

    public static String getBlockService() {
        return blockService;
    }

    public static void setBlockService(String blockService) {
        BCAASApplication.blockService = blockService;
    }

    public static boolean isKeepHttpRequest() {
        return keepHttpRequest;
    }

    public static void setKeepHttpRequest(boolean keepHttpRequest) {
        BCAASApplication.keepHttpRequest = keepHttpRequest;
    }

    /*检测当前网络是否是真的*/
    public static boolean isRealNet() {
        return realNet;
    }

    @Subscribe
    public void netChanged(NetStateChangeEvent stateChangeEvent) {
        if (stateChangeEvent.isConnect()) {
            setRealNet(true);
        } else {
            setRealNet(false);
        }
    }

    public static void setRealNet(boolean realNet) {
        BCAASApplication.realNet = realNet;
    }

    @Override
    protected void attachBaseContext(Context base) {
        //保存系统选择语言
//        LanguageTool.saveSystemCurrentLanguage(base);
        super.attachBaseContext(LanguageTool.setLocal(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //保存系统选择语言
        LanguageTool.onConfigurationChanged(getApplicationContext());
    }


}
