package luckyclient.caserun.exappium.iosex;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Properties;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import luckyclient.caserun.exappium.AppiumInitialization;
import luckyclient.caserun.exappium.AppiumService;
import luckyclient.caserun.exinterface.TestControl;
import luckyclient.dblog.DbLink;
import luckyclient.dblog.LogOperation;
import luckyclient.planapi.api.GetServerAPI;
import luckyclient.planapi.entity.ProjectCase;
import luckyclient.planapi.entity.ProjectCasesteps;
import luckyclient.planapi.entity.PublicCaseParams;
import luckyclient.planapi.entity.TestTaskexcute;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸� ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944 seagull1985
 * =================================================================
 * 
 * @author�� seagull
 * 
 * @date 2018��2��2��
 * 
 */
public class IosBatchExecute {

	public static void batchCaseExecuteForTast(String projectname, String taskid, String batchcase) throws IOException, InterruptedException {
		// ��¼��־�����ݿ�
		DbLink.exetype = 0;
		TestControl.TASKID = taskid;
		IOSDriver<IOSElement> iosd = null;
		AppiumService as=null;
		try {
			Properties properties = luckyclient.publicclass.AppiumConfig.getConfiguration();
			//���������Զ�����Appiume����
			if(Boolean.valueOf(properties.getProperty("autoRunAppiumService"))){
				as =new AppiumService();
				as.start();
				Thread.sleep(10000);
			}
			
			iosd = AppiumInitialization.setIosAppium(properties);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		LogOperation caselog = new LogOperation();
		TestTaskexcute task = GetServerAPI.cgetTaskbyid(Integer.valueOf(taskid));
		List<PublicCaseParams> pcplist = GetServerAPI
				.cgetParamsByProjectid(task.getTestJob().getProjectid().toString());
		// ִ��ȫ���ǳɹ�״̬����
		if (batchcase.indexOf("ALLFAIL") > -1) {
			String casemore = caselog.unSucCaseUpdate(taskid);
			String[] temp = casemore.split("\\#", -1);
			for (int i = 0; i < temp.length; i++) {
				String testCaseExternalId = temp[i].substring(0, temp[i].indexOf("%"));
				ProjectCase testcase = GetServerAPI.cgetCaseBysign(testCaseExternalId);
				List<ProjectCasesteps> steps = GetServerAPI.getStepsbycaseid(testcase.getId());
				// ɾ���ɵ���־
				LogOperation.deleteCaseLogDetail(testCaseExternalId, taskid);
				try {
					IosCaseExecution.caseExcution(testcase, steps, taskid, iosd, caselog, pcplist);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					luckyclient.publicclass.LogUtil.APP.error("�û�ִ�й������׳��쳣��", e);
					e.printStackTrace();
				}
			}
		} else { // ����ִ������
			String[] temp = batchcase.split("\\#", -1);
			for (int i = 0; i < temp.length; i++) {
				String testCaseExternalId = temp[i].substring(0, temp[i].indexOf("%"));
				// int version =
				// Integer.parseInt(temp[i].substring(temp[i].indexOf("%")+1,temp[i].length()));
				ProjectCase testcase = GetServerAPI.cgetCaseBysign(testCaseExternalId);
				List<ProjectCasesteps> steps = GetServerAPI.getStepsbycaseid(testcase.getId());
				// ɾ���ɵ���־
				LogOperation.deleteCaseLogDetail(testCaseExternalId, taskid);
				try {
					IosCaseExecution.caseExcution(testcase, steps, taskid, iosd, caselog, pcplist);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					luckyclient.publicclass.LogUtil.APP.error("�û�ִ�й������׳��쳣��", e);
					e.printStackTrace();
				}
			}
		}
		LogOperation.updateTastdetail(taskid, 0);
		iosd.closeApp();
		//�ر�Appium������߳�
		if(as!=null){
			as.interrupt();
		}
	}

}
