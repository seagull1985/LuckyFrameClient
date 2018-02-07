package luckyclient.caserun;

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
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 seagull1985
 * =================================================================
 * 
 * @author： seagull
 * 
 * @date 2017年12月1日 上午9:29:40
 * 
 */
public class BatchCaseExecute extends TestControl {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			PropertyConfigurator.configure(System.getProperty("user.dir") + "\\log4j.conf");
			String taskid = args[0];
			String batchcase = args[1];
			TestTaskexcute task = GetServerAPI.cgetTaskbyid(Integer.valueOf(taskid));
			if (task.getTestJob().getExtype() == 0) {
				if (task.getTestJob().getProjecttype() == 1) {
					// 接口测试
					BatchTestLinkCaseExecution.batchCaseExecuteForTast(task.getTestJob().getPlanproj(),
							String.valueOf(task.getId()), batchcase);
				} else if (task.getTestJob().getProjecttype() == 0) {
					BatchTestCaseExecution.batchCaseExecuteForTast(task.getTestJob().getPlanproj(),
							String.valueOf(task.getId()), batchcase);
				}
			} else if (task.getTestJob().getExtype() == 1) {
				if (task.getTestJob().getProjecttype() == 1) {
					// UI测试
					WebBatchExecuteTestLink.batchCaseExecuteForTast(task.getTestJob().getPlanproj(),
							String.valueOf(task.getId()), batchcase);
				} else if (task.getTestJob().getProjecttype() == 0) {
					// UI测试
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
