package io.bcaas.vo;

import java.io.Serializable;

/**
 * 尚未定義
 * 
 * @since 2015-11-11
 * 
 * @author Costa
 * 
 * @version 1.0.0
 * 請求服務器需要傳遞的參數：dataChainVO
 */

public class DataChainVO implements Serializable {

	private static final long serialVersionUID = 1L;

	// The 12-byte ObjectId value consists of:
	// a 4-byte value representing the seconds since the Unix epoch,
	// a 3-byte machine identifier,
	// a 2-byte process id,
	// a 3-byte counter, starting with a random value.
	private String _id;

	// System time
	private String systemTime;

	public DataChainVO() {
		super();
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getSystemTime() {
		return systemTime;
	}

	public void setSystemTime(String systemTime) {
		this.systemTime = systemTime;
	}

}
