package io.bcaas.event;

/**
 * @author catherine.brainwilliam
 * @since 2018/10/15
 * <p>
 * 发送当前交易
 */
public class SendTransactionEvent {

    //此事件的状态
    private String status;
    //当前输入的密码
    private String password;

    public SendTransactionEvent(String status) {
        super();
        this.status = status;
    }

    public SendTransactionEvent(String status, String password) {
        super();
        this.status = status;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getStatus() {
        return status;
    }
}
