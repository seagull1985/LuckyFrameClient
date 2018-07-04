package luckyclient.caserun.exinterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.eti.kinoshita.testlinkjavaapi.util.TestLinkAPIException;
import luckyclient.caserun.exinterface.analyticsteps.InterfaceAnalyticCase;
import luckyclient.dblog.LogOperation;
import luckyclient.planapi.api.GetServerAPI;
import luckyclient.planapi.entity.ProjectCase;
import luckyclient.planapi.entity.ProjectCasesteps;
import luckyclient.planapi.entity.PublicCaseParams;
import luckyclient.publicclass.ChangString;
import luckyclient.publicclass.InvokeMethod;
import luckyclient.publicclass.remoterinterface.HttpRequest;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 seagull1985
 * =================================================================
 * 
 * @ClassName: TestCaseDebug
 * @Description: 针对自动化用例在编写过程中，对用例脚本进行调试 @author： seagull
 * @date 2018年3月1日
 * 
 */
public class ApiTestCaseDebug {
	private static final String ASSIGNMENT_SIGN = "$=";
	private static final String FUZZY_MATCHING_SIGN = "%=";
	private static final String REGULAR_MATCHING_SIGN = "~=";

	/**
	 * 用于在本地做单条用例调试
	 * 
	 * @param projectname
	 * @param testCaseExternalId
	 */
	public static void oneCaseDebug(String projectname, String testCaseExternalId) {
		Map<String, String> variable = new HashMap<String, String>(0);
		String packagename = null;
		String functionname = null;
		String expectedresults = null;
		Integer setresult = 1;
		Object[] getParameterValues = null;
		String testnote = "初始化测试结果";
		int k = 0;
		ProjectCase testcaseob = GetServerAPI.cgetCaseBysign(testCaseExternalId);
		List<PublicCaseParams> pcplist = GetServerAPI.cgetParamsByProjectid(String.valueOf(testcaseob.getProjectid()));
		// 把公共参数加入到MAP中
		for (PublicCaseParams pcp : pcplist) {
			variable.put(pcp.getParamsname(), pcp.getParamsvalue());
		}
		List<ProjectCasesteps> steps = GetServerAPI.getStepsbycaseid(testcaseob.getId());
		if (steps.size() == 0) {
			setresult = 2;
			luckyclient.publicclass.LogUtil.APP.error("用例中未找到步骤，请检查！");
			testnote = "用例中未找到步骤，请检查！";
		}
		// 进入循环，解析用例所有步骤
		for (int i = 0; i < steps.size(); i++) {
			Map<String, String> casescript = InterfaceAnalyticCase.analyticCaseStep(testcaseob, steps.get(i), "888888",
					null);
			try {
				packagename = casescript.get("PackageName").toString();
				packagename = ChangString.changparams(packagename, variable, "包路径");
				functionname = casescript.get("FunctionName").toString();
				functionname = ChangString.changparams(functionname, variable, "方法名");
			} catch (Exception e) {
				k = 0;
				luckyclient.publicclass.LogUtil.APP.error("用例：" + testcaseob.getSign() + "解析包名或是方法名失败，请检查！");
				e.printStackTrace();
				break; // 某一步骤失败后，此条用例置为失败退出
			}
			// 用例名称解析出现异常或是单个步骤参数解析异常
			if (functionname.indexOf("解析异常") > -1 || k == 1) {
				k = 0;
				testnote = "用例第" + (i + 1) + "步解析出错啦！";
				break;
			}
			expectedresults = casescript.get("ExpectedResults").toString();
			expectedresults = ChangString.changparams(expectedresults, variable, "预期结果");
			// 判断方法是否带参数
			if (casescript.size() > 4) {
				// 获取传入参数，放入对象中，初始化参数对象个数
				getParameterValues = new Object[casescript.size() - 4];
				for (int j = 0; j < casescript.size() - 4; j++) {
					if (casescript.get("FunctionParams" + (j + 1)) == null) {
						k = 1;
						break;
					}
					String parameterValues = casescript.get("FunctionParams" + (j + 1));
					parameterValues = ChangString.changparams(parameterValues, variable, "用例参数");
					luckyclient.publicclass.LogUtil.APP.info("用例：" + testcaseob.getSign() + "解析包名：" + packagename
							+ " 方法名：" + functionname + " 第" + (j + 1) + "个参数：" + parameterValues);
					getParameterValues[j] = parameterValues;
				}
			} else {
				getParameterValues = null;
			}
			// 调用动态方法，执行测试用例
			try {
				luckyclient.publicclass.LogUtil.APP.info("开始调用方法：" + functionname + " .....");
				testnote = InvokeMethod.callCase(packagename, functionname, getParameterValues,
						steps.get(i).getSteptype(), steps.get(i).getAction());

				if (null != expectedresults && !expectedresults.isEmpty()) {
					luckyclient.publicclass.LogUtil.APP.info("expectedResults=【" + expectedresults + "】");
					// 赋值传参
					if (expectedresults.length() > ASSIGNMENT_SIGN.length()
							&& expectedresults.startsWith(ASSIGNMENT_SIGN)) {
						variable.put(expectedresults.substring(ASSIGNMENT_SIGN.length()), testnote);
						luckyclient.publicclass.LogUtil.APP
								.info("用例：" + testcaseob.getSign() + " 第" + (i + 1) + "步，将测试结果【" + testnote + "】赋值给变量【"
										+ expectedresults.substring(ASSIGNMENT_SIGN.length()) + "】");
					}
					// 模糊匹配
					else if (expectedresults.length() > FUZZY_MATCHING_SIGN.length()
							&& expectedresults.startsWith(FUZZY_MATCHING_SIGN)) {
						if (testnote.contains(expectedresults.substring(FUZZY_MATCHING_SIGN.length()))) {
							setresult = 0;
							luckyclient.publicclass.LogUtil.APP.info(
									"用例：" + testcaseob.getSign() + " 第" + (i + 1) + "步，模糊匹配预期结果成功！执行结果：" + testnote);
						} else {
							setresult = 1;
							luckyclient.publicclass.LogUtil.APP.error("用例：" + testcaseob.getSign() + " 第" + (i + 1)
									+ "步，模糊匹配预期结果失败！预期结果：" + expectedresults.substring(FUZZY_MATCHING_SIGN.length())
									+ "，测试结果：" + testnote);
							testnote = "用例第" + (i + 1) + "步，模糊匹配预期结果失败！";
							break; // 某一步骤失败后，此条用例置为失败退出
						}
					}
					// 正则匹配
					else if (expectedresults.length() > REGULAR_MATCHING_SIGN.length()
							&& expectedresults.startsWith(REGULAR_MATCHING_SIGN)) {
						Pattern pattern = Pattern.compile(expectedresults.substring(REGULAR_MATCHING_SIGN.length()));
						Matcher matcher = pattern.matcher(testnote);
						if (matcher.find()) {
							setresult = 0;
							luckyclient.publicclass.LogUtil.APP.info(
									"用例：" + testcaseob.getSign() + " 第" + (i + 1) + "步，正则匹配预期结果成功！执行结果：" + testnote);
						} else {
							setresult = 1;
							luckyclient.publicclass.LogUtil.APP.error("用例：" + testcaseob.getSign() + " 第" + (i + 1)
									+ "步，正则匹配预期结果失败！预期结果：" + expectedresults.substring(REGULAR_MATCHING_SIGN.length())
									+ "，测试结果：" + testnote);
							testnote = "用例第" + (i + 1) + "步，正则匹配预期结果失败！";
							break; // 某一步骤失败后，此条用例置为失败退出
						}
					}
					// 完全相等
					else {
						if (expectedresults.equals(testnote)) {
							setresult = 0;
							luckyclient.publicclass.LogUtil.APP.info(
									"用例：" + testcaseob.getSign() + " 第" + (i + 1) + "步，精确匹配预期结果成功！执行结果：" + testnote);
						} else {
							setresult = 1;
							luckyclient.publicclass.LogUtil.APP.error("用例：" + testcaseob.getSign() + " 第" + (i + 1)
									+ "步，精确匹配预期结果失败！预期结果：" + expectedresults + "，测试结果：" + testnote);
							testnote = "用例第" + (i + 1) + "步，精确匹配预期结果失败！";
							break; // 某一步骤失败后，此条用例置为失败退出
						}
					}
				}
				// 获取步骤间等待时间
				int waitsec = Integer.parseInt(casescript.get("StepWait").toString());
				if (waitsec != 0) {
					Thread.sleep(waitsec * 1000);
				}
			} catch (Exception e) {
				setresult = 1;
				luckyclient.publicclass.LogUtil.APP.error("调用方法过程出错，方法名：" + functionname + " 请重新检查脚本方法名称以及参数！");
				luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
				testnote = "CallCase调用出错！";
				e.printStackTrace();
				break;
			}
		}
		variable.clear(); // 清空传参MAP
		// 如果调用方法过程中未出错，进入设置测试结果流程
		if (testnote.indexOf("CallCase调用出错！") <= -1 && testnote.indexOf("解析出错啦！") <= -1) {
			luckyclient.publicclass.LogUtil.APP.info("用例 " + testCaseExternalId + "解析成功，并成功调用用例中方法，请继续查看执行结果！");
		} else {
			luckyclient.publicclass.LogUtil.APP.error("用例 " + testCaseExternalId + "解析或是调用步骤中的方法出错！");
		}
		if (0 == setresult) {
			luckyclient.publicclass.LogUtil.APP.info("用例 " + testCaseExternalId + "步骤全部执行成功！");
		} else {
			luckyclient.publicclass.LogUtil.APP.error("用例 " + testCaseExternalId + "在执行过程中失败，请检查日志！");
		}
	}

