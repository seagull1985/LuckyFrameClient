package luckyclient.caserun.exwebdriver.extestlink;

import java.io.IOException;

import org.openqa.selenium.WebDriver;

import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import luckyclient.caserun.exinterface.TestControl;
import luckyclient.caserun.exwebdriver.WebDriverInitialization;
import luckyclient.dblog.DbLink;
import luckyclient.dblog.LogOperation;
import luckyclient.testlinkapi.TestBuildApi;
import luckyclient.testlinkapi.TestCaseApi;

public class WebOneCaseExecuteTestLink{
	
	@SuppressWarnings("static-access")
	public static void OneCaseExecuteForTast(String projectname,String testCaseExternalId,int version,String taskid){
		DbLink.exetype = 0;   //记录日志到数据库
		TestControl.TASKID = taskid;
		int drivertype = LogOperation.Querydrivertype(taskid);
		WebDriver wd = null;
		try {
			wd = WebDriverInitialization.setWebDriverForTask(taskid,drivertype);
		} catch (IOException e1) {
			luckyclient.publicclass.LogUtil.APP.error("初始化WebDriver出错！", e1);
			e1.printStackTrace();
		}
		LogOperation caselog = new LogOperation(); // 初始化写用例结果以及日志模块
		caselog.DeleteCaseDetail(testCaseExternalId, taskid);   //删除旧的用例
		caselog.DeleteCaseLogDetail(testCaseExternalId, taskid);    //删除旧的日志
		TestBuildApi.GetBuild(projectname);
		TestCase testcase = TestCaseApi.getTestCaseByExternalId(testCaseExternalId, version);
		luckyclient.publicclass.LogUtil.APP.info("开始执行用例：【"+testCaseExternalId+"】......");
		try {
			WebCaseExecutionTestLink.CaseExcution(projectname,testcase, taskid,wd,caselog);
			luckyclient.publicclass.LogUtil.APP.info("当前用例：【"+testcase.getFullExternalId()+"】执行完成......进入下一条");
		} catch (InterruptedException e) {
			luckyclient.publicclass.LogUtil.APP.error("用户执行过程中抛出异常！", e);
			e.printStackTrace();
		}
        //关闭浏览器
        wd.quit();
	}

}
