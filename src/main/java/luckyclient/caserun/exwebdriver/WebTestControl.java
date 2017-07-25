package luckyclient.caserun.exwebdriver;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.WebDriver;

import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import luckyclient.caserun.exinterface.TestControl;
import luckyclient.caserun.exwebdriver.ex.WebCaseExecution;
import luckyclient.caserun.exwebdriver.extestlink.WebCaseExecutionTestLink;
import luckyclient.dblog.DbLink;
import luckyclient.dblog.LogOperation;
import luckyclient.jenkinsapi.BuildingInitialization;
import luckyclient.jenkinsapi.RestartServerInitialization;
import luckyclient.mail.HtmlMail;
import luckyclient.mail.MailSendInitialization;
import luckyclient.planapi.api.GetServerAPI;
import luckyclient.planapi.entity.ProjectCase;
import luckyclient.planapi.entity.ProjectCasesteps;
import luckyclient.planapi.entity.TestTaskexcute;
import luckyclient.testlinkapi.TestBuildApi;
import luckyclient.testlinkapi.TestCaseApi;

public class WebTestControl{
	
	/**
	 * @param args
	 * @throws ClassNotFoundException
	 * 控制台模式调度计划执行用例 
	 */
	
	public  static void ManualExecutionPlan(String planname){
		DbLink.exetype = 1;   //不记日志到数据库
		String taskid = "888888";
		WebDriver wd = null;
		try {
			wd = WebDriverInitialization.setWebDriverForLocal();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LogOperation caselog = new LogOperation(); // 初始化写用例结果以及日志模块
		List<ProjectCase> testCases=GetServerAPI.getCasesbyplanname(planname);
		luckyclient.publicclass.LogUtil.APP.info("当前计划中读取到用例共 "+testCases.size()+" 个");
		int i=0;
		for(ProjectCase testcase:testCases){
			List<ProjectCasesteps> steps=GetServerAPI.getStepsbycaseid(testcase.getId());
			if(steps.size()==0){
				continue;
			}
			i++;
			luckyclient.publicclass.LogUtil.APP.info("开始执行第"+i+"条用例：【"+testcase.getSign()+"】......");
			try {
				WebCaseExecution.CaseExcution(testcase,steps,taskid,wd,caselog);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				luckyclient.publicclass.LogUtil.APP.error("用户执行过程中抛出异常！", e);
				e.printStackTrace();
			}
			luckyclient.publicclass.LogUtil.APP.info("当前用例：【"+testcase.getSign()+"】执行完成......进入下一条");
		}
		luckyclient.publicclass.LogUtil.APP.info("当前项目测试计划中的用例已经全部执行完成...");
        //关闭浏览器
        wd.quit();
	}
	
	public static void TaskExecutionPlan(String taskid,TestTaskexcute task) throws InterruptedException {
		DbLink.exetype = 0; // 记录日志到数据库
		TestControl.TASKID = taskid;
		String restartstatus = RestartServerInitialization.RestartServerRun(taskid);
		String buildstatus = BuildingInitialization.BuildingRun(taskid);
		String projectname=task.getTestJob().getPlanproj();
		task=GetServerAPI.cgetTaskbyid(Integer.valueOf(taskid));
		String jobname = task.getTestJob().getTaskName();
		int drivertype = LogOperation.Querydrivertype(taskid);
		// 判断是否要自动重启TOMCAT
		if (restartstatus.indexOf("Status:true") > -1) {
			// 判断是否构建是否成功
			if (buildstatus.indexOf("Status:true") > -1) {
				WebDriver wd = null;
				try {
					wd = WebDriverInitialization.setWebDriverForTask(taskid,drivertype);
				} catch (IOException e1) {
					luckyclient.publicclass.LogUtil.APP.error("初始化WebDriver出错！", e1);
					e1.printStackTrace();
				}
				LogOperation caselog = new LogOperation(); // 初始化写用例结果以及日志模块
				int[] tastcount=null;
				if(task.getTestJob().getProjecttype()==0){
					TestBuildApi.GetBuild(projectname);
					TestCase[] testCases = TestCaseApi.getplantestcase(projectname, taskid,"");
					luckyclient.publicclass.LogUtil.APP.info("当前计划中读取到用例共 " + testCases.length + " 个");
					LogOperation.UpdateTastStatus(taskid,testCases.length);
					
					for (TestCase testcase : testCases) {
						if (testcase.getSteps().size() == 0) {
							continue;
						}
						luckyclient.publicclass.LogUtil.APP.info("开始执行用例：【" + testcase.getFullExternalId() + "】......");
						try {
							WebCaseExecutionTestLink.CaseExcution(projectname, testcase, taskid, wd, caselog);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							luckyclient.publicclass.LogUtil.APP.error("用户执行过程中抛出异常！", e);
							e.printStackTrace();
						}
						luckyclient.publicclass.LogUtil.APP.info("当前用例：【" + testcase.getFullExternalId() + "】执行完成......进入下一条");
					}
					tastcount = LogOperation.UpdateTastdetail(taskid, testCases.length);
				}else if(task.getTestJob().getProjecttype()==1){
					List<ProjectCase> cases=GetServerAPI.getCasesbyplanid(task.getTestJob().getPlanid());
					luckyclient.publicclass.LogUtil.APP.info("当前计划中读取到用例共 " + cases.size() + " 个");
					LogOperation.UpdateTastStatus(taskid,cases.size());
					
					for (ProjectCase testcase : cases) {
						List<ProjectCasesteps> steps=GetServerAPI.getStepsbycaseid(testcase.getId());
						if (steps.size() == 0) {
							continue;
						}
						luckyclient.publicclass.LogUtil.APP.info("开始执行用例：【" + testcase.getSign() + "】......");
						try {
							WebCaseExecution.CaseExcution(testcase, steps, taskid, wd, caselog);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							luckyclient.publicclass.LogUtil.APP.error("用户执行过程中抛出异常！", e);
							e.printStackTrace();
						}
						luckyclient.publicclass.LogUtil.APP.info("当前用例：【" + testcase.getSign() + "】执行完成......进入下一条");
					}
					tastcount = LogOperation.UpdateTastdetail(taskid, cases.size());
				}
				String testtime = LogOperation.GetTestTime(taskid);
				luckyclient.publicclass.LogUtil.APP.info("当前项目【" + projectname + "】测试计划中的用例已经全部执行完成...");
				MailSendInitialization.SendMailInitialization(HtmlMail.HtmlSubjectFormat(jobname),
						HtmlMail.HtmlContentFormat(tastcount, taskid, buildstatus, restartstatus, testtime,jobname), taskid);
				// 关闭浏览器
				wd.quit();
			} else {
				luckyclient.publicclass.LogUtil.APP.error("项目构建失败，自动化测试自动退出！请前往JENKINS中检查项目构建情况。");
				MailSendInitialization.SendMailInitialization(jobname,
						"构建项目过程中失败，自动化测试自动退出！请前去JENKINS查看构建情况！", taskid);
			}
		} else {
			luckyclient.publicclass.LogUtil.APP.error("项目TOMCAT重启失败，自动化测试自动退出！请检查项目TOMCAT运行情况。");
			MailSendInitialization.SendMailInitialization(jobname,
					"项目TOMCAT重启失败，自动化测试自动退出！请检查项目TOMCAT运行情况！", taskid);
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			PropertyConfigurator.configure(System.getProperty("user.dir")
					+ "\\log4j.conf");
			//ManualExecutionPlan("automation test");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
