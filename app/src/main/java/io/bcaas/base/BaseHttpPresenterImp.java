package io.bcaas.base;

import io.bcaas.bean.ServerBean;
import io.bcaas.constants.MessageConstants;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.http.retrofit.RetrofitFactory;
import io.bcaas.requester.BaseHttpRequester;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.NetWorkTool;
import io.bcaas.tools.ServerTool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.tools.gson.JsonTool;
import io.bcaas.ui.contracts.BaseContract;
import io.bcaas.vo.ClientIpInfoVO;
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

    public BaseHttpPresenterImp(BaseContract.HttpView httpView) {
        this.httpView = httpView;
        baseHttpRequester = new BaseHttpRequester();
    }

    /**
     * 验证当前Wallet的token信息，以及得到新的SAN信息
     * <p>
     * 目前调用此接口的有：
     * 1：每次切换区块服务，包括「login」
     * 2：每次「send」交易之前需要验证
     * 3：背景执行拿取「getBalance」需要（目前这个是通过调用MasterService）里面的verify
     *
     * @param from 来自哪个需求
     */
    @Override
    public void checkVerify(String from) {
        LogTool.d(TAG, MessageConstants.Verify.TAG + from);
        //获取需要发送给服务器的资讯
        RequestJson requestJson = JsonTool.getRequestJson();
        if (requestJson == null) {
            httpView.verifyFailure(from);
            return;
        }
        //判断当前是否有网络
        if (!BCAASApplication.isRealNet()) {
            httpView.noNetWork();
            return;
        }
        LogTool.d(TAG, MessageConstants.Verify.TAG + requestJson);
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
                                    BCAASApplication.setClientIpInfoVO(clientIpInfoVO);
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
                            checkVerify(from);
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
                        checkVerify(from);
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
        LogTool.i(TAG, MessageConstants.Reset.REQUEST_JSON + from);
        RequestJson requestJson = JsonTool.getRequestJson();
        if (requestJson == null) {
            httpView.resetAuthNodeFailure(MessageConstants.Empty, from);
            return;
        }
        if (!BCAASApplication.isKeepHttpRequest()) {
            return;
        }
        LogTool.i(TAG, MessageConstants.Reset.REQUEST_JSON + requestJson);
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
                            onResetAuthNodeInfo(from);
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
                        onResetAuthNodeInfo(from);
                    } else {
                        ServerTool.needResetServerStatus = true;
                        httpView.resetAuthNodeFailure(MessageConstants.Empty, from);
                    }
                } else {
                    httpView.resetAuthNodeFailure(throwable.getMessage(), from);
                }
            }
        });
    }

    /**
     * 「send」区块之前请求最新的余额信息
     * param transactionAmount 需要交易的金额
     */
    @Override
    public void getLatestBlockAndBalance() {
        if (!BCAASApplication.isRealNet()) {
            httpView.noNetWork();
            httpView.hideLoading();
            return;
        }

        RequestJson requestJson = JsonTool.getRequestJson();
        if (requestJson == null) {
            LogTool.i(TAG, MessageConstants.GET_BALANCE_DATA_ERROR);
            return;
        }
        httpView.showLoading();
        LogTool.i(TAG, MessageConstants.GET_LATEST_BLOCK_AND_BALANCE + requestJson);
        baseHttpRequester.getLastBlockAndBalance(GsonTool.beanToRequestBody(requestJson),
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
//                          如果当前AN的接口请求不通过的时候，应该重新去SFN拉取新AN的数据
//                        onResetAuthNodeInfo(Constants.Reset.GET_LASTEST_BLOCK_AND_BALANCE_FAILURE);

                    }
                });
    }

    //解析AN的地址
    private void parseAuthNodeAddress(WalletVO walletVO, String from) {
        if (walletVO == null) {
            httpView.resetAuthNodeFailure(MessageConstants.WALLET_DATA_FAILURE, from);
            return;
        }
        ClientIpInfoVO clientIpInfoVO = walletVO.getClientIpInfoVO();
        if (clientIpInfoVO == null) {
            httpView.resetAuthNodeFailure(MessageConstants.WALLET_DATA_FAILURE, from);
            return;
        }
        BCAASApplication.setClientIpInfoVO(clientIpInfoVO);
        httpView.resetAuthNodeSuccess(from);

    }

    //取消订阅
    public void unSubscribe() {
        LogTool.d(TAG, MessageConstants.UNSUBSCRIBE);
    }

}