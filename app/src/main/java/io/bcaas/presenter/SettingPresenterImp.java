package io.bcaas.presenter;


import java.util.ArrayList;
import java.util.List;

import io.bcaas.R;
import io.bcaas.base.BasePresenterImp;
import io.bcaas.base.BcaasApplication;
import io.bcaas.bean.SettingsBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.requester.SettingRequester;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.StringTool;
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

    private String TAG = SettingPresenterImp.class.getSimpleName();

    private SettingContract.View viewInterface;
    private SettingRequester settingRequester;

    public SettingPresenterImp(SettingContract.View view) {
        super();
        this.viewInterface = view;
        settingRequester = new SettingRequester();
    }

    @Override
    public List<SettingsBean> initSettingTypes() {
        List<SettingsBean> settingTypes = new ArrayList<>();
        SettingsBean settingTypeBean = new SettingsBean(getString(R.string.check_wallet_info), Constants.SettingType.CHECK_WALLET_INFO);
        SettingsBean settingTypeBean2 = new SettingsBean(getString(R.string.modify_password), Constants.SettingType.MODIFY_PASSWORD);
        SettingsBean settingTypeBean3 = new SettingsBean(getString(R.string.modify_authorized_representatives), Constants.SettingType.MODIFY_AUTH);
        SettingsBean settingTypeBean4 = new SettingsBean(getString(R.string.address_manager), Constants.SettingType.ADDRESS_MANAGE);
        SettingsBean settingTypeBean5 = new SettingsBean(context.getString(R.string.Language_switching), Constants.SettingType.LANGUAGE_SWITCHING);
        settingTypes.add(settingTypeBean);
        settingTypes.add(settingTypeBean2);
        settingTypes.add(settingTypeBean3);
        settingTypes.add(settingTypeBean4);
        settingTypes.add(settingTypeBean5);
        return settingTypes;

    }

    /**
     * 登出当前账户
     */
    @Override
    public void logout() {
        String address = BcaasApplication.getWalletAddress();
        if (StringTool.isEmpty(address)) {
            viewInterface.onTip(getString(R.string.dataexceptionofaccount));
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

    @Override
    public void getLastChangeBlock() {
        RequestJson walletRequestJson = new RequestJson();
        WalletVO walletVO = new WalletVO();
        walletVO.setWalletAddress(BcaasApplication.getWalletAddress());
        walletVO.setBlockService(BcaasApplication.getStringFromSP(Constants.Preference.BLOCK_SERVICE));
        walletVO.setAccessToken(BcaasApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN));
        walletRequestJson.setWalletVO(walletVO);
        RequestBody body = GsonTool.beanToRequestBody(walletRequestJson);
        settingRequester.getLastChangeBlock(body, new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                        ResponseJson walletVoResponseJson = response.body();
                        if (walletVoResponseJson == null) {
                            viewInterface.onTip(getString(R.string.data_error));
                            return;
                        }
                        if (walletVoResponseJson.isSuccess()) {
                            BcaasLog.d(TAG, MessageConstants.socket.GETLATESTCHANGEBLOCK_SUCCESS);
                        } else {
                            viewInterface.onTip(walletVoResponseJson.getMessage());
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseJson> call, Throwable t) {
                        viewInterface.onTip(t.getMessage());

                    }
                }
        );

    }
}
