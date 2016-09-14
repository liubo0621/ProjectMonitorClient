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
import com.pojo.CPU;
import com.pojo.PhysicalMemory;
import com.pojo.Server;
import com.utils.Tools;

/**
 * @author Boris
 * @description ȡ��������Ϣ
 * 2016��9��13��
 */
public abstract class ServerManager {
	private Sigar sigar = new Sigar();
	protected Tools tools = Tools.getTools();
	
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
    
	public String getOSName() {     
        return System.getProperty("os.name").toLowerCase();     
    }  
	
	
	public String getMACAddr() {
		String mac = "";
		
		try {
			NetworkInterface netInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
			// ���Mac��ַ��byte����
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
		// ��������int��������ת��Ϊʮ��������
		String str = Integer.toHexString((int) (integer & 0xff));
		// ����������ַ���ǰ��0ռλ��������
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
		// �����ڴ���Ϣ  
		Mem mem = sigar.getMem();  
		// �ڴ�����  
		System.out.println("Total = " + mem.getTotal() / 1024L / 1024 + "M av");  
		// ��ǰ�ڴ�ʹ����  
		System.out.println("Used = " + mem.getUsed() / 1024L / 1024 + "M used");  
		// ��ǰ�ڴ�ʣ����  
		System.out.println("Free = " + mem.getFree() / 1024L / 1024 + "M free");  
		  
		// ϵͳҳ���ļ���������Ϣ  
		Swap swap = sigar.getSwap();  
		// ����������  
		System.out.println("Total = " + swap.getTotal() / 1024L + "K av");  
		// ��ǰ������ʹ����  
		System.out.println("Used = " + swap.getUsed() / 1024L + "K used");  
		// ��ǰ������ʣ����  
		System.out.println("Free = " + swap.getFree() / 1024L + "K free");  
	}
	
	
	public List<CPU> getCpu() throws SigarException{
		List<CPU> cpus = new ArrayList<CPU>();
		CPU cpu = new CPU();
		  
		// CPU����������λ��HZ����CPU�������Ϣ  
		CpuInfo infos[] = sigar.getCpuInfoList();
		CpuPerc cpuList[] = sigar.getCpuPercList();
		for (int i = 0; i < infos.length; i++) {// �����ǵ���CPU���Ƕ�CPU������  
		    CpuInfo info = infos[i];
		    CpuPerc cpuPerc= cpuList[i];
		    System.out.println("mhz=" + info.getMhz());// CPU������MHz  
		    System.out.println("vendor=" + info.getVendor());// ���CPU���������磺Intel  
		    System.out.println("model=" + info.getModel());// ���CPU������磺Celeron  
		    System.out.println("cache size=" + info.getCacheSize());// ����洢������  
		    
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
	
	public abstract void restartServer();
	
	public static void main(String[] args) throws SigarException {
		ServerManager s = new ServerManagerWin();
		s.getMemory();
	}

}
