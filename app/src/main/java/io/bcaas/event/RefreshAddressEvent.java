package io.bcaas.event;

import java.io.Serializable;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/17
 * <p>
 * 發出此訂閱：將通過「Scan」得到的帳戶地址傳送給「Send」頁面
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
