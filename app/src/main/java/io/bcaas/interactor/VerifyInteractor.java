package io.bcaas.interactor;


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
 * 验证当前账户
 */
public class VerifyInteractor {

    public void verify(RequestBody body, Callback<ResponseJson> callBackListener) {
        HttpApi httpApi = RetrofitFactory.getInstance().create(HttpApi.class);
        Call<ResponseJson> call = httpApi.verify(body);
        call.enqueue(callBackListener);
    }

}
