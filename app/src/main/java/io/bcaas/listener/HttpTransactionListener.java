package io.bcaas.listener;

/**
 * @author catherine.brainwilliam
 * @since 2018/10/19
 * <p>
 * Http 交易的监听
 */
public interface HttpTransactionListener {

    //交易记录已经存在
    void transactionAlreadyExists();

    //Http请求 「签章区块」成功
    void receiveBlockHttpSuccess();

    //Http请求 「签章区块」失败
    void receiveBlockHttpFailure();
}
