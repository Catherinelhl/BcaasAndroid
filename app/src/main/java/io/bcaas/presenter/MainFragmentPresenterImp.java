package io.bcaas.presenter;


import android.util.Log;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.bcaas.base.BasePresenterImp;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.requester.BaseHttpRequester;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.tools.gson.JsonTool;
import io.bcaas.ui.contracts.MainFragmentContracts;
import io.bcaas.vo.PaginationVO;
import io.bcaas.vo.TransactionChainChangeVO;
import io.bcaas.vo.TransactionChainOpenVO;
import io.bcaas.vo.TransactionChainReceiveVO;
import io.bcaas.vo.TransactionChainSendVO;
import io.bcaas.vo.TransactionChainVO;
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
    public void getAccountDoneTC() {
        WalletVO walletVO = new WalletVO();
        walletVO.setBlockService(BcaasApplication.getBlockService());
        walletVO.setWalletAddress(BcaasApplication.getWalletAddress());
        RequestJson requestJson = new RequestJson(walletVO);
        // 默认传0，
        PaginationVO paginationVO = new PaginationVO(Constants.ValueMaps.DEFAULT_PAGINATION);
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
                LogTool.d(TAG, responseJson);
                if (responseJson == null) {
                    view.noResponseData();
                    return;
                }
                if (response.isSuccessful()) {
                    PaginationVO paginationVOResponse = responseJson.getPaginationVO();
                    LogTool.d(TAG, paginationVOResponse);
                    if (paginationVOResponse != null) {
                        String nextObjectId = paginationVOResponse.getNextObjectId();
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
                LogTool.d(TAG, t.getCause());
                view.getAccountDoneTCFailure(t.getMessage());
            }
        });
    }
}
