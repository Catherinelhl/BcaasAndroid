package io.bcaas.tools.gson;

import org.json.JSONException;
import org.json.JSONObject;

import io.bcaas.constants.Constants;
import io.bcaas.tools.StringTool;

/**
 * BcaasAndroid
 * <p>
 * io.bcaas.tools
 * <p>
 * created by catherine in 九月/04/2018/下午5:20
 * JSON 数据判断
 */
public class JsonTool {

    public static String getString(String resource, String key) {
        return getString(resource, key, (String) null);
    }

    public static String getString(String resource, String key, String value) {
        if (StringTool.isEmpty(resource)) {
            return value;
        } else if (StringTool.isEmpty(key)) {
            return value;
        } else {
            JSONObject jsonObject = null;

            try {
                jsonObject = new JSONObject(resource);
                return !jsonObject.has(key) ? value : jsonObject.getString(key);
            } catch (JSONException var5) {
                return value;
            }
        }
    }


    /*是否是Open区块*/
    public static boolean isOpenBlock(String json) {
        return jsonIsBlockOf(json, Constants.BLOCK_TYPE_OPEN);
    }

    /*是否是Change区块*/
    public static boolean isChangeBlock(String json) {
        return jsonIsBlockOf(json, Constants.BLOCK_TYPE_CHANGE);
    }

    /*是否是Receive区块*/
    public static boolean isReceiveBlock(String json) {
        return jsonIsBlockOf(json, Constants.BLOCK_TYPE_RECEIVE);

    }

    /*是否是Send区块*/
    public static boolean isSendBlock(String json) {
        return jsonIsBlockOf(json, Constants.BLOCK_TYPE_SEND);
    }

    private static boolean jsonIsBlockOf(String json, String type) {
        if (StringTool.isEmpty(json)) {
            return false;
        }
        return json.contains(Constants.BLOCK_TYPE + type + Constants.BLOCK_TYPE_QUOTATION);

    }
}
