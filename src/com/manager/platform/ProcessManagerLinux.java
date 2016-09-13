package com.manager.platform;

import com.manager.ProcessManager;

/**
 * @author Boris
 * @description 
 * 2016Äê9ÔÂ13ÈÕ
 */
public class ProcessManagerLinux extends ProcessManager {

	private String processName;
	public ProcessManagerLinux(String processName){
		super();
		this.processName = processName;
	}
	
	@Override
	protected String getProcessPID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProcessMemory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProcessCPU() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void closeProcess() {
		// TODO Auto-generated method stub

	}

}
