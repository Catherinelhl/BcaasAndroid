package io.bcaas.tools;

import io.bcaas.constants.Constants;

/**
 * BcaasAndroid
 * <p>
 * io.bcaas.tools
 * <p>
 * created by catherine in 九月/04/2018/下午5:20
 * JSON 数据判断
 */
public class JsonTool {

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
