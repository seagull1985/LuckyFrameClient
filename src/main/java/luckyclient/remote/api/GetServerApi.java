package luckyclient.remote.api;

import com.alibaba.fastjson.JSONObject;
import luckyclient.execution.SchedulingConstants;
import luckyclient.execution.dispose.ParamsManageForSteps;
import luckyclient.remote.entity.*;
import luckyclient.utils.httputils.HttpRequest;

import java.util.List;


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
	 * @param planId 测试计划ID
	 * @return 返回用例List
	 */
	public static List<ProjectCase> getCasesbyplanId(int planId) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetCaseListByPlanId.do?planId=" + planId);
		return JSONObject.parseArray(result, ProjectCase.class);
	}

	/**
	 * 通过计划ID获取测试用例对象集
	 * @param suiteId 测试计划ID
	 * @return 返回用例List
	 */
	public static List<ProjectPlan> getPlansbysuiteId(int suiteId) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetPlanListBySuiteId.do?suiteId=" + suiteId);
		return JSONObject.parseArray(result, ProjectPlan.class);
	}

	/**
	 * 通过计划名称获取测试用例对象集
	 * @param name 测试计划名称
	 * @return 返回用例List
	 */
	public static List<ProjectCase> getCasesbyplanname(String name) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetCaseListByPlanName.do?planName=" + name);
		return JSONObject.parseArray(result, ProjectCase.class);
	}

	/**
	 * 通过用例ID获取下面的步骤对象
	 * @param caseid 用例ID
	 * @return 返回用例步骤List
	 */
	public static List<ProjectCaseSteps> getStepsbycaseid(Integer caseid) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetStepListByCaseId.do?caseId=" + caseid);
		return JSONObject.parseArray(result, ProjectCaseSteps.class);
	}

	/**
	 * 通过taskid获取对象
	 * @param taskid 测试任务ID
	 * @return 返回测试任务对象
	 */
	public static TaskExecute cgetTaskbyid(int taskid) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetTaskByTaskId.do?taskId=" + taskid);
		return JSONObject.parseObject(result, TaskExecute.class);
	}
	
	/**
	 * 通过taskid获取调度对象
	 * @param taskid 测试任务ID
	 * @return 返回调度对象
	 */
	public static TaskScheduling cGetTaskSchedulingByTaskId(int taskid) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetTaskSchedulingByTaskId.do?taskId=" + taskid);
		return JSONObject.parseObject(result, TaskScheduling.class);
	}

	/**
	 * 通过用例编号获取对象
	 * @param sign 用例编号
	 * @return 用例对象
	 */
	public static ProjectCase cgetCaseBysign(String sign) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetCaseByCaseSign.do?caseSign=" + sign);
		return JSONObject.parseObject(result, ProjectCase.class);
	}

	/**
	 * 通过用例ID获取对象
	 * @param caseId 用例ID
	 * @return 用例对象
	 */
	public static ProjectCase cGetCaseByCaseId(Integer caseId) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetCaseByCaseId.do?caseId=" + caseId);
		return JSONObject.parseObject(result, ProjectCase.class);
	}
	
	/**
	 * 获取项目下的所有公共参数
	 * @param projectid 项目ID
	 * @return 公共参数集合
	 */
	public static List<ProjectCaseParams> cgetParamsByProjectid(String projectid) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetParamsByProjectIdAndEnvName.do?projectId="+projectid+"&envName="+ SchedulingConstants.envName);
		List<ProjectCaseParams> paramsList = JSONObject.parseArray(result, ProjectCaseParams.class);
		//当公共参数存在内置函数时，先进行数据转换
		for(ProjectCaseParams pcp:paramsList){
			pcp.setParamsValue(ParamsManageForSteps.paramsManage(pcp.getParamsValue()));
		}
		return paramsList;
	}

	/**
	 * 通过计划ID获取测试用例对象集
	 * @param taskId 测试任务ID
	 * @return 测试用例ID集合
	 */
	public static List<Integer> clientGetCaseListForUnSucByTaskId(Integer taskId) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetCaseListForUnSucByTaskId.do?taskId=" + taskId);
		return JSONObject.parseArray(result, Integer.class);
	}
	
	/**
	 * 通过templateId获取实体
	 * @param templateId 模板ID
	 * @return 协议模板对象
	 * @author Seagull
	 * @date 2019年4月24日
	 */
	public static ProjectProtocolTemplate clientGetProjectProtocolTemplateByTemplateId(Integer templateId) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetProjectProtocolTemplateByTemplateId.do?templateId=" + templateId);
		return JSONObject.parseObject(result, ProjectProtocolTemplate.class);
	}
	
	/**
	 * 通过模板ID获取参数列表
	 * @param templateId 模板ID
	 * @return 参数集合
	 * @author Seagull
	 * @date 2019年4月24日
	 */
	public static List<ProjectTemplateParams> clientGetProjectTemplateParamsListByTemplateId(Integer templateId) {
		String result = HttpRequest.loadJSON(PREFIX+"/clientGetProjectTemplateParamsListByTemplateId.do?templateId=" + templateId);
		return JSONObject.parseArray(result, ProjectTemplateParams.class);
	}

}
