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
 * 2016��9��13��
 */
public abstract class ProcessManager {
	private String processId = null;
	private long runTime = 0;
	private long cpuLoadTime = 0;
	
	protected static Tools tools = Tools.getTools();
	
	public abstract int getProcessMemoryUsed();
	public abstract void closeProcess();
	
	protected  String getProcessPID(){
		return processId;
	}
	
	public void setProcessId(String processId){
		this.processId = processId;
	}
	
	public double getProcessCpuUsed(){
		double cpuUsed = 0;
		if (processId != null) {
			Long pid = Long.parseLong(processId);
			Sigar sigar = new Sigar();
			try {
				ProcCpu curPc = sigar.getProcCpu(pid);
				runTime = getRunTime()  - runTime;
				cpuLoadTime = curPc.getTotal() + curPc.getUser() - cpuLoadTime;
				int cpuCount = sigar.getCpuList().length;
				
				cpuUsed =  100.0 * cpuLoadTime /  runTime / cpuCount;
				
			} catch (SigarException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		return cpuUsed;
	}

	public boolean isProcessRunning(){
		return processId != null;
	}
	
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
	
	/**
	 * @Method: getRunTime 
	 * @Description:
	 * @return ����
	 * long
	 */
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
