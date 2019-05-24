package luckyclient.caserun.exinterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import luckyclient.caserun.exinterface.analyticsteps.InterfaceAnalyticCase;
import luckyclient.caserun.publicdispose.ActionManageForSteps;
import luckyclient.caserun.publicdispose.ChangString;
import luckyclient.caserun.publicdispose.ParamsManageForSteps;
import luckyclient.dblog.LogOperation;
import luckyclient.publicclass.InvokeMethod;
import luckyclient.publicclass.LogUtil;
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
 * @ClassName: ThreadForExecuteCase
 * @Description: 线程池方式执行用例
 * @author： seagull
 * @date 2018年3月1日
 */
public class ThreadForExecuteCase extends Thread {
    private static final String ASSIGNMENT_SIGN = "$=";
    private static final String ASSIGNMENT_GLOBALSIGN = "$A=";
    private static final String FUZZY_MATCHING_SIGN = "%=";
    private static final String REGULAR_MATCHING_SIGN = "~=";

    private Integer caseId;
    private String caseSign;
    private ProjectCase testcase;
    private String taskid;
    private Integer projectId;
    private List<ProjectCaseSteps> steps;
    private List<ProjectCaseParams> pcplist;
    private LogOperation caselog;

    public ThreadForExecuteCase(ProjectCase projectcase, List<ProjectCaseSteps> steps, String taskid, List<ProjectCaseParams> pcplist, LogOperation caselog) {
        this.caseId = projectcase.getCaseId();
        this.testcase = projectcase;
        this.projectId = projectcase.getProjectId();
        this.caseSign = projectcase.getCaseSign();
        this.taskid = taskid;
        this.steps = steps;
        this.pcplist = pcplist;
        this.caselog = caselog;
    }

