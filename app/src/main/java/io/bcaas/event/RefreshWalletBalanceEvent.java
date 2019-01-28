package io.bcaas.event;

import io.bcaas.constants.Constants;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/24
 * <p>
 * 發出此訂閱：根據TCP返回的餘額信息發出通知，相關頁面收到通知更新界面餘額信息
 */
public class RefreshWalletBalanceEvent {

    //给谁接受此命令，接受者
    private Constants.EventSubscriber subscriber;

    public RefreshWalletBalanceEvent(Constants.EventSubscriber subscriber) {
        this.subscriber = subscriber;
    }

    public Constants.EventSubscriber getSubscriber() {
        return subscriber;
    }
}
