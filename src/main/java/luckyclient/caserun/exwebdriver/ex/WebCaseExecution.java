package luckyclient.caserun.exwebdriver.ex;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import luckyclient.caserun.exinterface.TestCaseExecution;
import luckyclient.caserun.exwebdriver.BaseWebDrive;
import luckyclient.caserun.exwebdriver.EncapsulateOperation;
import luckyclient.dblog.LogOperation;
import luckyclient.planapi.entity.ProjectCase;
import luckyclient.planapi.entity.ProjectCasesteps;
import luckyclient.publicclass.DBOperation;

public class WebCaseExecution extends TestCaseExecution{
	static Map<String, String> variable = new HashMap<String, String>();

	public static void CaseExcution(ProjectCase testcase, List<ProjectCasesteps> steps,String taskid, WebDriver wd,LogOperation caselog)
			throws InterruptedException {
		int setresult = 0; // 0:成功 1:失败 2:锁定 其他：锁定
		String casenote = "备注初始化";
		String imagname = "";
		
		caselog.AddCaseDetail(taskid, testcase.getSign(), "1", testcase.getName(), 4);       //插入开始执行的用例
		
		for (ProjectCasesteps step : steps) {
			Map<String, String> params = WebDriverAnalyticCase.AnalyticCaseStep(testcase, step, taskid);
			
			if(params.get("exception")!=null&&params.get("exception").toString().indexOf("解析异常")>-1){
				setresult = 2;
				break;
			}
			
			String result = WebCaseExecution.runStep(params, wd, taskid, testcase.getSign(), step.getStepnum(), caselog);

			String expectedResults = params.get("ExpectedResults").toString();

			if (result.indexOf("出错") < 0 && result.indexOf("失败") < 0) { // 运行结果正常
				int waitsec = Integer.parseInt(params.get("StepWait").toString()); // 获取步骤间等待时间
				if (waitsec != 0) {
					luckyclient.publicclass.LogUtil.APP.info("操作休眠【"+waitsec+"】秒");
					Thread.sleep(waitsec * 1000);
				}
				
				if (!expectedResults.equals("")) { // 有预期结果
					// 判断传参
					luckyclient.publicclass.LogUtil.APP.info("expectedResults=【"+expectedResults+"】");
					if (expectedResults.length() > 2 && expectedResults.substring(0, 2).indexOf("$=") > -1) {
						String ExpectedResultVariable = expectedResults.substring(2);
						variable.put(ExpectedResultVariable, result);
						continue;
					}

					// 判断预期结果-检查模式
					if (params.get("checkproperty") != null && params.get("checkproperty_value") != null) {
						String checkproperty = params.get("checkproperty").toString();
						String checkproperty_value = params.get("checkproperty_value").toString();

						WebElement we = isElementExist(wd, checkproperty, checkproperty_value);
						if (null != we) {
							luckyclient.publicclass.LogUtil.APP.info("用例：" + testcase.getSign() + " 第" + step.getStepnum()
									+ "步，在当前页面中找到预期结果中对象。当前步骤执行成功！");
							caselog.CaseLogDetail(taskid, testcase.getSign(), "在当前页面中找到预期结果中对象。当前步骤执行成功！",
									"info", String.valueOf(step.getStepnum()),"");
							continue;
						} else {
							casenote = "第" + step.getStepnum() + "步，没有在当前页面中找到预期结果中对象。执行失败！";
							setresult = 1;
							java.text.DateFormat timeformat = new java.text.SimpleDateFormat("MMdd-hhmmss");
							imagname = timeformat.format(new Date());
							BaseWebDrive.WebScreenShot(wd,imagname);
							luckyclient.publicclass.LogUtil.APP.error("用例：" + testcase.getSign() + " 第" + step.getStepnum()
									+ "步，没有在当前页面中找到预期结果中对象。当前步骤执行失败！");
							caselog.CaseLogDetail(taskid, testcase.getSign(), "在当前页面中没有找到预期结果中对象。当前步骤执行失败！"
									+ "checkproperty【"+checkproperty+"】  checkproperty_value【"+checkproperty_value+"】","error", String.valueOf(step.getStepnum()),imagname);
							break;
						}

					}else{
						// 模糊匹配预期结果模式
						if (expectedResults.length()>2 && expectedResults.substring(0, 2).indexOf("%=")>-1) {
							if(result.indexOf(expectedResults.substring(2))>-1){
								luckyclient.publicclass.LogUtil.APP.info("用例：" + testcase.getSign() + " 第" + step.getStepnum()
								+ "步，模糊匹配预期结果成功！执行结果："+result);
						        caselog.CaseLogDetail(taskid, testcase.getSign(), "步骤模糊匹配预期结果成功！",
								"info", String.valueOf(step.getStepnum()),"");
						        continue;
							}else{
								casenote = "第" + step.getStepnum() + "步，模糊匹配预期结果失败！";
								setresult = 1;
								java.text.DateFormat timeformat = new java.text.SimpleDateFormat("MMdd-hhmmss");
								imagname = timeformat.format(new Date());
								BaseWebDrive.WebScreenShot(wd,imagname);
								luckyclient.publicclass.LogUtil.APP.error("用例：" + testcase.getSign() + " 第" + step.getStepnum()
								+ "步，模糊匹配预期结果失败！执行结果："+result);
						        caselog.CaseLogDetail(taskid, testcase.getSign(), "步骤模糊匹配预期结果失败！执行结果："+result,
								"error", String.valueOf(step.getStepnum()),imagname);
								break;
							}
						}else if(expectedResults.equals(result)) {    // 直接匹配预期结果模式
							luckyclient.publicclass.LogUtil.APP.info("用例：" + testcase.getSign() + " 第" + step.getStepnum()
							+ "步，直接匹配预期结果成功！执行结果："+result);
					        caselog.CaseLogDetail(taskid, testcase.getSign(), "步骤直接匹配预期结果成功！",
							"info", String.valueOf(step.getStepnum()),"");
					        continue;
						} else {
							casenote = "第" + step.getStepnum() + "步，直接匹配预期结果失败！";
							setresult = 1;
							java.text.DateFormat timeformat = new java.text.SimpleDateFormat("MMdd-hhmmss");
							imagname = timeformat.format(new Date());
							BaseWebDrive.WebScreenShot(wd,imagname);
							luckyclient.publicclass.LogUtil.APP.error("用例：" + testcase.getSign() + " 第" + step.getStepnum()
							+ "步，直接匹配预期结果失败！执行结果："+result);
					        caselog.CaseLogDetail(taskid, testcase.getSign(), "步骤直接匹配预期结果失败！执行结果："+result,
							"error", String.valueOf(step.getStepnum()),imagname);
							break;
						}
					}
				}

			} else {
				casenote = result;
				setresult = 2;
				java.text.DateFormat timeformat = new java.text.SimpleDateFormat("MMdd-hhmmss");
				imagname = timeformat.format(new Date());
				BaseWebDrive.WebScreenShot(wd,imagname);
				luckyclient.publicclass.LogUtil.APP.error("用例：" + testcase.getSign() + " 第" + step.getStepnum()	+ "步，"+result);
		        caselog.CaseLogDetail(taskid, testcase.getSign(), "当前步骤在执行过程中解析|定位元素|操作对象失败！"+result,
				"error", String.valueOf(step.getStepnum()),imagname);
				break;
			}

		}

		variable.clear();
		caselog.UpdateCaseDetail(taskid, testcase.getSign(), setresult);
		if(setresult==0){
			luckyclient.publicclass.LogUtil.APP.info("用例【"+testcase.getSign()+"】全部步骤执行结果成功...");
	        caselog.CaseLogDetail(taskid, testcase.getSign(), "用例全部步骤执行结果成功","info", "ending","");
		}else{
			luckyclient.publicclass.LogUtil.APP.error("用例【"+testcase.getSign()+"】步骤执行过程中失败或是锁定...请查看具体原因！"+casenote);
	        caselog.CaseLogDetail(taskid, testcase.getSign(), "用例执行过程中失败或是锁定"+casenote,"error", "ending","");
		}
		//LogOperation.UpdateTastdetail(taskid, 0);
	}

