package io.bcaas.listener;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/21
 * TCP  连接R区块的监听
 */
public interface TCPReceiveBlockListener {
    void httpToRequestReceiverBlock();//http请求开始

    void receiveBlockData(String data);

    void resetANSocket();//重置socket

}
