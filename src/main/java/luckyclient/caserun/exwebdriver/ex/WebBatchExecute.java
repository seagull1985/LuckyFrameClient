package luckyclient.caserun.exwebdriver.ex;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.openqa.selenium.WebDriver;

import luckyclient.caserun.exinterface.TestControl;
import luckyclient.caserun.exwebdriver.WebDriverInitialization;
import luckyclient.dblog.DbLink;
import luckyclient.dblog.LogOperation;
import luckyclient.planapi.api.GetServerAPI;
import luckyclient.planapi.entity.ProjectCase;
import luckyclient.planapi.entity.ProjectCasesteps;
import luckyclient.testlinkapi.TestBuildApi;

public class WebBatchExecute{
	
	@SuppressWarnings("static-access")
	public static void BatchCaseExecuteForTast(String projectname,String taskid,String batchcase) throws IOException{
		DbLink.exetype = 0;   //记录日志到数据库
		TestControl.TASKID = taskid;
		int drivertype = LogOperation.Querydrivertype(taskid);
		WebDriver wd = null;
		try {
			wd = WebDriverInitialization.setWebDriverForTask(taskid,drivertype);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		LogOperation caselog = new LogOperation(); // 初始化写用例结果以及日志模块
		TestBuildApi.GetBuild(projectname);
		if(batchcase.indexOf("ALLFAIL")>-1){    //执行全部非成功状态用例
			String casemore = caselog.UnSucCaseUpdate(taskid);
			String temp[]=casemore.split("\\#",-1);
			for(int i=0;i<temp.length;i++){
  			   String testCaseExternalId = temp[i].substring(0, temp[i].indexOf("%"));
			   //int version = Integer.parseInt(temp[i].substring(temp[i].indexOf("%")+1,temp[i].length()-1));
			   ProjectCase testcase = GetServerAPI.cgetCaseBysign(testCaseExternalId);
			   List<ProjectCasesteps> steps=GetServerAPI.getStepsbycaseid(testcase.getId());
			   caselog.DeleteCaseDetail(testCaseExternalId, taskid);   //删除旧的用例
			   caselog.DeleteCaseLogDetail(testCaseExternalId, taskid);    //删除旧的日志
			   try {
				WebCaseExecution.CaseExcution(testcase, steps, taskid,wd,caselog);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				luckyclient.publicclass.LogUtil.APP.error("用户执行过程中抛出异常！", e);
				e.printStackTrace();
			 }
			}			
		}else{                                           //批量执行用例
			String temp[]=batchcase.split("\\#",-1);
			for(int i=0;i<temp.length;i++){
				String testCaseExternalId = temp[i].substring(0, temp[i].indexOf("%"));
				//int version = Integer.parseInt(temp[i].substring(temp[i].indexOf("%")+1,temp[i].length()));
				ProjectCase testcase = GetServerAPI.cgetCaseBysign(testCaseExternalId);
				List<ProjectCasesteps> steps=GetServerAPI.getStepsbycaseid(testcase.getId());
				try {
					WebCaseExecution.CaseExcution(testcase, steps,taskid,wd,caselog);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					luckyclient.publicclass.LogUtil.APP.error("用户执行过程中抛出异常！", e);
					e.printStackTrace();
				}
			}
		}
        //关闭浏览器
        wd.quit();
	}
	
}
