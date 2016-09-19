import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.pojo.ServerMsg;
import com.utils.Tools;


/**
 * @author Boris
 * @description 
 * 2016Äê9ÔÂ12ÈÕ
 */
public class Test {
	public static void main(String[] args) throws FileNotFoundException {
//		Tools tools = Tools.getTools();
//		String statusFile = tools.getProperty("process1.status_file");
//		
//		tools.writeFile(statusFile, "<write_file_time=2016-09-08 12:32:64,process_name=xxx,crash=false,thread_id=1,thread_num=10,task_id=6,task_name=xxx,task_length=12,task_status=doing,task_done_num=20/>");
	
//		  JSONStringer js = new JSONStringer();  
//	        JSONObject obj2 = new JSONObject();  
//	        JSONObject obj3 = new JSONObject();  
//	        JSONObject obj4 = new JSONObject();  
//	        obj4.put("title", "book1");
//	        obj4.put("price", "$11");  
//	        obj3.put("book", obj4);  
//	        obj3.put("author", new JSONObject().put("name", "author-1"));  
//	          
//	        JSONObject obj5 = new JSONObject();  
//	        JSONObject obj6 = new JSONObject();  
//	        obj6.put("title", "book2");
//	        obj6.put("price", "$22");  
//	        obj5.put("book", obj6);  
//	        obj5.put("author", new JSONObject().put("name", "author-2"));  
//	          
//	        JSONArray obj7 = new JSONArray();
//	        obj7.add(obj3);
//	        obj7.add(obj5);  
//	          
//	          
//	          
//	        obj2.put("title","BOOK");  
//	        obj2.put("signing", obj7);  
//	          
//	        js.object().key("session").value(obj2).endObject();  
//	          
//	        System.out.println(js.toString());  
//	        
//	        PrintWriter out = new PrintWriter(new FileOutputStream("D://1.txt"));  
//	        out.println(js.toString());
		
		JSONObject json = new JSONObject();
		json.put("name", "server1");
		
		JSONArray cupArray = new JSONArray();
		
		JSONObject jsonCPU1 = new JSONObject();
		jsonCPU1.put("name2", "server2");
		jsonCPU1.put("name3", "server2");
		
		JSONObject jsonCPU2 = new JSONObject();
		jsonCPU2.put("name2", "server3");
		jsonCPU2.put("name3", "server3");
		
		cupArray.add(jsonCPU1);
		cupArray.add(jsonCPU2);
		
		ServerMsg server = new ServerMsg();
		server.setSerMac("1212121212121212");
		json.put("server", server);
		
		
		json.put("cups", cupArray);
		
		String str = json.toString();
		System.out.println(str);
		
//		JSONTokener jso3 = new JSONTokener(str);
//		JSONObject json2 = (JSONObject) jso3.nextValue();
		JSONObject json2 = JSONObject.fromObject(str);
		String name = json2.getString("name");
		System.out.println(name);
		JSONArray cArray = json2.getJSONArray("cups");
		for (int i = 0; i < cArray.size(); i++) {
			JSONObject ob = cArray.getJSONObject(i);
			System.out.println(ob.getString("name3"));
		}
		
		
		JSONObject s = json2.getJSONObject("server");
		System.out.println(s.getString("serMac"));
		
		ServerMsg ser = (ServerMsg) JSONObject.toBean(s, ServerMsg.class);
		System.out.println(ser.getSerMac());
		
		//---------------
		
	
	}
}
