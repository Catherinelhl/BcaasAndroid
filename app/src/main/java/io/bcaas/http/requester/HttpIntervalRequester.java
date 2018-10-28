package io.bcaas.http.requester;

import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.listener.HttpASYNTCPResponseListener;
import io.bcaas.requester.BaseHttpRequester;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.tools.gson.JsonTool;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.concurrent.TimeUnit;

/**
 * @author: catherine
 * @date: 2018/10/19
 * @description 背景執行未簽章區塊
 */
public class HttpIntervalRequester {
    private static String TAG = HttpIntervalRequester.class.getSimpleName();
    //开始背景执行未签章区块的請求定時管理
    private static Disposable getReceiveBlockByIntervalDisposable;
    //獲取帳戶餘額的請求定時管理
    private static Disposable getBalanceIntervalDisposable;

    /**
     * 开始定时http请求
     *
     * @param httpASYNTCPResponseListener
     */
    public static void startToHttpIntervalRequest(HttpASYNTCPResponseListener httpASYNTCPResponseListener) {
        // 開始獲取帳戶餘額
        startGetBalanceLoop(httpASYNTCPResponseListener);
        // 開始背景執行獲取未簽章區塊
        startGetWalletWaitingToReceiveBlockLoop(httpASYNTCPResponseListener);
    }

