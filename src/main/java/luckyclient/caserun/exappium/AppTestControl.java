package luckyclient.caserun.exappium;

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
import luckyclient.planapi.entity.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸� ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944 seagull1985
 * =================================================================
 * 
 * @author�� seagull
 * 
 * @date 2017��12��1�� ����9:29:40
 * 
 */
public class AppTestControl {

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 *             ����̨ģʽ���ȼƻ�ִ������
	 */

	public static void manualExecutionPlan(String planname) {
		// ������־�����ݿ�
		DbLink.exetype = 1;
		String taskid = "888888";
		AndroidDriver<AndroidElement> androiddriver = null;
		IOSDriver<IOSElement> iosdriver = null;
		Properties properties = luckyclient.publicclass.AppiumConfig.getConfiguration();
		try {
			if ("Android".equals(properties.getProperty("platformName"))) {
				androiddriver = AppiumInitialization.setAndroidAppium(properties);
			} else if ("IOS".equals(properties.getProperty("platformName"))) {
				iosdriver = AppiumInitialization.setIosAppium(properties);
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
		luckyclient.publicclass.LogUtil.APP.info("��ǰ�ƻ��ж�ȡ�������� " + testCases.size() + " ��");
		int i = 0;
		for (ProjectCase testcase : testCases) {
			List<ProjectCasesteps> steps = GetServerAPI.getStepsbycaseid(testcase.getId());
			if (steps.size() == 0) {
				continue;
			}
			i++;
			luckyclient.publicclass.LogUtil.APP.info("��ʼִ�е�" + i + "����������" + testcase.getSign() + "��......");
			try {
				//���뿪ʼִ�е�����
				caselog.addCaseDetail(taskid, testcase.getSign(), "1", testcase.getName(), 4);
				if ("Android".equals(properties.getProperty("platformName"))) {
					AndroidCaseExecution.caseExcution(testcase, steps, taskid, androiddriver, caselog, pcplist);
				} else if ("IOS".equals(properties.getProperty("platformName"))) {
					IosCaseExecution.caseExcution(testcase, steps, taskid, iosdriver, caselog, pcplist);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				luckyclient.publicclass.LogUtil.APP.error("�û�ִ�й������׳��쳣��", e);
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			luckyclient.publicclass.LogUtil.APP.info("��ǰ��������" + testcase.getSign() + "��ִ�����......������һ��");
		}
		luckyclient.publicclass.LogUtil.APP.info("��ǰ��Ŀ���Լƻ��е������Ѿ�ȫ��ִ�����...");
		// �ر�APP�Լ�appium�Ự
		if ("Android".equals(properties.getProperty("platformName"))) {
			androiddriver.closeApp();
		} else if ("IOS".equals(properties.getProperty("platformName"))) {
			iosdriver.closeApp();
		}
	}

	public static void taskExecutionPlan(String taskid, TestTaskexcute task) throws InterruptedException {
		// ��¼��־�����ݿ�
		DbLink.exetype = 0;
		TestControl.TASKID = taskid;
		AndroidDriver<AndroidElement> androiddriver = null;
		IOSDriver<IOSElement> iosdriver = null;
		Properties properties = luckyclient.publicclass.AppiumConfig.getConfiguration();
		AppiumService as=null;
		//���������Զ�����Appiume����
		if(Boolean.valueOf(properties.getProperty("autoRunAppiumService"))){
			as =new AppiumService();
			as.start();
			Thread.sleep(10000);
		}
		
		String restartstatus = RestartServerInitialization.restartServerRun(taskid);
		String buildstatus = BuildingInitialization.buildingRun(taskid);
		List<PublicCaseParams> pcplist = GetServerAPI
				.cgetParamsByProjectid(task.getTestJob().getProjectid().toString());
		String projectname = task.getTestJob().getPlanproj();
		task = GetServerAPI.cgetTaskbyid(Integer.valueOf(taskid));
        TestJobs testJob = task.getTestJob();
		String jobname = task.getTestJob().getTaskName();
        int[] tastcount = null;
		// �ж��Ƿ�Ҫ�Զ�����TOMCAT
		if (restartstatus.indexOf("Status:true") > -1) {
			// �ж��Ƿ񹹽��Ƿ�ɹ�
			if (buildstatus.indexOf("Status:true") > -1) {
				try {
					if ("Android".equals(properties.getProperty("platformName"))) {
						androiddriver = AppiumInitialization.setAndroidAppium(properties);
						luckyclient.publicclass.LogUtil.APP.info("���AndroidDriver��ʼ������...APPIUM Server��http://"
								+ properties.getProperty("appiumsever") + "/wd/hub��");
					} else if ("IOS".equals(properties.getProperty("platformName"))) {
						iosdriver = AppiumInitialization.setIosAppium(properties);
						luckyclient.publicclass.LogUtil.APP.info("���IOSDriver��ʼ������...APPIUM Server��http://"
								+ properties.getProperty("appiumsever") + "/wd/hub��");
					}
				} catch (Exception e) {
					luckyclient.publicclass.LogUtil.APP.error("��ʼ��AppiumDriver���� ��APPIUM Server��http://"
							+ properties.getProperty("appiumsever") + "/wd/hub��", e);
					e.printStackTrace();
				}
				LogOperation caselog = new LogOperation();
				List<ProjectCase> cases = GetServerAPI.getCasesbyplanid(task.getTestJob().getPlanid());
				luckyclient.publicclass.LogUtil.APP.info("��ǰ�ƻ��ж�ȡ�������� " + cases.size() + " ��");
				LogOperation.updateTastStatus(taskid, cases.size());

				for (ProjectCase testcase : cases) {
					List<ProjectCasesteps> steps = GetServerAPI.getStepsbycaseid(testcase.getId());
					if (steps.size() == 0) {
						continue;
					}
					luckyclient.publicclass.LogUtil.APP.info("��ʼִ����������" + testcase.getSign() + "��......");
					try {
						//���뿪ʼִ�е�����
						caselog.addCaseDetail(taskid, testcase.getSign(), "1", testcase.getName(), 4);
						if ("Android".equals(properties.getProperty("platformName"))) {
							AndroidCaseExecution.caseExcution(testcase, steps, taskid, androiddriver, caselog, pcplist);
						} else if ("IOS".equals(properties.getProperty("platformName"))) {
							IosCaseExecution.caseExcution(testcase, steps, taskid, iosdriver, caselog, pcplist);
						}
					} catch (InterruptedException | IOException e) {
						// TODO Auto-generated catch block
						luckyclient.publicclass.LogUtil.APP.error("�û�ִ�й������׳��쳣��", e);
						e.printStackTrace();
					}
					luckyclient.publicclass.LogUtil.APP.info("��ǰ��������" + testcase.getSign() + "��ִ�����......������һ��");
				}
				tastcount = LogOperation.updateTastdetail(taskid, cases.size());
				String testtime = LogOperation.getTestTime(taskid);
				luckyclient.publicclass.LogUtil.APP.info("��ǰ��Ŀ��" + projectname + "�����Լƻ��е������Ѿ�ȫ��ִ�����...");
				MailSendInitialization.sendMailInitialization(HtmlMail.htmlSubjectFormat(jobname),
						HtmlMail.htmlContentFormat(tastcount, taskid, buildstatus, restartstatus, testtime, jobname),
						taskid, testJob, tastcount);
				// �ر�APP�Լ�appium�Ự
				if ("Android".equals(properties.getProperty("platformName"))) {
					androiddriver.closeApp();
				} else if ("IOS".equals(properties.getProperty("platformName"))) {
					iosdriver.closeApp();
				}
			} else {
				luckyclient.publicclass.LogUtil.APP.error("��Ŀ����ʧ�ܣ��Զ��������Զ��˳�����ǰ��JENKINS�м����Ŀ���������");
				MailSendInitialization.sendMailInitialization(jobname, "������Ŀ������ʧ�ܣ��Զ��������Զ��˳�����ǰȥJENKINS�鿴���������", taskid, testJob, tastcount);
			}
		} else {
			luckyclient.publicclass.LogUtil.APP.error("��ĿTOMCAT����ʧ�ܣ��Զ��������Զ��˳���������ĿTOMCAT���������");
			MailSendInitialization.sendMailInitialization(jobname, "��ĿTOMCAT����ʧ�ܣ��Զ��������Զ��˳���������ĿTOMCAT���������", taskid, testJob, tastcount);
		}
		//�ر�Appium������߳�
		if(as!=null){
			as.interrupt();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
