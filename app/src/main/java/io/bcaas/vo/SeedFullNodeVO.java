package io.bcaas.vo;

/**
 * SNF節點資訊
 * 
 * @author Andy Wang
 *
 * 請求服務器需要傳遞的參數：seedFullNodeBeanList
 * 用於得到「Login」接口返回的線上可用的 SFN 信息
 */
public class SeedFullNodeVO {

	private String ip;

	private int port;

	private int rpcPort;

	// BCT為0,同步資料用
	private int deviceLevel;

	public SeedFullNodeVO() {

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
