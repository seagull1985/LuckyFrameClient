package luckyclient.caserun;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;

import luckyclient.caserun.exappium.AppTestControl;
import luckyclient.caserun.exinterface.TestControl;
import luckyclient.caserun.exwebdriver.WebTestControl;
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
public class RunAutomationTest extends TestControl {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			PropertyConfigurator.configure(System.getProperty("user.dir") + File.separator + "log4j.conf");
			String taskid = args[0];
			TestTaskexcute task = GetServerAPI.cgetTaskbyid(Integer.valueOf(taskid));

			if (task.getTestJob().getExtype() == 0) {
				// �ӿڲ���
				TestControl.taskExecutionPlan(taskid, task);
			} else if (task.getTestJob().getExtype() == 1) {
				// UI����
				WebTestControl.taskExecutionPlan(taskid, task);
			} else if (task.getTestJob().getExtype() == 2) {
				AppTestControl.taskExecutionPlan(taskid, task);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
