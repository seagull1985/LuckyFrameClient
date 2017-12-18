package luckyclient.planapi.api;

import java.io.UnsupportedEncodingException;
import java.util.List;

import luckyclient.planapi.entity.ProjectCase;
import luckyclient.planapi.entity.ProjectCasesteps;
import luckyclient.planapi.entity.PublicCaseParams;
import luckyclient.planapi.entity.TestTaskexcute;
import luckyclient.publicclass.remoterinterface.HttpRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

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
public class GetServerAPI {

	/**
	 * 通过计划ID获取测试用例对象集
	 * @param planid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<ProjectCase> getCasesbyplanid(int planid) {
		String result = HttpRequest.loadJSON("/projectPlanCase/cgetcasebyplanid.do?planid=" + planid);
		JSONObject jsonObject = JSONObject.fromObject(result.toString());
		JSONArray jsonarr = JSONArray.fromObject(jsonObject.getString("cases"));
		List<ProjectCase> list = JSONArray.toList(jsonarr, new ProjectCase(), new JsonConfig());
		return list;
	}

	/**
	 * 通过计划名称获取测试用例对象集
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<ProjectCase> getCasesbyplanname(String name) {
		String result = HttpRequest.loadJSON("/projectPlanCase/cgetcasebyplanname.do?name=" + name);
		JSONObject jsonObject = JSONObject.fromObject(result.toString());
		JSONArray jsonarr = JSONArray.fromObject(jsonObject.getString("cases"));
		List<ProjectCase> list = JSONArray.toList(jsonarr, new ProjectCase(), new JsonConfig());
		return list;
	}

	/**
	 * 通过用例ID获取下面的步骤对象
	 * @param caseid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<ProjectCasesteps> getStepsbycaseid(int caseid) {
		String result = HttpRequest.loadJSON("/projectCasesteps/cgetStepsByCase.do?caseid=" + caseid);
		JSONObject jsonObject = JSONObject.fromObject(result.toString());
		JSONArray jsonarr = JSONArray.fromObject(jsonObject.getString("steps"));
		List<ProjectCasesteps> list = JSONArray.toList(jsonarr, new ProjectCasesteps(), new JsonConfig());
		return list;
	}

	/**
	 * 通过taskid获取对象
	 * @param taskid
	 * @return
	 */
	public static TestTaskexcute cgetTaskbyid(int taskid) {
		String result = HttpRequest.loadJSON("/tastExecute/cgettaskbyid.do?taskid=" + taskid);
		JSONObject jsonObject = JSONObject.fromObject(result.toString());
		TestTaskexcute task = (TestTaskexcute) JSONObject.toBean(jsonObject, TestTaskexcute.class);
		return task;
	}

	/**
	 * 通过用例编号获取对象
	 * @param sign
	 * @return
	 */
	public static ProjectCase cgetCaseBysign(String sign) {
		String result = HttpRequest.loadJSON("/projectCase/cgetcasebysign.do?sign=" + sign);
		JSONObject jsonObject = JSONObject.fromObject(result.toString());
		ProjectCase pc = (ProjectCase) JSONObject.toBean(jsonObject, ProjectCase.class);
		return pc;
	}

	/**
	 * 获取项目下的所有公共参数
	 * @param projectid
	 * @return
	 */
	public static List<PublicCaseParams> cgetParamsByProjectid(String projectid) {
		String result = HttpRequest.loadJSON("/publicCaseParams/cgetParamsByProjectid.do?projectid="+projectid);
		JSONObject jsonObject = JSONObject.fromObject(result.toString());
		JSONArray jsonarr = JSONArray.fromObject(jsonObject.getString("params"));
		@SuppressWarnings("unchecked")
		List<PublicCaseParams> list = JSONArray.toList(jsonarr, new PublicCaseParams(), new JsonConfig());
		return list;
	}
	
	/**
	 * put web界面的数据到服务端
	 * @param sign
	 * @param executor
	 * @param loglevel
	 * @param detail
	 */
	public static void cPostDebugLog(String sign, String executor, String loglevel, String detail){
		sign = sign.replace("%","BBFFHH");
		sign = sign.replace("=","DHDHDH");
		sign = sign.replace("&","ANDAND");
		detail = detail.replace("%","BBFFHH");
		detail = detail.replace("=","DHDHDH");
		detail = detail.replace("&","ANDAND");
		String params = "";
		params = "sign=" + sign;
		params += "&executor=" + executor;
		params += "&executor=" + executor;
		params += "&loglevel=" + loglevel;
		params += "&detail=" + detail;

		String logid = HttpRequest.sendPost("/projectCasesteps/cPostDebugLog.do", params);
		System.out.println("已经成功写入临时日志库，ID：" + logid);
	}

	/**
	 * 供其他系统远程调用调度任务，启动执行
	 * @param jobid
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String cRunJobForId(String jobid) throws UnsupportedEncodingException {
		String result = HttpRequest.loadJSON("/testJobs/runJobForInterface.do?jobid=" + jobid);
		return new String(result.getBytes("GBK"), "UTF-8");
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		getCasesbyplanid(79);
	}

}
