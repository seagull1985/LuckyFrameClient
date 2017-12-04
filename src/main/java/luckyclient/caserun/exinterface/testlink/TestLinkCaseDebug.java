package luckyclient.caserun.exinterface.testlink;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionType;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import luckyclient.publicclass.DBOperation;
import luckyclient.publicclass.InvokeMethod;
import luckyclient.testlinkapi.InterfaceAnalyticTestLinkCase;
import luckyclient.testlinkapi.TestCaseApi;
/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 * @ClassName: TestCaseDebug 
 * @Description: 针对自动化用例在编写过程中，对用例脚本进行调试
 * @author： seagull
 * @date 2014年8月24日 上午9:29:40  
 * 
 */
public class TestLinkCaseDebug{

	/**
	 * @param 项目名
	 * @param 用例编号
	 * @param 用例版本号
	 * 用于在testlink上配置好用例参数后，做单条用例调试
	 */
	public static void oneCaseDebug(String projectname,String testCaseExternalId,int version){
		Map<String,String> variable = new HashMap<String,String>(0);
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
		    	Map<String,String> casescript = InterfaceAnalyticTestLinkCase.analyticCaseStep(testcaseob, i+1,"888888",null);    
		    	packagename = casescript.get("PackageName").toString();
		    	functionname = casescript.get("FunctionName").toString();
		    	//用例名称解析出现异常或是单个步骤参数解析异常
		    	if(functionname.indexOf("解析异常")>-1||k==1){
		    		k=0;
		    		testnote = "用例第"+(i+1)+"步解析出错啦！";
		    		break;
		    	}
		    	expectedresults = casescript.get("ExpectedResults").toString();    
		    	if(expectedresults.indexOf("&quot;")>-1||expectedresults.indexOf("&#39;")>-1){                             
		    		expectedresults = expectedresults.replaceAll("&quot;", "\"");
		    		expectedresults = expectedresults.replaceAll("&#39;", "\'");
		    	}
		    	//判断方法是否带参数
		    	if(casescript.size()>4){
			    	//获取传入参数，放入对象中
			    	getParameterValues = new Object[casescript.size()-4];    
			    	for(int j=0;j<casescript.size()-4;j++){		    		
			    		if(casescript.get("FunctionParams"+(j+1))==null){
			    			k = 1;
			    			break;
			    		}
			    		if(casescript.get("FunctionParams"+(j+1)).indexOf("@")>-1
			    				&&casescript.get("FunctionParams"+(j+1)).indexOf("@@")<0){                       
			    			int keyexistidentity = 0;
			    			//取单个参数中引用变量次数
			    			int sumvariable = DBOperation.sumString(casescript.get("FunctionParams"+(j+1)), "@");     
			    			String uservariable = null;
			    			String uservariable1 = null;
			    			String uservariable2 = null;
			    			
			    			if(sumvariable==1){
			    				uservariable = casescript.get("FunctionParams"+(j+1)).substring(
				    					casescript.get("FunctionParams"+(j+1)).indexOf("@")+1);
			    			}else if(sumvariable==2){      
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
			    			@SuppressWarnings("rawtypes")
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
			    			if(sumvariable==2||sumvariable==3){           
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
			    			if(sumvariable==3){           
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
					    		String parameterValues =casescript.get("FunctionParams"+(j+1)).replaceAll("@"+uservariable, variable.get(uservariable).toString());
					    		//处理第二个传参
					    		if(sumvariable==2||sumvariable==3){
					    			parameterValues = parameterValues.replaceAll("@"+uservariable1, variable.get(uservariable1).toString());
					    		}
					    		//处理第三个传参
					    		if(sumvariable==3){
					    			parameterValues = parameterValues.replaceAll("@"+uservariable2, variable.get(uservariable2).toString());
					    		}
						    	if(parameterValues.indexOf("&quot;")>-1 || parameterValues.indexOf("&#39;")>-1){         
						    		parameterValues = parameterValues.replaceAll("&quot;", "\"");
						    		parameterValues = parameterValues.replaceAll("&#39;", "\'");
						    	}
					    		luckyclient.publicclass.LogUtil.APP.info("解析包名："+packagename+" 方法名："+functionname
					    				+" 第"+(j+1)+"个参数："+parameterValues);
					    		getParameterValues[j] = parameterValues;
			    			}else{
			    				luckyclient.publicclass.LogUtil.APP.error("没有找到你要的变量哦，再找下吧！第一个变量名称是："+uservariable+"，第"
			    						+ "二个变量名称是："+uservariable1+"，第三个变量名称是："+uservariable2);
			    			}

			    		}else{
				    		String parameterValues1 = casescript.get("FunctionParams"+(j+1));
					    	if(parameterValues1.indexOf("&quot;")>-1 || parameterValues1.indexOf("&#39;")>-1 || parameterValues1.indexOf("@@")>-1){        
					    		parameterValues1 = parameterValues1.replaceAll("&quot;", "\"");
					    		parameterValues1 = parameterValues1.replaceAll("&#39;", "\'");
					    		parameterValues1 = parameterValues1.replaceAll("@@", "@");
					    	}
				    		luckyclient.publicclass.LogUtil.APP.info("解析包名："+packagename+" 方法名："+functionname
				    				+" 第"+(j+1)+"个参数："+parameterValues1);
				    		getParameterValues[j] = parameterValues1;
			    		}
			    	}
		    	}else{
		    		getParameterValues = null;
		    	}
		    	//调用动态方法，执行测试用例
			    try{
			    	luckyclient.publicclass.LogUtil.APP.info("开始调用方法："+functionname+" .....");
			    	if(expectedresults.length()>2 && expectedresults.substring(0, 2).indexOf("$=")>-1){                            
			    		String expectedResultVariable = casescript.get("ExpectedResults").toString().substring(2);
			    		String temptestnote = InvokeMethod.callCase(packagename,functionname,getParameterValues,0,"");
			    		variable.put(expectedResultVariable, temptestnote);
			    	}else if(expectedresults.length()>2 && expectedresults.substring(0, 2).indexOf("%=")>-1){                    
				    	testnote = InvokeMethod.callCase(packagename,functionname,getParameterValues,0,"");
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
				    	testnote = InvokeMethod.callCase(packagename,functionname,getParameterValues,0,"");
				    	if(expectedresults.equals(testnote)){
				    		setresult = 0;
				    		luckyclient.publicclass.LogUtil.APP.info("用例执行结果是："+testnote+"，与预期结果匹配成功！");
				    	}else{
				    		setresult = 1;
				    		luckyclient.publicclass.LogUtil.APP.error("用例第"+(i+1)+"步执行结果与预期结果匹配失败！");
				    		luckyclient.publicclass.LogUtil.APP.error("预期结果："+expectedresults+"      测试结果："+testnote);
				    		testnote = "用例第"+(i+1)+"步执行结果与预期结果匹配失败！";
				    		break;        //某一步骤失败后，此条用例置为失败退出
				    	}
			    	}
			    	int waitsec = Integer.parseInt(casescript.get("StepWait").toString()); 
			    	if(waitsec!=0){
			    		Thread.sleep(waitsec*1000);
			    	}
			    }catch(Exception e){
			    	setresult = 1;
			    	luckyclient.publicclass.LogUtil.APP.error("调用方法过程出错，方法名："+functionname+" 请重新检查脚本方法名称以及参数！");
					luckyclient.publicclass.LogUtil.APP.error(e.getMessage(),e);
					testnote = "CallCase调用出错！";
					e.printStackTrace();
	    			break;
			    }			    
		    }
		    variable.clear();               //清空传参MAP
		    //如果调用方法过程中未出错，进入设置测试结果流程
		    if(testnote.indexOf("CallCase调用出错！")<=-1&&testnote.indexOf("解析出错啦！")<=-1){
		    	luckyclient.publicclass.LogUtil.APP.info("用例 "+testCaseExternalId+"解析成功，并成功调用用例中方法，请继续查看执行结果！");    	
		     }else{
		    	 luckyclient.publicclass.LogUtil.APP.error("用例 "+testCaseExternalId+"解析或是调用步骤中的方法出错！"); 
		     }
		    if(setresult == 0){
		    	luckyclient.publicclass.LogUtil.APP.info("用例 "+testCaseExternalId+"步骤全部执行成功！"); 
		    }else{
		    	luckyclient.publicclass.LogUtil.APP.error("用例 "+testCaseExternalId+"在执行过程中失败，请检查日志！"); 
		    }
		}else{
			luckyclient.publicclass.LogUtil.APP.error("用例 "+testCaseExternalId+"不是一个自动化的用例哦！先去把它设置一下吧！"); 
		}
	}
	
	/**
	 * @param 项目名
	 * @param 用例编号
	 * @param 用例版本号
	 * 用于在testlink上配置好用例参数后，做多条用例串行调试
	 */
	public static void moreCaseDebug(String projectname,Map<String,Integer> addtestcase){
		System.out.println(addtestcase.size());
		@SuppressWarnings("rawtypes")
		Iterator it=addtestcase.entrySet().iterator();
		while(it.hasNext()){
		    @SuppressWarnings("rawtypes")
			Map.Entry entry=(Map.Entry)it.next();
		    String testCaseExternalId = (String)entry.getKey();
		    Integer version = (Integer)entry.getValue();
		    try{
		    luckyclient.publicclass.LogUtil.APP.info("开始调用方法，项目名："+projectname+"，用例编号："+testCaseExternalId+"，用例版本："+version); 
		    oneCaseDebug(projectname,testCaseExternalId,version);
		    }catch(Exception e){
		    	continue;
		    }
		}
	}

}
