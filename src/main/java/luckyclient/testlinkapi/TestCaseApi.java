package luckyclient.testlinkapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;

import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionType;
import br.eti.kinoshita.testlinkjavaapi.constants.TestCaseDetails;
import br.eti.kinoshita.testlinkjavaapi.constants.TestCaseStepAction;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestCaseStep;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestSuite;
import br.eti.kinoshita.testlinkjavaapi.util.TestLinkAPIException;
import luckyclient.caserun.exinterface.TestControl;
import luckyclient.dblog.LogOperation;
import luckyclient.publicclass.remoterinterface.HttpRequest;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 * @ClassName: TestCaseApi 
 * @Description: 二次封装关于测试用例部分的API 
 * @author： seagull
 * @date 2014年6月24日 上午9:29:40  
 * 
 */
public class TestCaseApi extends TestLinkBaseApi {

	/**
	 * 设置用例执行结果
	 * @param projectname
	 * @param testCaseExternalId
	 */

	public static String setTCResult (String projectname,String testCaseExternalId,String note,Integer version,Integer setresult){
/*
		  ExecutionStatus status = null;
		  ReportTCResultResponse result = null;
		try     {		    
			    //通过用例序号获取用例ID
			    TestCase testcaseob = new TestCase();
			    testcaseob = api.getTestCaseByExternalId(testCaseExternalId, version);
			    Integer testcaseid = testcaseob.getId();

	    	    //能过项目名称以及计划名称获取计划ID
	    	    TestPlan testplanob = new TestPlan();
	    	    //testplanob = api.getTestPlanByName(TestPlanName(projectname), projectname);
	    	    testplanob = api.getTestPlanByName(LogOperation.GetTestPlanName(TestControl.TASTID), projectname);
	    	    Integer planid = testplanob.getId();
	    	    
	    	    //通过测试计划ID获取最后一次的构建名称以及ID
	    	    Build lastbuild = new Build(); 
	    	    try{
	    	    	lastbuild = api.getLatestBuildForTestPlan(planid);
	    	    }catch( TestLinkAPIException te){
	    	    	JavaBaseTest.LogUtil.APP.info("测试计划 "+testplanob.getName()+" 下没有找到测试版本，无法设置测试结果，现在将先为你创建一个测试版本！");
	    	    	TestBuildApi.createBuild(projectname);
	    	    	lastbuild = api.getLatestBuildForTestPlan(planid);
	    	    }finally{
		    	    Integer buildId = lastbuild.getId();
		    	    String buildName = lastbuild.getName();
		    	    
		    	    //自定义域保留，未知用处
		    	    Map<String, String> key = new HashMap<String, String>();
		    	    key.put("", "");
		    	    
		    	   //备注以及从备注中初始化执行结果
		    	    if(setresult == 0) {
		    	    	status = ExecutionStatus.PASSED;
		    	    }
		    	    else if(setresult == 1) {
		    	    	status = ExecutionStatus.FAILED;
		    	    }else{
		    	    	status = ExecutionStatus.BLOCKED;
		    	    }
		    	    
		    	    result = new ReportTCResultResponse();
		    	    
		    	    //获取执行结果
		    	    result = api.setTestCaseExecutionResult(testcaseid, 1, planid, 
		    				status, buildId, buildName, note, true, "", 
		    				PLATFORMID, PLATFORMNAME, key, true);		    	    
	    	    }
	    	    return result.getMessage().toString();  
	    } catch( TestLinkAPIException te) {
	            te.printStackTrace( System.err );
	            JavaBaseTest.LogUtil.ERROR.error("用例设置执行结果出错，请检查！    用例编号："+testCaseExternalId+" 备注："+note+" 用例版本号："+version);
	            JavaBaseTest.LogUtil.ERROR.error(te,te);
	            return "用例："+testCaseExternalId+" 设置结果出错，请检查原因！";
	    }*/
		return "直接返回成功，暂时不更新testlink中的测试结果！Success!";
	} 
	
	/**
	 * 添加用例到测试计划中
	 * @param projectname
	 * @param testCaseExternalID
	 * @param version
	 */
	
