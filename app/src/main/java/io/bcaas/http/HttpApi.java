package io.bcaas.http;


import io.bcaas.constants.APIURLConstants;
import io.bcaas.constants.Constants;
import io.bcaas.gson.ResponseJson;
import io.bcaas.gson.ResponseJson;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/20
 * <p>
 * 请求网络的所有接口
 */
public interface HttpApi {

    /************** SFN ***************/
    // 登入  @FormUrlEncoded
    @POST(Constants.RequestUrl.login)
    Call<ResponseJson> login(@Body RequestBody requestBody);

    /*SFN：登出*/
    @POST(Constants.RequestUrl.logout)
    Call<ResponseJson> logout(@Body RequestBody requestBody);

    /*SFN：验证当前token是否过期*/
    @POST(Constants.RequestUrl.verify)
    Call<ResponseJson> verify(@Body RequestBody requestBody);

    /*SFN：當錢包與AuthNode無法通信時調用,取得新的AuthNode IP資訊*/
    @POST(Constants.RequestUrl.resetAuthNodeInfo)
    Call<ResponseJson> resetAuthNodeInfo(@Body RequestBody requestBody);


    /************** AN ***************/
    /*AN："取最新的區塊 & wallet餘額"*/
    /* 每次发送之前需要请求*/
    @POST(Constants.RequestUrl.getLatestBlockAndBalance)
    Call<ResponseJson> getLastedBlockAndBalance(@Body RequestBody requestBody);

    /*AN："取得未簽章R區塊的Send區塊 & 取最新的R區塊 & wallet餘額"*/
    /*由TCP和服务器建立长连接，进行定时的拉取数据*/
    @POST(Constants.RequestUrl.getWalletWaitingToReceiveBlock)
    Call<ResponseJson> getWalletWaitingToReceiveBlock(@Body RequestBody requestBody);

    /*AN：TC Send*/
    @POST(Constants.RequestUrl.send)
    Call<ResponseJson> send(@Body RequestBody requestBody);

    /*AN：TC receiver*/
    @POST(Constants.RequestUrl.receive)
    Call<ResponseJson> receive(@Body RequestBody requestBody);

    /*AN：獲取最新的更換委託人區塊*/
    @POST(Constants.RequestUrl.getLatestChangeBlock)
    Call<ResponseJson> getLatestChangeBlock(@Body RequestBody requestBody);

    /*AN：TC change*/
    @POST(Constants.RequestUrl.change)
    Call<ResponseJson> change(@Body RequestBody requestBody);


    /************** API ***************/

    /*獲取幣種清單 API*/
    @POST(APIURLConstants.API_GET_BLOCK_SERVICE_LIST)
    Call<ResponseJson> getBlockServiceList(@Body RequestBody requestBody);

    /*獲取已完成交易 API*/
    @POST(APIURLConstants.API_ACCOUNT_DONE_TC)
    Call<ResponseJson> getAccountDoneTC(@Body RequestBody requestBody);


    /************** UPDATE ***************/
    /*检查更新Android版本信息*/
    @POST(Constants.RequestUrl.getAndroidVersionInfo)
    Call<ResponseJson> getAndroidVersionInfo(@Body RequestBody requestBody);
}
