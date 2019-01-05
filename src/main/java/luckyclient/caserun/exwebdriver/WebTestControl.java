package luckyclient.caserun.exwebdriver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import luckyclient.planapi.entity.*;
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
import luckyclient.testlinkapi.TestBuildApi;
import luckyclient.testlinkapi.TestCaseApi;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸�
 * ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944  seagull1985
 * =================================================================
 * 
 * @author�� seagull
 * @date 2017��12��1�� ����9:29:40
 * 
 */
public class WebTestControl{
	
	/**
	 * @param args
	 * @throws ClassNotFoundException
	 * ����̨ģʽ���ȼƻ�ִ������ 
	 */
	
	public  static void manualExecutionPlan(String planname){
		//������־�����ݿ�
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
		luckyclient.publicclass.LogUtil.APP.info("��ǰ�ƻ��ж�ȡ�������� "+testCases.size()+" ��");
		int i=0;
		for(ProjectCase testcase:testCases){
			List<ProjectCasesteps> steps=GetServerAPI.getStepsbycaseid(testcase.getId());
			if(steps.size()==0){
				continue;
			}
			i++;
			luckyclient.publicclass.LogUtil.APP.info("��ʼִ�е�"+i+"����������"+testcase.getSign()+"��......");
			try {
		        //���뿪ʼִ�е�����
		        caselog.addCaseDetail(taskid, testcase.getSign(), "1", testcase.getName(), 4);
				WebCaseExecution.caseExcution(testcase,steps,taskid,wd,caselog,pcplist);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				luckyclient.publicclass.LogUtil.APP.error("�û�ִ�й������׳��쳣��", e);
				e.printStackTrace();
			}
			luckyclient.publicclass.LogUtil.APP.info("��ǰ��������"+testcase.getSign()+"��ִ�����......������һ��");
		}
		luckyclient.publicclass.LogUtil.APP.info("��ǰ��Ŀ���Լƻ��е������Ѿ�ȫ��ִ�����...");
        //�ر������
        wd.quit();
	}
	
	public static void taskExecutionPlan(String taskid,TestTaskexcute task) throws InterruptedException {
		// ��¼��־�����ݿ�
		DbLink.exetype = 0; 
		TestControl.TASKID = taskid;
		String restartstatus = RestartServerInitialization.restartServerRun(taskid);
		String buildstatus = BuildingInitialization.buildingRun(taskid);
		List<PublicCaseParams> pcplist=GetServerAPI.cgetParamsByProjectid(task.getTestJob().getProjectid().toString());
		String projectname=task.getTestJob().getPlanproj();
		task=GetServerAPI.cgetTaskbyid(Integer.valueOf(taskid));
		String jobname = task.getTestJob().getTaskName();
		int drivertype = LogOperation.querydrivertype(taskid);
        TestJobs testJob = task.getTestJob();
        int[] tastcount=null;
        // �ж��Ƿ�Ҫ�Զ�����TOMCAT
		if (restartstatus.indexOf("Status:true") > -1) {
			// �ж��Ƿ񹹽��Ƿ�ɹ�
			if (buildstatus.indexOf("Status:true") > -1) {
				WebDriver wd = null;
				try {
				    wd = WebDriverInitialization.setWebDriverForTask(drivertype);
				} catch (WebDriverException e1) {
					luckyclient.publicclass.LogUtil.APP.error("��ʼ��WebDriver���� WebDriverException��", e1);
					e1.printStackTrace();
				} catch (IOException e2) {
					luckyclient.publicclass.LogUtil.APP.error("��ʼ��WebDriver���� IOException��", e2);
					e2.printStackTrace();
				}
				LogOperation caselog = new LogOperation(); 

				if(task.getTestJob().getProjecttype()==1){
					TestBuildApi.getBuild(projectname);
					TestCase[] testCases = TestCaseApi.getplantestcase(projectname, taskid,"");
					luckyclient.publicclass.LogUtil.APP.info("��ǰ�ƻ��ж�ȡ�������� " + testCases.length + " ��");
					LogOperation.updateTastStatus(taskid,testCases.length);
					
					for (TestCase testcase : testCases) {
						if (testcase.getSteps().size() == 0) {
							continue;
						}
						luckyclient.publicclass.LogUtil.APP.info("��ʼִ����������" + testcase.getFullExternalId() + "��......");
						try {
							WebCaseExecutionTestLink.caseExcution(projectname, testcase, taskid, wd, caselog);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							luckyclient.publicclass.LogUtil.APP.error("�û�ִ�й������׳��쳣��", e);
							e.printStackTrace();
						}
						luckyclient.publicclass.LogUtil.APP.info("��ǰ��������" + testcase.getFullExternalId() + "��ִ�����......������һ��");
					}
					tastcount = LogOperation.updateTastdetail(taskid, testCases.length);
				}else if(task.getTestJob().getProjecttype()==0){
					List<ProjectCase> cases=GetServerAPI.getCasesbyplanid(task.getTestJob().getPlanid());
					luckyclient.publicclass.LogUtil.APP.info("��ǰ�ƻ��ж�ȡ�������� " + cases.size() + " ��");
					LogOperation.updateTastStatus(taskid,cases.size());
					
					for (ProjectCase testcase : cases) {
						List<ProjectCasesteps> steps=GetServerAPI.getStepsbycaseid(testcase.getId());
						if (steps.size() == 0) {
							continue;
						}
						luckyclient.publicclass.LogUtil.APP.info("��ʼִ����������" + testcase.getSign() + "��......");
						try {
					        //���뿪ʼִ�е�����
					        caselog.addCaseDetail(taskid, testcase.getSign(), "1", testcase.getName(), 4);
							WebCaseExecution.caseExcution(testcase, steps, taskid, wd, caselog,pcplist);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							luckyclient.publicclass.LogUtil.APP.error("�û�ִ�й������׳��쳣��", e);
							e.printStackTrace();
						}
						luckyclient.publicclass.LogUtil.APP.info("��ǰ��������" + testcase.getSign() + "��ִ�����......������һ��");
					}
					tastcount = LogOperation.updateTastdetail(taskid, cases.size());
				}
				String testtime = LogOperation.getTestTime(taskid);
				luckyclient.publicclass.LogUtil.APP.info("��ǰ��Ŀ��" + projectname + "�����Լƻ��е������Ѿ�ȫ��ִ�����...");
				MailSendInitialization.sendMailInitialization(HtmlMail.htmlSubjectFormat(jobname),
						HtmlMail.htmlContentFormat(tastcount, taskid, buildstatus, restartstatus, testtime,jobname), taskid, testJob, tastcount);
				// �ر������
				wd.quit();
			} else {
				luckyclient.publicclass.LogUtil.APP.error("��Ŀ����ʧ�ܣ��Զ��������Զ��˳�����ǰ��JENKINS�м����Ŀ���������");
				MailSendInitialization.sendMailInitialization(jobname,
						"������Ŀ������ʧ�ܣ��Զ��������Զ��˳�����ǰȥJENKINS�鿴���������", taskid, testJob, tastcount);
			}
		} else {
			luckyclient.publicclass.LogUtil.APP.error("��ĿTOMCAT����ʧ�ܣ��Զ��������Զ��˳���������ĿTOMCAT���������");
			MailSendInitialization.sendMailInitialization(jobname,
					"��ĿTOMCAT����ʧ�ܣ��Զ��������Զ��˳���������ĿTOMCAT���������", taskid, testJob, tastcount);
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
