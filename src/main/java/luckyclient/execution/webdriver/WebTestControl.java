package luckyclient.execution.webdriver;

import com.offbytwo.jenkins.model.BuildResult;
import luckyclient.execution.httpinterface.TestControl;
import luckyclient.execution.webdriver.ex.WebCaseExecution;
import luckyclient.remote.api.GetServerApi;
import luckyclient.remote.api.serverOperation;
import luckyclient.remote.entity.*;
import luckyclient.tool.jenkins.BuildingInitialization;
import luckyclient.tool.mail.HtmlMail;
import luckyclient.tool.mail.MailSendInitialization;
import luckyclient.tool.shell.RestartServerInitialization;
import luckyclient.utils.LogUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	 * 控制台模式调度计划执行用例
	 * @param planname 计划名称
	 */
	public static void manualExecutionPlan(String planname) {
		// 不记日志到数据库
		serverOperation.exetype = 1;
		String taskid = "888888";
		WebDriver wd = null;
		try {
			wd = WebDriverInitialization.setWebDriverForLocal();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogUtil.APP.error("初始化WebDriver出现异常！",e);
		}
		serverOperation caselog = new serverOperation();
		List<ProjectCase> testCases = GetServerApi.getCasesbyplanname(planname);
		List<ProjectCaseParams> pcplist = new ArrayList<>();
		if (testCases.size() != 0) {
			pcplist = GetServerApi.cgetParamsByProjectid(String.valueOf(testCases.get(0).getProjectId()));
		}
		LogUtil.APP.info("当前计划中读取到用例共【{}】个",testCases.size());
		int i = 0;
		for (ProjectCase testcase : testCases) {
			List<ProjectCaseSteps> steps = GetServerApi.getStepsbycaseid(testcase.getCaseId());
			if (steps.size() == 0) {
				continue;
			}
			i++;
			LogUtil.APP.info("开始执行第{}条用例:【{}】......",i,testcase.getCaseSign());
			try {
				WebCaseExecution.caseExcution(testcase, steps, taskid, null,wd, caselog, pcplist);
			} catch (Exception e) {
				LogUtil.APP.error("用户执行过程中抛出异常！", e);
			}
			LogUtil.APP.info("当前用例:【{}】执行完成......进入下一条",testcase.getCaseSign());
		}
		LogUtil.APP.info("当前项目测试计划中的用例已经全部执行完成...");
		// 关闭浏览器
		assert wd != null;
		wd.quit();
	}

	public static void taskExecutionPlan(TaskExecute task) {
		// 记录日志到数据库
		serverOperation.exetype = 0;
		String taskid = task.getTaskId().toString();
		TestControl.TASKID = taskid;
		String restartstatus = RestartServerInitialization.restartServerRun(taskid);
		BuildResult buildResult = BuildingInitialization.buildingRun(taskid);
		TaskScheduling taskScheduling = GetServerApi.cGetTaskSchedulingByTaskId(task.getTaskId());
		List<ProjectCaseParams> pcplist = GetServerApi.cgetParamsByProjectid(task.getProjectId().toString());
		String projectname = taskScheduling.getProject().getProjectName();
		task = GetServerApi.cgetTaskbyid(Integer.parseInt(taskid));
		String jobname = taskScheduling.getSchedulingName();
		int drivertype = serverOperation.querydrivertype(taskid);
		int[] tastcount=new int[5];
		// 判断是否要自动重启TOMCAT
		if (restartstatus.contains("Status:true")) {
			// 判断是否构建是否成功
			if (BuildResult.SUCCESS.equals(buildResult)) {

				List<ProjectPlan> plans=new ArrayList<>();
				// 单计划执行
				if(taskScheduling.getPlanType()==1){
					ProjectPlan projectPlan=new ProjectPlan();
					projectPlan.setPlanId(taskScheduling.getPlanId());
					plans.add(projectPlan);
				}
				// 聚合多计划执行
				else if(taskScheduling.getPlanType()==2){
					plans.addAll(GetServerApi.getPlansbysuiteId(taskScheduling.getSuiteId()));
				}
				LogUtil.APP.info("当前测试任务 {} 中共有【{}】条测试计划...",task.getTaskName(),plans.size());

				int caseCount=0;
				for(ProjectPlan pp:plans){
					List<ProjectCase> cases = GetServerApi.getCasesbyplanId(pp.getPlanId());
					caseCount+=cases.size();
				}

				for(ProjectPlan pp:plans){
					WebDriver wd = null;
					try {
						wd = WebDriverInitialization.setWebDriverForTask(drivertype);
					} catch (WebDriverException e1) {
						LogUtil.APP.error("初始化WebDriver出错 WebDriverException！", e1);
					} catch (IOException e2) {
						LogUtil.APP.error("初始化WebDriver出错 IOException！", e2);
					}

					serverOperation caselog = new serverOperation();
					List<ProjectCase> cases = GetServerApi.getCasesbyplanId(pp.getPlanId());
					LogUtil.APP.info("当前测试计划 {} 中共有【{}】条待测试用例...",pp.getPlanName(),cases.size());
					LogUtil.APP.info("开始执行当前测试计划 {} ......",pp.getPlanName());
					serverOperation.updateTaskExecuteStatusIng(taskid, caseCount);
					int i = 0;
					for (ProjectCase testcase : cases) {
						i++;
						LogUtil.APP.info("开始执行当前测试任务 {} 的第【{}】条测试用例:【{}】......",task.getTaskName(),i,testcase.getCaseSign());
						List<ProjectCaseSteps> steps = GetServerApi.getStepsbycaseid(testcase.getCaseId());
						if (steps.size() == 0) {
							continue;
						}
						try {
							// 插入开始执行的用例
							caselog.insertTaskCaseExecute(taskid, taskScheduling.getProjectId(),pp.getPlanId(),testcase.getCaseId(),testcase.getCaseSign(), testcase.getCaseName(), 4);
							WebCaseExecution.caseExcution(testcase, steps, taskid,pp.getPlanId(),wd, caselog, pcplist);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							LogUtil.APP.error("用户执行过程中抛出异常！", e);
						}
						LogUtil.APP.info("当前用例:【{}】执行完成......进入下一条",testcase.getCaseSign());
					}
					LogUtil.APP.info("当前【{}】测试计划中的用例已经全部执行完成...",pp.getPlanName());
					assert wd != null;
					wd.quit();
				}

				tastcount = serverOperation.updateTaskExecuteData(taskid, caseCount,2);
				tastcount[0]=caseCount;
				String testtime = serverOperation.getTestTime(taskid);
				LogUtil.APP.info("当前项目【{}】测试计划中的用例已经全部执行完成...",projectname);
				MailSendInitialization.sendMailInitialization(HtmlMail.htmlSubjectFormat(jobname),
						HtmlMail.htmlContentFormat(tastcount, taskid, buildResult.toString(), restartstatus, testtime, jobname),
						taskid, taskScheduling, tastcount,testtime,buildResult.toString(),restartstatus);
			} else {
				LogUtil.APP.warn("项目构建失败，自动化测试自动退出！请前往JENKINS中检查项目构建情况。");
				MailSendInitialization.sendMailInitialization(jobname, "构建项目过程中失败，自动化测试自动退出！请前去JENKINS查看构建情况！", taskid,
						taskScheduling, null,"0小时0分0秒",buildResult.toString(),restartstatus);
			}
		} else {
			LogUtil.APP.warn("项目TOMCAT重启失败，自动化测试自动退出！请检查项目TOMCAT运行情况。");
			MailSendInitialization.sendMailInitialization(jobname, "项目TOMCAT重启失败，自动化测试自动退出！请检查项目TOMCAT运行情况！", taskid,
					taskScheduling, null,"0小时0分0秒",buildResult.toString(),restartstatus);
		}
	}

}
