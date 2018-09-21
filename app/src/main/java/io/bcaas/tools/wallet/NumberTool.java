package io.bcaas.tools.wallet;

import android.annotation.SuppressLint;

import java.text.DecimalFormat;

import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.tools.StringTool;

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
            String localBalance = BcaasApplication.getWalletBalance();
            if (StringTool.isEmpty(localBalance)) {
                return localBalance;
            }
        }
        double d = Double.parseDouble(balance);
        @SuppressLint("DefaultLocale") String result = String.format("%.6f", d);
        return result;
    }


    /**
     * 格式化数字为千分位显示,且保留6位精度
     * <p>
     * 1、井号(#)表示一位数字，逗号是用于分组分隔符的占位符，点是小数点的占位符。
     * 2、如果小数点的右面，值有三位，但是式样只有两位。format方法通过四舍五入处理。
     * <p>
     * 3、0 - 如果对应位置上没有数字，则用零代替
     * 4、# - 如果对应位置上没有数字，则保持原样（不用补）；如果最前、后为0，则保持为空。
     * 5、正负数模板用分号（;）分割
     *
     * @return
     */
    public static String formatNumber(String text) {
        if (StringTool.isEmpty(text)) {
            String localBalance = BcaasApplication.getWalletBalance();
            if (StringTool.isEmpty(localBalance)) {
                return localBalance;
            }
        }
        DecimalFormat df = null;
        if (text.indexOf(".") > 0) {
            if (text.length() - text.indexOf(".") - 1 == 0) {
                df = new DecimalFormat("###,##0.000000");
            } else if (text.length() - text.indexOf(".") - 1 == 1) {
                df = new DecimalFormat("###,##0.000000");
            } else {
                df = new DecimalFormat("###,##0.000000");
            }
        } else {
            df = new DecimalFormat("###,##0.000000");
        }
        double number = 0.0;
        try {
            number = Double.parseDouble(text);
        } catch (Exception e) {
            number = 0.000000;
        }
        return df.format(number);
    }

}
