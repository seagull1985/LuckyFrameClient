package luckyclient.caserun.exinterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import luckyclient.caserun.exinterface.analyticsteps.InterfaceAnalyticCase;
import luckyclient.caserun.publicdispose.ActionManageForSteps;
import luckyclient.caserun.publicdispose.ChangString;
import luckyclient.publicclass.InvokeMethod;
import luckyclient.publicclass.LogUtil;
import luckyclient.serverapi.api.GetServerAPI;
import luckyclient.serverapi.api.PostServerAPI;
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
 * @ClassName: WebTestCaseDebug
 * @Description: 提供Web端调试接口
 * @author： seagull
 * @date 2018年3月1日
 */
public class WebTestCaseDebug {
	private static final String ASSIGNMENT_SIGN = "$=";
	private static final String FUZZY_MATCHING_SIGN = "%=";
	private static final String REGULAR_MATCHING_SIGN = "~=";
	
    /**
     * @param executor
     * @param sign 用于在WEB页面上调试用例时提供的接口
     */
    public static void oneCaseDebug(String caseIdStr, String userIdStr) {
        Map<String, String> variable = new HashMap<>(0);
        String packagename = null;
        String functionname = null;
        String expectedresults = null;
        Integer setcaseresult = 0;
        Object[] getParameterValues = null;
        String testnote = "初始化测试结果";
        int k = 0;
        Integer caseId = Integer.valueOf(caseIdStr);
        Integer userId = Integer.valueOf(userIdStr);
        ProjectCase testcase = GetServerAPI.cGetCaseByCaseId(caseId);

        String sign = testcase.getCaseSign();
        List<ProjectCaseParams> pcplist = GetServerAPI.cgetParamsByProjectid(String.valueOf(testcase.getProjectId()));
        // 把公共参数加入到MAP中
        for (ProjectCaseParams pcp : pcplist) {
            variable.put(pcp.getParamsName(), pcp.getParamsValue());
        }
        List<ProjectCaseSteps> steps = GetServerAPI.getStepsbycaseid(testcase.getCaseId());
        //进入循环，解析用例所有步骤
        for (int i = 0; i < steps.size(); i++) {
            Map<String, String> casescript = InterfaceAnalyticCase.analyticCaseStep(testcase, steps.get(i), "888888", null);
            try {
                packagename = casescript.get("PackageName");
                packagename = ChangString.changparams(packagename, variable, "包路径");
                functionname = casescript.get("FunctionName");
                functionname = ChangString.changparams(functionname, variable, "方法名");
            } catch (Exception e) {
                k = 0;
                PostServerAPI.cPostDebugLog(userId, caseId, "ERROR", "解析包名或是方法名失败，请检查！",2);
                e.printStackTrace();
                break;        //某一步骤失败后，此条用例置为失败退出
            }
            //用例名称解析出现异常或是单个步骤参数解析异常
            if ((null != functionname && functionname.contains("解析异常")) || k == 1) {
                k = 0;
                testnote = "用例第" + (i + 1) + "步解析出错啦！";
                break;
            }
            expectedresults = casescript.get("ExpectedResults");
            expectedresults = ChangString.changparams(expectedresults, variable, "预期结果");
            //判断方法是否带参数
            if (casescript.size() > 4) {
                //获取传入参数，放入对象中
                getParameterValues = new Object[casescript.size() - 4];
                for (int j = 0; j < casescript.size() - 4; j++) {
                    if (casescript.get("FunctionParams" + (j + 1)) == null) {
                        k = 1;
                        break;
                    }

                    String parameterValues = casescript.get("FunctionParams" + (j + 1));
                    parameterValues = ChangString.changparams(parameterValues, variable, "用例参数");
                    PostServerAPI.cPostDebugLog(userId, caseId, "INFO", "解析包名：" + packagename + " 方法名：" + functionname + " 第" + (j + 1) + "个参数：" + parameterValues, 0);
                    getParameterValues[j] = parameterValues;
                }
            } else {
                getParameterValues = null;
            }
            //调用动态方法，执行测试用例
            try {
                PostServerAPI.cPostDebugLog(userId, caseId, "INFO", "开始调用方法：" + functionname + " .....",0);

                testnote = InvokeMethod.callCase(packagename, functionname, getParameterValues, steps.get(i).getStepType(), steps.get(i).getExtend());
                testnote = ActionManageForSteps.actionManage(casescript.get("Action"), testnote);
                if (null != expectedresults && !expectedresults.isEmpty()) {
                    // 赋值传参
                    if (expectedresults.length() > ASSIGNMENT_SIGN.length() && expectedresults.startsWith(ASSIGNMENT_SIGN)) {
                        variable.put(expectedresults.substring(ASSIGNMENT_SIGN.length()), testnote);
                        PostServerAPI.cPostDebugLog(userId, caseId, "INFO", "将测试结果【" + testnote + "】赋值给变量【" + expectedresults.substring(ASSIGNMENT_SIGN.length()) + "】",0);
                    }
                    // 模糊匹配
                    else if (expectedresults.length() > FUZZY_MATCHING_SIGN.length() && expectedresults.startsWith(FUZZY_MATCHING_SIGN)) {
                        if (testnote.contains(expectedresults.substring(FUZZY_MATCHING_SIGN.length()))) {
                            PostServerAPI.cPostDebugLog(userId, caseId, "INFO", "模糊匹配预期结果成功！执行结果：" + testnote,0);
                        } else {
                            setcaseresult = 1;
                            PostServerAPI.cPostDebugLog(userId, caseId, "ERROR", "第" + (i + 1) + "步，模糊匹配预期结果失败！预期结果：" + expectedresults.substring(FUZZY_MATCHING_SIGN.length()) + "，测试结果：" + testnote,0);
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
                            PostServerAPI.cPostDebugLog(userId, caseId, "INFO", "正则匹配预期结果成功！执行结果：" + testnote,0);
                        } else {
                            setcaseresult = 1;
                            PostServerAPI.cPostDebugLog(userId, caseId, "ERROR", "第" + (i + 1) + "步，正则匹配预期结果失败！预期结果：" + expectedresults.substring(REGULAR_MATCHING_SIGN.length()) + "，测试结果：" + testnote,0);
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
                            PostServerAPI.cPostDebugLog(userId, caseId, "INFO", "精确匹配预期结果成功！执行结果：" + testnote,0);
                        } else {
                            setcaseresult = 1;
                            PostServerAPI.cPostDebugLog(userId, caseId, "ERROR", "第" + (i + 1) + "步，精确匹配预期结果失败！预期结果：" + expectedresults + "，测试结果：" + testnote,0);
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
                setcaseresult = 1;
                PostServerAPI.cPostDebugLog(userId, caseId, "ERROR", "调用方法过程出错，方法名：" + functionname + " 请重新检查脚本方法名称以及参数！",0);
                testnote = "CallCase调用出错！";
                e.printStackTrace();
                if (testcase.getFailcontinue() == 0) {
                    LogUtil.APP.warn("用例【"+testcase.getCaseSign()+"】第【"+(i + 1)+"】步骤执行失败，中断本条用例后续步骤执行，进入到下一条用例执行中......");
                    break;
                } else {
                    LogUtil.APP.warn("用例【"+testcase.getCaseSign()+"】第【"+(i + 1)+"】步骤执行失败，继续本条用例后续步骤执行，进入下个步骤执行中......");
                }
            }
        }
        variable.clear();               //清空传参MAP
        //如果调用方法过程中未出错，进入设置测试结果流程
        if (testnote.contains("CallCase调用出错！") && testnote.contains("解析出错啦！")) {
            PostServerAPI.cPostDebugLog(userId, caseId, "ERRORover", "用例 " + sign + "解析或是调用步骤中的方法出错！",1);
        }
        if (0 == setcaseresult) {
            PostServerAPI.cPostDebugLog(userId, caseId, "INFOover", "用例 " + sign + "步骤全部执行完成！",1);
        } else {
            PostServerAPI.cPostDebugLog(userId, caseId, "ERRORover", "用例 " + sign + "在执行过程中失败，请检查！",1);
        }
    }

    public static void main(String[] args) throws Exception {
    	
    }
}
