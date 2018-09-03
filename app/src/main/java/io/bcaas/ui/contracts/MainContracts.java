package io.bcaas.ui.contracts;

import java.util.List;

import io.bcaas.vo.PublicUnitVO;
import io.bcaas.vo.TransactionChainVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/21
 */
public interface MainContracts {
    interface View extends BaseContract.HttpView {
        void noAnClientInfo();

        void showTransactionChainView(List<TransactionChainVO> transactionChainVOList);//显示未产生的R区块

        void hideTransactionChainView();//隐藏当前首页显示「待交易」的区块
        void signatureTransaction(TransactionChainVO transactionChain);

        void sendTransactionFailure(String message);//发送失败

        void sendTransactionSuccess(String message);//发送成功

        void showWalletBalance(String walletBalance);

        void getBlockServicesListSuccess(List<PublicUnitVO> publicUnitVOList);//獲取清單文件成功

        void noBlockServicesList();// 沒有可顯示的幣種
    }

    interface Presenter extends BaseContract.HttpPresenter {
        void startTCPConnectToGetReceiveBlock();//开始TCP连线，请求未处理的交易

        void checkANClientIPInfo(String from);

        void unSubscribe();

        void stopThread();

        void getBlockServiceList();//獲取幣種清單
    }
}

