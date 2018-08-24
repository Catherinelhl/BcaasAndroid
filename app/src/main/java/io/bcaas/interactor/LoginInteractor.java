package io.bcaas.interactor;


import io.bcaas.http.HttpApi;
import io.bcaas.http.retrofit.RetrofitFactory;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/20
 * <p>
 * 登录的网络请求
 */
public class LoginInteractor {

    public void login(RequestBody body, Callback<String> callBackListener) {
        HttpApi httpApi = RetrofitFactory.getInstance().create(HttpApi.class);
        Call<String> call = httpApi.login(body);
        call.enqueue(callBackListener);
    }
}
