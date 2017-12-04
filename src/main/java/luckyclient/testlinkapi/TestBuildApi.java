package luckyclient.testlinkapi;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.util.TestLinkAPIException;
import luckyclient.caserun.exinterface.TestControl;
import luckyclient.dblog.LogOperation;
/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 * @ClassName: TestBuildApi 
 * @Description: 二次封装关于测试版本部分的API 
 * @author： seagull
 * @date 2014年6月24日 上午9:29:40  
 * 
 */
public class TestBuildApi extends TestLinkBaseApi {
	/**
	 * @param args
	 */
	public static Integer createBuild(String projectname){
	    Build result = new Build();
		Date nowTime = new Date(System.currentTimeMillis());
		SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String retStrFormatNowDate = sdFormatter.format(nowTime);
	    String buildName = "自动化测试-"+retStrFormatNowDate;
		try{
		    //能过项目名称以及计划名称获取计划ID
		    TestPlan testplanob = new TestPlan();
		  //  testplanob = api.getTestPlanByName(TestPlanName(projectname), projectname);
		    if(TestControl.TASKID.indexOf("NULL")>-1||LogOperation.getTestPlanName(TestControl.TASKID).indexOf("NULL")>-1){
		    	testplanob = api.getTestPlanByName(testPlanName(projectname), projectname);
		    }else{
		    	testplanob = api.getTestPlanByName(LogOperation.getTestPlanName(TestControl.TASKID), projectname);
		    }
		    Integer planid = testplanob.getId();
		    
		    //创建构建到测试计划
	        result = api.createBuild(planid, buildName, "自动化测试日期："+retStrFormatNowDate);     	        

	        luckyclient.publicclass.LogUtil.APP.info(projectname+"项目的版本创建成功！版本ID："+result.getId()+" 版本名称："+buildName);
			return result.getId();
	         
		}catch( TestLinkAPIException te) {
            te.printStackTrace( System.err );
            luckyclient.publicclass.LogUtil.ERROR.error(projectname+"版本创建失败！");
            return 999999;
     }
	}
	
	/**
	 * 当没在测试计划中找到版本后，自动创建一个版本
	 */
	public static void getBuild(String projectname){
	    //能过项目名称以及计划名称获取计划ID
	    TestPlan testplanob = new TestPlan();
	   // testplanob = api.getTestPlanByName(TestPlanName(projectname), projectname);
	    if(TestControl.TASKID.indexOf("NULL")>-1||LogOperation.getTestPlanName(TestControl.TASKID).indexOf("NULL")>-1){
	    	testplanob = api.getTestPlanByName(testPlanName(projectname), projectname);
	    }else{
	    	testplanob = api.getTestPlanByName(LogOperation.getTestPlanName(TestControl.TASKID), projectname);
	    }
	    Integer planid = testplanob.getId();
	    try{
			Build lastbuild = api.getLatestBuildForTestPlan(planid);
			luckyclient.publicclass.LogUtil.APP.info("测试计划 "+testplanob.getName()+" 下找到一个测试版本，用例将在此版本下执行！版本名："+lastbuild.getName());
	    }catch( TestLinkAPIException te){
	    	luckyclient.publicclass.LogUtil.APP.error("测试计划 "+testplanob.getName()+" 下没有找到测试版本，无法设置测试结果，现在将先为你创建一个测试版本！");
	    	TestBuildApi.createBuild(projectname);
	    }
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//创建构建
//		System.out.println(createBuild(""));
	}

}
