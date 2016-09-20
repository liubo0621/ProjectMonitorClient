package com.manager;

import java.awt.Desktop;
import java.io.File;

import com.utils.Log;
import com.utils.Tools;

/**
 * @author Boris
 * @description 
 * 2016��9��13��
 */
public abstract class ProcessManager {
	protected static Tools tools = Tools.getTools();
	
	protected abstract String getProcessPID();
	public abstract int getProcessMemoryUsed();
	public abstract double getProcessCpuUsed();
	public abstract void closeProcess();
	
	// ʹ��Desktop����Ӧ�ó���
	public void startProcess(String processPath) {
		Log.out.info("����Ӧ�ó���" + processPath);
		try {
			Desktop.getDesktop().open(new File(processPath));
		} catch (Exception e) {
			e.printStackTrace();
			Log.out.error("Ӧ�ó���" + processPath + "�����ڣ�");
		}
	}
}
