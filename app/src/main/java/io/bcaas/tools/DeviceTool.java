package io.bcaas.tools;

import android.app.ActivityManager;
import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
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

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.UI_MODE_SERVICE;

/**
 * @projectName: BcaasAndroid
 * @packageName: io.bcaas.tools
 * @author: catherine
 * @time: 2018/9/12
 * <p>
 * 工具類：设备信息
 */
public class DeviceTool {
    private static String TAG = DeviceTool.class.getSimpleName();

    public static void getMemoryInfo(String tag) {
        ActivityManager manager = (ActivityManager) BCAASApplication.context().getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(mi);

        Formatter.formatFileSize(BCAASApplication.context(), mi.availMem);// 将获取的内存大小规格化
        LogTool.i(TAG, "[" + tag + "]\t 可用内存：" + Formatter.formatFileSize(BCAASApplication.context(), mi.availMem)
                + ";\t总内存:" + Formatter.formatFileSize(BCAASApplication.context(), mi.totalMem)
                + ";\t阀值：" + Formatter.formatFileSize(BCAASApplication.context(), mi.threshold));
    }

    /**
     * 获取设备的内存信息
     *
     * @return
     */
    public static String getDeviceMemoryInfo() {
        ActivityManager manager = (ActivityManager) BCAASApplication.context().getSystemService(ACTIVITY_SERVICE);
        //获取Android设备限定的一个应用程序占用的内存限制;
        int memoryClass = manager.getMemoryClass();

        //获取运行期间，内存的使用情况
        Runtime runtime = Runtime.getRuntime();
        //当前程序在当前时间，整个分配的内存空间，这个空间可以增加，有虚拟机自己来处理
        long totalMemory = runtime.totalMemory();
        long maxMemory = runtime.maxMemory();
        //当前程序在当前时间，虚拟机分配的内存中可用的内存空间尺寸
        long freeMemory = runtime.freeMemory();

        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(mi);

        Formatter.formatFileSize(BCAASApplication.context(), mi.availMem);// 将获取的内存大小规格化
        //  + ";\r 可用内存：" + mi.availMem
        //  (1024 * 1024)
//        "TotalMemory:" + Formatter.formatFileSize(BCAASApplication.context(), totalMemory)
//                + ";\tFreeMemory:" + Formatter.formatFileSize(BCAASApplication.context(), freeMemory)
//                + ";\tMaxMemory:" + Formatter.formatFileSize(BCAASApplication.context(), maxMemory)
//                + ";\tlargerMemory:" + manager.getLargeMemoryClass()
//                +
//        + ";\t 是否低内存：" + mi.lowMemory;//当前可用内存
        //   + ";\t内存:" + memoryClass
        return "\t 可用内存：" + Formatter.formatFileSize(BCAASApplication.context(), mi.availMem)
                + ";\t总内存:" + Formatter.formatFileSize(BCAASApplication.context(), mi.totalMem)
                + ";\t阀值：" + Formatter.formatFileSize(BCAASApplication.context(), mi.threshold);

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
            LogTool.e(TAG, e.getMessage());
        }
        //+ "2-cpu频率:" + cpuInfo[1]
        return MessageConstants.CPU_INFO + cpuInfo[0];
    }


    public static String getTotalRam() {//GB
        String path = "/proc/meminfo";
        String firstLine = null;
        int totalRam = 0;
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader br = new BufferedReader(fileReader, 8192);
            firstLine = br.readLine().split("\\s+")[1];
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (firstLine != null) {
            totalRam = (int) Math.ceil((new Float(Float.valueOf(firstLine) / (1024 * 1024)).doubleValue()));
        }

        return totalRam + "GB";//返回1GB/2GB/3GB/4GB
    }

    public static String getDeviceOSInfo() {
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
        LogTool.d(TAG, MessageConstants.MANUFACTURER + manufacturer,
                MessageConstants.BRAND + brand,
                MessageConstants.BOARD + board,
                MessageConstants.DEVICE + device,
                MessageConstants.MODEL + model,
                MessageConstants.DISPLAY + display,
                MessageConstants.PRODUCT + product,
                MessageConstants.fingerprint + fingerPrint);
        return brand;
    }

    /**
     * 检查当前是否是TV
     *
     * @return
     */
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

    /**
     * 获取当前移动设备的Ip信息
     *
     * @return
     */
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


    /**
     * 获取有限网IP
     *
     * @return
     */
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

    /**
     * 通过检查布局是否是手机
     *
     * @param context
     * @return
     */
    private static boolean checkLayoutIsPhone(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) <= Configuration.SCREENLAYOUT_SIZE_LARGE;

    }

    /**
     * 检查SIM信息来比对是否是TV
     *
     * @param context
     * @return
     */
    private static boolean checkSIMStatusIsPhone(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }

    /**
     * 检查当前是否是手机
     *
     * @param context
     * @return
     */
    public static boolean checkIsPhone(Context context) {
        return checkLayoutIsPhone(context) && checkSIMStatusIsPhone(context);
    }

    /**
     * 得到当前设备的model
     *
     * @return
     */
    public static String getDeviceModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 应用区的顶端位置即状态栏的高度
     * *注意*
     * 该方法不能在初始化的时候用
     */
    public static void getStatusTop() {
        int height = 0;
        int resourceId = BCAASApplication.context().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = BCAASApplication.context().getResources().getDimensionPixelSize(resourceId);
        }
        LogTool.d(TAG, "getStatusTop" + height);

    }
}
