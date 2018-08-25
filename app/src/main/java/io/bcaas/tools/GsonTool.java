package io.bcaas.tools;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.Result;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;


import io.bcaas.encryption.AES;
import io.bcaas.http.ParameterizedTypeImpl;
import okhttp3.MediaType;
import okhttp3.RequestBody;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 */
public class GsonTool {
    //解析数据是object的情况
    public static <T> T fromJsonObject(String response, Class<T> clazz) {
        Type type = new ParameterizedTypeImpl(Result.class, new Class[]{clazz});
        return getGson().fromJson(response, type);
    }

    public static <T> T fromJsonObject(Reader reader, Class<T> clazz) {
        Type type = new ParameterizedTypeImpl(Result.class, new Class[]{clazz});
        return getGson().fromJson(reader, type);
    }

    //解析数据是数组的情况
    public static <T> List<T> fromJsonArray(Reader reader, Class<T> clazz) {
        // 生成List<T> 中的 List<T>
        Type listType = new ParameterizedTypeImpl(List.class, new Class[]{clazz});
        // 根据List<T>生成完整的Result<List<T>>
        Type type = new ParameterizedTypeImpl(Result.class, new Type[]{listType});
        return getGson().fromJson(reader, type);
    }


    /*将对象转换为String*/
    public static <T> String encodeToString(T bean) {
        if (bean == null) return null;
        return getGson().toJson(bean);
    }

    /*   encryption */
    public static <T> String AESJsonBean(T jsonBean) {
        if (jsonBean == null) {
            throw new NullPointerException("AESJsonBean jsonBean is null");
        }
        String json = GsonTool.encodeToString(jsonBean);
        // encryption
        String encodeJson = null;
        try {
            encodeJson = AES.encodeCBC_128(json);
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

}
