package com.pojo;

/**
 * @author Boris
 * @description 
 * 2016年9月8日
 */
public class CPU {
	private int msz;//总容量
	private String vendor; //卖主
	private String model;
	private long chacheSize;
	private double systemUse;
	private double userUse;
	private double wait;
	private double idel;
	public double getSystemUse() {
		return systemUse;
	}
	public void setSystemUse(double systemUse) {
		this.systemUse = systemUse;
	}
	public double getUserUse() {
		return userUse;
	}
	public void setUserUse(double userUse) {
		this.userUse = userUse;
	}
	public double getWait() {
		return wait;
	}
	public void setWait(double wait) {
		this.wait = wait;
	}
	public double getIdel() {
		return idel;
	}
	public void setIdel(double idel) {
		this.idel = idel;
	}
	public int getMsz() {
		return msz;
	}
	public void setMsz(int msz) {
		this.msz = msz;
	}
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public long getChacheSize() {
		return chacheSize;
	}
	public void setChacheSize(long chacheSize) {
		this.chacheSize = chacheSize;
	}
	
	
}
