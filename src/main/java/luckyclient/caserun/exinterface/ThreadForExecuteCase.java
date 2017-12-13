package luckyclient.caserun.exinterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import luckyclient.caserun.exinterface.analyticsteps.InterfaceAnalyticCase;
import luckyclient.dblog.LogOperation;
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
 * @ClassName: ThreadForExecuteCase
 * @Description: 线程池方式执行用例 
 * @author： seagull
 * @date 2017年7月13日 上午9:29:40
 * 
 */
public class ThreadForExecuteCase extends Thread {
	private String caseid;
	private ProjectCase testcaseob;
	private String taskid;
	private List<ProjectCasesteps> steps;
	private List<PublicCaseParams> pcplist;
	private LogOperation caselog;

	public ThreadForExecuteCase(ProjectCase projectcase, List<ProjectCasesteps> steps, String taskid,
			List<PublicCaseParams> pcplist,LogOperation caselog) {
		this.caseid = projectcase.getSign();
		this.testcaseob = projectcase;
		this.taskid = taskid;
		this.steps = steps;
		this.pcplist = pcplist;
		this.caselog = caselog;
	}
	
	@Override
	public void run() {
		Map<String, String> variable = new HashMap<String, String>(0);
		// 把公共参数加入到MAP中
		for (PublicCaseParams pcp : pcplist) {
			variable.put(pcp.getParamsname(), pcp.getParamsvalue());
		}
		String functionname = null;
		String packagename = null;
		String expectedresults = null;
		Integer setresult = null;
		Object[] getParameterValues = null;
		String testnote = null;
		int k = 0;
		// 进入循环，解析单个用例所有步骤
		// 插入开始执行的用例
		caselog.addCaseDetail(taskid, caseid, "1", testcaseob.getName(), 4); 
		for (int i = 0; i < steps.size(); i++) {
			// 解析单个步骤中的脚本
			Map<String, String> casescript = InterfaceAnalyticCase.analyticCaseStep(testcaseob, steps.get(i), taskid,
					caselog); 
			try {
				packagename = casescript.get("PackageName").toString();
				packagename = ChangString.changparams(packagename, variable,"包路径");
				functionname = casescript.get("FunctionName").toString();
				functionname = ChangString.changparams(functionname, variable,"方法名");
			} catch (Exception e) {
				k = 0;
				luckyclient.publicclass.LogUtil.APP.error("用例：" + testcaseob.getSign() + "解析包名或是方法名失败，请检查！");
				caselog.caseLogDetail(taskid, caseid, "解析包名或是方法名失败，请检查！", "error", String.valueOf(i + 1), "");
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
					caselog.caseLogDetail(taskid, caseid,
							"解析包名：" + packagename + " 方法名：" + functionname + " 第" + (j + 1) + "个参数：" + parameterValues,
							"info", String.valueOf(i + 1), "");
					getParameterValues[j] = parameterValues;
				}
			} else {
				getParameterValues = null;
			}
			// 调用动态方法，执行测试用例
			try {
				luckyclient.publicclass.LogUtil.APP
						.info("用例：" + testcaseob.getSign() + "开始调用方法：" + functionname + " .....");
				caselog.caseLogDetail(taskid, caseid, "开始调用方法：" + functionname + " .....", "info",
						String.valueOf(i + 1), "");
				// 把预期结果前两个字符判断是否是要把结果存入变量
				if (expectedresults.length() > 2 && expectedresults.substring(0, 2).indexOf("$=") > -1) { 
					String expectedResultVariable = casescript.get("ExpectedResults").toString().substring(2);
					String temptestnote = InvokeMethod.callCase(packagename, functionname, getParameterValues,
							steps.get(i).getSteptype(), steps.get(i).getAction());
					// 用例参数与公共参数冲突，优先用例参数，自动替换
					variable.put(expectedResultVariable, temptestnote);					
				} else if (expectedresults.length() > 2 && expectedresults.substring(0, 2).indexOf("%=") > -1) {
					// 把预期结果与测试结果做模糊匹配
					testnote = InvokeMethod.callCase(packagename, functionname, getParameterValues,
							steps.get(i).getSteptype(), steps.get(i).getAction());
					if (testnote.indexOf(expectedresults.substring(2)) > -1) {
						setresult = 0;
						luckyclient.publicclass.LogUtil.APP
								.info("用例：" + testcaseob.getSign() + "执行结果是：" + testnote + "，与预期结果匹配成功！");
						caselog.caseLogDetail(taskid, caseid, "执行结果是：" + testnote + "，与预期结果匹配成功！", "info",
								String.valueOf(i + 1), "");
					} else {
						setresult = 1;
						luckyclient.publicclass.LogUtil.APP
								.error("用例：" + testcaseob.getSign() + "第" + (i + 1) + "步执行结果与预期结果匹配失败！");
						luckyclient.publicclass.LogUtil.APP.error(
								"用例：" + testcaseob.getSign() + "预期结果：" + expectedresults + "      测试结果：" + testnote);
						caselog.caseLogDetail(taskid, caseid, "第" + (i + 1) + "步执行结果与预期结果匹配失败！" + "预期结果："
								+ expectedresults + "      测试结果：" + testnote, "error", String.valueOf(i + 1), "");
						testnote = "用例第" + (i + 1) + "步执行结果与预期结果匹配失败！";
						break; // 某一步骤失败后，此条用例置为失败退出
					}
				} else { // 把预期结果与测试结果做精确匹配
					testnote = InvokeMethod.callCase(packagename, functionname, getParameterValues,
							steps.get(i).getSteptype(), steps.get(i).getAction());
					if (expectedresults.equals(testnote)) {
						setresult = 0;
						luckyclient.publicclass.LogUtil.APP
								.info("用例：" + testcaseob.getSign() + "执行结果是：" + testnote + "，与预期结果匹配成功！");
						caselog.caseLogDetail(taskid, caseid, "执行结果是：" + testnote + "，与预期结果匹配成功！", "info",
								String.valueOf(i + 1), "");
					} else {
						setresult = 1;
						luckyclient.publicclass.LogUtil.APP
								.error("用例：" + testcaseob.getSign() + "第" + (i + 1) + "步执行结果与预期结果匹配失败！");
						luckyclient.publicclass.LogUtil.APP.error(
								"用例：" + testcaseob.getSign() + "预期结果：" + expectedresults + "      测试结果：" + testnote);
						caselog.caseLogDetail(taskid, caseid, "第" + (i + 1) + "步执行结果与预期结果匹配失败！" + "预期结果："
								+ expectedresults + "      测试结果：" + testnote, "error", String.valueOf(i + 1), "");
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
				luckyclient.publicclass.LogUtil.ERROR
						.error("用例：" + testcaseob.getSign() + "调用方法过程出错，方法名：" + functionname + " 请重新检查脚本方法名称以及参数！");
				caselog.caseLogDetail(taskid, caseid, "调用方法过程出错，方法名：" + functionname + " 请重新检查脚本方法名称以及参数！", "error",
						String.valueOf(i + 1), "");
				luckyclient.publicclass.LogUtil.ERROR.error(e, e);
				testnote = "CallCase调用出错！调用方法过程出错，方法名：" + functionname + " 请重新检查脚本方法名称以及参数！";
				setresult = 1;
				e.printStackTrace();
				break;
			}
		}
		// 如果调用方法过程中未出错，进入设置测试结果流程
		try {
			// 成功跟失败的用例走此流程
			if (testnote.indexOf("CallCase调用出错！") <0 && testnote.indexOf("解析出错啦！") <0) { 
				caselog.updateCaseDetail(taskid, caseid, setresult);
			} else {
				// 解析用例或是调用方法出错，全部把用例置为锁定
				luckyclient.publicclass.LogUtil.ERROR
						.error("用例：" + testcaseob.getSign() + "设置执行结果为锁定，请参考错误日志查找锁定用例的原因....."); 
				caselog.caseLogDetail(taskid, caseid, "设置执行结果为锁定，请参考错误日志查找锁定用例的原因.....", "error", "SETCASERESULT...",
						"");
				setresult = 2;
				caselog.updateCaseDetail(taskid, caseid, setresult);
			}
			if (setresult == 0) {
				luckyclient.publicclass.LogUtil.APP.info("用例：" + testcaseob.getSign() + "执行结果成功......");
				caselog.caseLogDetail(taskid, caseid, "用例步骤执行全部成功......", "info", "ending", "");
				luckyclient.publicclass.LogUtil.APP
						.info("*********用例【"+testcaseob.getSign()+"】执行完成,测试结果：成功*********");
			} else if (setresult == 1) {
				luckyclient.publicclass.LogUtil.ERROR.error("用例：" + testcaseob.getSign() + "执行结果失败......");
				caselog.caseLogDetail(taskid, caseid, "用例执行结果失败......", "error", "ending", "");
				luckyclient.publicclass.LogUtil.APP
						.info("*********用例【"+testcaseob.getSign()+"】执行完成,测试结果：失败*********");
			} else {
				luckyclient.publicclass.LogUtil.ERROR.error("用例：" + testcaseob.getSign() + "执行结果锁定......");
				caselog.caseLogDetail(taskid, caseid, "用例执行结果锁定......", "error", "ending", "");
				luckyclient.publicclass.LogUtil.APP
						.info("*********用例【"+testcaseob.getSign()+"】执行完成,测试结果：锁定*********");
			}
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.ERROR.error("用例：" + testcaseob.getSign() + "设置执行结果过程出错......");
			caselog.caseLogDetail(taskid, caseid, "设置执行结果过程出错......", "error", "ending", "");
			luckyclient.publicclass.LogUtil.ERROR.error(e, e);
			e.printStackTrace();
		} finally {
			variable.clear(); // 一条用例结束后，清空变量存储空间
			TestControl.Debugcount--; // 多线程计数--，用于检测线程是否全部执行完
		}
	}

	public static void main(String[] args) {
	}

}
