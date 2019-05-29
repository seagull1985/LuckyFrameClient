package luckyclient.serverapi.api;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import luckyclient.publicclass.remoterinterface.HttpRequest;
import luckyclient.serverapi.entity.ProjectCaseDebug;
import luckyclient.serverapi.entity.TaskCaseExecute;
import luckyclient.serverapi.entity.TaskCaseLog;


/**
 * 
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 Seagull
 * =================================================================
 * @author Seagull
 * @date 2019年4月18日
 */
public class PostServerAPI {
	
	private static final String prefix = "/openPostApi";
	
	/**
	 * put web界面的数据到服务端
	 * @param sign
	 * @param executor
	 * @param loglevel
	 * @param detail
	 */
	public static void cPostDebugLog(Integer userId, Integer caseId, String logLevel, String logDetail,Integer debugIsend){
		ProjectCaseDebug ProjectCaseDebug = new ProjectCaseDebug();
		ProjectCaseDebug.setCaseId(caseId);
		ProjectCaseDebug.setUserId(userId);
		ProjectCaseDebug.setLogLevel(logLevel);
		ProjectCaseDebug.setLogDetail(logDetail);
		ProjectCaseDebug.setDebugIsend(debugIsend);
		
		HttpRequest.httpClientPostJson(prefix+"/clientPostCaseDebugLog", JSONObject.toJSONString(ProjectCaseDebug));
	}

	/**
	 * 插入用例执行明细到数据库
	 * @param taskId
	 * @param projectId
	 * @param caseId
	 * @param caseSign
	 * @param caseName
	 * @param caseStatus
	 * @author Seagull
	 * @date 2019年4月22日
	 */
	public static void clientPostInsertTaskCaseExecute(Integer taskId, Integer projectId, Integer caseId, String caseSign, String caseName, Integer caseStatus){
		TaskCaseExecute taskCaseExecute = new TaskCaseExecute();
		taskCaseExecute.setTaskId(taskId);
		taskCaseExecute.setProjectId(projectId);
		taskCaseExecute.setCaseId(caseId);
		taskCaseExecute.setCaseSign(caseSign);
		taskCaseExecute.setCaseName(caseName);
		taskCaseExecute.setCaseStatus(caseStatus);
		taskCaseExecute.setCreateTime(new Date());
		taskCaseExecute.setUpdateTime(new Date());
		
		HttpRequest.httpClientPostJson(prefix+"/clientPostTaskCaseExecute", JSONObject.toJSONString(taskCaseExecute));
	}
	
	/**
	 * 修改用例执行状态
	 * @param taskId
	 * @param caseId
	 * @param caseStatus
	 * @author Seagull
	 * @date 2019年4月22日
	 */
	public static void clientUpdateTaskCaseExecuteStatus(Integer taskId, Integer caseId, Integer caseStatus){
		TaskCaseExecute taskCaseExecute = new TaskCaseExecute();
		taskCaseExecute.setTaskId(taskId);
		taskCaseExecute.setCaseId(caseId);
		taskCaseExecute.setCaseStatus(caseStatus);
		taskCaseExecute.setUpdateTime(new Date());
		
		HttpRequest.httpClientPostJson(prefix+"/clientUpdateTaskCaseExecuteStatus", JSONObject.toJSONString(taskCaseExecute));
	}
	
	/**
	 * 插入用例执行明细到数据库
	 * @param taskId
	 * @param caseId
	 * @param logDetail
	 * @param logGrade
	 * @param logStep
	 * @param imgname
	 * @author Seagull
	 * @date 2019年4月22日
	 */
	public static void clientPostInsertTaskCaseLog(Integer taskId, Integer caseId, String logDetail, String logGrade, String logStep,
			String imgname){
		TaskCaseLog taskCaseLog = new TaskCaseLog();
		taskCaseLog.setTaskId(taskId);
		taskCaseLog.setCaseId(caseId);
		taskCaseLog.setLogDetail(logDetail);
		taskCaseLog.setLogGrade(logGrade);
		taskCaseLog.setLogStep(logStep);
		taskCaseLog.setImgname(imgname);
		taskCaseLog.setCreateTime(new Date());
		taskCaseLog.setUpdateTime(new Date());
		
		HttpRequest.httpClientPostJson(prefix+"/clientPostTaskCaseLog", JSONObject.toJSONString(taskCaseLog));
	}
	
	/**
	 * 更新任务执行数据
	 * @param taskId
	 * @param casecount
	 * @author Seagull
	 * @date 2019年4月22日
	 */
	public static String clientUpdateTaskExecuteData(Integer taskId, Integer caseCount, Integer taskStatus){
		String str = "{\"taskId\":"+taskId+",\"caseCount\":"+caseCount+",\"taskStatus\":"+taskStatus+"}";
		JSONObject jsonObject = JSON.parseObject(str);
		return HttpRequest.httpClientPostJson(prefix+"/clientUpdateTaskExecuteData", jsonObject.toJSONString());
	}
	
	/**
	 * 更新任务执行数据
	 * @param taskId
	 * @param casecount
	 * @author Seagull
	 * @date 2019年4月22日
	 */
	public static String clientDeleteTaskCaseLog(Integer taskId, Integer caseId){
		String str = "{\"taskId\":"+taskId+",\"caseId\":"+caseId+"}";
		JSONObject jsonObject = JSON.parseObject(str);
		return HttpRequest.httpClientPostJson(prefix+"/clientDeleteTaskCaseLog", jsonObject.toJSONString());
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {

	}

}
