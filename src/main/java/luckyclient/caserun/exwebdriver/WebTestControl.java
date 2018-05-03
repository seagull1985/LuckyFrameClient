package luckyclient.caserun.exwebdriver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

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
import luckyclient.planapi.entity.PublicCaseParams;
import luckyclient.planapi.entity.TestTaskexcute;
import luckyclient.testlinkapi.TestBuildApi;
import luckyclient.testlinkapi.TestCaseApi;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 * 
 * @author： seagull
 * @date 2017年12月1日 上午9:29:40
 * 
 */
public class WebTestControl{
	
	/**
	 * @param args
	 * @throws ClassNotFoundException
	 * 控制台模式调度计划执行用例 
	 */
	
	public  static void manualExecutionPlan(String planname){
		//不记日志到数据库
		DbLink.exetype = 1;   
		String taskid = "888888";
		WebDriver wd = null;
		try {
			wd = WebDriverInitialization.setWebDriverForLocal();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LogOperation caselog = new LogOperation();
		List<ProjectCase> testCases=GetServerAPI.getCasesbyplanname(planname);
		List<PublicCaseParams> pcplist = new ArrayList<PublicCaseParams>();
		if(testCases.size()!=0){
			pcplist=GetServerAPI.cgetParamsByProjectid(String.valueOf(testCases.get(0).getProjectid()));
		}
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
				WebCaseExecution.caseExcution(testcase,steps,taskid,wd,caselog,pcplist);
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
	
	public static void taskExecutionPlan(String taskid,TestTaskexcute task) throws InterruptedException {
		// 记录日志到数据库
		DbLink.exetype = 0; 
		TestControl.TASKID = taskid;
		String restartstatus = RestartServerInitialization.restartServerRun(taskid);
		String buildstatus = BuildingInitialization.buildingRun(taskid);
		List<PublicCaseParams> pcplist=GetServerAPI.cgetParamsByProjectid(task.getTestJob().getProjectid().toString());
		String projectname=task.getTestJob().getPlanproj();
		task=GetServerAPI.cgetTaskbyid(Integer.valueOf(taskid));
		String jobname = task.getTestJob().getTaskName();
		int drivertype = LogOperation.querydrivertype(taskid);
		// 判断是否要自动重启TOMCAT
		if (restartstatus.indexOf("Status:true") > -1) {
			// 判断是否构建是否成功
			if (buildstatus.indexOf("Status:true") > -1) {
				WebDriver wd = null;
				try {
				    wd = WebDriverInitialization.setWebDriverForTask(drivertype);
				} catch (WebDriverException e1) {
					luckyclient.publicclass.LogUtil.APP.error("初始化WebDriver出错 WebDriverException！", e1);
					e1.printStackTrace();
				} catch (IOException e2) {
					luckyclient.publicclass.LogUtil.APP.error("初始化WebDriver出错 IOException！", e2);
					e2.printStackTrace();
				}
				LogOperation caselog = new LogOperation(); 
				int[] tastcount=null;
				if(task.getTestJob().getProjecttype()==1){
					TestBuildApi.getBuild(projectname);
					TestCase[] testCases = TestCaseApi.getplantestcase(projectname, taskid,"");
					luckyclient.publicclass.LogUtil.APP.info("当前计划中读取到用例共 " + testCases.length + " 个");
					LogOperation.updateTastStatus(taskid,testCases.length);
					
					for (TestCase testcase : testCases) {
						if (testcase.getSteps().size() == 0) {
							continue;
						}
						luckyclient.publicclass.LogUtil.APP.info("开始执行用例：【" + testcase.getFullExternalId() + "】......");
						try {
							WebCaseExecutionTestLink.caseExcution(projectname, testcase, taskid, wd, caselog);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							luckyclient.publicclass.LogUtil.APP.error("用户执行过程中抛出异常！", e);
							e.printStackTrace();
						}
						luckyclient.publicclass.LogUtil.APP.info("当前用例：【" + testcase.getFullExternalId() + "】执行完成......进入下一条");
					}
					tastcount = LogOperation.updateTastdetail(taskid, testCases.length);
				}else if(task.getTestJob().getProjecttype()==0){
					List<ProjectCase> cases=GetServerAPI.getCasesbyplanid(task.getTestJob().getPlanid());
					luckyclient.publicclass.LogUtil.APP.info("当前计划中读取到用例共 " + cases.size() + " 个");
					LogOperation.updateTastStatus(taskid,cases.size());
					
					for (ProjectCase testcase : cases) {
						List<ProjectCasesteps> steps=GetServerAPI.getStepsbycaseid(testcase.getId());
						if (steps.size() == 0) {
							continue;
						}
						luckyclient.publicclass.LogUtil.APP.info("开始执行用例：【" + testcase.getSign() + "】......");
						try {
							WebCaseExecution.caseExcution(testcase, steps, taskid, wd, caselog,pcplist);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							luckyclient.publicclass.LogUtil.APP.error("用户执行过程中抛出异常！", e);
							e.printStackTrace();
						}
						luckyclient.publicclass.LogUtil.APP.info("当前用例：【" + testcase.getSign() + "】执行完成......进入下一条");
					}
					tastcount = LogOperation.updateTastdetail(taskid, cases.size());
				}
				String testtime = LogOperation.getTestTime(taskid);
				luckyclient.publicclass.LogUtil.APP.info("当前项目【" + projectname + "】测试计划中的用例已经全部执行完成...");
				MailSendInitialization.sendMailInitialization(HtmlMail.htmlSubjectFormat(jobname),
						HtmlMail.htmlContentFormat(tastcount, taskid, buildstatus, restartstatus, testtime,jobname), taskid);
				// 关闭浏览器
				wd.quit();
			} else {
				luckyclient.publicclass.LogUtil.APP.error("项目构建失败，自动化测试自动退出！请前往JENKINS中检查项目构建情况。");
				MailSendInitialization.sendMailInitialization(jobname,
						"构建项目过程中失败，自动化测试自动退出！请前去JENKINS查看构建情况！", taskid);
			}
		} else {
			luckyclient.publicclass.LogUtil.APP.error("项目TOMCAT重启失败，自动化测试自动退出！请检查项目TOMCAT运行情况。");
			MailSendInitialization.sendMailInitialization(jobname,
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
