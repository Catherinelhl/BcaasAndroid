package io.bcaas.listener;

/**
 * @author catherine.brainwilliam
 * @since 2018/10/9
 * <p>
 * Http 响应的回调
 */
public interface HttpResponseListener {

    //获取最后change区块成功
    void getLatestChangeBlockSuccess();

    //获取最后Change区块失败
    void getLatestChangeBlockFailure(String failure);
}
