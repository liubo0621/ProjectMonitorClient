import java.io.FileNotFoundException;
import java.lang.management.ManagementFactory;
import java.util.Date;

import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.ProcState;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.cmd.SigarCommandBase;


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
		String name = ManagementFactory.getRuntimeMXBean().getName();    
		System.out.println(name);    
		// get pid    
		String pid_ = name.split("@")[0];    
		System.out.println("Pid is:" + pid_);
		
		Sigar sigar = new Sigar();
		try {
			long pid =   Long.parseLong(pid_);
			ProcCpu procCpu = sigar.getProcCpu(pid);
			procCpu.gather(sigar, pid); 
			Thread.sleep(1000);
			System.out.println("getStartTime:" + procCpu.getStartTime());
			System.out.println(procCpu.getTotal()); //占用cpu的总时间
			double percent = procCpu.getPercent();
			System.out.println(percent);
			
			System.out.println(new Date().getTime());
			
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		Test test = new Test();
//		String str[] = {"SocketService1.exe"};
//		test.output(str);
	
	}
}
