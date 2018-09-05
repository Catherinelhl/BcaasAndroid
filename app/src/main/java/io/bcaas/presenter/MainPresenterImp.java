package io.bcaas.presenter;


import java.util.ArrayList;
import java.util.List;

import io.bcaas.base.BaseHttpPresenterImp;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.tools.ecc.Wallet;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.http.thread.ReceiveThread;
import io.bcaas.requester.BaseHttpRequester;
import io.bcaas.listener.TCPReceiveBlockListener;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.StringTool;
import io.bcaas.ui.contracts.MainContracts;
import io.bcaas.vo.ClientIpInfoVO;
import io.bcaas.vo.PublicUnitVO;
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
                BcaasLog.d(TAG, clientIpInfoVO);
                startTCPConnectToGetReceiveBlock();
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
    public void startTCPConnectToGetReceiveBlock() {
        Wallet wallet = BcaasApplication.getWallet();
        if (wallet == null) {
            return;
        }
        WalletVO walletVO = new WalletVO(
                wallet.getAddress(),
                BcaasApplication.getBlockService(),
                BcaasApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN));
        RequestJson requestJson = new RequestJson(walletVO);
        String json = GsonTool.encodeToString(requestJson);
        /*先保證沒有其他socket在工作*/
        stopThread();
        ReceiveThread receiveThread = new ReceiveThread(json + "\n", tcpReceiveBlockListener);
        receiveThread.start();

    }

    //监听Tcp数据返回
    TCPReceiveBlockListener tcpReceiveBlockListener = new TCPReceiveBlockListener() {
        @Override
        public void httpToRequestReceiverBlock() {
            startToGetWalletWaitingToReceiveBlockLoop();
        }

        @Override
        public void haveTransactionChainData(List<TransactionChainVO> transactionChainVOList) {
            //得到尚未产生的Receiver区块
            //遍历每一条数据，然后对每一条数据进行签章，然后方给服务器
            view.showTransactionChainView(transactionChainVOList);
        }

        @Override
        public void restartSocket() {
            startTCPConnectToGetReceiveBlock();
        }

        @Override
        public void resetANAddress() {
            onResetAuthNodeInfo();
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
        public void noTransactionChainData() {
            view.hideTransactionChainView();

        }

        @Override
        public void signatureTransaction(TransactionChainVO transactionChain) {
            view.signatureTransaction(transactionChain);
        }

        @Override
        public void canNotModifyRepresentative() {
            view.canNotModifyRepresentative();
        }

        @Override
        public void intentToModifyRepresentative() {
            view.intentToModifyRepresentative();
        }

        @Override
        public void modifyRepresentative(boolean isSuccess) {
            view.modifyRepresentative(isSuccess);

        }

        @Override
        public void toLogin() {
            view.toLogin();
        }
    };

    @Override
    public void unSubscribe() {
        super.unSubscribe();
    }

    @Override
    public void stopThread() {
        ReceiveThread.kill();
    }

    @Override
    public void getBlockServiceList() {
        WalletVO walletVO = new WalletVO();
        walletVO.setWalletAddress(BcaasApplication.getWalletAddress());
        RequestJson requestJson = new RequestJson(walletVO);
        BcaasLog.d(TAG, requestJson);
        RequestBody requestBody = GsonTool.beanToRequestBody(requestJson);
        baseHttpRequester.getBlockServiceList(requestBody, new Callback<ResponseJson>() {
            @Override
            public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                ResponseJson responseJson = response.body();
                BcaasLog.d(TAG, response.body());
                if (responseJson != null) {
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
                            BcaasApplication.setStringToSP(Constants.Preference.BLOCK_SERVICE_LIST, GsonTool.getGsonBuilder().toJson(publicUnitVOListNew));
                            view.getBlockServicesListSuccess(publicUnitVOListNew);
                        } else {
                            view.noBlockServicesList();
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseJson> call, Throwable t) {
                BcaasLog.d(TAG, t.getMessage());

            }
        });

    }
}
