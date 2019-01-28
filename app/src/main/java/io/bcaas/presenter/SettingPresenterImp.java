package io.bcaas.presenter;


import io.bcaas.base.BCAASApplication;
import io.bcaas.bean.ServerBean;
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
import io.bcaas.ui.contracts.SettingContract;
import io.bcaas.vo.WalletVO;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 * Presenter：「Setting」fragment界面需要的數據請求&處理
 */
public class SettingPresenterImp implements SettingContract.Presenter {

    private String TAG = SettingPresenterImp.class.getSimpleName();

    private SettingContract.View viewInterface;
    private BaseHttpRequester baseHttpRequester;

    public SettingPresenterImp(SettingContract.View view) {
        super();
        this.viewInterface = view;
        baseHttpRequester = new BaseHttpRequester();
    }

    /**
     * 登出当前账户
     */
    @Override
    public void logout() {
        if (!BCAASApplication.isRealNet()) {
            viewInterface.noNetWork();
            return;
        }
        String address = BCAASApplication.getWalletAddress();
        if (StringTool.isEmpty(address)) {
            viewInterface.accountError();
            return;
        }
        RequestJson requestJson = new RequestJson();
        WalletVO walletVO = new WalletVO();
        walletVO.setWalletAddress(address);
        requestJson.setWalletVO(walletVO);
        // logout  暂时没有要求加realIP
//        requestJson.setRemoteInfoVO(new RemoteInfoVO(BCAASApplication.getWalletExternalIp()));
        RequestBody body = GsonTool.beanToRequestBody(requestJson);
        //1:请求服务器，「登出」当前账户
        baseHttpRequester.logout(body, new Callback<ResponseJson>() {
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
                    public void onFailure(Call<ResponseJson> call, Throwable throwable) {
                        //如果當前是服務器訪問不到或者連接超時，那麼需要重新切換服務器
                        LogTool.d(TAG, MessageConstants.CONNECT_TIME_OUT);
                        //1：得到新的可用的服务器
                        ServerBean serverBean = ServerTool.checkAvailableServerToSwitch();
                        if (serverBean != null) {
                            RetrofitFactory.cleanSFN();
                            logout();
                        } else {
                            ServerTool.needResetServerStatus = true;
                            viewInterface.logoutFailure(throwable.getMessage());
                        }
                    }
                }
        );
    }
}
