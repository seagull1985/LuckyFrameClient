package luckyclient.execution.httpinterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import luckyclient.driven.SubString;
import luckyclient.execution.dispose.ActionManageForSteps;
import luckyclient.execution.dispose.ParamsManageForSteps;
import luckyclient.execution.httpinterface.analyticsteps.InterfaceAnalyticCase;
import luckyclient.remote.api.serverOperation;
import luckyclient.remote.entity.ProjectCase;
import luckyclient.remote.entity.ProjectCaseParams;
import luckyclient.remote.entity.ProjectCaseSteps;
import luckyclient.utils.Constants;
import luckyclient.utils.InvokeMethod;
import luckyclient.utils.LogUtil;
import org.apache.commons.lang.StringUtils;

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
    private Integer caseId;
    private String caseSign;
    private ProjectCase testcase;
    private String taskid;
    private Integer planId;
    private Integer projectId;
    private List<ProjectCaseSteps> steps;
    private List<ProjectCaseParams> pcplist;
    private serverOperation caselog;

    public ThreadForExecuteCase(ProjectCase projectcase, List<ProjectCaseSteps> steps, String taskid,Integer planId, List<ProjectCaseParams> pcplist, serverOperation caselog) {
        this.caseId = projectcase.getCaseId();
        this.testcase = projectcase;
        this.projectId = projectcase.getProjectId();
        this.caseSign = projectcase.getCaseSign();
        this.taskid = taskid;
        this.planId=planId;
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
        String functionname;
        String packagename;
        String expectedresults;
        int setcaseresult = 0;
        Object[] getParameterValues;
        String testnote = "初始化测试结果";
        int k = 0;
        int stepJumpNo=0;
        // 进入循环，解析单个用例所有步骤
        // 插入开始执行的用例
        caselog.insertTaskCaseExecute(taskid, projectId, planId,caseId, caseSign, testcase.getCaseName(), 3);
        for (int i = 0; i < steps.size(); i++) {
            //处理步骤跳转语法
            if(stepJumpNo!=0&&setcaseresult!=0){
                if(stepJumpNo==i+1){
                    setcaseresult = 0;
                    LogUtil.APP.info("跳转至当前用例第{}步",i+1);
                }else if(stepJumpNo>i+1){
                    LogUtil.APP.info("当前用例第{}步,跳过执行...",i+1);
                    continue;
                }else{
                    LogUtil.APP.info("跳转步骤【{}】小于当前步骤【{}】，直接向下继续执行...",stepJumpNo,i+1);
                }
            }

            // 解析单个步骤中的脚本
            Map<String, String> casescript = InterfaceAnalyticCase.analyticCaseStep(testcase, steps.get(i), taskid, caselog,variable);
            try {
                packagename = casescript.get("PackageName");
                functionname = casescript.get("FunctionName");
            } catch (Exception e) {
                LogUtil.APP.error("用例:{} 解析包名或是方法名出现异常，请检查！",testcase.getCaseSign(),e);
                caselog.insertTaskCaseLog(taskid, caseId, "解析包名或是方法名失败，请检查！", "error", String.valueOf(i + 1), "");
                break; // 某一步骤失败后，此条用例置为失败退出
            }
            // 用例名称解析出现异常或是单个步骤参数解析异常
            if ((null != functionname && functionname.contains("解析异常")) || k == 1) {
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
                    caselog.insertTaskCaseLog(taskid, caseId, "解析包名：" + packagename + " 方法名：" + functionname + " 第" + (j + 1) + "个参数：" + parameterValues, "info", String.valueOf(i + 1), "");
                    getParameterValues[j] = parameterValues;
                }
            } else {
                getParameterValues = null;
            }
            // 调用动态方法，执行测试用例
            try {
                LogUtil.APP.info("用例:{}开始调用方法:{} .....",testcase.getCaseSign(),functionname);
                caselog.insertTaskCaseLog(taskid, caseId, "开始调用方法：" + functionname + " .....", "info", String.valueOf(i + 1), "");

                // 接口用例支持使用runcase关键字
                if ((null != functionname && "runcase".equals(functionname))) {
                    TestCaseExecution testCaseExecution=new TestCaseExecution();
                    testnote = testCaseExecution.oneCaseExecuteForCase(getParameterValues[0].toString(), taskid, variable, caselog, null);
                }else{
                    testnote = InvokeMethod.callCase(packagename, functionname, getParameterValues, steps.get(i).getStepType(), steps.get(i).getExtend());
                }
                testnote = ActionManageForSteps.actionManage(casescript.get("Action"), testnote);

                if (null != expectedresults && !expectedresults.isEmpty()) {
                    //处理步骤跳转
                    if (expectedresults.length() > Constants.IFFAIL_JUMP.length() && expectedresults.startsWith(Constants.IFFAIL_JUMP)) {
                        LogUtil.APP.info("预期结果中存在判断条件跳转步骤，处理前原始字符串：{}",expectedresults);
                        String expectedTemp = expectedresults.substring(Constants.IFFAIL_JUMP.length());
                        if(expectedTemp.contains(Constants.SYMLINK)){
                            expectedresults = expectedTemp.substring(expectedTemp.indexOf(Constants.SYMLINK)+2);
                            try{
                                stepJumpNo =  Integer.parseInt(expectedTemp.substring(0,expectedTemp.indexOf(Constants.SYMLINK)));
                            }catch (NumberFormatException nfe){
                                LogUtil.APP.error("步骤跳转语法解析失败，步骤编号不是数字，请确认:{}",expectedTemp.substring(0,expectedTemp.indexOf(Constants.SYMLINK)));
                            }
                        }else{
                            LogUtil.APP.warn("处理预期结果条件判断失败，请确认预期结果语法结构：【"+Constants.IFFAIL_JUMP+">>预期结果】，原始预期结果值：{}",expectedresults);
                        }
                    }
                    LogUtil.APP.info("expectedResults=【{}】",expectedresults);
                    // 赋值传参
                    if (expectedresults.length() > Constants.ASSIGNMENT_SIGN.length() && expectedresults.startsWith(Constants.ASSIGNMENT_SIGN)) {
                        variable.put(expectedresults.substring(Constants.ASSIGNMENT_SIGN.length()), testnote);
                        LogUtil.APP.info("用例:{} 第{}步，将测试结果【{}】赋值给变量【{}】",testcase.getCaseSign(),(i+1),testnote,expectedresults.substring(Constants.ASSIGNMENT_SIGN.length()));
                        caselog.insertTaskCaseLog(taskid, caseId, "将测试结果【" + testnote + "】赋值给变量【" + expectedresults.substring(Constants.ASSIGNMENT_SIGN.length()) + "】", "info", String.valueOf(i + 1), "");
                    }
                    // 赋值全局变量
                    else if (expectedresults.length() > Constants.ASSIGNMENT_GLOBALSIGN.length() && expectedresults.startsWith(Constants.ASSIGNMENT_GLOBALSIGN)) {
                        variable.put(expectedresults.substring(Constants.ASSIGNMENT_GLOBALSIGN.length()), testnote);
                        ParamsManageForSteps.GLOBAL_VARIABLE.put(expectedresults.substring(Constants.ASSIGNMENT_GLOBALSIGN.length()), testnote);
                        LogUtil.APP.info("用例:{} 第{}步，将测试结果【{}】赋值给全局变量【{}】",testcase.getCaseSign(),(i+1),testnote,expectedresults.substring(Constants.ASSIGNMENT_GLOBALSIGN.length()));
                        caselog.insertTaskCaseLog(taskid, caseId, "将测试结果【" + testnote + "】赋值给全局变量【" + expectedresults.substring(Constants.ASSIGNMENT_GLOBALSIGN.length()) + "】", "info", String.valueOf(i + 1), "");
                    }
                    // 模糊匹配
                    else if (expectedresults.length() > Constants.FUZZY_MATCHING_SIGN.length() && expectedresults.startsWith(Constants.FUZZY_MATCHING_SIGN)) {
                        if (testnote.contains(expectedresults.substring(Constants.FUZZY_MATCHING_SIGN.length()))) {
                            LogUtil.APP.info("用例:{} 第{}步，模糊匹配预期结果成功！执行结果:{}",testcase.getCaseSign(),(i+1),testnote);
                            caselog.insertTaskCaseLog(taskid, caseId, "模糊匹配预期结果成功！执行结果：" + testnote, "info", String.valueOf(i + 1), "");
                        } else {
                            setcaseresult = 1;
                            LogUtil.APP.warn("用例:{} 第{}步，模糊匹配预期结果失败！预期结果:{}，测试结果:{}",testcase.getCaseSign(),(i+1),expectedresults.substring(Constants.FUZZY_MATCHING_SIGN.length()),testnote);
                            caselog.insertTaskCaseLog(taskid, caseId, "第" + (i + 1) + "步，模糊匹配预期结果失败！预期结果：" + expectedresults.substring(Constants.FUZZY_MATCHING_SIGN.length()) + "，测试结果：" + testnote, "error", String.valueOf(i + 1), "");
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
                    else if (expectedresults.length() > Constants.REGULAR_MATCHING_SIGN.length() && expectedresults.startsWith(Constants.REGULAR_MATCHING_SIGN)) {
                        Pattern pattern = Pattern.compile(expectedresults.substring(Constants.REGULAR_MATCHING_SIGN.length()));
                        Matcher matcher = pattern.matcher(testnote);
                        if (matcher.find()) {
                            LogUtil.APP.info("用例:{} 第{}步，正则匹配预期结果成功！执行结果:{}",testcase.getCaseSign(),(i+1),testnote);
                            caselog.insertTaskCaseLog(taskid, caseId, "正则匹配预期结果成功！执行结果：" + testnote, "info", String.valueOf(i + 1), "");
                        } else {
                            setcaseresult = 1;
                            LogUtil.APP.warn("用例:{} 第{}步，正则匹配预期结果失败！预期结果:{}，测试结果:{}",testcase.getCaseSign(),(i+1),expectedresults.substring(Constants.REGULAR_MATCHING_SIGN.length()),testnote);
                            caselog.insertTaskCaseLog(taskid, caseId, "第" + (i + 1) + "步，正则匹配预期结果失败！预期结果：" + expectedresults.substring(Constants.REGULAR_MATCHING_SIGN.length()) + "，测试结果：" + testnote, "error", String.valueOf(i + 1), "");
                            testnote = "用例第" + (i + 1) + "步，正则匹配预期结果失败！";
                            if (testcase.getFailcontinue() == 0) {
                                LogUtil.APP.warn("用例【{}】第【{}】步骤执行失败，中断本条用例后续步骤执行，进入到下一条用例执行中......",testcase.getCaseSign(),(i+1));
                                break;
                            } else {
                                LogUtil.APP.warn("用例【{}】第【{}】步骤执行失败，继续本条用例后续步骤执行，进入下个步骤执行中......",testcase.getCaseSign(),(i+1));
                            }
                        }
                    }
                    //jsonpath断言
                    else if (expectedresults.length() > Constants.JSONPATH_SIGN.length() && expectedresults.startsWith(Constants.JSONPATH_SIGN)) {
                        expectedresults = expectedresults.substring(Constants.JSONPATH_SIGN.length());
                        String expression = expectedresults.split("(?<!\\\\)=")[0].replace("\\=","=");
                        String exceptResult = expectedresults.split("(?<!\\\\)=")[1].replace("\\=","=");
                        //对测试结果进行jsonPath取值
                        String result = SubString.jsonPathGetParams(expression, testnote);
                        
                        if (exceptResult.equals(result)) {
                            setcaseresult = 0;
                            LogUtil.APP.info("用例【{}】 第【{}】步，jsonpath断言预期结果成功！预期结果:{} 测试结果: {} 执行结果:true",testcase.getCaseSign(),(i+1),exceptResult,result);
                            caselog.insertTaskCaseLog(taskid, caseId, "jsonpath断言预期结果成功！预期结果:"+ expectedresults + "测试结果:" + result + "执行结果:true","info", String.valueOf(i + 1), "");
                        } else {
                            setcaseresult = 1;
                            LogUtil.APP.warn("用例:{} 第{}步，jsonpath断言预期结果失败！预期结果:{}，测试结果:{}",testcase.getCaseSign(),(i+1),expectedresults,result);
                            caselog.insertTaskCaseLog(taskid, caseId, "第" + (i + 1) + "步，正则匹配预期结果失败！预期结果：" + exceptResult + "，测试结果：" + result, "error", String.valueOf(i + 1), "");
                            testnote = "用例第" + (i + 1) + "步，jsonpath断言预期结果失败！";
                            if (testcase.getFailcontinue() == 0) {
                                LogUtil.APP.warn("用例【{}】第【{}】步骤执行失败，中断本条用例后续步骤执行，进入到下一条用例执行中......",testcase.getCaseSign(),(i+1));
                                break;
                            } else {
                                LogUtil.APP.warn("用例【{}】第【{}】步骤执行失败，继续本条用例后续步骤执行，进入下个步骤执行中......",testcase.getCaseSign(),(i+1));
                            }

                            // 某一步骤失败后，此条用例置为失败退出
                            break;
                        }
                    }
                    // 完全相等
                    else {
                        if (expectedresults.equals(testnote)) {
                            LogUtil.APP.info("用例:{} 第{}步，精确匹配预期结果成功！执行结果:{}",testcase.getCaseSign(),(i+1),testnote);
                            caselog.insertTaskCaseLog(taskid, caseId, "精确匹配预期结果成功！执行结果：" + testnote, "info", String.valueOf(i + 1), "");
                        } else if(expectedresults.trim().equals("NULL")&&StringUtils.isBlank(testnote)){
                            testnote = "返回结果为空，匹配NULL成功";
                            LogUtil.APP.info("用例:{} 第{}步，精确匹配预期结果成功！执行结果:{}",testcase.getCaseSign(),(i+1),testnote);
                            caselog.insertTaskCaseLog(taskid, caseId, "精确匹配预期结果成功！执行结果：" + testnote, "info", String.valueOf(i + 1), "");
                        } else {
                            setcaseresult = 1;
                            LogUtil.APP.warn("用例:{} 第{}步，精确匹配预期结果失败！预期结果:{}，测试结果:{}",testcase.getCaseSign(),(i+1),expectedresults,testnote);
                            caselog.insertTaskCaseLog(taskid, caseId, "第" + (i + 1) + "步，精确匹配预期结果失败！预期结果：" + expectedresults + "，测试结果：" + testnote, "error", String.valueOf(i + 1), "");
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
                LogUtil.APP.error("用例:{}调用方法过程出错，方法名:{} 请重新检查脚本方法名称以及参数！",testcase.getCaseSign(),functionname,e);
                caselog.insertTaskCaseLog(taskid, caseId, "调用方法过程出错，方法名：" + functionname + " 请重新检查脚本方法名称以及参数！", "error", String.valueOf(i + 1), "");
                testnote = "CallCase调用出错！调用方法过程出错，方法名：" + functionname + " 请重新检查脚本方法名称以及参数！";
                setcaseresult = 1;
                if (testcase.getFailcontinue() == 0) {
                    LogUtil.APP.error("用例【{}】第【{}】步骤执行失败，中断本条用例后续步骤执行，进入到下一条用例执行中......",testcase.getCaseSign(),(i+1));
                    break;
                } else {
                    LogUtil.APP.error("用例【{}】第【{}】步骤执行失败，继续本条用例后续步骤执行，进入下个步骤执行中......",testcase.getCaseSign(),(i+1));
                }
            }
        }
        // 如果调用方法过程中未出错，进入设置测试结果流程
        try {
            // 成功跟失败的用例走此流程
            if (testnote.contains("CallCase调用出错！") || testnote.contains("解析出错啦！")) {
                // 解析用例或是调用方法出错，全部把用例置为锁定
                LogUtil.APP.warn("用例:{} 设置执行结果为锁定，请参考错误日志查找锁定用例的原因.....", testcase.getCaseSign());
                caselog.insertTaskCaseLog(taskid, caseId, "设置执行结果为锁定，请参考错误日志查找锁定用例的原因.....", "error", "SETCASERESULT...", "");
                setcaseresult = 2;
            }
            caselog.updateTaskCaseExecuteStatus(taskid, planId,caseId, setcaseresult);
            if (0 == setcaseresult) {
                LogUtil.APP.info("用例:{}执行结果成功......",testcase.getCaseSign());
                caselog.insertTaskCaseLog(taskid, caseId, "用例步骤执行全部成功......", "info", "ending", "");
                LogUtil.APP.info("*********用例【{}】执行完成,测试结果：成功*********",testcase.getCaseSign());
            } else if (1 == setcaseresult) {
                LogUtil.APP.warn("用例:{}执行结果失败......",testcase.getCaseSign());
                caselog.insertTaskCaseLog(taskid, caseId, "用例执行结果失败......", "error", "ending", "");
                LogUtil.APP.warn("*********用例【{}】执行完成,测试结果：失败*********",testcase.getCaseSign());
            } else {
                LogUtil.APP.warn("用例：" + testcase.getCaseSign() + "执行结果锁定......");
                caselog.insertTaskCaseLog(taskid, caseId, "用例执行结果锁定......", "error", "ending", "");
                LogUtil.APP.warn("*********用例【{}】执行完成,测试结果：锁定*********",testcase.getCaseSign());
            }
        } catch (Exception e) {
            LogUtil.APP.error("用例:{}设置执行结果过程出错......",testcase.getCaseSign(),e);
            caselog.insertTaskCaseLog(taskid, caseId, "设置执行结果过程出错......", "error", "ending", "");
        } finally {
            variable.clear(); // 一条用例结束后，清空变量存储空间
            TestControl.THREAD_COUNT--; // 多线程计数--，用于检测线程是否全部执行完
        }
    }
}
