package io.bcaas.vo;

import java.io.Serializable;
import java.util.List;

/**
 * API key
 * <p>
 * 請求服務器需要傳遞的參數：apiKey
 * 用於匹配當前是否需要更新Client APP
 */

public class APIKeyVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> apiKeyList;

    public APIKeyVO() {
        super();
    }

    public APIKeyVO(List<String> apiKeyList) {
        super();
        this.apiKeyList = apiKeyList;
    }

    public List<String> getApiKeyList() {
        return apiKeyList;
    }

    public void setApiKeyList(List<String> apiKeyList) {
        this.apiKeyList = apiKeyList;
    }

}
