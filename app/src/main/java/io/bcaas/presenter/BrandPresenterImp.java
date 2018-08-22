package io.bcaas.presenter;

import java.util.List;

import io.bcaas.base.BasePresenterImp;
import io.bcaas.database.WalletInfo;
import io.bcaas.gson.WalletVoRequestJson;
import io.bcaas.gson.WalletVoResponseJson;
import io.bcaas.interactor.VerifyInteractor;
import io.bcaas.ui.contracts.BrandContracts;
import io.bcaas.tools.GsonTool;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.StringTool;
import io.bcaas.vo.WalletVO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/20
 * <p>
 * 1：查询当前本地数据库，如果没有钱包数据，代表没有可解锁的钱包，提示用户创建钱包/导入钱包
 * 2：如果当前有钱包数据，然后拿到是否有「accessToken」字段，如果没有，那么就进行网络请求，进行「登入」的操作，拿到返回的数据
 * 4：得到钱包登入「accessToken」，存储到当前用户下，然后以此来判断是否需要重新「登入」
 * 5：如果有「accessToken」那么就直接进入首页
 * todo  貌似判断Token是否存在应该在Brand页面去检测
 */
public class BrandPresenterImp extends BasePresenterImp
        implements BrandContracts.Presenter {

    private BrandContracts.View view;
    private VerifyInteractor verifyInteractor;

    public BrandPresenterImp(BrandContracts.View view) {
        super();
        this.view = view;
        verifyInteractor = new VerifyInteractor();

    }


    @Override
    public void queryWalletInfo() {
        List<WalletInfo> walletInfos = getAllWallets();
        if (ListTool.isEmpty(walletInfos)) {
            view.noWalletInfo();
        } else {
            BcaasLog.d("数据库共有==" + walletInfos.size() + "==条数据；");
            for (WalletInfo walletInfo : walletInfos) {
                BcaasLog.d(walletInfo);
            }
            WalletInfo wallet = walletInfos.get(0);//得到当前的钱包
            String walletAddress = wallet.getBitcoinAddressStr();
            String blockService = wallet.getBlockService();
            String accessToken = wallet.getAccessToken();
            if (StringTool.isEmpty(blockService) || StringTool.isEmpty(walletAddress)) {
                //检查到当前数据库没有钱包地址数据，那么需要提示用户先创建或者导入钱包
                view.noWalletInfo();
            } else {
                if (StringTool.isEmpty(accessToken)) {
                    //有钱包，但是没有token
                    view.noWalletInfo();
                } else {
                    WalletVO walletVO = new WalletVO();
                    walletVO.setAccessToken(accessToken);
                    walletVO.setWalletAddress(walletAddress);
                    walletVO.setBlockService(blockService);
                    verifyToken(walletVO);
                }

            }

        }

    }

    //得到所有得钱包信息
    private List<WalletInfo> getAllWallets() {
        if (walletInfoDao == null) {
            throw new NullPointerException("walletInfoDao is null");
        }
        return walletInfoDao.queryBuilder().list();
    }

    //验证当前的token是否可用
    private void verifyToken(final WalletVO walletVO) {
        WalletVoRequestJson walletVoRequestJson = new WalletVoRequestJson(walletVO);
        BcaasLog.d("verifyToken",walletVoRequestJson);
        verifyInteractor.verify(GsonTool.beanToRequestBody(walletVoRequestJson), new Callback<WalletVoResponseJson>() {
            @Override
            public void onResponse(Call<WalletVoResponseJson> call, Response<WalletVoResponseJson> response) {
                BcaasLog.d("verifyToken response==>" + response.body());
                WalletVoResponseJson walletVoResponseJson = response.body();
                if (walletVoResponseJson == null) {
                    view.noWalletInfo();
                } else {
                    if (walletVoResponseJson.getSuccess()) {
                        saveWalletInfo(walletVO);
                        view.online();
                    } else {
                        view.offline();
                        view.failure(walletVoResponseJson.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<WalletVoResponseJson> call, Throwable t) {
                view.noWalletInfo();
            }
        });
    }


}
