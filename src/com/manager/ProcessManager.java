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
	public abstract String getProcessMemory();
	public abstract String getProcessCPU();
	public abstract void closeProcess();
}
