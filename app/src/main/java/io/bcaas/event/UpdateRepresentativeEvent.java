package io.bcaas.event;

/**
 * @projectName: BcaasAndroid
 * @packageName: io.bcaas.event
 * @author: catherine
 * @time: 2018/9/10
 * 更新当前的授权人地址
 */
public class UpdateRepresentativeEvent {
    private String representative;

    public UpdateRepresentativeEvent(String representative) {
        this.representative = representative;
    }

    public String getRepresentative() {
        return representative;
    }
}
