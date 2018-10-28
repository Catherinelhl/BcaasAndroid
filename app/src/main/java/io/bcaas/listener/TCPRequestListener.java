package io.bcaas.listener;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/21
 * TCP  连接R区块的监听
 */
public interface TCPRequestListener {
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

    //「签章成功」显示通知提示
    void showNotification(String blockService, String amount);

    /*刷新TCP连接的IP信息*/
    void refreshTCPConnectIP(String ip);

    void resetSuccess();

    void needUnbindService();
}
