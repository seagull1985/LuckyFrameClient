package luckyclient.mail;

import java.util.HashMap;
import java.util.Map;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 * 
 * @author： seagull
 * @date 2017年12月1日 上午9:29:40
 * 
 */
public class HtmlMail {
	static FreemarkerEmailTemplate  fet=new FreemarkerEmailTemplate();
	
	public static String htmlContentFormat(int[] taskcount,String taskid,String buildstatus,String restartstatus,String time,String jobname){		
		Map<Object, Object> parameters = new HashMap<Object, Object>();
		parameters.put("buildstatus", buildstatus);
		parameters.put("restartstatus", restartstatus);
		parameters.put("taskcount", taskcount);
		parameters.put("time", time);
		parameters.put("taskid", taskid);
		parameters.put("jobname", jobname);
		return fet.getText("task-body", parameters);
	}
	
	public static String htmlSubjectFormat(String jobname){
		Map<Object, Object> parameters = new HashMap<Object, Object>();
		parameters.put("jobname", jobname);
		return fet.getText("task-title", parameters);
	}

}
