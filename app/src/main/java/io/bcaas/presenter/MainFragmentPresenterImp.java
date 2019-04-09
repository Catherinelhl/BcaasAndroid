package io.bcaas.presenter;


import java.util.List;

import io.bcaas.base.BCAASApplication;
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
import io.bcaas.vo.WalletVO;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/21
 * <p>
 * Presenter：「首頁/mainFragment」界面需要的 數據獲取&處理
 */
public class MainFragmentPresenterImp implements MainFragmentContracts.Presenter {

    private String TAG = MainFragmentPresenterImp.class.getSimpleName();
    private MainFragmentContracts.View view;
    private BaseHttpRequester baseHttpRequester;
    private Disposable disposableGetAccountTransaction;

    public MainFragmentPresenterImp(MainFragmentContracts.View view) {
        this.view = view;
        baseHttpRequester = new BaseHttpRequester();
    }

    /**
     * * 获取已完成的区块信息
     * * 1.分页一次回传10笔
     * * 2.请求第一页nextObjectId为”0“
     * * 3.请求下一页区须带入nextObjectId
     * * 4.没有分页会回传"NextPageIsEmpty"
     *
     * @param nextObjectId
     * @param isDone       是否完成
     */
    @Override
    public void getAccountTransactions(String nextObjectId, boolean isDone) {
        disposeDisposable(disposableGetAccountTransaction);
        WalletVO walletVO = new WalletVO();
        walletVO.setBlockService(BCAASApplication.getBlockService());
        walletVO.setWalletAddress(BCAASApplication.getWalletAddress());
        RequestJson requestJson = new RequestJson(walletVO);
        // 默认传0，
        PaginationVO paginationVO = new PaginationVO(nextObjectId);
        requestJson.setPaginationVO(paginationVO);
        LogTool.d(TAG, requestJson);
        RequestBody requestBody = GsonTool.beanToRequestBody(requestJson);
        //得到当前的所有已完成交易
        Observable<ResponseJson> observableDone = baseHttpRequester.getAccountDoneTC(requestBody);
        //得到当前的所有未完成交易
        Observable<ResponseJson> observableUnDone = baseHttpRequester.getAccountUNDoneTC(requestBody);
        //根据用户的选择获取相对应的交易记录
        (isDone ? observableDone : observableUnDone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseJson>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposableGetAccountTransaction = d;
                    }

                    @Override
                    public void onNext(ResponseJson responseJson) {
                        if (responseJson == null) {
                            view.noAccountDoneTC();
                            return;
                        }
                        LogTool.d(TAG, responseJson);
                        if (responseJson.isSuccess()) {
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
                                    getAccountTransactions(Constants.ValueMaps.DEFAULT_PAGINATION, isDone);
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
                    public void onError(Throwable e) {
                        LogTool.e(TAG, e.getMessage());
                        e.printStackTrace();
                        if (StringTool.contains(e.getMessage(), String.valueOf(MessageConstants.CODE_404))) {
                            view.httpException();
                            return;
                        }
                        view.getAccountDoneTCFailure(e.getMessage());
                        disposeDisposable(disposableGetAccountTransaction);

                    }

                    @Override
                    public void onComplete() {
                        disposeDisposable(disposableGetAccountTransaction);
                    }
                });
    }

    /**
     * 处理可以转让
     *
     * @param disposable
     */
    private void disposeDisposable(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
