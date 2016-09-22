package com.manager;

import java.awt.Desktop;
import java.io.File;

import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import com.utils.Log;
import com.utils.Tools;

/**
 * @author Boris
 * @description 
 * 2016年9月13日
 */
public abstract class ProcessManager {
	private String processId = null;
	
	protected static Tools tools = Tools.getTools();
	
	public abstract int getProcessMemoryUsed();
	public abstract double getProcessCpuUsed();
	public abstract void closeProcess();
	
	protected  String getProcessPID(){
		return processId;
	}
	
	public void setProcessId(String processId){
		this.processId = processId;
	}
	
	public boolean isProcessRunning(){
		return processId != null;
	}
	
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
	
	public long getRunTime(){
		long runTime =  0;
		if (processId  != null) {
			Sigar sigar = new Sigar();
			try {
				long pid = Long.parseLong(processId);
				ProcCpu procCpu = sigar.getProcCpu(pid);
				procCpu.gather(sigar, pid);
				long startTime = procCpu.getStartTime();
				runTime = tools.getBetweenCurrrentTime(startTime);
			} catch (SigarException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return runTime;
	}
}
