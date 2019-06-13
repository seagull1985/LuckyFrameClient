package luckyclient.caserun.exwebdriver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import luckyclient.caserun.exinterface.TestControl;
import luckyclient.caserun.exwebdriver.ex.WebCaseExecution;
import luckyclient.dblog.DbLink;
import luckyclient.dblog.LogOperation;
import luckyclient.jenkinsapi.BuildingInitialization;
import luckyclient.jenkinsapi.RestartServerInitialization;
import luckyclient.mail.HtmlMail;
import luckyclient.mail.MailSendInitialization;
import luckyclient.publicclass.LogUtil;
import luckyclient.serverapi.api.GetServerAPI;
import luckyclient.serverapi.entity.ProjectCase;
import luckyclient.serverapi.entity.ProjectCaseParams;
import luckyclient.serverapi.entity.ProjectCaseSteps;
import luckyclient.serverapi.entity.TaskExecute;
import luckyclient.serverapi.entity.TaskScheduling;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 seagull1985
 * =================================================================
 * 
 * @author： seagull
 * 
 * @date 2017年12月1日 上午9:29:40
 * 
 */
public class WebTestControl {

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 *             控制台模式调度计划执行用例
	 */

	public static void manualExecutionPlan(String planname) {
		// 不记日志到数据库
		DbLink.exetype = 1;
		String taskid = "888888";
		WebDriver wd = null;
		try {
			wd = WebDriverInitialization.setWebDriverForLocal();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogUtil.APP.error("初始化WebDriver出现异常！",e);
		}
		LogOperation caselog = new LogOperation();
		List<ProjectCase> testCases = GetServerAPI.getCasesbyplanname(planname);
		List<ProjectCaseParams> pcplist = new ArrayList<ProjectCaseParams>();
		if (testCases.size() != 0) {
			pcplist = GetServerAPI.cgetParamsByProjectid(String.valueOf(testCases.get(0).getProjectId()));
		}
		LogUtil.APP.info("当前计划中读取到用例共【{}】个",testCases.size());
		int i = 0;
		for (ProjectCase testcase : testCases) {
			List<ProjectCaseSteps> steps = GetServerAPI.getStepsbycaseid(testcase.getCaseId());
			if (steps.size() == 0) {
				continue;
			}
			i++;
			LogUtil.APP.info("开始执行第{}条用例:【{}】......",i,testcase.getCaseSign());
			try {
				WebCaseExecution.caseExcution(testcase, steps, taskid, wd, caselog, pcplist);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				LogUtil.APP.error("用户执行过程中抛出异常！", e);
			}
			LogUtil.APP.info("当前用例:【{}】执行完成......进入下一条",testcase.getCaseSign());
		}
		LogUtil.APP.info("当前项目测试计划中的用例已经全部执行完成...");
		// 关闭浏览器
		wd.quit();
	}

	public static void taskExecutionPlan(TaskExecute task) throws InterruptedException {
		// 记录日志到数据库
		DbLink.exetype = 0;
		String taskid = task.getTaskId().toString();
		TestControl.TASKID = taskid;
		String restartstatus = RestartServerInitialization.restartServerRun(taskid);
		String buildstatus = BuildingInitialization.buildingRun(taskid);
		List<ProjectCaseParams> pcplist = GetServerAPI.cgetParamsByProjectid(task.getProjectId().toString());
		TaskScheduling taskScheduling = GetServerAPI.cGetTaskSchedulingByTaskId(task.getTaskId());
		String projectname = taskScheduling.getProject().getProjectName();
		task = GetServerAPI.cgetTaskbyid(Integer.valueOf(taskid));
		String jobname = taskScheduling.getSchedulingName();
		int drivertype = LogOperation.querydrivertype(taskid);
		int[] tastcount = null;
		// 判断是否要自动重启TOMCAT
		if (restartstatus.indexOf("Status:true") > -1) {
			// 判断是否构建是否成功
			if (buildstatus.indexOf("Status:true") > -1) {
				WebDriver wd = null;
				try {
					wd = WebDriverInitialization.setWebDriverForTask(drivertype);
				} catch (WebDriverException e1) {
					LogUtil.APP.error("初始化WebDriver出错 WebDriverException！", e1);
				} catch (IOException e2) {
					LogUtil.APP.error("初始化WebDriver出错 IOException！", e2);
				}
				LogOperation caselog = new LogOperation();

				List<ProjectCase> cases = GetServerAPI.getCasesbyplanId(taskScheduling.getPlanId());
				LogUtil.APP.info("当前计划【{}】中共有【{}】条待测试用例...",task.getTaskName(),cases.size());
				LogOperation.updateTaskExecuteStatus(taskid, cases.size());

				for (ProjectCase testcase : cases) {
					List<ProjectCaseSteps> steps = GetServerAPI.getStepsbycaseid(testcase.getCaseId());
					if (steps.size() == 0) {
						continue;
					}
					LogUtil.APP.info("开始执行用例:【{}】......",testcase.getCaseSign());
					try {
						// 插入开始执行的用例
						caselog.insertTaskCaseExecute(taskid, taskScheduling.getProjectId(),testcase.getCaseId(),testcase.getCaseSign(), testcase.getCaseName(), 4);
						WebCaseExecution.caseExcution(testcase, steps, taskid, wd, caselog, pcplist);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						LogUtil.APP.error("用户执行过程中抛出异常！", e);
					}
					LogUtil.APP.info("当前用例:【{}】执行完成......进入下一条",testcase.getCaseSign());
				}
				tastcount = LogOperation.updateTaskExecuteData(taskid, cases.size());

				String testtime = LogOperation.getTestTime(taskid);
				LogUtil.APP.info("当前项目【{}】测试计划中的用例已经全部执行完成...",projectname);
				MailSendInitialization.sendMailInitialization(HtmlMail.htmlSubjectFormat(jobname),
						HtmlMail.htmlContentFormat(tastcount, taskid, buildstatus, restartstatus, testtime, jobname),
						taskid, taskScheduling, tastcount);
				// 关闭浏览器
				wd.quit();
			} else {
				LogUtil.APP.warn("项目构建失败，自动化测试自动退出！请前往JENKINS中检查项目构建情况。");
				MailSendInitialization.sendMailInitialization(jobname, "构建项目过程中失败，自动化测试自动退出！请前去JENKINS查看构建情况！", taskid,
						taskScheduling, tastcount);
			}
		} else {
			LogUtil.APP.warn("项目TOMCAT重启失败，自动化测试自动退出！请检查项目TOMCAT运行情况。");
			MailSendInitialization.sendMailInitialization(jobname, "项目TOMCAT重启失败，自动化测试自动退出！请检查项目TOMCAT运行情况！", taskid,
					taskScheduling, tastcount);
		}
	}

}
