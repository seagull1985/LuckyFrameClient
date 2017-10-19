package luckyclient.caserun;

import org.apache.log4j.PropertyConfigurator;

import luckyclient.caserun.exinterface.TestControl;
import luckyclient.caserun.exinterface.WebTestCaseDebug;


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
