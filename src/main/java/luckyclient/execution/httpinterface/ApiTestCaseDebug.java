package luckyclient.execution.httpinterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import luckyclient.execution.dispose.ActionManageForSteps;
import luckyclient.execution.httpinterface.analyticsteps.InterfaceAnalyticCase;
import luckyclient.remote.api.GetServerApi;
import luckyclient.remote.entity.ProjectCase;
import luckyclient.remote.entity.ProjectCaseParams;
import luckyclient.remote.entity.ProjectCaseSteps;
import luckyclient.utils.InvokeMethod;
import luckyclient.utils.LogUtil;
import luckyclient.utils.httputils.HttpRequest;

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
	 * @param testCaseExternalId 用例编号
	 */
	public static void oneCaseDebug(String testCaseExternalId) {
		Map<String, String> variable = new HashMap<>(0);
		String packagename;
		String functionname;
		String expectedresults;
		int setcaseresult = 0;
		Object[] getParameterValues;
		String testnote = "初始化测试结果";
		int k = 0;
		ProjectCase testcase = GetServerApi.cgetCaseBysign(testCaseExternalId);
		List<ProjectCaseParams> pcplist = GetServerApi.cgetParamsByProjectid(String.valueOf(testcase.getProjectId()));
		// 把公共参数加入到MAP中
		for (ProjectCaseParams pcp : pcplist) {
			variable.put(pcp.getParamsName(), pcp.getParamsValue());
		}
		List<ProjectCaseSteps> steps = GetServerApi.getStepsbycaseid(testcase.getCaseId());
		if (steps.size() == 0) {
			setcaseresult = 2;
			LogUtil.APP.warn("用例中未找到步骤，请检查！");
			testnote = "用例中未找到步骤，请检查！";
		}
		// 进入循环，解析用例所有步骤
		for (int i = 0; i < steps.size(); i++) {
			Map<String, String> casescript = InterfaceAnalyticCase.analyticCaseStep(testcase, steps.get(i), "888888",
					null,variable);
			try {
				packagename = casescript.get("PackageName");
				functionname = casescript.get("FunctionName");
			} catch (Exception e) {
				LogUtil.APP.error("用例:{} 解析包名或是方法名失败，请检查！",testcase.getCaseSign(),e);
				break; // 某一步骤失败后，此条用例置为失败退出
			}
			// 用例名称解析出现异常或是单个步骤参数解析异常
			if (functionname.contains("解析异常") || k == 1) {
				testnote = "用例第" + (i + 1) + "步解析出错啦！";
				break;
			}
			expectedresults = casescript.get("ExpectedResults");
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
					LogUtil.APP.info("用例:{} 解析包名:{} 方法名:{} 第{}个参数:{}",testcase.getCaseSign(),packagename,functionname,(j+1),parameterValues);
					getParameterValues[j] = parameterValues;
				}
			} else {
				getParameterValues = null;
			}
			// 调用动态方法，执行测试用例
			try {
				LogUtil.APP.info("开始调用方法:{} .....",functionname);
				testnote = InvokeMethod.callCase(packagename, functionname, getParameterValues,
						steps.get(i).getStepType(), steps.get(i).getExtend());
				testnote = ActionManageForSteps.actionManage(casescript.get("Action"), testnote);
				if (null != expectedresults && !expectedresults.isEmpty()) {
					LogUtil.APP.info("expectedResults=【{}】",expectedresults);
					// 赋值传参
					if (expectedresults.length() > ASSIGNMENT_SIGN.length()
							&& expectedresults.startsWith(ASSIGNMENT_SIGN)) {
						variable.put(expectedresults.substring(ASSIGNMENT_SIGN.length()), testnote);
						LogUtil.APP
								.info("用例:{} 第{}步，将测试结果【{}】赋值给变量【{}】",testcase.getCaseSign(),(i+1),testnote,expectedresults.substring(ASSIGNMENT_SIGN.length()));
					}
					// 模糊匹配
					else if (expectedresults.length() > FUZZY_MATCHING_SIGN.length()
							&& expectedresults.startsWith(FUZZY_MATCHING_SIGN)) {
						if (testnote.contains(expectedresults.substring(FUZZY_MATCHING_SIGN.length()))) {
							LogUtil.APP.info(
									"用例:{} 第{}步，模糊匹配预期结果成功！执行结果:{}",testcase.getCaseSign(),(i+1),testnote);
						} else {
							setcaseresult = 1;
							LogUtil.APP.warn("用例:{} 第{}步，模糊匹配预期结果失败！预期结果{}，测试结果:{}",testcase.getCaseSign(),(i+1),expectedresults.substring(FUZZY_MATCHING_SIGN.length()),testnote);
							testnote = "用例第" + (i + 1) + "步，模糊匹配预期结果失败！";
			                if (testcase.getFailcontinue() == 0) {
			                    LogUtil.APP.warn("用例【{}】第【{}】步骤执行失败，中断本条用例后续步骤执行，进入到下一条用例执行中......",testcase.getCaseSign(),(i+1));
			                    break;
			                } else {
			                    LogUtil.APP.warn("用例【{}】第【{}】步骤执行失败，继续本条用例后续步骤执行，进入下个步骤执行中......",testcase.getCaseSign(),(i+1));
			                }
						}
					}
					// 正则匹配
					else if (expectedresults.length() > REGULAR_MATCHING_SIGN.length()
							&& expectedresults.startsWith(REGULAR_MATCHING_SIGN)) {
						Pattern pattern = Pattern.compile(expectedresults.substring(REGULAR_MATCHING_SIGN.length()));
						Matcher matcher = pattern.matcher(testnote);
						if (matcher.find()) {
							LogUtil.APP.info(
									"用例:{} 第{}步，正则匹配预期结果成功！执行结果:{}",testcase.getCaseSign(),(i+1),testnote);
						} else {
							setcaseresult = 1;
							LogUtil.APP.warn("用例:{} 第{}步，正则匹配预期结果失败！预期结果:{}，测试结果:{}",testcase.getCaseSign(),(i+1),expectedresults.substring(REGULAR_MATCHING_SIGN.length()),testnote);
							testnote = "用例第" + (i + 1) + "步，正则匹配预期结果失败！";
			                if (testcase.getFailcontinue() == 0) {
			                    LogUtil.APP.warn("用例【{}】第【{}】步骤执行失败，中断本条用例后续步骤执行，进入到下一条用例执行中......",testcase.getCaseSign(),(i+1));
			                    break;
			                } else {
			                    LogUtil.APP.warn("用例【{}】第【{}】步骤执行失败，继续本条用例后续步骤执行，进入下个步骤执行中......",testcase.getCaseSign(),(i+1));
			                }
						}
					}
					// 完全相等
					else {
						if (expectedresults.equals(testnote)) {
							LogUtil.APP.info(
									"用例:{} 第{}步，精确匹配预期结果成功！执行结果:{}",testcase.getCaseSign(),(i+1),testnote);
						} else {
							setcaseresult = 1;
							LogUtil.APP.warn("用例:{} 第{}步，精确匹配预期结果失败！预期结果:{}，测试结果:{}",testcase.getCaseSign(),(i+1),expectedresults,testnote);
							testnote = "用例第" + (i + 1) + "步，精确匹配预期结果失败！";
			                if (testcase.getFailcontinue() == 0) {
			                    LogUtil.APP.warn("用例【{}】第【{}】步骤执行失败，中断本条用例后续步骤执行，进入到下一条用例执行中......",testcase.getCaseSign(),(i+1));
			                    break;
			                } else {
			                    LogUtil.APP.warn("用例【{}】第【{}】步骤执行失败，继续本条用例后续步骤执行，进入下个步骤执行中......",testcase.getCaseSign(),(i+1));
			                }
						}
					}
				}
			} catch (Exception e) {
				setcaseresult = 1;
				LogUtil.APP.error("调用方法过程出错，方法名:{} 请重新检查脚本方法名称以及参数！",functionname,e);
				testnote = "CallCase调用出错！";
                if (testcase.getFailcontinue() == 0) {
                    LogUtil.APP.warn("用例【{}】第【{}】步骤执行失败，中断本条用例后续步骤执行，进入到下一条用例执行中......",testcase.getCaseSign(),(i+1));
                    break;
                } else {
                    LogUtil.APP.warn("用例【{}】第【{}】步骤执行失败，继续本条用例后续步骤执行，进入下个步骤执行中......",testcase.getCaseSign(),(i+1));
                }
			}
		}
		variable.clear(); // 清空传参MAP
		// 如果调用方法过程中未出错，进入设置测试结果流程
		if (!testnote.contains("CallCase调用出错！") && !testnote.contains("解析出错啦！")) {
			LogUtil.APP.info("用例{}解析成功，并成功调用用例中方法，请继续查看执行结果！",testCaseExternalId);
		} else {
			LogUtil.APP.warn("用例{}解析或是调用步骤中的方法出错！",testCaseExternalId);
		}
		if (0 == setcaseresult) {
			LogUtil.APP.info("用例{}步骤全部执行成功！",testCaseExternalId);
		} else {
			LogUtil.APP.warn("用例{}在执行过程中失败，请检查日志！",testCaseExternalId);
		}
	}

	/**
	 * 用于在本地做多条用例串行调试
	 * @param projectname 项目名称
	 * @param addtestcase 用例集
	 */
	public static void moreCaseDebug(String projectname, List<String> addtestcase) {
		System.out.println("当前调试用例总共："+addtestcase.size());
		for(String testCaseExternalId:addtestcase) {
			try {
				LogUtil.APP
						.info("开始调用方法，项目名:{}，用例编号:{}",projectname,testCaseExternalId);
				oneCaseDebug(testCaseExternalId);
			} catch (Exception e) {
				LogUtil.APP.error("批量Debug用例出现异常！",e);
			}
		}
	}

	/**
	 * 更新系统中用例指定步骤的预期结果
	 */
	public static String setExpectedResults(String testCaseSign, int steps, String expectedResults) {
		String results;
		String params;
		try {
			expectedResults = expectedResults.replace("%", "BBFFHH");
			expectedResults = expectedResults.replace("=", "DHDHDH");
			expectedResults = expectedResults.replace("&", "ANDAND");
			params = "caseno=" + testCaseSign;
			params += "&stepnum=" + steps;
			params += "&expectedresults=" + expectedResults;
			results = HttpRequest.sendPost("/projectCasesteps/cUpdateStepExpectedResults.do", params);
		} catch (Exception e) {
			LogUtil.APP.error("更新系统中用例指定步骤的预期结果出现异常！",e);
			return "更新系统中用例指定步骤的预期结果出现异常！";
		}
		return results;

	}

}
