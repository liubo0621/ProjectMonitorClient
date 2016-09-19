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
import com.pojo.PhysicalMemoryMsg;
import com.pojo.ProjectMsg;
import com.pojo.ServerMsg;
import com.pojo.ThreadMsg;
import com.utils.Log;
import com.utils.Tools;

/**
 * @author Boris
 * @description 
 * 2016��9��9��
 */
public class Client implements Runnable{
	private Tools tools;
	private ProcessManager processManager;
	private ServerManager serverManager;
	private Socket socket = null;
	private OutputStream os = null;
	private InputStream is = null;
	
	private int readFilePerSec;

	private String processStartTime = null; //Ӧ�ó���ʼʱ��  Ϊ��һ�� �����ļ�ʱ��
	private String statusFileName; //Ӧ�ó���д�����ļ�
	private String processName;
	private String commandFileName;
	private String process; // Ӧ�ó��� process1 process2 ...
	private String serverIp;
	private int serverPort;
	
	public static final int TRY_AGAIN = 5000;
	
	public static final String LISTEN_SERVER = "0";
	public static final String LISTEN_PROCESS_STATUS = "1";	
	
	public Client(int processSequence){
		tools = Tools.getTools();
		
		//init
		process = "process" + processSequence;
		processName = tools.getProperty(process + ".name");
		Log.out.debug("����Ӧ�ó���< " + processName + " >");
		
		readFilePerSec = Integer.parseInt(tools.getProperty("client.read_file_time")) * 1000;
		statusFileName = tools.getProperty(process + ".status_file");
		commandFileName = tools.getProperty(String.format("client%d.command_file", processSequence));
		
		//socket
		serverIp = tools.getProperty("service.ip");
		serverPort = Integer.parseInt(tools.getProperty("service.port"));
		createSocket();
		
		//manager
		String processMain = tools.getProperty(process + ".main_class"); //�������Ϊ������
		if (tools.isWindows()) {
			processManager =  new ProcessManagerWin(processMain);
			serverManager = new ServerManagerWin();
		}else{
			processManager =  new ProcessManagerLinux(processMain);
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
	
	public String readFile(){
		while(true){
			//���socket û������ �򲻶�ȡ�ļ�
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
					if (processStartTime ==  null) {
						processStartTime = tools.getCurrentTime();
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
			String str = msgs[i]+ "/>";
			Log.out.debug("read - " + str );
			
			//������������յ�����Ϣ
			try {
				sendMsgToServer(str);
			} catch (SigarException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//����
			if (getKeyValue(str, "crash").equals("true")) {
				//1. �ر�Ӧ�ó���
				processManager.closeProcess();
				
				//2. ��Ӧ�ó����͵���crash��taskId
				String taskId = getKeyValue(str, "task_id");
				tools.writeFile(commandFileName, "TASK:CRASH " + taskId);
				Log.out.info("������ taskId = " + taskId);
				
				//3. ����Ӧ�ó���
				String processExeFile = tools.getProperty(process + ".execute_file");
				tools.startProgram(processExeFile);
			}
			//�ж��Ƿ�ʱ ��ʱֹͣ����
			else{
				//�жϵ�ǰ�����Ƿ�ִ���� ��ִ���� ��û��ʱ
				boolean isDone = false;
				String taskId = getKeyValue(str, "task_id");
				for (int j = i; j < msgs.length; j++) {
					String strTemp = msgs[j];
					//��ǰ�����Ƿ��յ�done״̬
					if (strTemp.contains("task_id="+taskId) && getKeyValue(strTemp, "task_status").equals("done")) {
						isDone = true;
						break;
					}
				}
				
				if (!isDone) {
					//��ǰʱ����д�ļ���ʱ���
					String writeFileTime = getKeyValue(str, "write_file_time");
					long betweenTime = tools.getBetweenCurrrentTime(writeFileTime);
					
					int taskTime = Integer.parseInt(getKeyValue(str, "task_length")) * 60;
					if (betweenTime > taskTime) {
						//��ʱ  ֹͣ���� ��Ӧ�ó���������״̬ ����һ����
						String threadId = getKeyValue(str, "thread_id");
						Log.out.info("��ʱ�� taskId = " + taskId + " threadId = " + threadId);
						tools.writeFile(commandFileName, String.format("TASK:STOP %s,%s", taskId, threadId));
					}
				}
				
			}
		}
	}
	
	private void sendMsgToServer(String baseMsg) throws SigarException{
		//׷��Ӧ�ó���������Ϣ �� ��������Ϣ
		//process msg
		double proCpuRate = processManager.getProcessCpuUsed();
		int proManagerUsed = processManager.getProcessMemoryUsed();
		long betCurrentTime = tools.getBetweenCurrrentTime(processStartTime);
		String proRuntime =tools.getBetweenTime(betCurrentTime) + " " + betCurrentTime;
		int threadNum = Integer.parseInt(getKeyValue(baseMsg, "thread_num"));
		int taskDoneNum = Integer.parseInt(getKeyValue(baseMsg, "task_done_num"));
		
		ProjectMsg projectMsg = new ProjectMsg();
		projectMsg.setProName(processName);
		projectMsg.setProCpuRate(proCpuRate);
		projectMsg.setProMemory(proManagerUsed);
		projectMsg.setProRunTime(proRuntime);
		projectMsg.setProThreadNum(threadNum);
		projectMsg.setProTaskDoneNum(taskDoneNum);
		
		//client msg
		ClientMsg clientMsg = new ClientMsg();
		clientMsg.setCliLogPath("\\" + "\\" + tools.getLocalIP() + "\\logs\\client.log");
		
		//thread_msg
		int threadId = Integer.parseInt(getKeyValue(baseMsg, "thread_id"));
		int taskId =  Integer.parseInt(getKeyValue(baseMsg, "task_id"));
		String taskName = getKeyValue(baseMsg, "task_name");
		ThreadMsg threadMsg = new ThreadMsg();
		threadMsg.setThrTaskId(taskId);
		threadMsg.setThrThreadId(threadId);
		threadMsg.setThrTaskName(taskName);
		
		//servermsg
		ServerMsg serverMsg = serverManager.getServerInfo();
		
		//cpu
		List<CpuMsg> cpuMsgs = serverManager.getCpuMsg();
		
		//physical_memory_msg
		List<PhysicalMemoryMsg> physicalMemoryMsgs = serverManager.getPhysicMemory();
		
		
		JSONObject json = new JSONObject();
		json.put("projectMsg", projectMsg);
		json.put("clientMsg", clientMsg);
		json.put("threadMsg", threadMsg);
		json.put("serverMsg", serverMsg);
		json.put("cpuMsgs", cpuMsgs);
		json.put("physicalMemoryMsgs", physicalMemoryMsgs);
		
		String msg = json.toString();
		Log.out.debug("send - " +msg);
		System.out.println("-------------"+msg.getBytes().length);
		
		try {
			os.write(msg.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.out.error(e);
			e.printStackTrace();
		}
	}
	
	public void ListenServer(){
		Log.out.debug("��������");
		boolean flag = true;
		while(flag){
			byte[] buffer = new byte[1024];
			try {
				int length = is.read(buffer);
				String recMsg = new String(buffer, 0, length);
				Log.out.debug("rec - " + recMsg);
			} catch (IOException e) {//һ���Ƿ���˶Ͽ���
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				//���´���socket
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
