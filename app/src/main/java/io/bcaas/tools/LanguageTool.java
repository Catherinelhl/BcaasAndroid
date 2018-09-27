package io.bcaas.tools;

import java.util.Locale;

import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/27
 * <p>
 * 「语言切换」辅助类
 */
public class LanguageTool {
    private static String TAG = LanguageTool.class.getSimpleName();

    /*獲取當前語言環境*/
    public static String getCurrentLanguage() {
        // 1：檢查應用是否已經有用戶自己存儲的語言種類
        String currentString = BcaasApplication.getStringFromSP(Constants.Preference.LANGUAGE_TYPE);
        LogTool.d(TAG, currentString);
        if (StringTool.isEmpty(currentString)) {
            //2:當前的選中為空，那麼就默認讀取當前系統的語言環境
            Locale locale = BcaasApplication.context().getResources().getConfiguration().locale;
            //locale.getLanguage();//zh  是中國
            currentString = locale.getCountry();//CN-簡體中文，TW、HK-繁體中文
        }
        //3:匹配當前的語言獲取，返回APP裡面識別的TAG
        if (StringTool.equals(currentString, Constants.ValueMaps.CN)) {
            return currentString;
        } else {
            return Constants.ValueMaps.EN;

        }
    }
}
