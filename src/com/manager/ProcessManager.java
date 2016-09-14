package com.manager;

import com.utils.Tools;

/**
 * @author Boris
 * @description 
 * 2016Äê9ÔÂ13ÈÕ
 */
public abstract class ProcessManager {
	protected static Tools tools = Tools.getTools();
	
	protected abstract String getProcessPID();
	public abstract int getProcessMemoryUsed();
	public abstract double getProcessCpuUsed();
	public abstract void closeProcess();
}
