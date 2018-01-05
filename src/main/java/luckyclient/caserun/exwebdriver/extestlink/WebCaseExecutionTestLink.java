package luckyclient.caserun.exwebdriver.extestlink;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestCaseStep;
import luckyclient.caserun.exinterface.testlink.TestLinkCaseExecution;
import luckyclient.caserun.exwebdriver.BaseWebDrive;
import luckyclient.caserun.exwebdriver.EncapsulateOperation;
import luckyclient.dblog.LogOperation;
import luckyclient.publicclass.DBOperation;
import luckyclient.testlinkapi.WebDriverAnalyticTestLinkCase;

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
public class WebCaseExecutionTestLink extends TestLinkCaseExecution{
	static Map<String, String> variable = new HashMap<String, String>();

	public static void caseExcution(String projectname, TestCase testcase, String taskid, WebDriver wd,LogOperation caselog)
			throws InterruptedException {
		// 0:成功 1:失败 2:锁定 其他：锁定
		int setresult = 0; 
		String casenote = "备注初始化";
		String imagname = "";
		//插入开始执行的用例
		caselog.addCaseDetail(taskid, testcase.getFullExternalId(), testcase.getVersion().toString(), testcase.getName(), 4);       
		
		for (TestCaseStep step : testcase.getSteps()) {
			Map<String, String> params = WebDriverAnalyticTestLinkCase.analyticCaseStep(testcase, step.getNumber(), taskid,caselog);
			
			if(params.get("exception")!=null&&params.get("exception").toString().indexOf("解析异常")>-1){
				setresult = 2;
				break;
			}
			
			String result = WebCaseExecutionTestLink.runStep(params, wd, taskid, testcase.getFullExternalId(), step.getNumber(), caselog);

			String expectedResults = params.get("ExpectedResults").toString();
			// 运行结果正常
			if (result.indexOf("出错") < 0 && result.indexOf("失败") < 0) { 
				// 获取步骤间等待时间
				int waitsec = Integer.parseInt(params.get("StepWait").toString()); 
				if (waitsec != 0) {
					luckyclient.publicclass.LogUtil.APP.info("操作休眠【"+waitsec+"】秒");
					Thread.sleep(waitsec * 1000);
				}
				// 有预期结果
				if (!"".equals(expectedResults)) { 
					// 判断传参
					luckyclient.publicclass.LogUtil.APP.info("expectedResults=【"+expectedResults+"】");
					if (expectedResults.length() > 2 && expectedResults.substring(0, 2).indexOf("$=") > -1) {
						String expectedResultVariable = expectedResults.substring(2);
						variable.put(expectedResultVariable, result);
						continue;
					}

					// 判断预期结果-检查模式
					if (params.get("checkproperty") != null && params.get("checkproperty_value") != null) {
						String checkproperty = params.get("checkproperty").toString();
						String checkPropertyValue = params.get("checkproperty_value").toString();

						WebElement we = isElementExist(wd, checkproperty, checkPropertyValue);
						if (we != null) {
							luckyclient.publicclass.LogUtil.APP.info("用例：" + testcase.getFullExternalId() + " 第" + step.getNumber()
									+ "步，在当前页面中找到预期结果中对象。当前步骤执行成功！");
							caselog.caseLogDetail(taskid, testcase.getFullExternalId(), "在当前页面中找到预期结果中对象。当前步骤执行成功！",
									"info", String.valueOf(step.getNumber()),"");
							continue;
						} else {
							casenote = "第" + step.getNumber() + "步，没有在当前页面中找到预期结果中对象。执行失败！";
							setresult = 1;
							java.text.DateFormat timeformat = new java.text.SimpleDateFormat("MMdd-hhmmss");
							imagname = timeformat.format(new Date());
							BaseWebDrive.webScreenShot(wd,imagname);
							luckyclient.publicclass.LogUtil.APP.error("用例：" + testcase.getFullExternalId() + " 第" + step.getNumber()
									+ "步，没有在当前页面中找到预期结果中对象。当前步骤执行失败！");
							caselog.caseLogDetail(taskid, testcase.getFullExternalId(), "在当前页面中没有找到预期结果中对象。当前步骤执行失败！"
									+ "checkproperty【"+checkproperty+"】  checkproperty_value【"+checkPropertyValue+"】","error", String.valueOf(step.getNumber()),imagname);
							break;
						}

					}else{
						// 模糊匹配预期结果模式
						if (expectedResults.length()>2 && expectedResults.substring(0, 2).indexOf("%=")>-1) {
							if(result.indexOf(expectedResults.substring(2))>-1){
								luckyclient.publicclass.LogUtil.APP.info("用例：" + testcase.getFullExternalId() + " 第" + step.getNumber()
								+ "步，模糊匹配预期结果成功！执行结果："+result);
						        caselog.caseLogDetail(taskid, testcase.getFullExternalId(), "步骤模糊匹配预期结果成功！",
								"info", String.valueOf(step.getNumber()),"");
						        continue;
							}else{
								casenote = "第" + step.getNumber() + "步，模糊匹配预期结果失败！";
								setresult = 1;
								java.text.DateFormat timeformat = new java.text.SimpleDateFormat("MMdd-hhmmss");
								imagname = timeformat.format(new Date());
								BaseWebDrive.webScreenShot(wd,imagname);
								luckyclient.publicclass.LogUtil.APP.error("用例：" + testcase.getFullExternalId() + " 第" + step.getNumber()
								+ "步，模糊匹配预期结果失败！执行结果："+result);
						        caselog.caseLogDetail(taskid, testcase.getFullExternalId(), "步骤模糊匹配预期结果失败！执行结果："+result,
								"error", String.valueOf(step.getNumber()),imagname);
								break;
							}
							// 直接匹配预期结果模式
						}else if(expectedResults.equals(result)) {    
							luckyclient.publicclass.LogUtil.APP.info("用例：" + testcase.getFullExternalId() + " 第" + step.getNumber()
							+ "步，直接匹配预期结果成功！执行结果："+result);
					        caselog.caseLogDetail(taskid, testcase.getFullExternalId(), "步骤直接匹配预期结果成功！",
							"info", String.valueOf(step.getNumber()),"");
					        continue;
						} else {
							casenote = "第" + step.getNumber() + "步，直接匹配预期结果失败！";
							setresult = 1;
							java.text.DateFormat timeformat = new java.text.SimpleDateFormat("MMdd-hhmmss");
							imagname = timeformat.format(new Date());
							BaseWebDrive.webScreenShot(wd,imagname);
							luckyclient.publicclass.LogUtil.APP.error("用例：" + testcase.getFullExternalId() + " 第" + step.getNumber()
							+ "步，直接匹配预期结果失败！执行结果："+result);
					        caselog.caseLogDetail(taskid, testcase.getFullExternalId(), "步骤直接匹配预期结果失败！执行结果："+result,
							"error", String.valueOf(step.getNumber()),imagname);
							break;
						}
					}
				}

			} else {
				casenote = result;
				setresult = 2;
				java.text.DateFormat timeformat = new java.text.SimpleDateFormat("MMdd-hhmmss");
				imagname = timeformat.format(new Date());
				BaseWebDrive.webScreenShot(wd,imagname);
				luckyclient.publicclass.LogUtil.APP.error("用例：" + testcase.getFullExternalId() + " 第" + step.getNumber()	+ "步，"+result);
		        caselog.caseLogDetail(taskid, testcase.getFullExternalId(), "当前步骤在执行过程中解析|定位元素|操作对象失败！"+result,
				"error", String.valueOf(step.getNumber()),imagname);
				break;
			}

		}

		variable.clear();
		caselog.updateCaseDetail(taskid, testcase.getFullExternalId(), setresult);
		if(setresult==0){
			luckyclient.publicclass.LogUtil.APP.info("用例【"+testcase.getFullExternalId()+"】全部步骤执行结果成功...");
	        caselog.caseLogDetail(taskid, testcase.getFullExternalId(), "用例全部步骤执行结果成功","info", "ending","");
		}else{
			luckyclient.publicclass.LogUtil.APP.error("用例【"+testcase.getFullExternalId()+"】步骤执行过程中失败或是锁定...请查看具体原因！"+casenote);
	        caselog.caseLogDetail(taskid, testcase.getFullExternalId(), "用例执行过程中失败或是锁定"+casenote,"error", "ending","");
		}
		//LogOperation.UpdateTastdetail(taskid, 0);
	}

