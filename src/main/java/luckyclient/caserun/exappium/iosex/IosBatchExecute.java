package luckyclient.caserun.exappium.iosex;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import luckyclient.caserun.exappium.AppiumInitialization;
import luckyclient.caserun.exinterface.TestControl;
import luckyclient.dblog.DbLink;
import luckyclient.dblog.LogOperation;
import luckyclient.planapi.api.GetServerAPI;
import luckyclient.planapi.entity.ProjectCase;
import luckyclient.planapi.entity.ProjectCasesteps;
import luckyclient.planapi.entity.PublicCaseParams;
import luckyclient.planapi.entity.TestTaskexcute;
import luckyclient.testlinkapi.TestBuildApi;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 seagull1985
 * =================================================================
 * 
 * @author： seagull
 * 
 * @date 2018年2月2日
 * 
 */
public class IosBatchExecute {

	public static void batchCaseExecuteForTast(String projectname, String taskid, String batchcase) throws IOException {
		// 记录日志到数据库
		DbLink.exetype = 0;
		TestControl.TASKID = taskid;
		IOSDriver<IOSElement> iosd = null;
		try {
			iosd = AppiumInitialization.setIosAppium();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		LogOperation caselog = new LogOperation();
		TestBuildApi.getBuild(projectname);
		TestTaskexcute task = GetServerAPI.cgetTaskbyid(Integer.valueOf(taskid));
		List<PublicCaseParams> pcplist = GetServerAPI
				.cgetParamsByProjectid(task.getTestJob().getProjectid().toString());
		// 执行全部非成功状态用例
		if (batchcase.indexOf("ALLFAIL") > -1) {
			String casemore = caselog.unSucCaseUpdate(taskid);
			String[] temp = casemore.split("\\#", -1);
			for (int i = 0; i < temp.length; i++) {
				String testCaseExternalId = temp[i].substring(0, temp[i].indexOf("%"));
				ProjectCase testcase = GetServerAPI.cgetCaseBysign(testCaseExternalId);
				List<ProjectCasesteps> steps = GetServerAPI.getStepsbycaseid(testcase.getId());
				// 删除旧的用例
				LogOperation.deleteCaseDetail(testCaseExternalId, taskid);
				// 删除旧的日志
				LogOperation.deleteCaseLogDetail(testCaseExternalId, taskid);
				try {
					IosCaseExecution.caseExcution(testcase, steps, taskid, iosd, caselog, pcplist);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					luckyclient.publicclass.LogUtil.APP.error("用户执行过程中抛出异常！", e);
					e.printStackTrace();
				}
			}
		} else { // 批量执行用例
			String[] temp = batchcase.split("\\#", -1);
			for (int i = 0; i < temp.length; i++) {
				String testCaseExternalId = temp[i].substring(0, temp[i].indexOf("%"));
				// int version =
				// Integer.parseInt(temp[i].substring(temp[i].indexOf("%")+1,temp[i].length()));
				ProjectCase testcase = GetServerAPI.cgetCaseBysign(testCaseExternalId);
				List<ProjectCasesteps> steps = GetServerAPI.getStepsbycaseid(testcase.getId());
				try {
					IosCaseExecution.caseExcution(testcase, steps, taskid, iosd, caselog, pcplist);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					luckyclient.publicclass.LogUtil.APP.error("用户执行过程中抛出异常！", e);
					e.printStackTrace();
				}
			}
		}
		iosd.closeApp();
	}

}
