package luckyclient.caserun;

import org.apache.log4j.PropertyConfigurator;

import luckyclient.caserun.exinterface.TestCaseExecution;
import luckyclient.caserun.exinterface.TestControl;
import luckyclient.caserun.exinterface.testlink.TestLinkCaseExecution;
import luckyclient.caserun.exwebdriver.ex.WebOneCaseExecute;
import luckyclient.caserun.exwebdriver.extestlink.WebOneCaseExecuteTestLink;
import luckyclient.planapi.api.GetServerAPI;
import luckyclient.planapi.entity.TestTaskexcute;


public class OneCaseExecute extends TestControl{

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		PropertyConfigurator.configure(System.getProperty("user.dir")
				+ "\\log4j.conf");
 		String taskid = args[0];
 		String testCaseExternalId = args[1];
 		int version = Integer.parseInt(args[2]);
 		TestTaskexcute task=GetServerAPI.cgetTaskbyid(Integer.valueOf(taskid));
 		if(task.getTestJob().getExtype()==0){
 			if(task.getTestJob().getProjecttype()==0){
 				TestLinkCaseExecution.OneCaseExecuteForTast(task.getTestJob().getPlanproj(), testCaseExternalId,version,String.valueOf(task.getId()));   //testlinkΩ”ø⁄≤‚ ‘
 			}else if(task.getTestJob().getProjecttype()==1){
 				TestCaseExecution.OneCaseExecuteForTast(task.getTestJob().getPlanproj(), testCaseExternalId,version,String.valueOf(task.getId()));   //Ω”ø⁄≤‚ ‘
 			}
 			
 		}else if(task.getTestJob().getExtype()==1){
 			if(task.getTestJob().getProjecttype()==0){
 				WebOneCaseExecuteTestLink.OneCaseExecuteForTast(task.getTestJob().getPlanproj(), testCaseExternalId,version,String.valueOf(task.getId()));   //UI≤‚ ‘
 			}else if(task.getTestJob().getProjecttype()==1){
 				WebOneCaseExecute.OneCaseExecuteForTast(task.getTestJob().getPlanproj(), testCaseExternalId,version,String.valueOf(task.getId()));   //UI≤‚ ‘
 			}
 			
 		}
	}
}
