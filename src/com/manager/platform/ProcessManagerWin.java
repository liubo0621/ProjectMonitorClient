package com.manager.platform;

import com.manager.ProcessManager;

/**
 * @author Boris
 * @description 
 * 2016年9月13日
 */
public class ProcessManagerWin extends ProcessManager {

	@Override
	public int getProcessMemoryUsed() {
		// TODO Auto-generated method stub
		//tasklist的关键字 exe程序可以直接用程序名  java程序 需要以进程id
		String key = getProcessPID();
		
		if(key == null) return 0;
		
		String msg = tools.executeCommand("tasklist");
		String[] msgs = msg.split("\n");
		
		String processMemory = "0";
		
		for (String str: msgs) {
//			System.out.println(str);
			if (str.contains(key)) {
				String[] infos = str.split("\\s\\s*");  // \s*表示0个或以上的空格   匹配一个以上的空格
				processMemory = infos[4];
				break;
			}
		}
		return Integer.parseInt(processMemory.replace(",", ""));
	}

	@Override
	public void closeProcess() {
		// TODO Auto-generated method stub
		String PID = getProcessPID();
		if (PID != null) {
			tools.executeCommand(String.format("taskkill /PID %s /F", PID));
			setProcessId(null);
		}
	}

}
