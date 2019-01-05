package luckyclient.caserun;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;

import luckyclient.caserun.exinterface.TestControl;
import luckyclient.caserun.exinterface.WebTestCaseDebug;

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
public class WebDebugExecute extends TestControl{

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		PropertyConfigurator.configure(System.getProperty("user.dir") + File.separator + "log4j.conf");
 		String sign = args[0];
 		String executor = args[1];
 		WebTestCaseDebug.oneCaseDebug(sign,executor);
 		System.exit(0);
	}
}
