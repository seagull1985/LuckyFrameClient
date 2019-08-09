package luckyclient.serverapi.api;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import luckyclient.publicclass.remoterinterface.HttpRequest;
import luckyclient.serverapi.entity.ProjectCase;
import luckyclient.serverapi.entity.ProjectCaseParams;
import luckyclient.serverapi.entity.ProjectCaseSteps;
import luckyclient.serverapi.entity.ProjectProtocolTemplate;
import luckyclient.serverapi.entity.ProjectTemplateParams;
import luckyclient.serverapi.entity.TaskExecute;
import luckyclient.serverapi.entity.TaskScheduling;


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
public class GetServerApi {
	
	private static final String PREFIX = "/openGetApi";
	/**
	 * 通过计划ID获取测试用例对象集
	 * @param planid
	 * @return
	 */
	public static List<ProjectCase> getCasesbyplanId(int planId) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetCaseListByPlanId.do?planId=" + planId);		
        List<ProjectCase> caseList = JSONObject.parseArray(result, ProjectCase.class);
		return caseList;
	}

	/**
	 * 通过计划名称获取测试用例对象集
	 * @param name
	 * @return
	 */
	public static List<ProjectCase> getCasesbyplanname(String name) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetCaseListByPlanName.do?planName=" + name);
		List<ProjectCase> caseList = JSONObject.parseArray(result, ProjectCase.class);
		return caseList;
	}

	/**
	 * 通过用例ID获取下面的步骤对象
	 * @param caseid
	 * @return
	 */
	public static List<ProjectCaseSteps> getStepsbycaseid(Integer caseid) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetStepListByCaseId.do?caseId=" + caseid);
		List<ProjectCaseSteps> stepsList = JSONObject.parseArray(result, ProjectCaseSteps.class);
		return stepsList;
	}

	/**
	 * 通过taskid获取对象
	 * @param taskid
	 * @return
	 */
	public static TaskExecute cgetTaskbyid(int taskid) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetTaskByTaskId.do?taskId=" + taskid);
		TaskExecute task = JSONObject.parseObject(result, TaskExecute.class);
		return task;
	}
	
	/**
	 * 通过taskid获取调度对象
	 * @param taskid
	 * @return
	 */
	public static TaskScheduling cGetTaskSchedulingByTaskId(int taskid) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetTaskSchedulingByTaskId.do?taskId=" + taskid);
		TaskScheduling taskScheduling = JSONObject.parseObject(result, TaskScheduling.class);
		return taskScheduling;
	}

	/**
	 * 通过用例编号获取对象
	 * @param sign
	 * @return
	 */
	public static ProjectCase cgetCaseBysign(String sign) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetCaseByCaseSign.do?caseSign=" + sign);
		ProjectCase projectCase = JSONObject.parseObject(result, ProjectCase.class);
		return projectCase;
	}

	/**
	 * 通过用例ID获取对象
	 * @param sign
	 * @return
	 */
	public static ProjectCase cGetCaseByCaseId(Integer caseId) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetCaseByCaseId.do?caseId=" + caseId);
		ProjectCase projectCase = JSONObject.parseObject(result, ProjectCase.class);
		return projectCase;
	}
	
	/**
	 * 获取项目下的所有公共参数
	 * @param projectid
	 * @return
	 */
	public static List<ProjectCaseParams> cgetParamsByProjectid(String projectid) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetParamsByProjectId.do?projectId="+projectid);
		List<ProjectCaseParams> paramsList = JSONObject.parseArray(result, ProjectCaseParams.class);
		return paramsList;
	}
	
	/**
	 * 通过计划ID获取测试用例对象集
	 * @param planid
	 * @return
	 */
	public static List<Integer> clientGetCaseListForUnSucByTaskId(Integer taskId) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetCaseListForUnSucByTaskId.do?taskId=" + taskId);		
        List<Integer> caseIdList = JSONObject.parseArray(result, Integer.class);
		return caseIdList;
	}
	
	/**
	 * 通过templateId获取实体
	 * @param templateId
	 * @return
	 * @author Seagull
	 * @date 2019年4月24日
	 */
	public static ProjectProtocolTemplate clientGetProjectProtocolTemplateByTemplateId(Integer templateId) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetProjectProtocolTemplateByTemplateId.do?templateId=" + templateId);
		ProjectProtocolTemplate projectProtocolTemplate = JSONObject.parseObject(result, ProjectProtocolTemplate.class);
		return projectProtocolTemplate;
	}
	
	/**
	 * 通过模板ID获取参数列表
	 * @param templateId
	 * @return
	 * @author Seagull
	 * @date 2019年4月24日
	 */
	public static List<ProjectTemplateParams> clientGetProjectTemplateParamsListByTemplateId(Integer templateId) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetProjectTemplateParamsListByTemplateId.do?templateId=" + templateId);
		List<ProjectTemplateParams> projectTemplateParamsList = JSONObject.parseArray(result, ProjectTemplateParams.class);
		return projectTemplateParamsList;
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {

	}

}
