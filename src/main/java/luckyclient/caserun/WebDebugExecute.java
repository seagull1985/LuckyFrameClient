package luckyclient.caserun;

import org.apache.log4j.PropertyConfigurator;

import luckyclient.caserun.exinterface.TestCaseExecution;
import luckyclient.caserun.exinterface.TestControl;
import luckyclient.caserun.exinterface.WebTestCaseDebug;
import luckyclient.caserun.exinterface.testlink.TestLinkCaseExecution;
import luckyclient.caserun.exwebdriver.ex.WebOneCaseExecute;
import luckyclient.caserun.exwebdriver.extestlink.WebOneCaseExecuteTestLink;
import luckyclient.planapi.api.GetServerAPI;
import luckyclient.planapi.entity.TestTaskexcute;


public class WebDebugExecute extends TestControl{

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		PropertyConfigurator.configure(System.getProperty("user.dir")
				+ "\\log4j.conf");
 		String sign = args[0];
 		String executor = args[1];
 		WebTestCaseDebug.OneCaseDebug(sign,executor);
	}
}
