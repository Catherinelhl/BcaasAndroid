package io.bcaas.presenter;

import io.bcaas.base.BCAASApplication;
import io.bcaas.bean.ServerBean;
import io.bcaas.bean.WalletBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.http.retrofit.RetrofitFactory;
import io.bcaas.requester.BaseHttpRequester;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.NetWorkTool;
import io.bcaas.tools.ServerTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.tools.wallet.WalletDBTool;
import io.bcaas.ui.contracts.LoginContracts;
import io.bcaas.vo.WalletVO;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/20
 * <p>
 * Presenter：「Login」界面需要的數據獲取&處理
 * 1：查询当前本地数据库，如果没有钱包数据，代表没有可解锁的钱包，提示用户创建钱包/导入钱包
 * 2：如果当前有钱包数据，那么就进行网络请求，进行「登入」的操作，拿到返回的数据
 * 3：得到钱包登入「accessToken」，存储到当前用户下，然後拿「accessToken」進行「verify」操作，以此来請求判断是否需要重新「登入」
 */
public class LoginPresenterImp implements LoginContracts.Presenter {
    private String TAG = LoginPresenterImp.class.getSimpleName();
    private LoginContracts.View view;
    private BaseHttpRequester baseHttpRequester;

    public LoginPresenterImp(LoginContracts.View view) {
        super();
        this.view = view;
        baseHttpRequester = new BaseHttpRequester();
    }

    /**
     * 查询当前钱包
     *
     * @param password
     */
    @Override
    public void queryWalletFromDB(String password) {
        //1：查询当前数据库数据,得到Keystore
        String keyStore = WalletDBTool.queryKeyStore();
        if (StringTool.isEmpty(keyStore)) {
            view.noWalletInfo();
        } else {
            //2：解析当前KeyStore，然后得到钱包信息
            WalletBean walletBean = WalletDBTool.parseKeystore(keyStore);
            LogTool.d(TAG, BCAASApplication.getStringFromSP(Constants.Preference.PASSWORD));
            //3：比对当前密码是否正确
            if (StringTool.equals(BCAASApplication.getStringFromSP(Constants.Preference.PASSWORD), password)) {
                //4：判断当前的钱包地址是否为空
                String walletAddress = walletBean.getAddress();
                if (StringTool.isEmpty(walletAddress)) {
                    LogTool.d(MessageConstants.WALLET_DATA_FAILURE);
                    view.noWalletInfo();
                } else {
                    //4:存储当前钱包信息
                    BCAASApplication.setWalletBean(walletBean);
                    //5：开始「登入」
                    login();
                }
            } else {
                view.passwordError();
            }
        }
    }

    /**
     * 開始「Login」接口請求操作
     */
    @Override
    public void login() {
        LogTool.d(TAG, MessageConstants.TO_LOGIN);
        view.showLoading();
        if (!BCAASApplication.isRealNet()) {
            view.noNetWork();
            view.hideLoading();
            return;
        }
        //获取当前钱包的地址
        WalletVO walletVO = new WalletVO(BCAASApplication.getWalletAddress());
        RequestJson requestJson = new RequestJson(walletVO);
        LogTool.d(TAG, requestJson);
        RequestBody body = GsonTool.beanToRequestBody(requestJson);
        baseHttpRequester.login(body, new Callback<ResponseJson>() {
            @Override
            public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                ResponseJson responseJson = response.body();
                if (responseJson == null) {
                    view.loginFailure();
                    return;
                }
                if (responseJson.isSuccess()) {
                    parseLoginInfo(responseJson.getWalletVO());
                } else {
                    LogTool.d(TAG, response.message());
                    view.loginFailure();
                }
                view.hideLoading();

            }

            @Override
            public void onFailure(Call<ResponseJson> call, Throwable throwable) {
                LogTool.d(TAG, throwable.getCause());
                if (NetWorkTool.connectTimeOut(throwable)) {
                    //如果當前是服務器訪問不到或者連接超時，那麼需要重新切換服務器
                    LogTool.d(TAG, MessageConstants.CONNECT_TIME_OUT);
                    //1：得到新的可用的服务器
                    ServerBean serverBean = ServerTool.checkAvailableServerToSwitch();
                    if (serverBean != null) {
                        RetrofitFactory.cleanSFN();
                        login();
                    } else {
                        ServerTool.needResetServerStatus = true;
                        view.hideLoading();
                        view.loginFailure();
                    }
                } else {
                    view.hideLoading();
                    view.loginFailure();
                }


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
            view.noWalletInfo();
            return;
        }
        String accessToken = walletVO.getAccessToken();
        if (StringTool.isEmpty(accessToken)) {
            view.noWalletInfo();
        } else {
            ServerTool.addServerInfo(walletVO.getSeedFullNodeList());
            BCAASApplication.setStringToSP(Constants.Preference.ACCESS_TOKEN, accessToken);
            view.loginSuccess();
        }
    }
}
