package io.bcaas.ui.contracts;

import java.util.List;

import io.bcaas.vo.TransactionChainVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/21
 */
public interface MainContracts {
    interface View extends BaseContract.HttpView {
        void noAnClientInfo();

        void showPaginationVoList(List<TransactionChainVO> transactionChainVOList);//显示未产生的R区块

        void sendTransactionFailure(String message);//发送失败

        void sendTransactionSuccess(String message);//发送成功

        void showWalletBalance(String walletBalance);
    }

    interface Presenter extends BaseContract.HttpPresenter {
        void startTCPConnectToGetReceiveBlock();//开始TCP连线，请求未处理的交易

        void checkANClientIPInfo(String from);

        void unSubscribe();
    }
}

