package io.bcaas.base;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Subscribe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.bcaas.bean.WalletBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.db.BcaasDBHelper;
import io.bcaas.event.NetStateChangeEvent;
import io.bcaas.http.HttpApi;
import io.bcaas.http.retrofit.RetrofitFactory;
import io.bcaas.receiver.NetStateReceiver;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.PreferenceTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.vo.ClientIpInfoVO;
import io.bcaas.vo.PublicUnitVO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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
    private static WalletBean walletBean;
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

    public static String getTcpIp() {
        return tcpIp;
    }

    public static void setTcpIp(String tcpIp) {
        BcaasApplication.tcpIp = tcpIp;
    }

    public static int getTcpPort() {
        return tcpPort;
    }

    public static int getHttpPort() {
        return httpPort;
    }

    public static void setHttpPort(int httpPort) {
        BcaasApplication.httpPort = httpPort;
    }

    public static void setTcpPort(int tcpPort) {
        BcaasApplication.tcpPort = tcpPort;
    }

    //Http需要连接的port
    public static int getRpcPort() {
        if (clientIpInfoVO == null) {
            return 0;
        }
        return clientIpInfoVO.getRpcPort();
    }

    //Http需要连接的port
    public static int getInternalRpcPort() {
        if (clientIpInfoVO == null) {
            return 0;
        }
        return clientIpInfoVO.getInternalRpcPort();
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
        if (StringTool.isEmpty(getTcpIp()) || getTcpPort() == 0) {
            return null;
        }
        return MessageConstants.REQUEST_HTTP + getTcpIp() + MessageConstants.REQUEST_COLON + getHttpPort();
    }

    //获取与AN连线的外网TCP请求地址
    public static String getANExternalTCPAddress() {
        if (clientIpInfoVO == null) {
            return "";

        }
        return MessageConstants.REQUEST_HTTP + getTcpIp() + MessageConstants.REQUEST_COLON + getTcpPort();
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
        walletBean = new WalletBean();
        preferenceTool = PreferenceTool.getInstance(context());
        getScreenMeasure();
        createDB();
        registerNetStateReceiver();

    }

    /**
     * 创建存储当前钱包「Keystore」的数据库
     */
    private static void createDB() {
        LogTool.d(TAG, MessageConstants.CREATEDB);
        bcaasDBHelper = new BcaasDBHelper(BcaasApplication.context());

    }

    /*注册网络变化的监听*/
    private void registerNetStateReceiver() {
        NetStateReceiver netStateReceiver = new NetStateReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(netStateReceiver, intentFilter);
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
        BcaasApplication.walletBean = walletBean;
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
        if (StringTool.isEmpty(blockService)) {
            return Constants.BLOCKSERVICE_BCC;
        }
        return blockService;
    }

    public static void setBlockService(String blockService) {
        BcaasApplication.blockService = blockService;
    }

    public static boolean isKeepHttpRequest() {
        return keepHttpRequest;
    }

    public static void setKeepHttpRequest(boolean keepHttpRequest) {
        BcaasApplication.keepHttpRequest = keepHttpRequest;
    }

    /*检测当前网络是否是真的*/
    public static boolean isRealNet() {
        LogTool.d(TAG, realNet);
        if (!realNet) {
//            requestNetState();
//            setRealNet(true);
        }

        return realNet;
    }

    @Subscribe
    public void netChanged(NetStateChangeEvent stateChangeEvent) {
        if (stateChangeEvent.isConnect()) {
//            requestNetState();
            setRealNet(true);
        } else {
            setRealNet(false);
        }
    }

    /*执行「检查当前网络」网络请求*/
    private static void requestNetState() {
        HttpApi httpApi = RetrofitFactory.pingInstance().create(HttpApi.class);
        Call<String> call = httpApi.ping();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                LogTool.d(TAG, response.body());
                if (StringTool.contains(response.body(), Constants.ValueMaps.PONG)) {
                    setRealNet(true);
                } else {
                    setRealNet(false);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                setRealNet(false);

            }
        });
    }

//    public static void pingNet() {
//        LogTool.d(TAG, "pingNet");
//        try {
//            if (InetAddress.getByName("120.25.236.134").isReachable(3000)) {
//                LogTool.d(TAG, "pingNet onSuccess");
//            } else {
//                LogTool.d(TAG, "pingNet onFailure");
//            }
//        } catch (Throwable e) {
//            LogTool.d(TAG, "pingNet onFailure");
//        }
//    }


    private static boolean ping() {
        LogTool.d(TAG, "ping");
        String result = null;
        try {
            String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址1次
            // 读取ping的内容，可以不加
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            LogTool.d(TAG, "------ping-----result content : " + stringBuffer.toString());
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
            LogTool.d(TAG, "----result---", "result = " + result);
        }
        return false;
    }

    public static void setRealNet(boolean realNet) {
        BcaasApplication.realNet = realNet;
    }

}
