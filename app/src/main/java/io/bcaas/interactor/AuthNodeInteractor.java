package io.bcaas.interactor;


import io.bcaas.base.BcaasApplication;
import io.bcaas.gson.WalletResponseJson;
import io.bcaas.gson.WalletVoResponseJson;
import io.bcaas.http.HttpApi;
import io.bcaas.http.retrofit.RetrofitFactory;
import io.bcaas.tools.StringTool;
import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/20
 * <p>
 * An相关
 * <p>
 * 重设An地址
 */
public class AuthNodeInteractor {

    //获取钱包余额以及R区块，长连接
    public void getWalletWaitingToReceiveBlock(RequestBody body, Callback<WalletResponseJson> callBackListener) {
        String baseUrl = BcaasApplication.getANHttpAddress();
        if (StringTool.isEmpty(baseUrl)) return;
        HttpApi httpApi = RetrofitFactory.getAnInstance(baseUrl).create(HttpApi.class);
        Call<WalletResponseJson> call = httpApi.getWalletWaitingToReceiveBlock(body);
        call.enqueue(callBackListener);
    }

    //获取最新余额
    public void getLatesBlockAndBalance(RequestBody body, Callback<WalletResponseJson> callBackListener) {
        String baseUrl = BcaasApplication.getANHttpAddress();
        if (StringTool.isEmpty(baseUrl)) return;
        HttpApi httpApi = RetrofitFactory.getAnInstance(baseUrl).create(HttpApi.class);
        Call<WalletResponseJson> call = httpApi.getLastesBlockAndBalance(body);
        call.enqueue(callBackListener);
    }


    //重新拿去AN的信息
    public void resetAuthNode(RequestBody body, Callback<WalletVoResponseJson> callBackListener) {
        HttpApi httpApi = RetrofitFactory.getInstance().create(HttpApi.class);
        Call<WalletVoResponseJson> call = httpApi.resetAuthNodeInfo(body);
        call.enqueue(callBackListener);
    }

}
