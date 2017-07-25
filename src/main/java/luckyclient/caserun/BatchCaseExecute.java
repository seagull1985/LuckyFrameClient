package luckyclient.caserun;

import org.apache.log4j.PropertyConfigurator;

import luckyclient.caserun.exinterface.BatchTestCaseExecution;
import luckyclient.caserun.exinterface.TestControl;
import luckyclient.caserun.exinterface.testlink.BatchTestLinkCaseExecution;
import luckyclient.caserun.exwebdriver.ex.WebBatchExecute;
import luckyclient.caserun.exwebdriver.extestlink.WebBatchExecuteTestLink;
import luckyclient.planapi.api.GetServerAPI;
import luckyclient.planapi.entity.TestTaskexcute;


public class BatchCaseExecute extends TestControl{
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
 		try {
		PropertyConfigurator.configure(System.getProperty("user.dir")
				+ "\\log4j.conf");
 		String taskid = args[0];
 		String batchcase = args[1];
 		TestTaskexcute task=GetServerAPI.cgetTaskbyid(Integer.valueOf(taskid));
 		if(task.getTestJob().getExtype()==0){
 			if(task.getTestJob().getProjecttype()==0){
 				BatchTestLinkCaseExecution.BatchCaseExecuteForTast(task.getTestJob().getPlanproj(), String.valueOf(task.getId()), batchcase);   //Ω”ø⁄≤‚ ‘
 			}else if(task.getTestJob().getProjecttype()==1){
 				BatchTestCaseExecution.BatchCaseExecuteForTast(task.getTestJob().getPlanproj(), String.valueOf(task.getId()), batchcase);
 			}
 		}else if(task.getTestJob().getExtype()==1){
 			if(task.getTestJob().getProjecttype()==0){
 				WebBatchExecuteTestLink.BatchCaseExecuteForTast(task.getTestJob().getPlanproj(), String.valueOf(task.getId()), batchcase);   //UI≤‚ ‘
 			}else if(task.getTestJob().getProjecttype()==1){
 				WebBatchExecute.BatchCaseExecuteForTast(task.getTestJob().getPlanproj(), String.valueOf(task.getId()), batchcase);   //UI≤‚ ‘
 			}
 			
 		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
