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

    void receiveBlockData(List<TransactionChainVO> transactionChainVOList);

    void restartSocket();//重置socket

    void resetANAddress();//重置socket

    void sendTransactionFailure(String message);//发送失败
    void sendTransactionSuccess(String message);//发送成功
}
