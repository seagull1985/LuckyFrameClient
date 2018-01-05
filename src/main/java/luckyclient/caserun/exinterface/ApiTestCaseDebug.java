package luckyclient.caserun.exinterface;

import java.util.HashMap;
import java.util.Iterator;
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
 * @ClassName: TestCaseDebug 
 * @Description: 针对自动化用例在编写过程中，对用例脚本进行调试
 * @author： seagull
 * @date 2014年8月24日 上午9:29:40  
 * 
 */
public class ApiTestCaseDebug{

	/**
	 * 用于在本地做单条用例调试
	 * @param projectname
	 * @param testCaseExternalId
	 */
	public static void oneCaseDebug(String projectname,String testCaseExternalId){
		Map<String,String> variable = new HashMap<String,String>(0);
		String packagename =null;
		String functionname = null;
		String expectedresults = null;
		Integer setresult = null;
		Object[] getParameterValues = null;
    	String testnote = null;
		int k = 0;
		ProjectCase testcaseob = GetServerAPI.cgetCaseBysign(testCaseExternalId);
		List<PublicCaseParams> pcplist=GetServerAPI.cgetParamsByProjectid(String.valueOf(testcaseob.getProjectid()));
		// 把公共参数加入到MAP中
		for (PublicCaseParams pcp : pcplist) {
			variable.put(pcp.getParamsname(), pcp.getParamsvalue());
		}
		List<ProjectCasesteps> steps=GetServerAPI.getStepsbycaseid(testcaseob.getId());
		if(steps.size()==0){
			setresult=2;
			luckyclient.publicclass.LogUtil.APP.error("用例中未找到步骤，请检查！");
			testnote="用例中未找到步骤，请检查！";
		}
		    //进入循环，解析用例所有步骤
		    for(int i=0;i<steps.size();i++){		    	
		    	Map<String,String> casescript = InterfaceAnalyticCase.analyticCaseStep(testcaseob, steps.get(i),"888888",null); 
				try {
					packagename = casescript.get("PackageName").toString();
					packagename = ChangString.changparams(packagename, variable,"包路径");
					functionname = casescript.get("FunctionName").toString();
					functionname = ChangString.changparams(functionname, variable,"方法名");
				} catch (Exception e) {
					k = 0;
					luckyclient.publicclass.LogUtil.APP.error("用例：" + testcaseob.getSign() + "解析包名或是方法名失败，请检查！");
					e.printStackTrace();
					break; // 某一步骤失败后，此条用例置为失败退出
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
			    	//获取传入参数，放入对象中，初始化参数对象个数
			    	getParameterValues = new Object[casescript.size()-4];    
			    	for(int j=0;j<casescript.size()-4;j++){		    		
			    		if(casescript.get("FunctionParams"+(j+1))==null){
			    			k = 1;
			    			break;
			    		}
						String parameterValues = casescript.get("FunctionParams" + (j + 1));
						parameterValues = ChangString.changparams(parameterValues, variable,"用例参数");
						luckyclient.publicclass.LogUtil.APP.info("用例：" + testcaseob.getSign() + "解析包名：" + packagename
								+ " 方法名：" + functionname + " 第" + (j + 1) + "个参数：" + parameterValues);
						getParameterValues[j] = parameterValues;
			    	}
		    	}else{
		    		getParameterValues = null;
		    	}
		    	//调用动态方法，执行测试用例
			    try{
			    	luckyclient.publicclass.LogUtil.APP.info("开始调用方法："+functionname+" .....");
			    	//把预期结果前两个字符判断是否是要把结果存入变量
			    	if(expectedresults.length()>2 && expectedresults.substring(0, 2).indexOf("$=")>-1){                             
			    		String expectedResultVariable = casescript.get("ExpectedResults").toString().substring(2);
			    		String temptestnote = InvokeMethod.callCase(packagename,functionname,getParameterValues,steps.get(i).getSteptype(),steps.get(i).getAction());
			    		variable.put(expectedResultVariable, temptestnote);
			    		//把预期结果与测试结果做模糊匹配
			    	}else if(expectedresults.length()>2 && expectedresults.substring(0, 2).indexOf("%=")>-1){                     
				    	testnote = InvokeMethod.callCase(packagename,functionname,getParameterValues,steps.get(i).getSteptype(),steps.get(i).getAction());
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
				    	testnote = InvokeMethod.callCase(packagename,functionname,getParameterValues,steps.get(i).getSteptype(),steps.get(i).getAction());
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
			    	//获取步骤间等待时间
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
	}
	
	/**
	 * 用于在本地做多条用例串行调试
	 * @param projectname
	 * @param addtestcase
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
		    oneCaseDebug(projectname,testCaseExternalId);
		    }catch(Exception e){
		    	continue;
		    }
		}
	}

	public static void main(String[] args) throws Exception {

	}
}
