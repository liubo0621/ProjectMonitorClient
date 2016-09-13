package com.client;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.utils.Log;
import com.utils.Tools;

/**
 * @author Boris
 * @description 
 * 2016��9��9��
 */
public abstract class Client implements Runnable{
	protected Tools tools;
	
	private int readFilePerSec;
	private long processStartTime = 0; //Ӧ�ó���ʼʱ��  Ϊ��һ�� �����ļ�ʱ��
	
	private String statusFileName; //Ӧ�ó���д�����ļ�
	private String processName;
	private String commandFileName;
	private String process; // Ӧ�ó��� process1 process2 ...
	
	public Client(int processSequence){
		tools = Tools.getTools();
		
		process = "process" + processSequence;
		processName = tools.getProperty(process + ".name");
		Log.out.debug("����Ӧ�ó���< " + processName + " >");
		
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
				
				String[] infos = str.split("\\s\\s*");  // \s*��ʾ0�������ϵĿո�   ƥ��һ�����ϵĿո�
				
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
			
			//������������յ�����Ϣ
			
			//����
			if (getKeyValue(str, "crash").equals("true")) {
				//1. �ر�Ӧ�ó���
				closeProcess();
				
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
	
	public abstract void closeProcess();
	public abstract void restartServer();
	public abstract String getProcessMemory();
	public abstract String getProcessCpu();
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
