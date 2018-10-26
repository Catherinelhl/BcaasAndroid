package io.bcaas.listener;

/**
 * @author catherine.brainwilliam
 * @since 2018/10/20
 * <p>
 * 时间倒计时、定时监听
 */
public interface ObservableTimerListener {
    //时间到
    void timeUp(String from);
}
