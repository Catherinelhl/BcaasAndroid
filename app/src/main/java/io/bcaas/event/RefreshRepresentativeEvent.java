package io.bcaas.event;

/**
 * @projectName: BcaasAndroid
 * @packageName: io.bcaas.event
 * @author: catherine
 * @time: 2018/9/10
 * 發出此訂閱：將当前通過TCP返回的的授权人地址更新在界面上
 */
public class RefreshRepresentativeEvent {
    private String representative;

    public RefreshRepresentativeEvent(String representative) {
        this.representative = representative;
    }

    public String getRepresentative() {
        return representative;
    }
}
