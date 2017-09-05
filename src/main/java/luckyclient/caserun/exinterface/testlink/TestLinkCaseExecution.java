package luckyclient.caserun.exinterface.testlink;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionType;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import luckyclient.caserun.exinterface.TestControl;
import luckyclient.dblog.DbLink;
import luckyclient.dblog.LogOperation;
import luckyclient.publicclass.DBOperation;
import luckyclient.publicclass.InvokeMethod;
import luckyclient.testlinkapi.InterfaceAnalyticTestLinkCase;
import luckyclient.testlinkapi.TestCaseApi;

public class TestLinkCaseExecution{	
	/**
	 * @param 项目名
	 * @param 用例编号
	 * @param 用例版本号
	 * 用于在testlink上配置好用例参数后，做单条用例调试，并通过日志框架写日志到UTP上，用做UTP上单条用例运行
	 */
	@SuppressWarnings("static-access")
	public static void OneCaseExecuteForTast(String projectname,String testCaseExternalId,int version,String tastid){
		Map<String,String> variable = new HashMap<String,String>();
		TestControl.TASKID = tastid;
		DbLink.exetype = 0;
		LogOperation caselog = new LogOperation();        //初始化写用例结果以及日志模块 
		String packagename =null;
		String functionname = null;
		String expectedresults = null;
		Integer setresult = null;
		Object[] getParameterValues = null;
    	String testnote = null;
		int k = 0;
		caselog.DeleteCaseLogDetail(testCaseExternalId, tastid);    //删除旧的日志
		TestCase testcaseob = TestCaseApi.getTestCaseByExternalId(testCaseExternalId, version);
		if(testcaseob.getExecutionType()==ExecutionType.AUTOMATED){
		    //进入循环，解析用例所有步骤
		    for(int i=0;i<testcaseob.getSteps().size();i++){		    	
		    	Map<String,String> casescript = InterfaceAnalyticTestLinkCase.AnalyticCaseStep(testcaseob, i+1,tastid);    //解析单个步骤中的脚本
		    	packagename = casescript.get("PackageName").toString();
		    	functionname = casescript.get("FunctionName").toString();
		    	//用例名称解析出现异常或是单个步骤参数解析异常
		    	if(functionname.indexOf("解析异常")>-1||k==1){
		    		k=0;
		    		testnote = "用例第"+(i+1)+"步解析出错啦！";
		    		break;
		    	}
		    	expectedresults = casescript.get("ExpectedResults").toString();    //预期结果
		    	if(expectedresults.indexOf("&quot;")>-1||expectedresults.indexOf("&#39;")>-1){                             //页面转义字符转换
		    		expectedresults = expectedresults.replaceAll("&quot;", "\"");
		    		expectedresults = expectedresults.replaceAll("&#39;", "\'");
		    	}
		    	//判断方法是否带参数
		    	if(casescript.size()>4){
			    	//获取传入参数，放入对象中
			    	getParameterValues = new Object[casescript.size()-4];    //初始化参数对象个数
			    	for(int j=0;j<casescript.size()-4;j++){		    		
			    		if(casescript.get("FunctionParams"+(j+1))==null){
			    			k = 1;
			    			break;
			    		}
			    		if(casescript.get("FunctionParams"+(j+1)).indexOf("@")>-1
			    				&&casescript.get("FunctionParams"+(j+1)).indexOf("@@")<0){                        //如果存在传参，进行处理
			    			int keyexistidentity = 0;
			    			//取单个参数中引用变量次数
			    			int sumvariable = DBOperation.sumString(casescript.get("FunctionParams"+(j+1)), "@");     
			    			String uservariable = null;
			    			String uservariable1 = null;
			    			String uservariable2 = null;
			    			
			    			if(sumvariable==1){
			    				uservariable = casescript.get("FunctionParams"+(j+1)).substring(
				    					casescript.get("FunctionParams"+(j+1)).indexOf("@")+1);
			    			}else if(sumvariable==2){       //单个参数中引用第二个变量
			    				uservariable = casescript.get("FunctionParams"+(j+1)).substring(casescript.get("FunctionParams"+(j+1)).indexOf("@")+1,
			    						casescript.get("FunctionParams"+(j+1)).lastIndexOf("@"));
			    				uservariable1 = casescript.get("FunctionParams"+(j+1)).substring(
				    					casescript.get("FunctionParams"+(j+1)).lastIndexOf("@")+1);
			    			}else if(sumvariable==3){
			    				String temp = casescript.get("FunctionParams"+(j+1)).substring(casescript.get("FunctionParams"+(j+1)).indexOf("@")+1,
			    						casescript.get("FunctionParams"+(j+1)).lastIndexOf("@"));
		    					uservariable1 = temp.substring(temp.indexOf("@")+1);
		    					uservariable2 = casescript.get("FunctionParams"+(j+1)).substring(
				    					casescript.get("FunctionParams"+(j+1)).lastIndexOf("@")+1);
		    					uservariable = casescript.get("FunctionParams"+(j+1)).substring(casescript.get("FunctionParams"+(j+1)).indexOf("@")+1,
				    					casescript.get("FunctionParams"+(j+1)).indexOf(uservariable1)-1);
		    				}else{
		    					luckyclient.publicclass.LogUtil.APP.error("你好像在一个参数中引用了超过3个以上的变量哦！我处理不过来啦！");
			    				caselog.UpdateCaseLogDetail(testCaseExternalId, tastid, "你好像在一个参数中引用了超过2个以上的变量哦！我处理不过来啦！", "error",String.valueOf(i+1));
			    			}
			    			Iterator keys = variable.keySet().iterator();
			    			String key = null;
			    			while(keys.hasNext()){
			    				key = (String)keys.next();
			    				if(uservariable.indexOf(key)>-1){
			    					keyexistidentity = 1;
			    					uservariable = key;
						    		break;
			    				}
			    			}
			    			if(sumvariable==2||sumvariable==3){            //处理第二个传参
			    				keys = variable.keySet().iterator();
				    			while(keys.hasNext()){
				    				keyexistidentity = 0;
				    				key = (String)keys.next();
				    				if(uservariable.indexOf(key)>-1){
				    					keyexistidentity = 1;
				    					uservariable1 = key;
							    		break;
				    				}
				    			}
			    			}
			    			if(sumvariable==3){            //处理第三个传参
			    				keys = variable.keySet().iterator();
				    			while(keys.hasNext()){
				    				keyexistidentity = 0;
				    				key = (String)keys.next();
				    				if(uservariable.indexOf(key)>-1){
				    					keyexistidentity = 1;
				    					uservariable2 = key;
							    		break;
				    				}
				    			}
			    			}
			    			if(keyexistidentity == 1){
					    		//拼装参数（传参+原有字符串）
					    		String ParameterValues =casescript.get("FunctionParams"+(j+1)).replaceAll("@"+uservariable, variable.get(uservariable).toString());
					    		//处理第二个传参
					    		if(sumvariable==2||sumvariable==3){
					    			ParameterValues = ParameterValues.replaceAll("@"+uservariable1, variable.get(uservariable1).toString());
					    		}
					    		//处理第三个传参
					    		if(sumvariable==3){
					    			ParameterValues = ParameterValues.replaceAll("@"+uservariable2, variable.get(uservariable2).toString());
					    		}
						    	if(ParameterValues.indexOf("&quot;")>-1 || ParameterValues.indexOf("&#39;")>-1){         //页面转义字符转换
						    		ParameterValues = ParameterValues.replaceAll("&quot;", "\"");
						    		ParameterValues = ParameterValues.replaceAll("&#39;", "\'");
						    	}
					    		luckyclient.publicclass.LogUtil.APP.info("解析包名："+packagename+" 方法名："+functionname
					    				+" 第"+(j+1)+"个参数："+ParameterValues);
					    		caselog.UpdateCaseLogDetail(testCaseExternalId, tastid, "解析包名："+packagename+" 方法名："+functionname
					    				+" 第"+(j+1)+"个参数："+ParameterValues, "info",String.valueOf(i+1));
					    		getParameterValues[j] = ParameterValues;
			    			}else{
			    				luckyclient.publicclass.LogUtil.APP.error("没有找到你要的变量哦，再找下吧！第一个变量名称是："+uservariable+"，第"
			    						+ "二个变量名称是："+uservariable1+"，第三个变量名称是："+uservariable2);
			    				caselog.UpdateCaseLogDetail(testCaseExternalId, tastid, "没有找到你要的变量哦，再找下吧！第二变量名称是："+uservariable+"，第"
			    						+ "二个变量名称是："+uservariable1+"，第三个变量名称是："+uservariable2, "error",String.valueOf(i+1));
			    			}

			    		}else{
				    		String ParameterValues1 = casescript.get("FunctionParams"+(j+1));
					    	if(ParameterValues1.indexOf("&quot;")>-1 || ParameterValues1.indexOf("&#39;")>-1 || ParameterValues1.indexOf("@@")>-1){         //页面转义字符转换
					    		ParameterValues1 = ParameterValues1.replaceAll("&quot;", "\"");
					    		ParameterValues1 = ParameterValues1.replaceAll("&#39;", "\'");
					    		ParameterValues1 = ParameterValues1.replaceAll("@@", "@");
					    	}
				    		luckyclient.publicclass.LogUtil.APP.info("解析包名："+packagename+" 方法名："+functionname
				    				+" 第"+(j+1)+"个参数："+ParameterValues1);
				    		caselog.UpdateCaseLogDetail(testCaseExternalId, tastid, "解析包名："+packagename+" 方法名："+functionname
				    				+" 第"+(j+1)+"个参数："+ParameterValues1, "info",String.valueOf(i+1));
				    		getParameterValues[j] = ParameterValues1;
			    		}
			    	}
		    	}else{
		    		getParameterValues = null;
		    	}
		    	//调用动态方法，执行测试用例
			    try{
			    	luckyclient.publicclass.LogUtil.APP.info("开始调用方法："+functionname+" .....");
		    		caselog.UpdateCaseLogDetail(testCaseExternalId, tastid, "开始调用方法："+functionname+" .....", "info",String.valueOf(i+1));
			    	if(expectedresults.length()>2 && expectedresults.substring(0, 2).indexOf("$=")>-1){                             //把预期结果前两个字符判断是否是要把结果存入变量
			    		String ExpectedResultVariable = casescript.get("ExpectedResults").toString().substring(2);
			    		String temptestnote = InvokeMethod.CallCase(packagename,functionname,getParameterValues,0,"");
			    		variable.put(ExpectedResultVariable, temptestnote);
			    	}else if(expectedresults.length()>2 && expectedresults.substring(0, 2).indexOf("%=")>-1){                     //把预期结果与测试结果做模糊匹配
				    	testnote = InvokeMethod.CallCase(packagename,functionname,getParameterValues,0,"");
				    	if(testnote.indexOf(expectedresults.substring(2))>-1){
				    		setresult = 0;
				    		luckyclient.publicclass.LogUtil.APP.info("用例执行结果是："+testnote+"，与预期结果匹配成功！");
				    		caselog.UpdateCaseLogDetail(testCaseExternalId, tastid, "用例执行结果是："+testnote+"，与预期结果匹配成功！", "info",String.valueOf(i+1));
				    	}else{
				    		setresult = 1;
				    		luckyclient.publicclass.LogUtil.APP.error("用例第"+(i+1)+"步执行结果与预期结果匹配失败！");
				    		caselog.UpdateCaseLogDetail(testCaseExternalId, tastid, "用例第"+(i+1)+"步执行结果与预期结果匹配失败！预期结果："+expectedresults+"      测试结果："+testnote, "error",String.valueOf(i+1));
				    		luckyclient.publicclass.LogUtil.APP.error("预期结果："+expectedresults+"      测试结果："+testnote);
				    		testnote = "用例第"+(i+1)+"步执行结果与预期结果匹配失败！";
				    		break;        //某一步骤失败后，此条用例置为失败退出
				    	}
			    	}else{                                                                                                                          //把预期结果与测试结果做精确匹配
				    	testnote = InvokeMethod.CallCase(packagename,functionname,getParameterValues,0,"");
				    	if(expectedresults.equals(testnote)){
				    		setresult = 0;
				    		luckyclient.publicclass.LogUtil.APP.info("用例执行结果是："+testnote+"，与预期结果匹配成功！");
				    		caselog.UpdateCaseLogDetail(testCaseExternalId, tastid, "用例执行结果是："+testnote+"，与预期结果匹配成功！", "info",String.valueOf(i+1));
				    	}else{
				    		setresult = 1;
				    		luckyclient.publicclass.LogUtil.APP.error("用例第"+(i+1)+"步执行结果与预期结果匹配失败！");
				    		caselog.UpdateCaseLogDetail(testCaseExternalId, tastid, "用例第"+(i+1)+"步执行结果与预期结果匹配失败！预期结果："
				    		+expectedresults+"      测试结果："+testnote, "error",String.valueOf(i+1));
				    		luckyclient.publicclass.LogUtil.APP.error("预期结果："+expectedresults+"      测试结果："+testnote);
				    		testnote = "用例第"+(i+1)+"步执行结果与预期结果匹配失败！预期结果："+expectedresults+"      测试结果："+testnote;
				    		break;        //某一步骤失败后，此条用例置为失败退出
				    	}
			    	}
			    	int waitsec = Integer.parseInt(casescript.get("StepWait").toString());   //获取步骤间等待时间
			    	if(waitsec!=0){
			    		Thread.sleep(waitsec*1000);
			    	}
			    }catch(Exception e){
			    	luckyclient.publicclass.LogUtil.APP.error("调用方法过程出错，方法名："+functionname+" 请重新检查脚本方法名称以及参数！");
			    	caselog.UpdateCaseLogDetail(testCaseExternalId, tastid, "调用方法过程出错，方法名："+functionname+" 请重新检查脚本方法名称以及参数！", "error",String.valueOf(i+1));
					luckyclient.publicclass.LogUtil.APP.error(e,e);
					testnote = "CallCase调用出错！";
					setresult = 1;
					e.printStackTrace();
	    			break;
			    }			    
		    }
		    variable.clear();               //清空传参MAP
		    //如果调用方法过程中未出错，进入设置测试结果流程
		    if(testnote.indexOf("CallCase调用出错！")<=-1&&testnote.indexOf("解析出错啦！")<=-1){
		    	luckyclient.publicclass.LogUtil.APP.info("用例 "+testCaseExternalId+"解析成功，并成功调用用例中方法，请继续查看执行结果！"); 
		    	caselog.UpdateCaseLogDetail(testCaseExternalId, tastid, "解析成功，并成功调用用例中方法，请继续查看执行结果！", "info","SETCASERESULT...");
		    	//TCResult = TestCaseApi.setTCResult(projectname,testCaseExternalId, testnote, version,setresult);	
		    	caselog.UpdateCaseDetail(tastid, testCaseExternalId, setresult);
		     }else{
		    	 setresult = 1;
		    	 luckyclient.publicclass.LogUtil.APP.error("用例 "+testCaseExternalId+"解析或是调用步骤中的方法出错！"); 
		    	 caselog.UpdateCaseLogDetail(testCaseExternalId, tastid, "解析或是调用步骤中的方法出错！", "error","SETCASERESULT...");
		    	// TCResult = TestCaseApi.setTCResult(projectname,testCaseExternalId, testnote, version,2);
		    	 caselog.UpdateCaseDetail(tastid, testCaseExternalId, 2);
		     }
		    if(0==setresult){
		    	luckyclient.publicclass.LogUtil.APP.info("用例 "+testCaseExternalId+"步骤全部执行成功！"); 
		    	caselog.UpdateCaseLogDetail(testCaseExternalId, tastid, "步骤全部执行成功！", "info","EXECUTECASESUC...");
		    }else{
		    	luckyclient.publicclass.LogUtil.APP.error("用例 "+testCaseExternalId+"在执行过程中失败，请检查日志！"); 
		    	 caselog.UpdateCaseLogDetail(testCaseExternalId, tastid, "在执行过程中失败，请检查日志！", "error","EXECUTECASESUC...");
		    }
		}else{
			luckyclient.publicclass.LogUtil.APP.error("用例 "+testCaseExternalId+"不是一个自动化的用例哦！先去把它设置一下吧！"); 
			 caselog.UpdateCaseLogDetail(testCaseExternalId, tastid, "不是一个自动化的用例哦！先去把它设置一下吧！", "error","EXECUTECASEFAIL...");
		}
		LogOperation.UpdateTastdetail(tastid, 0);
	}

	
	/**
	 * @param 项目名
	 * @param 用例编号
	 * @param 用例版本号
	 * 用于在UI的测试过程中，需要调用接口的测试用例
	 */
	@SuppressWarnings("static-access")
	protected static String OneCaseExecuteForWebDriver(String testCaseExternalId,int version,String tastid){
		Map<String,String> variable = new HashMap<String,String>();
		String packagename =null;
		String functionname = null;
		String expectedresults = null;
		Integer setresult = null;
		Object[] getParameterValues = null;
    	String testnote = null;
		int k = 0;
		TestCase testcaseob = TestCaseApi.getTestCaseByExternalId(testCaseExternalId, version);
		if(testcaseob.getExecutionType()==ExecutionType.AUTOMATED){
		    //进入循环，解析用例所有步骤
		    for(int i=0;i<testcaseob.getSteps().size();i++){		    	
		    	Map<String,String> casescript = InterfaceAnalyticTestLinkCase.AnalyticCaseStep(testcaseob, i+1,tastid);    //解析单个步骤中的脚本
		    	packagename = casescript.get("PackageName").toString();
		    	functionname = casescript.get("FunctionName").toString();
		    	//用例名称解析出现异常或是单个步骤参数解析异常
		    	if(functionname.indexOf("解析异常")>-1||k==1){
		    		k=0;
		    		testnote = "用例第"+(i+1)+"步解析出错啦！";
		    		break;
		    	}
		    	expectedresults = casescript.get("ExpectedResults").toString();    //预期结果
		    	if(expectedresults.indexOf("&quot;")>-1||expectedresults.indexOf("&#39;")>-1){                             //页面转义字符转换
		    		expectedresults = expectedresults.replaceAll("&quot;", "\"");
		    		expectedresults = expectedresults.replaceAll("&#39;", "\'");
		    	}
		    	//判断方法是否带参数
		    	if(casescript.size()>4){
			    	//获取传入参数，放入对象中
			    	getParameterValues = new Object[casescript.size()-4];    //初始化参数对象个数
			    	for(int j=0;j<casescript.size()-4;j++){		    		
			    		if(casescript.get("FunctionParams"+(j+1))==null){
			    			k = 1;
			    			break;
			    		}
			    		if(casescript.get("FunctionParams"+(j+1)).indexOf("@")>-1
			    				&&casescript.get("FunctionParams"+(j+1)).indexOf("@@")<0){                        //如果存在传参，进行处理
			    			int keyexistidentity = 0;
			    			//取单个参数中引用变量次数
			    			int sumvariable = DBOperation.sumString(casescript.get("FunctionParams"+(j+1)), "@");     
			    			String uservariable = null;
			    			String uservariable1 = null;
			    			String uservariable2 = null;
			    			
			    			if(sumvariable==1){
			    				uservariable = casescript.get("FunctionParams"+(j+1)).substring(
				    					casescript.get("FunctionParams"+(j+1)).indexOf("@")+1);
			    			}else if(sumvariable==2){       //单个参数中引用第二个变量
			    				uservariable = casescript.get("FunctionParams"+(j+1)).substring(casescript.get("FunctionParams"+(j+1)).indexOf("@")+1,
			    						casescript.get("FunctionParams"+(j+1)).lastIndexOf("@"));
			    				uservariable1 = casescript.get("FunctionParams"+(j+1)).substring(
				    					casescript.get("FunctionParams"+(j+1)).lastIndexOf("@")+1);
			    			}else if(sumvariable==3){
			    				String temp = casescript.get("FunctionParams"+(j+1)).substring(casescript.get("FunctionParams"+(j+1)).indexOf("@")+1,
			    						casescript.get("FunctionParams"+(j+1)).lastIndexOf("@"));
		    					uservariable1 = temp.substring(temp.indexOf("@")+1);
		    					uservariable2 = casescript.get("FunctionParams"+(j+1)).substring(
				    					casescript.get("FunctionParams"+(j+1)).lastIndexOf("@")+1);
		    					uservariable = casescript.get("FunctionParams"+(j+1)).substring(casescript.get("FunctionParams"+(j+1)).indexOf("@")+1,
				    					casescript.get("FunctionParams"+(j+1)).indexOf(uservariable1)-1);
		    				}else{
			    				luckyclient.publicclass.LogUtil.APP.error("你好像在一个参数中引用了超过3个以上的变量哦！我处理不过来啦！");
			    			}
			    			Iterator keys = variable.keySet().iterator();
			    			String key = null;
			    			while(keys.hasNext()){
			    				key = (String)keys.next();
			    				if(uservariable.indexOf(key)>-1){
			    					keyexistidentity = 1;
			    					uservariable = key;
						    		break;
			    				}
			    			}
			    			if(sumvariable==2||sumvariable==3){            //处理第二个传参
			    				keys = variable.keySet().iterator();
				    			while(keys.hasNext()){
				    				keyexistidentity = 0;
				    				key = (String)keys.next();
				    				if(uservariable1.indexOf(key)>-1){
				    					keyexistidentity = 1;
				    					uservariable1 = key;
							    		break;
				    				}
				    			}
			    			}
			    			if(sumvariable==3){            //处理第三个传参
			    				keys = variable.keySet().iterator();
				    			while(keys.hasNext()){
				    				keyexistidentity = 0;
				    				key = (String)keys.next();
				    				if(uservariable2.indexOf(key)>-1){
				    					keyexistidentity = 1;
				    					uservariable2 = key;
							    		break;
				    				}
				    			}
			    			}
			    			if(keyexistidentity == 1){
					    		//拼装参数（传参+原有字符串）
					    		String ParameterValues =casescript.get("FunctionParams"+(j+1)).replaceAll("@"+uservariable, variable.get(uservariable).toString());
					    		//处理第二个传参
					    		if(sumvariable==2||sumvariable==3){
					    			ParameterValues = ParameterValues.replaceAll("@"+uservariable1, variable.get(uservariable1).toString());
					    		}
					    		//处理第三个传参
					    		if(sumvariable==3){
					    			ParameterValues = ParameterValues.replaceAll("@"+uservariable2, variable.get(uservariable2).toString());
					    		}
						    	if(ParameterValues.indexOf("&quot;")>-1 || ParameterValues.indexOf("&#39;")>-1){         //页面转义字符转换
						    		ParameterValues = ParameterValues.replaceAll("&quot;", "\"");
						    		ParameterValues = ParameterValues.replaceAll("&#39;", "\'");
						    	}
					    		luckyclient.publicclass.LogUtil.APP.info("解析包名："+packagename+" 方法名："+functionname
					    				+" 第"+(j+1)+"个参数："+ParameterValues);
					    		getParameterValues[j] = ParameterValues;
			    			}else{
			    				luckyclient.publicclass.LogUtil.APP.error("没有找到你要的变量哦，再找下吧！第一个变量名称是："+uservariable+"，第"
			    						+ "二个变量名称是："+uservariable1+"，第三个变量名称是："+uservariable2);			    		
			    			}

			    		}else{
				    		String ParameterValues1 = casescript.get("FunctionParams"+(j+1));
					    	if(ParameterValues1.indexOf("&quot;")>-1 || ParameterValues1.indexOf("&#39;")>-1 || ParameterValues1.indexOf("@@")>-1){         //页面转义字符转换
					    		ParameterValues1 = ParameterValues1.replaceAll("&quot;", "\"");
					    		ParameterValues1 = ParameterValues1.replaceAll("&#39;", "\'");
					    		ParameterValues1 = ParameterValues1.replaceAll("@@", "@");
					    	}
				    		luckyclient.publicclass.LogUtil.APP.info("解析包名："+packagename+" 方法名："+functionname
				    				+" 第"+(j+1)+"个参数："+ParameterValues1);				    		
				    		getParameterValues[j] = ParameterValues1;
			    		}
			    	}
		    	}else{
		    		getParameterValues = null;
		    	}
		    	//调用动态方法，执行测试用例
			    try{
			    	luckyclient.publicclass.LogUtil.APP.info("开始调用方法："+functionname+" .....");
		    		if(expectedresults.length()>2 && expectedresults.substring(0, 2).indexOf("$=")>-1){                             //把预期结果前两个字符判断是否是要把结果存入变量
			    		String ExpectedResultVariable = casescript.get("ExpectedResults").toString().substring(2);
			    		String temptestnote = InvokeMethod.CallCase(packagename,functionname,getParameterValues,0,"");
			    		variable.put(ExpectedResultVariable, temptestnote);
			    	}else if(expectedresults.length()>2 && expectedresults.substring(0, 2).indexOf("%=")>-1){                     //把预期结果与测试结果做模糊匹配
				    	testnote = InvokeMethod.CallCase(packagename,functionname,getParameterValues,0,"");
				    	if(testnote.indexOf(expectedresults.substring(2))>-1){
				    		setresult = 0;
				    		luckyclient.publicclass.LogUtil.APP.info("用例执行结果是："+testnote+"，与预期结果匹配成功！");
				    	}else{
				    		setresult = 1;
				    		luckyclient.publicclass.LogUtil.APP.error("用例第"+(i+1)+"步执行结果与预期结果匹配失败！");
				    		luckyclient.publicclass.LogUtil.APP.error("预期结果："+expectedresults+"      测试结果："+testnote);
				    		testnote = "用例第"+(i+1)+"步执行结果与预期结果匹配失败！";
				    		break;        //某一步骤失败后，此条用例置为失败退出
				    	}
			    	}else{                                                                                                                          //把预期结果与测试结果做精确匹配
				    	testnote = InvokeMethod.CallCase(packagename,functionname,getParameterValues,0,"");
				    	if("".equals(expectedresults)||testnote.equals(expectedresults)){
				    		setresult = 0;
				    		luckyclient.publicclass.LogUtil.APP.info("用例执行结果是："+testnote+"，与预期结果匹配成功！");
				    	}else{
				    		setresult = 1;
				    		luckyclient.publicclass.LogUtil.APP.error("用例第"+(i+1)+"步执行结果与预期结果匹配失败！");
				    		luckyclient.publicclass.LogUtil.APP.error("预期结果："+expectedresults+"      测试结果："+testnote);
				    		testnote = "用例第"+(i+1)+"步执行结果与预期结果匹配失败！预期结果："+expectedresults+"      测试结果："+testnote;
				    		break;        //某一步骤失败后，此条用例置为失败退出
				    	}
			    	}
			    	int waitsec = Integer.parseInt(casescript.get("StepWait").toString());   //获取步骤间等待时间
			    	if(waitsec!=0){
			    		Thread.sleep(waitsec*1000);
			    	}
			    }catch(Exception e){
			    	luckyclient.publicclass.LogUtil.APP.error("调用方法过程出错，方法名："+functionname+" 请重新检查脚本方法名称以及参数！");
			    	luckyclient.publicclass.LogUtil.APP.error(e,e);
					testnote = "CallCase调用出错！";
					setresult = 1;
					e.printStackTrace();
	    			break;
			    }			    
		    }
		    variable.clear();               //清空传参MAP
		    if(0==setresult){
		    	luckyclient.publicclass.LogUtil.APP.info("用例 "+testcaseob.getFullExternalId()+"步骤全部执行成功！"); 
		    }else{
		    	luckyclient.publicclass.LogUtil.APP.error("用例 "+testcaseob.getFullExternalId()+"在执行过程中失败，请检查日志！"); 
		    }
		}else{
			luckyclient.publicclass.LogUtil.APP.error("用例 "+testcaseob.getFullExternalId()+"不是一个自动化的用例哦！先去把它设置一下吧！");			
		}
		return testnote;
	}
	
}
