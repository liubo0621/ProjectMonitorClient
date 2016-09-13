package com.run;

import com.client.Client;
import com.client.WinClient;
import com.utils.Tools;

/**
 * @author Boris
 * @description 
 * 2016Äê9ÔÂ12ÈÕ
 */
public class RunClient {
	
	public static void main(String[] args) {
		Tools tools = Tools.getTools();

		Client client = null;
		if (tools.isWindows()) {
			client = new WinClient(1);
			client.readFile();
		}
	}

}
