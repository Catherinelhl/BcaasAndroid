package io.bcaas.tools;

import android.annotation.SuppressLint;

import io.bcaas.base.BcaasApplication;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/25
 * <p>
 * 换算数字
 */
public class NumberTool {

    public static String getBalance() {
        return getBalance(BcaasApplication.getWalletBalance());
    }

    public static String getBalance(String balance) {
        if (StringTool.isEmpty(balance)) {
            String localBalance = BcaasApplication.getWalletBalance();
            if (StringTool.isEmpty(localBalance)) {
                return "读取中";
            }
        }
        double d = Double.parseDouble(balance);
        @SuppressLint("DefaultLocale") String result = String.format("%.6f", d);
        return result;
    }
}
