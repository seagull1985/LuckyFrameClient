package luckyclient.execution;

import java.io.File;
import java.util.Properties;

import luckyclient.netty.ClientHandler;
import org.apache.log4j.PropertyConfigurator;

import luckyclient.execution.appium.androidex.AndroidBatchExecute;
import luckyclient.execution.appium.iosex.IosBatchExecute;
import luckyclient.execution.httpinterface.BatchTestCaseExecution;
import luckyclient.execution.httpinterface.TestControl;
import luckyclient.execution.webdriver.ex.WebBatchExecute;
import luckyclient.remote.api.GetServerApi;
import luckyclient.remote.entity.TaskExecute;
import luckyclient.remote.entity.TaskScheduling;
import luckyclient.utils.LogUtil;
import luckyclient.utils.config.AppiumConfig;
import springboot.RunService;

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
			PropertyConfigurator.configure(RunService.APPLICATION_HOME + File.separator + "log4j.conf");
			String taskid = args[0];
			String batchcase = args[1];
			TaskExecute task = GetServerApi.cgetTaskbyid(Integer.parseInt(taskid));
			TaskScheduling taskScheduling = GetServerApi.cGetTaskSchedulingByTaskId(Integer.parseInt(taskid));
			ClientHandler.clientId = taskScheduling.getClientId();
			//获取到调度任务的环境信息
			SchedulingConstants.envName=taskScheduling.getEnvName();
			if (taskScheduling.getTaskType() == 0) {
					BatchTestCaseExecution.batchCaseExecuteForTast(
                            String.valueOf(task.getTaskId()), batchcase);
			} else if (taskScheduling.getTaskType() == 1) {
					// UI测试
					WebBatchExecute.batchCaseExecuteForTast(
							String.valueOf(task.getTaskId()), batchcase);

			} else if (taskScheduling.getTaskType() == 2) {
				Properties properties = AppiumConfig.getConfiguration();

				if ("Android".equals(properties.getProperty("platformName"))) {
					AndroidBatchExecute.batchCaseExecuteForTast(
                            String.valueOf(task.getTaskId()), batchcase);
				} else if ("IOS".equals(properties.getProperty("platformName"))) {
					IosBatchExecute.batchCaseExecuteForTast(
                            String.valueOf(task.getTaskId()), batchcase);
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogUtil.APP.error("启动批量运行用例主函数出现异常，请检查！",e);
		} finally{
			System.exit(0);
		}
	}

}
