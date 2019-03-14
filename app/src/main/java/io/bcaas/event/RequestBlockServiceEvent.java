package io.bcaas.event;

/**
 * @author catherine.brainwilliam
 * @since 2018/12/21
 * 發出此訂閱：重新请求币种列表事件
 */
public class RequestBlockServiceEvent {

    private String from;

    public RequestBlockServiceEvent(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }
}
