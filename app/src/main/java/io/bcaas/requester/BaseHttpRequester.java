package io.bcaas.requester;


import io.bcaas.base.BCAASApplication;
import io.bcaas.gson.ResponseJson;
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
 * Requester:所有网络请求前數據組裝以及響應結果的交互器
 */
public class BaseHttpRequester {

    //获取钱包余额以及R区块，长连接
    public void getWalletWaitingToReceiveBlock(RequestBody body, Callback<ResponseJson> callBackListener) {
        String baseUrl = BCAASApplication.getANHttpAddress();
        if (StringTool.isEmpty(baseUrl)) {
            return;
        }
        HttpApi httpApi = RetrofitFactory.getAnInstance(baseUrl).create(HttpApi.class);
        Call<ResponseJson> call = httpApi.getWalletWaitingToReceiveBlock(body);
        call.enqueue(callBackListener);
    }


    //单独获取钱包
    public void getBalance(RequestBody body, Callback<ResponseJson> callBackListener) {
        String baseUrl = BCAASApplication.getANHttpAddress();
        if (StringTool.isEmpty(baseUrl)) {
            return;
        }
        HttpApi httpApi = RetrofitFactory.getAnInstance(baseUrl).create(HttpApi.class);
        Call<ResponseJson> call = httpApi.getBalance(body);
        call.enqueue(callBackListener);
    }

    //获取最新余额
    public void getLastBlockAndBalance(RequestBody body, Callback<ResponseJson> callBackListener) {
        String baseUrl = BCAASApplication.getANHttpAddress();
        if (StringTool.isEmpty(baseUrl)) {
            return;
        }
        HttpApi httpApi = RetrofitFactory.getAnInstance(baseUrl).create(HttpApi.class);
        Call<ResponseJson> call = httpApi.getLastedBlockAndBalance(body);
        call.enqueue(callBackListener);
    }


    //重新拿去AN的信息
    public Observable<ResponseJson> resetAuthNode(RequestBody body) {
        HttpApi httpApi = RetrofitFactory.getInstance().create(HttpApi.class);
        return httpApi.resetAuthNodeInfo(body);

    }

    //拿去幣種清單的信息
    public void getBlockServiceList(RequestBody body, Callback<ResponseJson> callBackListener) {
        HttpApi httpApi = RetrofitFactory.getAPIInstance().create(HttpApi.class);
        Call<ResponseJson> call = httpApi.getBlockServiceList(body);
        call.enqueue(callBackListener);
    }

    //receive
    public void receive(RequestBody body, Callback<ResponseJson> callBackListener) {
        String baseUrl = BCAASApplication.getANHttpAddress();
        if (StringTool.isEmpty(baseUrl)) {
            return;
        }
        HttpApi httpApi = RetrofitFactory.getAnInstance(baseUrl).create(HttpApi.class);
        Call<ResponseJson> call = httpApi.receive(body);
        call.enqueue(callBackListener);
    }

    //send
    public void send(RequestBody body, Callback<ResponseJson> callBackListener) {
        String baseUrl = BCAASApplication.getANHttpAddress();
        if (StringTool.isEmpty(baseUrl)) {
            return;
        }
        HttpApi httpApi = RetrofitFactory.getAnInstance(baseUrl).create(HttpApi.class);
        Call<ResponseJson> call = httpApi.send(body);
        call.enqueue(callBackListener);
    }

    public void getAndroidVersionInfo(RequestBody requestBody, Callback<ResponseJson> callBackListener) {
        HttpApi httpApi = RetrofitFactory.getUpdateInstance().create(HttpApi.class);
        Call<ResponseJson> call = httpApi.getAndroidVersionInfo(requestBody);
        call.enqueue(callBackListener);
    }

    //获取已完成交易 API
    public void getAccountDoneTC(RequestBody requestBody, Callback<ResponseJson> callBackListener) {
        HttpApi httpApi = RetrofitFactory.getAPIInstance().create(HttpApi.class);
        Call<ResponseJson> call = httpApi.getAccountDoneTC(requestBody);
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

    /*「登出」當前帳戶*/
    public void logout(RequestBody body, Callback<ResponseJson> callBackListener) {
        HttpApi httpApi = RetrofitFactory.getInstance().create(HttpApi.class);
        Call<ResponseJson> call = httpApi.logout(body);
        call.enqueue(callBackListener);
    }

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

    public void getMyIpInfo(RequestBody body, Callback<ResponseJson> callBackListener) {
        HttpApi httpApi = RetrofitFactory.getAPIInstance().create(HttpApi.class);
        Call<ResponseJson> call = httpApi.getMyIpInfo(body);
        call.enqueue(callBackListener);
    }
}
