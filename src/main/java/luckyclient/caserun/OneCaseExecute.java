package luckyclient.caserun;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import luckyclient.caserun.exappium.androidex.AndroidOneCaseExecute;
import luckyclient.caserun.exappium.iosex.IosOneCaseExecute;
import luckyclient.caserun.exinterface.TestCaseExecution;
import luckyclient.caserun.exinterface.TestControl;
import luckyclient.caserun.exinterface.testlink.TestLinkCaseExecution;
import luckyclient.caserun.exwebdriver.ex.WebOneCaseExecute;
import luckyclient.caserun.exwebdriver.extestlink.WebOneCaseExecuteTestLink;
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
public class OneCaseExecute extends TestControl {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		PropertyConfigurator.configure(System.getProperty("user.dir")+ File.separator +"log4j.conf");
		String taskid = args[0];
		String testCaseExternalId = args[1];
		int version = Integer.parseInt(args[2]);
		TestTaskexcute task = GetServerAPI.cgetTaskbyid(Integer.valueOf(taskid));
		if (task.getTestJob().getExtype() == 0) {
			if (task.getTestJob().getProjecttype() == 1) {
				// testlink�ӿڲ���
				TestLinkCaseExecution.oneCaseExecuteForTast(task.getTestJob().getPlanproj(), testCaseExternalId,
						version, String.valueOf(task.getId()));
			} else if (task.getTestJob().getProjecttype() == 0) {
				// �ӿڲ���
				TestCaseExecution.oneCaseExecuteForTast(task.getTestJob().getPlanproj(), testCaseExternalId, version,
						String.valueOf(task.getId()));
			}

		} else if (task.getTestJob().getExtype() == 1) {
			if (task.getTestJob().getProjecttype() == 1) {
				// UI����
				WebOneCaseExecuteTestLink.oneCaseExecuteForTast(task.getTestJob().getPlanproj(), testCaseExternalId,
						version, String.valueOf(task.getId()));
			} else if (task.getTestJob().getProjecttype() == 0) {
				WebOneCaseExecute.oneCaseExecuteForTast(task.getTestJob().getPlanproj(), testCaseExternalId, version,
						String.valueOf(task.getId()));
			}

		} else if (task.getTestJob().getExtype() == 2) {
			Properties properties = luckyclient.publicclass.AppiumConfig.getConfiguration();

			if ("Android".equals(properties.getProperty("platformName"))) {
				AndroidOneCaseExecute.oneCaseExecuteForTast(task.getTestJob().getPlanproj(), testCaseExternalId,
						version, String.valueOf(task.getId()));
			} else if ("IOS".equals(properties.getProperty("platformName"))) {
				IosOneCaseExecute.oneCaseExecuteForTast(task.getTestJob().getPlanproj(), testCaseExternalId, version,
						String.valueOf(task.getId()));
			}

		}
		System.exit(0);
	}
}
