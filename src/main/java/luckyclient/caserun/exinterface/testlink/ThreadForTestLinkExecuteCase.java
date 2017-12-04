package luckyclient.caserun.exinterface.testlink;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import luckyclient.caserun.exinterface.TestControl;
import luckyclient.dblog.LogOperation;
import luckyclient.publicclass.DBOperation;
import luckyclient.publicclass.InvokeMethod;
import luckyclient.testlinkapi.InterfaceAnalyticTestLinkCase;
/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 * @ClassName: ThreadForExecuteCase 
 * @Description: 线程池方式执行用例
 * @author： seagull
 * @date 2015年4月13日 上午9:29:40  
 * 
 */
public class ThreadForTestLinkExecuteCase extends Thread{
	private String caseid;
	private TestCase testcaseob;
	private String tastid;
	
	public ThreadForTestLinkExecuteCase(String projectname,String caseid,TestCase testcaseob,String tastid){
		this.caseid = caseid;
		this.testcaseob = testcaseob;
		this.tastid = tastid;
	}
	
	@Override
	public void run(){
		Map<String,String> variable = new HashMap<String,String>(0);
		LogOperation caselog = new LogOperation();
		String functionname = null;
		String packagename =null;
		String expectedresults = null;
		Integer setresult = null;
		Object[] getParameterValues = null;
    	String testnote = null;
		int k = 0;
		 //进入循环，解析单个用例所有步骤
		System.out.println(caseid);
		//插入开始执行的用例
		caselog.addCaseDetail(tastid, testcaseob.getFullExternalId(), testcaseob.getVersion().toString(), testcaseob.getName(), 4);
	    for(int i=0;i<testcaseob.getSteps().size();i++){
	    	 //解析单个步骤中的脚本
	    	Map<String,String> casescript = InterfaceAnalyticTestLinkCase.analyticCaseStep(testcaseob, i+1,tastid,caselog);   
	    	try{
		    	packagename = casescript.get("PackageName").toString();
		    	functionname = casescript.get("FunctionName").toString();
	    	}catch(Exception e){
	    		k=0;
	    		luckyclient.publicclass.LogUtil.APP.error("用例："+testcaseob.getFullExternalId()+"解析包名或是方法名失败，请检查！");
				caselog.caseLogDetail(tastid, caseid, "解析包名或是方法名失败，请检查！","error",String.valueOf(i+1),"");
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
		    				caselog.caseLogDetail(tastid, caseid, "你好像在一个参数中引用了超过2个以上的变量哦！我处理不过来啦！","error",String.valueOf(i+1),"");
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
				    		caselog.caseLogDetail(tastid, caseid, "解析包名："+packagename+" 方法名："+functionname
				    				+" 第"+(j+1)+"个参数："+parameterValues,"info",String.valueOf(i+1),"");
				    		getParameterValues[j] = parameterValues;
		    			}else{
		    				luckyclient.publicclass.LogUtil.APP.error("没有找到你要的变量哦，再找下吧！第一个变量名称是："+uservariable+"，第"
		    						+ "二个变量名称是："+uservariable1+"，第三个变量名称是："+uservariable2);
		    				caselog.caseLogDetail(tastid, caseid, "没有找到你要的变量哦，再找下吧！第二变量名称是："+uservariable+"，第"
		    						+ "二个变量名称是："+uservariable1,"error",String.valueOf(i+1),"");
		    			}

		    		}else{
			    		String parameterValues1 = casescript.get("FunctionParams"+(j+1));
				    	if(parameterValues1.indexOf("&quot;")>-1 || parameterValues1.indexOf("&#39;")>-1 || parameterValues1.indexOf("@@")>-1){
				    		parameterValues1 = parameterValues1.replaceAll("&quot;", "\"");
				    		parameterValues1 = parameterValues1.replaceAll("&#39;", "\'");
				    		parameterValues1 = parameterValues1.replaceAll("@@", "@");
				    	}
			    		luckyclient.publicclass.LogUtil.APP.info("用例："+testcaseob.getFullExternalId()+"解析包名："+packagename+" 方法名："+functionname
			    				+" 第"+(j+1)+"个参数："+parameterValues1);
			    		caselog.caseLogDetail(tastid, caseid,"解析包名："+packagename+" 方法名："+functionname
			    				+" 第"+(j+1)+"个参数："+parameterValues1,"info",String.valueOf(i+1),"");
			    		getParameterValues[j] = parameterValues1;
		    		}
		    	}
	    	}else{
	    		getParameterValues = null;
	    	}
	    	//调用动态方法，执行测试用例
		    try{
		    	luckyclient.publicclass.LogUtil.APP.info("用例："+testcaseob.getFullExternalId()+"开始调用方法："+functionname+" .....");
		    	caselog.caseLogDetail(tastid, caseid,"开始调用方法："+functionname+" .....","info",String.valueOf(i+1),"");
		    	//把预期结果前两个字符判断是否是要把结果存入变量
		    	if(expectedresults.length()>2 && expectedresults.substring(0, 2).indexOf("$=")>-1){
		    		String expectedResultVariable = casescript.get("ExpectedResults").toString().substring(2);
		    		String temptestnote = InvokeMethod.callCase(packagename,functionname,getParameterValues,0,"");
		    		variable.put(expectedResultVariable, temptestnote);
		    		//把预期结果与测试结果做模糊匹配
		    	}else if(expectedresults.length()>2 && expectedresults.substring(0, 2).indexOf("%=")>-1){
			    	testnote = InvokeMethod.callCase(packagename,functionname,getParameterValues,0,"");
			    	if(testnote.indexOf(expectedresults.substring(2))>-1){
			    		setresult = 0;
			    		luckyclient.publicclass.LogUtil.APP.info("用例："+testcaseob.getFullExternalId()+"执行结果是："+testnote+"，与预期结果匹配成功！");
			    		caselog.caseLogDetail(tastid, caseid,"执行结果是："+testnote+"，与预期结果匹配成功！","info",String.valueOf(i+1),"");
			    	}else{
			    		setresult = 1;
			    		luckyclient.publicclass.LogUtil.APP.error("用例："+testcaseob.getFullExternalId()+"第"+(i+1)+"步执行结果与预期结果匹配失败！");
			    		luckyclient.publicclass.LogUtil.APP.error("用例："+testcaseob.getFullExternalId()+"预期结果："+expectedresults+"      测试结果："+testnote);
			    		caselog.caseLogDetail(tastid, caseid,"第"+(i+1)+"步执行结果与预期结果匹配失败！"+"预期结果："+expectedresults+"      测试结果："+testnote,"error",String.valueOf(i+1),"");
			    		testnote = "用例第"+(i+1)+"步执行结果与预期结果匹配失败！";
			    		break;        //某一步骤失败后，此条用例置为失败退出
			    	}
		    	}else{                                                                                                                    //把预期结果与测试结果做精确匹配
			    	testnote = InvokeMethod.callCase(packagename,functionname,getParameterValues,0,"");
			    	if(expectedresults.equals(testnote)){
			    		setresult = 0;
			    		luckyclient.publicclass.LogUtil.APP.info("用例："+testcaseob.getFullExternalId()+"执行结果是："+testnote+"，与预期结果匹配成功！");
			    		caselog.caseLogDetail(tastid, caseid,"执行结果是："+testnote+"，与预期结果匹配成功！","info",String.valueOf(i+1),"");
			    	}else{
			    		setresult = 1;
			    		luckyclient.publicclass.LogUtil.APP.error("用例："+testcaseob.getFullExternalId()+"第"+(i+1)+"步执行结果与预期结果匹配失败！");
			    		luckyclient.publicclass.LogUtil.APP.error("用例："+testcaseob.getFullExternalId()+"预期结果："+expectedresults+"      测试结果："+testnote);
			    		caselog.caseLogDetail(tastid, caseid,"第"+(i+1)+"步执行结果与预期结果匹配失败！"+"预期结果："+expectedresults+"      测试结果："+testnote,"error",String.valueOf(i+1),"");
						StringBuilder stringBuilder = new StringBuilder();
						stringBuilder.append("用例第"+(i+1)+"步执行结果与预期结果匹配失败！预期结果："+expectedresults+"      测试结果：");
						stringBuilder.append(testnote);
						testnote = stringBuilder.toString();
			    		break;        //某一步骤失败后，此条用例置为失败退出
			    	}
		    	}
		    	int waitsec = Integer.parseInt(casescript.get("StepWait").toString());  
		    	if(waitsec!=0){
		    		Thread.sleep(waitsec*1000);
		    	}
		    }catch(Exception e){
		    	luckyclient.publicclass.LogUtil.ERROR.error("用例："+testcaseob.getFullExternalId()+"调用方法过程出错，方法名："+functionname+" 请重新检查脚本方法名称以及参数！");
		    	caselog.caseLogDetail(tastid, caseid,"调用方法过程出错，方法名："+functionname+" 请重新检查脚本方法名称以及参数！","error",String.valueOf(i+1),"");
				luckyclient.publicclass.LogUtil.ERROR.error(e,e);
				testnote = "CallCase调用出错！调用方法过程出错，方法名："+functionname+" 请重新检查脚本方法名称以及参数！";
				setresult = 1;
				e.printStackTrace();
    			break;
		    }			    
	    }
	    //如果调用方法过程中未出错，进入设置测试结果流程
	    try{
	    //成功跟失败的用例走此流程
	    if(testnote.indexOf("CallCase调用出错！")<=-1&&testnote.indexOf("解析出错啦！")<=-1){       
		    	//luckyclient.publicclass.LogUtil.APP.info("用例："+testcaseob.getFullExternalId()+"开始设置测试用例执行结果 .....");
		    	//caselog.caseLogDetail(tastid, caseid,"开始设置测试用例执行结果 .....","info","SETCASERESULT...");
		    	//TCResult = TestCaseApi.setTCResult(projectname,caseid, testnote, testcaseob.getVersion(),setresult);
		    	caselog.updateCaseDetail(tastid, caseid, setresult);
	     }else{
	    	//解析用例或是调用方法出错，全部把用例置为锁定
	    	 luckyclient.publicclass.LogUtil.ERROR.error("用例："+testcaseob.getFullExternalId()+"设置执行结果为锁定，请参考错误日志查找锁定用例的原因.....");
	    	 caselog.caseLogDetail(tastid, caseid,"设置执行结果为锁定，请参考错误日志查找锁定用例的原因.....","error","SETCASERESULT...","");
		     //TCResult = TestCaseApi.setTCResult(projectname,caseid, testnote, testcaseob.getVersion(),2);
	    	 setresult = 2;
		     caselog.updateCaseDetail(tastid, caseid, setresult);
	     }
    	if(setresult==0){
    		luckyclient.publicclass.LogUtil.APP.info("用例："+testcaseob.getFullExternalId()+"执行结果成功......");
    		caselog.caseLogDetail(tastid, caseid,"用例步骤执行全部成功......","info","ending","");
    		luckyclient.publicclass.LogUtil.APP.info("******************************分割线*************************************");
    	}else if(setresult==1){
    		luckyclient.publicclass.LogUtil.ERROR.error("用例："+testcaseob.getFullExternalId()+"执行结果失败......");
    		caselog.caseLogDetail(tastid, caseid,"用例执行结果失败......","error","ending","");
    		luckyclient.publicclass.LogUtil.APP.info("******************************分割线*************************************");
    	}else{
    		luckyclient.publicclass.LogUtil.ERROR.error("用例："+testcaseob.getFullExternalId()+"执行结果锁定......");
    		caselog.caseLogDetail(tastid, caseid,"用例执行结果锁定......","error","ending","");
    		luckyclient.publicclass.LogUtil.APP.info("******************************分割线*************************************");
    	}
	    }catch(Exception e){
	    	luckyclient.publicclass.LogUtil.ERROR.error("用例："+testcaseob.getFullExternalId()+"设置执行结果过程出错......");
	    	caselog.caseLogDetail(tastid, caseid,"设置执行结果过程出错......","error","ending","");
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
