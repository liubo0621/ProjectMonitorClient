package com.client;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import net.sf.json.JSONObject;

import org.hyperic.sigar.SigarException;

import com.manager.ProcessManager;
import com.manager.ServerManager;
import com.manager.platform.ProcessManagerLinux;
import com.manager.platform.ProcessManagerWin;
import com.manager.platform.ServerManagerLinux;
import com.manager.platform.ServerManagerWin;
import com.pojo.ClientMsg;
import com.pojo.CpuMsg;
import com.pojo.ExceptionMsg;
import com.pojo.PhysicalMemoryMsg;
import com.pojo.ProjectMsg;
import com.pojo.ServerMsg;
import com.pojo.ThreadMsg;
import com.utils.Constance;
import com.utils.Log;
import com.utils.Tools;

/**
 * @author Boris
 * @description 
 * 2016年9月9日
 */
public class Client implements Runnable{
	private Tools tools;
	private ProcessManager processManager;
	private ServerManager serverManager;
	private Socket socket = null;
	private OutputStream os = null;
	private InputStream is = null;
	
	private String statusFileName; //应用程序写出的文件
	private String processName;
	private String processExeFile;
	private String commandFileName;
	private String process; // 应用程序 process1 process2 ...
	private String serverIp;
	private int serverPort;
	private int readFilePerSec;
	private boolean flag;
	
	public static final int TRY_AGAIN = 5000;
	
	public static final String LISTEN_SERVER = "0";
	public static final String LISTEN_PROCESS_STATUS = "1";	
	
	public Client(int processSequence){
		tools = Tools.getTools();
		
		//init
		flag = true;
		process = "process" + processSequence;
		processName = tools.getProperty(process + ".name");
		processExeFile = tools.getProperty(process + ".execute_file");
		Log.out.debug("管理应用程序< " + processName + " >");
		
		readFilePerSec = Integer.parseInt(tools.getProperty("client.read_file_time")) * 1000;
		statusFileName = tools.getProperty(process + ".status_file");
		commandFileName = tools.getProperty(String.format("client%d.command_file", processSequence));
		
		//socket
		serverIp = tools.getProperty("service.ip");
		serverPort = Integer.parseInt(tools.getProperty("service.port"));
		createSocket();
		
		if (tools.isWindows()) {
			processManager =  new ProcessManagerWin();
			serverManager = new ServerManagerWin();
		}else{
			processManager =  new ProcessManagerLinux();
			serverManager = new ServerManagerLinux();
		}
	}
	
