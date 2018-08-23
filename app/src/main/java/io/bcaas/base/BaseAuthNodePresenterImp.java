package io.bcaas.base;

import io.bcaas.R;
import io.bcaas.database.ANClientIpInfo;
import io.bcaas.database.WalletInfo;
import io.bcaas.gson.WalletRequestJson;
import io.bcaas.gson.WalletResponseJson;
import io.bcaas.gson.WalletVoRequestJson;
import io.bcaas.gson.WalletVoResponseJson;
import io.bcaas.interactor.AuthNodeInteractor;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.GsonTool;
import io.bcaas.tools.WalletTool;
import io.bcaas.vo.ClientIpInfoVO;
import io.bcaas.vo.WalletVO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    public BaseAuthNodePresenterImp(BaseAuthNodeView view) {
        this.view = view;
        authNodeInteractor = new AuthNodeInteractor();
    }

    //"取得未簽章R區塊的Send區塊 &取最新的R區塊 &wallet餘額"
    public void getWalletWaitingToReceiveBlock() {
        WalletRequestJson walletRequestJson = new WalletRequestJson();
        WalletInfo walletInfo = getWalletInfo();
        if (walletInfo == null) {
            view.failure(context.getString(R.string.walletdata_failure));
            return;
        }
        walletRequestJson.setWalletAddress(walletInfo.getBitcoinAddressStr());
        walletRequestJson.setBlockService(walletInfo.getBlockService());
        walletRequestJson.setAccessToken(walletInfo.getAccessToken());
        authNodeInteractor.getWalletWaitingToReceiveBlock(GsonTool.beanToRequestBody(walletRequestJson),
                new Callback<WalletResponseJson>() {
                    @Override
                    public void onResponse(Call<WalletResponseJson> call, Response<WalletResponseJson> response) {
                        BcaasLog.d(TAG, response.body());
                        WalletResponseJson walletResponseJson = response.body();
                        if (!walletResponseJson.isSuccess()) {
                            view.httpANSuccess();
                        } else {
                            view.failure(walletResponseJson.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<WalletResponseJson> call, Throwable t) {
                        BcaasLog.d(TAG, t.getMessage());
                        view.failure(t.getMessage());
                        //  如果当前AN的接口请求不通过的时候，应该重新去SFN拉取新AN的数据
                        resetAuthNodeInfo();

                    }
                });
    }

    //「send」区块之前请求最新的余额信息
    public void getLatestBlockAndBalance() {
        WalletRequestJson walletRequestJson = new WalletRequestJson();
        WalletInfo walletInfo = getWalletInfo();
        if (walletInfo == null) {
            view.failure(context.getString(R.string.walletdata_failure));
            return;
        }
        walletRequestJson.setWalletAddress(walletInfo.getBitcoinAddressStr());
        walletRequestJson.setBlockService(walletInfo.getBlockService());
        walletRequestJson.setAccessToken(walletInfo.getAccessToken());
        authNodeInteractor.getLatesBlockAndBalance(GsonTool.beanToRequestBody(walletRequestJson),
                new Callback<WalletResponseJson>() {
                    @Override
                    public void onResponse(Call<WalletResponseJson> call, Response<WalletResponseJson> response) {
                        BcaasLog.d(TAG, response.body());
                        WalletResponseJson walletResponseJson = response.body();
                        if (!walletResponseJson.isSuccess()) {
                            view.httpANSuccess();
                        } else {
                            view.failure(walletResponseJson.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<WalletResponseJson> call, Throwable t) {
                        BcaasLog.d(TAG, t.getMessage());
                        view.failure(t.getMessage());
                        //  如果当前AN的接口请求不通过的时候，应该重新去SFN拉取新AN的数据
                        resetAuthNodeInfo();

                    }
                });
    }

    //重置AN信息
    public void resetAuthNodeInfo() {
        final WalletVO walletVO = WalletTool.infoToVo(getWalletInfo());
        WalletVoRequestJson walletVoRequestJson = new WalletVoRequestJson(walletVO);
        BcaasLog.d(TAG, walletVoRequestJson);
        authNodeInteractor.resetAuthNode(GsonTool.beanToRequestBody(walletVoRequestJson), new Callback<WalletVoResponseJson>() {
            @Override
            public void onResponse(Call<WalletVoResponseJson> call, Response<WalletVoResponseJson> response) {
                BcaasLog.d(TAG, response.body());
                WalletVoResponseJson walletVoResponseJson = response.body();
                if (walletVoResponseJson.getSuccess()) {
                    parseAuthNodeAddress(walletVoResponseJson.getWalletVO());
                } else {
                    view.resetAuthNodeFailure(walletVoResponseJson.getMessage());
                }
            }

            @Override
            public void onFailure(Call<WalletVoResponseJson> call, Throwable t) {
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
        clientIpInfoDao.deleteAll();
        // TODO: 2018/8/21 是否自己的实体类可以替代数据库的实体类 ？
        clientIpInfoDao.insert(WalletTool.ClientIpInfoVOToDB(clientIpInfoVO));
        view.resetAuthNodeSuccess();

    }
}
