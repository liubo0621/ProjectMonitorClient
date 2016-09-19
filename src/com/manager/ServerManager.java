package com.manager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Swap;

import com.manager.platform.ServerManagerWin;
import com.pojo.CpuMsg;
import com.pojo.PhysicalMemoryMsg;
import com.pojo.ServerMsg;
import com.utils.Tools;

/**
 * @author Boris
 * @description 取服务器信息
 * 2016年9月13日
 */
public abstract class ServerManager {
	private Sigar sigar = new Sigar();
	protected Tools tools = Tools.getTools();
	
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
   
    
	public List<PhysicalMemoryMsg> getPhysicMemory() {
		List<PhysicalMemoryMsg> phyMemorys = new ArrayList<PhysicalMemoryMsg>();
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
			
			PhysicalMemoryMsg phyMemory = new PhysicalMemoryMsg();
			
			phyMemory.setPhyName(fs.getDevName() + "\\");
			phyMemory.setPhySysTypeName(fs.getSysTypeName());
			phyMemory.setPhyTypeName(fs.getTypeName());
			
			FileSystemUsage usage = null;
			try {
				usage = sigar.getFileSystemUsage(fs.getDirName());
				
				phyMemory.setPhyMemory((int)usage.getTotal());
				phyMemory.setPhyMemoryUsed((int)usage.getUsed());
				phyMemory.setPhyMemoryAvail((int)usage.getAvail());
				phyMemory.setPhyMemoryFree((int)usage.getFree());
				phyMemory.setPhyUsePercent(usage.getUsePercent());
				
				phyMemorys.add(phyMemory);
				
			} catch (SigarException e) {
					e.printStackTrace();
				continue;
			}
		}
		return phyMemorys;
	}
	
	public ServerMsg getServerInfo() throws SigarException{
		ServerMsg server = new ServerMsg();
		
		server.setSerIp(getLocalIP());
		server.setSerSystem(getOSName());
		server.setSerMac(getMACAddr());
		
		//memory k
		Mem mem = sigar.getMem();
		server.setSerMemory((int)(mem.getTotal() / 1024L));
		server.setSerMemoryFree((int)(mem.getFree() / 1024L));
		server.setSerMemoryUsed((int)(mem.getUsed() / 1024L));
		
		//swap k
		Swap swap = sigar.getSwap();
		server.setSerSwap((int)(swap.getTotal() / 1024L));
		server.setSerSwapFree((int)(swap.getFree() / 1024L));
		server.setSerSwapUsed((int)(swap.getUsed() / 1024L));
		
		return server;
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
	
	
	public List<CpuMsg> getCpuMsg() throws SigarException{
		List<CpuMsg> cpus = new ArrayList<CpuMsg>();
		  
		// CPU的总量（单位：HZ）及CPU的相关信息  
		CpuInfo infos[] = sigar.getCpuInfoList();
		CpuPerc cpuList[] = sigar.getCpuPercList();
		for (int i = 0; i < infos.length; i++) {// 不管是单块CPU还是多CPU都适用  
		    CpuInfo info = infos[i];
		    CpuPerc cpuPerc= cpuList[i];
//		    System.out.println("mhz=" + info.getMhz());// CPU的总量MHz  
//		    System.out.println("vendor=" + info.getVendor());// 获得CPU的卖主，如：Intel  
//		    System.out.println("model=" + info.getModel());// 获得CPU的类别，如：Celeron  
//		    System.out.println("cache size=" + info.getCacheSize());// 缓冲存储器数量  
			
		    CpuMsg cpu = new CpuMsg();
		    
		    cpu.setCpuMhz((int)info.getMhz());
		    cpu.setCpuVendor(info.getVendor());
		    cpu.setCpuModel(info.getModel());
		    cpu.setCpuChacheSize((int)info.getCacheSize());
		    
		    cpu.setCpuSystemUsed(cpuPerc.getSys());
		    cpu.setCpuUserUsed(cpuPerc.getUser());
		    cpu.setCpuIdle(cpuPerc.getIdle());
		    cpu.setCpuWait(cpuPerc.getWait());
		    
		    cpus.add(cpu);
		}  
		    
//		for (int i = 0; i < cpuList.length; i++) {  
//			System.out.println(cpuList[i]);
//		}
		
		return cpus;
	}
	
	public abstract void restartServer();
	
	public static void main(String[] args) throws SigarException {
		ServerManager s = new ServerManagerWin();
		s.getMemory();
	}

}
