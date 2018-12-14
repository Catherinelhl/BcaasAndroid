package io.bcaas.event;

/**
 * @author catherine.brainwilliam
 * @since 2018/12/14
 *
 * 验证成功且充值SAN成功事件
 */
public class VerifySuccessAndResetAuthNodeEvent {
    private String from;
    public VerifySuccessAndResetAuthNodeEvent(String from){
        this.from=from;
    }

    public String getFrom() {
        return from;
    }
}
