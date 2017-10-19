package luckyclient.caserun.exinterface;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import luckyclient.caserun.exinterface.testlink.ThreadForTestLinkExecuteCase;
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

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 此测试框架主要采用testlink做分层框架，负责数据驱动以及用例管理部分，有任何疑问欢迎联系作者讨论。 QQ:24163551 seagull1985
 * =================================================================
 * 
 * @ClassName: TestControl
 * @Description: 启动扫描指定项目的用例脚本，并调用脚本中的方法 @author： 何彦霖
 * @date 2014年8月24日 上午9:29:40
 * 
 */
public class TestControl {
	public static String TASKID = "NULL";
	public static int Debugcount = 0;

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 *             控制台模式调度计划执行testlink用例
	 */

	public static void ManualExecutionTestLinkPlan(String projectname, String testplan) throws Exception {
		DbLink.exetype = 1;
		int threadcount = 10;
		// 创建线程池，多线程执行用例
		ThreadPoolExecutor threadExecute = new ThreadPoolExecutor(threadcount, 20, 3, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

		TestBuildApi.GetBuild(projectname);
		TestCase[] testCases = TestCaseApi.getplantestcase(projectname, "NULL", testplan);
		String taskid = "888888";
		for (TestCase testcase : testCases) {
			if (testcase.getSteps().size() == 0) {
				continue;
			}
			Debugcount++; // 多线程计数++，用于检测线程是否全部执行完
			threadExecute
					.execute(new ThreadForTestLinkExecuteCase(projectname, testcase.getFullExternalId(), testcase, taskid));
			// new ThreadForExecuteCase(projectname,caseid,testcaseob).run();
		}
		// 多线程计数，用于检测线程是否全部执行完
		int i = 0;
		while (Debugcount != 0) {
			i++;
			if (i > 600) {
				break;
			}
			Thread.sleep(6000);
		}
		luckyclient.publicclass.LogUtil.APP.info("亲，没有下一条啦！我发现你的用例已经全部执行完毕，快去看看有没有失败的用例吧！");
		threadExecute.shutdown();
	}

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 *             控制台模式调度计划执行用例
	 */

	public static void ManualExecutionPlan(String planname) throws Exception {
		DbLink.exetype = 1;
		int threadcount = 10;
		// 创建线程池，多线程执行用例
		ThreadPoolExecutor threadExecute = new ThreadPoolExecutor(threadcount, 20, 3, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

		List<ProjectCase> testCases = GetServerAPI.getCasesbyplanname(planname);
		String taskid = "888888";
		for (ProjectCase testcase : testCases) {
			List<ProjectCasesteps> steps = GetServerAPI.getStepsbycaseid(testcase.getId());
			if (steps.size() == 0) {
				continue;
			}
			Debugcount++; // 多线程计数++，用于检测线程是否全部执行完
			threadExecute
					.execute(new ThreadForExecuteCase(testcase, steps,taskid));
		}
		// 多线程计数，用于检测线程是否全部执行完
		int i = 0;
		while (Debugcount != 0) {
			i++;
			if (i > 600) {
				break;
			}
			Thread.sleep(6000);
		}
		luckyclient.publicclass.LogUtil.APP.info("亲，没有下一条啦！我发现你的用例已经全部执行完毕，快去看看有没有失败的用例吧！");
		threadExecute.shutdown();
	}
	
	/**
	 * @param args
	 * @throws ClassNotFoundException
	 *             计划任务模式调度计划执行用例
	 */

	protected static void TastExecutionPlan(String taskid,TestTaskexcute task) throws Exception {
		DbLink.exetype = 0;
		TestControl.TASKID = taskid;
		String restartstatus = RestartServerInitialization.RestartServerRun(taskid);
		String buildstatus = BuildingInitialization.BuildingRun(taskid);
		String jobname = task.getTestJob().getTaskName();
		String projectname=task.getTestJob().getPlanproj();
		int timeout = task.getTestJob().getTimeout();
		// 判断是否要自动重启TOMCAT
		if (restartstatus.indexOf("Status:true") > -1) {
			// 判断是否构建是否成功
			if (buildstatus.indexOf("Status:true") > -1) {
				int threadcount = task.getTestJob().getThreadCount();
				// 创建线程池，多线程执行用例
				ThreadPoolExecutor threadExecute = new ThreadPoolExecutor(threadcount, 20, 3, TimeUnit.SECONDS,
						new ArrayBlockingQueue<Runnable>(1000), new ThreadPoolExecutor.CallerRunsPolicy());
				
				int[] tastcount=null;
				if(task.getTestJob().getProjecttype()==1){
					TestBuildApi.GetBuild(projectname);
					TestCase[] testCases= TestCaseApi.getplantestcase(projectname, taskid, "");
					LogOperation.UpdateTastStatus(taskid, testCases.length);
					for (TestCase testcase : testCases) {
						if (testcase.getSteps().size() == 0) {
							continue;
						}
						Debugcount++; // 多线程计数++，用于检测线程是否全部执行完
						threadExecute.execute(
								new ThreadForTestLinkExecuteCase(projectname, testcase.getFullExternalId(), testcase, taskid));
					}
					// 多线程计数，用于检测线程是否全部执行完
					int i = 0;
					while (Debugcount != 0) {
						i++;
						if (i > timeout * 10) {
							break;
						}
						Thread.sleep(6000);
					}
					tastcount = LogOperation.UpdateTastdetail(taskid, testCases.length);
					
				}else{
					 List<ProjectCase> cases=GetServerAPI.getCasesbyplanid(task.getTestJob().getPlanid());
					 LogOperation.UpdateTastStatus(taskid, cases.size());
						for (int j=0;j<cases.size();j++) {
							ProjectCase projectcase =cases.get(j);
							List<ProjectCasesteps> steps=GetServerAPI.getStepsbycaseid(projectcase.getId());
							if (steps.size()== 0) {
								continue;
							}
							Debugcount++; // 多线程计数++，用于检测线程是否全部执行完
							threadExecute.execute(
									new ThreadForExecuteCase(projectcase, steps,taskid));
						}
						// 多线程计数，用于检测线程是否全部执行完
						int i = 0;
						while (Debugcount != 0) {
							i++;
							if (i > timeout * 10) {
								break;
							}
							Thread.sleep(6000);
						}
						tastcount = LogOperation.UpdateTastdetail(taskid, cases.size());
						
				}

				String testtime = LogOperation.GetTestTime(taskid);
				MailSendInitialization.SendMailInitialization(HtmlMail.HtmlSubjectFormat(jobname),
						HtmlMail.HtmlContentFormat(tastcount, taskid, buildstatus, restartstatus, testtime,jobname), taskid);
				threadExecute.shutdown();
				luckyclient.publicclass.LogUtil.APP.info("亲，没有下一条啦！我发现你的用例已经全部执行完毕，快去看看有没有失败的用例吧！");
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
	
	public static void main(String[] args) throws Exception {
		ManualExecutionPlan("test");
	}

}
