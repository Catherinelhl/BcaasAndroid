package io.bcaas.base;

import android.os.Handler;
import android.os.Looper;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.constants.SystemConstants;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.http.retrofit.RetrofitFactory;
import io.bcaas.http.tcp.TCPThread;
import io.bcaas.requester.BaseHttpRequester;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.ui.contracts.BaseContract;
import io.bcaas.vo.ClientIpInfoVO;
import io.bcaas.vo.PaginationVO;
import io.bcaas.vo.WalletVO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/22
 * <p>
 * Http 请求查询当前
 */
public class BaseHttpPresenterImp extends BasePresenterImp implements BaseContract.HttpPresenter {
    private String TAG = BaseHttpPresenterImp.class.getSimpleName();

    private BaseContract.HttpView httpView;
    private BaseHttpRequester baseHttpRequester;
    private Handler handler;
    //重置SAN的次数
    private int resetSANCount = 0;
    //重置SAN的轮数
    private int resetSANLoop = 0;
    //请求verify的次数
    private int resetVerifyCount = 0;
    //请求Verify的轮数
    private int resetVerifyLoop = 0;

    public BaseHttpPresenterImp(BaseContract.HttpView httpView) {
        this.httpView = httpView;
        handler = new Handler();
        baseHttpRequester = new BaseHttpRequester();
    }

