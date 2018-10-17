package io.bcaas.base;

import java.util.concurrent.TimeUnit;

import io.bcaas.bean.ServerBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.http.callback.BcaasCallback;
import io.bcaas.http.retrofit.RetrofitFactory;
import io.bcaas.requester.BaseHttpRequester;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.NetWorkTool;
import io.bcaas.tools.ServerTool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.ui.contracts.BaseContract;
import io.bcaas.vo.ClientIpInfoVO;
import io.bcaas.vo.PaginationVO;
import io.bcaas.vo.WalletVO;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
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
    //是否开始背景执行拿取未签章块
    private boolean isStart;
    //开始背景执行未签章区块的管理
    private Disposable getReceiveBlockByIntervalDisposable;


    public BaseHttpPresenterImp(BaseContract.HttpView httpView) {
        this.httpView = httpView;
//        handler = new Handler();
        baseHttpRequester = new BaseHttpRequester();
    }

    /**
     * 验证检查当前的「登入」信息
     *
     * @param from 来自哪个需求
     */
    @Override
    public void checkVerify(String from) {
        httpView.showLoading();
        if (!BCAASApplication.isRealNet()) {
            httpView.noNetWork();
            httpView.hideLoading();
            return;
        }
//        getMyIpInfo();
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
                if (responseJson == null) {
                    httpView.hideLoading();
                    httpView.verifyFailure(from);
                } else {
                    int code = responseJson.getCode();
                    if (responseJson.isSuccess()) {
                        httpView.hideLoading();
                        WalletVO walletVONew = responseJson.getWalletVO();
                        //当前success的情况有两种
                        if (code == MessageConstants.CODE_200) {
                            //正常，不需要操作
                            httpView.verifySuccess(from);
                        } else if (code == MessageConstants.CODE_2014) {
                            // 需要替换AN的信息
                            if (walletVONew != null) {
                                ClientIpInfoVO clientIpInfoVO = walletVONew.getClientIpInfoVO();
                                if (clientIpInfoVO != null) {
                                    updateClientIpInfoVO(walletVONew);
                                    //重置AN成功，需要重新連結
                                    httpView.resetAuthNodeSuccess(from);
                                }
                            }
                        } else {
                            // 异常情况
                            httpView.httpExceptionStatus(responseJson);
                        }
                    } else {
                        if (code == MessageConstants.CODE_3003) {
                            //重新获取验证，直到拿到SAN的信息
                            checkVerify(Constants.Verify.RESET);
                        } else {
                            httpView.hideLoading();
                            httpView.httpExceptionStatus(responseJson);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseJson> call, Throwable throwable) {
                LogTool.e(TAG, throwable.getMessage());
                if (NetWorkTool.connectTimeOut(throwable)) {
                    //如果當前是服務器訪問不到或者連接超時，那麼需要重新切換服務器
                    LogTool.d(TAG, MessageConstants.CONNECT_TIME_OUT);
                    //1：得到新的可用的服务器
                    ServerBean serverBean = ServerTool.checkAvailableServerToSwitch();
                    if (serverBean != null) {
                        RetrofitFactory.cleanSFN();
                        checkVerify(Constants.Verify.VERIFY_FAILURE);
                    } else {
                        httpView.hideLoading();
                        ServerTool.needResetServerStatus = true;
                        httpView.verifyFailure(from);
                    }
                } else {
                    httpView.hideLoading();

                    httpView.verifyFailure(from);
                }
            }
        });
    }

    /**
     * 获取当前Wallet Ip info
     */
    private void getMyIpInfo() {
        LogTool.d(TAG, "getMyIpInfo");
        baseHttpRequester.getMyIpInfo(new BcaasCallback<String>() {
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                LogTool.e(TAG, "getMyIpInfo:" + throwable.getMessage());

            }

            @Override
            public void onSuccess(Response<String> response) {
                LogTool.d(TAG, "getMyIpInfo:" + response);

            }

            @Override
            public void onNotFound() {
                LogTool.d(TAG, "getMyIpInfo:onNotFound");
                super.onNotFound();
            }
        });
    }

    /**
     * 重置AN的信息
     * "{""walletVO"":  {
     * ""walletAddress"": String 錢包地址,
     * ""accessToken"": String accessToken,
     * ""blockService"": String 區塊服務名稱,}}"
     *
     * @param
     */
    @Override
    public void onResetAuthNodeInfo(String from) {
        if (!BCAASApplication.isKeepHttpRequest()) {
            return;
        }
        WalletVO walletVO = new WalletVO();
        walletVO.setWalletAddress(BCAASApplication.getWalletAddress());
        walletVO.setAccessToken(BCAASApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN));
        walletVO.setBlockService(BCAASApplication.getBlockService());
        RequestJson requestJson = new RequestJson(walletVO);
        baseHttpRequester.resetAuthNode(GsonTool.beanToRequestBody(requestJson), new Callback<ResponseJson>() {
            @Override
            public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                ResponseJson walletVoResponseJson = response.body();
                if (walletVoResponseJson != null) {
                    if (walletVoResponseJson.isSuccess()) {
                        parseAuthNodeAddress(walletVoResponseJson.getWalletVO(), from);
                    } else {
                        int code = walletVoResponseJson.getCode();
                        if (code == MessageConstants.CODE_3003) {
                            //如果是3003，那么则没有可用的SAN，需要reset一个
                            onResetAuthNodeInfo(Constants.Reset.RESET);
                        } else {
                            httpView.httpExceptionStatus(walletVoResponseJson);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseJson> call, Throwable throwable) {
                if (NetWorkTool.connectTimeOut(throwable)) {
                    //如果當前是服務器訪問不到或者連接超時，那麼需要重新切換服務器
                    LogTool.d(TAG, MessageConstants.CONNECT_TIME_OUT);
                    //1：得到新的可用的服务器
                    ServerBean serverBean = ServerTool.checkAvailableServerToSwitch();
                    if (serverBean != null) {
                        RetrofitFactory.cleanSFN();
                        onResetAuthNodeInfo(Constants.Reset.RESET_FAILURE);
                    } else {
                        ServerTool.needResetServerStatus = true;
                        httpView.resetAuthNodeFailure("", from);
                    }
                } else {
                    httpView.resetAuthNodeFailure(throwable.getMessage(), from);
                }
            }
        });
    }

    /*开始定时http请求是否有需要处理的R区块*/
    @Override
    public void startToGetWalletWaitingToReceiveBlockLoop() {
        // 判断当前是否已经启动背景执行，否则才进行启动
        if (isStart) {
            return;
        }

        LogTool.d(TAG, MessageConstants.socket.START_GET_RECEIVE_BLOCK_BY_INTERVAL_TIMER);
//        int count_time = 30; //总时间
        Observable.interval(0, Constants.ValueMaps.GET_RECEIVE_BLOCK_TIME, TimeUnit.SECONDS)
//                .take(count_time + 1)//设置总共发送的次数
//                .map(new io.reactivex.functions.Function<Long, Long>() {
//                    @Override
//                    public Long apply(Long aLong) throws Exception {
//                        //aLong从0开始
//                        return count_time - aLong;
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getReceiveBlockByIntervalDisposable = d;
                    }

                    @Override
                    public void onNext(Long value) {
                        isStart = true;
                        //开始背景执行
                        getReceiveBlock();
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogTool.e(TAG, e.getMessage());
                        removeGetWalletWaitingToReceiveBlockLoop();
                    }

                    @Override
                    public void onComplete() {
                        removeGetWalletWaitingToReceiveBlockLoop();
                    }
                });
    }

    private void getBalance() {
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
                                    onResetAuthNodeInfo(Constants.Reset.RESET);
                                } else {
                                    httpView.getBalanceFailure();
                                    httpView.httpExceptionStatus(walletResponseJson);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseJson> call, Throwable t) {
                        LogTool.d(TAG, t.getMessage());
                        httpView.getBalanceFailure();
                    }
                });
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
                            httpView.httpGetLastestBlockAndBalanceFailure();
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
                        onResetAuthNodeInfo(Constants.Reset.GET_LASTEST_BLOCK_AND_BALANCE_FAILURE);

                    }
                });
    }

    //解析AN的地址
    private void parseAuthNodeAddress(WalletVO walletVO, String from) {
        if (walletVO == null) {
            httpView.failure(MessageConstants.WALLET_DATA_FAILURE, from);
            return;
        }
        ClientIpInfoVO clientIpInfoVO = walletVO.getClientIpInfoVO();
        if (clientIpInfoVO == null) {
            httpView.failure(MessageConstants.WALLET_DATA_FAILURE, from);
            return;
        }
        updateClientIpInfoVO(walletVO);
        httpView.resetAuthNodeSuccess(from);

    }

    //取消订阅
    public void unSubscribe() {
        LogTool.d(TAG, MessageConstants.UNSUBSCRIBE);
        removeGetWalletWaitingToReceiveBlockLoop();
    }

    /**
     * 拿取未签章区块
     */
    private void getReceiveBlock() {
        httpView.hideLoading();
        LogTool.d(TAG, MessageConstants.START_R_HTTP);
        /*同时获取「余额」*/
        getBalance();
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
                                    onResetAuthNodeInfo(Constants.Reset.RESET);
                                } else if (code == MessageConstants.CODE_2035) {
                                    onResetAuthNodeInfo(Constants.Reset.TCP_NOT_CONNECT);
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
                        onResetAuthNodeInfo(Constants.Reset.GET_WALLET_WAITING_TO_RECEIVE_BLOCK_FAILURE);
                    }
                });
    }

    /**
     * 关闭定时发送器
     */
    @Override
    public void removeGetWalletWaitingToReceiveBlockLoop() {
        if (getReceiveBlockByIntervalDisposable != null) {
            LogTool.i(TAG, MessageConstants.socket.CLOSE_GET_RECEIVE_BLOCK_BY_INTERVAL_TIMER);
            getReceiveBlockByIntervalDisposable.dispose();
        }
    }
}