	private static String runStep(Map<String, String> params, WebDriver wd,String taskid,String casenum,int stepno,LogOperation caselog) {
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

			// 用例名称解析出现异常或是单个步骤参数解析异常
			if (propertyValue != null && property.indexOf("解析异常") > -1) {
				luckyclient.publicclass.LogUtil.APP.error("当前步骤解析出现异常或是对象为空！---"+property);
				return "用例解析出错啦！";
			}

			// 处理值传递
			if (propertyValue != null && propertyValue.indexOf("@") > -1 && propertyValue.indexOf("[@") < 0 
					&& propertyValue.indexOf("@@") < 0) {
				propertyValue = settingParameter(propertyValue);
				// 判断传参是否存在问题
				if (propertyValue.indexOf("Set parameter error") > -1) {
					caselog.caseLogDetail(taskid, casenum, "当前步骤解析出现异常或是对象为空！---"+property,
							"error", String.valueOf(stepno),"");
					return "处理传参过程出错：" + propertyValue;
				}
			}else if(propertyValue != null && (propertyValue.indexOf("&quot;")>-1 
					|| propertyValue.indexOf("&#39;")>-1 || propertyValue.indexOf("@@")>-1)){
				propertyValue = propertyValue.replaceAll("&quot;", "\"");
				propertyValue = propertyValue.replaceAll("&#39;", "\'");
				propertyValue = propertyValue.replaceAll("@@", "@");
			}
			
			if (operationValue != null && operationValue.indexOf("@") > -1 && operationValue.indexOf("@@") < 0) {
				operationValue = settingParameter(operationValue);
				if (operationValue.indexOf("Set parameter error") > -1) {
					return "处理传参过程出错：" + propertyValue;
				}
			}else if(operationValue != null && (operationValue.indexOf("&quot;")>-1 
					|| operationValue.indexOf("&#39;")>-1 || operationValue.indexOf("@@")>-1)){
				operationValue = operationValue.replaceAll("&quot;", "\"");
				operationValue = operationValue.replaceAll("&#39;", "\'");
				operationValue = operationValue.replaceAll("@@", "@");
			}
			
			luckyclient.publicclass.LogUtil.APP.info("二次解析用例过程完成，等待进行对象操作......");
			caselog.caseLogDetail(taskid, casenum, "对象操作:"+operation+"; 操作值:"+operationValue,"info", String.valueOf(stepno),"");

		} catch (Exception e) {
			e.printStackTrace();
			luckyclient.publicclass.LogUtil.APP.error("二次解析用例过程抛出异常！---"+e.getMessage());
			return "解析用例失败!";
		}

