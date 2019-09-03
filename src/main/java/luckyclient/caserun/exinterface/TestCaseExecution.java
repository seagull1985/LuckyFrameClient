package luckyclient.caserun.exinterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import luckyclient.caserun.exappium.AppDriverAnalyticCase;
import luckyclient.caserun.exappium.androidex.AndroidCaseExecution;
import luckyclient.caserun.exappium.iosex.IosCaseExecution;
import luckyclient.caserun.exinterface.analyticsteps.InterfaceAnalyticCase;
import luckyclient.caserun.exwebdriver.ex.WebCaseExecution;
import luckyclient.caserun.exwebdriver.ex.WebDriverAnalyticCase;
import luckyclient.caserun.publicdispose.ActionManageForSteps;
import luckyclient.caserun.publicdispose.ParamsManageForSteps;
import luckyclient.dblog.DbLink;
import luckyclient.dblog.LogOperation;
import luckyclient.driven.SubString;
import luckyclient.publicclass.InvokeMethod;
import luckyclient.publicclass.LogUtil;
import luckyclient.serverapi.api.GetServerApi;
import luckyclient.serverapi.entity.ProjectCase;
import luckyclient.serverapi.entity.ProjectCaseParams;
import luckyclient.serverapi.entity.ProjectCaseSteps;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 *
 * @author： seagull
 * @date 2018年3月1日
 */
public class TestCaseExecution {
    protected static final String ASSIGNMENT_SIGN = "$=";
    protected static final String FUZZY_MATCHING_SIGN = "%=";
    protected static final String REGULAR_MATCHING_SIGN = "~=";
    protected static final String ASSIGNMENT_GLOBALSIGN = "$A=";
    protected static final String JSONPATH_SIGN = "$JP#";
    private static Map<String, String> VARIABLE = new HashMap<String, String>(0);

