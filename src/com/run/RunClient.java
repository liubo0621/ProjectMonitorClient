package com.run;

import com.client.Client;
import com.utils.Tools;

/**
 * @author Boris
 * @description 
 * 2016Äê9ÔÂ12ÈÕ
 */
public class RunClient {
	
	public static void main(String[] args) {
		Tools tools = Tools.getTools();

		Client client = new Client(1);
		new Thread(client, client.LISTEN_PROCESS_STATUS).start();
		new Thread(client, client.LISTEN_SERVER).start();
		
	}

}