	private void closeSocket(){
		try {
			is.close();
			os.close();
			socket.close();
			is = null;
			os =  null;
			socket = null;
			flag = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void createSocket(){
		try {
			socket = new Socket(serverIp, serverPort);
			//stream
			os = socket.getOutputStream();
			is = socket.getInputStream();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void readFile(){
		while(flag){
			//如果socket 没有连接 则不读取文件
			if(socket == null){
				try {
					Thread.sleep(TRY_AGAIN);
					continue;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			try {
				String msg = tools.readFile(statusFileName).replace("\\n", "");
				if (msg != "") {
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
	
	private String setKeyValue(String str, String key, String value){
		String oldValue = getKeyValue(str, key);
		String oldKeyValue = key + "=" + oldValue;
		String newKeyValue = key + "=" + value;
		return str.replace(oldKeyValue, newKeyValue);
	}

    
	private void dealReadedMsg(String msg){
		String []msgs = msg.split("\n");
		for (int i = 0; i < msgs.length; ++i) {
			String str = msgs[i];
			Log.out.debug("read - " + str );
			
			String processId = getKeyValue(str, "process_id");
			processManager.setProcessId(processId);
			
			//非任务引起的崩溃
			if (getKeyValue(str, "exception").equals("true")) {
				//1. 关闭应用程序
				processManager.closeProcess();
				
				//2. 启动应用程序
				processManager.startProcess(processExeFile);
			}
			//任务异常
			else if(getKeyValue(str, "task_status").equals(String.valueOf(Constance.TaskStatus.EXCEPTION))){
				//  停止任务 （应用程序至任务状态 做下一任务）
				String threadId = getKeyValue(str, "thread_id");
				String taskId = getKeyValue(str, "task_id");
				Log.out.info("任务异常： taskId = " + taskId + " threadId = " + threadId);
				tools.writeFile(commandFileName, String.format("TASK:STOP %s,%s", taskId, threadId));
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
					long betweenTime = tools.getBetweenCurrrentTime(writeFileTime) / 1000L;
					
					int taskTime = Integer.parseInt(getKeyValue(str, "task_length")) * 60;
					if (betweenTime > taskTime) {
						//超时  停止任务 （应用程序至任务状态 做下一任务）
						String threadId = getKeyValue(str, "thread_id");
						Log.out.info("任务超时： taskId = " + taskId + " threadId = " + threadId);
						tools.writeFile(commandFileName, String.format("TASK:STOP %s,%s", taskId, threadId));
						str = setKeyValue(str, "task_status", String.valueOf(Constance.TaskStatus.OVERTIME));
					}
				}
				
			}
			//向服务器发送收到的信息
			try {
				parseMsgToServer(str);
			} catch (SigarException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void parseMsgToServer(String baseMsg) throws SigarException{
		//追加应用程序其他信息 和 服务器信息
		//process msg
		double proCpuRate = processManager.getProcessCpuUsed();
		int proManagerUsed = processManager.getProcessMemoryUsed();
		long proRuntime = processManager.getRunTime() / 1000;
		String ISO8601Time =tools.getISO8601BetweenTime(proRuntime) + " " + proRuntime;
		int threadNum = Integer.parseInt(getKeyValue(baseMsg, "thread_num"));
		int taskDoneNum = Integer.parseInt(getKeyValue(baseMsg, "task_done_num"));
		
		ProjectMsg projectMsg = new ProjectMsg();
		projectMsg.setProName(processName);
		projectMsg.setProCpuRate(proCpuRate);
		projectMsg.setProMemory(proManagerUsed);
		projectMsg.setProRunTime(ISO8601Time);
		projectMsg.setProThreadNum(threadNum);
		projectMsg.setProTaskDoneNum(taskDoneNum);
		if (processManager.isProcessRunning()) {
			projectMsg.setProStatus(Constance.ProjectStatus.RUNNING);
		}else{
			projectMsg.setProStatus(Constance.ProjectStatus.STOP);
		}
		
		//client msg
		ClientMsg clientMsg = new ClientMsg();
		clientMsg.setCliLogPath("\\" + "\\" + tools.getLocalIP() + "\\logs\\client.log");
		
		//thread_msg
		int threadId = Integer.parseInt(getKeyValue(baseMsg, "thread_id"));
		int taskId =  Integer.parseInt(getKeyValue(baseMsg, "task_id"));
		int taskStatus = Integer.parseInt(getKeyValue(baseMsg, "task_status"));
		String taskName = getKeyValue(baseMsg, "task_name");
		ThreadMsg threadMsg = new ThreadMsg();
		threadMsg.setThrTaskId(taskId);
		threadMsg.setThrThreadId(threadId);
		threadMsg.setThrTaskName(taskName);
		threadMsg.setThrTaskStatus(taskStatus);
		
		//servermsg
		ServerMsg serverMsg = serverManager.getServerInfo();
		serverMsg.setSerStatus(Constance.ServerStatus.RUNNING);
		
		//cpu
		List<CpuMsg> cpuMsgs = serverManager.getCpuMsg();
		
		//physical_memory_msg
		List<PhysicalMemoryMsg> physicalMemoryMsgs = serverManager.getPhysicMemory();
		
		//exception
		String excMsg = getKeyValue(baseMsg, "exception_msg");
		ExceptionMsg exceptionMsg = new ExceptionMsg();
		exceptionMsg.setExcMsg(excMsg);
		
		JSONObject json = new JSONObject();
		json.put("projectMsg", projectMsg);
		json.put("clientMsg", clientMsg);
		json.put("threadMsg", threadMsg);
		json.put("serverMsg", serverMsg);
		json.put("execptionMsg", exceptionMsg);
		json.put("cpuMsgs", cpuMsgs);
		json.put("physicalMemoryMsgs", physicalMemoryMsgs);
		
		String msg = json.toString();
		Log.out.debug("send - " +msg);
		System.out.println("-------------"+msg.getBytes().length);
		
		sendMsgToServer(msg);
	}
	
	private void sendMsgToServer(String msg){
		try {
			msg = "<msg=" + msg+"/>";
			os.write(msg.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.out.error(e);
			e.printStackTrace();
		}
	}
	
	public void ListenServer(){
		while(flag){
			byte[] buffer = new byte[1024];
			try {
				int length = is.read(buffer);
				String recMsg = new String(buffer, 0, length);
				Log.out.debug("rec - " + recMsg);
				dealServerCommand(recMsg);
			} catch (IOException e) {//一般是服务端断开了
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				//从新创建socket
				createSocket();
				try {
					Thread.sleep(TRY_AGAIN);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	
	public void dealServerCommand(String command){
		sendMsgToServer("DONE:" + command);
		
		if (command.startsWith("TASK") || command.startsWith("THR")) {
			tools.writeFile(commandFileName, command);
		
		}else if(command.equals("SERver:RESTART")){
			closeSocket();
			serverManager.restartServer();
		
		}else if(command.equals("PROcess:RESTART")){
			processManager.closeProcess();
			processManager.startProcess(processExeFile);
			
		}else if(command.equals("PROcess:START")){
			processManager.startProcess(processExeFile);
		}
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (Thread.currentThread().getName().equals(LISTEN_PROCESS_STATUS)) {
			readFile();
		}else if (Thread.currentThread().getName().equals(LISTEN_SERVER)){
			ListenServer();
		}
	}
}