    /**
     * @param projectname        项目名
     * @param testCaseExternalId 用例编号
     * @param version            用例版本号
     *                           用于单条用例调试，并通过日志框架写日志到UTP上，用做UTP上单条用例运行
     */
    public static void oneCaseExecuteForTask(String projectname, Integer caseId, String taskid) {
        TestControl.TASKID = taskid;
        DbLink.exetype = 0;
        // 初始化写用例结果以及日志模块
        LogOperation caselog = new LogOperation();
        String packagename = null;
        String functionname = null;
        String expectedresults = null;
        Integer setcaseresult = 0;
        Object[] getParameterValues = null;
        String testnote = "初始化测试结果";
        int k = 0;
        ProjectCase testcase = GetServerApi.cGetCaseByCaseId(caseId);
        //更新用例状态
        caselog.updateTaskCaseExecuteStatus(taskid, testcase.getCaseId(), 3);
        // 删除旧的日志
        LogOperation.deleteTaskCaseLog(testcase.getCaseId(), taskid);

        List<ProjectCaseParams> pcplist = GetServerApi.cgetParamsByProjectid(String.valueOf(testcase.getProjectId()));
        // 把公共参数加入到MAP中
        for (ProjectCaseParams pcp : pcplist) {
        	VARIABLE.put(pcp.getParamsName(), pcp.getParamsValue());
        }
        // 加入全局变量
        VARIABLE.putAll(ParamsManageForSteps.GLOBAL_VARIABLE);
        List<ProjectCaseSteps> steps = GetServerApi.getStepsbycaseid(testcase.getCaseId());
        if (steps.size() == 0) {
            setcaseresult = 2;
            LogUtil.APP.warn("用例中未找到步骤，请检查！");
            caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "用例中未找到步骤，请检查！", "error", "1", "");
            testnote = "用例中未找到步骤，请检查！";
        }
        // 进入循环，解析用例所有步骤
        for (int i = 0; i < steps.size(); i++) {
            Map<String, String> casescript = InterfaceAnalyticCase.analyticCaseStep(testcase, steps.get(i), taskid, caselog,VARIABLE);
            try {
                packagename = casescript.get("PackageName");
                functionname = casescript.get("FunctionName");
            } catch (Exception e) {
                k = 0;
                LogUtil.APP.error("用例:{} 解析包名或是方法名失败，请检查！",testcase.getCaseSign(),e);
                caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "解析包名或是方法名失败，请检查！", "error", String.valueOf(i + 1), "");
                break; // 某一步骤失败后，此条用例置为失败退出
            }
            // 用例名称解析出现异常或是单个步骤参数解析异常
            if ((null != functionname && functionname.contains("解析异常")) || k == 1) {
                k = 0;
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
                    caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "解析包名：" + packagename + " 方法名：" + functionname + " 第" + (j + 1) + "个参数：" + parameterValues, "info", String.valueOf(i + 1), "");
                    getParameterValues[j] = parameterValues;
                }
            } else {
                getParameterValues = null;
            }
            // 调用动态方法，执行测试用例
            try {
                LogUtil.APP.info("开始调用方法:{} .....",functionname);
                caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "开始调用方法：" + functionname + " .....", "info", String.valueOf(i + 1), "");
                testnote = InvokeMethod.callCase(packagename, functionname, getParameterValues, steps.get(i).getStepType(), steps.get(i).getExtend());
                testnote = ActionManageForSteps.actionManage(casescript.get("Action"), testnote);
                // 判断结果
                int stepresult = interfaceJudgeResult(testcase, steps.get(i), taskid, expectedresults, testnote, caselog);
    			// 失败，并且不在继续,直接终止
                if (0 != stepresult) {
                	setcaseresult = stepresult;
                    if (testcase.getFailcontinue() == 0) {
                        LogUtil.APP.warn("用例【{}】第【{}】步骤执行失败，中断本条用例后续步骤执行，进入到下一条用例执行中......",testcase.getCaseSign(),steps.get(i).getStepSerialNumber());
                        break;
                    } else {
                        LogUtil.APP.warn("用例【{}】第【{}】步骤执行失败，继续本条用例后续步骤执行，进入下个步骤执行中......",testcase.getCaseSign(),steps.get(i).getStepSerialNumber());
                    }
                }

            } catch (Exception e) {
                caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "调用方法过程出错，方法名：" + functionname + " 请重新检查脚本方法名称以及参数！", "error", String.valueOf(i + 1), "");
                LogUtil.APP.error("调用方法过程出错，方法名:{} 请重新检查脚本方法名称以及参数！",functionname, e);
                testnote = "CallCase调用出错！";
                setcaseresult = 1;
                e.printStackTrace();
                if (testcase.getFailcontinue() == 0) {
                    LogUtil.APP.error("用例【{}】第【{}】步骤执行失败，中断本条用例后续步骤执行，进入到下一条用例执行中......",testcase.getCaseSign(),(i+1));
                    break;
                } else {
                    LogUtil.APP.error("用例【{}】第【{}】步骤执行失败，继续本条用例后续步骤执行，进入下个步骤执行中......",testcase.getCaseSign(),(i+1));
                }
            }
        }

        VARIABLE.clear(); // 清空传参MAP
        // 如果调用方法过程中未出错，进入设置测试结果流程
        if (!testnote.contains("CallCase调用出错！") && !testnote.contains("解析出错啦！")) {
            LogUtil.APP.info("用例{}解析成功，并成功调用用例中方法，请继续查看执行结果！",testcase.getCaseSign());
            caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "解析成功，并成功调用用例中方法，请继续查看执行结果！", "info", "SETCASERESULT...", "");
            caselog.updateTaskCaseExecuteStatus(taskid, testcase.getCaseId(), setcaseresult);
        } else {
            setcaseresult = 1;
            LogUtil.APP.warn("用例{}解析或是调用步骤中的方法出错！",testcase.getCaseSign());
            caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "解析或是调用步骤中的方法出错！", "error", "SETCASERESULT...", "");
            caselog.updateTaskCaseExecuteStatus(taskid, testcase.getCaseId(), 2);
        }
        if (0 == setcaseresult) {
            LogUtil.APP.info("用例{}步骤全部执行成功！",testcase.getCaseSign());
            caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "步骤全部执行成功！", "info", "EXECUTECASESUC...", "");
        } else {
            LogUtil.APP.warn("用例{}在执行过程中失败，请检查日志！",testcase.getCaseSign());
            caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "在执行过程中失败，请检查日志！", "error", "EXECUTECASESUC...", "");
        }
        LogOperation.updateTaskExecuteData(taskid, 0, 2);
    }

    /**
     * @param testCaseExternalId 用例编号
     * @param taskid             任务ID
     * @param caselog            日志操作对象
     *                           用于在UI的测试过程中，需要调用接口的测试用例
     * @deprecated
     */
    protected static String oneCaseExecuteForWebDriver(String testCaseExternalId, String taskid, LogOperation caselog) {
        Map<String, String> variable = new HashMap<String, String>(0);
        String packagename = null;
        String functionname = null;
        String expectedresults = null;
        Integer setresult = 1;
        Object[] getParameterValues = null;
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
            setresult = 2;
            LogUtil.APP.warn("用例中未找到步骤，请检查！");
            caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "用例中未找到步骤，请检查！", "error", "1", "");
            testnote = "用例中未找到步骤，请检查！";
        }
        // 进入循环，解析用例所有步骤
        for (int i = 0; i < steps.size(); i++) {
            Map<String, String> casescript = InterfaceAnalyticCase.analyticCaseStep(testcase, steps.get(i), taskid, caselog,variable);
            try {
                packagename = casescript.get("PackageName");
                functionname = casescript.get("FunctionName");
            } catch (Exception e) {
                k = 0;
                LogUtil.APP.error("用例:{} 解析包名或是方法名失败，请检查！",testcase.getCaseId(),e);
                caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "解析包名或是方法名失败，请检查！", "error", String.valueOf(i + 1), "");
                break; // 某一步骤失败后，此条用例置为失败退出
            }
            // 用例名称解析出现异常或是单个步骤参数解析异常
            if ((null != functionname && functionname.contains("解析异常")) || k == 1) {
                k = 0;
                testnote = "用例第" + (i + 1) + "步解析出错啦！";
                break;
            }
            expectedresults = casescript.get("ExpectedResults");
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
                    LogUtil.APP.info("用例:{} 解析包名:{} 方法名:{} 第{}个参数:{}",testcase.getCaseSign(),packagename,functionname,(j+1),parameterValues);
                    caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "解析包名：" + packagename + " 方法名：" + functionname + " 第" + (j + 1) + "个参数：" + parameterValues, "info", String.valueOf(i + 1), "");
                    getParameterValues[j] = parameterValues;
                }
            } else {
                getParameterValues = null;
            }
            // 调用动态方法，执行测试用例
            try {
                LogUtil.APP.info("开始调用方法:{} .....",functionname);

                testnote = InvokeMethod.callCase(packagename, functionname, getParameterValues, steps.get(i).getStepType(), steps.get(i).getExtend());
                testnote = ActionManageForSteps.actionManage(casescript.get("Action"), testnote);
                if (null != expectedresults && !expectedresults.isEmpty()) {
                    LogUtil.APP.info("expectedResults=【{}】",expectedresults);
                    // 赋值传参
                    if (expectedresults.length() > ASSIGNMENT_SIGN.length() && expectedresults.startsWith(ASSIGNMENT_SIGN)) {
                        variable.put(expectedresults.substring(ASSIGNMENT_SIGN.length()), testnote);
                        LogUtil.APP.info("用例:{} 第{}步，将测试结果【{}】赋值给变量【{}】",testcase.getCaseSign(),(i+1),testnote,expectedresults.substring(ASSIGNMENT_SIGN.length()));
                    }
                    // 模糊匹配
                    else if (expectedresults.length() > FUZZY_MATCHING_SIGN.length() && expectedresults.startsWith(FUZZY_MATCHING_SIGN)) {
                        if (testnote.contains(expectedresults.substring(FUZZY_MATCHING_SIGN.length()))) {
                            setresult = 0;
                            LogUtil.APP.info("用例:{} 第{}步，模糊匹配预期结果成功！执行结果:{}",testcase.getCaseSign(),(i+1),testnote);
                        } else {
                            setresult = 1;
                            LogUtil.APP.warn("用例:{} 第{}步，模糊匹配预期结果失败！预期结果:{}，测试结果:{}",testcase.getCaseSign(),(i+1),expectedresults.substring(FUZZY_MATCHING_SIGN.length()),testnote);
                            testnote = "用例第" + (i + 1) + "步，模糊匹配预期结果失败！";
                            break; // 某一步骤失败后，此条用例置为失败退出
                        }
                    }
                    // 正则匹配
                    else if (expectedresults.length() > REGULAR_MATCHING_SIGN.length() && expectedresults.startsWith(REGULAR_MATCHING_SIGN)) {
                        Pattern pattern = Pattern.compile(expectedresults.substring(REGULAR_MATCHING_SIGN.length()));
                        Matcher matcher = pattern.matcher(testnote);
                        if (matcher.find()) {
                            setresult = 0;
                            LogUtil.APP.info("用例:{} 第{}步，正则匹配预期结果成功！执行结果:{}",testcase.getCaseSign(),(i+1),testnote);
                        } else {
                            setresult = 1;
                            LogUtil.APP.warn("用例:{} 第{}步，正则匹配预期结果失败！预期结果:{}，测试结果:{}",testcase.getCaseSign(),(i+1),expectedresults.substring(REGULAR_MATCHING_SIGN.length()),testnote);
                            testnote = "用例第" + (i + 1) + "步，正则匹配预期结果失败！";
                            break; // 某一步骤失败后，此条用例置为失败退出
                        }
                    }
                    //jsonpath断言
                    else if (expectedresults.length() > JSONPATH_SIGN.length() && expectedresults.startsWith(JSONPATH_SIGN)) {
                        expectedresults = expectedresults.substring(JSONPATH_SIGN.length());
                        String expression = expectedresults.split("(?<!\\\\)=")[0].replace("\\=","=");
                        String exceptResult = expectedresults.split("(?<!\\\\)=")[1].replace("\\=","=");
                        //对测试结果进行jsonPath取值
                        String result = SubString.jsonPathGetParams(expression, testnote);
                        
                        if (exceptResult.equals(result)) {
                            setresult = 0;
                            LogUtil.APP.info("用例:{} 第{}步，jsonpath断言预期结果成功！预期结果:{} 测试结果: {} 执行结果:true",testcase.getCaseSign(),(i+1),exceptResult,result);
                        } else {
                            setresult = 1;
                            LogUtil.APP.warn("用例:{} 第{}步，jsonpath断言预期结果失败！预期结果:{}，测试结果:{}",testcase.getCaseSign(),(i+1),expectedresults.substring(REGULAR_MATCHING_SIGN.length()),testnote);
                            testnote = "用例第" + (i + 1) + "步，jsonpath断言预期结果失败！";
                            // 某一步骤失败后，此条用例置为失败退出
                            break;
                        }
                    }
                    // 完全相等
                    else {
                        if (expectedresults.equals(testnote)) {
                            setresult = 0;
                            LogUtil.APP.info("用例:{} 第{}步，精确匹配预期结果成功！执行结果:{}",testcase.getCaseSign(),(i+1),testnote);
                        } else {
                            setresult = 1;
                            LogUtil.APP.warn("用例:{} 第{}步，精确匹配预期结果失败！预期结果:{}，测试结果:{}",testcase.getCaseSign(),(i+1),expectedresults,testnote);
                            testnote = "用例第" + (i + 1) + "步，精确匹配预期结果失败！";
                            break; // 某一步骤失败后，此条用例置为失败退出
                        }
                    }
                }
            } catch (Exception e) {
                LogUtil.APP.error("调用方法过程出错，方法名:{} 请重新检查脚本方法名称以及参数！",functionname,e);
                testnote = "CallCase调用出错！";
                setresult = 1;
                break;
            }
        }
        variable.clear(); // 清空传参MAP
        if (0 == setresult) {
            LogUtil.APP.info("用例:{}步骤全部执行成功！",testcase.getCaseSign());
        } else {
            LogUtil.APP.warn("用例:{}在执行过程中失败，请检查日志！",testcase.getCaseSign());
        }
        return testnote;
    }

    /**
     *
     * @param testCaseExternalId
     * @param taskid
     * @param caselog
     * @param driver
     * @return
     * @throws InterruptedException
     * 提供给Web用例中，runcase的时候使用
     */
    @SuppressWarnings("unchecked")
	protected static String oneCaseExecuteForUICase(String testCaseExternalId, String taskid, LogOperation caselog, Object driver) throws InterruptedException {
        String expectedresults = null;
        Integer setresult = 1;
        String testnote = "初始化测试结果";
        ProjectCase testcase = GetServerApi.cgetCaseBysign(testCaseExternalId);
        List<ProjectCaseParams> pcplist = GetServerApi.cgetParamsByProjectid(String.valueOf(testcase.getProjectId()));
        // 把公共参数加入到MAP中
        for (ProjectCaseParams pcp : pcplist) {
        	VARIABLE.put(pcp.getParamsName(), pcp.getParamsValue());
        }
        // 加入全局变量
        VARIABLE.putAll(ParamsManageForSteps.GLOBAL_VARIABLE);
        List<ProjectCaseSteps> steps = GetServerApi.getStepsbycaseid(testcase.getCaseId());
        if (steps.size() == 0) {
            setresult = 2;
            LogUtil.APP.warn("用例中未找到步骤，请检查！");
            caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "用例中未找到步骤，请检查！", "error", "1", "");
            testnote = "用例中未找到步骤，请检查！";
        }

        // 进入循环，解析用例所有步骤
        for (ProjectCaseSteps step : steps) {
            Map<String, String> params;

            // 根据步骤类型来分析步骤参数
            if (1 == step.getStepType()){
            	params = WebDriverAnalyticCase.analyticCaseStep(testcase, step, taskid, caselog,VARIABLE);
            }else if (3 == step.getStepType()){
            	params = AppDriverAnalyticCase.analyticCaseStep(testcase, step, taskid,caselog,VARIABLE);
            } else{
            	params = InterfaceAnalyticCase.analyticCaseStep(testcase, step, taskid, caselog,VARIABLE);
            }

            // 判断分析步骤参数是否有异常
            if (params.get("exception") != null && params.get("exception").contains("解析异常")) {
                setresult = 2;
                break;
            }

            expectedresults = params.get("ExpectedResults");

            // 根据步骤类型来执行步骤
            if (1 == step.getStepType()){
            	WebDriver wd=(WebDriver)driver;
            	testnote = WebCaseExecution.runWebStep(params, wd, taskid, testcase.getCaseId(), step.getStepSerialNumber(), caselog);
                // 判断结果
                setresult = WebCaseExecution.judgeResult(testcase, step, params, wd, taskid, expectedresults, testnote, caselog);
            }else if (3 == step.getStepType()){
            	if (driver instanceof AndroidDriver){
            		AndroidDriver<AndroidElement> ad=(AndroidDriver<AndroidElement>)driver;
            		testnote = AndroidCaseExecution.androidRunStep(params, ad, taskid, testcase.getCaseId(), step.getStepSerialNumber(), caselog);
            		// 判断结果
                    setresult = AndroidCaseExecution.judgeResult(testcase, step, params, ad, taskid, expectedresults, testnote, caselog);
            	}else{
            		IOSDriver<IOSElement> ios=(IOSDriver<IOSElement>)driver;
            		testnote = IosCaseExecution.iosRunStep(params, VARIABLE, ios, taskid, testcase.getCaseId(), step.getStepSerialNumber(), caselog);
            		// 判断结果
                    setresult = IosCaseExecution.judgeResult(testcase, step, params, ios, taskid, expectedresults, testnote, caselog);
            	}

            } else{
            	testnote = runStep(params, taskid, testcase.getCaseSign(), step, caselog);
            	// 判断结果
            	setresult = interfaceJudgeResult(testcase, step, taskid, expectedresults, testnote, caselog);
            }

            if (0 != setresult){
            	break;
            }
        }

        VARIABLE.clear(); // 清空传参MAP
        if (0 == setresult) {
            LogUtil.APP.info("调用用例:{}步骤全部执行成功！",testcase.getCaseSign());
        } else {
            LogUtil.APP.warn("调用用例:{}在执行过程中失败，请检查日志！",testcase.getCaseSign());
        }
        return testnote;
    }

    /**
     * 其他类型测试用例中调用接口测试步骤
     * @param params
     * @param variable
     * @param taskid
     * @param casenum
     * @param step
     * @param caselog
     * @return
     */
    public static String runStep(Map<String, String> params, String taskid, String casenum, ProjectCaseSteps step, LogOperation caselog) {
        String result = "";
        String packagename = "";
        String functionname = "";
        Object[] getParameterValues = null;
        ProjectCase projectCase = GetServerApi.cgetCaseBysign(casenum);
        try {
            packagename = params.get("PackageName");
            functionname = params.get("FunctionName");

            if (null != functionname && functionname.contains("解析异常")) {
                LogUtil.APP.warn("用例:{}, 解析这个方法【{}】失败！",casenum,functionname);
                caselog.insertTaskCaseLog(taskid, projectCase.getCaseId(), "用例: " + casenum + ", 解析这个方法【" + functionname + "】失败！", "error", String.valueOf(step.getStepSerialNumber()), "");
                result = "步骤执行失败：解析用例失败!";
            } else {
                // 判断方法是否带参数
                if (params.size() > 4) {
                    // 获取传入参数，放入对象中
                    getParameterValues = new Object[params.size() - 4];
                    for (int j = 0; j < params.size() - 4; j++) {
                        if (params.get("FunctionParams" + (j + 1)) == null) {
                            break;
                        }
                        String parameterValues = params.get("FunctionParams" + (j + 1));
                        LogUtil.APP.info("用例:{}, 解析包路径:{}; 方法名:{} 第{}个参数:{}",casenum,packagename,functionname,(j+1),parameterValues);
                        caselog.insertTaskCaseLog(taskid, projectCase.getCaseId(), "用例: " + casenum + ", 解析包名：" + packagename + " 方法名：" + functionname + " 第" + (j + 1) + "个参数：" + parameterValues, "info", String.valueOf(step.getStepSerialNumber()), "");
                        getParameterValues[j] = parameterValues;
                    }
                } else {
                    getParameterValues = null;
                }

                LogUtil.APP.info("二次解析用例过程完成，等待进行接口操作......");
                caselog.insertTaskCaseLog(taskid, projectCase.getCaseId(), "包路径: " + packagename + "; 方法名: " + functionname, "info", String.valueOf(step.getStepSerialNumber()), "");

                result = InvokeMethod.callCase(packagename, functionname, getParameterValues, step.getStepType(), step.getExtend());
                result = ActionManageForSteps.actionManage(step.getAction(), result);
            }
        } catch (Exception e) {
            LogUtil.APP.error("调用方法过程出错，方法名:{}，请重新检查脚本方法名称以及参数！",functionname,e);
            result = "步骤执行失败：接口调用出错！";
        }
        if (result.contains("步骤执行失败：")){
        	caselog.insertTaskCaseLog(taskid, projectCase.getCaseId(), result, "error", String.valueOf(step.getStepSerialNumber()), "");
        } else{
        	caselog.insertTaskCaseLog(taskid, projectCase.getCaseId(), result, "info", String.valueOf(step.getStepSerialNumber()), "");
        }
        return result;
    }

    private static int interfaceJudgeResult(ProjectCase testcase, ProjectCaseSteps step, String taskid, String expectedresults, String testnote, LogOperation caselog){
        int setresult = 0;
        try{
        	if (null != expectedresults && !expectedresults.isEmpty()) {
                LogUtil.APP.info("expectedResults=【{}】",expectedresults);
                // 赋值传参
                if (expectedresults.length() > ASSIGNMENT_SIGN.length() && expectedresults.startsWith(ASSIGNMENT_SIGN)) {
                	VARIABLE.put(expectedresults.substring(ASSIGNMENT_SIGN.length()), testnote);
                    LogUtil.APP.info("用例:{} 第{}步，将测试结果【{}】赋值给变量【{}】",testcase.getCaseSign(),step.getStepSerialNumber(),testnote,expectedresults.substring(ASSIGNMENT_SIGN.length()));
                    caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "将测试结果【" + testnote + "】赋值给变量【" + expectedresults.substring(ASSIGNMENT_SIGN.length()) + "】", "info", String.valueOf(step.getStepSerialNumber()), "");
                }
                // 赋值全局变量
                else if (expectedresults.length() > ASSIGNMENT_GLOBALSIGN.length() && expectedresults.startsWith(ASSIGNMENT_GLOBALSIGN)) {
                	VARIABLE.put(expectedresults.substring(ASSIGNMENT_GLOBALSIGN.length()), testnote);
                    ParamsManageForSteps.GLOBAL_VARIABLE.put(expectedresults.substring(ASSIGNMENT_GLOBALSIGN.length()), testnote);
                    LogUtil.APP.info("用例:{} 第{}步，将测试结果【{}】赋值给全局变量【{}】",testcase.getCaseSign(),step.getStepSerialNumber(),testnote,expectedresults.substring(ASSIGNMENT_GLOBALSIGN.length()));
                    caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "将测试结果【" + testnote + "】赋值给全局变量【" + expectedresults.substring(ASSIGNMENT_GLOBALSIGN.length()) + "】", "info", String.valueOf(step.getStepSerialNumber()), "");
                }
                // 模糊匹配
                else if (expectedresults.length() > FUZZY_MATCHING_SIGN.length() && expectedresults.startsWith(FUZZY_MATCHING_SIGN)) {
                    if (testnote.contains(expectedresults.substring(FUZZY_MATCHING_SIGN.length()))) {
                        LogUtil.APP.info("用例:{} 第{}步，模糊匹配预期结果成功！执行结果:{}",testcase.getCaseSign(),step.getStepSerialNumber(),testnote);
                        caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "模糊匹配预期结果成功！执行结果：" + testnote, "info", String.valueOf(step.getStepSerialNumber()), "");
                    } else {
                    	setresult = 1;
                        LogUtil.APP.warn("用例:{} 第{}步，模糊匹配预期结果失败！预期结果:{}，测试结果:{}",testcase.getCaseSign(),step.getStepSerialNumber(),expectedresults.substring(FUZZY_MATCHING_SIGN.length()),testnote);
                        caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "模糊匹配预期结果失败！预期结果：" + expectedresults.substring(FUZZY_MATCHING_SIGN.length()) + "，测试结果：" + testnote, "error", String.valueOf(step.getStepSerialNumber()), "");
                        testnote = "用例第" + step.getStepSerialNumber() + "步，模糊匹配预期结果失败！";
                    }
                }
                // 正则匹配
                else if (expectedresults.length() > REGULAR_MATCHING_SIGN.length() && expectedresults.startsWith(REGULAR_MATCHING_SIGN)) {
                    Pattern pattern = Pattern.compile(expectedresults.substring(REGULAR_MATCHING_SIGN.length()));
                    Matcher matcher = pattern.matcher(testnote);
                    if (matcher.find()) {
                        LogUtil.APP.info("用例:{} 第{}步，正则匹配预期结果成功！执行结果:{}",testcase.getCaseSign(),step.getStepSerialNumber(),testnote);
                        caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "正则匹配预期结果成功！执行结果：" + testnote, "info", String.valueOf(step.getStepSerialNumber()), "");
                    } else {
                        setresult = 1;
                        LogUtil.APP.warn("用例:{} 第{}步，正则匹配预期结果失败！预期结果:{}，测试结果:{}",testcase.getCaseSign(),step.getStepSerialNumber(),expectedresults.substring(REGULAR_MATCHING_SIGN.length()),testnote);
                        caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "正则匹配预期结果失败！预期结果：" + expectedresults.substring(REGULAR_MATCHING_SIGN.length()) + "，测试结果：" + testnote, "error", String.valueOf(step.getStepSerialNumber()), "");
                        testnote = "用例第" + step.getStepSerialNumber() + "步，正则匹配预期结果失败！";
                    }
                }
                //jsonpath断言
                else if (expectedresults.length() > JSONPATH_SIGN.length() && expectedresults.startsWith(JSONPATH_SIGN)) {
                    expectedresults = expectedresults.substring(JSONPATH_SIGN.length());
                    String expression = expectedresults.split("(?<!\\\\)=")[0].replace("\\=","=");
                    String exceptResult = expectedresults.split("(?<!\\\\)=")[1].replace("\\=","=");
                    //对测试结果进行jsonPath取值
                    String result = SubString.jsonPathGetParams(expression, testnote);
                    
                    if (exceptResult.equals(result)) {
                        setresult = 0;
                        LogUtil.APP.info("用例:{} 第{}步，jsonpath断言预期结果成功！预期结果:{} 测试结果: {} 执行结果:true",testcase.getCaseSign(),step.getStepSerialNumber(),exceptResult,result);
                    } else {
                        setresult = 1;
                        LogUtil.APP.warn("用例:{} 第{}步，jsonpath断言预期结果失败！预期结果:{}，测试结果:{}" + expectedresults + "，测试结果：" + result.toString(), "error", String.valueOf(step.getStepSerialNumber()), "");
                        testnote = "用例第" + step.getStepSerialNumber() + "步，jsonpath断言预期结果失败！";
                        // 某一步骤失败后，此条用例置为失败退出
                    }

                }
                // 完全相等
                else {
                    if (expectedresults.equals(testnote)) {
                        LogUtil.APP.info("用例:{} 第{}步，精确匹配预期结果成功！执行结果:{}",testcase.getCaseSign(),step.getStepSerialNumber(),testnote);
                        caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "精确匹配预期结果成功！执行结果：" + testnote, "info", String.valueOf(step.getStepSerialNumber()), "");
                    } else {
                        setresult = 1;
                        LogUtil.APP.warn("用例:{} 第{}步，精确匹配预期结果失败！预期结果:{}，测试结果:{}",testcase.getCaseSign(),step.getStepSerialNumber(),expectedresults,testnote);
                        caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "精确匹配预期结果失败！预期结果：" + expectedresults + "，测试结果：" + testnote, "error", String.valueOf(step.getStepSerialNumber()), "");
                        testnote = "用例第" + step.getStepSerialNumber() + "步，精确匹配预期结果失败！";
                    }
                }
            }
        }catch(Exception e){
        	LogUtil.APP.error("匹配接口预期结果出现异常！",e);
        	setresult = 2;
        	return setresult;
        }
        return setresult;
    }


}
