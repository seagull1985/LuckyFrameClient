package luckyclient.caserun.exappium.androidex;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import luckyclient.caserun.exappium.AppDriverAnalyticCase;
import luckyclient.caserun.exinterface.TestCaseExecution;
import luckyclient.caserun.exinterface.analyticsteps.InterfaceAnalyticCase;
import luckyclient.dblog.LogOperation;
import luckyclient.planapi.entity.ProjectCase;
import luckyclient.planapi.entity.ProjectCasesteps;
import luckyclient.planapi.entity.PublicCaseParams;
import luckyclient.publicclass.ChangString;
import luckyclient.publicclass.LogUtil;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 * 
 * @author seagull
 * @date 2018年1月21日 上午15:12:48
 */
public class AndroidCaseExecution extends TestCaseExecution{
	static Map<String, String> variable = new HashMap<String, String>();
    private static String casenote = "备注初始化";
    private static String imagname = "";

	public static void caseExcution(ProjectCase testcase, List<ProjectCasesteps> steps,String taskid, AndroidDriver<AndroidElement> appium,LogOperation caselog,List<PublicCaseParams> pcplist)
			throws InterruptedException, IOException {
		// 把公共参数加入到MAP中
		for (PublicCaseParams pcp : pcplist) {
			variable.put(pcp.getParamsname(), pcp.getParamsvalue());
		}
		//插入开始执行的用例
		caselog.addCaseDetail(taskid, testcase.getSign(), "1", testcase.getName(), 4);       
	    // 0:成功 1:失败 2:锁定 其他：锁定
	    int setcaseresult = 0;
		for (ProjectCasesteps step : steps) {
            Map<String, String> params;
            String result;

            // 根据步骤类型来分析步骤参数
            if (4 == step.getSteptype()){
            	params = AppDriverAnalyticCase.analyticCaseStep(testcase, step, taskid,caselog);
            }else{
            	params = InterfaceAnalyticCase.analyticCaseStep(testcase, step, taskid, caselog);
            }
			
			if(params.get("exception")!=null&&params.get("exception").toString().indexOf("解析异常")>-1){
				setcaseresult = 2;
				break;
			}
			
            // 根据步骤类型来执行步骤
            if (4 == step.getSteptype()){
            	result = androidRunStep(params, variable, appium, taskid, testcase.getSign(), step.getStepnum(), caselog);
            }else{
            	result = TestCaseExecution.runStep(params, variable, taskid, testcase.getSign(), step, caselog);
            }

			String expectedResults = params.get("ExpectedResults").toString();
			expectedResults=ChangString.changparams(expectedResults, variable,"预期结果");
			
            // 判断结果
			int stepresult = judgeResult(testcase, step, params, appium, taskid, expectedResults, result, caselog);
			// 失败，并且不在继续,直接终止
            if (0 != stepresult) {
            	setcaseresult = stepresult;
                if (testcase.getFailcontinue() == 0) {
                    luckyclient.publicclass.LogUtil.APP.error("用例【"+testcase.getSign()+"】第【"+step.getStepnum()+"】步骤执行失败，中断本条用例后续步骤执行，进入到下一条用例执行中......");
                    break;
                } else {
                    luckyclient.publicclass.LogUtil.APP.error("用例【"+testcase.getSign()+"】第【"+step.getStepnum()+"】步骤执行失败，继续本条用例后续步骤执行，进入下个步骤执行中......");
                }
            }
		}

		variable.clear();
		caselog.updateCaseDetail(taskid, testcase.getSign(), setcaseresult);
		if(setcaseresult==0){
			luckyclient.publicclass.LogUtil.APP.info("用例【"+testcase.getSign()+"】全部步骤执行结果成功...");
	        caselog.caseLogDetail(taskid, testcase.getSign(), "用例全部步骤执行结果成功","info", "ending","");
		}else{
			luckyclient.publicclass.LogUtil.APP.error("用例【"+testcase.getSign()+"】步骤执行过程中失败或是锁定...请查看具体原因！"+casenote);
	        caselog.caseLogDetail(taskid, testcase.getSign(), "用例执行过程中失败或是锁定"+casenote,"error", "ending","");
		}
		//LogOperation.UpdateTastdetail(taskid, 0);
	}