	private static String runStep(Map<String, String> params, WebDriver wd,String taskid,String casenum,int stepno,LogOperation caselog) {
		String result = "";
		String property;
		String property_value;
		String operation;
		String operation_value;

		try {
			property = params.get("property");
			property_value = params.get("property_value");
			operation = params.get("operation");
			operation_value = params.get("operation_value");

			// 处理值传递
			if (property_value != null && property_value.indexOf("@") > -1 && property_value.indexOf("[@") < 0 
					&& property_value.indexOf("@@") < 0) {
				property_value = SettingParameter(property_value);
				// 判断传参是否存在问题
				if (property_value.indexOf("Set parameter error") > -1) {
					caselog.CaseLogDetail(taskid, casenum, "当前步骤解析出现异常或是对象为空！---"+property,
							"error", String.valueOf(stepno),"");
					return "处理传参过程出错：" + property_value;
				}
			}else if(property_value != null && (property_value.indexOf("&quot;")>-1 
					|| property_value.indexOf("&#39;")>-1 || property_value.indexOf("@@")>-1)){
				property_value = property_value.replaceAll("&quot;", "\"");
				property_value = property_value.replaceAll("&#39;", "\'");
				property_value = property_value.replaceAll("@@", "@");
			}
			
			if (operation_value != null && operation_value.indexOf("@") > -1 && operation_value.indexOf("@@") < 0) {
				operation_value = SettingParameter(operation_value);
				if (operation_value.indexOf("Set parameter error") > -1) {
					return "处理传参过程出错：" + property_value;
				}
			}else if(operation_value != null && (operation_value.indexOf("&quot;")>-1 
					|| operation_value.indexOf("&#39;")>-1 || operation_value.indexOf("@@")>-1)){
				operation_value = operation_value.replaceAll("&quot;", "\"");
				operation_value = operation_value.replaceAll("&#39;", "\'");
				operation_value = operation_value.replaceAll("@@", "@");
			}
			
			luckyclient.publicclass.LogUtil.APP.info("二次解析用例过程完成，等待进行对象操作......");
			caselog.CaseLogDetail(taskid, casenum, "对象操作:"+operation+"; 操作值:"+operation_value,"info", String.valueOf(stepno),"");

		} catch (Exception e) {
			e.printStackTrace();
			luckyclient.publicclass.LogUtil.APP.error("二次解析用例过程抛出异常！---"+e.getMessage());
			return "解析用例失败!";
		}

		try {		
			//调用接口用例
			if(null != operation&&null != operation_value&&"runcase".equals(operation)){
				String temp[]=operation_value.split(",",-1);
				String ex = TestCaseExecution.OneCaseExecuteForWebDriver(temp[0],taskid);
				if(ex.indexOf("CallCase调用出错！")<=-1&&ex.indexOf("解析出错啦！")<=-1&&ex.indexOf("匹配失败")<=-1){
					return ex;
				}else{
					return "调用接口用例过程失败";
				}
			}
			
			WebElement we = null;

			if (null != property && null != property_value) { // 页面元素层
				we = isElementExist(wd, property, property_value);
				// 判断此元素是否存在
				if (null==we) {
					luckyclient.publicclass.LogUtil.APP.error("定位对象失败，isElementExist为null!");
					return "isElementExist定位元素过程失败！";
				}

				if (operation.indexOf("select") > -1) {
					result = EncapsulateOperation.SelectOperation(we, operation, operation_value);
				} else if (operation.indexOf("get") > -1){
					result = EncapsulateOperation.GetOperation(wd, we, operation);
				} else if (operation.indexOf("mouse") > -1){
					result = EncapsulateOperation.ActionWeOperation(wd, we, operation, operation_value, property, property_value);
				} else {
					result = EncapsulateOperation.ObjectOperation(wd, we, operation, operation_value, property, property_value);
				}
			} else if (null==property && null != operation) { // Driver层操作				
				// 处理弹出框事件
				if (operation.indexOf("alert") > -1){
					result = EncapsulateOperation.AlertOperation(wd, operation);
				}else if(operation.indexOf("mouse") > -1){
					result = EncapsulateOperation.ActionOperation(wd, operation, operation_value);
				}else{
					result = EncapsulateOperation.DriverOperation(wd, operation, operation_value);
				} 				
			}else{
				luckyclient.publicclass.LogUtil.APP.error("元素操作过程失败！");
				result =  "元素操作过程失败！";
			}
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error("元素定位过程或是操作过程失败或异常！"+e.getMessage());
			return "元素定位过程或是操作过程失败或异常！" + e.getMessage();
		}
		caselog.CaseLogDetail(taskid, casenum, result,"info", String.valueOf(stepno),"");
		
		if(result.indexOf("获取到的值是【")>-1&&result.indexOf("】")>-1){
			result = result.substring(7, result.length()-1);
		}
		return result;

	}

