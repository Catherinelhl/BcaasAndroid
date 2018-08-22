package io.bcaas.utils;

import android.text.TextUtils;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 */
public class StringU {

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
