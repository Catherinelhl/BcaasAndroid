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
    /*http请求开始*/
    void httpToRequestReceiverBlock();

    /*http请求停止*/
    void stopToHttpToRequestReceiverBlock();

    void haveTransactionChainData(List<TransactionChainVO> transactionChainVOList);

    /*已经签章好的交易*/
    void signatureTransaction(TransactionChainVO transactionChain);

    void noTransactionChainData();

    /*重置socket*/
    void restartSocket();

    /*重新获取AN的信息*/
    void resetANAddress();

    /*发送失败*/
    void sendTransactionFailure(String message);

    /*发送成功*/
    void sendTransactionSuccess(String message);

    /*显示当前余额*/
    void showWalletBalance(String i);

    /*不能修改授权代表*/
    void canNotModifyRepresentative();

    /*跳转修改授权代表*/
    void toModifyRepresentative(String representative);

    /*修改授权代表结果*/
    void modifyRepresentativeResult(boolean isSuccess,int code);

    /*跳转登录*/
    void toLogin();

    /*余额不足*/
    void noEnoughBalance();

    /*tcp返回数据异常*/
    void tcpResponseDataError(String nullWallet);
}
