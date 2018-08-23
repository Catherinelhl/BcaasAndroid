package io.bcaas.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * API key
 *
 */

public class APIKey implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<String> apiKeyList;

	public APIKey() {
		super();
	}

	public APIKey(List<String> apiKeyList) {
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
