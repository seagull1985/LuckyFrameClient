package luckyclient.caserun.exwebdriver.ex;

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.WebDriver;

import luckyclient.caserun.exinterface.TestControl;
import luckyclient.caserun.exwebdriver.WebDriverInitialization;
import luckyclient.dblog.DbLink;
import luckyclient.dblog.LogOperation;
import luckyclient.planapi.api.GetServerAPI;
import luckyclient.planapi.entity.ProjectCase;
import luckyclient.planapi.entity.ProjectCasesteps;
import luckyclient.planapi.entity.PublicCaseParams;

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
public class WebOneCaseExecute{
	
	public static void oneCaseExecuteForTast(String projectname,String testCaseExternalId,int version,String taskid){
		//��¼��־�����ݿ�
		DbLink.exetype = 0;   
		TestControl.TASKID = taskid;
		int drivertype = LogOperation.querydrivertype(taskid);
		WebDriver wd = null;
		try {
			wd = WebDriverInitialization.setWebDriverForTask(drivertype);
		} catch (IOException e1) {
			luckyclient.publicclass.LogUtil.APP.error("��ʼ��WebDriver����", e1);
			e1.printStackTrace();
		}
		LogOperation caselog = new LogOperation();
		//ɾ���ɵ���־
		LogOperation.deleteCaseLogDetail(testCaseExternalId, taskid);    
		ProjectCase testcase = GetServerAPI.cgetCaseBysign(testCaseExternalId);
		List<PublicCaseParams> pcplist=GetServerAPI.cgetParamsByProjectid(String.valueOf(testcase.getProjectid()));
		luckyclient.publicclass.LogUtil.APP.info("��ʼִ����������"+testCaseExternalId+"��......");
		try {
			List<ProjectCasesteps> steps=GetServerAPI.getStepsbycaseid(testcase.getId());
			WebCaseExecution.caseExcution(testcase, steps, taskid,wd,caselog,pcplist);
			luckyclient.publicclass.LogUtil.APP.info("��ǰ��������"+testcase.getSign()+"��ִ�����......������һ��");
		} catch (InterruptedException e) {
			luckyclient.publicclass.LogUtil.APP.error("�û�ִ�й������׳��쳣��", e);
			e.printStackTrace();
		}
		LogOperation.updateTastdetail(taskid, 0);
        //�ر������
        wd.quit();
	}

}
