package io.bcaas.presenter;


import java.util.List;

import io.bcaas.base.BaseHttpPresenterImp;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.requester.BaseHttpRequester;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.VersionTool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.ui.contracts.MainContracts;
import io.bcaas.vo.VersionVO;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/21
 * <p>
 * 后台需要和服务器建立长连接，进行R区块的请求
 */
public class MainPresenterImp extends BaseHttpPresenterImp
        implements MainContracts.Presenter {

    private String TAG = MainPresenterImp.class.getSimpleName();
    private MainContracts.View view;
    private BaseHttpRequester baseHttpRequester;

    public MainPresenterImp(MainContracts.View view) {
        super(view);
        this.view = view;
        baseHttpRequester = new BaseHttpRequester();
    }

    @Override
    public void unSubscribe() {
        super.unSubscribe();
    }

    @Override
    public void checkUpdate() {
        VersionVO versionVO = new VersionVO(Constants.ValueMaps.AUTHKEY);
        RequestJson requestJson = new RequestJson(versionVO);
        LogTool.d(TAG, requestJson);
        RequestBody requestBody = GsonTool.beanToRequestBody(requestJson);
        baseHttpRequester.checkUpdate(requestBody, new Callback<ResponseJson>() {
            @Override
            public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                view.hideLoading();
                if (response != null) {
                    ResponseJson responseJson = response.body();
                    if (responseJson != null) {
                        if (responseJson.isSuccess()) {
                            List<VersionVO> versionVOList = responseJson.getVersionVOList();
                            if (ListTool.noEmpty(versionVOList)) {
                                VersionVO versionVO1 = versionVOList.get(0);
                                LogTool.d(TAG, MessageConstants.CHECK_UPDATE_SUCCESS);
                                LogTool.d(TAG, versionVO1);
                                if (versionVO1 != null) {
                                    matchLocalVersion(versionVO);
                                } else {
                                    view.checkUpdateFailure();
                                }
                            } else {
                                view.checkUpdateFailure();
                            }
                        } else {
                            LogTool.d(TAG, MessageConstants.CHECK_UPDATE_FAILED);
                            view.checkUpdateFailure();
                        }
                    }
                } else {
                    view.checkUpdateFailure();
                }
            }

            @Override
            public void onFailure(Call<ResponseJson> call, Throwable throwable) {
                view.hideLoading();
                LogTool.d(TAG, MessageConstants.CHECK_UPDATE_FAILED);
                LogTool.d(TAG, throwable.getCause());
                view.checkUpdateFailure();
            }
        });

    }

    /*将服务器获取的数据与当前数据库的的版本信息进行比对，查看是否需要更新*/
    private void matchLocalVersion(VersionVO versionVO) {
        LogTool.d(TAG, this);
        //1:得到当前APP的版本code
        int currentVersionCode = VersionTool.getVersionCode(context);
        //2:得到服务器返回的更新信息
        String version = versionVO.getVersion();
        int forceUpgrade = versionVO.getForceUpgrade();
        String updateUrl = versionVO.getUpdateUrl();
        String type = versionVO.getType();
        String modifyTime = versionVO.getMotifyTime();
        String systermTime = versionVO.getSystemTime();
        //3:判断呢是否强制更新
        if (forceUpgrade == 1) {
            //提示用户更新
            view.updateVersion(true);

        } else {
            //4:否则比较版本是否落后
            if (currentVersionCode < Integer.valueOf(version)) {
                //5:提示用户更新
                view.updateVersion(false);

            }

        }

    }

    @Override
    public void checkVerify() {
        view.showLoading();
        if (!BcaasApplication.isRealNet()) {
            view.noNetWork();
            view.hideLoading();
            return;
        }
        super.checkVerify();
    }
}
