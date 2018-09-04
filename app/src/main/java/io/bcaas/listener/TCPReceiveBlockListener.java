package io.bcaas.listener;

import java.util.List;

import io.bcaas.vo.PaginationVO;
import io.bcaas.vo.TransactionChainVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/21
 * TCP  连接R区块的监听
 */
public interface TCPReceiveBlockListener {
    void httpToRequestReceiverBlock();//http请求开始

    void stopToHttpToRequestReceiverBlock();//http请求停止

    void haveTransactionChainData(List<TransactionChainVO> transactionChainVOList);

    void signatureTransaction(TransactionChainVO transactionChain);//已经签章好的交易

    void noTransactionChainData();

    void restartSocket();//重置socket

    void resetANAddress();//重新获取AN的信息

    void sendTransactionFailure(String message);//发送失败

    void sendTransactionSuccess(String message);//发送成功

    void showWalletBalance(String i);//显示当前余额

    void canNotModifyRepresentative();//不能修改授权代表

    void intentToModifyRepresentative();

}
