package io.bcaas.tools;

import android.text.TextUtils;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * 工具類：字符串判斷
 */
public class StringTool {

    public static boolean isEmpty(String content) {
        return TextUtils.isEmpty(content);
    }

    public static boolean notEmpty(String content) {
        return !isEmpty(content);
    }

    public static boolean equals(String str1, String str2) {
        return TextUtils.equals(str1, str2);
    }

    public static boolean contains(CharSequence seq, CharSequence searchSeq) {
        if (seq != null && searchSeq != null) {
            return TextUtils.indexOf(seq, searchSeq, 0) >= 0;
        } else {
            return false;
        }

    }

    /**
     * @param str
     * @return 去除不规则空格，平常空格，全角空格
     */
    public static String removeIllegalSpace(String str) {
        if (str == null) {
            return null;
        }
//        char nbsp = 0x00a0;//160
//        char qjnbsp = '\u3000';//12288
//        return str.replace(nbsp, ' ').replace(qjnbsp, ' ');

        char[] newt = str.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char w : newt) {
            int i = (int) w;
            if (i == 160 || i == 32 || i == 12288) {
                sb.append(" ");
            } else {
                sb.append(String.valueOf(w));
            }
        }
        return sb.toString();
    }
}
