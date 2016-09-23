package com.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @author Boris
 * @description 
 * 2016��9��7��
 */
public class Tools {
	public static Tools getTools(){
		return new Tools();
	}
	
    ///////////////////////////////////////////////////
	
	public String executeCommand(String command){
		String msg = "";
		try {
			Process processList = Runtime.getRuntime().exec(command);
			InputStream in = processList.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			
			String str = null;
			while((str = reader.readLine()) != null){
				msg += str + "\n";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return msg.trim();
	}
	
	///////////////////////////////////////////////////
	
	private static String OS = System.getProperty("os.name").toLowerCase();  
	
	public boolean isLinux(){  
        return OS.indexOf("linux")>=0;  
    }
	
	public boolean isMacOS() {
		return OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0&& OS.indexOf("x") < 0;
	}  
	
	public boolean isWindows(){  
	     return OS.indexOf("windows")>=0;  
    } 
	
	 /** 
     * @Method: getLocalIP 
     * @Description: ���ر�����ַ
     * @return
     * String
     */ 
    public String getLocalIP(){
		String ip = null;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return ip;
	}
	
    ///////////////////////////////////////////////////
	
    //��ȡproperties�ļ�
    static Properties pps = new Properties();
    static{
		try {
			String path = Thread.currentThread().getContextClassLoader().getResource("client_config.properties").getPath();
			pps.load(new FileInputStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	public String getProperty(String key) {
		return pps.getProperty(key).trim();
	}
	
    ///////////////////////////////////////////////////
	
	public void writeFile(final String fileName, final String content){
		try {
			//�ж�Ŀ¼�Ƿ���� �����ڴ���
    		File file = new File(fileName);
    		if (!file.getParentFile().exists()) {
    			file.getParentFile().mkdirs();
    		}
    		
    		FileWriter out = new FileWriter(fileName, false);
			out.write(content);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
 
    public String readFile(final String fileName){
    	String str = "";
    	try {
    		File file = new File(fileName);
    		if (file.exists()) {
    			FileInputStream is = new FileInputStream(file);
    			byte[] buffer = new byte[1024];
    			int byteRead;
    			while((byteRead = is.read(buffer)) != -1){
    				str += new String(buffer, 0, byteRead);
    			}

    			is.close();
    			file.delete();
			}
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return str;
    }
    
    ///////////////////////////////////////////////////
    
    public String getCurrentTime(){
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	return df.format(new Date());
    }
    
    /**
     * @Method: getBetweenCurrrentTime 
     * @Description: ȡ�͵�ǰ��ʱ����
     * @param oldTime ����
     * @return ����
     * long
     */
    public long getBetweenCurrrentTime(long oldTime){
    	long betweenTime = new Date().getTime() - oldTime;
		
		return betweenTime;
    	
    }
    
    /**
     * @Method: getBetweenCurrrentTime 
     * @Description: ȡ�͵�ǰ��ʱ����
     * @param oldTime yyyy-MM-dd HH:mm:ss��ʽ
     * @return ����
     * long
     */
    public long getBetweenCurrrentTime(String oldTime){
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date oldDate = null;
		try {
			oldDate = df.parse(oldTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date currentDate = new Date();
		long betweenTime = currentDate.getTime() - oldDate.getTime();
		
		return betweenTime;
    }
    
    /**
     * @Method: getBetweenTime 
     * @Description: ȡʱ����  �� �� �� ʱ �� ��
     * @param time ���������
     * @return P1Y3M3DT2H2M2S ��ʽ
     * String
     */
    public String getISO8601BetweenTime(long time){
    	long second = time % 60;
		long minute = ((time - second) / 60) % 60;
		long hour = ((time - minute * 60 - second) / (60 * 60)) % 24;
		long day = ((time - hour * 60 * 60 - minute * 60 - second) / (60 * 60 * 24));
		long year = day / 365;
		long month = (day - year * 365) / 30;
		day = day - year * 365 - month * 30;
		
//		System.out.println(String.format("%d �� %d ��  %d �� %d ʱ %d �� %d ��", year, month, day, hour, minute, second));
		//"P3Y6M4DT12H30M5S"
		String betweenTime = String.format("P%dY%dM%dDT%dH%dM%dS", year, month, day, hour, minute, second);
    	return betweenTime;
    }
    
    public static void main(String[] args) {
		long time = 1 * 365 * 24 * 60 * 60 + 3 * 30 * 24 * 60 * 60 + 3 * 24 * 60 * 60 + 2 * 60 * 60 + 2 * 60 + 2;//1y2m3d2h2m2s
		System.out.println(String.format("time%d", time));
		String btw = Tools.getTools().getISO8601BetweenTime(time);	
		
		System.out.println(btw);
		
	}

    ///////////////////////////////////////////////////
}
