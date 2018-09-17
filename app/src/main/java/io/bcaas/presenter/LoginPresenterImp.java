package io.bcaas.presenter;

import java.util.List;

import io.bcaas.base.BaseHttpPresenterImp;
import io.bcaas.base.BasePresenterImp;
import io.bcaas.base.BcaasApplication;
import io.bcaas.bean.SeedFullNodeBean;
import io.bcaas.bean.WalletBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.constants.SystemConstants;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.requester.LoginRequester;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.tools.wallet.WalletDBTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.StringTool;
import io.bcaas.ui.contracts.BaseContract;
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
 * 登入
 * 1：查询当前本地数据库，如果没有钱包数据，代表没有可解锁的钱包，提示用户创建钱包/导入钱包
 * 2：如果当前有钱包数据，然后拿到是否有「accessToken」字段，如果没有，那么就进行网络请求，进行「登入」的操作，拿到返回的数据
 * 4：得到钱包登入「accessToken」，存储到当前用户下，然后以此来判断是否需要重新「登入」
 * 5：把拿到的钱包信息得到，然后「verify」
 */
public class LoginPresenterImp extends BasePresenterImp
        implements LoginContracts.Presenter {
    private String TAG = LoginPresenterImp.class.getSimpleName();
    private LoginContracts.View view;
    private LoginRequester loginRequester;

    public LoginPresenterImp(LoginContracts.View view) {
        super();
        this.view = view;
        loginRequester = new LoginRequester();
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
            LogTool.d(TAG, BcaasApplication.getStringFromSP(Constants.Preference.PASSWORD));
            //2：比对当前密码是否正确
            if (StringTool.equals(BcaasApplication.getStringFromSP(Constants.Preference.PASSWORD), password)) {
                //4:存储当前钱包信息
                BcaasApplication.setWalletBean(walletBean);
                //5：判断当前的钱包地址是否为空
                String walletAddress = walletBean.getAddress();
                if (StringTool.isEmpty(walletAddress)) {
                    LogTool.d(MessageConstants.WALLET_DATA_FAILURE);
                    view.noWalletInfo();
                } else {
                    //4：开始「登入」
                    toLogin();
                }
            } else {
                view.passwordError();
            }
        }
    }

    @Override
    public void toLogin() {
        view.showLoadingDialog();
        if (!BcaasApplication.isRealNet()) {
            view.noNetWork();
            view.hideLoadingDialog();
            return;
        }
        //获取当前钱包的地址
        WalletVO walletVO = new WalletVO(BcaasApplication.getWalletAddress());
        RequestJson requestJson = new RequestJson(walletVO);
        LogTool.d(TAG, requestJson);
        RequestBody body = GsonTool.beanToRequestBody(requestJson);
        loginRequester.login(body, new Callback<ResponseJson>() {
            @Override
            public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                ResponseJson responseJson = response.body();
                if (responseJson.isSuccess()) {
                    parseLoginInfo(responseJson.getWalletVO());
                } else {
                    LogTool.d(TAG, response.message());
                    view.loginFailure();
                }
                view.hideLoadingDialog();

            }

            @Override
            public void onFailure(Call<ResponseJson> call, Throwable t) {
                LogTool.d(TAG, t.getMessage());
                LogTool.d(TAG, t.getCause());
                LogTool.d(TAG, call.toString());
                view.loginFailure();
                view.hideLoadingDialog();

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
            addSeedFullNodeList(walletVO.getSeedFullNodeList());
            BcaasApplication.setStringToSP(Constants.Preference.ACCESS_TOKEN, accessToken);
            view.loginSuccess();
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
}
