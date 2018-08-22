package io.bcaas.vo;

import java.io.Serializable;

/**
 * 
 * 2018-8-1
 * 
 * @author ableliu
 *
 */

public class GenesisVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String _id;

	private String previous;

	private String blockService;

	private String currencyUnit;

	private String work;

	private String systemTime;

	public GenesisVO() {
		super();
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getPrevious() {
		return previous;
	}

	public void setPrevious(String previous) {
		this.previous = previous;
	}

	public String getBlockService() {
		return blockService;
	}

	public void setBlockService(String blockService) {
		this.blockService = blockService;
	}

	public String getCurrencyUnit() {
		return currencyUnit;
	}

	public void setCurrencyUnit(String currencyUnit) {
		this.currencyUnit = currencyUnit;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getSystemTime() {
		return systemTime;
	}

	public void setSystemTime(String systemTime) {
		this.systemTime = systemTime;
	}

	@Override
	public String toString() {
		return "GenesisVO{" +
				"_id='" + _id + '\'' +
				", previous='" + previous + '\'' +
				", blockService='" + blockService + '\'' +
				", currencyUnit='" + currencyUnit + '\'' +
				", work='" + work + '\'' +
				", systemTime='" + systemTime + '\'' +
				'}';
	}
}
