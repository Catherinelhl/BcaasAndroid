package io.bcaas.base;

import android.os.Handler;
import android.os.Looper;

import io.bcaas.R;
import io.bcaas.constants.Constants;
import io.bcaas.database.WalletInfo;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.interactor.AuthNodeInteractor;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.GsonTool;
import io.bcaas.vo.ClientIpInfoVO;
import io.bcaas.vo.PaginationVO;
import io.bcaas.vo.WalletVO;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.subscriptions.CompositeSubscription;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/22
 * <p>
 * 请求AuthNode查询当前
 */
public class BaseAuthNodePresenterImp extends BasePresenterImp {
    private String TAG = "BaseAuthNodePresenterImp";

    private BaseAuthNodeView view;
    private AuthNodeInteractor authNodeInteractor;
    private CompositeSubscription compositeSubscription;
    private Disposable disposable;
    private Handler handler;

    public BaseAuthNodePresenterImp(BaseAuthNodeView view) {
        this.view = view;
        handler = new Handler();
        authNodeInteractor = new AuthNodeInteractor();
        compositeSubscription = new CompositeSubscription();
    }

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

    //"取得未簽章R區塊的Send區塊 &取最新的R區塊 &wallet餘額"
    protected void getWalletWaitingToReceiveBlock() {
        authNodeInteractor.getWalletWaitingToReceiveBlock(GsonTool.beanToRequestBody(getRequestJson()),
                new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                        BcaasLog.d(TAG, response.body());
                        ResponseJson walletResponseJson = response.body();
                        if (walletResponseJson != null) {
                            if (!walletResponseJson.isSuccess()) {
                                view.httpANSuccess();
                            } else {
                                view.failure(walletResponseJson.getMessage());
                            }
                        } else {
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseJson> call, Throwable t) {
                        BcaasLog.d(TAG, t.getMessage());
                        view.failure(t.getMessage());
                        //  如果当前AN的接口请求不通过的时候，应该重新去SFN拉取新AN的数据
                        resetAuthNodeInfo();

                    }
                });
    }

    //获取需要请求的数据
//    "{
//    walletVO:{
//        accessToken : String,
//                blockService : String,
//                walletAddress : String
//    },
//    paginationVO:{
//        nextObjectId : String
//    }
//}"
    private RequestJson getRequestJson() {
        RequestJson requestJson = new RequestJson();
        WalletInfo walletInfo = getWalletInfo();
        if (walletInfo == null) {
            view.failure(context.getString(R.string.walletdata_failure));
            return requestJson;
        }
        WalletVO walletVO = new WalletVO(walletInfo.getBitcoinAddressStr()
                , BcaasApplication.getBlockService(), BcaasApplication.getAccessToken());
        requestJson.setWalletVO(walletVO);
        PaginationVO paginationVO=new PaginationVO("");
        requestJson.setPaginationVO(paginationVO);
        BcaasLog.d(TAG,requestJson);
        return requestJson;

    }

    //「send」区块之前请求最新的余额信息
    public void getLatestBlockAndBalance() {
        authNodeInteractor.getLatesBlockAndBalance(GsonTool.beanToRequestBody(getRequestJson()),
                new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                        BcaasLog.d(TAG, response.body());
                        ResponseJson walletResponseJson = response.body();
                        if (!walletResponseJson.isSuccess()) {
                            view.httpANSuccess();
                        } else {
                            view.failure(walletResponseJson.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseJson> call, Throwable t) {
                        BcaasLog.d(TAG, t.getMessage());
                        view.failure(t.getMessage());
                        //  如果当前AN的接口请求不通过的时候，应该重新去SFN拉取新AN的数据
                        resetAuthNodeInfo();

                    }
                });
    }

    //重置AN信息
//    "{
//            ""walletVO"":
//    {
//        ""walletAddress"": String 錢包地址,
//        ""accessToken"": String accessToken,
//        ""blockService"": String 區塊服務名稱,
//    }
//}"
    public void resetAuthNodeInfo() {
        final WalletVO walletVO = new WalletVO();
        walletVO.setWalletAddress(getWalletInfo().getBitcoinAddressStr());
        walletVO.setAccessToken(BcaasApplication.getAccessToken());
        walletVO.setBlockService(BcaasApplication.getBlockService());
        RequestJson requestJson = new RequestJson(walletVO);
        BcaasLog.d(TAG, requestJson);
        authNodeInteractor.resetAuthNode(GsonTool.beanToRequestBody(requestJson), new Callback<ResponseJson>() {
            @Override
            public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                BcaasLog.d(TAG, response.body());
                ResponseJson walletVoResponseJson = response.body();
                if (walletVoResponseJson.isSuccess()) {
                    parseAuthNodeAddress(walletVoResponseJson.getWalletVO());
                } else {
                    // TODO: 2018/8/23 是否需要重新请求
//                    view.resetAuthNodeFailure(walletVoResponseJson.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseJson> call, Throwable t) {
                view.resetAuthNodeFailure(t.getMessage());
            }
        });


    }

    //解析处AN的地址
    private void parseAuthNodeAddress(WalletVO walletVO) {
        if (walletVO == null) {
            view.failure(context.getString(R.string.null_wallet));
            return;
        }
        ClientIpInfoVO clientIpInfoVO = walletVO.getClientIpInfoVO();
        if (clientIpInfoVO == null) {
            view.failure(context.getString(R.string.null_wallet));
            return;
        }
        BcaasLog.d(TAG, clientIpInfoVO);
        //1:遍历得到数据库ANClientIpInfo里面的数据
        //2：根据钱包地址得到与之匹配的AN ip信息
        //3：组装Ip+port，以备An访问
        //4：重新登入以及reset之后需要重新存储
        BcaasApplication.setClientIpInfoVO(clientIpInfoVO);
        view.resetAuthNodeSuccess();

    }

    //取消订阅
    public void unSubscribe() {
        if (handler != null) {
            handler.removeCallbacks(requestReceiveBlock);
        }
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
        if (disposable != null) {
            if (disposable.isDisposed()) {
                disposable.dispose();
            }
        }
    }

    private Runnable requestReceiveBlock = new Runnable() {
        @Override
        public void run() {
            BcaasLog.d(TAG, "requestReceiveBlock");
            getWalletWaitingToReceiveBlock();
            handler.postDelayed(this, Constants.ValueMaps.REQUESTRECEIVETIME);
        }
    };
}