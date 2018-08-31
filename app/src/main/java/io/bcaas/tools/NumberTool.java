package io.bcaas.tools;

import android.annotation.SuppressLint;

import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/25
 * <p>
 * 换算数字
 */
public class NumberTool {
    /**
     * 获取当前余额，如果当前余额为null，需要请求余额，否则进行显示
     *
     * @param balance
     * @return
     */
    public static String getBalance(String balance) {
        if (StringTool.isEmpty(balance)) {
            String localBalance = BcaasApplication.getStringFromSP(Constants.Preference.WALLET_BALANCE);
            if (StringTool.isEmpty(localBalance)) {
                return localBalance;
            }
        }
        double d = Double.parseDouble(balance);
        @SuppressLint("DefaultLocale") String result = String.format("%.6f", d);
        return result;
    }
}