	public static String androidRunStep(Map<String, String> params, Map<String, String> variable, AndroidDriver<AndroidElement> appium,String taskid,String casenum,int stepno,LogOperation caselog) {
		String result = "";
		String property;
		String propertyValue;
		String operation;
		String operationValue;

		try {
			property = params.get("property");
			propertyValue = params.get("property_value");
			operation = params.get("operation");
			operationValue = params.get("operation_value");

			// 处理值传递
			property = ChangString.changparams(property, variable,"定位方式");
			propertyValue=ChangString.changparams(propertyValue, variable,"定位路径");
			operation=ChangString.changparams(operation, variable,"操作");
			operationValue=ChangString.changparams(operationValue, variable,"操作参数");
			
			luckyclient.publicclass.LogUtil.APP.info("二次解析用例过程完成，等待进行对象操作......");
			caselog.caseLogDetail(taskid, casenum, "对象操作:"+operation+"; 操作值:"+operationValue,"info", String.valueOf(stepno),"");
		} catch (Exception e) {
			e.printStackTrace();
			luckyclient.publicclass.LogUtil.APP.error("二次解析用例过程抛出异常！---"+e.getMessage());
			return "步骤执行失败：解析用例失败!";
		}

		try {		
			//调用接口用例
			if(null != operation&&null != operationValue&&"runcase".equals(operation)){
				String[] temp=operationValue.split(",",-1);
				String ex = TestCaseExecution.oneCaseExecuteForUICase(temp[0], taskid, caselog, appium);
				if(!ex.contains("CallCase调用出错！") && !ex.contains("解析出错啦！") && !ex.contains("失败")){
					return ex;
				}else{
					return "步骤执行失败：调用接口用例过程失败";
				}
			}
			
			AndroidElement ae = null;
			// 页面元素层
			if (null != property && null != propertyValue) { 
				ae = isElementExist(appium, property, propertyValue);
				// 判断此元素是否存在
				if (null==ae) {
					luckyclient.publicclass.LogUtil.APP.error("定位对象失败，isElementExist为null!");
					return "步骤执行失败：isElementExist定位元素过程失败！";
				}

				if (operation.indexOf("select") > -1) {
					result = AndroidEncapsulateOperation.selectOperation(ae, operation, operationValue);
				} else if (operation.indexOf("get") > -1){
					result = AndroidEncapsulateOperation.getOperation(ae, operation,operationValue);
				} else {
					result = AndroidEncapsulateOperation.objectOperation(appium, ae, operation, operationValue, property, propertyValue);
				}
				// Driver层操作
			} else if (null==property && null != operation) { 				
				// 处理弹出框事件
				if (operation.indexOf("alert") > -1){
					result = AndroidEncapsulateOperation.alertOperation(appium, operation);
				}else{
					result = AndroidEncapsulateOperation.driverOperation(appium, operation, operationValue);
				} 				
			}else{
				luckyclient.publicclass.LogUtil.APP.error("元素操作过程失败！");
				result =  "步骤执行失败：元素操作过程失败！";
			}
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error("元素定位过程或是操作过程失败或异常！"+e.getMessage());
			return "步骤执行失败：元素定位过程或是操作过程失败或异常！" + e.getMessage();
		}
		caselog.caseLogDetail(taskid, casenum, result,"info", String.valueOf(stepno),"");
		
		if(result.indexOf("获取到的值是【")>-1&&result.indexOf("】")>-1){
			result = result.substring(result.indexOf("获取到的值是【")+7, result.length()-1);
		}
		return result;

	}
    
	public static AndroidElement isElementExist(AndroidDriver<AndroidElement> appium, String property, String propertyValue) {
		try {
			AndroidElement ae = null;
			property=property.toLowerCase();
			// 处理WebElement对象定位
			switch (property) {
			case "id":
				ae = appium.findElementById(propertyValue);
				break;
			case "name":
				ae = appium.findElementByAndroidUIAutomator("text(\""+propertyValue+"\")");
				break;
			case "androiduiautomator":
				ae = appium.findElementByAndroidUIAutomator(propertyValue);
				break;
			case "xpath":
				ae = appium.findElementByXPath(propertyValue);
				break;
			case "linktext":
				ae = appium.findElementByLinkText(propertyValue);
				break;
			case "tagname":
				ae = appium.findElementByTagName(propertyValue);
				break;
			case "cssselector":
				ae = appium.findElementByCssSelector(propertyValue);
				break;
			case "classname":
				ae = appium.findElementByClassName(propertyValue);
				break;
			case "partiallinktext":
				ae = appium.findElementByPartialLinkText(propertyValue);
				break;
			default:
				break;
			}

			return ae;

		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error("当前对象定位失败："+e.getMessage());
			return null;
		}
		
	}

