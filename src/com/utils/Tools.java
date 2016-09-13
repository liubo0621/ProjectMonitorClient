package com.utils;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Swap;

import com.pojo.CPU;
import com.pojo.PhysicalMemory;
import com.pojo.Server;

/**
 * @author Boris
 * @description 
 * 2016年9月7日
 */
public class Tools {
	Sigar sigar = new Sigar();
	
	static{
		System.loadLibrary("sigar-x86-winnt");
	}
	
	public static Tools getTools(){
		return new Tools();
	}
	
	 /** 
     * @Method: getLocalIP 
     * @Description: 返回本机地址
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
    
	public String getOSName() {     
        return System.getProperty("os.name").toLowerCase();     
    }  
	
	
	public String getMACAddr() {
		String mac = "";
		
		try {
			NetworkInterface netInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
			// 获得Mac地址的byte数组
			byte[] macAddr = netInterface.getHardwareAddress();

			mac = toHexString(macAddr[0]);
			for (int i = 1; i < macAddr.length; i++) {
				mac += "-" + toHexString(macAddr[i]);
			}
			
		} catch (SocketException | UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mac;
	}
	
	public String toHexString(int integer) {
		// 将得来的int类型数字转化为十六进制数
		String str = Integer.toHexString((int) (integer & 0xff));
		// 如果遇到单字符，前置0占位补满两格
		if (str.length() == 1) {
			str = "0" + str;
		}
		return str.toUpperCase();
	}
   
    
	public List<PhysicalMemory> getPhysicMemory() {
		List<PhysicalMemory> phyMemorys = new ArrayList<PhysicalMemory>();
		PhysicalMemory phyMemory = new PhysicalMemory();
		FileSystem[] fslist = null;
		
		try {
			fslist = sigar.getFileSystemList();
		} catch (SigarException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for (int i = 0; i < fslist.length; i++) {
			FileSystem fs = fslist[i];
			
//			System.out.println(fs.getDevName());
//			System.out.println(fs.getTypeName());
			
			phyMemory.setPhyName(fs.getDevName());
			phyMemory.setSysTypeName(fs.getSysTypeName());
			phyMemory.setTypeName(fs.getTypeName());
			
			FileSystemUsage usage = null;
			try {
				usage = sigar.getFileSystemUsage(fs.getDirName());
				
				phyMemory.setMemory(usage.getTotal());
				phyMemory.setMemoryUsed(usage.getUsed());
				phyMemory.setMemoryAvail(usage.getAvail());
				phyMemory.setMemoryFree(usage.getFree());
				phyMemory.setUsePercent(usage.getUsePercent());
				
				phyMemorys.add(phyMemory);
				
			} catch (SigarException e) {
					e.printStackTrace();
				continue;
			}
		}
		return phyMemorys;
	}
	
	public void getServerInfo() throws SigarException{
		Server server = new Server();
		
		server.setServerIp(getLocalIP());
		server.setSystem(getOSName());
		server.setMacAddr(getMACAddr());
		
		//memory k
		Mem mem = sigar.getMem();
		server.setMemory(mem.getTotal() / 1024L);
		server.setMemoryFree(mem.getFree() / 1024L);
		server.setMemoryUsed(mem.getUsed() / 1024L);
		
		//swap k
		Swap swap = sigar.getSwap();
		server.setSwap(swap.getTotal() / 1024L);
		server.setSwapFree(swap.getFree() / 1024L);
		server.setSwapUsed(swap.getUsed() / 1024L);
	}
	
	public void getMemory() throws SigarException{
		// 物理内存信息  
		Mem mem = sigar.getMem();  
		// 内存总量  
		System.out.println("Total = " + mem.getTotal() / 1024L / 1024 + "M av");  
		// 当前内存使用量  
		System.out.println("Used = " + mem.getUsed() / 1024L / 1024 + "M used");  
		// 当前内存剩余量  
		System.out.println("Free = " + mem.getFree() / 1024L / 1024 + "M free");  
		  
		// 系统页面文件交换区信息  
		Swap swap = sigar.getSwap();  
		// 交换区总量  
		System.out.println("Total = " + swap.getTotal() / 1024L + "K av");  
		// 当前交换区使用量  
		System.out.println("Used = " + swap.getUsed() / 1024L + "K used");  
		// 当前交换区剩余量  
		System.out.println("Free = " + swap.getFree() / 1024L + "K free");  
	}
	
	
	public List<CPU> getCpu() throws SigarException{
		List<CPU> cpus = new ArrayList<CPU>();
		CPU cpu = new CPU();
		  
		// CPU的总量（单位：HZ）及CPU的相关信息  
		CpuInfo infos[] = sigar.getCpuInfoList();
		CpuPerc cpuList[] = sigar.getCpuPercList();
		for (int i = 0; i < infos.length; i++) {// 不管是单块CPU还是多CPU都适用  
		    CpuInfo info = infos[i];
		    CpuPerc cpuPerc= cpuList[i];
		    System.out.println("mhz=" + info.getMhz());// CPU的总量MHz  
		    System.out.println("vendor=" + info.getVendor());// 获得CPU的卖主，如：Intel  
		    System.out.println("model=" + info.getModel());// 获得CPU的类别，如：Celeron  
		    System.out.println("cache size=" + info.getCacheSize());// 缓冲存储器数量  
		    
		    cpu.setMsz(info.getMhz());
		    cpu.setVendor(info.getVendor());
		    cpu.setModel(info.getModel());
		    cpu.setChacheSize(info.getCacheSize());
		    
		    cpu.setSystemUse(cpuPerc.getSys());
		    cpu.setUserUse(cpuPerc.getUser());
		    cpu.setIdel(cpuPerc.getIdle());
		    cpu.setWait(cpuPerc.getWait());
		    
		    cpus.add(cpu);
		}  
		    
//		for (int i = 0; i < cpuList.length; i++) {  
//			System.out.println(cpuList[i]);
//		}
		
		return cpus;
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
	
	//使用Desktop启动应用程序    
	public void startProgram(String programPath){
		Log.out.info("启动应用程序：" + programPath);
		try {
			Desktop.getDesktop().open(new File(programPath));
		} catch (Exception e) {
			e.printStackTrace();
			Log.out.error("应用程序：" + programPath + "不存在！");
		}
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
	
    ///////////////////////////////////////////////////
	
    //读取properties文件
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
			//判断目录是否存在 不存在创建
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
//    			file.delete();
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
    
    public long getCurrentSecond(){
    	return new Date().getTime() / 1000;
    }
    
    public long getBetweenCurrrentTime(String oldTime){
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date writeDate = null;
		try {
			writeDate = df.parse(oldTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date currentDate = new Date();
		long betweenTime = currentDate.getTime() - writeDate.getTime();
		
		return betweenTime;
    }

    ///////////////////////////////////////////////////
	
	public static void main(String[] args) throws SigarException, IOException {
		Tools tools = Tools.getTools();
		
//		String ip = tools.getLocalIP();
//		String osName = tools.getOSName();
//		System.out.println(osName);
//		tools.getMACAddr();
//		tools.internateInfo();
//		tools.getPhysicMemory();
//		tools.getServerInfo();
//		tools.getSys();
//		tools.getMemory();
//		tools.getCpu();
//		tools.startProgram("C:\\Users\\Boris\\Desktop\\Test.jar");
//		tools.startProgram("D:\\WorkSpace\\VC\\SocketService\\Debug\\SocketService1.exe");
		
//		tools.restartServer();
		
//		long totalMem = Runtime.getRuntime().totalMemory();
//		System.out.println(totalMem / 1024 + "k");
		
//		mac: C8-5B-76-03-A6-C4
	}
}
