package io.bcaas.presenter;


import io.bcaas.base.BaseHttpPresenterImp;
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

import java.util.List;

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
    public void getAndroidVersionInfo() {
        VersionVO versionVO = new VersionVO(Constants.ValueMaps.AUTHKEY);
        RequestJson requestJson = new RequestJson(versionVO);
        LogTool.d(TAG, requestJson);
        RequestBody requestBody = GsonTool.beanToRequestBody(requestJson);
        baseHttpRequester.getAndroidVersionInfo(requestBody, new Callback<ResponseJson>() {
            @Override
            public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                view.hideLoading();
                if (response != null) {
                    ResponseJson responseJson = response.body();
                    if (responseJson != null) {
                        if (responseJson.isSuccess()) {
                            List<VersionVO> versionVOList = responseJson.getVersionVOList();
                            if (ListTool.noEmpty(versionVOList)) {
                                VersionVO versionVONew = versionVOList.get(0);
                                LogTool.d(TAG, MessageConstants.CHECK_UPDATE_SUCCESS);
                                if (versionVONew != null) {
                                    parseVersionInfo(versionVONew);
                                } else {
                                    view.getAndroidVersionInfoFailure();
                                }
                            } else {
                                view.getAndroidVersionInfoFailure();
                            }
                        } else {
                            LogTool.d(TAG, MessageConstants.CHECK_UPDATE_FAILED);
                            view.getAndroidVersionInfoFailure();
                        }
                    }
                } else {
                    view.getAndroidVersionInfoFailure();
                }
            }

            @Override
            public void onFailure(Call<ResponseJson> call, Throwable throwable) {
                view.hideLoading();
                LogTool.d(TAG, MessageConstants.CHECK_UPDATE_FAILED+throwable.getMessage());
                view.getAndroidVersionInfoFailure();
            }
        });

    }

    /**
     * 将服务器获取的数据与当前数据库的的版本信息进行比对，
     * 查看是否需要更新
     *
     * @param versionVO
     */
    private void parseVersionInfo(VersionVO versionVO) {
        LogTool.d(TAG, versionVO);
        //1:得到服务器返回的更新信息
        String versionName = versionVO.getVersion();
        //2：比对当前的versionName和服务器返回的Version进行比对
        if (VersionTool.needUpdate(versionName)) {
            LogTool.d(TAG, MessageConstants.NEED_UPDATE);
            int forceUpgrade = versionVO.getForceUpgrade();
            String updateUrl = versionVO.getUpdateUrl();
            String updateSourceUrl = versionVO.getUpdateSourceUrl();
            String appStoreUrl = versionVO.getAppStoreUrl();
            String type = versionVO.getType();
            String modifyTime = versionVO.getMotifyTime();
            String systermTime = versionVO.getSystemTime();
            //3:判断呢是否强制更新
            view.updateVersion(forceUpgrade == 1, appStoreUrl, updateUrl);
        } else {
            LogTool.d(TAG, MessageConstants.NOT_NEED_UPDATE);
        }

    }
}