    /**
     * 定時請求未簽章區塊
     */
    public static void startGetWalletWaitingToReceiveBlockLoop(HttpASYNTCPResponseListener httpASYNTCPResponseListener) {
        //1：關閉當前如果還存在的此定時器
        closeGetWalletWaitingToReceiveBlockIntervalRequest();
        //2：開啟定時器，定時請求未簽章區塊
        Observable.interval(0, Constants.ValueMaps.GET_RECEIVE_BLOCK_TIME, TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getReceiveBlockByIntervalDisposable = d;
                    }

                    @Override
                    public void onNext(Long value) {
                        //开始背景执行
                        getWalletWaitingToReceiveBlock(httpASYNTCPResponseListener);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogTool.e(TAG, e.getMessage());
                        closeGetWalletWaitingToReceiveBlockIntervalRequest();
                    }

                    @Override
                    public void onComplete() {
                        closeGetWalletWaitingToReceiveBlockIntervalRequest();
                    }
                });

    }

    /**
     * 定時獲取餘額
     */
    private static void startGetBalanceLoop(HttpASYNTCPResponseListener httpASYNTCPResponseListener) {
        //1：關閉當前如果還存在的此定時器
        closeGetBalanceIntervalRequest();
        //2：開啟定時器，定時請求帳戶餘額
        Observable.interval(0, Constants.ValueMaps.GET_BALANCE_TIME, TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getBalanceIntervalDisposable = d;
                    }

                    @Override
                    public void onNext(Long value) {
                        //每次开始背景执行的时候要前去验证一下
                        MasterRequester.verify(Constants.Verify.GET_BALANCE_LOOP, httpASYNTCPResponseListener);
                        //开始背景执行獲取帳戶餘額
                        getBalance(httpASYNTCPResponseListener);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogTool.e(TAG, e.getMessage());
                        closeGetBalanceIntervalRequest();
                    }

                    @Override
                    public void onComplete() {
                        closeGetBalanceIntervalRequest();
                    }
                });
    }

    /**
     * 关闭背景執行獲取未簽章區塊
     */
    public static void closeGetWalletWaitingToReceiveBlockIntervalRequest() {
        if (getReceiveBlockByIntervalDisposable != null) {
            LogTool.i(TAG, MessageConstants.socket.CLOSE_GET_WALLET_WAITING_TO_RECEIVE_BLOCK_INTERVAL_REQUEST);
            getReceiveBlockByIntervalDisposable.dispose();
        }
    }

    /**
     * 关闭背景執行獲取帳戶餘額
     */

    public static void closeGetBalanceIntervalRequest() {
        if (getBalanceIntervalDisposable != null) {
            LogTool.i(TAG, MessageConstants.socket.CLOSE_GET_BALANCE_INTERVAL_REQUEST);
            getBalanceIntervalDisposable.dispose();
        }
    }

    /**
     * 背景執行獲取餘額，以及 拿取未签章区块
     */
    private static void getWalletWaitingToReceiveBlock(HttpASYNTCPResponseListener httpASYNTCPResponseListener) {
        RequestJson requestJson = JsonTool.getWalletWaitingToReceiveBlockRequestJson();
        if (requestJson == null) {
            LogTool.i(TAG, MessageConstants.GET_WALLET_WAITING_TO_RECEIVE_BLOCK_DATA_ERROR);
            return;
        }
        LogTool.d(TAG, MessageConstants.GET_WALLET_WAITING_TO_RECEIVE_BLOCK);
        BaseHttpRequester baseHttpRequester = new BaseHttpRequester();
        baseHttpRequester.getWalletWaitingToReceiveBlock(GsonTool.beanToRequestBody(requestJson),
                new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                        LogTool.d(TAG, response.body());
                        ResponseJson walletResponseJson = response.body();
                        if (walletResponseJson != null) {
                            if (walletResponseJson.isSuccess()) {
                                LogTool.d(TAG, MessageConstants.SUCCESS_GET_WALLET_RECEIVE_BLOCK);
                            } else {
                                //因為背景執行「getBalance」是10s進行一次，所以這裏的失敗就不做處理，未簽章區塊一起做
                                LogTool.d(TAG, MessageConstants.FAILURE_GET_WALLET_RECEIVE_BLOCK);
                                int code = walletResponseJson.getCode();
                                if (JsonTool.isTokenInvalid(code)) {
                                    if (httpASYNTCPResponseListener != null) {
                                        httpASYNTCPResponseListener.logout();
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseJson> call, Throwable t) {
                        LogTool.d(TAG, MessageConstants.FAILURE_GET_WALLET_RECEIVE_BLOCK + t.getMessage());
                    }
                });
    }

    /**
     * 定時獲取帳戶餘額
     *
     * @param httpASYNTCPResponseListener
     */
    private static void getBalance(HttpASYNTCPResponseListener httpASYNTCPResponseListener) {
        RequestJson requestJson = JsonTool.getRequestJson();
        if (requestJson == null) {
            LogTool.i(TAG, MessageConstants.GET_BALANCE_DATA_ERROR);
            return;
        }
        LogTool.i(TAG, MessageConstants.GET_BALANCE + requestJson);
        BaseHttpRequester baseHttpRequester = new BaseHttpRequester();
        baseHttpRequester.getBalance(GsonTool.beanToRequestBody(requestJson),
                new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                        ResponseJson walletResponseJson = response.body();
                        LogTool.i(TAG, walletResponseJson);
                        if (walletResponseJson != null) {
                            if (walletResponseJson.isSuccess()) {
                                LogTool.i(TAG, MessageConstants.SUCCESS_GET_WALLET_GETBALANCE);
                            } else {
                                LogTool.d(TAG, MessageConstants.FAILURE_GET_WALLET_GETBALANCE);
                                int code = walletResponseJson.getCode();
                                if (code == MessageConstants.CODE_3003) {
                                    //出现异常关闭当前定时请求
                                    closeGetBalanceIntervalRequest();
                                    MasterRequester.reset(httpASYNTCPResponseListener, true);
                                } else if (code == MessageConstants.CODE_2035) {
                                    //出现异常关闭当前定时请求
                                    closeGetBalanceIntervalRequest();
                                    MasterRequester.reset(httpASYNTCPResponseListener, true);
                                } else if (JsonTool.isTokenInvalid(code)) {
                                    //出现异常关闭当前定时请求
                                    closeGetBalanceIntervalRequest();
                                    if (httpASYNTCPResponseListener != null) {
                                        httpASYNTCPResponseListener.logout();
                                    }
                                }

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseJson> call, Throwable t) {
                        LogTool.d(TAG, MessageConstants.FAILURE_GET_WALLET_GETBALANCE + t.getMessage());
                    }
                });
    }


}
