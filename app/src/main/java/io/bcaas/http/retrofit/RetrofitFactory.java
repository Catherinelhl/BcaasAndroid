package io.bcaas.http.retrofit;


import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import io.bcaas.constants.Constants;
import io.bcaas.constants.SystemConstants;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/20
 * <p>
 * 网络请求
 */
public class RetrofitFactory {

    private static Retrofit instance;
    private static Retrofit ANInstance;//访问AN的网络
    private static Retrofit APIInstance;//访问正常訪問的网络
    private static OkHttpClient client;

    private static void initClient() {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .connectTimeout(Constants.ValueMaps.TIME_OUT_TIME, TimeUnit.SECONDS)
                    .readTimeout(Constants.ValueMaps.TIME_OUT_TIME, TimeUnit.SECONDS)
                    .writeTimeout(Constants.ValueMaps.TIME_OUT_TIME, TimeUnit.SECONDS)
                    .addInterceptor(new OkHttpInterceptor())
                    .build();
        }
    }

    //默认先走test
    public static Retrofit getInstance() {
        return getInstance(SystemConstants.SEEDFULLNODE_URL_DEFAULT_1);
    }

    //因为AN的请求地址是通过访问SFN得到，所以这里的baseUrl是个动态的
    public static Retrofit getInstance(String baseUrl) {
        initClient();
        if (instance == null) {
            instance = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(new StringConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//Observble，暂时没用
                    .build();
        }
        return instance;
    }

    //创建一个请求AN地址的网络管理，考虑到地址可能是变化的....
    public static Retrofit getAnInstance(String baseUrl) {
        initClient();
        ANInstance = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(new StringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//Observble，暂时没用
                .build();
        return ANInstance;
    }

    public static Retrofit getAPIInstance() {
        initClient();
        APIInstance = new Retrofit.Builder()
                .baseUrl(SystemConstants.SEEDFULLNODE_URL_DEFAULT_5)
                .client(client)
                .addConverterFactory(new StringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//Observble，暂时没用
                .build();
        return APIInstance;
    }
}
