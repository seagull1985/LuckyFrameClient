package luckyclient.caserun.exinterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * @date 2018年3月1日
 */
public class ThreadForExecuteCase extends Thread {
    private static final String ASSIGNMENT_SIGN = "$=";
    private static final String FUZZY_MATCHING_SIGN = "%=";
    private static final String REGULAR_MATCHING_SIGN = "~=";

    private String caseid;
    private ProjectCase testcaseob;
    private String taskid;
    private List<ProjectCasesteps> steps;
    private List<PublicCaseParams> pcplist;
    private LogOperation caselog;

    public ThreadForExecuteCase(ProjectCase projectcase, List<ProjectCasesteps> steps, String taskid, List<PublicCaseParams> pcplist, LogOperation caselog) {
        this.caseid = projectcase.getSign();
        this.testcaseob = projectcase;
        this.taskid = taskid;
        this.steps = steps;
        this.pcplist = pcplist;
        this.caselog = caselog;
    }

    @Override
    public void run() {
        Map<String, String> variable = new HashMap<>(0);
        // 把公共参数加入到MAP中
        for (PublicCaseParams pcp : pcplist) {
            variable.put(pcp.getParamsname(), pcp.getParamsvalue());
        }
        String functionname = null;
        String packagename = null;
        String expectedresults = null;
        Integer setresult = 1;
        Object[] getParameterValues = null;
        String testnote = "初始化测试结果";
        int k = 0;
        // 进入循环，解析单个用例所有步骤
        // 插入开始执行的用例
        caselog.addCaseDetail(taskid, caseid, "1", testcaseob.getName(), 4);
        for (int i = 0; i < steps.size(); i++) {
            // 解析单个步骤中的脚本
            Map<String, String> casescript = InterfaceAnalyticCase.analyticCaseStep(testcaseob, steps.get(i), taskid, caselog);
            try {
                packagename = casescript.get("PackageName");
                packagename = ChangString.changparams(packagename, variable, "包路径");
                functionname = casescript.get("FunctionName");
                functionname = ChangString.changparams(functionname, variable, "方法名");
            } catch (Exception e) {
                k = 0;
                luckyclient.publicclass.LogUtil.APP.error("用例：" + testcaseob.getSign() + "解析包名或是方法名失败，请检查！");
                caselog.caseLogDetail(taskid, caseid, "解析包名或是方法名失败，请检查！", "error", String.valueOf(i + 1), "");
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
                    luckyclient.publicclass.LogUtil.APP.info("用例：" + testcaseob.getSign() + "解析包名：" + packagename + " 方法名：" + functionname + " 第" + (j + 1) + "个参数：" + parameterValues);
                    caselog.caseLogDetail(taskid, caseid, "解析包名：" + packagename + " 方法名：" + functionname + " 第" + (j + 1) + "个参数：" + parameterValues, "info", String.valueOf(i + 1), "");
                    getParameterValues[j] = parameterValues;
                }
            } else {
                getParameterValues = null;
            }
            // 调用动态方法，执行测试用例
            try {
                luckyclient.publicclass.LogUtil.APP.info("用例：" + testcaseob.getSign() + "开始调用方法：" + functionname + " .....");
                caselog.caseLogDetail(taskid, caseid, "开始调用方法：" + functionname + " .....", "info", String.valueOf(i + 1), "");

                testnote = InvokeMethod.callCase(packagename, functionname, getParameterValues, steps.get(i).getSteptype(), steps.get(i).getAction());

                if (null != expectedresults && !expectedresults.isEmpty()) {
                    luckyclient.publicclass.LogUtil.APP.info("expectedResults=【" + expectedresults + "】");
                    // 赋值传参
                    if (expectedresults.length() > ASSIGNMENT_SIGN.length() && expectedresults.startsWith(ASSIGNMENT_SIGN)) {
                        variable.put(expectedresults.substring(ASSIGNMENT_SIGN.length()), testnote);
                        luckyclient.publicclass.LogUtil.APP.info("用例：" + testcaseob.getSign() + " 第" + (i + 1) + "步，将测试结果【" + testnote + "】赋值给变量【" + expectedresults.substring(ASSIGNMENT_SIGN.length()) + "】");
                        caselog.caseLogDetail(taskid, caseid, "将测试结果【" + testnote + "】赋值给变量【" + expectedresults.substring(ASSIGNMENT_SIGN.length()) + "】", "info", String.valueOf(i + 1), "");
                    }
                    // 模糊匹配
                    else if (expectedresults.length() > FUZZY_MATCHING_SIGN.length() && expectedresults.startsWith(FUZZY_MATCHING_SIGN)) {
                        if (testnote.contains(expectedresults.substring(FUZZY_MATCHING_SIGN.length()))) {
                            setresult = 0;
                            luckyclient.publicclass.LogUtil.APP.info("用例：" + testcaseob.getSign() + " 第" + (i + 1) + "步，模糊匹配预期结果成功！执行结果：" + testnote);
                            caselog.caseLogDetail(taskid, caseid, "模糊匹配预期结果成功！执行结果：" + testnote, "info", String.valueOf(i + 1), "");
                        } else {
                            setresult = 1;
                            luckyclient.publicclass.LogUtil.APP.error("用例：" + testcaseob.getSign() + " 第" + (i + 1) + "步，模糊匹配预期结果失败！预期结果：" + expectedresults.substring(FUZZY_MATCHING_SIGN.length()) + "，测试结果：" + testnote);
                            caselog.caseLogDetail(taskid, caseid, "第" + (i + 1) + "步，模糊匹配预期结果失败！预期结果：" + expectedresults.substring(FUZZY_MATCHING_SIGN.length()) + "，测试结果：" + testnote, "error", String.valueOf(i + 1), "");
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
                            luckyclient.publicclass.LogUtil.APP.info("用例：" + testcaseob.getSign() + " 第" + (i + 1) + "步，正则匹配预期结果成功！执行结果：" + testnote);
                            caselog.caseLogDetail(taskid, caseid, "正则匹配预期结果成功！执行结果：" + testnote, "info", String.valueOf(i + 1), "");
                        } else {
                            setresult = 1;
                            luckyclient.publicclass.LogUtil.APP.error("用例：" + testcaseob.getSign() + " 第" + (i + 1) + "步，正则匹配预期结果失败！预期结果：" + expectedresults.substring(REGULAR_MATCHING_SIGN.length()) + "，测试结果：" + testnote);
                            caselog.caseLogDetail(taskid, caseid, "第" + (i + 1) + "步，正则匹配预期结果失败！预期结果：" + expectedresults.substring(REGULAR_MATCHING_SIGN.length()) + "，测试结果：" + testnote, "error", String.valueOf(i + 1), "");
                            testnote = "用例第" + (i + 1) + "步，正则匹配预期结果失败！";
                            break; // 某一步骤失败后，此条用例置为失败退出
                        }
                    }
                    // 完全相等
                    else {
                        if (expectedresults.equals(testnote)) {
                            setresult = 0;
                            luckyclient.publicclass.LogUtil.APP.info("用例：" + testcaseob.getSign() + " 第" + (i + 1) + "步，精确匹配预期结果成功！执行结果：" + testnote);
                            caselog.caseLogDetail(taskid, caseid, "精确匹配预期结果成功！执行结果：" + testnote, "info", String.valueOf(i + 1), "");
                        } else {
                            setresult = 1;
                            luckyclient.publicclass.LogUtil.APP.error("用例：" + testcaseob.getSign() + " 第" + (i + 1) + "步，精确匹配预期结果失败！预期结果：" + expectedresults + "，测试结果：" + testnote);
                            caselog.caseLogDetail(taskid, caseid, "第" + (i + 1) + "步，精确匹配预期结果失败！预期结果：" + expectedresults + "，测试结果：" + testnote, "error", String.valueOf(i + 1), "");
                            testnote = "用例第" + (i + 1) + "步，精确匹配预期结果失败！";
                            break; // 某一步骤失败后，此条用例置为失败退出
                        }
                    }
                }

                // 获取步骤间等待时间
                int waitsec = Integer.parseInt(casescript.get("StepWait"));
                if (waitsec > 0) {
                    Thread.sleep(waitsec * 1000);
                }
            } catch (Exception e) {
                luckyclient.publicclass.LogUtil.ERROR.error("用例：" + testcaseob.getSign() + "调用方法过程出错，方法名：" + functionname + " 请重新检查脚本方法名称以及参数！");
                caselog.caseLogDetail(taskid, caseid, "调用方法过程出错，方法名：" + functionname + " 请重新检查脚本方法名称以及参数！", "error", String.valueOf(i + 1), "");
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
            if (!testnote.contains("CallCase调用出错！") && !testnote.contains("解析出错啦！")) {
                caselog.updateCaseDetail(taskid, caseid, setresult);
            } else {
                // 解析用例或是调用方法出错，全部把用例置为锁定
                luckyclient.publicclass.LogUtil.ERROR.error("用例：" + testcaseob.getSign() + "设置执行结果为锁定，请参考错误日志查找锁定用例的原因.....");
                caselog.caseLogDetail(taskid, caseid, "设置执行结果为锁定，请参考错误日志查找锁定用例的原因.....","error", "SETCASERESULT...", "");
                setresult = 2;
                caselog.updateCaseDetail(taskid, caseid, setresult);
            }
            if (0 == setresult) {
                luckyclient.publicclass.LogUtil.APP.info("用例：" + testcaseob.getSign() + "执行结果成功......");
                caselog.caseLogDetail(taskid, caseid, "用例步骤执行全部成功......", "info", "ending", "");
                luckyclient.publicclass.LogUtil.APP.info("*********用例【" + testcaseob.getSign() + "】执行完成,测试结果：成功*********");
            } else if (1 == setresult) {
                luckyclient.publicclass.LogUtil.ERROR.error("用例：" + testcaseob.getSign() + "执行结果失败......");
                caselog.caseLogDetail(taskid, caseid, "用例执行结果失败......", "error", "ending", "");
                luckyclient.publicclass.LogUtil.APP.info("*********用例【" + testcaseob.getSign() + "】执行完成,测试结果：失败*********");
            } else {
                luckyclient.publicclass.LogUtil.ERROR.error("用例：" + testcaseob.getSign() + "执行结果锁定......");
                caselog.caseLogDetail(taskid, caseid, "用例执行结果锁定......", "error", "ending", "");
                luckyclient.publicclass.LogUtil.APP.info("*********用例【" + testcaseob.getSign() + "】执行完成,测试结果：锁定*********");
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
