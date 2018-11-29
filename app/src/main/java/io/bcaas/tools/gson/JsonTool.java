package io.bcaas.tools.gson;

import org.json.JSONException;
import org.json.JSONObject;

import io.bcaas.base.BCAASApplication;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.gson.RequestJson;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.StringTool;
import io.bcaas.vo.PaginationVO;
import io.bcaas.vo.RemoteInfoVO;
import io.bcaas.vo.WalletVO;

/**
 * BcaasAndroid
 * <p>
 * io.bcaasc.tools
 * <p>
 * created by catherine in 九月/04/2018/下午5:20
 * 工具類：JSON 数据判断
 */
public class JsonTool {

    private static String TAG = JsonTool.class.getSimpleName();

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


    /**
     * 获取需要请求的数据
     * "{
     * walletVO:{        accessToken : String,
     * blockService : String,
     * walletAddress : String
     * }
     * }"
     */
    public static RequestJson getRequestJson() {
        String walletAddress = BCAASApplication.getWalletAddress();
        String accessToken = BCAASApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN);
        String blockService = BCAASApplication.getBlockService();
        if (StringTool.isEmpty(walletAddress)
                || StringTool.isEmpty(accessToken)
                || StringTool.isEmpty(blockService)) {
            return null;
        }
        WalletVO walletVO = new WalletVO(walletAddress, blockService
                , accessToken);
        RequestJson requestJson = new RequestJson(walletVO);
        return requestJson;

    }

    /**
     * 获取需要请求的数据
     *
     * @return "{
     * <p>
     * "walletVO":
     * {
     * "walletAddress": String 錢包地址
     * },
     * "remoteInfoVO":
     * {
     * "realIP": String 外網IP
     * }
     * *}"
     */
    public static RequestJson getRequestJsonWithRealIp() {
        String walletAddress = BCAASApplication.getWalletAddress();
        String accessToken = BCAASApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN);
        String blockService = BCAASApplication.getBlockService();
        if (StringTool.isEmpty(walletAddress)
                || StringTool.isEmpty(accessToken)
                || StringTool.isEmpty(blockService)) {
            return null;
        }
        WalletVO walletVO = new WalletVO(walletAddress, blockService
                , accessToken);
        RequestJson requestJson = new RequestJson(walletVO);
        requestJson.setRemoteInfoVO(new RemoteInfoVO(BCAASApplication.getWalletExternalIp()));
        LogTool.d(TAG, "requestJson:" + requestJson);
        return requestJson;

    }

    /**
     * 获取未签章区块的请求数据
     *
     * @return
     */
    public static RequestJson getWalletWaitingToReceiveBlockRequestJson() {
        RequestJson requestJson = getRequestJson();
        if (requestJson == null) {
            LogTool.i(TAG, MessageConstants.DATA_ERROR);
            return null;
        }
        PaginationVO paginationVO = new PaginationVO(BCAASApplication.getNextObjectId());
        requestJson.setPaginationVO(paginationVO);
        LogTool.i(TAG, GsonTool.string(requestJson));
        return requestJson;

    }

    /**
     * 交易记录是否已经存在
     *
     * @param code
     * @return
     */
    public static boolean isTransactionAlreadyExists(int code) {
        return code == MessageConstants.CODE_2028;
    }

    /**
     * token失效
     *
     * @param code
     * @return
     */
    public static boolean isTokenInvalid(int code) {
        return code == MessageConstants.CODE_3006
                || code == MessageConstants.CODE_3008;
    }

    /**
     * 公钥不匹配
     *
     * @param code
     * @return
     */
    public static boolean isPublicKeyNotMatch(int code) {
        return code == MessageConstants.CODE_2006;
    }
}