		try {		
			//调用接口用例
			if(operation!=null&&operationValue!=null&&"runcase".equals(operation)){
				String[] temp=operationValue.split(",",-1);
				String ex = TestLinkCaseExecution.oneCaseExecuteForWebDriver(temp[0], Integer.valueOf(temp[1]),taskid,caselog);
				if(ex.indexOf("CallCase调用出错！")<=-1&&ex.indexOf("解析出错啦！")<=-1&&ex.indexOf("匹配失败")<=-1){
					return ex;
				}else{
					return "调用接口用例过程失败";
				}
			}
			
			WebElement we = null;
			// 页面元素层
			if (property != null && propertyValue != null) { 
				we = isElementExist(wd, property, propertyValue);
				// 判断此元素是否存在
				if (we == null) {
					luckyclient.publicclass.LogUtil.APP.error("定位对象失败，isElementExist为null!");
					return "isElementExist定位元素过程失败！";
				}

				if (operation.indexOf("select") > -1) {
					result = EncapsulateOperation.selectOperation(we, operation, operationValue);
				} else if (operation.indexOf("get") > -1){
					result = EncapsulateOperation.getOperation(wd, we, operation,operationValue);
				} else if (operation.indexOf("mouse") > -1){
					result = EncapsulateOperation.actionWeOperation(wd, we, operation, operationValue, property, propertyValue);
				} else {
					result = EncapsulateOperation.objectOperation(wd, we, operation, operationValue, property, propertyValue);
				}
				// Driver层操作		
			} else if (property == null && operation != null) { 		
				// 处理弹出框事件
				if (operation.indexOf("alert") > -1){
					result = EncapsulateOperation.alertOperation(wd, operation);
				}else if(operation.indexOf("mouse") > -1){
					result = EncapsulateOperation.actionOperation(wd, operation, operationValue);
				}else{
					result = EncapsulateOperation.driverOperation(wd, operation, operationValue);
				} 				
			}else{
				luckyclient.publicclass.LogUtil.APP.error("元素操作过程失败！");
				result =  "元素操作过程失败！";
			}
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error("元素定位过程或是操作过程失败或异常！"+e.getMessage());
			return "元素定位过程或是操作过程失败或异常！" + e.getMessage();
		}
		caselog.caseLogDetail(taskid, casenum, result,"info", String.valueOf(stepno),"");
		
