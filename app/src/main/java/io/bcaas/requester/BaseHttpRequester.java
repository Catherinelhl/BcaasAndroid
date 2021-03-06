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
    public Observable<ResponseJson> getWalletWaitingToReceiveBlock(RequestBody body) {
        String baseUrl = BCAASApplication.getANHttpAddress();
        if (StringTool.isEmpty(baseUrl)) {
            return Observable.empty();
        }
        HttpApi httpApi = RetrofitFactory.getAnInstance(baseUrl).create(HttpApi.class);
        return httpApi.getWalletWaitingToReceiveBlock(body);
    }


    //单独获取钱包
    public Observable<ResponseJson> getBalance(RequestBody body) {
        String baseUrl = BCAASApplication.getANHttpAddress();
        if (StringTool.isEmpty(baseUrl)) {
            return Observable.empty();
        }
        HttpApi httpApi = RetrofitFactory.getAnInstance(baseUrl).create(HttpApi.class);
        return httpApi.getBalance(body);
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
    public Observable<ResponseJson> getBlockServiceList(RequestBody body) {
        HttpApi httpApi = RetrofitFactory.getAPIInstance().create(HttpApi.class);
        return httpApi.getBlockServiceList(body);
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
    public Observable<ResponseJson> getAccountDoneTC(RequestBody requestBody) {
        HttpApi httpApi = RetrofitFactory.getAPIInstance().create(HttpApi.class);
        return httpApi.getAccountDoneTC(requestBody);
    }

    //获取未完成交易 API
    public Observable<ResponseJson> getAccountUNDoneTC(RequestBody requestBody) {
        HttpApi httpApi = RetrofitFactory.getAPIInstance().create(HttpApi.class);
        return httpApi.getAccountUNDoneTC(requestBody);
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

    public Observable<ResponseJson> verify(RequestBody body) {
        HttpApi httpApi = RetrofitFactory.getInstance().create(HttpApi.class);
        return httpApi.verify(body);
    }

    public Observable<ResponseJson> getMyIpInfo(RequestBody body) {
        HttpApi httpApi = RetrofitFactory.getAPIInstance().create(HttpApi.class);
        return httpApi.getMyIpInfo(body);
    }
}
