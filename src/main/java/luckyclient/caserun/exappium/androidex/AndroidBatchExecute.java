package luckyclient.caserun.exappium.androidex;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Properties;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import luckyclient.caserun.exappium.AppiumInitialization;
import luckyclient.caserun.exappium.AppiumService;
import luckyclient.caserun.exinterface.TestControl;
import luckyclient.dblog.DbLink;
import luckyclient.dblog.LogOperation;
import luckyclient.publicclass.AppiumConfig;
import luckyclient.publicclass.LogUtil;
import luckyclient.serverapi.api.GetServerAPI;
import luckyclient.serverapi.entity.ProjectCase;
import luckyclient.serverapi.entity.ProjectCaseParams;
import luckyclient.serverapi.entity.ProjectCaseSteps;
import luckyclient.serverapi.entity.TaskExecute;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 seagull1985
 * =================================================================
 * 
 * @author： seagull
 * 
 * @date 2018年1月26日 上午15:29:40
 * 
 */
public class AndroidBatchExecute {

	public static void batchCaseExecuteForTast(String projectname, String taskid, String batchcase) throws IOException, InterruptedException {
		// 记录日志到数据库
		DbLink.exetype = 0;
		TestControl.TASKID = taskid;
		AndroidDriver<AndroidElement> ad = null;
		AppiumService as=null;
		try {
			Properties properties = AppiumConfig.getConfiguration();
			//根据配置自动启动Appiume服务
			if(Boolean.valueOf(properties.getProperty("autoRunAppiumService"))){
				as =new AppiumService();
				as.start();
				Thread.sleep(10000);
			}
			
			ad = AppiumInitialization.setAndroidAppium(properties);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			LogUtil.APP.error("安卓手机根据配置自动启动Appiume服务出现异常",e);
		}
		LogOperation caselog = new LogOperation();
		TaskExecute task = GetServerAPI.cgetTaskbyid(Integer.valueOf(taskid));
		List<ProjectCaseParams> pcplist = GetServerAPI
				.cgetParamsByProjectid(task.getProjectId().toString());
		// 执行全部非成功状态用例
		if (batchcase.indexOf("ALLFAIL") > -1) {
			List<Integer> caseIdList = caselog.getCaseListForUnSucByTaskId(taskid);
			for (int i = 0; i < caseIdList.size(); i++) {
				ProjectCase testcase = GetServerAPI.cGetCaseByCaseId(caseIdList.get(i));
				List<ProjectCaseSteps> steps = GetServerAPI.getStepsbycaseid(testcase.getCaseId());
				// 删除旧的日志
				LogOperation.deleteTaskCaseLog(testcase.getCaseId(), taskid);
				try {
					AndroidCaseExecution.caseExcution(testcase, steps, taskid, ad, caselog, pcplist);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					LogUtil.APP.error("用户执行过程中抛出异常！", e);
				}
			}
		} else { // 批量执行用例
			String[] temp = batchcase.split("\\#");
			for (int i = 0; i < temp.length; i++) {
				ProjectCase testcase = GetServerAPI.cGetCaseByCaseId(Integer.valueOf(temp[i]));
				List<ProjectCaseSteps> steps = GetServerAPI.getStepsbycaseid(testcase.getCaseId());
				// 删除旧的日志
				LogOperation.deleteTaskCaseLog(testcase.getCaseId(), taskid);
				try {
					AndroidCaseExecution.caseExcution(testcase, steps, taskid, ad, caselog, pcplist);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					LogUtil.APP.error("用户执行过程中抛出异常！", e);
				}
			}
		}
		LogOperation.updateTaskExecuteData(taskid, 0);
		ad.closeApp();
		//关闭Appium服务的线程
		if(as!=null){
			as.interrupt();
		}
	}

}
