package io.bcaas.listener;

/**
 * @author catherine.brainwilliam
 * @since 2018/10/10
 * <p>
 * 监听http  verify 接口请求返回数据
 */
public interface HttpVerifyRequestListener {

    void verifySuccess();

    void verifyFailure();
}
