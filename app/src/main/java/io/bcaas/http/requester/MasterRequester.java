package io.bcaas.http.requester;

import io.bcaas.base.BCAASApplication;
import io.bcaas.bean.ServerBean;
import io.bcaas.constants.MessageConstants;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.http.callback.BcaasCallback;
import io.bcaas.http.tcp.TCPThread;
import io.bcaas.listener.GetMyIpInfoListener;
import io.bcaas.listener.HttpASYNTCPResponseListener;
import io.bcaas.requester.BaseHttpRequester;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.NetWorkTool;
import io.bcaas.tools.ServerTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.tools.gson.JsonTool;
import io.bcaas.vo.ClientIpInfoVO;
import io.bcaas.vo.RemoteInfoVO;
import io.bcaas.vo.WalletVO;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author: tianyonghong
 * @date: 2018/8/17
 * @description 執行HTTP請求類：頻繁會用到的HTTP請求，such as:「Verify」、「Reset」
 */
public class MasterRequester {
    private static String TAG = MasterRequester.class.getSimpleName();

    // 存放用户登录验证地址以后返回的ClientIpInfoVO
    public static ClientIpInfoVO clientIpInfoVO;

    private static Disposable disposableReset;


    /**
     * 获取当前Wallet Ip info
     */
    public static void getMyIpInfo(GetMyIpInfoListener getMyIpInfoListener) {
        WalletVO walletVO = new WalletVO();
        walletVO.setWalletAddress(BCAASApplication.getWalletAddress());
        RequestJson requestJson = new RequestJson(walletVO);
        LogTool.d(TAG, requestJson);
        BaseHttpRequester baseHttpRequester = new BaseHttpRequester();
        baseHttpRequester.getMyIpInfo(GsonTool.beanToRequestBody(requestJson), new BcaasCallback<ResponseJson>() {
            @Override
            public void onSuccess(Response<ResponseJson> response) {
                LogTool.d(TAG, MessageConstants.getMyIpInfo + response.body());
                ResponseJson responseJson = response.body();
                RemoteInfoVO remoteInfoVO = responseJson.getRemoteInfoVO();
                if (remoteInfoVO != null) {
                    String walletExternalIp = remoteInfoVO.getRealIP();
                    if (StringTool.notEmpty(walletExternalIp)) {
                        BCAASApplication.setWalletExternalIp(walletExternalIp);
                        getMyIpInfoListener.responseGetMyIpInfo(true);
                    } else {
                        getMyIpInfoListener.responseGetMyIpInfo(false);

                    }
                } else {
                    getMyIpInfoListener.responseGetMyIpInfo(false);

                }
            }

            @Override
            public void onNotFound() {
                super.onNotFound();
                getMyIpInfoListener.responseGetMyIpInfo(false);
            }

            @Override
            public void onFailure(Call<ResponseJson> call, Throwable t) {
                super.onFailure(call, t);
                getMyIpInfoListener.responseGetMyIpInfo(false);
            }
        });
    }

