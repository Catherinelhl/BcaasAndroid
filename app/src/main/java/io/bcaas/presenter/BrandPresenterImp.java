package io.bcaas.presenter;

import java.util.ArrayList;
import java.util.List;

import io.bcaas.base.BasePresenterImp;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.MessageConstants;
import io.bcaas.database.WalletInfo;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.interactor.VerifyInteractor;
import io.bcaas.ui.contracts.BrandContracts;
import io.bcaas.tools.GsonTool;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.StringTool;
import io.bcaas.vo.ClientIpInfoVO;
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

    private String TAG = "BrandPresenterImp";

    private BrandContracts.View view;
    private VerifyInteractor verifyInteractor;

    public BrandPresenterImp(BrandContracts.View view) {
        super();
        this.view = view;
        verifyInteractor = new VerifyInteractor();

    }


    @Override
    public void queryWalletInfo() {
        List<WalletInfo> walletInfo = getAllWallets();
        if (ListTool.isEmpty(walletInfo)) {
            view.noWalletInfo();
        } else {
            WalletInfo wallet = walletInfo.get(0);//得到当前的钱包
            BcaasLog.d(TAG, "数据库钱包信息：" + wallet);
            String walletAddress = wallet.getBitcoinAddressStr();
            String blockService = BcaasApplication.getBlockService();
            String publicKey = wallet.getBitcoinPublicKeyStr();
            String privateKey = wallet.getBitcoinPrivateKeyWIFStr();
            //如果当前有数据，将私钥/公钥存储起来
            BcaasApplication.setPrivateKey(privateKey);
            BcaasApplication.setPublicKey(publicKey);
            if (StringTool.isEmpty(blockService) || StringTool.isEmpty(walletAddress)) {
                //检查到当前数据库没有钱包地址数据，那么需要提示用户先创建或者导入钱包
                view.noWalletInfo();
            } else {
                String accessToken = BcaasApplication.getAccessToken();
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

    //得到当前存储的数据库钱包信息
    private List<WalletInfo> getAllWallets() {
        if (walletInfoDao != null) {
            return walletInfoDao.queryBuilder().list();
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 验证当前的token是否可用
     * 1:每次「登入」成功之后需要携带BlockService进行访问
     * 2:每次Brand直接进来可以访问
     *
     * @param walletVO 傳輸資料皆要加密:狀態碼：code＝200為驗證通過，2014為變更AuthNode資訊,請重新連線AuthNode,其餘皆為異常。
     */
    private void verifyToken(WalletVO walletVO) {
         RequestJson requestJson = new RequestJson(walletVO);
        BcaasLog.d(TAG, requestJson);
        verifyInteractor.verify(GsonTool.beanToRequestBody(requestJson), new Callback<ResponseJson>() {
            @Override
            public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                BcaasLog.d(TAG, response.body());
                ResponseJson responseJson = response.body();
                if (responseJson == null) {
                    view.noWalletInfo();
                } else {
                    if (responseJson.isSuccess()) {
                        WalletVO walletVONew = responseJson.getWalletVO();
                        saveWalletInfo(walletVONew);
                        view.online();
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
                        view.noWalletInfo();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseJson> call, Throwable t) {
                BcaasLog.e(TAG, t.getMessage());
                view.noWalletInfo();
            }
        });
    }


}
