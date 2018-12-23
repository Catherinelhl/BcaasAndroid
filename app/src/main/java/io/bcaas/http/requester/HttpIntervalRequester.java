package io.bcaas.http.requester;

import java.util.concurrent.TimeUnit;

import io.bcaas.base.BCAASApplication;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author: catherine
 * @date: 2018/10/19
 * @description 執行HTTP請求類：需要背景執行的請求
 * such as:「獲取未簽章區塊/GetWalletWaitingToReceiveBlock」、「獲取餘額/getBalance」
 */
public class HttpIntervalRequester {
    private static String TAG = HttpIntervalRequester.class.getSimpleName();
    //开始背景执行未签章区块的請求定時管理
    public static Disposable getReceiveBlockByIntervalDisposable;
    //獲取帳戶餘額的請求定時管理
    public static Disposable getBalanceIntervalDisposable;

    /**
     * 开始定时http请求
     *
     * @param httpASYNTCPResponseListener
     */
    public static void startToHttpIntervalRequest(HttpASYNTCPResponseListener httpASYNTCPResponseListener) {
        if (BCAASApplication.tokenIsNull()) {
            //如果当前的token为null，那么就停止所有循环
            disposeRequest(getBalanceIntervalDisposable);
            disposeRequest(getReceiveBlockByIntervalDisposable);
            return;
        }
        // 開始獲取帳戶餘額
        startGetBalanceLoop(httpASYNTCPResponseListener);
        // 開始背景執行獲取未簽章區塊
        startGetWalletWaitingToReceiveBlockLoop(httpASYNTCPResponseListener);
    }

    /**
     * 定時請求未簽章區塊
     */
    public static void startGetWalletWaitingToReceiveBlockLoop(HttpASYNTCPResponseListener httpASYNTCPResponseListener) {
        LogTool.d(TAG, "token:startGetWalletWaitingToReceiveBlockLoop" + BCAASApplication.tokenIsNull());

        if (BCAASApplication.tokenIsNull()) {
            //如果当前的token为null，那么就停止所有循环
            disposeRequest(getBalanceIntervalDisposable);
            disposeRequest(getReceiveBlockByIntervalDisposable);
            return;
        }
        //1：關閉當前如果還存在的此定時器
        disposeRequest(getReceiveBlockByIntervalDisposable);
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
                        disposeRequest(getReceiveBlockByIntervalDisposable);
                    }

                    @Override
                    public void onComplete() {
                        disposeRequest(getReceiveBlockByIntervalDisposable);
                    }
                });

    }

    /**
     * 定時獲取餘額
     */
    private static void startGetBalanceLoop(HttpASYNTCPResponseListener httpASYNTCPResponseListener) {
        LogTool.d(TAG, "token:startGetBalanceLoop" + BCAASApplication.tokenIsNull());
        if (BCAASApplication.tokenIsNull()) {
            //如果当前的token为null，那么就停止所有循环
            disposeRequest(getBalanceIntervalDisposable);
            disposeRequest(getReceiveBlockByIntervalDisposable);
            return;
        }
        //1：關閉當前如果還存在的此定時器
        disposeRequest(getBalanceIntervalDisposable);
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
                        disposeRequest(getBalanceIntervalDisposable);
                    }

                    @Override
                    public void onComplete() {
                        disposeRequest(getBalanceIntervalDisposable);
                    }
                });
    }

    /**
     * 关闭背景執行獲取帳戶餘額
     */

    public static void disposeRequest(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            LogTool.i(TAG, MessageConstants.socket.CLOSE_GET_BALANCE_INTERVAL_REQUEST);
            disposable.dispose();
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
        if (BCAASApplication.tokenIsNull()) {
            //如果当前的token为null，那么就停止所有循环
            disposeRequest(getBalanceIntervalDisposable);
            disposeRequest(getReceiveBlockByIntervalDisposable);
        } else {
            LogTool.d(TAG, MessageConstants.GET_WALLET_WAITING_TO_RECEIVE_BLOCK);
            BaseHttpRequester baseHttpRequester = new BaseHttpRequester();
            baseHttpRequester.getWalletWaitingToReceiveBlock(GsonTool.beanToRequestBody(requestJson))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseJson>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(ResponseJson responseJson) {
                            LogTool.d(TAG, responseJson);
                            if (responseJson != null) {
                                if (responseJson.isSuccess()) {
                                    LogTool.d(TAG, MessageConstants.SUCCESS_GET_WALLET_RECEIVE_BLOCK);
                                } else {
                                    //因為背景執行「getBalance」是10s進行一次，所以這裏的失敗就不做處理，未簽章區塊一起做
                                    LogTool.d(TAG, MessageConstants.FAILURE_GET_WALLET_RECEIVE_BLOCK);
                                    int code = responseJson.getCode();
                                    if (JsonTool.isTokenInvalid(code)) {
                                        if (httpASYNTCPResponseListener != null) {
                                            httpASYNTCPResponseListener.logout();
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            LogTool.d(TAG, MessageConstants.FAILURE_GET_WALLET_RECEIVE_BLOCK + e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        }
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
        if (BCAASApplication.tokenIsNull()) {
            //如果当前的token为null，那么就停止所有循环
            disposeRequest(getReceiveBlockByIntervalDisposable);
            disposeRequest(getBalanceIntervalDisposable);
        } else {
            LogTool.i(TAG, MessageConstants.GET_BALANCE + requestJson);
            BaseHttpRequester baseHttpRequester = new BaseHttpRequester();
            baseHttpRequester.getBalance(GsonTool.beanToRequestBody(requestJson))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseJson>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ResponseJson responseJson) {
                            LogTool.i(TAG, responseJson);
                            if (responseJson != null) {
                                if (responseJson.isSuccess()) {
                                    LogTool.i(TAG, MessageConstants.SUCCESS_GET_WALLET_GETBALANCE);
                                } else {
                                    LogTool.d(TAG, MessageConstants.FAILURE_GET_WALLET_GETBALANCE);
                                    int code = responseJson.getCode();
                                    if (code == MessageConstants.CODE_3003
                                            || code == MessageConstants.CODE_2034
                                            || code == MessageConstants.CODE_2035) {
                                        //出现异常关闭当前定时请求
                                        disposeRequest(getBalanceIntervalDisposable);
                                    } else if (JsonTool.isTokenInvalid(code)) {
                                        //出现异常关闭当前定时请求
                                        disposeRequest(getBalanceIntervalDisposable);
                                        if (httpASYNTCPResponseListener != null) {
                                            httpASYNTCPResponseListener.logout();
                                        }
                                    }

                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            LogTool.d(TAG, MessageConstants.FAILURE_GET_WALLET_GETBALANCE + e.getMessage());

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

}
