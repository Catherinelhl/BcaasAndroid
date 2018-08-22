package io.bcaas.interactor;


import io.bcaas.base.BcaasApplication;
import io.bcaas.gson.WalletResponseJson;
import io.bcaas.gson.WalletVoResponseJson;
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
 * 重设An地址
 */
public class MainInteractor {

    //获取钱包余额以及R区块，长连接
    public void getWalletWaitingToReceiveBlock(RequestBody body, Callback<WalletResponseJson> callBackListener) {
        String internalIp = BcaasApplication.getExternalIp();
        int rpcPort = BcaasApplication.getRpcPort();
        if (StringTool.isEmpty(internalIp) || rpcPort == 0) return;
        HttpApi httpApi = RetrofitFactory.getAnInstance("http://" + internalIp + ":" + rpcPort).create(HttpApi.class);
        Call<WalletResponseJson> call = httpApi.getWalletWaitingToReceiveBlock(body);
        call.enqueue(callBackListener);
    }

    //重新拿去AN的信息
    public void resetAuthNode(RequestBody body, Callback<WalletVoResponseJson> callBackListener) {
        HttpApi httpApi = RetrofitFactory.getInstance().create(HttpApi.class);
        Call<WalletVoResponseJson> call = httpApi.resetAuthNodeInfo(body);
        call.enqueue(callBackListener);
    }

}
