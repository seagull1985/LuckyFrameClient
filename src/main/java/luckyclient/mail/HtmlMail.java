package luckyclient.mail;

import java.util.HashMap;
import java.util.Map;

public class HtmlMail {
	static FreemarkerEmailTemplate  fet=new FreemarkerEmailTemplate();
	
	public static String HtmlContentFormat(int[] taskcount,String taskid,String buildstatus,String restartstatus,String time,String jobname){		
		Map<Object, Object> parameters = new HashMap<Object, Object>();
		parameters.put("buildstatus", buildstatus);
		parameters.put("restartstatus", restartstatus);
		parameters.put("taskcount", taskcount);
		parameters.put("time", time);
		parameters.put("taskid", taskid);
		parameters.put("jobname", jobname);
		return fet.getText("task-body", parameters);
	}
	
	public static String HtmlSubjectFormat(String jobname){
		Map<Object, Object> parameters = new HashMap<Object, Object>();
		parameters.put("jobname", jobname);
		return fet.getText("task-title", parameters);
	}

}
