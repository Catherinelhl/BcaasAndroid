package io.bcaas.base;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/22
 */
public interface BaseAuthNodeView extends BaseView {
    void httpANSuccess();//http请求R成功
    void httpANFailure();//http请求R失败

    void resetAuthNodeFailure(String message);//重设AN失败

    void resetAuthNodeSuccess();//重设AN成功

}
