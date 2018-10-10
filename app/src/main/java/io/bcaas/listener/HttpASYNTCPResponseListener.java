package io.bcaas.listener;

import io.bcaas.vo.ClientIpInfoVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/10/10
 * <p>
 * 监听http 异步响应给TCP数据
 */
public interface HttpASYNTCPResponseListener {

    void resetSuccess(ClientIpInfoVO clientIpInfoVO);

    void resetFailure();
}
