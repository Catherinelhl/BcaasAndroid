package io.bcaas.requester;


import io.bcaas.base.BCAASApplication;
import io.bcaas.gson.ResponseJson;
import io.bcaas.http.HttpApi;
import io.bcaas.http.retrofit.RetrofitFactory;
import io.bcaas.tools.StringTool;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/20
 * <p>
 * 设置的网络
 */
public class SettingRequester {

    /*「登出」當前帳戶*/
    public void logout(RequestBody body, Callback<ResponseJson> callBackListener) {
        HttpApi httpApi = RetrofitFactory.getInstance().create(HttpApi.class);
        Call<ResponseJson> call = httpApi.logout(body);
        call.enqueue(callBackListener);
    }

    /* 取最新的更換委託人區塊*/
    public void getLastChangeBlock(RequestBody body, Callback<ResponseJson> callBackListener) {
        String baseUrl = BCAASApplication.getANHttpAddress();
        if (StringTool.isEmpty(baseUrl)) {
            return;
        }
        HttpApi httpApi = RetrofitFactory.getAnInstance(baseUrl).create(HttpApi.class);
        Call<ResponseJson> call = httpApi.getLatestChangeBlock(body);
        call.enqueue(callBackListener);
    }

    /* TC change*/
    public void change(RequestBody body, Callback<ResponseJson> callBackListener) {
        String baseUrl = BCAASApplication.getANHttpAddress();
        if (StringTool.isEmpty(baseUrl)) {
            return;
        }
        HttpApi httpApi = RetrofitFactory.getAnInstance(baseUrl).create(HttpApi.class);
        Call<ResponseJson> call = httpApi.change(body);
        call.enqueue(callBackListener);
    }
}
