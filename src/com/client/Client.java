package com.client;


import com.manager.ProcessManager;
import com.manager.ServerManager;
import com.manager.platform.ProcessManagerLinux;
import com.manager.platform.ProcessManagerWin;
import com.manager.platform.ServerManagerLinux;
import com.manager.platform.ServerManagerWin;
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
		
		String processMain = tools.getProperty(process + ".main_class"); //�������Ϊ������
		if (tools.isWindows()) {
			processManager =  new ProcessManagerWin(processMain);
			serverManager = new ServerManagerWin();
		}else{
			processManager =  new ProcessManagerLinux(processMain);
			serverManager = new ServerManagerLinux();
		}
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
			
			//׷��Ӧ�ó���������Ϣ �� ��������Ϣ
			
			//������������յ�����Ϣ
			
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

	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
