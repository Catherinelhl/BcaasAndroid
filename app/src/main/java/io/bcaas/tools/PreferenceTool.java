package io.bcaas.tools;

import android.content.Context;
import android.content.SharedPreferences;

import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/23
 * <p>
 * 用于存储当前APP需要用的值
 */
public class PreferenceTool {
    private String TAG = PreferenceTool.class.getSimpleName();
    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;
    private static PreferenceTool instance = null;

    private PreferenceTool(Context context) {
        sp = context.getSharedPreferences(Constants.SP_NAME + BcaasApplication.getWalletAddress(), 0);
        editor = sp.edit();
    }

    public static synchronized PreferenceTool getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceTool(context);
        }

        return instance;
    }

    public String getString(String key) {
        return this.getString(key, "");
    }

    public String getString(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    public int getInt(String key) {
        return this.getInt(key, 0);
    }

    public int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    public boolean getBoolean(String key) {
        return this.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    public Long getLong(String key) {
        return this.getLong(key, 0L);
    }

    public Long getLong(String key, Long defValue) {
        return sp.getLong(key, defValue);
    }

    public Float getFloat(String key) {
        return this.getFloat(key, 0.0F);
    }

    public Float getFloat(String key, Float defValue) {
        return sp.getFloat(key, defValue);
    }

    public void saveString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void saveInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public void saveFloat(String key, float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    public void saveLong(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public void saveBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }

    public void clear(String key) {
        editor.remove(key);
        editor.commit();
    }
}
