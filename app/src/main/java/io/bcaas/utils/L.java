package io.bcaas.utils;

import android.util.Log;


import io.bcaas.constants.Constants;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/20
 * 显示日志
 */
public class L {
    private static final boolean DEBUG = true;

    private static final int D = 745;
    private static final int E = 421;
    private static final int V = 674;

    private static final L ourInstance = new L();

    public static L getInstance() {
        return ourInstance;
    }

    private L() {
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


    public static <T> void d(String tag, T values) {
        printf(D, tag, values.toString());
    }

    public static void d(String tag, String... values) {
        printf(D, tag, values);
    }

    public static void e(String tag, String... values) {
        printf(E, tag, values);
    }

    public static void v(String tag, String... values) {
        printf(V, tag, values);
    }

    private static void printf(int mark, String tag, String... values) {
        if (!DEBUG) {
            return;
        }

        //需要打印的内容
        StringBuffer value = new StringBuffer();
        for (int i = 0; i < values.length; i++) {
            value.append(values[i]);
            if (i == values.length - 1) {
                break;
            }
            value.append(", ");
        }

        // 打印
        switch (mark) {
            case D:
                printfLine(D, tag, value.toString());
                break;
            case E:
                printfLine(E, tag, value.toString());
                break;
            case V:
                printfLine(V, tag, value.toString());
                break;
        }


    }

    private static String getPosition(String tag) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement element = getTargetStack(tag);

        if (null == element) {
            return null;
        }

//        sb.append(".")// 我电脑的AndroidStudio有点问题，必须在这加个点，在logcat中才能定位。Androidstudio升级后，这个问题不存在了。
        sb.append("(")
                .append(element.getFileName())
                .append(":")
                .append(element.getLineNumber())
                .append(")");
        return sb.toString();
    }

    private static void printfLine(int mark, String tag, String msg) {
        String startLine = getPosition(tag);
        switch (mark) {
            case D:
                Log.d(tag, " ");
                Log.d(startLine, msg);
                break;
            case E:
                Log.e(tag, " ");
                Log.e(startLine, msg);
                break;
            case V:
                Log.v(tag, " ");
                Log.v(startLine, msg);
                break;
        }


    }

    /**
     * 获取最后调用我们log的StackTraceElement
     *
     * @param tag 目标类的SimpleName
     * @return
     */

    private static StackTraceElement getTargetStack(String tag) {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().contains(tag)) {
                //返回调用位置的 element
                return element;
            }

        }

        return null;
    }

}
