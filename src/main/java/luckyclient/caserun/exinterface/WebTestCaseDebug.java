package luckyclient.caserun.exinterface;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import luckyclient.caserun.exinterface.AnalyticSteps.InterfaceAnalyticCase;
import luckyclient.planapi.api.GetServerAPI;
import luckyclient.planapi.entity.ProjectCase;
import luckyclient.planapi.entity.ProjectCasesteps;
import luckyclient.publicclass.DBOperation;
import luckyclient.publicclass.InvokeMethod;
/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 此测试框架主要采用testlink做分层框架，负责数据驱动以及用例管理部分，有任何疑问欢迎联系作者讨论。
 * QQ:24163551 seagull1985
 * =================================================================
 * @ClassName: WebTestCaseDebug 
 * @Description: 提供Web端调试接口
 * @author： seagull
 * @date 2017年9月2日 上午9:29:40  
 * 
 */
public class WebTestCaseDebug{

	/**
	 * @param 执行人
	 * @param 用例编号
	 * 用于在WEB页面上调试用例时提供的接口
	 */
	public static void OneCaseDebug(String sign,String executor){
		Map<String,String> variable = new HashMap<String,String>();
		String packagename =null;
		String functionname = null;
		String expectedresults = null;
		Integer setresult = null;
		Object[] getParameterValues = null;
    	String testnote = null;
		int k = 0;
		ProjectCase testcaseob = GetServerAPI.cgetCaseBysign(sign);
		List<ProjectCasesteps> steps=GetServerAPI.getStepsbycaseid(testcaseob.getId());
		    //进入循环，解析用例所有步骤
		    for(int i=0;i<steps.size();i++){		    	
		    	Map<String,String> casescript = InterfaceAnalyticCase.AnalyticCaseStep(testcaseob, steps.get(i),"888888");    //解析单个步骤中的脚本
		    	try{
			    	packagename = casescript.get("PackageName").toString();
			    	functionname = casescript.get("FunctionName").toString();
		    	}catch(Exception e){
		    		k=0;
		    		GetServerAPI.cPostDebugLog(sign, executor, "ERROR", "解析包名或是方法名失败，请检查！");
		    		e.printStackTrace();
		    		break;        //某一步骤失败后，此条用例置为失败退出
		    	}
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
		    					GetServerAPI.cPostDebugLog(sign, executor, "WARNING", "你好像在一个参数中引用了超过3个以上的变量哦！我处理不过来啦！");
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
					    		GetServerAPI.cPostDebugLog(sign, executor, "INFO", "解析包名："+packagename+" 方法名："+functionname
					    				+" 第"+(j+1)+"个参数："+ParameterValues);
					    		getParameterValues[j] = ParameterValues;
			    			}else{
			    				GetServerAPI.cPostDebugLog(sign, executor, "WARNING", "没有找到你要的变量哦，再找下吧！第一个变量名称是："+uservariable+"，第"
			    						+ "二个变量名称是："+uservariable1+"，第三个变量名称是："+uservariable2);
			    			}

			    		}else{
				    		String ParameterValues1 = casescript.get("FunctionParams"+(j+1));
					    	if(ParameterValues1.indexOf("&quot;")>-1 || ParameterValues1.indexOf("&#39;")>-1 || ParameterValues1.indexOf("@@")>-1){         //页面转义字符转换
					    		ParameterValues1 = ParameterValues1.replaceAll("&quot;", "\"");
					    		ParameterValues1 = ParameterValues1.replaceAll("&#39;", "\'");
					    		ParameterValues1 = ParameterValues1.replaceAll("@@", "@");
					    	}
		    				GetServerAPI.cPostDebugLog(sign, executor, "INFO", "解析包名："+packagename+" 方法名："+functionname
				    				+" 第"+(j+1)+"个参数："+ParameterValues1);
				    		getParameterValues[j] = ParameterValues1;
			    		}
			    	}
		    	}else{
		    		getParameterValues = null;
		    	}
		    	//调用动态方法，执行测试用例
			    try{
    				GetServerAPI.cPostDebugLog(sign, executor, "INFO", "开始调用方法："+functionname+" .....");
			    	if(expectedresults.length()>2 && expectedresults.substring(0, 2).indexOf("$=")>-1){                             //把预期结果前两个字符判断是否是要把结果存入变量
			    		String ExpectedResultVariable = casescript.get("ExpectedResults").toString().substring(2);
			    		String temptestnote = InvokeMethod.CallCase(packagename,functionname,getParameterValues,steps.get(i).getSteptype(),steps.get(i).getAction());
			    		variable.put(ExpectedResultVariable, temptestnote);
			    		GetServerAPI.cPostDebugLog(sign, executor, "INFO", "赋值变量【"+expectedresults.substring(2, expectedresults.length())+"】： "+temptestnote);
			    	}else if(expectedresults.length()>2 && expectedresults.substring(0, 2).indexOf("%=")>-1){                     //把预期结果与测试结果做模糊匹配
				    	testnote = InvokeMethod.CallCase(packagename,functionname,getParameterValues,steps.get(i).getSteptype(),steps.get(i).getAction());
				    	if(testnote.indexOf(expectedresults.substring(2))>-1){
				    		setresult = 0;
				    		GetServerAPI.cPostDebugLog(sign, executor, "INFO", "用例执行结果是："+testnote+"，与预期结果匹配成功！");
				    	}else{
				    		setresult = 1;
				    		GetServerAPI.cPostDebugLog(sign, executor, "ERROR", "第"+(i+1)+"步执行结果与预期结果匹配失败！"+"预期结果："+expectedresults+"      测试结果："+testnote);
				    		testnote = "用例第"+(i+1)+"步执行结果与预期结果匹配失败！";
				    		break;        //某一步骤失败后，此条用例置为失败退出
				    	}
			    	}else{                                                                                                                          //把预期结果与测试结果做精确匹配
				    	testnote = InvokeMethod.CallCase(packagename,functionname,getParameterValues,steps.get(i).getSteptype(),steps.get(i).getAction());
				    	if(expectedresults.equals(testnote)){
				    		setresult = 0;
				    		GetServerAPI.cPostDebugLog(sign, executor, "INFO", "用例执行结果是："+testnote+"，与预期结果匹配成功！");
				    	}else{
				    		setresult = 1;
				    		GetServerAPI.cPostDebugLog(sign, executor, "ERROR", "第"+(i+1)+"步执行结果与预期结果匹配失败！"+"预期结果："+expectedresults+"      测试结果："+testnote);
				    		testnote = "用例第"+(i+1)+"步执行结果与预期结果匹配失败！";
				    		break;        //某一步骤失败后，此条用例置为失败退出
				    	}
			    	}
			    	int waitsec = Integer.parseInt(casescript.get("StepWait").toString());   //获取步骤间等待时间
			    	if(waitsec!=0){
			    		Thread.sleep(waitsec*1000);
			    	}
			    }catch(Exception e){
			    	setresult = 1;
			    	GetServerAPI.cPostDebugLog(sign, executor, "ERROR", "调用方法过程出错，方法名："+functionname+" 请重新检查脚本方法名称以及参数！");
					testnote = "CallCase调用出错！";
					e.printStackTrace();
	    			break;
			    }			    
		    }
		    variable.clear();               //清空传参MAP
		    //如果调用方法过程中未出错，进入设置测试结果流程
		    if(testnote.indexOf("CallCase调用出错！")<=-1&&testnote.indexOf("解析出错啦！")<=-1){
		    	GetServerAPI.cPostDebugLog(sign, executor, "INFOover", "用例 "+sign+"解析成功，并成功调用用例中方法，请继续查看执行结果！");
		     }else{
		    	 GetServerAPI.cPostDebugLog(sign, executor, "ERRORover", "用例 "+sign+"解析或是调用步骤中的方法出错！");
		     }
		    if(setresult == 0){
		    	GetServerAPI.cPostDebugLog(sign, executor, "INFOover", "用例 "+sign+"步骤全部执行成功！");
		    }else{
		    	GetServerAPI.cPostDebugLog(sign, executor, "ERRORover", "用例 "+sign+"在执行过程中失败，请检查！");
		    }
	}

	public static void main(String[] args) throws Exception {
	}
}
