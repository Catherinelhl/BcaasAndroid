package io.bcaas.listener;

/**
 * @author catherine.brainwilliam
 * @since 2018/10/19
 * <p>
 * 回調監聽：Http 請求關於「交易」接口的監聽
 */
public interface HttpTransactionListener {

    //交易记录已经存在
    void transactionAlreadyExists();

    //Http请求 「签章区块」成功
    void receiveBlockHttpSuccess();

    //Http请求 「签章区块」失败
    void receiveBlockHttpFailure();
}
