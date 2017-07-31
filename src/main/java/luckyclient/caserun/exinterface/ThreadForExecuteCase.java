package luckyclient.caserun.exinterface;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import luckyclient.caserun.exinterface.AnalyticSteps.InterfaceAnalyticCase;
import luckyclient.dblog.LogOperation;
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
 * @ClassName: ThreadForExecuteCase 
 * @Description: 线程池方式执行用例
 * @author： seagull
 * @date 2017年7月13日 上午9:29:40  
 * 
 */
public class ThreadForExecuteCase extends Thread{
	private String caseid;
	private ProjectCase testcaseob;
	private String taskid;
	private List<ProjectCasesteps> steps;
	
	public ThreadForExecuteCase(ProjectCase projectcase,List<ProjectCasesteps> steps,String taskid){
		this.caseid = String.valueOf(projectcase.getId());
		this.testcaseob = projectcase;
		this.taskid = taskid;
		this.steps = steps;
	}
	
	public void run(){
		Map<String,String> variable = new HashMap<String,String>();
		LogOperation caselog = new LogOperation();        //初始化写用例结果以及日志模块 
		String functionname = null;
		String packagename =null;
		String expectedresults = null;
		Integer setresult = null;
		Object[] getParameterValues = null;
    	String testnote = null;
		int k = 0;
		 //进入循环，解析单个用例所有步骤
		System.out.println(testcaseob.getSign());
		caselog.AddCaseDetail(taskid, testcaseob.getSign(), "1", testcaseob.getName(), 4);       //插入开始执行的用例
	    for(int i=0;i<steps.size();i++){
	    	Map<String,String> casescript = InterfaceAnalyticCase.AnalyticCaseStep(testcaseob, steps.get(i),taskid);    //解析单个步骤中的脚本
	    	try{
		    	packagename = casescript.get("PackageName").toString();
		    	functionname = casescript.get("FunctionName").toString();
	    	}catch(Exception e){
	    		k=0;
	    		luckyclient.publicclass.LogUtil.APP.error("用例："+testcaseob.getSign()+"解析包名或是方法名失败，请检查！");
				caselog.CaseLogDetail(taskid, caseid, "解析包名或是方法名失败，请检查！","error",String.valueOf(i+1),"");
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
		    		if(casescript.get("FunctionParams"+(j+1)).indexOf("@")>-1){                        //如果存在传参，进行处理
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
		    				caselog.CaseLogDetail(taskid, caseid, "你好像在一个参数中引用了超过2个以上的变量哦！我处理不过来啦！","error",String.valueOf(i+1),"");
		    			}
		    			Iterator keys = variable.keySet().iterator();
		    			String key = null;
		    			while(keys.hasNext()){
		    				key = (String)keys.next();
		    				if(uservariable.equals(key)){
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
			    				if(uservariable1.equals(key)){
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
			    				if(uservariable2.equals(key)){
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
				    		caselog.CaseLogDetail(taskid, caseid, "解析包名："+packagename+" 方法名："+functionname
				    				+" 第"+(j+1)+"个参数："+ParameterValues,"info",String.valueOf(i+1),"");
				    		getParameterValues[j] = ParameterValues;
		    			}else{
		    				luckyclient.publicclass.LogUtil.APP.error("没有找到你要的变量哦，再找下吧！第一个变量名称是："+uservariable+"，第"
		    						+ "二个变量名称是："+uservariable1+"，第三个变量名称是："+uservariable2);
		    				caselog.CaseLogDetail(taskid, caseid, "没有找到你要的变量哦，再找下吧！第二变量名称是："+uservariable+"，第"
		    						+ "二个变量名称是："+uservariable1,"error",String.valueOf(i+1),"");
		    			}

		    		}else{
			    		String ParameterValues1 = casescript.get("FunctionParams"+(j+1));
				    	if(ParameterValues1.indexOf("&quot;")>-1 || ParameterValues1.indexOf("&#39;")>-1){         //页面转义字符转换
				    		ParameterValues1 = ParameterValues1.replaceAll("&quot;", "\"");
				    		ParameterValues1 = ParameterValues1.replaceAll("&#39;", "\'");
				    	}
			    		luckyclient.publicclass.LogUtil.APP.info("用例："+testcaseob.getSign()+"解析包名："+packagename+" 方法名："+functionname
			    				+" 第"+(j+1)+"个参数："+ParameterValues1);
			    		caselog.CaseLogDetail(taskid, caseid,"解析包名："+packagename+" 方法名："+functionname
			    				+" 第"+(j+1)+"个参数："+ParameterValues1,"info",String.valueOf(i+1),"");
			    		getParameterValues[j] = ParameterValues1;
		    		}
		    	}
	    	}else{
	    		getParameterValues = null;
	    	}
	    	//调用动态方法，执行测试用例
		    try{
		    	luckyclient.publicclass.LogUtil.APP.info("用例："+testcaseob.getSign()+"开始调用方法："+functionname+" .....");
		    	caselog.CaseLogDetail(taskid, caseid,"开始调用方法："+functionname+" .....","info",String.valueOf(i+1),"");
		    	if(expectedresults.length()>2 && expectedresults.substring(0, 2).indexOf("$=")>-1){                             //把预期结果前两个字符判断是否是要把结果存入变量
		    		String ExpectedResultVariable = casescript.get("ExpectedResults").toString().substring(2);
		    		String temptestnote = InvokeMethod.CallCase(packagename,functionname,getParameterValues);
		    		variable.put(ExpectedResultVariable, temptestnote);
		    	}else if(expectedresults.length()>2 && expectedresults.substring(0, 2).indexOf("%=")>-1){                     //把预期结果与测试结果做模糊匹配
			    	testnote = InvokeMethod.CallCase(packagename,functionname,getParameterValues);
			    	if(testnote.indexOf(expectedresults.substring(2))>-1){
			    		setresult = 0;
			    		luckyclient.publicclass.LogUtil.APP.info("用例："+testcaseob.getSign()+"执行结果是："+testnote+"，与预期结果匹配成功！");
			    		caselog.CaseLogDetail(taskid, caseid,"执行结果是："+testnote+"，与预期结果匹配成功！","info",String.valueOf(i+1),"");
			    	}else{
			    		setresult = 1;
			    		luckyclient.publicclass.LogUtil.APP.error("用例："+testcaseob.getSign()+"第"+(i+1)+"步执行结果与预期结果匹配失败！");
			    		luckyclient.publicclass.LogUtil.APP.error("用例："+testcaseob.getSign()+"预期结果："+expectedresults+"      测试结果："+testnote);
			    		caselog.CaseLogDetail(taskid, caseid,"第"+(i+1)+"步执行结果与预期结果匹配失败！"+"预期结果："+expectedresults+"      测试结果："+testnote,"error",String.valueOf(i+1),"");
			    		testnote = "用例第"+(i+1)+"步执行结果与预期结果匹配失败！";
			    		break;        //某一步骤失败后，此条用例置为失败退出
			    	}
		    	}else{                                                                                                                    //把预期结果与测试结果做精确匹配
			    	testnote = InvokeMethod.CallCase(packagename,functionname,getParameterValues);
			    	if(expectedresults.equals(testnote)){
			    		setresult = 0;
			    		luckyclient.publicclass.LogUtil.APP.info("用例："+testcaseob.getSign()+"执行结果是："+testnote+"，与预期结果匹配成功！");
			    		caselog.CaseLogDetail(taskid, caseid,"执行结果是："+testnote+"，与预期结果匹配成功！","info",String.valueOf(i+1),"");
			    	}else{
			    		setresult = 1;
			    		luckyclient.publicclass.LogUtil.APP.error("用例："+testcaseob.getSign()+"第"+(i+1)+"步执行结果与预期结果匹配失败！");
			    		luckyclient.publicclass.LogUtil.APP.error("用例："+testcaseob.getSign()+"预期结果："+expectedresults+"      测试结果："+testnote);
			    		caselog.CaseLogDetail(taskid, caseid,"第"+(i+1)+"步执行结果与预期结果匹配失败！"+"预期结果："+expectedresults+"      测试结果："+testnote,"error",String.valueOf(i+1),"");
			    		testnote = "用例第"+(i+1)+"步执行结果与预期结果匹配失败！预期结果："+expectedresults+"      测试结果："+testnote;
			    		break;        //某一步骤失败后，此条用例置为失败退出
			    	}
		    	}
		    	int waitsec = Integer.parseInt(casescript.get("StepWait").toString());   //获取步骤间等待时间
		    	if(waitsec!=0){
		    		Thread.sleep(waitsec*1000);
		    	}
		    }catch(Exception e){
		    	luckyclient.publicclass.LogUtil.ERROR.error("用例："+testcaseob.getSign()+"调用方法过程出错，方法名："+functionname+" 请重新检查脚本方法名称以及参数！");
		    	caselog.CaseLogDetail(taskid, caseid,"调用方法过程出错，方法名："+functionname+" 请重新检查脚本方法名称以及参数！","error",String.valueOf(i+1),"");
				luckyclient.publicclass.LogUtil.ERROR.error(e,e);
				testnote = "CallCase调用出错！调用方法过程出错，方法名："+functionname+" 请重新检查脚本方法名称以及参数！";
				setresult = 1;
				e.printStackTrace();
    			break;
		    }			    
	    }
	    //如果调用方法过程中未出错，进入设置测试结果流程
	    try{
	    if(testnote.indexOf("CallCase调用出错！")<=-1&&testnote.indexOf("解析出错啦！")<=-1){                //成功跟失败的用例走此流程
		    	caselog.UpdateCaseDetail(taskid, caseid, setresult);
	     }else{
	    	 luckyclient.publicclass.LogUtil.ERROR.error("用例："+testcaseob.getSign()+"设置执行结果为锁定，请参考错误日志查找锁定用例的原因.....");    //解析用例或是调用方法出错，全部把用例置为锁定
	    	 caselog.CaseLogDetail(taskid, caseid,"设置执行结果为锁定，请参考错误日志查找锁定用例的原因.....","error","SETCASERESULT...","");
	    	 setresult = 2;
		     caselog.UpdateCaseDetail(taskid, caseid, setresult);
	     }
    	if(setresult==0){
    		luckyclient.publicclass.LogUtil.APP.info("用例："+testcaseob.getSign()+"执行结果成功......");
    		caselog.CaseLogDetail(taskid, caseid,"用例步骤执行全部成功......","info","ending","");
    		luckyclient.publicclass.LogUtil.APP.info("******************************分割线*************************************");
    	}else if(setresult==1){
    		luckyclient.publicclass.LogUtil.ERROR.error("用例："+testcaseob.getSign()+"执行结果失败......");
    		caselog.CaseLogDetail(taskid, caseid,"用例执行结果失败......","error","ending","");
    		luckyclient.publicclass.LogUtil.APP.info("******************************分割线*************************************");
    	}else{
    		luckyclient.publicclass.LogUtil.ERROR.error("用例："+testcaseob.getSign()+"执行结果锁定......");
    		caselog.CaseLogDetail(taskid, caseid,"用例执行结果锁定......","error","ending","");
    		luckyclient.publicclass.LogUtil.APP.info("******************************分割线*************************************");
    	}
	    }catch(Exception e){
	    	luckyclient.publicclass.LogUtil.ERROR.error("用例："+testcaseob.getSign()+"设置执行结果过程出错......");
	    	caselog.CaseLogDetail(taskid, caseid,"设置执行结果过程出错......","error","ending","");
			luckyclient.publicclass.LogUtil.ERROR.error(e,e);
			e.printStackTrace();
	    }finally{
	    	variable.clear();                     //一条用例结束后，清空变量存储空间
	    	TestControl.Debugcount--;        //多线程计数--，用于检测线程是否全部执行完
	    }
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
