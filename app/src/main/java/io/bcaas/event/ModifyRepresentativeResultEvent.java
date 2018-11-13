package io.bcaas.event;

/**
 * BcaasAndroid
 * <p>
 * io.bcaas.event
 * <p>
 * created by catherine in 九月/04/2018/下午4:25
 * <p>
 * 發出此訂閱：更新「修改授權代表」结果
 */
public class ModifyRepresentativeResultEvent {

    private boolean isSuccess;
    private int code;
    private String  currentStatus;

    public ModifyRepresentativeResultEvent(String currentStatus,boolean isSuccess,int code) {
        this.isSuccess = isSuccess;
        this.code=code;
        this.currentStatus=currentStatus;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public int getCode() {
        return code;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }
}
