package com.manager.platform;

import com.manager.ProcessManager;

/**
 * @author Boris
 * @description 
 * 2016��9��13��
 */
public class ProcessManagerWin extends ProcessManager {

	private String processName;
	public ProcessManagerWin(String processName){
		super();
		this.processName = processName;
	}

	@Override
	protected String getProcessPID() {
		// TODO Auto-generated method stub
		String processPID = null;
		
		if (processName.endsWith(".exe")) {//exe ����
			String msg = tools.executeCommand("tasklist");
			String[] msgs = msg.split("\n");
			
			for (String str: msgs) {
				if (str.startsWith(processName)) {
					String[] infos = str.split("\\s\\s*");  // \s*��ʾ0�������ϵĿո�   ƥ��һ�����ϵĿո�
					processPID = infos[1];
					
					break;
				}
			}
		}else{//java ����
			String msg = tools.executeCommand("jps -l");
			String[] msgs = msg.split("\n");
			
			for (String str: msgs) {
				if (str.endsWith(processName)) {
					String[] infos = str.split("\\s\\s*");  // \s*��ʾ0�������ϵĿո�   ƥ��һ�����ϵĿո�
					processPID = infos[0];
					
					break;
				}
			}
		}
		
		return processPID;
	}

	@Override
	public int getProcessMemoryUsed() {
		// TODO Auto-generated method stub
		//tasklist�Ĺؼ��� exe�������ֱ���ó�����  java���� ��Ҫ�Խ���id
		String key = null;
		if (processName.endsWith(".exe")) {
			key = processName;
		}else{
			key = getProcessPID();
		}
		
		if(key == null) return 0;
		
		String msg = tools.executeCommand("tasklist");
		String[] msgs = msg.split("\n");
		
		String processMemory = null;
		
		for (String str: msgs) {
//			System.out.println(str);
			if (str.contains(key)) {
				String[] infos = str.split("\\s\\s*");  // \s*��ʾ0�������ϵĿո�   ƥ��һ�����ϵĿո�
				processMemory = infos[4];
				break;
			}
		}
		return Integer.parseInt(processMemory.replace(",", ""));
	}

	@Override
	public double getProcessCpuUsed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void closeProcess() {
		// TODO Auto-generated method stub
		String PID = getProcessPID();
		if (PID != null) {
			tools.executeCommand(String.format("taskkill /PID %s /F", PID));
		}
	}

}
