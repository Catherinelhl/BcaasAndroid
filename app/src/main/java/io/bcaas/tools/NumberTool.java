package io.bcaas.tools;

import android.annotation.SuppressLint;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/25
 *
 * 换算数字
 */
public class NumberTool {

    public static String getBalance(String balance) {
        if (StringTool.isEmpty(balance)) {
            return "0.000000";
        }
        double d = Double.parseDouble(balance);
        @SuppressLint("DefaultLocale") String result = String.format("%.6f", d);
        return result;
    }
}
