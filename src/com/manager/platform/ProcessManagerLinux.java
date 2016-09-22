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
	public int getProcessMemoryUsed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getProcessCpuUsed() {
		// TODO Auto-generated method stub
		//top -u oracle
		return 0;
	}

	@Override
	public void closeProcess() {
		// TODO Auto-generated method stub

	}

}
