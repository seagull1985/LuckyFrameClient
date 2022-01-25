package luckyclient.execution.httpinterface;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.offbytwo.jenkins.model.BuildResult;

import luckyclient.remote.api.GetServerApi;
import luckyclient.remote.api.serverOperation;
import luckyclient.remote.entity.*;
import luckyclient.tool.jenkins.BuildingInitialization;
import luckyclient.tool.mail.HtmlMail;
import luckyclient.tool.mail.MailSendInitialization;
import luckyclient.tool.shell.RestartServerInitialization;
import luckyclient.utils.LogUtil;

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
	 * 控制台模式调度计划执行用例
	 * @param planname 计划名称
	 */
	public static void manualExecutionPlan(String planname) throws Exception {
		serverOperation.exetype = 1;
		int threadcount = 10;
		// 创建线程池，多线程执行用例
		ThreadPoolExecutor threadExecute = new ThreadPoolExecutor(threadcount, 20, 3, TimeUnit.SECONDS,
				new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

		List<ProjectCase> testCases = GetServerApi.getCasesbyplanname(planname);
		List<ProjectCaseParams> pcplist = new ArrayList<>();
		if (testCases.size() != 0) {
			pcplist = GetServerApi.cgetParamsByProjectid(String.valueOf(testCases.get(0).getProjectId()));
		}

		String taskid = "888888";
		// 初始化写用例结果以及日志模块
		serverOperation caselog = new serverOperation();
		for (ProjectCase testcase : testCases) {
			List<ProjectCaseSteps> steps = GetServerApi.getStepsbycaseid(testcase.getCaseId());
			if (steps.size() == 0) {
				LogUtil.APP.warn("用例【{}】没有找到步骤，直接跳过，请检查！",testcase.getCaseSign());
				caselog.insertTaskCaseLog(taskid, testcase.getCaseId(), "在用例中没有找到步骤，请检查", "error", "1", "");
				continue;
			}
			THREAD_COUNT++; // 多线程计数++，用于检测线程是否全部执行完
			threadExecute.execute(new ThreadForExecuteCase(testcase, steps, taskid,null, pcplist, caselog));
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
	 * 计划任务模式调度计划执行用例
	 * @param task 任务对象
	 */
	public static void taskExecutionPlan(TaskExecute task) throws Exception {
		serverOperation.exetype = 0;
		String taskid = task.getTaskId().toString();
		TestControl.TASKID = taskid;
		String restartstatus = RestartServerInitialization.restartServerRun(taskid);
		BuildResult buildResult = BuildingInitialization.buildingRun(taskid);
		TaskScheduling taskScheduling = GetServerApi.cGetTaskSchedulingByTaskId(task.getTaskId());
		String jobname = taskScheduling.getSchedulingName();
		int timeout = taskScheduling.getTaskTimeout();
		int[] tastcount;
		List<ProjectCaseParams> pcplist = GetServerApi.cgetParamsByProjectid(taskScheduling.getProjectId().toString());
		// 初始化写用例结果以及日志模块
		serverOperation caselog = new serverOperation();
		// 判断是否要自动重启TOMCAT
		if (restartstatus.contains("Status:true")) {
			// 判断是否构建是否成功
			if (BuildResult.SUCCESS.equals(buildResult)) {
				int threadcount = taskScheduling.getExThreadCount();
				// 创建线程池，多线程执行用例
				ThreadPoolExecutor threadExecute = new ThreadPoolExecutor(threadcount, 20, 3, TimeUnit.SECONDS,
						new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

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
				int taskStatus = 2;
				for(ProjectPlan pp:plans){
					List<ProjectCase> cases = GetServerApi.getCasesbyplanId(pp.getPlanId());
					caseCount+=cases.size();
				}

				for(ProjectPlan pp:plans) {
					List<ProjectCase> cases = GetServerApi.getCasesbyplanId(pp.getPlanId());
					LogUtil.APP.info("当前测试计划 {} 中共有【{}】条待测试用例...", pp.getPlanName(), cases.size());
					serverOperation.updateTaskExecuteStatusIng(taskid, caseCount);
					int casepriority = 0;
					for (int j = 0; j < cases.size(); j++) {
						ProjectCase projectcase = cases.get(j);
						List<ProjectCaseSteps> steps = GetServerApi.getStepsbycaseid(projectcase.getCaseId());
						if (steps.size() == 0) {
							caselog.insertTaskCaseExecute(taskid, taskScheduling.getProjectId(),pp.getPlanId(), projectcase.getCaseId(), projectcase.getCaseSign(), projectcase.getCaseName(), 2);
							LogUtil.APP.warn("用例【{}】没有找到步骤，直接跳过，请检查！", projectcase.getCaseSign());
							caselog.insertTaskCaseLog(taskid, projectcase.getCaseId(), "在用例中没有找到步骤，请检查", "error", "1", "");
							continue;
						}
						// 多线程计数,如果用例设置了优先级，必须等优先级高的用例执行完成，才继续后面的用例
						if (casepriority < projectcase.getPriority()) {
							LogUtil.APP.info("用例编号:{} 上条用例优先级:{} 当前用例优先级:{}", projectcase.getCaseSign(), casepriority, projectcase.getPriority());
							int i = 0;
							while (THREAD_COUNT != 0) {
								i++;
								if (i > timeout * 60 * 5 / cases.size()) {
									LogUtil.APP.warn("用例编号:{} 上条用例优先级:{} 当前用例优先级:{} 等待时间已经超过设置的用例平均超时间{}秒(计算公式：任务超时时间*5/用例总数)，现在继续往下执行...", projectcase.getCaseSign(), casepriority, projectcase.getPriority(), i);
									break;
								}
								Thread.sleep(1000);
							}
						}
						casepriority = projectcase.getPriority();
						THREAD_COUNT++; // 多线程计数++，用于检测线程是否全部执行完
						LogUtil.APP.info("开始执行当前测试任务 {} 的第【{}】条测试用例...", task.getTaskName(), j + 1);
						threadExecute.execute(new ThreadForExecuteCase(projectcase, steps, taskid,pp.getPlanId(), pcplist, caselog));
					}
					// 多线程计数，用于检测线程是否全部执行完
					int i = 0;
					while (THREAD_COUNT != 0) {
						i++;
						if (i > timeout * 10) {
							taskStatus = 3;
							LogUtil.APP.warn("当前测试任务 {} 执行已经超过设置的最大任务超时时间【{}】分钟，现在即将停止任务执行...", task.getTaskName(), timeout);
							break;
						}
						Thread.sleep(6000);
					}
				}
				tastcount = serverOperation.updateTaskExecuteData(taskid, caseCount,taskStatus);

				String testtime = serverOperation.getTestTime(taskid);
				MailSendInitialization.sendMailInitialization(HtmlMail.htmlSubjectFormat(jobname),
						HtmlMail.htmlContentFormat(tastcount, taskid, buildResult.toString(), restartstatus, testtime, jobname),
						taskid, taskScheduling, tastcount,testtime,buildResult.toString(),restartstatus);
				threadExecute.shutdown();
				LogUtil.APP.info("亲，没有下一条啦！我发现你的用例已经全部执行完毕，快去看看有没有失败的用例吧！");
			} else {
				LogUtil.APP.warn("项目构建失败，自动化测试自动退出！请查看构建日志检查项目构建情况...");
				MailSendInitialization.sendMailInitialization(jobname, "构建项目过程中失败，自动化测试自动退出！请查看构建日志检查项目构建情况...", taskid,
						taskScheduling, null,"0小时0分0秒",buildResult.toString(),restartstatus);
			}
		} else {
			LogUtil.APP.warn("项目TOMCAT重启失败，自动化测试自动退出！请检查项目TOMCAT运行情况。");
			MailSendInitialization.sendMailInitialization(jobname, "项目TOMCAT重启失败，自动化测试自动退出！请检查项目TOMCAT运行情况！", taskid,
					taskScheduling, null,"0小时0分0秒",buildResult.toString(),restartstatus);
		}
	}

}
