package com.manager.platform;

import com.manager.ServerManager;

/**
 * @author Boris
 * @description 
 * 2016Äê9ÔÂ13ÈÕ
 */
public class ServerManagerWin extends ServerManager{

	@Override
	public void restartServer() {
		// TODO Auto-generated method stub
		tools.executeCommand("sudo shutdown -r -f -t 0");
	}

}
