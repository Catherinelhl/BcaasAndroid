package io.bcaas.presenter;


import java.util.ArrayList;
import java.util.List;

import io.bcaas.base.BasePresenterImp;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.requester.BaseHttpRequester;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.ui.contracts.BlockServiceContracts;
import io.bcaas.ui.contracts.MainFragmentContracts;
import io.bcaas.vo.PaginationVO;
import io.bcaas.vo.PublicUnitVO;
import io.bcaas.vo.WalletVO;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/21
 * <p>
 * 币种清单 数据请求
 */
public class BlockServicePresenterImp extends BasePresenterImp
        implements BlockServiceContracts.Presenter {

    private String TAG = BlockServicePresenterImp.class.getSimpleName();
    private BlockServiceContracts.View view;
    private BaseHttpRequester baseHttpRequester;

    public BlockServicePresenterImp(BlockServiceContracts.View view) {
        super();
        this.view = view;
        baseHttpRequester = new BaseHttpRequester();
    }

    @Override
    public void getBlockServiceList() {
        view.showLoadingDialog();
        if (!BcaasApplication.isRealNet()) {
            view.hideLoadingDialog();
            view.noNetWork();
            return;
        }
        WalletVO walletVO = new WalletVO();
        walletVO.setWalletAddress(BcaasApplication.getWalletAddress());
        RequestJson requestJson = new RequestJson(walletVO);
        LogTool.d(TAG, requestJson);
        RequestBody requestBody = GsonTool.beanToRequestBody(requestJson);
        baseHttpRequester.getBlockServiceList(requestBody, new Callback<ResponseJson>() {
            @Override
            public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                view.hideLoadingDialog();
                ResponseJson responseJson = response.body();
                LogTool.d(TAG, response.body());
                if (responseJson != null) {
                    if (responseJson.isSuccess()) {
                        List<PublicUnitVO> publicUnitVOList = responseJson.getPublicUnitVOList();
                        List<PublicUnitVO> publicUnitVOListNew = new ArrayList<>();
                        if (ListTool.noEmpty(publicUnitVOList)) {
                            for (PublicUnitVO publicUnitVO : publicUnitVOList) {
                                if (publicUnitVO != null) {
                                    /*isStartUp:0:關閉；1：開放*/
                                    String isStartUp = publicUnitVO.isStartup();
                                    if (StringTool.equals(isStartUp, Constants.BlockService.OPEN)) {
                                        publicUnitVOListNew.add(publicUnitVO);
                                    }
                                }
                            }
                            if (ListTool.noEmpty(publicUnitVOListNew)) {
                                BcaasApplication.setPublicUnitVOList(publicUnitVOListNew);
                                view.getBlockServicesListSuccess(publicUnitVOListNew);
                            } else {
                                view.noBlockServicesList();
                            }

                        }
                    } else {
                        int code = responseJson.getCode();
                        if (code == MessageConstants.CODE_2025) {
                            view.noBlockServicesList();
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseJson> call, Throwable t) {
                LogTool.d(TAG, t.getMessage());
                view.hideLoadingDialog();

            }
        });


    }
}
