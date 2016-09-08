package com.pojo;


/**
 * @author Boris
 * @description 服务器
 * 2016年9月8日
 */
public class Server {
	private String serverIp;
	private String macAddr;
	private String system;
	private long memory;
	private long memoryUsed;
	private long memoryFree;
	private long swap;
	private long swapUsed;
	private long swapFree;
	
	public String getServerIp() {
		return serverIp;
	}
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	public String getMacAddr() {
		return macAddr;
	}
	public void setMacAddr(String macAddr) {
		this.macAddr = macAddr;
	}
	public String getSystem() {
		return system;
	}
	public void setSystem(String system) {
		this.system = system;
	}
	public long getMemory() {
		return memory;
	}
	public void setMemory(long memory) {
		this.memory = memory;
	}
	public long getMemoryUsed() {
		return memoryUsed;
	}
	public void setMemoryUsed(long memoryUsed) {
		this.memoryUsed = memoryUsed;
	}
	public long getMemoryFree() {
		return memoryFree;
	}
	public void setMemoryFree(long memoryFree) {
		this.memoryFree = memoryFree;
	}
	public long getSwap() {
		return swap;
	}
	public void setSwap(long swap) {
		this.swap = swap;
	}
	public long getSwapUsed() {
		return swapUsed;
	}
	public void setSwapUsed(long swapUsed) {
		this.swapUsed = swapUsed;
	}
	public long getSwapFree() {
		return swapFree;
	}
	public void setSwapFree(long swapFree) {
		this.swapFree = swapFree;
	}
}
