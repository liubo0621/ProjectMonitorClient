package com.pojo;

/**
 * @author Boris
 * @description 
 * 2016年9月8日
 */
public class PhysicalMemory {
	private String phyName;
	private String sysTypeName;//文件系统类型
	private String typeName; //文件系统类型名
	private long memory;
	private long memoryFree;
	private long memoryUsed;
	private long memoryAvail;
	private double usePercent;
	public String getPhyName() {
		return phyName;
	}
	public void setPhyName(String phyName) {
		this.phyName = phyName;
	}
	public String getSysTypeName() {
		return sysTypeName;
	}
	public void setSysTypeName(String sysTypeName) {
		this.sysTypeName = sysTypeName;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public long getMemory() {
		return memory;
	}
	public void setMemory(long memory) {
		this.memory = memory;
	}
	public long getMemoryFree() {
		return memoryFree;
	}
	public void setMemoryFree(long memoryFree) {
		this.memoryFree = memoryFree;
	}
	public long getMemoryUsed() {
		return memoryUsed;
	}
	public void setMemoryUsed(long memoryUsed) {
		this.memoryUsed = memoryUsed;
	}
	public long getMemoryAvail() {
		return memoryAvail;
	}
	public void setMemoryAvail(long memoryAvail) {
		this.memoryAvail = memoryAvail;
	}
	public double getUsePercent() {
		return usePercent;
	}
	public void setUsePercent(double usePercent) {
		this.usePercent = usePercent;
	}
	
	
}