    public static void verify(String from, HttpASYNTCPResponseListener httpASYNTCPResponseListener) {
        RequestJson requestJson = JsonTool.getRequestJsonWithRealIp();
        if (requestJson == null) {
            httpASYNTCPResponseListener.verifyFailure(from);
            return;
        }
        LogTool.d(TAG,MessageConstants.Verify.TAG_INTERVAL +requestJson);
        BaseHttpRequester baseHttpRequester = new BaseHttpRequester();
        baseHttpRequester.verify(GsonTool.beanToRequestBody(requestJson), new Callback<ResponseJson>() {
            @Override
            public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                ResponseJson responseJson = response.body();
                LogTool.d(TAG, responseJson);
                if (responseJson == null) {
                    httpASYNTCPResponseListener.verifyFailure(from);
                } else {
                    int code = responseJson.getCode();
                    if (responseJson.isSuccess()) {
                        WalletVO walletVONew = responseJson.getWalletVO();
                        //当前success的情况有两种
                        if (code == MessageConstants.CODE_200) {
                            //正常，不需要操作
                            httpASYNTCPResponseListener.verifySuccess(from);
                        } else if (code == MessageConstants.CODE_2014) {
                            // 需要替换AN的信息
                            if (walletVONew != null) {
                                ClientIpInfoVO clientIpInfoVO = walletVONew.getClientIpInfoVO();
                                if (clientIpInfoVO != null) {
                                    BCAASApplication.setClientIpInfoVO(clientIpInfoVO);
                                    //重置AN成功，需要重新連結
                                    reset(httpASYNTCPResponseListener, TCPThread.canReset);
                                }
                            }
                        } else {
                            // 异常情况
                            parseHttpExceptionStatus(responseJson, httpASYNTCPResponseListener);
                        }
                    } else {
                        parseHttpExceptionStatus(responseJson, httpASYNTCPResponseListener);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseJson> call, Throwable throwable) {
                LogTool.e(TAG, throwable.getMessage());
                httpASYNTCPResponseListener.verifyFailure(from);
            }
        });
    }


    /**
     * 重置AN信息
     */
    public static void reset(HttpASYNTCPResponseListener httpASYNTCPResponseListener, boolean canReset) {
        LogTool.d(TAG, MessageConstants.socket.CAN_RESET + canReset);
        if (canReset) {
            //如果当前容器为空，那么就不进行请求
            if (disposableReset != null) {
                return;
            }
            //1：先重新请求IP，然后再根据IP重新请求SAN信息
            MasterRequester.getMyIpInfo(isSuccess -> {
                //这儿无论请求失败还是成功，本地都会又一个RealIP，所以直接请求即可
                resetWithRealIp(httpASYNTCPResponseListener);
            });
        }
    }

    private static void resetWithRealIp(HttpASYNTCPResponseListener httpASYNTCPResponseListener) {
        RequestJson requestJson = JsonTool.getRequestJsonWithRealIp();
        if (requestJson == null) {
            httpASYNTCPResponseListener.resetFailure();
            return;
        }
        LogTool.d(TAG, MessageConstants.Reset.REQUEST_JSON + requestJson);
        BaseHttpRequester baseHttpRequester = new BaseHttpRequester();
        baseHttpRequester.resetAuthNode(GsonTool.beanToRequestBody(requestJson))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseJson>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposableReset = d;
                    }

                    @Override
                    public void onNext(ResponseJson responseJson) {
                        if (responseJson != null) {
                            if (responseJson.isSuccess()) {
                                WalletVO walletVOResponse = responseJson.getWalletVO();
                                if (walletVOResponse != null) {
                                    clientIpInfoVO = walletVOResponse.getClientIpInfoVO();
                                    if (clientIpInfoVO != null) {
                                        if (httpASYNTCPResponseListener != null) {
                                            httpASYNTCPResponseListener.resetSuccess(clientIpInfoVO);
                                        }
                                    }
                                }
                            } else {
                                parseHttpExceptionStatus(responseJson, httpASYNTCPResponseListener);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        cleanResetSANRequest();
                        httpASYNTCPResponseListener.canReset();
//                                                        if (NetWorkTool.connectTimeOut(throwable)) {
                        //如果當前是服務器訪問不到或者連接超時，那麼需要重新切換服務器
                        LogTool.e(TAG, MessageConstants.CONNECT_TIME_OUT);
                        //1：得到新的可用的服务器
                        ServerBean serverBean = ServerTool.checkAvailableServerToSwitch();
                        if (serverBean == null) {
                            ServerTool.needResetServerStatus = true;
                        }
//                                                        } else {
//                                                            LogTool.d(TAG, throwable.getMessage());
//                                                        }
                    }

                    @Override
                    public void onComplete() {
                        cleanResetSANRequest();

                    }
                });
    }

    private static void cleanResetSANRequest() {
        if (disposableReset != null) {
            disposableReset.dispose();
            disposableReset = null;
        }
    }

    /**
     * 解析当前异常的情况
     *
     * @param responseJson
     */
    protected static void parseHttpExceptionStatus(ResponseJson responseJson, HttpASYNTCPResponseListener httpASYNTCPResponseListener) {
        String message = responseJson.getMessage();
        LogTool.e(TAG, message);
        int code = responseJson.getCode();
        if (code == MessageConstants.CODE_3003
                || code == MessageConstants.CODE_2012
                // 2012： public static final String ERROR_WALLET_ADDRESS_INVALID = "Wallet address invalid error.";
                || code == MessageConstants.CODE_2026) {
            //  2026：public static final String ERROR_API_ACCOUNT = "Account is empty.";
            if (httpASYNTCPResponseListener != null) {
                httpASYNTCPResponseListener.resetFailure();
            }
        } else if (JsonTool.isTokenInvalid(code)) {
            if (httpASYNTCPResponseListener != null) {
                httpASYNTCPResponseListener.logout();
            }
        } else if (code == MessageConstants.CODE_2035) {
            //代表TCP没有连接上，这个时候应该停止socket请求，重新请求新的AN
            //            reset();
        } else {
            if (httpASYNTCPResponseListener != null) {
                httpASYNTCPResponseListener.resetFailure();
            }
        }
    }

}
