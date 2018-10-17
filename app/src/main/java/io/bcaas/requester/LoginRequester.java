package io.bcaas.requester;


import io.bcaas.gson.ResponseJson;
import io.bcaas.http.HttpApi;
import io.bcaas.http.retrofit.RetrofitFactory;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/20
 * <p>
 * 「登入」、「验证」
 */
public class LoginRequester {

    public void login(RequestBody body, Callback<ResponseJson> callBackListener) {
        HttpApi httpApi = RetrofitFactory.getInstance().create(HttpApi.class);
        Call<ResponseJson> call = httpApi.login(body);
        call.enqueue(callBackListener);
    }

    public void verify(RequestBody body, Callback<ResponseJson> callBackListener) {
        HttpApi httpApi = RetrofitFactory.getInstance().create(HttpApi.class);
        Call<ResponseJson> call = httpApi.verify(body);
        call.enqueue(callBackListener);
    }

    public void getMyIpInfo(Callback<String> callBackListener) {
        HttpApi httpApi = RetrofitFactory.getAPIInstance().create(HttpApi.class);
        Call<String> call = httpApi.getMyIpInfp();
        call.enqueue(callBackListener);
    }
}
