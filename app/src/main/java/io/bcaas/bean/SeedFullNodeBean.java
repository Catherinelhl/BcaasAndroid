package io.bcaas.bean;

/**
 * SNF節點資訊
 * 
 * @author Andy Wang
 */
public class SeedFullNodeBean {

	private String ip;

	private int port;

	private int rpcPort;

	// BCT為0,同步資料用
	private int deviceLevel;

	public SeedFullNodeBean() {

	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getRpcPort() {
		return rpcPort;
	}

	public void setRpcPort(int rpcPort) {
		this.rpcPort = rpcPort;
	}

	public int getDeviceLevel() {
		return deviceLevel;
	}

	public void setDeviceLevel(int deviceLevel) {
		this.deviceLevel = deviceLevel;
	}

}
