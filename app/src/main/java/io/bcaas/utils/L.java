package io.bcaas.utils;

import android.util.Log;


import io.bcaas.constants.Constants;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/20
 * 显示日志
 */
public class L {
    private static final L ourInstance = new L();

    public static L getInstance() {
        return ourInstance;
    }

    private L() {
    }

    public static <T> void d(String tag, T info) {
        Log.d(Constants.KeyMaps.TAG, tag + "==>" + info.toString());
    }

    public static <T> void d(T info) {
        Log.d(Constants.KeyMaps.TAG, info.toString());
    }

    public static <T> void e(T info) {
        Log.e(Constants.KeyMaps.TAG, info.toString());
    }

    public static <T> void i(T info) {
        Log.i(Constants.KeyMaps.TAG, info.toString());
    }

    public static <T> void line(T info) {
        Log.i(Constants.KeyMaps.TAG, "======" + info.toString());
    }
}
