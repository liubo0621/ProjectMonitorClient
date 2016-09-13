package com.manager;

import com.utils.Tools;

/**
 * @author Boris
 * @description 
 * 2016��9��13��
 */
public abstract class ProcessManager {
	protected static Tools tools = Tools.getTools();
	
	protected abstract String getProcessPID();
	public abstract String getProcessMemory();
	public abstract String getProcessCPU();
	public abstract void closeProcess();
}