    public static int judgeResult(ProjectCase testcase, ProjectCasesteps step, Map<String, String> params, AndroidDriver<AndroidElement> appium, String taskid, String expect, String result, LogOperation caselog) throws InterruptedException {
        int setresult = 0;
        java.text.DateFormat timeformat = new java.text.SimpleDateFormat("MMdd-hhmmss");
        imagname = timeformat.format(new Date());
        
        if (null != result && !result.contains("步骤执行失败：")) {
            // 有预期结果
            if (null != expect && !expect.isEmpty()) {
                luckyclient.publicclass.LogUtil.APP.info("期望结果为【" + expect + "】");

                // 赋值传参模式
                if (expect.length() > ASSIGNMENT_SIGN.length() && expect.startsWith(ASSIGNMENT_SIGN)) {
                    variable.put(expect.substring(ASSIGNMENT_SIGN.length()), result);
                    luckyclient.publicclass.LogUtil.APP.info("用例：" + testcase.getSign() + " 第" + step.getStepnum() + "步，将测试结果【" + result + "】赋值给变量【" + expect.substring(ASSIGNMENT_SIGN.length()) + "】");
                    caselog.caseLogDetail(taskid, testcase.getSign(), "将测试结果【" + result + "】赋值给变量【" + expect.substring(ASSIGNMENT_SIGN.length()) + "】", "info", String.valueOf(step.getStepnum()), "");
                }
                // 移动端 UI检查模式
                else if (4 == step.getSteptype() && params.get("checkproperty") != null && params.get("checkproperty_value") != null) {
                    String checkproperty = params.get("checkproperty");
                    String checkPropertyValue = params.get("checkproperty_value");

                    AndroidElement ae = isElementExist(appium, checkproperty, checkPropertyValue);
                    if (null != ae) {
                        luckyclient.publicclass.LogUtil.APP.info("用例：" + testcase.getSign() + " 第" + step.getStepnum() + "步，在当前页面中找到预期结果中对象。当前步骤执行成功！");
                        caselog.caseLogDetail(taskid, testcase.getSign(), "在当前页面中找到预期结果中对象。当前步骤执行成功！", "info", String.valueOf(step.getStepnum()), "");
                    } else {
                        casenote = "第" + step.getStepnum() + "步，没有在当前页面中找到预期结果中对象。执行失败！";
                        setresult = 1;
                        AndroidBaseAppium.screenShot(appium, imagname);
                        luckyclient.publicclass.LogUtil.APP.error("用例：" + testcase.getSign() + " 第" + step.getStepnum() + "步，没有在当前页面中找到预期结果中对象。当前步骤执行失败！");
                        caselog.caseLogDetail(taskid, testcase.getSign(), "在当前页面中没有找到预期结果中对象。当前步骤执行失败！" + "checkproperty【" + checkproperty + "】  checkproperty_value【" + checkPropertyValue + "】", "error", String.valueOf(step.getStepnum()), imagname);
                    }
                }
                // 其它匹配模式
                else {
                    // 模糊匹配预期结果模式
                    if (expect.length() > FUZZY_MATCHING_SIGN.length() && expect.startsWith(FUZZY_MATCHING_SIGN)) {
                        if (result.contains(expect.substring(FUZZY_MATCHING_SIGN.length()))) {
                            luckyclient.publicclass.LogUtil.APP.info("用例：" + testcase.getSign() + " 第" + step.getStepnum() + "步，模糊匹配预期结果成功！执行结果：" + result);
                            caselog.caseLogDetail(taskid, testcase.getSign(), "模糊匹配预期结果成功！执行结果：" + result, "info", String.valueOf(step.getStepnum()), "");
                        } else {
                            casenote = "第" + step.getStepnum() + "步，模糊匹配预期结果失败！";
                            setresult = 1;
                            AndroidBaseAppium.screenShot(appium, imagname);
                            luckyclient.publicclass.LogUtil.APP.error("用例：" + testcase.getSign() + " 第" + step.getStepnum() + "步，模糊匹配预期结果失败！预期结果：" + expect.substring(FUZZY_MATCHING_SIGN.length()) + "，测试结果：" + result);
                            caselog.caseLogDetail(taskid, testcase.getSign(), "模糊匹配预期结果失败！预期结果：" + expect.substring(FUZZY_MATCHING_SIGN.length()) + "，测试结果：" + result, "error", String.valueOf(step.getStepnum()), imagname);
                        }
                    }
                    // 正则匹配预期结果模式
                    else if (expect.length() > REGULAR_MATCHING_SIGN.length() && expect.startsWith(REGULAR_MATCHING_SIGN)) {
                        Pattern pattern = Pattern.compile(expect.substring(REGULAR_MATCHING_SIGN.length()));
                        Matcher matcher = pattern.matcher(result);
                        if (matcher.find()) {
                            luckyclient.publicclass.LogUtil.APP.info("用例：" + testcase.getSign() + " 第" + step.getStepnum() + "步，正则匹配预期结果成功！执行结果：" + result);
                            caselog.caseLogDetail(taskid, testcase.getSign(), "正则匹配预期结果成功！", "info", String.valueOf(step.getStepnum()), "");
                        } else {
                            casenote = "第" + step.getStepnum() + "步，正则匹配预期结果失败！";
                            setresult = 1;
                            AndroidBaseAppium.screenShot(appium, imagname);
                            luckyclient.publicclass.LogUtil.APP.error("用例：" + testcase.getSign() + " 第" + step.getStepnum() + "步，正则匹配预期结果失败！预期结果：" + expect.substring(REGULAR_MATCHING_SIGN.length()) + "，测试结果：" + result);
                            caselog.caseLogDetail(taskid, testcase.getSign(), "正则匹配预期结果失败！预期结果：" + expect.substring(REGULAR_MATCHING_SIGN.length()) + "，测试结果：" + result, "error", String.valueOf(step.getStepnum()), imagname);
                        }
                    }
                    // 精确匹配预期结果模式
                    else {
                        if (expect.equals(result)) {
                            luckyclient.publicclass.LogUtil.APP.info("用例：" + testcase.getSign() + " 第" + step.getStepnum() + "步，精确匹配预期结果成功！执行结果：" + result);
                            caselog.caseLogDetail(taskid, testcase.getSign(), "精确匹配预期结果成功！", "info", String.valueOf(step.getStepnum()), "");
                        } else {
                            casenote = "第" + step.getStepnum() + "步，精确匹配预期结果失败！";
                            setresult = 1;
                            AndroidBaseAppium.screenShot(appium, imagname);
                            luckyclient.publicclass.LogUtil.APP.error("用例：" + testcase.getSign() + " 第" + step.getStepnum() + "步，精确匹配预期结果失败！预期结果是：【"+expect+"】  执行结果：【"+ result+"】");
                            caselog.caseLogDetail(taskid, testcase.getSign(), "精确匹配预期结果失败！预期结果是：【"+expect+"】  执行结果：【"+ result+"】", "error", String.valueOf(step.getStepnum()), imagname);
                        }
                    }
                }
            }
        } else {
            casenote = (null != result) ? result : "";
            setresult = 2;
            AndroidBaseAppium.screenShot(appium, imagname);
            LogUtil.APP.error("用例：" + testcase.getSign() + " 第" + step.getStepnum() + "步，执行结果：" + casenote);
            caselog.caseLogDetail(taskid, testcase.getSign(), "当前步骤在执行过程中解析|定位元素|操作对象失败！" + casenote, "error", String.valueOf(step.getStepnum()), imagname);
        }

        // 获取步骤间等待时间
        int waitsec = Integer.parseInt(params.get("StepWait"));
        if (waitsec > 0) {
            luckyclient.publicclass.LogUtil.APP.info("操作休眠【" + waitsec + "】秒");
            Thread.sleep(waitsec * 1000);
        }
        
        return setresult;
    }
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
