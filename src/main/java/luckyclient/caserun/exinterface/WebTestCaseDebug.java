package luckyclient.caserun.exinterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import luckyclient.caserun.exinterface.analyticsteps.InterfaceAnalyticCase;
import luckyclient.planapi.api.GetServerAPI;
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
	public static void oneCaseDebug(String sign,String executor){
		Map<String,String> variable = new HashMap<String,String>();
		String packagename =null;
		String functionname = null;
		String expectedresults = null;
		Integer setresult = null;
		Object[] getParameterValues = null;
    	String testnote = null;
		int k = 0;
		ProjectCase testcaseob = GetServerAPI.cgetCaseBysign(sign);
		List<PublicCaseParams> pcplist=GetServerAPI.cgetParamsByProjectid(String.valueOf(testcaseob.getProjectid()));
		// 把公共参数加入到MAP中
		for (PublicCaseParams pcp : pcplist) {
			variable.put(pcp.getParamsname(), pcp.getParamsvalue());
		}
		List<ProjectCasesteps> steps=GetServerAPI.getStepsbycaseid(testcaseob.getId());
		    //进入循环，解析用例所有步骤
		    for(int i=0;i<steps.size();i++){		    	
		    	Map<String,String> casescript = InterfaceAnalyticCase.analyticCaseStep(testcaseob, steps.get(i),"888888",null);    
		    	try{
			    	packagename = casescript.get("PackageName").toString();
			    	packagename = ChangString.changparams(packagename, variable,"包路径");
			    	functionname = casescript.get("FunctionName").toString();
			    	functionname = ChangString.changparams(functionname, variable,"方法名");
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
		    	expectedresults = casescript.get("ExpectedResults").toString();    
		    	expectedresults = ChangString.changparams(expectedresults, variable,"预期结果");
		    	//判断方法是否带参数
		    	if(casescript.size()>4){
			    	//获取传入参数，放入对象中
			    	getParameterValues = new Object[casescript.size()-4];    
			    	for(int j=0;j<casescript.size()-4;j++){
			    		if(casescript.get("FunctionParams"+(j+1))==null){
			    			k = 1;
			    			break;
			    		}
			    		
						String parameterValues = casescript.get("FunctionParams" + (j + 1));
						parameterValues = ChangString.changparams(parameterValues, variable,"用例参数");
	    				GetServerAPI.cPostDebugLog(sign, executor, "INFO", "解析包名："+packagename+" 方法名："+functionname
			    				+" 第"+(j+1)+"个参数："+parameterValues);
						getParameterValues[j] = parameterValues;
			    	}
		    	}else{
		    		getParameterValues = null;
		    	}
		    	//调用动态方法，执行测试用例
			    try{
    				GetServerAPI.cPostDebugLog(sign, executor, "INFO", "开始调用方法："+functionname+" .....");
			    	if(expectedresults.length()>2 && expectedresults.substring(0, 2).indexOf("$=")>-1){                             
			    		String expectedResultVariable = casescript.get("ExpectedResults").toString().substring(2);
			    		String temptestnote = InvokeMethod.callCase(packagename,functionname,getParameterValues,steps.get(i).getSteptype(),steps.get(i).getAction());
			    		variable.put(expectedResultVariable, temptestnote);
			    		GetServerAPI.cPostDebugLog(sign, executor, "INFO", "赋值变量【"+expectedresults.substring(2, expectedresults.length())+"】： "+temptestnote);
			    	}else if(expectedresults.length()>2 && expectedresults.substring(0, 2).indexOf("%=")>-1){                    
				    	testnote = InvokeMethod.callCase(packagename,functionname,getParameterValues,steps.get(i).getSteptype(),steps.get(i).getAction());
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
				    	testnote = InvokeMethod.callCase(packagename,functionname,getParameterValues,steps.get(i).getSteptype(),steps.get(i).getAction());
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
			    	int waitsec = Integer.parseInt(casescript.get("StepWait").toString());   
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