    /*验证检查当前的「登入」信息*/
    @Override
    public void checkVerify() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Looper.prepare();
                if (resetVerifyCount >= MessageConstants.socket.RESET_AN_INFO) {
                    if (resetVerifyLoop < MessageConstants.socket.RESET_LOOP) {
                        handler.postDelayed(verifyRunnable, Constants.ValueMaps.sleepTime10000);
                        resetVerifyLoop++;
                    } else {
                        resetVerifyLoop = 0;
                    }
                    resetVerifyCount = 0;
                } else {
                    handler.post(verifyRunnable);
                }
                Looper.loop();
            }
        }.start();

    }

    private Runnable verifyRunnable = new Runnable() {
        @Override
        public void run() {
            LogTool.d(TAG, MessageConstants.VERIFY + resetVerifyCount);
            resetVerifyCount++;
            /*组装数据*/
            WalletVO walletVO = new WalletVO();
            walletVO.setWalletAddress(BcaasApplication.getWalletAddress());
            walletVO.setBlockService(BcaasApplication.getBlockService());
            walletVO.setAccessToken(BcaasApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN));
            RequestJson requestJson = new RequestJson(walletVO);
            LogTool.d(TAG, requestJson);
            baseHttpRequester.verify(GsonTool.beanToRequestBody(requestJson), new Callback<ResponseJson>() {
                @Override
                public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                    ResponseJson responseJson = response.body();
                    LogTool.d(TAG, responseJson);
                    httpView.hideLoadingDialog();
                    removeVerifyRunnable();
                    if (responseJson == null) {
                        httpView.verifyFailure();
                    } else {
                        int code = responseJson.getCode();
                        if (responseJson.isSuccess()) {
                            WalletVO walletVONew = responseJson.getWalletVO();
                            //当前success的情况有两种
                            if (code == MessageConstants.CODE_200) {
                                //正常，不需要操作
                                httpView.verifySuccess();
                            } else if (code == MessageConstants.CODE_2014) {
                                // 需要替换AN的信息
                                if (walletVONew != null) {
                                    ClientIpInfoVO clientIpInfoVO = walletVONew.getClientIpInfoVO();
                                    if (clientIpInfoVO != null) {
                                        updateClientIpInfoVO(walletVONew);
                                        //重置AN成功，需要重新連結
                                        httpView.resetAuthNodeSuccess();
                                        httpView.verifySuccess();
                                    }
                                }
                            } else {
                                // 异常情况
                                httpView.httpExceptionStatus(responseJson);
                            }
                        } else {
                            if (code == MessageConstants.CODE_3003) {
                                //重新获取验证，直到拿到SAN的信息
                                // TODO: 2018/9/17 是否一直循环拿去verify信息
                                checkVerify();
                            } else {
                                httpView.httpExceptionStatus(responseJson);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseJson> call, Throwable t) {
                    httpView.hideLoadingDialog();
                    removeVerifyRunnable();
                    if (t instanceof UnknownHostException
                            || t instanceof SocketTimeoutException
                            || t instanceof ConnectException) {
                        //如果當前是服務器訪問不到或者連接超時，那麼需要重新切換服務器
                        LogTool.d(TAG, MessageConstants.CONNECT_TIME_OUT);
                        //1：得到新的可用的服务器
                        boolean isSwitchServer = SystemConstants.switchServer();
                        if (isSwitchServer) {
                            RetrofitFactory.cleanSFN();
                            checkVerify();
                        } else {
                            httpView.verifyFailure();
                        }
                    } else {
                        httpView.verifyFailure();
                    }
                }
            });
        }
    };

    private void removeVerifyRunnable() {
        if (handler != null) {
            handler.removeCallbacks(verifyRunnable);
        }
    }

    /**
     * 重置AN的信息
     * "{""walletVO"":  {
     * ""walletAddress"": String 錢包地址,
     * ""accessToken"": String accessToken,
     * ""blockService"": String 區塊服務名稱,}}"
     */
    @Override
    public void onResetAuthNodeInfo() {
        LogTool.d(TAG, resetSANCount + MessageConstants.ON_RESET_AUTH_NODE_INFO + BcaasApplication.isKeepHttpRequest());
        if (!BcaasApplication.isKeepHttpRequest()) {
            return;
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                Looper.prepare();
                if (resetSANCount >= MessageConstants.socket.RESET_AN_INFO) {
                    if (resetSANLoop < MessageConstants.socket.RESET_LOOP) {
                        handler.postDelayed(resetSANRunnable, Constants.ValueMaps.sleepTime10000);
                        resetSANLoop++;
                    } else {
                        resetSANLoop = 0;
                    }
                    resetSANCount = 0;
                } else {
                    handler.post(resetSANRunnable);
                }
                Looper.loop();
            }
        }.start();
    }

    private Runnable resetSANRunnable = new Runnable() {
        @Override
        public void run() {
            resetSANCount++;
            WalletVO walletVO = new WalletVO();
            walletVO.setWalletAddress(BcaasApplication.getWalletAddress());
            walletVO.setAccessToken(BcaasApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN));
            walletVO.setBlockService(BcaasApplication.getBlockService());
            RequestJson requestJson = new RequestJson(walletVO);
            baseHttpRequester.resetAuthNode(GsonTool.beanToRequestBody(requestJson), new Callback<ResponseJson>() {
                @Override
                public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                    ResponseJson walletVoResponseJson = response.body();
                    removeResetSANRunnable();
                    if (walletVoResponseJson != null) {
                        if (walletVoResponseJson.isSuccess()) {
                            parseAuthNodeAddress(walletVoResponseJson.getWalletVO());
                        } else {
                            int code = walletVoResponseJson.getCode();
                            if (code == MessageConstants.CODE_3003) {
                                //如果是3003，那么则没有可用的SAN，需要reset一个
                                onResetAuthNodeInfo();
                            } else {
                                httpView.httpExceptionStatus(walletVoResponseJson);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseJson> call, Throwable t) {
                    removeResetSANRunnable();
                    if (t instanceof UnknownHostException
                            || t instanceof SocketTimeoutException
                            || t instanceof ConnectException) {
                        //如果當前是服務器訪問不到或者連接超時，那麼需要重新切換服務器
                        LogTool.d(TAG, MessageConstants.CONNECT_TIME_OUT);
                        //1：得到新的可用的服务器
                        boolean isSwitchServer = SystemConstants.switchServer();
                        if (isSwitchServer) {
                            RetrofitFactory.cleanSFN();
                            onResetAuthNodeInfo();
                        } else {
                            httpView.verifyFailure();
                        }
                    } else {
                        httpView.resetAuthNodeFailure(t.getMessage());
                    }
                }
            });
        }
    };

    private void removeResetSANRunnable() {
        if (handler != null) {
            handler.removeCallbacks(resetSANRunnable);
        }
    }

    /*开始定时http请求是否有需要处理的R区块*/
    @Override
    public void startToGetWalletWaitingToReceiveBlockLoop() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Looper.prepare();
                handler.post(getWalletWaitingToReceiveBlockRunnable);
                Looper.loop();
            }
        }.start();

    }

    //"取得未簽章R區塊的Send區塊 &取最新的R區塊 &wallet餘額"
    private Runnable getWalletWaitingToReceiveBlockRunnable = new Runnable() {
        @Override
        public void run() {
            httpView.hideLoadingDialog();
            LogTool.d(TAG, MessageConstants.START_R_HTTP);
            baseHttpRequester.getWalletWaitingToReceiveBlock(GsonTool.beanToRequestBody(getRequestJson()),
                    new Callback<ResponseJson>() {
                        @Override
                        public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                            LogTool.d(TAG, response.body());
                            ResponseJson walletResponseJson = response.body();
                            removeGetWalletWaitingToReceiveBlockRunnable();
                            if (walletResponseJson != null) {
                                int code = walletResponseJson.getCode();
                                if (walletResponseJson.isSuccess()) {
                                    httpView.httpGetWalletWaitingToReceiveBlockSuccess();
                                } else {
                                    if (code == MessageConstants.CODE_3003) {
                                        onResetAuthNodeInfo();
                                        removeGetWalletWaitingToReceiveBlockRunnable();
                                    } else {
                                        httpView.httpExceptionStatus(walletResponseJson);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseJson> call, Throwable t) {
                            LogTool.d(TAG, t.getMessage());
                            httpView.httpGetWalletWaitingToReceiveBlockFailure();
                            //  如果当前AN的接口请求不通过的时候，应该重新去SFN拉取新AN的数据
                            onResetAuthNodeInfo();
                            removeGetWalletWaitingToReceiveBlockRunnable();
                        }
                    });
            handler.postDelayed(this, Constants.ValueMaps.REQUEST_RECEIVE_TIME);
        }
    };

    @Override
    public void stopTCP() {
        LogTool.d(TAG, MessageConstants.STOP_TCP);
        removeGetWalletWaitingToReceiveBlockRunnable();
        TCPThread.kill(true);
    }

    @Override
    public void removeGetWalletWaitingToReceiveBlockRunnable() {
        LogTool.d(TAG, MessageConstants.REMOVE_GET_WALLET_R_BLOCK);
        if (handler != null) {
            handler.removeCallbacks(getWalletWaitingToReceiveBlockRunnable);
        }
    }

    /**
     * 获取需要请求的数据
     * "{
     * walletVO:{        accessToken : String,
     * blockService : String,
     * walletAddress : String
     * },
     * paginationVO:{
     * nextObjectId : String
     * }
     * }"
     */
    private RequestJson getRequestJson() {
        RequestJson requestJson = new RequestJson();
        WalletVO walletVO = new WalletVO(BcaasApplication.getWalletAddress(),
                BcaasApplication.getBlockService(),
                BcaasApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN));
        requestJson.setWalletVO(walletVO);
        PaginationVO paginationVO = new PaginationVO(BcaasApplication.getNextObjectId());
        requestJson.setPaginationVO(paginationVO);
        LogTool.d(TAG, GsonTool.string(requestJson));
        return requestJson;

    }


    /**
     * 「send」区块之前请求最新的余额信息
     * param transactionAmount 需要交易的金额
     */
    @Override
    public void getLatestBlockAndBalance() {
        httpView.showLoadingDialog();
        if (!BcaasApplication.isRealNet()) {
            httpView.noNetWork();
            httpView.hideLoadingDialog();
            return;
        }
        baseHttpRequester.getLastBlockAndBalance(GsonTool.beanToRequestBody(getRequestJson()),
                new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                        ResponseJson walletResponseJson = response.body();
                        LogTool.d(TAG, walletResponseJson);
                        if (walletResponseJson == null) {
                            httpView.hideLoadingDialog();
                            httpView.responseDataError();
                            return;
                        } else {
                            if (walletResponseJson.isSuccess()) {
                                httpView.httpGetLastestBlockAndBalanceSuccess();
                            } else {
                                httpView.hideLoadingDialog();
                                httpView.httpExceptionStatus(walletResponseJson);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseJson> call, Throwable t) {
                        httpView.hideLoadingDialog();
                        httpView.httpGetLastestBlockAndBalanceFailure();
                        //  如果当前AN的接口请求不通过的时候，应该重新去SFN拉取新AN的数据
                        onResetAuthNodeInfo();

                    }
                });
    }

    //解析AN的地址
    private void parseAuthNodeAddress(WalletVO walletVO) {
        if (walletVO == null) {
            httpView.failure(MessageConstants.WALLET_DATA_FAILURE);
            return;
        }
        ClientIpInfoVO clientIpInfoVO = walletVO.getClientIpInfoVO();
        if (clientIpInfoVO == null) {
            httpView.failure(MessageConstants.WALLET_DATA_FAILURE);
            return;
        }
        updateClientIpInfoVO(walletVO);
        httpView.resetAuthNodeSuccess();

    }

    //取消订阅
    public void unSubscribe() {
        LogTool.d(TAG, MessageConstants.UNSUBSCRIBE);
        removeResetSANRunnable();
        removeVerifyRunnable();
        removeGetWalletWaitingToReceiveBlockRunnable();
    }
}