package com.manager.platform;

import com.manager.ProcessManager;

/**
 * @author Boris
 * @description 
 * 2016��9��13��
 */
public class ProcessManagerLinux extends ProcessManager {
	@Override
	public int getProcessMemoryUsed() {
		// TODO Auto-generated method stub
		//top -p pid �鿴�������� 
		//cat /proc/pid/status 
		return 0;
	}

	@Override
	public void closeProcess() {
		// TODO Auto-generated method stub
		//KILL -9 ID  //-9ǿ�ƹ�

	}

}
