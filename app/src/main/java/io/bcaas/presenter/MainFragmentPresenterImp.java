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
 * mainFragment 数据请求
 */
public class MainFragmentPresenterImp extends BasePresenterImp
        implements MainFragmentContracts.Presenter {

    private String TAG = MainFragmentPresenterImp.class.getSimpleName();
    private MainFragmentContracts.View view;
    private BaseHttpRequester baseHttpRequester;

    public MainFragmentPresenterImp(MainFragmentContracts.View view) {
        super();
        this.view = view;
        baseHttpRequester = new BaseHttpRequester();
    }

    /**
     * 获取已完成的区块信息
     * 1.分页一次回传10笔
     * 2.请求第一页nextObjectId为”0“
     * 3.请求下一页区须带入nextObjectId
     * 4.没有分页会回传"NextPageIsEmpty"
     */
    @Override
    public void getAccountDoneTC(String nextObjectId) {
        WalletVO walletVO = new WalletVO();
        walletVO.setBlockService(BcaasApplication.getBlockService());
        walletVO.setWalletAddress(BcaasApplication.getWalletAddress());
        RequestJson requestJson = new RequestJson(walletVO);
        // 默认传0，
        PaginationVO paginationVO = new PaginationVO(nextObjectId);
        requestJson.setPaginationVO(paginationVO);
        LogTool.d(TAG, requestJson);
        RequestBody requestBody = GsonTool.beanToRequestBody(requestJson);
        baseHttpRequester.getAccountDoneTC(requestBody, new Callback<ResponseJson>() {
            @Override
            public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                if (response == null) {
                    view.noResponseData();
                    return;
                }
                ResponseJson responseJson = response.body();
                if (responseJson == null) {
                    view.noResponseData();
                    return;
                }
                if (response.isSuccessful()) {
                    PaginationVO paginationVOResponse = responseJson.getPaginationVO();
                    if (paginationVOResponse != null) {
                        String nextObjectId = paginationVOResponse.getNextObjectId();
                        //不用顯示"點擊顯示更多"
                        view.getNextObjectId(nextObjectId);
                        LogTool.d(TAG, nextObjectId);
                        List<Object> objectList = paginationVOResponse.getObjectList();
                        if (ListTool.isEmpty(objectList)) {
                            view.noAccountDoneTC();
                        } else {
                            view.getAccountDoneTCSuccess(objectList);
                        }
                    } else {
                        view.noAccountDoneTC();
                    }
                } else {
                    view.httpExceptionStatus(responseJson);
                }
            }

            @Override
            public void onFailure(Call<ResponseJson> call, Throwable t) {
                LogTool.e(TAG, t.getMessage());
                view.getAccountDoneTCFailure(t.getMessage());
            }
        });
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

            }
        });


    }
}