	private static String SettingParameter(String parameter) {
		int keyexistidentity = 0;
		if (parameter.indexOf("&quot;") > -1 || parameter.indexOf("&#39;") > -1) { // 页面转义字符转换
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
		} else if (sumvariable == 2) { // 单个参数中引用第二个变量
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
		if (sumvariable == 2 || sumvariable == 3) { // 处理第二个传参
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
		if (sumvariable == 3) { // 处理第三个传参
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
			String ParameterValues = parameter.replaceAll("@" + uservariable, variable.get(uservariable).toString());
			// 处理第二个传参
			if (sumvariable == 2 || sumvariable == 3) {
				ParameterValues = ParameterValues.replaceAll("@" + uservariable1,
						variable.get(uservariable1).toString());
			}
			// 处理第三个传参
			if (sumvariable == 3) {
				ParameterValues = ParameterValues.replaceAll("@" + uservariable2,
						variable.get(uservariable2).toString());
			}

			return ParameterValues;
		} else {
			luckyclient.publicclass.LogUtil.APP.error("没有找到你要的变量哦，再找下吧！第一个变量名称是：" + uservariable + "，第" + "二个变量名称是：" + uservariable1
					+ "，第三个变量名称是：" + uservariable2);
			return "【Set parameter error】没有找到你要的变量哦，再找下吧！第一个变量名称是：" + uservariable + "，第" + "二个变量名称是：" + uservariable1
					+ "，第三个变量名称是：" + uservariable2;
		}
	}

	public static WebElement isElementExist(WebDriver wd, String property, String property_value) {
		try {
			WebElement we = null;

			// 处理WebElement对象定位
			switch (property) {
			case "id":
				we = wd.findElement(By.id(property_value));
				break;
			case "name":
				we = wd.findElement(By.name(property_value));
				break;
			case "xpath":
				we = wd.findElement(By.xpath(property_value));
				break;
			case "linktext":
				we = wd.findElement(By.linkText(property_value));
				break;
			case "tagname":
				we = wd.findElement(By.tagName(property_value));
				break;
			case "cssselector":
				we = wd.findElement(By.cssSelector(property_value));
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
