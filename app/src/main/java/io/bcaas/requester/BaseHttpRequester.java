package io.bcaas.requester;


import io.bcaas.base.BcaasApplication;
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
 * An相关
 * <p>
 * 进入钱包之后的与AN交互需要用到的网络请求
 */
public class BaseHttpRequester extends LoginRequester {

    //获取钱包余额以及R区块，长连接
    public void getWalletWaitingToReceiveBlock(RequestBody body, Callback<ResponseJson> callBackListener) {
        String baseUrl = BcaasApplication.getANHttpAddress();
        if (StringTool.isEmpty(baseUrl)) return;
        HttpApi httpApi = RetrofitFactory.getAnInstance(baseUrl).create(HttpApi.class);
        Call<ResponseJson> call = httpApi.getWalletWaitingToReceiveBlock(body);
        call.enqueue(callBackListener);
    }

    //获取最新余额
    public void getLastBlockAndBalance(RequestBody body, Callback<ResponseJson> callBackListener) {
        String baseUrl = BcaasApplication.getANHttpAddress();
        if (StringTool.isEmpty(baseUrl)) return;
        HttpApi httpApi = RetrofitFactory.getAnInstance(baseUrl).create(HttpApi.class);
        Call<ResponseJson> call = httpApi.getLastedBlockAndBalance(body);
        call.enqueue(callBackListener);
    }


    //重新拿去AN的信息
    public void resetAuthNode(RequestBody body, Callback<ResponseJson> callBackListener) {
        HttpApi httpApi = RetrofitFactory.getInstance().create(HttpApi.class);
        Call<ResponseJson> call = httpApi.resetAuthNodeInfo(body);
        call.enqueue(callBackListener);
    }

}
