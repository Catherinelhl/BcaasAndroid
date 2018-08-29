package io.bcaas.presenter;


import java.util.List;

import io.bcaas.base.BaseHttpPresenterImp;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.ecc.Wallet;
import io.bcaas.gson.RequestJson;
import io.bcaas.http.thread.ReceiveThread;
import io.bcaas.requester.BaseHttpRequester;
import io.bcaas.listener.TCPReceiveBlockListener;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.GsonTool;
import io.bcaas.tools.StringTool;
import io.bcaas.ui.contracts.MainContracts;
import io.bcaas.vo.ClientIpInfoVO;
import io.bcaas.vo.TransactionChainVO;
import io.bcaas.vo.WalletVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/21
 * <p>
 * 后台需要和服务器建立长连接，进行R区块的请求
 */
public class MainPresenterImp extends BaseHttpPresenterImp
        implements MainContracts.Presenter {

    private String TAG = "MainPresenterImp";
    private MainContracts.View view;
    private BaseHttpRequester authNodeInteractor;

    public MainPresenterImp(MainContracts.View view) {
        super(view);
        this.view = view;
        authNodeInteractor = new BaseHttpRequester();
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
        Wallet wallet=BcaasApplication.getWallet();
        if (wallet == null) {
            return;
        }
        WalletVO walletVO = new WalletVO(
                wallet.getAddress(),
                BcaasApplication.getBlockServiceFromSP(),
                BcaasApplication.getAccessTokenFromSP());
        RequestJson requestJson = new RequestJson(walletVO);
        String json = GsonTool.encodeToString(requestJson);
        ReceiveThread.kill();
        ReceiveThread sendActionThread = new ReceiveThread(json + "\n", tcpReceiveBlockListener);
        sendActionThread.start();

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
            view.sendTransactionFailure(message);
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
    };

    @Override
    public void unSubscribe() {
        super.unSubscribe();
    }
}
