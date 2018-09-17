package io.bcaas.base;

import android.os.Handler;
import android.os.Looper;

import java.util.List;

import io.bcaas.bean.SeedFullNodeBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.constants.SystemConstants;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.http.tcp.TCPThread;
import io.bcaas.requester.BaseHttpRequester;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.tools.StringTool;
import io.bcaas.ui.contracts.BaseContract;
import io.bcaas.vo.ClientIpInfoVO;
import io.bcaas.vo.PaginationVO;
import io.bcaas.vo.WalletVO;
import okhttp3.RequestBody;
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

    public BaseHttpPresenterImp(BaseContract.HttpView httpView) {
        this.httpView = httpView;
        handler = new Handler();
        baseHttpRequester = new BaseHttpRequester();
    }

    /**
     * 登入
     */
    @Override
    public void toLogin() {
        httpView.showLoadingDialog();
        if (!BcaasApplication.isRealNet()) {
            httpView.noNetWork();
            httpView.hideLoadingDialog();
            return;
        }
        //获取当前钱包的地址
        WalletVO walletVO = new WalletVO(BcaasApplication.getWalletAddress());
        RequestJson requestJson = new RequestJson(walletVO);
        LogTool.d(TAG, requestJson);
        RequestBody body = GsonTool.beanToRequestBody(requestJson);
        baseHttpRequester.login(body, new Callback<ResponseJson>() {
            @Override
            public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                ResponseJson responseJson = response.body();
                if (responseJson.isSuccess()) {
                    parseLoginInfo(responseJson.getWalletVO());
                } else {
                    LogTool.d(TAG, response.message());
                    httpView.loginFailure();
                }
                httpView.hideLoadingDialog();

            }

            @Override
            public void onFailure(Call<ResponseJson> call, Throwable t) {
                LogTool.d(TAG, t.getMessage());
                LogTool.d(TAG, t.getCause());
                LogTool.d(TAG, call.toString());
                httpView.loginFailure();
                httpView.hideLoadingDialog();

            }
        });

    }

    /*验证检查当前的「登入」信息*/
    @Override
    public void checkVerify() {
        httpView.showLoadingDialog();
        if (!BcaasApplication.isRealNet()) {
            httpView.noNetWork();
            httpView.hideLoadingDialog();
            return;
        }
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
                httpView.hideLoadingDialog();
                if (responseJson == null) {
                    httpView.verifyFailure();
                } else {
                    if (responseJson.isSuccess()) {
                        WalletVO walletVONew = responseJson.getWalletVO();
                        //当前success的情况有两种
                        int code = responseJson.getCode();
                        if (code == MessageConstants.CODE_200) {
                            //正常，不需要操作
                            httpView.verifySuccess();
                        } else if (code == MessageConstants.CODE_2014) {
                            // 需要替换AN的信息
                            if (walletVONew != null) {
                                ClientIpInfoVO clientIpInfoVO = walletVONew.getClientIpInfoVO();
                                if (clientIpInfoVO != null) {
                                    updateClientIpInfoVO(walletVO);
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
                        httpView.httpExceptionStatus(responseJson);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseJson> call, Throwable t) {
                httpView.hideLoadingDialog();
                httpView.verifyFailure();
            }
        });
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
        if (resetSANCount >= MessageConstants.socket.RESET_AN_INFO) {
            handler.postDelayed(resetSANRunnable, Constants.ValueMaps.sleepTime10000);
            resetSANCount = 0;
        } else {
            handler.post(resetSANRunnable);
        }
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
                    if (walletVoResponseJson != null) {
                        if (walletVoResponseJson.isSuccess()) {
                            if (handler != null) {
                                handler.removeCallbacks(resetSANRunnable);
                            }
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
                    httpView.resetAuthNodeFailure(t.getMessage());
                }
            });
        }
    };


    /**
     * 解析登录成功之后的信息
     *
     * @param walletVO
     */
    private void parseLoginInfo(WalletVO walletVO) {
        //得到当前回传的信息，存储当前的accessToken
        if (walletVO == null) {
            httpView.noData();
            return;
        }
        String accessToken = walletVO.getAccessToken();
        if (StringTool.isEmpty(accessToken)) {
            httpView.noData();
        } else {
            addSeedFullNodeList(walletVO.getSeedFullNodeList());
            BcaasApplication.setStringToSP(Constants.Preference.ACCESS_TOKEN, accessToken);
            httpView.loginSuccess();
            checkVerify();
        }
    }

    /**
     * 得到登录返回的可用的全节点数据,然后去重复，保存
     *
     * @param seedFullNodeBeanList
     */
    private void addSeedFullNodeList(List<SeedFullNodeBean> seedFullNodeBeanList) {
        for (SeedFullNodeBean seedList : seedFullNodeBeanList) {
            SystemConstants.add(seedList.getIp(), seedList.getPort());
        }
    }

    /*开始定时http请求是否有需要处理的R区块*/
    protected void startToGetWalletWaitingToReceiveBlockLoop() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Looper.prepare();
                handler.postDelayed(requestReceiveBlock, 0);
                Looper.loop();
            }
        }.start();

    }

    @Override
    public void stopTCP() {
        LogTool.d(TAG, MessageConstants.STOP_TCP);
        TCPThread.stopSocket = true;
        TCPThread.kill();
        stopToHttpGetWalletWaitingToReceiveBlock();
    }


    //暂停已经开始的定时请求
    protected void stopToHttpGetWalletWaitingToReceiveBlock() {
        LogTool.d(TAG, MessageConstants.STOP_R_HTTP);
        if (handler != null) {
            handler.removeCallbacks(requestReceiveBlock);
        }
    }

    //"取得未簽章R區塊的Send區塊 &取最新的R區塊 &wallet餘額"
    protected void getWalletWaitingToReceiveBlock() {
        LogTool.d(TAG, MessageConstants.START_R_HTTP);
        baseHttpRequester.getWalletWaitingToReceiveBlock(GsonTool.beanToRequestBody(getRequestJson()),
                new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                        LogTool.d(TAG, response.body());
                        ResponseJson walletResponseJson = response.body();
                        if (walletResponseJson != null) {
                            if (walletResponseJson.isSuccess()) {
                                httpView.httpGetWalletWaitingToReceiveBlockSuccess();
                            } else {
                                httpView.httpExceptionStatus(walletResponseJson);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseJson> call, Throwable t) {
                        LogTool.d(TAG, t.getMessage());
                        httpView.httpGetWalletWaitingToReceiveBlockFailure();
                        //  如果当前AN的接口请求不通过的时候，应该重新去SFN拉取新AN的数据
                        onResetAuthNodeInfo();

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
        WalletVO walletVO = new WalletVO(BcaasApplication.getWalletAddress(),
                BcaasApplication.getBlockService(),
                BcaasApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN));
        requestJson.setWalletVO(walletVO);
        // TODO: 2018/8/25   第一次发起请求，"PaginationVO"数据为""
        PaginationVO paginationVO = new PaginationVO("");
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
        if (handler != null) {
            handler.removeCallbacks(requestReceiveBlock);
        }
    }

    private Runnable requestReceiveBlock = new Runnable() {
        @Override
        public void run() {
            httpView.hideLoadingDialog();
            getWalletWaitingToReceiveBlock();
            handler.postDelayed(this, Constants.ValueMaps.REQUEST_RECEIVE_TIME);
        }
    };
}