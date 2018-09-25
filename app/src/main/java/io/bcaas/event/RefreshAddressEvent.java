package io.bcaas.event;

import java.io.Serializable;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/17
 * <p>
 * 更新地址信息的事件
 */
public class RefreshAddressEvent implements Serializable {

    private String result;

    public RefreshAddressEvent(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }
}
