package com.utils;

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

import com.pojo.CPU;
import com.pojo.PhysicalMemory;
import com.pojo.Server;

/**
 * @author Boris
 * @description 
 * 2016��9��7��
 */
public class Tools {
	Sigar sigar = new Sigar();
	
	public static Tools getTools(){
		return new Tools();
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
			
			phyMemory.setPhyName(fs.getDevName());
			phyMemory.setSysTypeName(fs.getSysTypeName());
			phyMemory.setTypeName(fs.getTypeName());
			
			FileSystemUsage usage = null;
			try {
				usage = sigar.getFileSystemUsage(fs.getDirName());
			} catch (SigarException e) {
					e.printStackTrace();
				continue;
			}
			switch (fs.getType()) {
			case 0: // TYPE_UNKNOWN ��δ֪
				break;
			case 1: // TYPE_NONE
				break;
			case 2: // TYPE_LOCAL_DISK : ����Ӳ��
//				// �ļ�ϵͳ�ܴ�С
//				System.out.println(" Total = " + usage.getTotal() + "KB");
//				// �ļ�ϵͳʣ���С
//				System.out.println(" Free = " + usage.getFree() + "KB");
//				// �ļ�ϵͳ���ô�С
//				System.out.println(" Avail = " + usage.getAvail() + "KB");
//				// �ļ�ϵͳ�Ѿ�ʹ����
//				System.out.println(" Used = " + usage.getUsed() + "KB");
//				double usePercent = usage.getUsePercent() * 100D;
//				// �ļ�ϵͳ��Դ��������
//				System.out.println(" Usage = " + usePercent + "%");
//				
				phyMemory.setMemory(usage.getTotal());
				phyMemory.setMemoryUsed(usage.getUsed());
				phyMemory.setMemoryAvail(usage.getAvail());
				phyMemory.setMemoryFree(usage.getFree());
				phyMemory.setUsePercent(usage.getUsePercent());
				break;
			case 3:// TYPE_NETWORK ������
				break;
			case 4:// TYPE_RAM_DISK ������
				break;
			case 5:// TYPE_CDROM ������
				break;
			case 6:// TYPE_SWAP ��ҳ�潻��
				break;
			}
			phyMemorys.add(phyMemory);
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
		server.setMemory(mem.getTotal() / 1000L);
		server.setMemoryFree(mem.getFree() / 1000L);
		server.setMemoryUsed(mem.getUsed() / 1000L);
		
		//swap k
		Swap swap = sigar.getSwap();
		server.setSwap(swap.getTotal() / 1000L);
		server.setSwapFree(swap.getFree() / 1000L);
		server.setSwapUsed(swap.getUsed() / 1000L);
	}
	
//	public void getMemory() throws SigarException{
//		// �����ڴ���Ϣ  
//		Mem mem = sigar.getMem();  
//		// �ڴ�����  
//		System.out.println("Total = " + mem.getTotal() / 1024L / 1024 + "M av");  
//		// ��ǰ�ڴ�ʹ����  
//		System.out.println("Used = " + mem.getUsed() / 1024L / 1024 + "M used");  
//		// ��ǰ�ڴ�ʣ����  
//		System.out.println("Free = " + mem.getFree() / 1024L / 1024 + "M free");  
//		  
//		// ϵͳҳ���ļ���������Ϣ  
//		Swap swap = sigar.getSwap();  
//		// ����������  
//		System.out.println("Total = " + swap.getTotal() / 1024L + "K av");  
//		// ��ǰ������ʹ����  
//		System.out.println("Used = " + swap.getUsed() / 1024L + "K used");  
//		// ��ǰ������ʣ����  
//		System.out.println("Free = " + swap.getFree() / 1024L + "K free");  
//	}
	
	
	public List<CPU> getCpu() throws SigarException{
		List<CPU> cpus = new ArrayList<CPU>();
		CPU cpu = new CPU();
		  
		// CPU����������λ��HZ����CPU�������Ϣ  
		CpuInfo infos[] = sigar.getCpuInfoList();
		CpuPerc cpuList[] = sigar.getCpuPercList();
		for (int i = 0; i < infos.length; i++) {// �����ǵ���CPU���Ƕ�CPU������  
		    CpuInfo info = infos[i];
		    CpuPerc cpuPerc= cpuList[i];
//		    System.out.println("mhz=" + info.getMhz());// CPU������MHz  
//		    System.out.println("vendor=" + info.getVendor());// ���CPU���������磺Intel  
//		    System.out.println("model=" + info.getModel());// ���CPU������磺Celeron  
//		    System.out.println("cache size=" + info.getCacheSize());// ����洢������  
		    
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
	
    
	public static void main(String[] args) throws SigarException, SocketException, UnknownHostException {
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
		tools.getCpu();
		
//		mac: C8-5B-76-03-A6-C4
	}
}
