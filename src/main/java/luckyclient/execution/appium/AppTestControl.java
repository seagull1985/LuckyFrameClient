package luckyclient.execution.appium;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.offbytwo.jenkins.model.BuildResult;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import luckyclient.execution.appium.androidex.AndroidCaseExecution;
import luckyclient.execution.appium.iosex.IosCaseExecution;
import luckyclient.execution.httpinterface.TestControl;
import luckyclient.remote.api.GetServerApi;
import luckyclient.remote.api.serverOperation;
import luckyclient.remote.entity.*;
import luckyclient.tool.jenkins.BuildingInitialization;
import luckyclient.tool.mail.HtmlMail;
import luckyclient.tool.mail.MailSendInitialization;
import luckyclient.tool.shell.RestartServerInitialization;
import luckyclient.utils.LogUtil;
import luckyclient.utils.config.AppiumConfig;

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
public class AppTestControl {

	/**
	 * 控制台模式调度计划执行用例
	 * @param planname 测试计划名称
	 */
	public static void manualExecutionPlan(String planname) {
		// 不记日志到数据库
		serverOperation.exetype = 1;
		String taskid = "888888";
		AndroidDriver<AndroidElement> androiddriver = null;
		IOSDriver<IOSElement> iosdriver = null;
		Properties properties = AppiumConfig.getConfiguration();
		try {
			if ("Android".equals(properties.getProperty("platformName"))) {
				androiddriver = AppiumInitialization.setAndroidAppium(properties);
			} else if ("IOS".equals(properties.getProperty("platformName"))) {
				iosdriver = AppiumInitialization.setIosAppium(properties);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogUtil.APP.error("控制台模式初始化Appium Driver异常！", e);
		}
		serverOperation caselog = new serverOperation();
		List<ProjectCase> testCases = GetServerApi.getCasesbyplanname(planname);
		List<ProjectCaseParams> pcplist = new ArrayList<>();
		if (testCases.size() != 0) {
			pcplist = GetServerApi.cgetParamsByProjectid(String.valueOf(testCases.get(0).getProjectId()));
		}
		LogUtil.APP.info("当前计划中读取到用例共{}个",testCases.size());
		int i = 0;
		for (ProjectCase testcase : testCases) {
			List<ProjectCaseSteps> steps = GetServerApi.getStepsbycaseid(testcase.getCaseId());
			if (steps.size() == 0) {
				continue;
			}
			i++;
			LogUtil.APP.info("开始执行计划中的第{}条用例：【{}】......",i,testcase.getCaseSign());
			try {
				if ("Android".equals(properties.getProperty("platformName"))) {
					AndroidCaseExecution.caseExcution(testcase, steps, taskid,null, androiddriver, caselog, pcplist);
				} else if ("IOS".equals(properties.getProperty("platformName"))) {
					IosCaseExecution.caseExcution(testcase, steps, taskid, null,iosdriver, caselog, pcplist);
				}
			} catch (Exception e) {
				LogUtil.APP.error("用户执行过程中抛出Exception异常！", e);
			}
			LogUtil.APP.info("当前用例：【{}】执行完成......进入下一条",testcase.getCaseSign());
		}
		LogUtil.APP.info("当前项目测试计划中的用例已经全部执行完成...");
		// 关闭APP以及appium会话
		if ("Android".equals(properties.getProperty("platformName"))) {
			assert androiddriver != null;
			androiddriver.closeApp();
		} else if ("IOS".equals(properties.getProperty("platformName"))) {
			assert iosdriver != null;
			iosdriver.closeApp();
		}
	}

	public static void taskExecutionPlan(TaskExecute task) throws InterruptedException {
		// 记录日志到数据库
		String taskId=task.getTaskId().toString();
		serverOperation.exetype = 0;
		TestControl.TASKID = taskId;
		AndroidDriver<AndroidElement> androiddriver = null;
		IOSDriver<IOSElement> iosdriver = null;
		Properties properties = AppiumConfig.getConfiguration();
		AppiumService as=null;
		//根据配置自动启动Appiume服务
		if(Boolean.parseBoolean(properties.getProperty("autoRunAppiumService"))){
			as =new AppiumService();
			as.start();
			Thread.sleep(10000);
		}
		TaskScheduling taskScheduling = GetServerApi.cGetTaskSchedulingByTaskId(task.getTaskId());
		String restartstatus = RestartServerInitialization.restartServerRun(taskId);
		BuildResult buildResult = BuildingInitialization.buildingRun(taskId);
		List<ProjectCaseParams> pcplist = GetServerApi
				.cgetParamsByProjectid(task.getProjectId().toString());
		String projectname = task.getProject().getProjectName();
		String jobname = GetServerApi.cGetTaskSchedulingByTaskId(task.getTaskId()).getSchedulingName();
        int[] tastcount;
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

					try {
						if ("Android".equals(properties.getProperty("platformName"))) {
							androiddriver = AppiumInitialization.setAndroidAppium(properties);
							LogUtil.APP.info("完成AndroidDriver初始化动作...APPIUM Server【http://{}/wd/hub】",properties.getProperty("appiumsever"));
						} else if ("IOS".equals(properties.getProperty("platformName"))) {
							iosdriver = AppiumInitialization.setIosAppium(properties);
							LogUtil.APP.info("完成IOSDriver初始化动作...APPIUM Server【http://{}/wd/hub】",properties.getProperty("appiumsever"));
						}
					} catch (Exception e) {
						LogUtil.APP.error("初始化AppiumDriver出错 ！APPIUM Server【http://{}/wd/hub】",properties.getProperty("appiumsever"), e);
					}
					serverOperation caselog = new serverOperation();
					List<ProjectCase> cases = GetServerApi.getCasesbyplanId(pp.getPlanId());
					LogUtil.APP.info("当前计划【{}】中共有【{}】条待测试用例...",pp.getPlanName(),cases.size());
					serverOperation.updateTaskExecuteStatusIng(taskId, caseCount);
					int i = 0;
					for (ProjectCase testcase : cases) {
						i++;
						LogUtil.APP.info("开始执行当前测试任务 {} 的第【{}】条测试用例:【{}】......",task.getTaskName(),i,testcase.getCaseSign());
						List<ProjectCaseSteps> steps = GetServerApi.getStepsbycaseid(testcase.getCaseId());
						if (steps.size() == 0) {
							continue;
						}
						try {
							//插入开始执行的用例
							caselog.insertTaskCaseExecute(taskId, taskScheduling.getProjectId(),pp.getPlanId(),testcase.getCaseId(),testcase.getCaseSign(), testcase.getCaseName(), 4);
							if ("Android".equals(properties.getProperty("platformName"))) {
								AndroidCaseExecution.caseExcution(testcase, steps, taskId, pp.getPlanId(),androiddriver, caselog, pcplist);
							} else if ("IOS".equals(properties.getProperty("platformName"))) {
								IosCaseExecution.caseExcution(testcase, steps, taskId, pp.getPlanId(),iosdriver, caselog, pcplist);
							}
						} catch (Exception e) {
							LogUtil.APP.error("用户执行过程中抛出异常！", e);
						}
						LogUtil.APP.info("当前用例：【{}】执行完成......进入下一条",testcase.getCaseSign());
					}
					LogUtil.APP.info("当前【{}】测试计划中的用例已经全部执行完成...",pp.getPlanName());
					// 关闭APP以及appium会话
					if ("Android".equals(properties.getProperty("platformName"))) {
						assert androiddriver != null;
						androiddriver.closeApp();
					} else if ("IOS".equals(properties.getProperty("platformName"))) {
						assert iosdriver != null;
						iosdriver.closeApp();
					}
				}
				tastcount = serverOperation.updateTaskExecuteData(taskId, caseCount,2);
				tastcount[0]=caseCount;
				String testtime = serverOperation.getTestTime(taskId);
				LogUtil.APP.info("当前项目【{}】测试计划中的用例已经全部执行完成...",projectname);
				MailSendInitialization.sendMailInitialization(HtmlMail.htmlSubjectFormat(jobname),
						HtmlMail.htmlContentFormat(tastcount, taskId, buildResult.toString(), restartstatus, testtime, jobname),
						taskId, taskScheduling, tastcount,testtime,buildResult.toString(),restartstatus);

			} else {
				LogUtil.APP.warn("项目构建失败，自动化测试自动退出！请前往JENKINS中检查项目构建情况。");
				MailSendInitialization.sendMailInitialization(jobname, "构建项目过程中失败，自动化测试自动退出！请前去JENKINS查看构建情况！", taskId, taskScheduling, null,"0小时0分0秒",buildResult.toString(),restartstatus);
			}
		} else {
			LogUtil.APP.warn("项目TOMCAT重启失败，自动化测试自动退出！请检查项目TOMCAT运行情况。");
			MailSendInitialization.sendMailInitialization(jobname, "项目TOMCAT重启失败，自动化测试自动退出！请检查项目TOMCAT运行情况！", taskId, taskScheduling, null,"0小时0分0秒",buildResult.toString(),restartstatus);
		}
		//关闭Appium服务的线程
		if(as!=null){
			as.interrupt();
		}
	}

}
