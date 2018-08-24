package io.bcaas.presenter;

import com.google.gson.Gson;

import java.util.List;

import io.bcaas.R;
import io.bcaas.base.BasePresenterImp;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.database.WalletInfo;
import io.bcaas.encryption.AES;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.interactor.LoginInteractor;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.GsonTool;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.WalletTool;
import io.bcaas.ui.contracts.LoginContracts;
import io.bcaas.vo.ClientIpInfoVO;
import io.bcaas.vo.WalletVO;
import okhttp3.RequestBody;
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
public class LoginPresenterImp extends BasePresenterImp
        implements LoginContracts.Presenter {

    private String TAG = "LoginPresenterImp";
    private LoginContracts.View view;
    private LoginInteractor loginInteractor;

    public LoginPresenterImp(LoginContracts.View view) {
        super();
        this.view = view;
        loginInteractor = new LoginInteractor();

    }


    @Override
    public void queryWalletInfo(String password) {
        List<WalletInfo> walletInfos = getAllWallets();
        if (ListTool.isEmpty(walletInfos)) {
            view.noWalletInfo();
        } else {
            WalletInfo wallet = walletInfos.get(0);//得到当前的钱包
            if (StringTool.equals(BcaasApplication.getPassword(), password)) {
                BcaasLog.d(TAG, "登入的钱包是==》" + wallet);
            }
            String walletAddress = wallet.getBitcoinAddressStr();
            String blockService = BcaasApplication.getBlockService();
            if (StringTool.isEmpty(blockService) || StringTool.isEmpty(walletAddress)) {
                //TODO 对当前的参数进行判空「自定义弹框」
                //检查到当前数据库没有钱包地址数据，那么需要提示用户先创建或者导入钱包
                view.loginFailure(context.getString(R.string.localdata_exception));
            } else {
                WalletVO walletVO = new WalletVO();
//                walletVO.setBlockService(blockService); //08-21 「登入」去掉此参数
                walletVO.setWalletAddress(walletAddress);
                login(walletVO);
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

    @Override
    public void login(WalletVO walletVO) {
        RequestJson requestJson = new RequestJson(walletVO);
        final String json = GsonTool.encodeToString(requestJson);
        BcaasLog.d(TAG, json);
        try {
            RequestBody body = GsonTool.beanToRequestBody(requestJson);
            loginInteractor.login(body, new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Gson gson = new Gson();
                    ResponseJson responseJson = gson.fromJson(response.body(), ResponseJson.class);
                    BcaasLog.d(TAG, responseJson);
                    if (responseJson.isSuccess()) {
                        parseData(responseJson.getWalletVO());
                    } else {
                        view.loginFailure(response.message());
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    view.loginFailure(t.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void parseData(WalletVO walletVO) {
        //得到当前回传的信息，存储当前的accessToken
        if (walletVO == null) {
            throw new NullPointerException(" loginPresenterImp parseData walletVO is null");
        }
        String accessToken = walletVO.getAccessToken();
        walletVO.setBlockService(Constants.BlockService.BCC);
        BcaasLog.d(TAG, accessToken);
        if (StringTool.isEmpty(accessToken)) {
            view.loginFailure(getString(R.string.login_failure));
        } else {
            saveWalletInfo(walletVO);
            BcaasApplication.setAccessToken(accessToken);
            view.loginSuccess();

        }
    }

}
