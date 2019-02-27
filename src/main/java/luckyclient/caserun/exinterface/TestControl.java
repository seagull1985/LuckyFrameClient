package luckyclient.caserun.exinterface;

import java.util.ArrayList;
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
import luckyclient.planapi.entity.PublicCaseParams;
import luckyclient.planapi.entity.TestJobs;
import luckyclient.planapi.entity.TestTaskexcute;
import luckyclient.testlinkapi.TestBuildApi;
import luckyclient.testlinkapi.TestCaseApi;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸�
 * ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944  seagull1985
 * =================================================================
 * 
 * @ClassName: TestControl
 * @Description: ����ɨ��ָ����Ŀ�������ű��������ýű��еķ��� 
 * @author�� seagull
 * @date 2014��8��24�� ����9:29:40
 * 
 */
public class TestControl {
	public static String TASKID = "NULL";
	public static int THREAD_COUNT = 0;

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 *             ����̨ģʽ���ȼƻ�ִ��testlink����
	 */

	public static void manualExecutionTestLinkPlan(String projectname, String testplan) throws Exception {
		DbLink.exetype = 1;
		int threadcount = 10;
		// �����̳߳أ����߳�ִ������
		ThreadPoolExecutor threadExecute = new ThreadPoolExecutor(threadcount, 20, 3, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

		TestBuildApi.getBuild(projectname);
		TestCase[] testCases = TestCaseApi.getplantestcase(projectname, "NULL", testplan);
		String taskid = "888888";

		for (TestCase testcase : testCases) {
			if (testcase.getSteps().size() == 0) {
				continue;
			}
			THREAD_COUNT++; // ���̼߳���++�����ڼ���߳��Ƿ�ȫ��ִ����
			threadExecute
					.execute(new ThreadForTestLinkExecuteCase(projectname, testcase.getFullExternalId(), testcase, taskid));
			// new ThreadForExecuteCase(projectname,caseid,testcaseob).run();
		}
		// ���̼߳��������ڼ���߳��Ƿ�ȫ��ִ����
		int i = 0;
		while (THREAD_COUNT != 0) {
			i++;
			if (i > 600) {
				break;
			}
			Thread.sleep(6000);
		}
		luckyclient.publicclass.LogUtil.APP.info("�ף�û����һ�������ҷ�����������Ѿ�ȫ��ִ����ϣ���ȥ������û��ʧ�ܵ������ɣ�");
		threadExecute.shutdown();
	}

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 *             ����̨ģʽ���ȼƻ�ִ������
	 */

	public static void manualExecutionPlan(String planname) throws Exception {
		DbLink.exetype = 1;
		int threadcount = 10;
		// �����̳߳أ����߳�ִ������
		ThreadPoolExecutor threadExecute = new ThreadPoolExecutor(threadcount, 20, 3, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

		List<ProjectCase> testCases = GetServerAPI.getCasesbyplanname(planname);
		List<PublicCaseParams> pcplist = new ArrayList<PublicCaseParams>();
		if(testCases.size()!=0){
			pcplist=GetServerAPI.cgetParamsByProjectid(String.valueOf(testCases.get(0).getProjectid()));
		}
		
		String taskid = "888888";
		// ��ʼ��д��������Լ���־ģ��
		LogOperation caselog = new LogOperation(); 
		for (ProjectCase testcase : testCases) {
			List<ProjectCasesteps> steps = GetServerAPI.getStepsbycaseid(testcase.getId());
			if (steps.size() == 0) {
				caselog.addCaseDetail(taskid, testcase.getSign(), "1", testcase.getName(), 2);
				luckyclient.publicclass.LogUtil.APP.error("������" + testcase.getSign() + "��û���ҵ����裬ֱ�����������飡");
				caselog.caseLogDetail(taskid, testcase.getSign(),"��������û���ҵ����裬����","error", "1", "");
				continue;
			}
			THREAD_COUNT++; // ���̼߳���++�����ڼ���߳��Ƿ�ȫ��ִ����
			threadExecute
					.execute(new ThreadForExecuteCase(testcase, steps,taskid,pcplist,caselog));
		}
		// ���̼߳��������ڼ���߳��Ƿ�ȫ��ִ����
		int i = 0;
		while (THREAD_COUNT != 0) {
			i++;
			if (i > 600) {
				break;
			}
			Thread.sleep(6000);
		}
		luckyclient.publicclass.LogUtil.APP.info("�ף�û����һ�������ҷ�����������Ѿ�ȫ��ִ����ϣ���ȥ������û��ʧ�ܵ������ɣ�");
		threadExecute.shutdown();
	}
	
	/**
	 * @param args
	 * @throws ClassNotFoundException
	 *             �ƻ�����ģʽ���ȼƻ�ִ������
	 */

	public static void taskExecutionPlan(String taskid,TestTaskexcute task) throws Exception {
		DbLink.exetype = 0;
		TestControl.TASKID = taskid;
		String restartstatus = RestartServerInitialization.restartServerRun(taskid);
		String buildstatus = BuildingInitialization.buildingRun(taskid);
		String jobname = task.getTestJob().getTaskName();
		String projectname=task.getTestJob().getPlanproj();
		int timeout = task.getTestJob().getTimeout();
        TestJobs testJob = task.getTestJob();
        int[] tastcount=null;
		List<PublicCaseParams> pcplist=GetServerAPI.cgetParamsByProjectid(task.getTestJob().getProjectid().toString());
		// ��ʼ��д��������Լ���־ģ��
		LogOperation caselog = new LogOperation(); 
		// �ж��Ƿ�Ҫ�Զ�����TOMCAT
		if (restartstatus.indexOf("Status:true") > -1) {
			// �ж��Ƿ񹹽��Ƿ�ɹ�
			if (buildstatus.indexOf("Status:true") > -1) {
				int threadcount = task.getTestJob().getThreadCount();
				// �����̳߳أ����߳�ִ������
				ThreadPoolExecutor threadExecute = new ThreadPoolExecutor(threadcount, 20, 3, TimeUnit.SECONDS,
						new ArrayBlockingQueue<Runnable>(1000), new ThreadPoolExecutor.CallerRunsPolicy());
				
				if(task.getTestJob().getProjecttype()==1){
					TestBuildApi.getBuild(projectname);
					TestCase[] testCases= TestCaseApi.getplantestcase(projectname, taskid, "");
					LogOperation.updateTastStatus(taskid, testCases.length);
					for (TestCase testcase : testCases) {
						if (testcase.getSteps().size() == 0) {
							continue;
						}
						THREAD_COUNT++; // ���̼߳���++�����ڼ���߳��Ƿ�ȫ��ִ����
						threadExecute.execute(
								new ThreadForTestLinkExecuteCase(projectname, testcase.getFullExternalId(), testcase, taskid));
					}
					// ���̼߳��������ڼ���߳��Ƿ�ȫ��ִ����
					int i = 0;
					while (THREAD_COUNT != 0) {
						i++;
						if (i > timeout * 10) {
							break;
						}
						Thread.sleep(6000);
					}
					tastcount = LogOperation.updateTastdetail(taskid, testCases.length);
					
				}else{
					 List<ProjectCase> cases=GetServerAPI.getCasesbyplanid(task.getTestJob().getPlanid());
					 LogOperation.updateTastStatus(taskid, cases.size());
					 int casepriority=0;
						for (int j=0;j<cases.size();j++) {
							ProjectCase projectcase =cases.get(j);
							List<ProjectCasesteps> steps=GetServerAPI.getStepsbycaseid(projectcase.getId());
							if (steps.size()== 0) {
								caselog.addCaseDetail(taskid, projectcase.getSign(), "1", projectcase.getName(), 2);
								luckyclient.publicclass.LogUtil.APP.error("������" + projectcase.getSign() + "��û���ҵ����裬ֱ�����������飡");
								caselog.caseLogDetail(taskid, projectcase.getSign(),"��������û���ҵ����裬����","error", "1", "");
								continue;
							}
							// ���̼߳���,����������������ȼ�����������ȼ��ߵ�����ִ����ɣ��ż������������
							if(casepriority<projectcase.getPriority()){
								luckyclient.publicclass.LogUtil.APP.info("������ţ�"+projectcase.getSign()+"  casepriority��"+casepriority+"   projectcase.getPriority()��"+projectcase.getPriority());
								luckyclient.publicclass.LogUtil.APP.info("THREAD_COUNT��"+THREAD_COUNT);
								int i = 0;
								while (THREAD_COUNT != 0) {
									i++;
									if (i > timeout*60*5/cases.size()) {
										break;
									}
									Thread.sleep(1000);
								}
							}
							casepriority=projectcase.getPriority();
							THREAD_COUNT++; // ���̼߳���++�����ڼ���߳��Ƿ�ȫ��ִ����
							threadExecute.execute(
									new ThreadForExecuteCase(projectcase, steps,taskid,pcplist,caselog));
						}
						// ���̼߳��������ڼ���߳��Ƿ�ȫ��ִ����
						int i = 0;
						while (THREAD_COUNT != 0) {
							i++;
							if (i > timeout * 10) {
								break;
							}
							Thread.sleep(6000);
						}
						tastcount = LogOperation.updateTastdetail(taskid, cases.size());
						
				}

				String testtime = LogOperation.getTestTime(taskid);
				MailSendInitialization.sendMailInitialization(HtmlMail.htmlSubjectFormat(jobname),
						HtmlMail.htmlContentFormat(tastcount, taskid, buildstatus, restartstatus, testtime,jobname), taskid, testJob,
                        tastcount);
				threadExecute.shutdown();
				luckyclient.publicclass.LogUtil.APP.info("�ף�û����һ�������ҷ�����������Ѿ�ȫ��ִ����ϣ���ȥ������û��ʧ�ܵ������ɣ�");
			} else {
				luckyclient.publicclass.LogUtil.APP.error("��Ŀ����ʧ�ܣ��Զ��������Զ��˳�����ǰ��JENKINS�м����Ŀ���������");
				MailSendInitialization.sendMailInitialization(jobname,
						"������Ŀ������ʧ�ܣ��Զ��������Զ��˳�����ǰȥJENKINS�鿴���������", taskid, testJob,
                        tastcount);
			}
		} else {
			luckyclient.publicclass.LogUtil.APP.error("��ĿTOMCAT����ʧ�ܣ��Զ��������Զ��˳���������ĿTOMCAT���������");
			MailSendInitialization.sendMailInitialization(jobname,
					"��ĿTOMCAT����ʧ�ܣ��Զ��������Զ��˳���������ĿTOMCAT���������", taskid, testJob,
                    tastcount);
		}
	}
	
	public static void main(String[] args) throws Exception {
		int timeout=10;
		int a=timeout*60*5/13;
		System.out.println(a);
	}

}
