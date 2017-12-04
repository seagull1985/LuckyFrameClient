package luckyclient.caserun.exinterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import luckyclient.caserun.exinterface.analyticsteps.InterfaceAnalyticCase;
import luckyclient.dblog.DbLink;
import luckyclient.dblog.LogOperation;
import luckyclient.planapi.api.GetServerAPI;
import luckyclient.planapi.entity.ProjectCase;
import luckyclient.planapi.entity.ProjectCasesteps;
import luckyclient.planapi.entity.PublicCaseParams;
import luckyclient.publicclass.ChangString;
import luckyclient.publicclass.InvokeMethod;

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
public class TestCaseExecution {
	/**
	 * @param 项目名
	 * @param 用例编号
	 * @param 用例版本号
	 * 用于单条用例调试，并通过日志框架写日志到UTP上，用做UTP上单条用例运行
	 */
	@SuppressWarnings("static-access")
	public static void oneCaseExecuteForTast(String projectname, String testCaseExternalId, int version,
			String taskid) {
		Map<String, String> variable = new HashMap<String, String>(0);
		TestControl.TASKID = taskid;
		DbLink.exetype = 0;
		// 初始化写用例结果以及日志模块
		LogOperation caselog = new LogOperation(); 
		String packagename = null;
		String functionname = null;
		String expectedresults = null;
		Integer setresult = null;
		Object[] getParameterValues = null;
		String testnote = null;
		int k = 0;
		// 删除旧的日志
		LogOperation.deleteCaseLogDetail(testCaseExternalId, taskid); 
		ProjectCase testcaseob = GetServerAPI.cgetCaseBysign(testCaseExternalId);
		List<PublicCaseParams> pcplist=GetServerAPI.cgetParamsByProjectid(String.valueOf(testcaseob.getProjectid()));
		// 把公共参数加入到MAP中
		for (PublicCaseParams pcp : pcplist) {
			variable.put(pcp.getParamsname(), pcp.getParamsvalue());
		}
		List<ProjectCasesteps> steps = GetServerAPI.getStepsbycaseid(testcaseob.getId());
		// 进入循环，解析用例所有步骤
		for (int i = 0; i < steps.size(); i++) {
			Map<String, String> casescript = InterfaceAnalyticCase.analyticCaseStep(testcaseob, steps.get(i), taskid,caselog);
			try {
				packagename = casescript.get("PackageName").toString();
				packagename = ChangString.changparams(packagename, variable,"包路径");
				functionname = casescript.get("FunctionName").toString();
				functionname = ChangString.changparams(functionname, variable,"方法名");
			} catch (Exception e) {
				k = 0;
				luckyclient.publicclass.LogUtil.APP.error("用例：" + testcaseob.getSign() + "解析包名或是方法名失败，请检查！");
				caselog.caseLogDetail(taskid, testcaseob.getSign(), "解析包名或是方法名失败，请检查！", "error", String.valueOf(i + 1), "");
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
			expectedresults = ChangString.changparams(expectedresults, variable,"预期结果");
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
					parameterValues = ChangString.changparams(parameterValues, variable,"用例参数");
					luckyclient.publicclass.LogUtil.APP.info("用例：" + testcaseob.getSign() + "解析包名：" + packagename
							+ " 方法名：" + functionname + " 第" + (j + 1) + "个参数：" + parameterValues);
					caselog.caseLogDetail(taskid, testcaseob.getSign(),
							"解析包名：" + packagename + " 方法名：" + functionname + " 第" + (j + 1) + "个参数：" + parameterValues,
							"info", String.valueOf(i + 1), "");
					getParameterValues[j] = parameterValues;
				}
			} else {
				getParameterValues = null;
			}
			// 调用动态方法，执行测试用例
			try {
				luckyclient.publicclass.LogUtil.APP.info("开始调用方法：" + functionname + " .....");
				LogOperation.updateCaseLogDetail(testCaseExternalId, taskid, "开始调用方法：" + functionname + " .....", "info",
						String.valueOf(i + 1));
				// 把预期结果前两个字符判断是否是要把结果存入变量
				if (expectedresults.length() > 2 && expectedresults.substring(0, 2).indexOf("$=") > -1) { 
					String expectedResultVariable = casescript.get("ExpectedResults").toString().substring(2);
					String temptestnote = InvokeMethod.callCase(packagename, functionname, getParameterValues,steps.get(i).getSteptype(),steps.get(i).getAction());
					variable.put(expectedResultVariable, temptestnote);
				} else if (expectedresults.length() > 2 && expectedresults.substring(0, 2).indexOf("%=") > -1) { 
					testnote = InvokeMethod.callCase(packagename, functionname, getParameterValues,steps.get(i).getSteptype(),steps.get(i).getAction());
					if (testnote.indexOf(expectedresults.substring(2)) > -1) {
						setresult = 0;
						luckyclient.publicclass.LogUtil.APP.info("用例执行结果是：" + testnote + "，与预期结果匹配成功！");
						LogOperation.updateCaseLogDetail(testCaseExternalId, taskid, "用例执行结果是：" + testnote + "，与预期结果匹配成功！",
								"info", String.valueOf(i + 1));
					} else {
						setresult = 1;
						luckyclient.publicclass.LogUtil.APP.error("用例第" + (i + 1) + "步执行结果与预期结果匹配失败！");
						LogOperation.updateCaseLogDetail(testCaseExternalId, taskid,
								"用例第" + (i + 1) + "步执行结果与预期结果匹配失败！预期结果：" + expectedresults + "      测试结果：" + testnote,
								"error", String.valueOf(i + 1));
						luckyclient.publicclass.LogUtil.APP.error("预期结果：" + expectedresults + "      测试结果：" + testnote);
						testnote = "用例第" + (i + 1) + "步执行结果与预期结果匹配失败！";
						break; // 某一步骤失败后，此条用例置为失败退出
					}
				} else { // 把预期结果与测试结果做精确匹配
					testnote = InvokeMethod.callCase(packagename, functionname, getParameterValues,steps.get(i).getSteptype(),steps.get(i).getAction());
					if (expectedresults.equals(testnote)) {
						setresult = 0;
						luckyclient.publicclass.LogUtil.APP.info("用例执行结果是：" + testnote + "，与预期结果匹配成功！");
						LogOperation.updateCaseLogDetail(testCaseExternalId, taskid, "用例执行结果是：" + testnote + "，与预期结果匹配成功！",
								"info", String.valueOf(i + 1));
					} else {
						setresult = 1;
						luckyclient.publicclass.LogUtil.APP.error("用例第" + (i + 1) + "步执行结果与预期结果匹配失败！");
						LogOperation.updateCaseLogDetail(testCaseExternalId, taskid,
								"用例第" + (i + 1) + "步执行结果与预期结果匹配失败！预期结果：" + expectedresults + "      测试结果：" + testnote,
								"error", String.valueOf(i + 1));
						luckyclient.publicclass.LogUtil.APP.error("预期结果：" + expectedresults + "      测试结果：" + testnote);
						StringBuilder stringBuilder = new StringBuilder();
						stringBuilder.append("用例第" + (i + 1) + "步执行结果与预期结果匹配失败！预期结果：" + expectedresults + "      测试结果：");
						stringBuilder.append(testnote);
						testnote = stringBuilder.toString();
						break; // 某一步骤失败后，此条用例置为失败退出
					}
				}
				// 获取步骤间等待时间
				int waitsec = Integer.parseInt(casescript.get("StepWait").toString()); 
				if (waitsec != 0) {
					Thread.sleep(waitsec * 1000);
				}
			} catch (Exception e) {
				luckyclient.publicclass.LogUtil.APP.error("调用方法过程出错，方法名：" + functionname + " 请重新检查脚本方法名称以及参数！");
				LogOperation.updateCaseLogDetail(testCaseExternalId, taskid,
						"调用方法过程出错，方法名：" + functionname + " 请重新检查脚本方法名称以及参数！", "error", String.valueOf(i + 1));
				luckyclient.publicclass.LogUtil.APP.error(e, e);
				testnote = "CallCase调用出错！";
				setresult = 1;
				e.printStackTrace();
				break;
			}
		}
		variable.clear(); // 清空传参MAP
		// 如果调用方法过程中未出错，进入设置测试结果流程
		if (testnote.indexOf("CallCase调用出错！") <= -1 && testnote.indexOf("解析出错啦！") <= -1) {
			luckyclient.publicclass.LogUtil.APP.info("用例 " + testCaseExternalId + "解析成功，并成功调用用例中方法，请继续查看执行结果！");
			LogOperation.updateCaseLogDetail(testCaseExternalId, taskid, "解析成功，并成功调用用例中方法，请继续查看执行结果！", "info",
					"SETCASERESULT...");
			// TCResult =
			// TestCaseApi.setTCResult(projectname,testCaseExternalId, testnote,
			// version,setresult);
			caselog.updateCaseDetail(taskid, testCaseExternalId, setresult);
		} else {
			setresult = 1;
			luckyclient.publicclass.LogUtil.APP.error("用例 " + testCaseExternalId + "解析或是调用步骤中的方法出错！");
			LogOperation.updateCaseLogDetail(testCaseExternalId, taskid, "解析或是调用步骤中的方法出错！", "error", "SETCASERESULT...");
			// TCResult =
			// TestCaseApi.setTCResult(projectname,testCaseExternalId, testnote,
			// version,2);
			caselog.updateCaseDetail(taskid, testCaseExternalId, 2);
		}
		if (0 == setresult) {
			luckyclient.publicclass.LogUtil.APP.info("用例 " + testCaseExternalId + "步骤全部执行成功！");
			LogOperation.updateCaseLogDetail(testCaseExternalId, taskid, "步骤全部执行成功！", "info", "EXECUTECASESUC...");
		} else {
			luckyclient.publicclass.LogUtil.APP.error("用例 " + testCaseExternalId + "在执行过程中失败，请检查日志！");
			LogOperation.updateCaseLogDetail(testCaseExternalId, taskid, "在执行过程中失败，请检查日志！", "error", "EXECUTECASESUC...");
		}
		LogOperation.updateTastdetail(taskid, 0);
	}

	/**
	 * @param 项目名
	 * @param 用例编号
	 * @param 用例版本号
	 *            用于在UI的测试过程中，需要调用接口的测试用例
	 */
	protected static String oneCaseExecuteForWebDriver(String testCaseExternalId, String taskid,LogOperation caselog) {
		Map<String, String> variable = new HashMap<String, String>(0);
		String packagename = null;
		String functionname = null;
		String expectedresults = null;
		Integer setresult = null;
		Object[] getParameterValues = null;
		String testnote = null;
		int k = 0;
		ProjectCase testcaseob = GetServerAPI.cgetCaseBysign(testCaseExternalId);
		List<PublicCaseParams> pcplist=GetServerAPI.cgetParamsByProjectid(String.valueOf(testcaseob.getProjectid()));
		// 把公共参数加入到MAP中
		for (PublicCaseParams pcp : pcplist) {
			variable.put(pcp.getParamsname(), pcp.getParamsvalue());
		}
		List<ProjectCasesteps> steps = GetServerAPI.getStepsbycaseid(testcaseob.getId());
		// 进入循环，解析用例所有步骤
		for (int i = 0; i < steps.size(); i++) {
			Map<String, String> casescript = InterfaceAnalyticCase.analyticCaseStep(testcaseob, steps.get(i), taskid,caselog);
			try {
				packagename = casescript.get("PackageName").toString();
				packagename = ChangString.changparams(packagename, variable,"包路径");
				functionname = casescript.get("FunctionName").toString();
				functionname = ChangString.changparams(functionname, variable,"方法名");
			} catch (Exception e) {
				k = 0;
				luckyclient.publicclass.LogUtil.APP.error("用例：" + testcaseob.getSign() + "解析包名或是方法名失败，请检查！");
				caselog.caseLogDetail(taskid, testcaseob.getSign(), "解析包名或是方法名失败，请检查！", "error", String.valueOf(i + 1), "");
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
			expectedresults = ChangString.changparams(expectedresults, variable,"预期结果");
			// 判断方法是否带参数
			if (casescript.size() > 4) {
				// 获取传入参数，放入对象中
				getParameterValues = new Object[casescript.size() - 4]; 
				for (int j = 0; j < casescript.size() - 4; j++) {
					if (casescript.get("FunctionParams" + (j + 1)) == null) {
						k = 1;
						break;
					}
					
					String parameterValues = casescript.get("FunctionParams" + (j + 1));
					parameterValues = ChangString.changparams(parameterValues, variable,"用例参数");
					luckyclient.publicclass.LogUtil.APP.info("用例：" + testcaseob.getSign() + "解析包名：" + packagename
							+ " 方法名：" + functionname + " 第" + (j + 1) + "个参数：" + parameterValues);
					caselog.caseLogDetail(taskid, testcaseob.getSign(),
							"解析包名：" + packagename + " 方法名：" + functionname + " 第" + (j + 1) + "个参数：" + parameterValues,
							"info", String.valueOf(i + 1), "");
					getParameterValues[j] = parameterValues;

				}
			} else {
				getParameterValues = null;
			}
			// 调用动态方法，执行测试用例
			try {
				luckyclient.publicclass.LogUtil.APP.info("开始调用方法：" + functionname + " .....");
				// 把预期结果前两个字符判断是否是要把结果存入变量
				if (expectedresults.length() > 2 && expectedresults.substring(0, 2).indexOf("$=") > -1) { 
					String expectedResultVariable = casescript.get("ExpectedResults").toString().substring(2);
					String temptestnote = InvokeMethod.callCase(packagename, functionname, getParameterValues,steps.get(i).getSteptype(),steps.get(i).getAction());
					variable.put(expectedResultVariable, temptestnote);
				} else if (expectedresults.length() > 2 && expectedresults.substring(0, 2).indexOf("%=") > -1) { 
					testnote = InvokeMethod.callCase(packagename, functionname, getParameterValues,steps.get(i).getSteptype(),steps.get(i).getAction());
					if (testnote.indexOf(expectedresults.substring(2)) > -1) {
						setresult = 0;
						luckyclient.publicclass.LogUtil.APP.info("用例执行结果是：" + testnote + "，与预期结果匹配成功！");
					} else {
						setresult = 1;
						luckyclient.publicclass.LogUtil.APP.error("用例第" + (i + 1) + "步执行结果与预期结果匹配失败！");
						luckyclient.publicclass.LogUtil.APP.error("预期结果：" + expectedresults + "      测试结果：" + testnote);
						testnote = "用例第" + (i + 1) + "步执行结果与预期结果匹配失败！";
						break; // 某一步骤失败后，此条用例置为失败退出
					}
				} else { // 把预期结果与测试结果做精确匹配
					testnote = InvokeMethod.callCase(packagename, functionname, getParameterValues,steps.get(i).getSteptype(),steps.get(i).getAction());
					if ("".equals(expectedresults) || testnote.equals(expectedresults)) {
						setresult = 0;
						luckyclient.publicclass.LogUtil.APP.info("用例执行结果是：" + testnote + "，与预期结果匹配成功！");
					} else {
						setresult = 1;
						luckyclient.publicclass.LogUtil.APP.error("用例第" + (i + 1) + "步执行结果与预期结果匹配失败！");
						luckyclient.publicclass.LogUtil.APP.error("预期结果：" + expectedresults + "      测试结果：" + testnote);
						StringBuilder stringBuilder = new StringBuilder();
						stringBuilder.append("用例第" + (i + 1) + "步执行结果与预期结果匹配失败！预期结果：" + expectedresults + "      测试结果：");
						stringBuilder.append(testnote);
						testnote = stringBuilder.toString();
						break; // 某一步骤失败后，此条用例置为失败退出
					}
				}
				int waitsec = Integer.parseInt(casescript.get("StepWait").toString()); 
				if (waitsec != 0) {
					Thread.sleep(waitsec * 1000);
				}
			} catch (Exception e) {
				luckyclient.publicclass.LogUtil.APP.error("调用方法过程出错，方法名：" + functionname + " 请重新检查脚本方法名称以及参数！");
				luckyclient.publicclass.LogUtil.APP.error(e, e);
				testnote = "CallCase调用出错！";
				setresult = 1;
				e.printStackTrace();
				break;
			}
		}
		variable.clear(); // 清空传参MAP
		if (0 == setresult) {
			luckyclient.publicclass.LogUtil.APP.info("用例 " + testcaseob.getSign() + "步骤全部执行成功！");
		} else {
			luckyclient.publicclass.LogUtil.APP.error("用例 " + testcaseob.getSign() + "在执行过程中失败，请检查日志！");
		}
		return testnote;
	}

}
