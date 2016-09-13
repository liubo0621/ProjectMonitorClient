package com.client;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.utils.Log;
import com.utils.Tools;

/**
 * @author Boris
 * @description 
 * 2016年9月9日
 */
public abstract class Client implements Runnable{
	protected Tools tools;
	
	private int readFilePerSec;
	private long processStartTime = 0; //应用程序开始时间  为第一次 读到文件时间
	
	private String statusFileName; //应用程序写出的文件
	private String processName;
	private String commandFileName;
	private String process; // 应用程序 process1 process2 ...
	
	public Client(int processSequence){
		tools = Tools.getTools();
		
		process = "process" + processSequence;
		processName = tools.getProperty(process + ".name");
		Log.out.debug("管理应用程序< " + processName + " >");
		
		readFilePerSec = Integer.parseInt(tools.getProperty("client.read_file_time")) * 1000;
		statusFileName = tools.getProperty(process + ".status_file");
		commandFileName = tools.getProperty(String.format("client%d.command_file", processSequence));
	}
	
	
	
	public void getMemoryUsed(){
		String msg = tools.executeCommand("tasklist");
		String[] msgs = msg.split("\n");
		
		String taskName = null;
		String taskPid = null;
		String taskMemory = null;
		
		for (String str: msgs) {
			if (str.startsWith("smss.exe")) {
				System.out.println(str);
				
				String[] infos = str.split("\\s\\s*");  // \s*表示0个或以上的空格   匹配一个以上的空格
				
				taskName = infos[0];
				taskPid = infos[1];
				taskMemory = infos[4];
			}
		}
		System.out.println("taskName " + taskName + " taskPid " + taskPid + " taskMemory " + taskMemory);
		
	}
	
	public String readFile(){
		while(true){
			try {
				String msg = tools.readFile(statusFileName).replace("\\n", "");
				if (msg != "") {
					if (processStartTime ==  0) {
						processStartTime = tools.getCurrentSecond();
					}
					
					dealReadedMsg(msg);
				}
				Thread.sleep(readFilePerSec);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.out.error(e);
			}
		}
	}
	
	private String getKeyValue(String str, String key){
		int beginPos = str.indexOf(key) + key.length() + 1;
		int endPos = str.indexOf(",", beginPos) == -1 ? str.indexOf("/>", beginPos) : str.indexOf(",", beginPos);
		
		return str.substring(beginPos, endPos);
	}

    
	private void dealReadedMsg(String msg){
		String []msgs = msg.split("/>");
		for (int i = 0; i < msgs.length; ++i) {
			String str = msgs[i] + "/>";
			Log.out.debug("read - " + str);
			
			//向服务器发送收到的信息
			
			//崩溃
			if (getKeyValue(str, "crash").equals("true")) {
				//1. 关闭应用程序
				closeProcess();
				
				//2. 向应用程序发送导致crash的taskId
				String taskId = getKeyValue(str, "task_id");
				tools.writeFile(commandFileName, "TASK:CRASH " + taskId);
				Log.out.info("崩溃： taskId = " + taskId);
				
				//3. 重启应用程序
				String processExeFile = tools.getProperty(process + ".execute_file");
				tools.startProgram(processExeFile);
			}
			//判断是否超时 超时停止任务
			else{
				//判断当前任务是否执行完 若执行完 则没超时
				boolean isDone = false;
				String taskId = getKeyValue(str, "task_id");
				for (int j = i; j < msgs.length; j++) {
					String strTemp = msgs[j];
					//当前任务是否收到done状态
					if (strTemp.contains("task_id="+taskId) && getKeyValue(strTemp, "task_status").equals("done")) {
						isDone = true;
						break;
					}
				}
				
				if (!isDone) {
					//求当前时间与写文件的时间差
					String writeFileTime = getKeyValue(str, "write_file_time");
					long betweenTime = tools.getBetweenCurrrentTime(writeFileTime);
					
					int taskTime = Integer.parseInt(getKeyValue(str, "task_length")) * 60;
					if (betweenTime > taskTime) {
						//超时  停止任务 （应用程序至任务状态 做下一任务）
						String threadId = getKeyValue(str, "thread_id");
						Log.out.info("超时： taskId = " + taskId + " threadId = " + threadId);
						tools.writeFile(commandFileName, String.format("TASK:STOP %s,%s", taskId, threadId));
					}
				}
				
			}
		}
	}
	
	public abstract void closeProcess();
	public abstract void restartServer();
	public abstract String getProcessMemory();
	public abstract String getProcessCpu();
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
