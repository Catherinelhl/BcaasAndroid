package io.bcaas.event;

/**
 * BcaasAndroid
 * <p>
 * io.bcaas.event
 * <p>
 * created by catherine in 九月/04/2018/下午4:25
 * <p>
 * 修改授权代表结果
 */
public class ModifyRepresentativeResult {

    private boolean isSuccess;

    public ModifyRepresentativeResult(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
