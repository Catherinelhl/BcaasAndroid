package io.bcaas.presenter;


import java.util.List;

import io.bcaas.base.BCAASApplication;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.http.callback.BcaasCallback;
import io.bcaas.requester.BaseHttpRequester;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.ui.contracts.MainFragmentContracts;
import io.bcaas.vo.PaginationVO;
import io.bcaas.vo.WalletVO;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/21
 * <p>
 * Presenter：「首頁/mainFragment」界面需要的 數據獲取&處理
 */
public class MainFragmentPresenterImp extends BlockServicePresenterImp
        implements MainFragmentContracts.Presenter {

    private String TAG = MainFragmentPresenterImp.class.getSimpleName();
    private MainFragmentContracts.View view;
    private BaseHttpRequester baseHttpRequester;

    public MainFragmentPresenterImp(MainFragmentContracts.View view) {
        super(view);
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
        walletVO.setBlockService(BCAASApplication.getBlockService());
        walletVO.setWalletAddress(BCAASApplication.getWalletAddress());
        RequestJson requestJson = new RequestJson(walletVO);
        // 默认传0，
        PaginationVO paginationVO = new PaginationVO(nextObjectId);
        requestJson.setPaginationVO(paginationVO);
        LogTool.d(TAG, requestJson);
        RequestBody requestBody = GsonTool.beanToRequestBody(requestJson);
        baseHttpRequester.getAccountDoneTC(requestBody, new BcaasCallback<ResponseJson>() {
            @Override
            public void onSuccess(Response<ResponseJson> response) {
                if (response == null) {
                    view.noAccountDoneTC();
                    return;
                }
                ResponseJson responseJson = response.body();
                if (responseJson == null) {
                    view.noAccountDoneTC();
                    return;
                }
                if (response.isSuccessful()) {
                    //判斷當前是否是同一個幣種，如果不是，就不顯示
                    WalletVO walletVOResponse = responseJson.getWalletVO();
                    if (walletVOResponse != null) {
                        String blockService = walletVOResponse.getBlockService();
                        // 判斷當前的幣種信息
                        if (StringTool.equals(blockService, BCAASApplication.getBlockService())) {
                            PaginationVO paginationVOResponse = responseJson.getPaginationVO();
                            if (paginationVOResponse != null) {
                                String nextObjectId = paginationVOResponse.getNextObjectId();
                                //不用顯示"點擊顯示更多"
                                view.getNextObjectId(nextObjectId);
                                List<Object> objectList = paginationVOResponse.getObjectList();
                                LogTool.d(TAG, nextObjectId + ":" + objectList.size());
                                if (ListTool.isEmpty(objectList)) {
                                    view.noAccountDoneTC();
                                } else {
                                    view.getAccountDoneTCSuccess(objectList);
                                }
                            } else {
                                //交易紀錄信息為空
                                view.noAccountDoneTC();
                            }
                        } else {
                            //幣種信息不相等
                            //重新拿去當前幣種情趣
                            getAccountDoneTC(Constants.ValueMaps.DEFAULT_PAGINATION);
//                            view.noAccountDoneTC();
                        }
                    } else {
                        //沒有錢包信息
                        view.noAccountDoneTC();

                    }

                } else {
                    view.httpExceptionStatus(responseJson);
                }
            }

            @Override
            public void onNotFound() {
                super.onNotFound();
                view.getAccountDoneTCFailure(MessageConstants.Empty);
            }

            @Override
            public void onFailure(Call<ResponseJson> call, Throwable t) {
                LogTool.e(TAG, t.getMessage());
                view.getAccountDoneTCFailure(t.getMessage());
            }
        });
    }
}