	public String addTCToTP(String projectname,String testCaseExternalID,Integer version){
        Object result = null;
		try{
		    //能过项目名称以及计划名称获取计划ID
		    TestPlan testplanob = new TestPlan();
		    //testplanob = api.getTestPlanByName(TestPlanName(projectname), projectname);
		    testplanob = api.getTestPlanByName(LogOperation.getTestPlanName(TestControl.TASKID), projectname);
		    Integer planid = testplanob.getId();
		    
		    Map<String, Object> params = new HashMap<String, Object>(0);			
		    params.put("devKey", TESTLINK_DEVKEY);
		    params.put("testprojectid", projectID(projectname));
		    params.put("testplanid", planid);
		    params.put("testcaseexternalid", testCaseExternalID);
		    params.put("version", version);
			
			try {
				result = api.executeXmlRpcCall("tl.addTestCaseToTestPlan", params);
			} catch (XmlRpcException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            	
		}catch( TestLinkAPIException te) {
            te.printStackTrace( System.err );
            System.exit(-1);
    }
		@SuppressWarnings("rawtypes")
		Map item = (Map)result;		
		return item.get("status").toString();
	}
	
	
	/**
	 * 获取单个用例的步骤
	 * @param testCaseExternalId
	 * @param version
	 */
	public static List<TestCaseStep> getTestCaseSteps(String testCaseExternalId,Integer version){
	    TestCase testcaseob = new TestCase();
	    testcaseob = api.getTestCaseByExternalId(testCaseExternalId, version);
	   return testcaseob.getSteps();
	}
	
	/**
	 * 从计划中取出用例
	 */
	public static TestCase[] getplantestcase(String projectname,String taskid,String testplan){
		TestCase[] testCases = null;
		try {
		//能过项目名称以及计划名称获取计划ID
			Integer planid;
			TestPlan tp;

			if ("888888".equals(taskid)) {
				tp = api.getTestPlanByName(testplan, projectname);
			} else if (taskid.indexOf("NULL") > -1 || LogOperation.getTestPlanName(taskid).indexOf("NULL") > -1) {
				tp = api.getTestPlanByName(testPlanName(projectname), projectname);
			} else {
				tp = api.getTestPlanByName(LogOperation.getTestPlanName(taskid), projectname);				
			}
			planid = tp.getId();
	    //用例明细全部信息
//	    TestCaseDetails detail = TestCaseDetails.FULL;
	    
	    //获取当前测试计划中的测试模块   
	    testCases = api.getTestCasesForTestPlan(
	    		planid, null, null, null, null, null, null, null, 
				ExecutionType.AUTOMATED, true, TestCaseDetails.FULL);
	    
	    //冒泡排序，按用例的执行优先级进行排序，数字越大，优先级越高
	    for(int i=0;i<testCases.length;i++)
	    {
	     for(int j=0;j<testCases.length-i-1;j++)
	     {
	      TestCase temp=null;
	      if(testCases[j].getExecutionOrder()>testCases[j+1].getExecutionOrder())
	      {
	       temp=testCases[j];
	       testCases[j]=testCases[j+1];
	       testCases[j+1]=temp;
	      }
	     }
	    }
	    
		if(testCases.length>0){
			luckyclient.publicclass.LogUtil.APP.info("项目："+projectname+" 测试计划："+tp.getName()+" 读取自动化用例对象完成！");		   
		}else{
			luckyclient.publicclass.LogUtil.ERROR.error("项目："+projectname+" 测试计划："+tp.getName()+" 没有添加对应的执行用例，请添加用例后再执行！");
		}
		
		}catch( TestLinkAPIException te) {
            te.printStackTrace( System.err );
            luckyclient.publicclass.LogUtil.ERROR.error("项目："+projectname+" 测试计划："+testPlanName(projectname)+" 读取自动化用例对象异常！");
            System.exit(-1);
    }
		 return testCases;

	}
	
	public static TestCase getTestCaseByExternalId(String testCaseExternalId,int version){
		return api.getTestCaseByExternalId(testCaseExternalId, version);
	}
	
	/**
	 * 更新testlink中用例指定步骤的预期结果
	 */
	public static String setTestLinkExpectedResults(String testCaseExternalId, int version, int steps, String expectedResults) {
		String results = "设置结果失败";
		try {
			TestCase tc = api.getTestCaseByExternalId(testCaseExternalId, version);
			
			tc.getSteps().get(steps - 1).setExpectedResults(expectedResults);
			api.createTestCaseSteps(tc.getId(), testCaseExternalId, tc.getVersion(), TestCaseStepAction.UPDATE, tc.getSteps());
			results = "设置结果成功";
		} catch (TestLinkAPIException te) {
			te.printStackTrace(System.err);
			results = te.getMessage().toString();
			return results;
		}
		return results;

	}
	
	
	/**
	 * 拷贝测试用例到系统自带用例库中   接口自动化用例
	 * @throws InterruptedException 
	 */
	public static void copyCaseToSysForInt(String projectname,String testplan,int projectid) throws InterruptedException{		
		TestCase[] testcases=getplantestcase(projectname,"888888",testplan);
		int casecount=1;
		for(TestCase cases:testcases){
			System.out.println("准备开始导第"+casecount+"条用例..."+cases.getFullExternalId());
			TestCase suitecase=api.getTestCaseByExternalId(cases.getFullExternalId(), cases.getVersion());
			List<Integer> suiteid=new ArrayList<Integer>();
			suiteid.add(suitecase.getTestSuiteId());
			TestSuite[] suite=api.getTestSuiteByID(suiteid);
			
			String params="";

			params="name="+cases.getName().replace("%", "BBFFHH");
			params+="&projectid="+projectid;
			params+="&modulename="+suite[0].getName();
			//0 接口  1 UI
			params+="&casetype=0";    
			
			String caseid=HttpRequest.sendPost("/projectCase/cpostcase.do", params);
			System.out.println("已经成功创建用例，ID："+caseid);
			Thread.sleep(500);
			int k=1;
			for(TestCaseStep step:cases.getSteps()){
				String stepsparams="";
				String resultstr = null;
				 //获取actions字符串
				String stepsstr = step.getActions();   
				String scriptstr = InterfaceAnalyticTestLinkCase.subComment(stepsstr);

				if(scriptstr.substring(scriptstr.length()-6, scriptstr.length()).indexOf("*Wait;")>-1){
					String action="";
					action = scriptstr.substring(scriptstr.lastIndexOf("|")+1,scriptstr.lastIndexOf("*Wait;")+5);
		        	stepsparams="action="+action+"&";
		        	scriptstr = scriptstr.substring(0, scriptstr.lastIndexOf("|")+1);
		        }
				if(scriptstr.substring(scriptstr.length()-6, scriptstr.length()).indexOf("*wait;")>-1){
					String action="";
					action = scriptstr.substring(scriptstr.lastIndexOf("|")+1,scriptstr.lastIndexOf("*wait;")+5);
		        	stepsparams="action="+action+"&";
		        	scriptstr = scriptstr.substring(0, scriptstr.lastIndexOf("|")+1);
		        }
				resultstr = InterfaceAnalyticTestLinkCase.subComment(step.getExpectedResults()); 
				stepsparams+="expectedresult="+resultstr.replace("%", "BBFFHH");
				stepsparams+="&caseid="+caseid;
				stepsparams+="&stepnum="+k;
				stepsparams+="&projectid="+projectid;
				stepsparams+="&steptype=0";
				String[] temp=scriptstr.split("\\|",-1);
				String param="";
				for(int i=0;i<temp.length;i++){
					if(i==0){
						String packagenage = temp[i].substring(0, temp[i].indexOf("#"));
						String functionname = temp[i].substring(temp[i].indexOf("#")+1, temp[i].indexOf(";"));
						//set包名
						stepsparams+="&path="+packagenage.trim();   
						//set方法名称
						stepsparams+="&operation="+functionname.trim();   
					}else if("".equals(temp[i])){
						continue;
					}else{
						param+=temp[i]+"|";
					}
				}
				//set方法名称
				stepsparams+="&parameters="+param.replace("%", "BBFFHH");   
				String stepid=HttpRequest.sendPost("/projectCasesteps/cpoststep.do", stepsparams);
				System.out.println("已经成功创建用例步骤，用例ID:"+caseid+"  步骤ID:"+stepid);
				k++;
				Thread.sleep(500);
			}
			System.out.println("第"+casecount+"条用例生成完成！"+cases.getFullExternalId());
			casecount++;
		}
		System.out.println(testplan+"中的用例已经全部生成完毕！");
	}
	
	/**
	 * 拷贝测试用例到系统自带用例库中   UI自动化用例
	 * @throws InterruptedException 
	 */
	public static void copyCaseToSysForUI(String projectname,String testplan,int projectid) throws InterruptedException{		
		TestCase[] testcases=getplantestcase(projectname,"888888",testplan);
		int casecount=1;
		for(TestCase cases:testcases){
			System.out.println("准备开始导第"+casecount+"条用例..."+cases.getFullExternalId());
			TestCase suitecase=api.getTestCaseByExternalId(cases.getFullExternalId(), cases.getVersion());
			List<Integer> suiteid=new ArrayList<Integer>();
			suiteid.add(suitecase.getTestSuiteId());
			TestSuite[] suite=api.getTestSuiteByID(suiteid);
			
			String params="";

			params="name="+cases.getName().replace("%", "BBFFHH");
			params+="&projectid="+projectid;
			params+="&modulename="+suite[0].getName();
			 //0 接口  1 UI
			params+="&casetype=1";   
			
			String caseid=HttpRequest.sendPost("/projectCase/cpostcase.do", params);
			System.out.println("已经成功创建用例，ID："+caseid);
			Thread.sleep(500);
			int k=1;
			for(TestCaseStep step:cases.getSteps()){
				String stepsparams="";
				String resultstr = null;
				//获取actions字符串
				String stepsstr = step.getActions();    
				String scriptstr = InterfaceAnalyticTestLinkCase.subComment(stepsstr);

				if(scriptstr.substring(scriptstr.length()-6, scriptstr.length()).indexOf("*Wait;")>-1){
					String action="";
					action = scriptstr.substring(scriptstr.lastIndexOf("|")+1,scriptstr.lastIndexOf("*Wait;")+5);
		        	stepsparams="action="+action+"&";
		        	scriptstr = scriptstr.substring(0, scriptstr.lastIndexOf("|")+1);
		        }
				if(scriptstr.substring(scriptstr.length()-6, scriptstr.length()).indexOf("*wait;")>-1){
					String action="";
					action = scriptstr.substring(scriptstr.lastIndexOf("|")+1,scriptstr.lastIndexOf("*wait;")+5);
		        	stepsparams="action="+action+"&";
		        	scriptstr = scriptstr.substring(0, scriptstr.lastIndexOf("|")+1);
		        }
				//获取预期结果字符串
				resultstr = InterfaceAnalyticTestLinkCase.subComment(step.getExpectedResults());   
				stepsparams+="expectedresult="+resultstr.replace("%", "BBFFHH");
				stepsparams+="&caseid="+caseid;
				stepsparams+="&stepnum="+k;
				stepsparams+="&projectid="+projectid;
				stepsparams+="&steptype=1";
				String[] temp=scriptstr.split("\\|",-1);
				for(int i=0;i<temp.length;i++){
					if(i==0&&temp[i].indexOf("=")>-1&&(temp.length>2||!"".equals(temp[1]))){
						//set包名		
						stepsparams+="&path="+temp[i].replace("=", "DHDHDH");   			
					}else if(temp[i].equals("")){
						continue;
					}else{
						String operation = null;
						String operationValue = null;
						if(temp[i].indexOf("(")>-1&&temp[i].indexOf(")")>-1){
							operation = temp[i].substring(0, temp[i].indexOf("("));
							operationValue = temp[i].substring(temp[i].indexOf("(")+1, temp[i].lastIndexOf(")"));
						}else{
							operation = temp[i];
						}
						//set方法名称
						stepsparams+="&operation="+operation.toLowerCase();   
						if(null!=operationValue){
							//set方法名称
							stepsparams+="&parameters="+operationValue.replace("%", "BBFFHH");   
						}						
					}
				}
				String stepid=HttpRequest.sendPost("/projectCasesteps/cpoststep.do", stepsparams);
				System.out.println("已经成功创建用例步骤，用例ID:"+caseid+"  步骤ID:"+stepid);
				k++;
				Thread.sleep(500);
			}
			System.out.println("第"+casecount+"条用例生成完成！"+cases.getFullExternalId());
			casecount++;
		}
		System.out.println(testplan+"中的用例已经全部生成完毕！");
	}
	
	
	public static void main(String[] args){

	}
}
