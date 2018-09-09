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

    private static Retrofit SFNinstance;
    private static Retrofit ANInstance;//访问AN的网络
    private static Retrofit APIInstance;//访问正常訪問的网络
    private static Retrofit UpdateInstance;//检查更新
    private static Retrofit pingInstance;//检查更新
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

    public static Retrofit getInstance() {
        return getSFNInstance(SystemConstants.SEEDFULLNODE_URL_DEFAULT_1);
    }

    /**
     * SFN api
     *
     * @param baseUrl
     * @return
     */
    public static Retrofit getSFNInstance(String baseUrl) {
        initClient();
        if (SFNinstance == null) {
            SFNinstance = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(new StringConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//Observble，暂时没用
                    .build();
        }
        return SFNinstance;
    }

    /**
     * AN api
     *
     * @param baseUrl
     * @return
     */
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

    /**
     * Application api
     *
     * @return
     */
    public static Retrofit getAPIInstance() {
        initClient();
        APIInstance = new Retrofit.Builder()
                .baseUrl(SystemConstants.APPLICATION_URL)
                .client(client)
                .addConverterFactory(new StringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//Observble，暂时没用
                .build();
        return APIInstance;
    }

    /**
     * update server api
     *
     * @return
     */
    public static Retrofit getUpdateInstance() {
        initClient();
        UpdateInstance = new Retrofit.Builder()
                .baseUrl(SystemConstants.UPDATE_SERVER_URL)
                .client(client)
                .addConverterFactory(new StringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//Observble，暂时没用
                .build();
        return UpdateInstance;
    }

    /**
     *ping 检查当前网络状况
     * @return
     */
    public static Retrofit pingInstance() {
        initClient();
        pingInstance = new Retrofit.Builder()
                .baseUrl(SystemConstants.SEEDFULLNODE_URL_DEFAULT_1)
                .client(client)
                .addConverterFactory(new StringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//Observble，暂时没用
                .build();
        return pingInstance;
    }
}
