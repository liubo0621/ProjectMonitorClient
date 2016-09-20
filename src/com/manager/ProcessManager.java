package com.manager;

import java.awt.Desktop;
import java.io.File;

import com.utils.Log;
import com.utils.Tools;

/**
 * @author Boris
 * @description 
 * 2016年9月13日
 */
public abstract class ProcessManager {
	protected static Tools tools = Tools.getTools();
	
	protected abstract String getProcessPID();
	public abstract int getProcessMemoryUsed();
	public abstract double getProcessCpuUsed();
	public abstract void closeProcess();
	
	// 使用Desktop启动应用程序
	public void startProcess(String processPath) {
		Log.out.info("启动应用程序：" + processPath);
		try {
			Desktop.getDesktop().open(new File(processPath));
		} catch (Exception e) {
			e.printStackTrace();
			Log.out.error("应用程序：" + processPath + "不存在！");
		}
	}
}