	/**
	 * 用于在本地做多条用例串行调试
	 * 
	 * @param projectname
	 * @param addtestcase
	 */
	public static void moreCaseDebug(String projectname, List<String> addtestcase) {
		System.out.println("当前调试用例总共："+addtestcase.size());
		for(String testCaseExternalId:addtestcase) {
			try {
				luckyclient.publicclass.LogUtil.APP
						.info("开始调用方法，项目名：" + projectname + "，用例编号：" + testCaseExternalId);
				oneCaseDebug(projectname, testCaseExternalId);
			} catch (Exception e) {
				continue;
			}
		}
	}

	/**
	 * 获取指定任务名称以及用例号报错日志中的执行预期结果 casestatus说明 pass:0 fail:1 lock:2 unexcute:4
	 */
	public static String getLogDetailExpectresult(String taskname, String caseno, int casestatus) {
		int taskid = LogOperation.getTaskExcuteTaskid(taskname);
		return LogOperation.getLogDetailExpectResult(taskid, caseno, casestatus);
	}

	/**
	 * 获取指定任务名称以及用例号报错日志中的执行测试结果 casestatus说明 pass:0 fail:1 lock:2 unexcute:4
	 */
	public static String getLogDetailRunresult(String taskname, String caseno, int casestatus) {
		int taskid = LogOperation.getTaskExcuteTaskid(taskname);
		return LogOperation.getLogDetailTestResult(taskid, caseno, casestatus);
	}

	/**
	 * 更新系统中用例指定步骤的预期结果
	 */
	public static String setExpectedResults(String testCaseSign, int steps, String expectedResults) {
		String results = "设置结果失败";
		String params = "";
		try {
			expectedResults = expectedResults.replace("%", "BBFFHH");
			expectedResults = expectedResults.replace("=", "DHDHDH");
			expectedResults = expectedResults.replace("&", "ANDAND");
			params = "caseno=" + testCaseSign;
			params += "&stepnum=" + steps;
			params += "&expectedresults=" + expectedResults;
			results = HttpRequest.sendPost("/projectCasesteps/cUpdateStepExpectedResults.do", params);
		} catch (TestLinkAPIException te) {
			te.printStackTrace(System.err);
			results = te.getMessage().toString();
			return results;
		}
		return results;

	}

	public static void main(String[] args) throws Exception {

	}
}
