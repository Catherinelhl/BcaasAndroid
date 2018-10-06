package io.bcaas.tools;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import io.bcaas.base.BCAASApplication;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;

import static android.content.Context.UI_MODE_SERVICE;

/**
 * @projectName: BcaasAndroid
 * @packageName: io.bcaas.tools
 * @author: catherine
 * @time: 2018/9/12
 * <p>
 * 设备信息
 */
public class DeviceTool {
    private static String TAG = DeviceTool.class.getSimpleName();

    public static String getVersionName() {
        //硬件制造商（MANUFACTURER)
        String manufacturer = android.os.Build.MANUFACTURER;
        //品牌名称（BRAND）
        String brand = android.os.Build.BRAND;
        //主板名称（BOARD）
        String board = android.os.Build.BOARD;
        //设备名 （DEVICE）
        String device = android.os.Build.DEVICE;
        //型号（MODEL）:即用户可见的名称
        String model = android.os.Build.MODEL;
        //显示屏参数（DISPLAY）
        String display = android.os.Build.DISPLAY;
        //产品名称（PRODUCT）：即手机厂商
        String product = android.os.Build.PRODUCT;
        //设备唯一识别码（FINGERPRINT）
        String fingerPrint = android.os.Build.FINGERPRINT;
        LogTool.d(TAG, MessageConstants.MANUFACTURER + manufacturer);
        LogTool.d(TAG, MessageConstants.BRAND + brand);
        LogTool.d(TAG, MessageConstants.BOARD + board);
        LogTool.d(TAG, MessageConstants.DEVICE + device);
        LogTool.d(TAG, MessageConstants.MODEL + model);
        LogTool.d(TAG, MessageConstants.DISPLAY + display);
        LogTool.d(TAG, MessageConstants.PRODUCT + product);
        LogTool.d(TAG, MessageConstants.fingerprint + fingerPrint);
        return brand;
    }

    /*检查是否是TV*/
    public static boolean checkIsTV() {
        UiModeManager uiModeManager = (UiModeManager) BCAASApplication.context().getSystemService(UI_MODE_SERVICE);
        if (uiModeManager == null) {
            return false;
        }
        if (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) {
            LogTool.d(TAG, MessageConstants.TV_DEVICE);
            return true;
        } else {
            LogTool.d(TAG, MessageConstants.NON_TV_DEVICE);
            return false;
        }


    }

    /*获取当前移动设备的Ip信息*/
    public static String getIpAddress() {
        NetworkInfo info = ((ConnectivityManager) BCAASApplication.context()
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            // 3/4g网络
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface networkInterface = en.nextElement();
                        for (Enumeration<InetAddress> enumeration = networkInterface.getInetAddresses(); enumeration.hasMoreElements(); ) {
                            InetAddress inetAddress = enumeration.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                //  wifi网络
                WifiManager wifiManager = (WifiManager) BCAASApplication.context().getApplicationContext().getSystemService(BCAASApplication.context().WIFI_SERVICE);
                if (wifiManager == null) {
                    return getLocalIp();
                }
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());
                return ipAddress;
            } else if (info.getType() == ConnectivityManager.TYPE_ETHERNET) {
                // 有限网络
                return getLocalIp();
            }
        }
        return null;
    }

    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }


    // 获取有限网IP
    private static String getLocalIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> enumeration = networkInterface
                        .getInetAddresses(); enumeration.hasMoreElements(); ) {
                    InetAddress inetAddress = enumeration.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            LogTool.d(TAG, ex.getMessage());
        }
        return Constants.LOCAL_DEFAULT_IP;

    }

    /**
     * 判断当前是否是TV
     * <p>
     * 电视和手机的差异：
     * 屏幕物理尺寸不同。
     * 布局尺寸不同。
     * SIM 卡的状态不同。
     * 电源接入的方式不同。
     */
    //检查当前屏幕尺寸,小于6.5认为是手机，否则是电视
    private static boolean checkScreenIsPhone() {
        LogTool.d(TAG, MessageConstants.CHECKSIMSTATUSISTV);
        DisplayMetrics displayMetrics = BCAASApplication.getDisplayMetrics();
        if (displayMetrics != null) {
            double x = Math.pow(displayMetrics.widthPixels / displayMetrics.xdpi, 2);
            double y = Math.pow(displayMetrics.heightPixels / displayMetrics.ydpi, 2);
            LogTool.d(TAG, x);
            LogTool.d(TAG, y);
            //屏幕尺寸
            double screenInches = Math.sqrt(x + y);
            return screenInches < 6.5;
        }
        return false;
    }

    //检查布局文件是否是TV
    private static boolean checkLayoutIsPhone(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) <= Configuration.SCREENLAYOUT_SIZE_LARGE;

    }

    private static boolean checkSIMStatusIsPhone(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }

    //检查当前是否是TV
    public static boolean checkIsPhone(Context context) {
        return checkLayoutIsPhone(context) && checkSIMStatusIsPhone(context);
    }

    // 获取手机CPU信息
    public static String getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        String[] cpuInfo = {"", ""}; // 1-cpu型号 //2-cpu频率
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            cpuInfo[1] += arrayOfString[2];
            localBufferedReader.close();
        } catch (IOException e) {
        }
        //+ "2-cpu频率:" + cpuInfo[1]
        return MessageConstants.CPU_INFO + cpuInfo[0];
    }

    public static String getDevice(){
        return android.os.Build.MODEL;
    }
}
