package io.bcaas.presenter;


import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.bcaas.base.BasePresenterImp;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.MessageConstants;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.http.retrofit.RetrofitFactory;
import io.bcaas.requester.SettingRequester;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.ServerTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.ui.contracts.SettingContract;
import io.bcaas.vo.WalletVO;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 */
public class SettingPresenterImp extends BasePresenterImp
        implements SettingContract.Presenter {

    private String TAG = SettingPresenterImp.class.getSimpleName();

    private SettingContract.View viewInterface;
    private SettingRequester settingRequester;

    public SettingPresenterImp(SettingContract.View view) {
        super();
        this.viewInterface = view;
        settingRequester = new SettingRequester();
    }

    /**
     * 登出当前账户
     */
    @Override
    public void logout() {
        if (!BcaasApplication.isRealNet()) {
            viewInterface.noNetWork();
            return;
        }
        String address = BcaasApplication.getWalletAddress();
        if (StringTool.isEmpty(address)) {
            viewInterface.accountError();
            return;
        }
        RequestJson walletRequestJson = new RequestJson();
        WalletVO walletVO = new WalletVO();
        walletVO.setWalletAddress(address);
        walletRequestJson.setWalletVO(walletVO);
        RequestBody body = GsonTool.beanToRequestBody(walletRequestJson);
        //1:请求服务器，「登出」当前账户
        settingRequester.logout(body, new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                        ResponseJson walletVoResponseJson = response.body();
                        if (walletVoResponseJson == null) {
                            viewInterface.logoutFailure();
                            return;
                        }
                        //2：如果服务器「登出」成功，清除本地存储的token信息
                        if (walletVoResponseJson.isSuccess()) {
                            viewInterface.logoutSuccess();
                        } else {
                            viewInterface.logoutFailure(walletVoResponseJson.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseJson> call, Throwable t) {
                        if (t instanceof UnknownHostException
                                || t instanceof SocketTimeoutException
                                || t instanceof ConnectException) {
                            //如果當前是服務器訪問不到或者連接超時，那麼需要重新切換服務器
                            LogTool.d(TAG, MessageConstants.CONNECT_TIME_OUT);
                            //1：得到新的可用的服务器
                            boolean isSwitchServer = ServerTool.checkAvailableServerToSwitch();
                            if (isSwitchServer) {
                                RetrofitFactory.cleanSFN();
                                logout();
                            } else {
                                ServerTool.needResetServerStatus = true;
                                viewInterface.logoutFailure(t.getMessage());
                            }
                        } else {
                            viewInterface.logoutFailure(t.getMessage());
                        }

                    }
                }
        );
    }
}
