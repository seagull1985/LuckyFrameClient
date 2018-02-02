package luckyclient.caserun.exappium;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import luckyclient.caserun.exappium.androidex.AndroidCaseExecution;
import luckyclient.caserun.exappium.iosex.IosCaseExecution;
import luckyclient.caserun.exinterface.TestControl;
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
	 * @param args
	 * @throws ClassNotFoundException
	 *             控制台模式调度计划执行用例
	 */

	public static void manualExecutionPlan(String planname) {
		// 不记日志到数据库
		DbLink.exetype = 1;
		String taskid = "888888";
		AndroidDriver<AndroidElement> androiddriver = null;
		IOSDriver<IOSElement> iosdriver = null;
		Properties properties = luckyclient.publicclass.AppiumConfig.getConfiguration();
		try {
			if ("Android".equals(properties.getProperty("platformName"))) {
				androiddriver = AppiumInitialization.setAndroidAppium();
			} else if ("IOS".equals(properties.getProperty("platformName"))) {
				iosdriver = AppiumInitialization.setIosAppium();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LogOperation caselog = new LogOperation();
		List<ProjectCase> testCases = GetServerAPI.getCasesbyplanname(planname);
		List<PublicCaseParams> pcplist = new ArrayList<PublicCaseParams>();
		if (testCases.size() != 0) {
			pcplist = GetServerAPI.cgetParamsByProjectid(String.valueOf(testCases.get(0).getProjectid()));
		}
		luckyclient.publicclass.LogUtil.APP.info("当前计划中读取到用例共 " + testCases.size() + " 个");
		int i = 0;
		for (ProjectCase testcase : testCases) {
			List<ProjectCasesteps> steps = GetServerAPI.getStepsbycaseid(testcase.getId());
			if (steps.size() == 0) {
				continue;
			}
			i++;
			luckyclient.publicclass.LogUtil.APP.info("开始执行第" + i + "条用例：【" + testcase.getSign() + "】......");
			try {
				if ("Android".equals(properties.getProperty("platformName"))) {
					AndroidCaseExecution.caseExcution(testcase, steps, taskid, androiddriver, caselog, pcplist);
				} else if ("IOS".equals(properties.getProperty("platformName"))) {
					IosCaseExecution.caseExcution(testcase, steps, taskid, iosdriver, caselog, pcplist);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				luckyclient.publicclass.LogUtil.APP.error("用户执行过程中抛出异常！", e);
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			luckyclient.publicclass.LogUtil.APP.info("当前用例：【" + testcase.getSign() + "】执行完成......进入下一条");
		}
		luckyclient.publicclass.LogUtil.APP.info("当前项目测试计划中的用例已经全部执行完成...");
		// 关闭APP以及appium会话
		if ("Android".equals(properties.getProperty("platformName"))) {
			androiddriver.closeApp();
		} else if ("IOS".equals(properties.getProperty("platformName"))) {
			iosdriver.closeApp();
		}
	}

	public static void taskExecutionPlan(String taskid, TestTaskexcute task) throws InterruptedException {
		// 记录日志到数据库
		DbLink.exetype = 0;
		TestControl.TASKID = taskid;
		AndroidDriver<AndroidElement> androiddriver = null;
		IOSDriver<IOSElement> iosdriver = null;
		Properties properties = luckyclient.publicclass.AppiumConfig.getConfiguration();
		String restartstatus = RestartServerInitialization.restartServerRun(taskid);
		String buildstatus = BuildingInitialization.buildingRun(taskid);
		List<PublicCaseParams> pcplist = GetServerAPI
				.cgetParamsByProjectid(task.getTestJob().getProjectid().toString());
		String projectname = task.getTestJob().getPlanproj();
		task = GetServerAPI.cgetTaskbyid(Integer.valueOf(taskid));
		String jobname = task.getTestJob().getTaskName();
		// 判断是否要自动重启TOMCAT
		if (restartstatus.indexOf("Status:true") > -1) {
			// 判断是否构建是否成功
			if (buildstatus.indexOf("Status:true") > -1) {
				try {
					if ("Android".equals(properties.getProperty("platformName"))) {
						androiddriver = AppiumInitialization.setAndroidAppium();
						luckyclient.publicclass.LogUtil.APP.info("完成AndroidDriver初始化动作...APPIUM Server【http://"
								+ properties.getProperty("appiumsever") + "/wd/hub】");
					} else if ("IOS".equals(properties.getProperty("platformName"))) {
						iosdriver = AppiumInitialization.setIosAppium();
						luckyclient.publicclass.LogUtil.APP.info("完成IOSDriver初始化动作...APPIUM Server【http://"
								+ properties.getProperty("appiumsever") + "/wd/hub】");
					}
				} catch (Exception e) {
					luckyclient.publicclass.LogUtil.APP.error("初始化AppiumDriver出错 ！APPIUM Server【http://"
							+ properties.getProperty("appiumsever") + "/wd/hub】", e);
					e.printStackTrace();
				}
				LogOperation caselog = new LogOperation();
				int[] tastcount = null;
				List<ProjectCase> cases = GetServerAPI.getCasesbyplanid(task.getTestJob().getPlanid());
				luckyclient.publicclass.LogUtil.APP.info("当前计划中读取到用例共 " + cases.size() + " 个");
				LogOperation.updateTastStatus(taskid, cases.size());

				for (ProjectCase testcase : cases) {
					List<ProjectCasesteps> steps = GetServerAPI.getStepsbycaseid(testcase.getId());
					if (steps.size() == 0) {
						continue;
					}
					luckyclient.publicclass.LogUtil.APP.info("开始执行用例：【" + testcase.getSign() + "】......");
					try {
						if ("Android".equals(properties.getProperty("platformName"))) {
							AndroidCaseExecution.caseExcution(testcase, steps, taskid, androiddriver, caselog, pcplist);
						} else if ("IOS".equals(properties.getProperty("platformName"))) {
							IosCaseExecution.caseExcution(testcase, steps, taskid, iosdriver, caselog, pcplist);
						}
					} catch (InterruptedException | IOException e) {
						// TODO Auto-generated catch block
						luckyclient.publicclass.LogUtil.APP.error("用户执行过程中抛出异常！", e);
						e.printStackTrace();
					}
					luckyclient.publicclass.LogUtil.APP.info("当前用例：【" + testcase.getSign() + "】执行完成......进入下一条");
				}
				tastcount = LogOperation.updateTastdetail(taskid, cases.size());
				String testtime = LogOperation.getTestTime(taskid);
				luckyclient.publicclass.LogUtil.APP.info("当前项目【" + projectname + "】测试计划中的用例已经全部执行完成...");
				MailSendInitialization.sendMailInitialization(HtmlMail.htmlSubjectFormat(jobname),
						HtmlMail.htmlContentFormat(tastcount, taskid, buildstatus, restartstatus, testtime, jobname),
						taskid);
				// 关闭APP以及appium会话
				if ("Android".equals(properties.getProperty("platformName"))) {
					androiddriver.closeApp();
				} else if ("IOS".equals(properties.getProperty("platformName"))) {
					iosdriver.closeApp();
				}
			} else {
				luckyclient.publicclass.LogUtil.APP.error("项目构建失败，自动化测试自动退出！请前往JENKINS中检查项目构建情况。");
				MailSendInitialization.sendMailInitialization(jobname, "构建项目过程中失败，自动化测试自动退出！请前去JENKINS查看构建情况！", taskid);
			}
		} else {
			luckyclient.publicclass.LogUtil.APP.error("项目TOMCAT重启失败，自动化测试自动退出！请检查项目TOMCAT运行情况。");
			MailSendInitialization.sendMailInitialization(jobname, "项目TOMCAT重启失败，自动化测试自动退出！请检查项目TOMCAT运行情况！", taskid);
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
