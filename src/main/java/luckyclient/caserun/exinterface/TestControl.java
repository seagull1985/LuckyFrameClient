package luckyclient.caserun.exinterface;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
 * @ClassName: TestControl
 * @Description: 启动扫描指定项目的用例脚本，并调用脚本中的方法 @author： seagull
 * @date 2014年8月24日 上午9:29:40
 * 
 */
public class TestControl {
	public static String TASKID = "NULL";
	public static int THREAD_COUNT = 0;

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 *             控制台模式调度计划执行用例
	 */

	public static void manualExecutionPlan(String planname) throws Exception {
		DbLink.exetype = 1;
		int threadcount = 10;
		// 创建线程池，多线程执行用例
		ThreadPoolExecutor threadExecute = new ThreadPoolExecutor(threadcount, 20, 3, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

		List<ProjectCase> testCases = GetServerAPI.getCasesbyplanname(planname);
		List<ProjectCaseParams> pcplist = new ArrayList<ProjectCaseParams>();
		if (testCases.size() != 0) {
			pcplist = GetServerAPI.cgetParamsByProjectid(String.valueOf(testCases.get(0).getProjectId()));
		}

		String taskid = "888888";
		// 初始化写用例结果以及日志模块
		LogOperation caselog = new LogOperation();
		for (ProjectCase testcase : testCases) {
			List<ProjectCaseSteps> steps = GetServerAPI.getStepsbycaseid(testcase.getCaseId());
			if (steps.size() == 0) {
				LogUtil.APP.warn("用例【" + testcase.getCaseSign() + "】没有找到步骤，直接跳过，请检查！");
				caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "在用例中没有找到步骤，请检查", "error", "1", "");
				continue;
			}
			THREAD_COUNT++; // 多线程计数++，用于检测线程是否全部执行完
			threadExecute.execute(new ThreadForExecuteCase(testcase, steps, taskid, pcplist, caselog));
		}
		// 多线程计数，用于检测线程是否全部执行完
		int i = 0;
		while (THREAD_COUNT != 0) {
			i++;
			if (i > 600) {
				break;
			}
			Thread.sleep(6000);
		}
		LogUtil.APP.info("亲，没有下一条啦！我发现你的用例已经全部执行完毕，快去看看有没有失败的用例吧！");
		threadExecute.shutdown();
	}

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 *             计划任务模式调度计划执行用例
	 */

	public static void taskExecutionPlan(TaskExecute task) throws Exception {
		DbLink.exetype = 0;
		String taskid = task.getTaskId().toString();
		TestControl.TASKID = taskid;
		String restartstatus = RestartServerInitialization.restartServerRun(taskid);
		String buildstatus = BuildingInitialization.buildingRun(taskid);
		TaskScheduling taskScheduling = GetServerAPI.cGetTaskSchedulingByTaskId(task.getTaskId());
		String jobname = taskScheduling.getSchedulingName();
		int timeout = taskScheduling.getTaskTimeout();
		int[] tastcount = null;
		List<ProjectCaseParams> pcplist = GetServerAPI.cgetParamsByProjectid(taskScheduling.getProjectId().toString());
		// 初始化写用例结果以及日志模块
		LogOperation caselog = new LogOperation();
		// 判断是否要自动重启TOMCAT
		if (restartstatus.indexOf("Status:true") > -1) {
			// 判断是否构建是否成功
			if (buildstatus.indexOf("Status:true") > -1) {
				int threadcount = taskScheduling.getExThreadCount();
				// 创建线程池，多线程执行用例
				ThreadPoolExecutor threadExecute = new ThreadPoolExecutor(threadcount, 20, 3, TimeUnit.SECONDS,
						new ArrayBlockingQueue<Runnable>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

				List<ProjectCase> cases = GetServerAPI.getCasesbyplanId(taskScheduling.getPlanId());
				LogOperation.updateTaskExecuteStatus(taskid, cases.size());
				int casepriority = 0;
				for (int j = 0; j < cases.size(); j++) {
					ProjectCase projectcase = cases.get(j);
					List<ProjectCaseSteps> steps = GetServerAPI.getStepsbycaseid(projectcase.getCaseId());
					if (steps.size() == 0) {
						caselog.insertTaskCaseExecute(taskid, taskScheduling.getProjectId(),projectcase.getCaseId(),projectcase.getCaseSign(), projectcase.getCaseName(), 2);
						LogUtil.APP.warn("用例【" + projectcase.getCaseSign() + "】没有找到步骤，直接跳过，请检查！");
						caselog.insertTaskCaseLog(taskid, projectcase.getCaseId(), "在用例中没有找到步骤，请检查", "error", "1", "");
						continue;
					}
					// 多线程计数,如果用例设置了优先级，必须等优先级高的用例执行完成，才继续后面的用例
					if (casepriority < projectcase.getPriority()) {
						LogUtil.APP.info("用例编号：" + projectcase.getCaseSign() + "  casepriority："
								+ casepriority + "   projectcase.getPriority()：" + projectcase.getPriority());
						LogUtil.APP.info("THREAD_COUNT：" + THREAD_COUNT);
						int i = 0;
						while (THREAD_COUNT != 0) {
							i++;
							if (i > timeout * 60 * 5 / cases.size()) {
								break;
							}
							Thread.sleep(1000);
						}
					}
					casepriority = projectcase.getPriority();
					THREAD_COUNT++; // 多线程计数++，用于检测线程是否全部执行完
					threadExecute.execute(new ThreadForExecuteCase(projectcase, steps, taskid, pcplist, caselog));
				}
				// 多线程计数，用于检测线程是否全部执行完
				int i = 0;
				while (THREAD_COUNT != 0) {
					i++;
					if (i > timeout * 10) {
						break;
					}
					Thread.sleep(6000);
				}
				tastcount = LogOperation.updateTaskExecuteData(taskid, cases.size());

				String testtime = LogOperation.getTestTime(taskid);
				MailSendInitialization.sendMailInitialization(HtmlMail.htmlSubjectFormat(jobname),
						HtmlMail.htmlContentFormat(tastcount, taskid, buildstatus, restartstatus, testtime, jobname),
						taskid, taskScheduling, tastcount);
				threadExecute.shutdown();
				LogUtil.APP.info("亲，没有下一条啦！我发现你的用例已经全部执行完毕，快去看看有没有失败的用例吧！");
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
