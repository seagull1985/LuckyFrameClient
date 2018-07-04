package luckyclient.caserun.exappium.iosex;

import java.io.IOException;
import java.util.List;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import luckyclient.dblog.DbLink;
import luckyclient.dblog.LogOperation;
import luckyclient.planapi.api.GetServerAPI;
import luckyclient.planapi.entity.ProjectCase;
import luckyclient.planapi.entity.ProjectCasesteps;
import luckyclient.planapi.entity.PublicCaseParams;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸� ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944 seagull1985
 * =================================================================
 * 
 * @author seagull
 * @date 2018��1��29��
 * 
 */
public class IosCaseLocalDebug {

	public static void oneCasedebug(IOSDriver<IOSElement> iosdriver, String testCaseExternalId) {
		// ����¼��־�����ݿ�
		DbLink.exetype = 1;
		LogOperation caselog = new LogOperation();

		try {
			ProjectCase testcase = GetServerAPI.cgetCaseBysign(testCaseExternalId);
			List<PublicCaseParams> pcplist = GetServerAPI
					.cgetParamsByProjectid(String.valueOf(testcase.getProjectid()));
			luckyclient.publicclass.LogUtil.APP.info("��ʼִ����������" + testCaseExternalId + "��......");
			List<ProjectCasesteps> steps = GetServerAPI.getStepsbycaseid(testcase.getId());
			IosCaseExecution.caseExcution(testcase, steps, "888888", iosdriver, caselog, pcplist);

			luckyclient.publicclass.LogUtil.APP.info("��ǰ��������" + testcase.getSign() + "��ִ�����......������һ��");
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error("�û�ִ�й������׳��쳣��", e);
			e.printStackTrace();
		}
	}

	/**
	 * @param ��Ŀ��
	 * @param �������
	 * @param �����汾��
	 *            ������testlink�����ú������������������������е���
	 */
	public static void moreCaseDebug(IOSDriver<IOSElement> iosdriver, String projectname,
			List<String> addtestcase) {
		System.out.println("��ǰ���������ܹ���"+addtestcase.size());
		for(String testCaseExternalId:addtestcase) {
			try {
				luckyclient.publicclass.LogUtil.APP
						.info("��ʼ���÷�������Ŀ����" + projectname + "��������ţ�" + testCaseExternalId);
				oneCasedebug(iosdriver, testCaseExternalId);
			} catch (Exception e) {
				continue;
			}
		}
		// �ر�APP�Լ�appium�Ự
		iosdriver.closeApp();
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub	
	}

}
