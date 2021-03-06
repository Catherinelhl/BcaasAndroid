package io.bcaas.http.retrofit;


import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import io.bcaas.bean.ServerBean;
import io.bcaas.constants.Constants;
import io.bcaas.tools.ServerTool;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/20
 * <p>
 * Http：Retrofit封裝网络请求
 */
public class RetrofitFactory {

    private static Retrofit SFNInstance;
    private static Retrofit ANInstance;//访问AN的网络
    private static Retrofit APIInstance;//访问正常訪問的网络
    private static Retrofit UpdateInstance;//检查更新
    private static OkHttpClient client;

    private static void initClient() {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .connectTimeout(Constants.Time.LONG_TIME_OUT, TimeUnit.SECONDS)
                    .readTimeout(Constants.Time.LONG_TIME_OUT, TimeUnit.SECONDS)
                    .writeTimeout(Constants.Time.LONG_TIME_OUT, TimeUnit.SECONDS)
                    .addInterceptor(new OkHttpInterceptor())
                    .build();
        }
    }

    public static Retrofit getInstance() {
        ServerBean serverBean = ServerTool.getDefaultServerBean();
        if (serverBean == null) {
            serverBean = ServerTool.getDefaultServerBean();
            if (serverBean == null) {
                return null;
            }
        }
        return getSFNInstance(serverBean.getSfnServer());
    }

    /**
     * SFN api
     *
     * @param baseUrl
     * @return
     */
    public static Retrofit getSFNInstance(String baseUrl) {
        initClient();
        if (SFNInstance == null) {
            SFNInstance = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(new StringConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//Observable，暂时没用
                    .build();
        }
        return SFNInstance;
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
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//Observable，暂时没用
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
        ServerBean serverBean = ServerTool.getDefaultServerBean();
        String apiServer = null;
        if (serverBean != null) {
            apiServer = serverBean.getApiServer();
        }
        APIInstance = new Retrofit.Builder()
                .baseUrl(apiServer)
                .client(client)
                .addConverterFactory(new StringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//Observable，暂时没用
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
        ServerBean serverBean = ServerTool.getDefaultServerBean();
        String updateServer = null;
        if (serverBean != null) {
            updateServer = serverBean.getUpdateServer();
        }
        UpdateInstance = new Retrofit.Builder()
                .baseUrl(updateServer)
                .client(client)
                .addConverterFactory(new StringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//Observable，暂时没用
                .build();
        return UpdateInstance;
    }

    //清空当前所有请求的缓存数据信息
    public static void clean() {
        cleanSFN();
        cleanAN();
        cleanAPI();
        UpdateInstance = null;
    }

    //清空当前的SFN请求
    public static void cleanSFN() {
        SFNInstance = null;
    }

    //清空AN请求
    public static void cleanAN() {
        ANInstance = null;

    }

    //清空API请求
    public static void cleanAPI() {
        APIInstance = null;
    }

}
