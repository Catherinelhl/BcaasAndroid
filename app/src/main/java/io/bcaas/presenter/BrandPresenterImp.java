package io.bcaas.presenter;

import io.bcaas.base.BasePresenterImp;
import io.bcaas.base.BcaasApplication;
import io.bcaas.bean.WalletBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.tools.ecc.WalletTool;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.requester.LoginRequester;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.tools.StringTool;
import io.bcaas.ui.contracts.BrandContracts;
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
    private String TAG = BrandPresenterImp.class.getSimpleName();
    private BrandContracts.View view;
    private LoginRequester loginRequester;

    public BrandPresenterImp(BrandContracts.View view) {
        super();
        this.view = view;
        loginRequester = new LoginRequester();

    }

    /**
     * 查询钱包信息
     */
    @Override
    public void queryWalletInfo() {
        //1: 查询当前数据库是否有KeyStore数据
        boolean existKeyStore = BcaasApplication.existKeystoreInDB();
        if (existKeyStore) {
            //2:查询当前数据库，得到Keystore
            String keyStore = BcaasApplication.queryKeyStore();
            if (StringTool.isEmpty(keyStore)) {
                view.noWalletInfo();
            } else {
                //3：解析当前KeyStore，然后得到钱包信息
                WalletBean walletBean = WalletTool.parseKeystore(keyStore);
                if (walletBean == null) {
                    //如果钱包信息是空的，那么可能数据库的数据已经异常了，这个时候可以删除数据库，重新「创建」、「导入」
                    BcaasApplication.clearWalletTable();
                    view.noWalletInfo();
                } else {
                    //4:存储当前钱包信息
                    BcaasApplication.setWalletBean(walletBean);
                    String walletAddress = walletBean.getAddress();
                    String publicKey = walletBean.getPublicKey();
                    String privateKey = walletBean.getPrivateKey();
                    //如果当前有数据，将私钥/公钥存储起来
                    BcaasApplication.setStringToSP(Constants.Preference.PRIVATE_KEY, privateKey);
                    BcaasApplication.setStringToSP(Constants.Preference.PUBLIC_KEY, publicKey);
                    if (StringTool.isEmpty(walletAddress)) {
                        //检查到当前数据库没有钱包地址数据，那么需要提示用户先创建或者导入钱包
                        view.noWalletInfo();
                    } else {
                        String accessToken = BcaasApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN);
                        LogTool.d(TAG, accessToken);
                        if (StringTool.isEmpty(accessToken)) {
                            //有钱包，但是没有token
                            view.noWalletInfo();
                        } else {
                            String blockService = BcaasApplication.getBlockService();
                            WalletVO walletVO = new WalletVO();
                            walletVO.setAccessToken(accessToken);
                            walletVO.setWalletAddress(walletAddress);
                            walletVO.setBlockService(StringTool.isEmpty(blockService) ? Constants.BLOCKSERVICE_BCC : blockService);
                            verifyToken(walletVO);
                        }
                    }

                }
            }

        } else {
            view.noWalletInfo();

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
        final RequestJson requestJson = new RequestJson(walletVO);
        LogTool.d(TAG, requestJson);
        loginRequester.verify(GsonTool.beanToRequestBody(requestJson), new Callback<ResponseJson>() {
            @Override
            public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                LogTool.d(TAG, response.body());
                ResponseJson responseJson = response.body();
                if (responseJson == null) {
                    view.noWalletInfo();
                } else {
                    if (responseJson.isSuccess()) {
                        WalletVO walletVONew = responseJson.getWalletVO();
                        BcaasApplication.setStringToSP(Constants.Preference.ACCESS_TOKEN, walletVONew.getAccessToken());
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
                        //eg:{"success":false,"code":3006,"message":"Redis data not found.","size":0}
                        //异常情况，作没有钱包处理
                        int code = responseJson.getCode();
                        if (code == MessageConstants.CODE_3006) {
                            view.noWalletInfo();

                        } else {
                            // 待定异常都重新登录
                            view.noWalletInfo();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseJson> call, Throwable t) {
                LogTool.e(TAG, t.getMessage());
                view.noWalletInfo();
            }
        });
    }

    /**
     * 检查当前的版本信息
     */
    @Override
    public void checkVersionInfo() {
//        loginRequester.getAndroidVersionInfo();

    }
}
