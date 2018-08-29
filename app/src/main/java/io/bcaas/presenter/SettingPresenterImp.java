package io.bcaas.presenter;


import java.util.ArrayList;
import java.util.List;

import io.bcaas.R;
import io.bcaas.base.BasePresenterImp;
import io.bcaas.bean.SettingsBean;
import io.bcaas.constants.Constants;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.requester.SettingRequester;
import io.bcaas.ui.contracts.SettingContract;
import io.bcaas.vo.WalletVO;
import io.bcaas.tools.GsonTool;
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

    private SettingContract.View viewInterface;

    public SettingPresenterImp(SettingContract.View view) {
        super();
        this.viewInterface = view;
    }

    @Override
    public List<SettingsBean> initSettingTypes() {
        List<SettingsBean> settingTypes = new ArrayList<>();
        SettingsBean settingTypeBean = new SettingsBean(getString(R.string.check_wallet_info), Constants.SettingType.CHECK_WALLET_INFO);
        SettingsBean settingTypeBean2 = new SettingsBean(getString(R.string.modify_password), Constants.SettingType.MODIFY_PASSWORD);
        SettingsBean settingTypeBean3 = new SettingsBean(getString(R.string.modify_authorized_representatives), Constants.SettingType.MODIFY_AUTH);
        SettingsBean settingTypeBean4 = new SettingsBean(getString(R.string.address_manager), Constants.SettingType.ADDRESS_MANAGE);
        SettingsBean settingTypeBean5 = new SettingsBean(context.getString(R.string.Language_switching), Constants.SettingType.ADDRESS_MANAGE);
        settingTypes.add(settingTypeBean);
        settingTypes.add(settingTypeBean2);
        settingTypes.add(settingTypeBean3);
        settingTypes.add(settingTypeBean4);
        settingTypes.add(settingTypeBean5);
        return settingTypes;

    }

    /**
     * 登出当前账户
     *
     * @param walletAddress
     */
    @Override
    public void logout(String walletAddress) {
        RequestJson walletRequestJson = new RequestJson();
        WalletVO walletVO = new WalletVO();
        walletVO.setWalletAddress(walletAddress);
        walletRequestJson.setWalletVO(walletVO);
        RequestBody body = GsonTool.beanToRequestBody(walletRequestJson);
        //1:请求服务器，「登出」当前账户
        SettingRequester settingRequester = new SettingRequester();
        settingRequester.logout(body, new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                        ResponseJson walletVoResponseJson = response.body();
                        if (walletVoResponseJson == null) {
                            viewInterface.logoutFailure(getString(R.string.data_error));
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
                        viewInterface.logoutFailure(t.getMessage());

                    }
                }
        );
    }

}
