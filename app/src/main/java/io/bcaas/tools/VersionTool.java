package io.bcaas.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import io.bcaas.base.BCAASApplication;
import io.bcaas.constants.Constants;

/**
 * @projectName: BcaasAndroid
 * @packageName: io.bcaas.tools
 * @author: catherine
 * @time: 2018/9/10
 * 工具類：获取当前APP版本信息
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

    /**
     * 比对当前的版本和服务器返回的名字是否一致，否则进行比较判断是否需要更新
     *
     * @param serverVersionName 服务器返回的版本名字
     */
    public static boolean needUpdate(String serverVersionName) {
        //当前的版本名字
        String currentVersionName = VersionTool.getVersionName(BCAASApplication.context());
        if (StringTool.isEmpty(currentVersionName)) {
            return false;
        }
        if (StringTool.isEmpty(serverVersionName)) {
            return false;
        }
        //1:解析当前本地的版本信息
        String[] localVersionSplit = currentVersionName.split(Constants.SYMBOL_DOT);
        //2:解析服务器传回的版本信息
        String[] serverVersionSplit = serverVersionName.split(Constants.SYMBOL_DOT);
        //3:比较两者是否相等，如果服务器的大于本地的，那么需要提示更新
        if (localVersionSplit.length < 3) {
            return false;
        }
        if (serverVersionSplit.length < 3) {
            return false;
        }
        if (Integer.valueOf(localVersionSplit[0]) < Integer.valueOf(serverVersionSplit[0])) {
            return true;
        }
        if (Integer.valueOf(localVersionSplit[1]) < Integer.valueOf(serverVersionSplit[1])) {
            return true;
        }
        if (Integer.valueOf(localVersionSplit[2]) < Integer.valueOf(serverVersionSplit[2])) {
            return true;
        }
        return false;

    }
}
