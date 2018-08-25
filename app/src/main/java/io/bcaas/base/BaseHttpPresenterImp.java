package io.bcaas.base;

import android.os.Handler;
import android.os.Looper;

import io.bcaas.R;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.database.WalletInfo;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.requester.BaseHttpRequester;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.GsonTool;
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
    private String TAG = "BaseHttpPresenterImp";

    private BaseContract.HttpView httpView;
    private BaseHttpRequester baseHttpRequester;
    private Handler handler;

    public BaseHttpPresenterImp(BaseContract.HttpView httpView) {
        this.httpView = httpView;
        handler = new Handler();
        baseHttpRequester = new BaseHttpRequester();
    }

    /*检查当前是否登录*/
    @Override
    public void checkLogin() {
        //获取当前钱包的存储信息
        WalletVO walletVO = new WalletVO(BcaasApplication.getWalletAddress());
        RequestJson requestJson = new RequestJson(walletVO);
        BcaasLog.d(TAG, requestJson);
        RequestBody body = GsonTool.beanToRequestBody(requestJson);
        baseHttpRequester.login(body, new Callback<ResponseJson>() {
            @Override
            public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                BcaasLog.d(TAG, response);
                ResponseJson responseJson = response.body();
                if (responseJson.isSuccess()) {
                    parseLoginInfo(responseJson.getWalletVO());
                } else {
                    httpView.loginFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseJson> call, Throwable t) {
                httpView.loginFailure(t.getMessage());

            }
        });

    }

    /*验证检查当前的「登入」信息*/
    @Override
    public void checkVerify(WalletVO walletVO) {
        RequestJson requestJson = new RequestJson(walletVO);
        BcaasLog.d(TAG, requestJson);
        baseHttpRequester.verify(GsonTool.beanToRequestBody(requestJson), new Callback<ResponseJson>() {
            @Override
            public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                BcaasLog.d(TAG, response.body());
                ResponseJson responseJson = response.body();
                if (responseJson == null) {
                    httpView.noWalletInfo();
                } else {
                    if (responseJson.isSuccess()) {
                        WalletVO walletVONew = responseJson.getWalletVO();
                        saveWalletInfo(walletVONew);
                        httpView.verifySuccess();
                        //当前success的情况有两种
                        int code = responseJson.getCode();
                        if (code == MessageConstants.CODE_200) {

                        } else if (code == MessageConstants.CODE_2014) {
                            if (walletVONew != null) {
                                ClientIpInfoVO clientIpInfoVO = walletVONew.getClientIpInfoVO();
                                if (clientIpInfoVO != null) {
                                    BcaasApplication.setClientIpInfoVO(clientIpInfoVO);
                                }
                            }
                        }

                    } else {
                        //异常情况，作没有钱包处理
                        httpView.noWalletInfo();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseJson> call, Throwable t) {
                BcaasLog.e(TAG, t.getMessage());
                httpView.noWalletInfo();
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
        WalletVO walletVO = new WalletVO();
        walletVO.setWalletAddress(getWalletInfo().getBitcoinAddressStr());
        walletVO.setAccessToken(BcaasApplication.getAccessToken());
        walletVO.setBlockService(BcaasApplication.getBlockService());
        RequestJson requestJson = new RequestJson(walletVO);
        BcaasLog.d(TAG, requestJson);
        baseHttpRequester.resetAuthNode(GsonTool.beanToRequestBody(requestJson), new Callback<ResponseJson>() {
            @Override
            public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                BcaasLog.d(TAG, response.body());
                ResponseJson walletVoResponseJson = response.body();
                if (walletVoResponseJson != null) {
                    if (walletVoResponseJson.isSuccess()) {
                        parseAuthNodeAddress(walletVoResponseJson.getWalletVO());
                    } else {
                        httpView.resetAuthNodeFailure(walletVoResponseJson.getMessage());
                    }
                } else {
                    httpView.resetAuthNodeFailure(walletVoResponseJson.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseJson> call, Throwable t) {
                httpView.resetAuthNodeFailure(t.getMessage());
            }
        });

    }


    /**
     * 解析登录成功之后的信息
     *
     * @param walletVO
     */
    private void parseLoginInfo(WalletVO walletVO) {
        //得到当前回传的信息，存储当前的accessToken
        if (walletVO == null) {
            throw new NullPointerException(" loginPresenterImp parseData walletVO is null");
        }
        String accessToken = walletVO.getAccessToken();
        BcaasLog.d(TAG, accessToken);
        if (StringTool.isEmpty(accessToken)) {
            httpView.loginFailure(getString(R.string.login_failure));
        } else {
            saveWalletInfo(walletVO);
            walletVO.setBlockService(BcaasApplication.getBlockService());
            BcaasApplication.setAccessToken(accessToken);
            httpView.loginSuccess();
            checkVerify(walletVO);
        }
    }

    /*开始定时http请求是否有需要处理的R区块*/
    protected void startToGetWalletWaitingToReceiveBlockLoop() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Looper.prepare();
//                getWalletWaitingToReceiveBlock();
                handler.postDelayed(requestReceiveBlock, 0);
                Looper.loop();
            }
        }.start();

    }

    //暂停已经开始的定时请求
    protected void stopToHttpGetWalletWaitingToReceiveBlock() {
        if (handler != null) {
            handler.removeCallbacks(requestReceiveBlock);
        }
    }

    //"取得未簽章R區塊的Send區塊 &取最新的R區塊 &wallet餘額"
    protected void getWalletWaitingToReceiveBlock() {
        BcaasLog.d(TAG, "getWalletWaitingToReceiveBlock");
        baseHttpRequester.getWalletWaitingToReceiveBlock(GsonTool.beanToRequestBody(getRequestJson()),
                new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                        BcaasLog.d(TAG, response.body());
                        ResponseJson walletResponseJson = response.body();
                        if (walletResponseJson != null) {
                            if (!walletResponseJson.isSuccess()) {
                                httpView.httpGetLatestBlockAndBalanceSuccess();
                            } else {
                                httpView.failure(walletResponseJson.getMessage());
                            }
                        } else {
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseJson> call, Throwable t) {
                        BcaasLog.d(TAG, t.getMessage());
                        httpView.failure(t.getMessage());
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
        WalletInfo walletInfo = getWalletInfo();
        if (walletInfo == null) {
            httpView.failure(context.getString(R.string.walletdata_failure));
            return requestJson;
        }
        WalletVO walletVO = new WalletVO(walletInfo.getBitcoinAddressStr()
                , BcaasApplication.getBlockService(), BcaasApplication.getAccessToken());
        requestJson.setWalletVO(walletVO);
        // TODO: 2018/8/25   第一次发起请求，"PaginationVO"数据为""
        PaginationVO paginationVO = new PaginationVO("");
        requestJson.setPaginationVO(paginationVO);
        BcaasLog.d(TAG, requestJson);
        return requestJson;

    }

    /**
     * 「send」区块之前请求最新的余额信息
     * param transactionAmount 需要交易的金额
     */
    public void getLatestBlockAndBalance() {
        baseHttpRequester.getLastBlockAndBalance(GsonTool.beanToRequestBody(getRequestJson()),
                new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                        BcaasLog.d(TAG, response.body());
                        ResponseJson walletResponseJson = response.body();
                        if (!walletResponseJson.isSuccess()) {
                            httpView.httpGetLatestBlockAndBalanceSuccess();
                        } else {
                            httpView.failure(walletResponseJson.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseJson> call, Throwable t) {
                        BcaasLog.d(TAG, t.getMessage());
                        httpView.failure(t.getMessage());
                        //  如果当前AN的接口请求不通过的时候，应该重新去SFN拉取新AN的数据
                        onResetAuthNodeInfo();

                    }
                });
    }

    //解析处AN的地址
    private void parseAuthNodeAddress(WalletVO walletVO) {
        if (walletVO == null) {
            httpView.failure(context.getString(R.string.null_wallet));
            return;
        }
        ClientIpInfoVO clientIpInfoVO = walletVO.getClientIpInfoVO();
        if (clientIpInfoVO == null) {
            httpView.failure(context.getString(R.string.null_wallet));
            return;
        }
        BcaasLog.d(TAG, clientIpInfoVO);
        BcaasApplication.setClientIpInfoVO(clientIpInfoVO);
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
            BcaasLog.d(TAG, "间隔五分钟 getWalletWaitingToReceiveBlock，检查我是不是五分钟哦！");
            getWalletWaitingToReceiveBlock();
            handler.postDelayed(this, Constants.ValueMaps.REQUEST_RECEIVE_TIME);
        }
    };
}