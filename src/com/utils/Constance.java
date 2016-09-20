package com.utils;

/**
 * @author Boris
 * @description 
 * 2016Äê9ÔÂ9ÈÕ
 */
public class Constance {
	public static final String STOP_TASK = "TASK:STOP taskId,threadId";
	public static final String MAX_THREAD = "THRead:MAX:NUM threadNum"; 
	public static final String RESTART_SERVER = "SERver:RESTART";
	public static final String RESTART_PROCESS = "PROcess:RESTART";
	public static final String START_PROCESS = "PROcess:START";
	
	public static final class Task_Status{
		public static final int DOING  = 0x0000001;
		public static final int DONE  = 0x0000002;
		public static final int EXCEPTION  = 0x0000003;
		public static final int OVERTIME  = 0x0000004;
		
	}

}
