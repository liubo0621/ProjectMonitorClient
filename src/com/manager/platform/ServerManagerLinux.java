package com.manager.platform;

import com.manager.ServerManager;

/**
 * @author Boris
 * @description 
 * 2016��9��13��
 */
public class ServerManagerLinux extends ServerManager{

	@Override
	public void restartServer() {
		// TODO Auto-generated method stub
		tools.executeCommand("sudo shutdown -r now");
	}

}
