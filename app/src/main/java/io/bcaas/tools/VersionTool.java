package io.bcaas.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * @projectName: BcaasAndroid
 * @packageName: io.bcaas.tools
 * @author: catherine
 * @time: 2018/9/10
 * 获取当前版本
 */
public class VersionTool {

    /**
     * 获取当前的版本信息
     *
     * @param context
     * @return
     */
    public static String getVersionInfo(Context context) {
        PackageInfo info = getPackageInfo(context);
        return info != null ? info.versionName + "( " + info.versionCode + " )" : null;
    }

    /**
     * 获取当前的版本code
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        int versionCode = 0;
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo != null) {
            versionCode = packageInfo.versionCode;
        }
        return versionCode;
    }

    /**
     * 得到包信息
     *
     * @param context
     * @return
     */
    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 16384);
        } catch (PackageManager.NameNotFoundException var3) {
        }

        return packageInfo;
    }

    /**
     * 获取版本名字
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        PackageInfo info = getPackageInfo(context);
        return info != null ? info.versionName : null;
    }
}
