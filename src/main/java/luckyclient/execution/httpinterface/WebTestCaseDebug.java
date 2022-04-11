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
import luckyclient.execution.webdriver.ex.WebOneCaseExecute;
import luckyclient.remote.api.GetServerApi;
import luckyclient.remote.api.PostServerApi;
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
 * @ClassName: WebTestCaseDebug
 * @Description: 提供Web端调试接口
 * @author： seagull
 * @date 2018年3月1日
 */
public class WebTestCaseDebug {
    /**
     * 用于在WEB页面上调试用例时提供的接口
     * @param caseIdStr 用例ID
     * @param userIdStr  用户ID
     * @param caseTypeStr 用例类型：HTTP接口 0,  Web UI, 1,   API驱动, 2,   移动端, 3
     */
    public static void oneCaseDebug(String caseIdStr, String userIdStr,String caseTypeStr) {
        //修改点
        if (Integer.parseInt(caseTypeStr)==0||Integer.parseInt(caseTypeStr)==2){
        Map<String, String> variable = new HashMap<>(0);
        serverOperation.exetype=1;
        String packagename;
        String functionname;
        String expectedresults;
        int setcaseresult = 0;
        int stepJumpNo=0;
        Object[] getParameterValues;
        String testnote = "初始化测试结果";
        int k = 0;
        Integer caseId = Integer.valueOf(caseIdStr);
        Integer userId = Integer.valueOf(userIdStr);
        ProjectCase testcase = GetServerApi.cGetCaseByCaseId(caseId);
        // 初始化写用例结果以及日志模块
        serverOperation caselog = new serverOperation();

        String sign = testcase.getCaseSign();
        List<ProjectCaseParams> pcplist = GetServerApi.cgetParamsByProjectid(String.valueOf(testcase.getProjectId()));
        // 把公共参数加入到MAP中
        for (ProjectCaseParams pcp : pcplist) {
            variable.put(pcp.getParamsName(), pcp.getParamsValue());
        }
        List<ProjectCaseSteps> steps = GetServerApi.getStepsbycaseid(testcase.getCaseId());
        //进入循环，解析用例所有步骤
        for (int i = 0; i < steps.size(); i++) {
            //处理步骤跳转语法
            if(stepJumpNo!=0&&setcaseresult!=0){
                if(stepJumpNo==i+1){
                    setcaseresult = 0;
                    PostServerApi.cPostDebugLog(userId, caseId, "INFO", "跳转至当前用例第"+(i+1)+"步",0);
                    LogUtil.APP.info("跳转至当前用例第{}步",i+1);
                }else if(stepJumpNo>i+1){
                    PostServerApi.cPostDebugLog(userId, caseId, "INFO", "当前用例第"+(i+1)+"步,跳过执行...",0);
                    LogUtil.APP.info("当前用例第{}步,跳过执行...",i+1);
                    continue;
                }else{
                    PostServerApi.cPostDebugLog(userId, caseId, "INFO", "跳转步骤【"+stepJumpNo+"】小于当前步骤【"+(i+1)+"】，直接向下继续执行...",0);
                    LogUtil.APP.info("跳转步骤【{}】小于当前步骤【{}】，直接向下继续执行...",stepJumpNo,(i+1));
                }
            }

            Map<String, String> casescript = InterfaceAnalyticCase.analyticCaseStep(testcase, steps.get(i), "888888", null,variable);
            try {
                packagename = casescript.get("PackageName");
                functionname = casescript.get("FunctionName");
            } catch (Exception e) {
                LogUtil.APP.error("解析包名或是方法名出现异常！",e);
                PostServerApi.cPostDebugLog(userId, caseId, "ERROR", "解析包名或是方法名失败，请检查！",2);
                break;        //某一步骤失败后，此条用例置为失败退出
            }
            //用例名称解析出现异常或是单个步骤参数解析异常
            if ((null != functionname && functionname.contains("解析异常")) || k == 1) {
                testnote = "用例第" + (i + 1) + "步解析出错啦！";
                break;
            }
            expectedresults = casescript.get("ExpectedResults");
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
                    PostServerApi.cPostDebugLog(userId, caseId, "INFO", "解析包名：" + packagename + " 方法名：" + functionname + " 第" + (j + 1) + "个参数：" + parameterValues, 0);
                    getParameterValues[j] = parameterValues;
                }
            } else {
                getParameterValues = null;
            }
            //调用动态方法，执行测试用例
            try {
                PostServerApi.cPostDebugLog(userId, caseId, "INFO", "开始调用方法：" + functionname + " .....",0);

                // 接口用例支持使用runcase关键字
                if ((null != functionname && "runcase".equals(functionname))) {
                    TestCaseExecution testCaseExecution=new TestCaseExecution();
                    testnote = testCaseExecution.oneCaseExecuteForCase(getParameterValues[0].toString(), "888888", variable, caselog, null);
                }else{
                    testnote = InvokeMethod.callCase(packagename, functionname, getParameterValues, steps.get(i).getStepType(), steps.get(i).getExtend());
                }
                testnote = ActionManageForSteps.actionManage(casescript.get("Action"), testnote);

                if (null != expectedresults && !expectedresults.isEmpty()) {
                    //处理步骤跳转
                    if (expectedresults.length() > Constants.IFFAIL_JUMP.length() && expectedresults.startsWith(Constants.IFFAIL_JUMP)) {
                        PostServerApi.cPostDebugLog(userId, caseId, "INFO", "预期结果中存在判断条件跳转步骤，处理前原始字符串："+expectedresults,0);
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
                            PostServerApi.cPostDebugLog(userId, caseId, "INFO", "处理预期结果条件判断失败，请确认预期结果语法结构：【"+Constants.IFFAIL_JUMP+">>预期结果】，原始预期结果值："+expectedresults,0);
                            LogUtil.APP.warn("处理预期结果条件判断失败，请确认预期结果语法结构：【"+Constants.IFFAIL_JUMP+">>预期结果】，原始预期结果值：{}",expectedresults);
                        }
                    }
                    // 赋值传参
                    if (expectedresults.length() > Constants.ASSIGNMENT_SIGN.length() && expectedresults.startsWith(Constants.ASSIGNMENT_SIGN)) {
                        variable.put(expectedresults.substring(Constants.ASSIGNMENT_SIGN.length()), testnote);
                        PostServerApi.cPostDebugLog(userId, caseId, "INFO", "将测试结果【" + testnote + "】赋值给变量【" + expectedresults.substring(Constants.ASSIGNMENT_SIGN.length()) + "】",0);
                    }
                    // 赋值全局变量
                    else if (expectedresults.length() > Constants.ASSIGNMENT_GLOBALSIGN.length() && expectedresults.startsWith(Constants.ASSIGNMENT_GLOBALSIGN)) {
                        variable.put(expectedresults.substring(Constants.ASSIGNMENT_GLOBALSIGN.length()), testnote);
                        ParamsManageForSteps.GLOBAL_VARIABLE.put(expectedresults.substring(Constants.ASSIGNMENT_GLOBALSIGN.length()), testnote);
                        LogUtil.APP.info("用例:{} 第{}步，将测试结果【{}】赋值给全局变量【{}】",testcase.getCaseSign(),(i+1),testnote,expectedresults.substring(Constants.ASSIGNMENT_GLOBALSIGN.length()));
                        PostServerApi.cPostDebugLog(userId, caseId, "INFO", "将测试结果【" + testnote + "】赋值给全局变量【" + expectedresults.substring(Constants.ASSIGNMENT_GLOBALSIGN.length()) + "】",0);
                    }
                    // 模糊匹配
                    else if (expectedresults.length() > Constants.FUZZY_MATCHING_SIGN.length() && expectedresults.startsWith(Constants.FUZZY_MATCHING_SIGN)) {
                        if (testnote.contains(expectedresults.substring(Constants.FUZZY_MATCHING_SIGN.length()))) {
                            PostServerApi.cPostDebugLog(userId, caseId, "INFO", "模糊匹配预期结果成功！执行结果：" + testnote,0);
                        } else {
                            setcaseresult = 1;
                            PostServerApi.cPostDebugLog(userId, caseId, "ERROR", "第" + (i + 1) + "步，模糊匹配预期结果失败！预期结果：" + expectedresults.substring(Constants.FUZZY_MATCHING_SIGN.length()) + "，测试结果：" + testnote,0);
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
                            PostServerApi.cPostDebugLog(userId, caseId, "INFO", "正则匹配预期结果成功！执行结果：" + testnote,0);
                        } else {
                            setcaseresult = 1;
                            PostServerApi.cPostDebugLog(userId, caseId, "ERROR", "第" + (i + 1) + "步，正则匹配预期结果失败！预期结果：" + expectedresults.substring(Constants.REGULAR_MATCHING_SIGN.length()) + "，测试结果：" + testnote,0);
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
                            PostServerApi.cPostDebugLog(userId, caseId, "INFO", "jsonpath断言预期结果成功！预期结果：" + exceptResult + " 测试结果: " + result + "校验结果: true", 0);
                        } else {
                            setcaseresult = 1;
                            PostServerApi.cPostDebugLog(userId, caseId, "ERROR", "第" + (i + 1) + "步，jsonpath断言预期结果失败！预期结果：" + exceptResult + "，测试结果：" + result,0);
                            testnote = "用例第" + (i + 1) + "步，jsonpath断言预期结果失败！";
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
                            PostServerApi.cPostDebugLog(userId, caseId, "INFO", "精确匹配预期结果成功！执行结果：" + testnote,0);
                        } else if(expectedresults.trim().equals("NULL")&& StringUtils.isBlank(testnote)){
                            testnote = "返回结果为空，匹配NULL成功";
                            PostServerApi.cPostDebugLog(userId, caseId, "INFO", "精确匹配预期结果成功！执行结果：" + testnote,0);
                        } else {
                            setcaseresult = 1;
                            PostServerApi.cPostDebugLog(userId, caseId, "ERROR", "第" + (i + 1) + "步，精确匹配预期结果失败！预期结果：" + expectedresults + "，测试结果：" + testnote,0);
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
                LogUtil.APP.error("用例执行出现异常！",e);
                PostServerApi.cPostDebugLog(userId, caseId, "ERROR", "调用方法过程出错，方法名：" + functionname + " 请重新检查脚本方法名称以及参数！",0);
                testnote = "CallCase调用出错！";
                if (testcase.getFailcontinue() == 0) {
                    LogUtil.APP.error("用例【{}】第【{}】步骤执行失败，中断本条用例后续步骤执行，进入到下一条用例执行中......",testcase.getCaseSign(),(i+1));
                    break;
                } else {
                    LogUtil.APP.error("用例【{}】第【{}】步骤执行失败，继续本条用例后续步骤执行，进入下个步骤执行中......",testcase.getCaseSign(),(i+1));
                }
            }
        }
        variable.clear();               //清空传参MAP
        //如果调用方法过程中未出错，进入设置测试结果流程
        if (testnote.contains("CallCase调用出错！") && testnote.contains("解析出错啦！")) {
            PostServerApi.cPostDebugLog(userId, caseId, "ERRORover", "用例 " + sign + "解析或是调用步骤中的方法出错！",1);
        }
        if (0 == setcaseresult) {
            PostServerApi.cPostDebugLog(userId, caseId, "INFOover", "用例 " + sign + "步骤全部执行完成！",1);
        } else {
            PostServerApi.cPostDebugLog(userId, caseId, "ERRORover", "用例 " + sign + "在执行过程中失败，请检查！",1);
        }
    }
    }

    //修改点
    /**
     * 用于在WEB页面上调试用例时提供的接口
     * @param caseIdStr 用例ID
     * @param userIdStr  用户ID
     * @param caseTypeStr 用例类型：针对1
     * @param browserTypeStr web驱动：0 IE 1 火狐 2 谷歌 3 Edge
     */
    public static void oneCaseDebug(String caseIdStr, String userIdStr,String caseTypeStr,String browserTypeStr) {
        int caseId=Integer.parseInt(caseIdStr);
        int browserType=Integer.parseInt(browserTypeStr);
        WebOneCaseExecute.debugoneCaseExecute(caseId, browserType);
    }
}