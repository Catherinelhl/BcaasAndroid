package io.bcaas.tools.language;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;

import java.util.Locale;

import io.bcaas.base.BCAASApplication;
import io.bcaas.constants.Constants;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.PreferenceTool;
import io.bcaas.tools.StringTool;


/**
 * 國際化語言管理
 */
public class LanguageTool {

    private static final String TAG = LanguageTool.class.getSimpleName();

    /**
     * 設置本地語言
     *
     * @param context
     * @return
     */
    public static Context setLocal(Context context) {
        return updateLanguage(context, getLanguageLocal(context));
    }

    /**
     * 切換語言
     *
     * @param locale 當前的語言環境
     */
    protected static Context updateLanguage(Context context, Locale locale) {
        // 1：获得res资源对象
        Resources resources = context.getResources();
        //2： 获得设置对象
        Configuration config = new Configuration(resources.getConfiguration());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
            context = context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            //获得屏幕参数：主要是分辨率，像素等
            resources.updateConfiguration(config, resources.getDisplayMetrics());

        }
        return context;
    }


    /**
     * 設置Application語言
     *
     * @param locale
     */
    public static void setApplicationLanguage(Context context, Locale locale) {
        // 1：获得res资源对象
        Resources resources = context.getApplicationContext().getResources();
        //2： 获得设置对象
        Configuration config = resources.getConfiguration();
        //3： 获得屏幕参数：主要是分辨率，像素等。
        DisplayMetrics dm = resources.getDisplayMetrics();
        BCAASApplication.setIsZH(locale == Locale.CHINA);
        config.locale = locale; // 简体中文
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            BCAASApplication.context().createConfigurationContext(config);
            Locale.setDefault(locale);
        }
        resources.updateConfiguration(config, dm);
    }


    /**
     * 配置改變的時候調用
     *
     * @param context
     */
    public static void onConfigurationChanged(Context context) {
        setLocal(context);
        setApplicationLanguage(context, getLanguageLocal(context));
    }

    /*獲取當前語言環境轉換成Local的形式*/
    public static Locale getLanguageLocal(Context context) {
        String currentString = getCurrentLanguageString(context);
        //3:匹配當前的語言獲取，返回APP裡面識別的TAG
        if (StringTool.equals(currentString, Constants.Language.CN)) {
            return Locale.CHINA;
        } else {
            return Locale.ENGLISH;

        }

    }

    /*獲取當前語言環境*/
    public static String getCurrentLanguageString(Context context) {
        // 1：檢查應用是否已經有用戶自己存儲的語言種類
        String currentString = PreferenceTool.getInstance(context).getString(Constants.Preference.LANGUAGE_TYPE);
        LogTool.d(TAG, "getLanguageLocal:" + currentString);
        if (StringTool.isEmpty(currentString)) {
            //2:當前的選中為空，那麼就默認讀取當前系統的語言環境
            Locale locale = Locale.getDefault();
            //locale.getLanguage();//zh  是中國
            currentString = locale.getCountry();//CN-簡體中文，TW、HK-繁體中文
        }
        //3:匹配當前的語言獲取，返回APP裡面識別的TAG
        if (StringTool.equals(currentString, Constants.Language.CN)) {
            return currentString;
        } else {
            return Constants.Language.EN;

        }

    }

}
