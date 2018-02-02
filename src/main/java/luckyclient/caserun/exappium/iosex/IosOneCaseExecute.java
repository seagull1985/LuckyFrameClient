package luckyclient.caserun.exappium.iosex;

import java.io.IOException;
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
public class IosOneCaseExecute {

	public static void oneCaseExecuteForTast(String projectname, String testCaseExternalId, int version, String taskid)
			throws IOException {
		// 记录日志到数据库
		DbLink.exetype = 0;
		TestControl.TASKID = taskid;
		IOSDriver<IOSElement> iosd = null;
		try {
			iosd = AppiumInitialization.setIosAppium();
		} catch (IOException e1) {
			luckyclient.publicclass.LogUtil.APP.error("初始化IOSDriver出错！", e1);
			e1.printStackTrace();
		}
		LogOperation caselog = new LogOperation();
		// 删除旧的用例
		LogOperation.deleteCaseDetail(testCaseExternalId, taskid);
		// 删除旧的日志
		LogOperation.deleteCaseLogDetail(testCaseExternalId, taskid);
		ProjectCase testcase = GetServerAPI.cgetCaseBysign(testCaseExternalId);
		List<PublicCaseParams> pcplist = GetServerAPI.cgetParamsByProjectid(String.valueOf(testcase.getProjectid()));
		luckyclient.publicclass.LogUtil.APP.info("开始执行用例：【" + testCaseExternalId + "】......");
		try {
			List<ProjectCasesteps> steps = GetServerAPI.getStepsbycaseid(testcase.getId());
			IosCaseExecution.caseExcution(testcase, steps, taskid, iosd, caselog, pcplist);
			luckyclient.publicclass.LogUtil.APP.info("当前用例：【" + testcase.getSign() + "】执行完成......进入下一条");
		} catch (InterruptedException e) {
			luckyclient.publicclass.LogUtil.APP.error("用户执行过程中抛出异常！", e);
			e.printStackTrace();
		}
		iosd.closeApp();
	}

}
