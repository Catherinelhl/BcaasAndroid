package io.bcaas.tools;

import android.text.TextUtils;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
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
}
