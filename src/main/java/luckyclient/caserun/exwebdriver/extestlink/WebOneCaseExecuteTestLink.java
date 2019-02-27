package luckyclient.caserun.exwebdriver.extestlink;

import java.io.IOException;

import org.openqa.selenium.WebDriver;

import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import luckyclient.caserun.exinterface.TestControl;
import luckyclient.caserun.exwebdriver.WebDriverInitialization;
import luckyclient.dblog.DbLink;
import luckyclient.dblog.LogOperation;
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
public class WebOneCaseExecuteTestLink{

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
		 //ɾ���ɵ�����
		LogOperation.deleteCaseDetail(testCaseExternalId, taskid);
		//ɾ���ɵ���־
		LogOperation.deleteCaseLogDetail(testCaseExternalId, taskid);    
		TestBuildApi.getBuild(projectname);
		TestCase testcase = TestCaseApi.getTestCaseByExternalId(testCaseExternalId, version);
		luckyclient.publicclass.LogUtil.APP.info("��ʼִ����������"+testCaseExternalId+"��......");
		try {
			WebCaseExecutionTestLink.caseExcution(projectname,testcase, taskid,wd,caselog);
			luckyclient.publicclass.LogUtil.APP.info("��ǰ��������"+testcase.getFullExternalId()+"��ִ�����......������һ��");
		} catch (InterruptedException e) {
			luckyclient.publicclass.LogUtil.APP.error("�û�ִ�й������׳��쳣��", e);
			e.printStackTrace();
		}
        //�ر������
        wd.quit();
	}

}
