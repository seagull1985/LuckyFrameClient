package luckyclient.caserun.exappium.androidex;

import java.io.IOException;
import java.util.List;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import luckyclient.dblog.DbLink;
import luckyclient.dblog.LogOperation;
import luckyclient.serverapi.api.GetServerAPI;
import luckyclient.serverapi.entity.ProjectCase;
import luckyclient.serverapi.entity.ProjectCaseParams;
import luckyclient.serverapi.entity.ProjectCaseSteps;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 seagull1985
 * =================================================================
 * 
 * @author seagull
 * @date 2018年1月29日
 * 
 */
public class AndroidCaseLocalDebug {

	public static void oneCasedebug(AndroidDriver<AndroidElement> androiddriver, String testCaseExternalId) {
		// 不记录日志到数据库
		DbLink.exetype = 1;
		LogOperation caselog = new LogOperation();

		try {
			ProjectCase testcase = GetServerAPI.cgetCaseBysign(testCaseExternalId);
			List<ProjectCaseParams> pcplist = GetServerAPI
					.cgetParamsByProjectid(String.valueOf(testcase.getProjectId()));
			luckyclient.publicclass.LogUtil.APP.info("开始执行用例：【" + testCaseExternalId + "】......");
			List<ProjectCaseSteps> steps = GetServerAPI.getStepsbycaseid(testcase.getCaseId());
			AndroidCaseExecution.caseExcution(testcase, steps, "888888", androiddriver, caselog, pcplist);

			luckyclient.publicclass.LogUtil.APP.info("当前用例：【" + testcase.getCaseSign() + "】执行完成......进入下一条");
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error("用户执行过程中抛出异常！", e);
			e.printStackTrace();
		}
	}

	/**
	 * 用于做多条用例串行调试
	 * @param androiddriver
	 * @param projectname
	 * @param addtestcase
	 * @author Seagull
	 * @date 2019年4月18日
	 */
	public static void moreCaseDebug(AndroidDriver<AndroidElement> androiddriver, String projectname,
			List<String> addtestcase) {
		System.out.println("当前调试用例总共："+addtestcase.size());
		for(String testCaseExternalId:addtestcase) {
			try {
				luckyclient.publicclass.LogUtil.APP
						.info("开始调用方法，项目名：" + projectname + "，用例编号：" + testCaseExternalId);
				oneCasedebug(androiddriver, testCaseExternalId);
			} catch (Exception e) {
				continue;
			}
		}
		// 关闭APP以及appium会话
		androiddriver.closeApp();
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub	
	}

}
