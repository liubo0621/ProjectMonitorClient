package com.manager.platform;

import com.manager.ProcessManager;

/**
 * @author Boris
 * @description 
 * 2016年9月13日
 */
public class ProcessManagerLinux extends ProcessManager {
	@Override
	public int getProcessMemoryUsed() {
		// TODO Auto-generated method stub
		//top -p pid 查看程序的情况 
		//cat /proc/pid/status 
		return 0;
	}

	@Override
	public void closeProcess() {
		// TODO Auto-generated method stub
		//KILL -9 ID  //-9强制关

	}

}