    @Override
    public void run() {
        Map<String, String> variable = new HashMap<>(0);
        // 把公共参数加入到MAP中
        for (ProjectCaseParams pcp : pcplist) {
            variable.put(pcp.getParamsName(), pcp.getParamsValue());
        }
        // 加入全局变量
        variable.putAll(ParamsManageForSteps.GLOBAL_VARIABLE);
        String functionname = null;
        String packagename = null;
        String expectedresults = null;
        Integer setcaseresult = 0;
        Object[] getParameterValues = null;
        String testnote = "初始化测试结果";
        int k = 0;
        // 进入循环，解析单个用例所有步骤
        // 插入开始执行的用例
        caselog.insertTaskCaseExecute(taskid, projectId, caseId, caseSign, testcase.getCaseName(), 3);
        for (int i = 0; i < steps.size(); i++) {
            // 解析单个步骤中的脚本
            Map<String, String> casescript = InterfaceAnalyticCase.analyticCaseStep(testcase, steps.get(i), taskid, caselog);
            try {
                packagename = casescript.get("PackageName");
                packagename = ChangString.changparams(packagename, variable, "包路径");
                functionname = casescript.get("FunctionName");
                functionname = ChangString.changparams(functionname, variable, "方法名");
            } catch (Exception e) {
                k = 0;
                LogUtil.APP.error("用例：" + testcase.getCaseSign() + "解析包名或是方法名失败，请检查！");
                caselog.insertTaskCaseLog(taskid, caseId, "解析包名或是方法名失败，请检查！", "error", String.valueOf(i + 1), "");
                e.printStackTrace();
                break; // 某一步骤失败后，此条用例置为失败退出
            }
            // 用例名称解析出现异常或是单个步骤参数解析异常
            if ((null != functionname && functionname.contains("解析异常")) || k == 1) {
                k = 0;
                testnote = "用例第" + (i + 1) + "步解析出错啦！";
                break;
            }
            expectedresults = casescript.get("ExpectedResults");
            expectedresults = ChangString.changparams(expectedresults, variable, "预期结果");
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
                    parameterValues = ChangString.changparams(parameterValues, variable, "用例参数");
                    LogUtil.APP.info("用例：" + testcase.getCaseSign() + "解析包名：" + packagename + " 方法名：" + functionname + " 第" + (j + 1) + "个参数：" + parameterValues);
                    caselog.insertTaskCaseLog(taskid, caseId, "解析包名：" + packagename + " 方法名：" + functionname + " 第" + (j + 1) + "个参数：" + parameterValues, "info", String.valueOf(i + 1), "");
                    getParameterValues[j] = parameterValues;
                }
            } else {
                getParameterValues = null;
            }
            // 调用动态方法，执行测试用例
            try {
                LogUtil.APP.info("用例：" + testcase.getCaseSign() + "开始调用方法：" + functionname + " .....");
                caselog.insertTaskCaseLog(taskid, caseId, "开始调用方法：" + functionname + " .....", "info", String.valueOf(i + 1), "");

                testnote = InvokeMethod.callCase(packagename, functionname, getParameterValues, steps.get(i).getStepType(), steps.get(i).getExtend());
                testnote = ActionManageForSteps.actionManage(casescript.get("Action"), testnote);
                if (null != expectedresults && !expectedresults.isEmpty()) {
                    LogUtil.APP.info("expectedResults=【" + expectedresults + "】");
                    // 赋值传参
                    if (expectedresults.length() > ASSIGNMENT_SIGN.length() && expectedresults.startsWith(ASSIGNMENT_SIGN)) {
                        variable.put(expectedresults.substring(ASSIGNMENT_SIGN.length()), testnote);
                        LogUtil.APP.info("用例：" + testcase.getCaseSign() + " 第" + (i + 1) + "步，将测试结果【" + testnote + "】赋值给变量【" + expectedresults.substring(ASSIGNMENT_SIGN.length()) + "】");
                        caselog.insertTaskCaseLog(taskid, caseId, "将测试结果【" + testnote + "】赋值给变量【" + expectedresults.substring(ASSIGNMENT_SIGN.length()) + "】", "info", String.valueOf(i + 1), "");
                    }
                    // 赋值全局变量
                    else if (expectedresults.length() > ASSIGNMENT_GLOBALSIGN.length() && expectedresults.startsWith(ASSIGNMENT_GLOBALSIGN)) {
                        variable.put(expectedresults.substring(ASSIGNMENT_GLOBALSIGN.length()), testnote);
                        ParamsManageForSteps.GLOBAL_VARIABLE.put(expectedresults.substring(ASSIGNMENT_GLOBALSIGN.length()), testnote);
                        LogUtil.APP.info("用例：" + testcase.getCaseSign() + " 第" + (i + 1) + "步，将测试结果【" + testnote + "】赋值给全局变量【" + expectedresults.substring(ASSIGNMENT_GLOBALSIGN.length()) + "】");
                        caselog.insertTaskCaseLog(taskid, caseId, "将测试结果【" + testnote + "】赋值给全局变量【" + expectedresults.substring(ASSIGNMENT_GLOBALSIGN.length()) + "】", "info", String.valueOf(i + 1), "");
                    }
                    // 模糊匹配
                    else if (expectedresults.length() > FUZZY_MATCHING_SIGN.length() && expectedresults.startsWith(FUZZY_MATCHING_SIGN)) {
                        if (testnote.contains(expectedresults.substring(FUZZY_MATCHING_SIGN.length()))) {
                            LogUtil.APP.info("用例：" + testcase.getCaseSign() + " 第" + (i + 1) + "步，模糊匹配预期结果成功！执行结果：" + testnote);
                            caselog.insertTaskCaseLog(taskid, caseId, "模糊匹配预期结果成功！执行结果：" + testnote, "info", String.valueOf(i + 1), "");
                        } else {
                            setcaseresult = 1;
                            LogUtil.APP.warn("用例：" + testcase.getCaseSign() + " 第" + (i + 1) + "步，模糊匹配预期结果失败！预期结果：" + expectedresults.substring(FUZZY_MATCHING_SIGN.length()) + "，测试结果：" + testnote);
                            caselog.insertTaskCaseLog(taskid, caseId, "第" + (i + 1) + "步，模糊匹配预期结果失败！预期结果：" + expectedresults.substring(FUZZY_MATCHING_SIGN.length()) + "，测试结果：" + testnote, "error", String.valueOf(i + 1), "");
                            testnote = "用例第" + (i + 1) + "步，模糊匹配预期结果失败！";
                            if (testcase.getFailcontinue() == 0) {
                                LogUtil.APP.warn("用例【"+testcase.getCaseSign()+"】第【"+(i + 1)+"】步骤执行失败，中断本条用例后续步骤执行，进入到下一条用例执行中......");
                                break;
                            } else {
                                LogUtil.APP.warn("用例【"+testcase.getCaseSign()+"】第【"+(i + 1)+"】步骤执行失败，继续本条用例后续步骤执行，进入下个步骤执行中......");
                            }
                        }
                    }
                    // 正则匹配
                    else if (expectedresults.length() > REGULAR_MATCHING_SIGN.length() && expectedresults.startsWith(REGULAR_MATCHING_SIGN)) {
                        Pattern pattern = Pattern.compile(expectedresults.substring(REGULAR_MATCHING_SIGN.length()));
                        Matcher matcher = pattern.matcher(testnote);
                        if (matcher.find()) {
                            LogUtil.APP.info("用例：" + testcase.getCaseSign() + " 第" + (i + 1) + "步，正则匹配预期结果成功！执行结果：" + testnote);
                            caselog.insertTaskCaseLog(taskid, caseId, "正则匹配预期结果成功！执行结果：" + testnote, "info", String.valueOf(i + 1), "");
                        } else {
                            setcaseresult = 1;
                            LogUtil.APP.warn("用例：" + testcase.getCaseSign() + " 第" + (i + 1) + "步，正则匹配预期结果失败！预期结果：" + expectedresults.substring(REGULAR_MATCHING_SIGN.length()) + "，测试结果：" + testnote);
                            caselog.insertTaskCaseLog(taskid, caseId, "第" + (i + 1) + "步，正则匹配预期结果失败！预期结果：" + expectedresults.substring(REGULAR_MATCHING_SIGN.length()) + "，测试结果：" + testnote, "error", String.valueOf(i + 1), "");
                            testnote = "用例第" + (i + 1) + "步，正则匹配预期结果失败！";
                            if (testcase.getFailcontinue() == 0) {
                                LogUtil.APP.warn("用例【"+testcase.getCaseSign()+"】第【"+(i + 1)+"】步骤执行失败，中断本条用例后续步骤执行，进入到下一条用例执行中......");
                                break;
                            } else {
                                LogUtil.APP.warn("用例【"+testcase.getCaseSign()+"】第【"+(i + 1)+"】步骤执行失败，继续本条用例后续步骤执行，进入下个步骤执行中......");
                            }
                        }
                    }
                    // 完全相等
                    else {
                        if (expectedresults.equals(testnote)) {
                            LogUtil.APP.info("用例：" + testcase.getCaseSign() + " 第" + (i + 1) + "步，精确匹配预期结果成功！执行结果：" + testnote);
                            caselog.insertTaskCaseLog(taskid, caseId, "精确匹配预期结果成功！执行结果：" + testnote, "info", String.valueOf(i + 1), "");
                        } else {
                            setcaseresult = 1;
                            LogUtil.APP.warn("用例：" + testcase.getCaseSign() + " 第" + (i + 1) + "步，精确匹配预期结果失败！预期结果：" + expectedresults + "，测试结果：" + testnote);
                            caselog.insertTaskCaseLog(taskid, caseId, "第" + (i + 1) + "步，精确匹配预期结果失败！预期结果：" + expectedresults + "，测试结果：" + testnote, "error", String.valueOf(i + 1), "");
                            testnote = "用例第" + (i + 1) + "步，精确匹配预期结果失败！";
                            if (testcase.getFailcontinue() == 0) {
                                LogUtil.APP.warn("用例【"+testcase.getCaseSign()+"】第【"+(i + 1)+"】步骤执行失败，中断本条用例后续步骤执行，进入到下一条用例执行中......");
                                break;
                            } else {
                                LogUtil.APP.warn("用例【"+testcase.getCaseSign()+"】第【"+(i + 1)+"】步骤执行失败，继续本条用例后续步骤执行，进入下个步骤执行中......");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LogUtil.APP.error("用例：" + testcase.getCaseSign() + "调用方法过程出错，方法名：" + functionname + " 请重新检查脚本方法名称以及参数！",e);
                caselog.insertTaskCaseLog(taskid, caseId, "调用方法过程出错，方法名：" + functionname + " 请重新检查脚本方法名称以及参数！", "error", String.valueOf(i + 1), "");
                testnote = "CallCase调用出错！调用方法过程出错，方法名：" + functionname + " 请重新检查脚本方法名称以及参数！";
                setcaseresult = 1;
                e.printStackTrace();
                if (testcase.getFailcontinue() == 0) {
                    LogUtil.APP.error("用例【"+testcase.getCaseSign()+"】第【"+(i + 1)+"】步骤执行失败，中断本条用例后续步骤执行，进入到下一条用例执行中......");
                    break;
                } else {
                    LogUtil.APP.error("用例【"+testcase.getCaseSign()+"】第【"+(i + 1)+"】步骤执行失败，继续本条用例后续步骤执行，进入下个步骤执行中......");
                }
            }
        }
        // 如果调用方法过程中未出错，进入设置测试结果流程
        try {
            // 成功跟失败的用例走此流程
            if (!testnote.contains("CallCase调用出错！") && !testnote.contains("解析出错啦！")) {
                caselog.updateTaskCaseExecuteStatus(taskid, caseId, setcaseresult);
            } else {
                // 解析用例或是调用方法出错，全部把用例置为锁定
                LogUtil.APP.warn("用例：" + testcase.getCaseSign() + "设置执行结果为锁定，请参考错误日志查找锁定用例的原因.....");
                caselog.insertTaskCaseLog(taskid, caseId, "设置执行结果为锁定，请参考错误日志查找锁定用例的原因.....","error", "SETCASERESULT...", "");
                setcaseresult = 2;
                caselog.updateTaskCaseExecuteStatus(taskid, caseId, setcaseresult);
            }
            if (0 == setcaseresult) {
                LogUtil.APP.info("用例：" + testcase.getCaseSign() + "执行结果成功......");
                caselog.insertTaskCaseLog(taskid, caseId, "用例步骤执行全部成功......", "info", "ending", "");
                LogUtil.APP.info("*********用例【" + testcase.getCaseSign() + "】执行完成,测试结果：成功*********");
            } else if (1 == setcaseresult) {
                LogUtil.APP.warn("用例：" + testcase.getCaseSign() + "执行结果失败......");
                caselog.insertTaskCaseLog(taskid, caseId, "用例执行结果失败......", "error", "ending", "");
                LogUtil.APP.info("*********用例【" + testcase.getCaseSign() + "】执行完成,测试结果：失败*********");
            } else {
                LogUtil.APP.warn("用例：" + testcase.getCaseSign() + "执行结果锁定......");
                caselog.insertTaskCaseLog(taskid, caseId, "用例执行结果锁定......", "error", "ending", "");
                LogUtil.APP.info("*********用例【" + testcase.getCaseSign() + "】执行完成,测试结果：锁定*********");
            }
        } catch (Exception e) {
            LogUtil.APP.error("用例：" + testcase.getCaseSign() + "设置执行结果过程出错......",e);
            caselog.insertTaskCaseLog(taskid, caseId, "设置执行结果过程出错......", "error", "ending", "");
        } finally {
            variable.clear(); // 一条用例结束后，清空变量存储空间
            TestControl.THREAD_COUNT--; // 多线程计数--，用于检测线程是否全部执行完
        }
    }

    public static void main(String[] args) {
    }

}
