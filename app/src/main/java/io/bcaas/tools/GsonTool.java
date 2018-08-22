package io.bcaas.tools;


import com.google.gson.Gson;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

import io.bcaas.encryption.AES;
import io.bcaas.http.ParameterizedTypeImpl;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.adapter.rxjava.Result;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 */
public class GsonTool {
    //解析数据是object的情况
    public static <T> Result<T> fromJsonObject(String response, Class<T> clazz) {
        Gson gson = new Gson();
        Type type = new ParameterizedTypeImpl(Result.class, new Class[]{clazz});
        return gson.fromJson(response, type);
    }

    public static <T> Result<T> fromJsonObject(Reader reader, Class<T> clazz) {
        Gson gson = new Gson();
        Type type = new ParameterizedTypeImpl(Result.class, new Class[]{clazz});
        return gson.fromJson(reader, type);
    }

    //解析数据是数组的情况
    public static <T> Result<List<T>> fromJsonArray(Reader reader, Class<T> clazz) {
        Gson gson = new Gson();
        // 生成List<T> 中的 List<T>
        Type listType = new ParameterizedTypeImpl(List.class, new Class[]{clazz});
        // 根据List<T>生成完整的Result<List<T>>
        Type type = new ParameterizedTypeImpl(Result.class, new Type[]{listType});
        return gson.fromJson(reader, type);
    }


    /*将对象转换为String*/
    public static <T> String encodeToString(T bean) {
        if (bean == null) return null;
        Gson gson = new Gson();
        return gson.toJson(bean);
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
}
