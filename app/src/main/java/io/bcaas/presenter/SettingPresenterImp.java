package io.bcaas.presenter;


import java.util.ArrayList;
import java.util.List;

import io.bcaas.R;
import io.bcaas.base.BasePresenterImp;
import io.bcaas.bean.SettingTypeBean;
import io.bcaas.constants.Constants;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.interactor.SettingInteractor;
import io.bcaas.ui.contracts.SettingContract;
import io.bcaas.utils.GsonU;
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

    private SettingContract.View viewInterface;

    public SettingPresenterImp(SettingContract.View view) {
        super();
        this.viewInterface = view;
    }

    @Override
    public List<SettingTypeBean> initSettingTypes() {
        List<SettingTypeBean> settingTypes = new ArrayList<>();
        SettingTypeBean settingTypeBean = new SettingTypeBean(getString(R.string.check_wallet_info), Constants.SettingType.CHECKWALLETINFO);
        SettingTypeBean settingTypeBean2 = new SettingTypeBean(getString(R.string.modify_password), Constants.SettingType.MODIFYPOSSWORD);
        SettingTypeBean settingTypeBean3 = new SettingTypeBean(getString(R.string.modify_authorized_representatives), Constants.SettingType.MODIFYAUTH);
        SettingTypeBean settingTypeBean4 = new SettingTypeBean(getString(R.string.address_manager), Constants.SettingType.ADRESSMANNAGE);
        settingTypes.add(settingTypeBean);
        settingTypes.add(settingTypeBean2);
        settingTypes.add(settingTypeBean3);
        settingTypes.add(settingTypeBean4);
        return settingTypes;

    }

    @Override
    public void logout(String walletAddress) {
        RequestJson walletRequestJson = new RequestJson();
        WalletVO walletVO = new WalletVO();
        walletVO.setWalletAddress(walletAddress);
        walletRequestJson.setWalletVO(walletVO);
        SettingInteractor settingInteractor = new SettingInteractor();
        RequestBody body= GsonU.beanToRequestBody(walletRequestJson);
        settingInteractor.logout(body, new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                        ResponseJson walletVoResponseJson = response.body();
                        if (walletVoResponseJson == null) {
                            viewInterface.logoutFailure(getString(R.string.data_error));
                            return;
                        }
                        if (walletVoResponseJson.isSuccess()) {
                            //todo 成功的退出，是否考虑去数据库删除当前退出账户的Token呢？应该不用，毕竟在拿到数据之后在BrandActivity去验证了以此
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
