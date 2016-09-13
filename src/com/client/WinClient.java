package com.client;

/**
 * @author Boris
 * @description 
 * 2016Äê9ÔÂ12ÈÕ
 */
public class WinClient extends Client {

	/**
	 * @param processSequence
	 */
	public WinClient(int processSequence) {
		super(processSequence);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void closeProcess() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getProcessMemory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProcessCpu() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void restartServer() {
		// TODO Auto-generated method stub
		String command =  "shutdown -r -f -t 0";
		tools.executeCommand(command);
	}

}
