package io.bcaas.listener;

import io.bcaas.vo.ClientIpInfoVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/24
 * <p>
 * 请求结果监听
 */
public interface RequestResultListener {
    void resetAuthNodeFailure(String message);
    void resetAuthNodeSuccess(ClientIpInfoVO clientIpInfoVO);
}
