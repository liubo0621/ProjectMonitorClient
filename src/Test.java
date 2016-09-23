import java.io.FileNotFoundException;

import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.ProcState;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.cmd.SigarCommandBase;

import com.utils.Tools;


/**
 * @author Boris
 * @description 
 * 2016年9月12日
 */
public class Test extends SigarCommandBase {


	@Override
	public void output(String[] arg0) throws SigarException {
		// TODO Auto-generated method stub
		long pid =  9676;
		ProcState prs = sigar.getProcState(pid); 
		ProcCpu pCpu = sigar.getProcCpu(pid);
		pCpu.gather(sigar, pid); 
		System.out.println(prs.getName()); 
		System.out.println(pCpu.getPercent());
		System.out.println(pCpu.getTotal());
	}
	
	public static void main(String[] args) throws FileNotFoundException, SigarException, InterruptedException {
//		String name = ManagementFactory.getRuntimeMXBean().getName();    
//		System.out.println(name);    
//		// get pid    
//		String pid_ = name.split("@")[0];    
//		System.out.println("Pid is:" + pid_);
//		
//		Sigar sigar = new Sigar();
//		try {
//			long pid =   Long.parseLong(pid_);
//			ProcCpu procCpu = sigar.getProcCpu(pid);
//			procCpu.gather(sigar, pid); 
//			Thread.sleep(1000);
//			System.out.println("getStartTime:" + procCpu.getStartTime());
//			System.out.println(procCpu.getTotal()); //占用cpu的总时间
//			double percent = procCpu.getPercent();
//			System.out.println(percent);
//			
//			System.out.println(new Date().getTime());
//			
//		} catch (SigarException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		Test test = new Test();
//		String str[] = {"SocketService1.exe"};
//		test.output(str);
		
		Sigar sigar = new Sigar();
		int pid = 12256;
		ProcCpu curPc = sigar.getProcCpu(pid);

		long startTime = curPc.getStartTime();
		long runTime = Tools.getTools().getBetweenCurrrentTime(startTime);
		long cpuTime = curPc.getTotal() + curPc.getUser();
		System.out.println(curPc.getTotal() + " "  + curPc.getUser());
		System.out.println(cpuTime + "  " + runTime);
		
		double load = 100.0 * cpuTime / runTime / 4;
		System.out.println(load);
		
		//pcpu = 100 * (processCpuTime2 – processCpuTime1)/(totalCpuTime2 - totalCpuTime1);
	
	}
}
