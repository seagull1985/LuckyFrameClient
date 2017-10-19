package luckyclient.caserun;

import org.apache.log4j.PropertyConfigurator;

import luckyclient.caserun.exinterface.TestControl;
import luckyclient.caserun.exwebdriver.WebTestControl;
import luckyclient.planapi.api.GetServerAPI;
import luckyclient.planapi.entity.TestTaskexcute;


public class RunAutomationTest extends TestControl{
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			PropertyConfigurator.configure(System.getProperty("user.dir")
					+ "\\log4j.conf");
     		String taskid = args[0];
     		TestTaskexcute task=GetServerAPI.cgetTaskbyid(Integer.valueOf(taskid));
     		if(task.getTestJob().getExtype()==0){
     			TestControl.TastExecutionPlan(taskid, task);   //Ω”ø⁄≤‚ ‘
     		}else if(task.getTestJob().getExtype()==1){
     			WebTestControl.TaskExecutionPlan(taskid, task);   //UI≤‚ ‘
     		}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
