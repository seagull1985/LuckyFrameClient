package luckyclient.caserun;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import luckyclient.caserun.exappium.androidex.AndroidBatchExecute;
import luckyclient.caserun.exappium.iosex.IosBatchExecute;
import luckyclient.caserun.exinterface.BatchTestCaseExecution;
import luckyclient.caserun.exinterface.TestControl;
import luckyclient.caserun.exinterface.testlink.BatchTestLinkCaseExecution;
import luckyclient.caserun.exwebdriver.ex.WebBatchExecute;
import luckyclient.caserun.exwebdriver.extestlink.WebBatchExecuteTestLink;
import luckyclient.planapi.api.GetServerAPI;
import luckyclient.planapi.entity.TestTaskexcute;

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
public class BatchCaseExecute extends TestControl {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			PropertyConfigurator.configure(System.getProperty("user.dir") + File.separator + "log4j.conf");
			String taskid = args[0];
			String batchcase = args[1];
			TestTaskexcute task = GetServerAPI.cgetTaskbyid(Integer.valueOf(taskid));
			if (task.getTestJob().getExtype() == 0) {
				if (task.getTestJob().getProjecttype() == 1) {
					// �ӿڲ���
					BatchTestLinkCaseExecution.batchCaseExecuteForTast(task.getTestJob().getPlanproj(),
							String.valueOf(task.getId()), batchcase);
				} else if (task.getTestJob().getProjecttype() == 0) {
					BatchTestCaseExecution.batchCaseExecuteForTast(task.getTestJob().getPlanproj(),
							String.valueOf(task.getId()), batchcase);
				}
			} else if (task.getTestJob().getExtype() == 1) {
				if (task.getTestJob().getProjecttype() == 1) {
					// UI����
					WebBatchExecuteTestLink.batchCaseExecuteForTast(task.getTestJob().getPlanproj(),
							String.valueOf(task.getId()), batchcase);
				} else if (task.getTestJob().getProjecttype() == 0) {
					// UI����
					WebBatchExecute.batchCaseExecuteForTast(task.getTestJob().getPlanproj(),
							String.valueOf(task.getId()), batchcase);
				}

			} else if (task.getTestJob().getExtype() == 2) {
				Properties properties = luckyclient.publicclass.AppiumConfig.getConfiguration();

				if ("Android".equals(properties.getProperty("platformName"))) {
					AndroidBatchExecute.batchCaseExecuteForTast(task.getTestJob().getPlanproj(),
							String.valueOf(task.getId()), batchcase);
				} else if ("IOS".equals(properties.getProperty("platformName"))) {
					IosBatchExecute.batchCaseExecuteForTast(task.getTestJob().getPlanproj(),
							String.valueOf(task.getId()), batchcase);
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
