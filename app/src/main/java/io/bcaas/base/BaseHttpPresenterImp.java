package io.bcaas.base;

import android.os.Handler;
import android.os.Looper;

import io.bcaas.bean.ServerBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.http.retrofit.RetrofitFactory;
import io.bcaas.http.tcp.TCPThread;
import io.bcaas.requester.BaseHttpRequester;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.NetWorkTool;
import io.bcaas.tools.ServerTool;
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
    //Reset的Thread
    private Thread resetThread;
    //Reset的Looper
    private Looper resetLooper;
    //Verify的Thread
    private Thread verifyThread;
    //Verify的Looper
    private Looper verifyLooper;
    //getWalletWaitingToReceiveBlock 的Thread
    private Thread getWalletWaitingToReceiveBlockThread;
    //getWalletWaitingToReceiveBlock 的Looper
    private Looper getWalletWaitingToReceiveBlockLooper;

    //getBalance 的Thread
    private Thread getBalanceThread;
    //getBalance 的Looper
    private Looper getBalanceLooper;

    public BaseHttpPresenterImp(BaseContract.HttpView httpView) {
        this.httpView = httpView;
        handler = new Handler();
        baseHttpRequester = new BaseHttpRequester();
    }

    /**
     * 验证检查当前的「登入」信息
     *
     * @param isAuto 是否是自動重新驗證，如果是，就需要進行一個重試次數的計數
     */
    @Override
    public void checkVerify(boolean isAuto) {
        verifyThread = new Thread() {
            @Override
            public void run() {
                super.run();
                Looper.prepare();
                verifyLooper = Looper.myLooper();
                if (isAuto) {
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
                } else {
                    resetVerifyCount = 0;
                    handler.post(verifyRunnable);
                }
                Looper.loop();
            }
        };
        verifyThread.start();

    }

    private Runnable verifyRunnable = new Runnable() {
        @Override
        public void run() {
            LogTool.d(TAG, MessageConstants.VERIFY + resetVerifyCount);
            resetVerifyCount++;
            /*组装数据*/
            WalletVO walletVO = new WalletVO();
            walletVO.setWalletAddress(BCAASApplication.getWalletAddress());
            walletVO.setBlockService(BCAASApplication.getBlockService());
            walletVO.setAccessToken(BCAASApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN));
            RequestJson requestJson = new RequestJson(walletVO);
            LogTool.d(TAG, requestJson);
            baseHttpRequester.verify(GsonTool.beanToRequestBody(requestJson), new Callback<ResponseJson>() {
                @Override
                public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                    ResponseJson responseJson = response.body();
                    LogTool.d(TAG, responseJson);
                    httpView.hideLoading();
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
                                httpView.verifySuccess(false);
                            } else if (code == MessageConstants.CODE_2014) {
                                // 需要替换AN的信息
                                if (walletVONew != null) {
                                    ClientIpInfoVO clientIpInfoVO = walletVONew.getClientIpInfoVO();
                                    if (clientIpInfoVO != null) {
                                        updateClientIpInfoVO(walletVONew);
                                        //重置AN成功，需要重新連結
                                        httpView.resetAuthNodeSuccess();
                                        httpView.verifySuccess(true);
                                    }
                                }
                            } else {
                                // 异常情况
                                httpView.httpExceptionStatus(responseJson);
                            }
                        } else {
                            if (code == MessageConstants.CODE_3003) {
                                //重新获取验证，直到拿到SAN的信息
                                checkVerify(true);
                            } else {
                                httpView.httpExceptionStatus(responseJson);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseJson> call, Throwable throwable) {
                    httpView.hideLoading();
                    LogTool.e(TAG, throwable.getMessage());
                    removeVerifyRunnable();
                    if (NetWorkTool.connectTimeOut(throwable)) {
                        //如果當前是服務器訪問不到或者連接超時，那麼需要重新切換服務器
                        LogTool.d(TAG, MessageConstants.CONNECT_TIME_OUT);
                        //1：得到新的可用的服务器
                        ServerBean serverBean = ServerTool.checkAvailableServerToSwitch();
                        if (serverBean != null) {
                            RetrofitFactory.cleanSFN();
                            checkVerify(true);
                        } else {
                            ServerTool.needResetServerStatus = true;
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
        LogTool.d(TAG, MessageConstants.REMOVE_VERIFY_RUNNABLE + verifyLooper);
        if (handler != null) {
            handler.removeCallbacks(verifyRunnable);
        }
        if (verifyLooper != null) {
            verifyLooper.quit();
            verifyLooper = null;
        }
        verifyThread = null;
    }

    /**
     * 重置AN的信息
     * "{""walletVO"":  {
     * ""walletAddress"": String 錢包地址,
     * ""accessToken"": String accessToken,
     * ""blockService"": String 區塊服務名稱,}}"
     *
     * @param isAuto 是否是自動驗證，如果是自動reset，就需要開始對reset進行計數
     */
    @Override
    public void onResetAuthNodeInfo(boolean isAuto) {
        LogTool.d(TAG, resetSANCount + MessageConstants.ON_RESET_AUTH_NODE_INFO + BCAASApplication.isKeepHttpRequest());
        if (!BCAASApplication.isKeepHttpRequest()) {
            return;
        }
        removeResetSANRunnable();
        resetThread = new Thread() {
            @Override
            public void run() {
                super.run();
                Looper.prepare();
                resetLooper = Looper.myLooper();
                if (isAuto) {
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
                } else {
                    resetSANCount = 0;
                    handler.post(resetSANRunnable);
                }
                Looper.loop();
            }
        };
        resetThread.start();
    }

    private Runnable resetSANRunnable = new Runnable() {
        @Override
        public void run() {
            resetSANCount++;
            WalletVO walletVO = new WalletVO();
            walletVO.setWalletAddress(BCAASApplication.getWalletAddress());
            walletVO.setAccessToken(BCAASApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN));
            walletVO.setBlockService(BCAASApplication.getBlockService());
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
                                onResetAuthNodeInfo(true);
                            } else {
                                httpView.httpExceptionStatus(walletVoResponseJson);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseJson> call, Throwable throwable) {
                    removeResetSANRunnable();
                    if (NetWorkTool.connectTimeOut(throwable)) {
                        //如果當前是服務器訪問不到或者連接超時，那麼需要重新切換服務器
                        LogTool.d(TAG, MessageConstants.CONNECT_TIME_OUT);
                        //1：得到新的可用的服务器
                        ServerBean serverBean = ServerTool.checkAvailableServerToSwitch();
                        if (serverBean != null) {
                            RetrofitFactory.cleanSFN();
                            onResetAuthNodeInfo(true);
                        } else {
                            ServerTool.needResetServerStatus = true;
                            httpView.verifyFailure();
                        }
                    } else {
                        httpView.resetAuthNodeFailure(throwable.getMessage());
                    }
                }
            });
        }
    };

    private void removeResetSANRunnable() {
        LogTool.d(TAG, MessageConstants.REMOVE_RESETSAN_RUNNABLE + resetLooper);
        if (handler != null) {
            handler.removeCallbacks(resetSANRunnable);
        }
        if (resetLooper != null) {
            resetLooper.quit();
            resetLooper = null;
        }
        resetThread = null;
    }

    /*开始定时http请求是否有需要处理的R区块*/
    @Override
    public void startToGetWalletWaitingToReceiveBlockLoop() {
        removeGetWalletWaitingToReceiveBlockRunnable();
        //拿去未签章块
        getWalletWaitingToReceiveBlockThread = new Thread() {
            @Override
            public void run() {
                super.run();
                Looper.prepare();
                getWalletWaitingToReceiveBlockLooper = Looper.myLooper();
                handler.post(getWalletWaitingToReceiveBlockRunnable);
                Looper.loop();
            }
        };
        getWalletWaitingToReceiveBlockThread.start();
        //同時開始請求餘額
        getBalanceThread = new Thread() {
            @Override
            public void run() {
                super.run();
                Looper.prepare();
                getBalanceLooper = Looper.myLooper();
                handler.post(getBalanceRunnable);
                Looper.loop();
            }
        };
        getBalanceThread.start();

    }

    //"取得未簽章R區塊的Send區塊 &取最新的R區塊 &wallet餘額"
    private Runnable getWalletWaitingToReceiveBlockRunnable = new Runnable() {
        @Override
        public void run() {
            httpView.hideLoading();
            LogTool.d(TAG, MessageConstants.START_R_HTTP);
            baseHttpRequester.getWalletWaitingToReceiveBlock(GsonTool.beanToRequestBody(getRequestJson()),
                    new Callback<ResponseJson>() {
                        @Override
                        public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                            LogTool.d(TAG, response.body());
                            ResponseJson walletResponseJson = response.body();
                            if (walletResponseJson != null) {
                                int code = walletResponseJson.getCode();
                                if (walletResponseJson.isSuccess()) {
                                    httpView.httpGetWalletWaitingToReceiveBlockSuccess();
                                } else {
                                    if (code == MessageConstants.CODE_3003) {
                                        onResetAuthNodeInfo(false);
                                        removeGetWalletWaitingToReceiveBlockRunnable();
                                    } else if (code == MessageConstants.CODE_2035) {
                                        removeGetWalletWaitingToReceiveBlockRunnable();
                                        onResetAuthNodeInfo(false);
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
                            //因为考虑到会影响到交易，所以不停止当前请求，也不用reset
                            removeGetWalletWaitingToReceiveBlockRunnable();
                            onResetAuthNodeInfo(false);
                        }
                    });
            handler.postDelayed(this, Constants.ValueMaps.REQUEST_RECEIVE_TIME);
        }
    };
    //單獨獲取餘額
    private Runnable getBalanceRunnable = new Runnable() {
        @Override
        public void run() {
            httpView.hideLoading();
            WalletVO walletVO = new WalletVO(BCAASApplication.getWalletAddress(),
                    BCAASApplication.getBlockService(),
                    BCAASApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN));
            RequestJson requestJson = new RequestJson(walletVO);
            LogTool.d(TAG, MessageConstants.GET_BALANCE + requestJson);
            baseHttpRequester.getBalance(GsonTool.beanToRequestBody(requestJson),
                    new Callback<ResponseJson>() {
                        @Override
                        public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                            LogTool.d(TAG, response.body());
                            ResponseJson walletResponseJson = response.body();
                            if (walletResponseJson != null) {
                                int code = walletResponseJson.getCode();
                                if (walletResponseJson.isSuccess()) {
                                    httpView.getBalanceSuccess();
                                } else {
                                    if (code == MessageConstants.CODE_3003) {
                                        onResetAuthNodeInfo(false);
                                    } else if (code == MessageConstants.CODE_2035) {
                                        removeGetBalanceRunnable();
                                        onResetAuthNodeInfo(false);
                                    } else {
                                        httpView.httpExceptionStatus(walletResponseJson);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseJson> call, Throwable t) {
                            LogTool.d(TAG, t.getMessage());
                            httpView.getBalanceFailure();
//                            //因为考虑到会影响到交易，所以不停止当前请求，也不用reset
//                            removeGetBalanceRunnable();
//                            onResetAuthNodeInfo(false);
                        }
                    });
            handler.postDelayed(this, Constants.ValueMaps.REQUEST_BALANCE_TIME);
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
        LogTool.d(TAG, MessageConstants.REMOVE_GET_WALLET_R_BLOCK + getWalletWaitingToReceiveBlockRunnable);
        if (handler != null) {
            handler.removeCallbacks(getWalletWaitingToReceiveBlockRunnable);
        }
        if (getWalletWaitingToReceiveBlockLooper != null) {
            getWalletWaitingToReceiveBlockLooper.quit();
            getWalletWaitingToReceiveBlockLooper = null;
        }
        getWalletWaitingToReceiveBlockThread = null;
        removeGetBalanceRunnable();
    }

    //移除获取余额的背景执行
    public void removeGetBalanceRunnable() {
        LogTool.d(TAG, MessageConstants.REMOVE_GET_BALANCE + getBalanceRunnable);
        if (handler != null) {
            handler.removeCallbacks(getBalanceRunnable);
        }
        if (getBalanceLooper != null) {
            getBalanceLooper.quit();
            getBalanceLooper = null;
        }
        getBalanceThread = null;
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
        // 组装钱包地址/区块服务/token信息
        WalletVO walletVO = new WalletVO(BCAASApplication.getWalletAddress(),
                BCAASApplication.getBlockService(),
                BCAASApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN));
        requestJson.setWalletVO(walletVO);
        PaginationVO paginationVO = new PaginationVO(BCAASApplication.getNextObjectId());
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
        httpView.showLoading();
        if (!BCAASApplication.isRealNet()) {
            httpView.noNetWork();
            httpView.hideLoading();
            return;
        }
        baseHttpRequester.getLastBlockAndBalance(GsonTool.beanToRequestBody(getRequestJson()),
                new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                        ResponseJson walletResponseJson = response.body();
                        LogTool.d(TAG, walletResponseJson);
                        if (walletResponseJson == null) {
                            httpView.hideLoading();
                            httpView.responseDataError();
                            return;
                        } else {
                            if (walletResponseJson.isSuccess()) {
                                httpView.httpGetLastestBlockAndBalanceSuccess();
                            } else {
                                httpView.hideLoading();
                                httpView.httpExceptionStatus(walletResponseJson);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseJson> call, Throwable t) {
                        httpView.hideLoading();
                        httpView.httpGetLastestBlockAndBalanceFailure();
                        //  如果当前AN的接口请求不通过的时候，应该重新去SFN拉取新AN的数据
                        onResetAuthNodeInfo(false);

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