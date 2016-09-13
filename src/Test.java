import com.utils.Tools;


/**
 * @author Boris
 * @description 
 * 2016Äê9ÔÂ12ÈÕ
 */
public class Test {
	public static void main(String[] args) {
		Tools tools = Tools.getTools();
		String statusFile = tools.getProperty("process1.status_file");
		
		tools.writeFile(statusFile, "<write_file_time=2016-09-08 12:32:64,process_name=xxx,crash=false,thread_id=1,thread_num=10,task_id=6,task_name=xxx,task_length=12,task_status=doing,task_done_num=20/>");
	}
}
