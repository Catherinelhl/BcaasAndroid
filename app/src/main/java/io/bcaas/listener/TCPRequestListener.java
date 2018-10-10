package io.bcaas.listener;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/21
 * TCP  连接R区块的监听
 */
public interface TCPRequestListener {
    /*http请求开始*/
    void httpToRequestReceiverBlock();

    /*http请求停止*/
    void stopToHttpToRequestReceiverBlock();

    /*发送失败*/
    void sendTransactionFailure(String message);

    /*发送成功*/
    void sendTransactionSuccess(String message);

    /*显示当前余额*/
    void showWalletBalance(String i);

    /*跳转修改授权代表*/
    void getPreviousModifyRepresentative(String representative);

    /*修改授权代表结果*/
    void modifyRepresentativeResult(String currentStatus, boolean isSuccess, int code);

    /*跳转登录*/
    void reLogin();

    /*余额不足*/
    void noEnoughBalance();

    /*金额异常*/
    void amountException();

    /*tcp返回数据异常*/
    void tcpResponseDataError(String nullWallet);

    /*获取数据异常*/
    void getDataException(String message);

    void refreshTransactionRecord();

    /*刷新TCP连接的IP信息*/
    void refreshTCPConnectIP(String ip);
}