		if(result.indexOf("获取到的值是【")>-1&&result.indexOf("】")>-1){
			result = result.substring(7, result.length()-1);
		}
		return result;

	}

	private static String settingParameter(String parameter) {
		int keyexistidentity = 0;
		if (parameter.indexOf("&quot;") > -1 || parameter.indexOf("&#39;") > -1) { 
			parameter = parameter.replaceAll("&quot;", "\"");
			parameter = parameter.replaceAll("&#39;", "\'");
		}
		//处理参数字符串中带@的情况
		if(parameter.indexOf("\\@")>-1){
			return parameter.replace("\\@", "@");
		}
		
		// 取单个参数中引用变量次数
		int sumvariable = DBOperation.sumString(parameter, "@");
		String uservariable = null;
		String uservariable1 = null;
		String uservariable2 = null;

		if (sumvariable == 1) {
			uservariable = parameter.substring(parameter.indexOf("@") + 1);
		} else if (sumvariable == 2) { 
			uservariable = parameter.substring(parameter.indexOf("@") + 1, parameter.lastIndexOf("@"));
			uservariable1 = parameter.substring(parameter.lastIndexOf("@") + 1);
		} else if (sumvariable == 3) {
			String temp = parameter.substring(parameter.indexOf("@") + 1, parameter.lastIndexOf("@"));
			uservariable1 = temp.substring(temp.indexOf("@") + 1);
			uservariable2 = parameter.substring(parameter.lastIndexOf("@") + 1);
			uservariable = parameter.substring(parameter.indexOf("@") + 1, parameter.indexOf(uservariable1) - 1);
		} else {
			luckyclient.publicclass.LogUtil.APP.error("你好像在一个参数中引用了超过3个以上的变量哦！我处理不过来啦！");
			return "你好像在一个参数中引用了超过3个以上的变量哦！我处理不过来啦！【Set parameter error】";
		}

		@SuppressWarnings("rawtypes")
		Iterator keys = variable.keySet().iterator();
		String key = null;
		while (keys.hasNext()) {
			key = (String) keys.next();
			if (uservariable.indexOf(key) > -1) {
				keyexistidentity = 1;
				uservariable = key;
				break;
			}
		}
		if (sumvariable == 2 || sumvariable == 3) { 
			keys = variable.keySet().iterator();
			while (keys.hasNext()) {
				keyexistidentity = 0;
				key = (String) keys.next();
				if (uservariable1.indexOf(key) > -1) {
					keyexistidentity = 1;
					uservariable1 = key;
					break;
				}
			}
		}
		if (sumvariable == 3) { 
			keys = variable.keySet().iterator();
			while (keys.hasNext()) {
				keyexistidentity = 0;
				key = (String) keys.next();
				if (uservariable2.indexOf(key) > -1) {
					keyexistidentity = 1;
					uservariable2 = key;
					break;
				}
			}
		}
		if (keyexistidentity == 1) {
			// 拼装参数（传参+原有字符串）
			String parameterValues = parameter.replaceAll("@" + uservariable, variable.get(uservariable).toString());
			// 处理第二个传参
			if (sumvariable == 2 || sumvariable == 3) {
				parameterValues = parameterValues.replaceAll("@" + uservariable1,
						variable.get(uservariable1).toString());
			}
			// 处理第三个传参
			if (sumvariable == 3) {
				parameterValues = parameterValues.replaceAll("@" + uservariable2,
						variable.get(uservariable2).toString());
			}

			return parameterValues;
		} else {
			luckyclient.publicclass.LogUtil.APP.error("没有找到你要的变量哦，再找下吧！第一个变量名称是：" + uservariable + "，第" + "二个变量名称是：" + uservariable1
					+ "，第三个变量名称是：" + uservariable2);
			return "【Set parameter error】没有找到你要的变量哦，再找下吧！第一个变量名称是：" + uservariable + "，第" + "二个变量名称是：" + uservariable1
					+ "，第三个变量名称是：" + uservariable2;
		}
	}

	public static WebElement isElementExist(WebDriver wd, String property, String propertyValue) {
		try {
			WebElement we = null;

			// 处理WebElement对象定位
			switch (property) {
			case "id":
				we = wd.findElement(By.id(propertyValue));
				break;
			case "name":
				we = wd.findElement(By.name(propertyValue));
				break;
			case "xpath":
				we = wd.findElement(By.xpath(propertyValue));
				break;
			case "linktext":
				we = wd.findElement(By.linkText(propertyValue));
				break;
			case "tagname":
				we = wd.findElement(By.tagName(propertyValue));
				break;
			case "cssselector":
				we = wd.findElement(By.cssSelector(propertyValue));
				break;
			default:
				break;
			}

			return we;

		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error("当前对象定位失败："+e.getMessage());
			return null;
		}
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
