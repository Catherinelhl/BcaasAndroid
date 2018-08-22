package io.bcaas.listener;

import java.util.List;

import io.bcaas.vo.PaginationVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/21
 * TCP  连接R区块的监听
 */
public interface TCPReceiveBlockListener {
    void httpToRequestReceiverBlock();//http请求开始

    void receiveBlockData(List<PaginationVO> paginationVOS);

    void resetANSocket();//重置socket

}
