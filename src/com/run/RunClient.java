package com.run;

import com.client.Client;
import com.utils.Tools;

/**
 * @author Boris
 * @description 
 * 2016��9��12��
 */
public class RunClient {
	
	public static void main(String[] args) {
		Tools tools = Tools.getTools();

		Client client = new Client(1);
		client.readFile();
	}

}
