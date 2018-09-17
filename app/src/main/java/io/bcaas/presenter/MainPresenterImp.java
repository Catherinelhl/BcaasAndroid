package io.bcaas.presenter;


import java.util.ArrayList;
import java.util.List;

import io.bcaas.base.BaseHttpPresenterImp;
import io.bcaas.base.BcaasApplication;
import io.bcaas.bean.WalletBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.http.tcp.TCPThread;
import io.bcaas.listener.TCPRequestListener;
import io.bcaas.requester.BaseHttpRequester;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.VersionTool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.ui.contracts.MainContracts;
import io.bcaas.vo.ClientIpInfoVO;
import io.bcaas.vo.PublicUnitVO;
import io.bcaas.vo.VersionVO;
import io.bcaas.vo.WalletVO;
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
    public void checkANClientIPInfo(String from) {
        //根据当前的进入方式去检查此钱包的AN访问地址
        if (StringTool.isEmpty(from)) {
            return;
        }
        if (StringTool.equals(from, Constants.ValueMaps.FROM_BRAND)) {
            //如果当前用户是直接进入的，那么需要从数据库里面拿到之前存储的AN请求IP
            ClientIpInfoVO clientIpInfoVO = BcaasApplication.getClientIpInfoVO();
            if (clientIpInfoVO == null) {
                //没有数据，需要重新reset
                view.noAnClientInfo();
            } else {
                LogTool.d(TAG, clientIpInfoVO);
                startTCP();
            }
        } else {//如果是重新「登录」进入，那么就重新获取子节点信息
            onResetAuthNodeInfo();
        }

    }


    /*开启连线
     * 1：通过TCP传给服务器的数据不需要加密
     * 2:开始socket连线之后，然后Http请求该接口，通知服务器可以下发数据了。
     * */
    @Override
    public void startTCP() {
        LogTool.d(TAG, MessageConstants.START_TCP);
        WalletBean walletBean = BcaasApplication.getWalletBean();
        if (walletBean == null) {
            return;
        }
        WalletVO walletVO = new WalletVO(
                walletBean.getAddress(),
                BcaasApplication.getBlockService(),
                BcaasApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN));
        RequestJson requestJson = new RequestJson(walletVO);
        String json = GsonTool.string(requestJson);
        /*先保證沒有其他socket在工作*/
        stopTCP();
        TCPThread TCPThread = new TCPThread(json + "\n", tcpRequestListener);
        TCPThread.start();

    }

    //监听Tcp数据返回
    TCPRequestListener tcpRequestListener = new TCPRequestListener() {
        @Override
        public void httpToRequestReceiverBlock() {
            startToGetWalletWaitingToReceiveBlockLoop();
        }

        @Override
        public void sendTransactionFailure(String message) {
            view.sendTransactionFailure(message);
        }

        @Override
        public void sendTransactionSuccess(String message) {
            view.sendTransactionSuccess(message);
        }

        @Override
        public void showWalletBalance(String walletBalance) {
            view.showWalletBalance(walletBalance);
        }

        @Override
        public void stopToHttpToRequestReceiverBlock() {
            stopToHttpGetWalletWaitingToReceiveBlock();
        }

        @Override
        public void toModifyRepresentative(String representative) {
            view.toModifyRepresentative(representative);
        }

        @Override
        public void modifyRepresentativeResult(String currentStatus, boolean isSuccess, int code) {
            view.modifyRepresentativeResult(currentStatus, isSuccess, code);

        }

        @Override
        public void toLogin() {
            view.toLogin();
        }

        @Override
        public void noEnoughBalance() {
            view.noEnoughBalance();

        }

        @Override
        public void tcpResponseDataError(String nullWallet) {
            view.tcpResponseDataError(nullWallet);
        }

        @Override
        public void getDataException(String message) {
            view.getDataException(message);
        }
    };

    @Override
    public void unSubscribe() {
        super.unSubscribe();
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
                                BcaasApplication.setStringToSP(Constants.Preference.BLOCK_SERVICE_LIST, GsonTool.getGson().toJson(publicUnitVOListNew));
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

    @Override
    public void checkUpdate() {
        VersionVO versionVO = new VersionVO(Constants.ValueMaps.AUTHKEY);
        RequestJson requestJson = new RequestJson(versionVO);
        LogTool.d(TAG, requestJson);
        RequestBody requestBody = GsonTool.beanToRequestBody(requestJson);
        baseHttpRequester.checkUpdate(requestBody, new Callback<ResponseJson>() {
            @Override
            public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
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
                                }
                            }
                        } else {
                            LogTool.d(TAG, MessageConstants.CHECK_UPDATE_FAILED);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseJson> call, Throwable t) {
                LogTool.d(TAG, MessageConstants.CHECK_UPDATE_FAILED);
                LogTool.d(TAG, t.getMessage());
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
}
