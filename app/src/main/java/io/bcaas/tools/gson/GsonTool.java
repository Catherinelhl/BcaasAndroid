package io.bcaas.tools.gson;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

import io.bcaas.tools.StringTool;
import io.bcaas.tools.encryption.AESTool;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.gson.jsonTypeAdapter.RequestJsonTypeAdapter;
import io.bcaas.gson.jsonTypeAdapter.ResponseJsonTypeAdapter;
import okhttp3.MediaType;
import okhttp3.RequestBody;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 * 工具類：Gson格式管理
 */
public class GsonTool {

    public GsonTool() {
    }

    /*将对象转换为String*/
    public static String string(Object o) {
        Gson gson = getGson();
        return gson.toJson(o);
    }

    /*通过传入的key得到相应的数组数据*/
    public static <T> T getListByKey(String resource, String key, Type type) {
        Gson gson = getGson();
        String value = JsonTool.getString(resource, key);
        return !StringTool.isEmpty(value) && !StringTool.equals("[]", value.replace(" ", "")) ? gson.fromJson(value, type) : null;
    }

    /*通过传入的key得到相应的数据*/
    public static <T> T getBeanByKey(String resource, String key, Type type) {
        Gson gson = getGson();
        String value = JsonTool.getString(resource, key);
        return StringTool.isEmpty(value) ? null : gson.fromJson(value, type);
    }

    /*解析数据是object的情况*/
    public static <T> T convert(String str, Class<T> cls) throws JsonSyntaxException {
        Gson gson = getGson();
        return gson.fromJson(str, cls);
    }

    public static <T> T convert(String str, Type type) throws JsonSyntaxException {
        Gson gson = getGson();
        return gson.fromJson(str, type);
    }

    /*   encryption request bean*/
    public static <T> String AESJsonBean(T jsonBean) {
        if (jsonBean == null) {
            throw new NullPointerException("AESJsonBean jsonBean is null");
        }
        String json = GsonTool.string(jsonBean);
        // encryption
        String encodeJson = null;
        try {
            encodeJson = AESTool.encodeCBC_128(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodeJson;
    }

    public static RequestBody stringToRequestBody(String str) {
        if (StringTool.isEmpty(str)) return null;
        return RequestBody.create(MediaType.parse("application/json"), str);
    }

    public static <T> RequestBody beanToRequestBody(T jsonBean) {
        String str = AESJsonBean(jsonBean);
        if (StringTool.isEmpty(str)) {
            throw new NullPointerException("beanToRequestBody str is null");
        }
        return RequestBody.create(MediaType.parse("application/json"), str);
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .disableHtmlEscaping()
                .create();
    }

    // 加上排序的TypeAdapter for RequestJson
    public static Gson getGsonTypeAdapterForRequestJson() {
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(RequestJson.class, new RequestJsonTypeAdapter())
                .create();

        return gson;
    }

    // 加上排序的TypeAdapter for ResponseJson
    public static Gson getGsonTypeAdapterForResponseJson() {
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(ResponseJson.class, new ResponseJsonTypeAdapter())
                .create();

        return gson;
    }

}
