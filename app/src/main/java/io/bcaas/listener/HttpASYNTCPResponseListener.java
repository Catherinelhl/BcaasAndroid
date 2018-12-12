package io.bcaas.listener;

import io.bcaas.vo.ClientIpInfoVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/10/10
 * <p>
 * 回調監聽：Http 异步响应给TCP数据
 */
public interface HttpASYNTCPResponseListener {
    //获取最后change区块成功
    void getLatestChangeBlockSuccess();

    //获取最后Change区块失败
    void getLatestChangeBlockFailure(String failure);

    void resetSuccess(ClientIpInfoVO clientIpInfoVO);

    void resetFailure();

    void logout();

    //发送交易失败
    void sendFailure();

    //可以reset SAN
    void canReset();

    void verifySuccess(String from);

    void verifyFailure(String from);
}
