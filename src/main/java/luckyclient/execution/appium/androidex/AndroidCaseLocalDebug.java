package luckyclient.execution.appium.androidex;

import java.util.List;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import luckyclient.remote.api.GetServerApi;
import luckyclient.remote.api.serverOperation;
import luckyclient.remote.entity.ProjectCase;
import luckyclient.remote.entity.ProjectCaseParams;
import luckyclient.remote.entity.ProjectCaseSteps;
import luckyclient.utils.LogUtil;

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

	/**
	 * IDEA工具方式调试测试用例
	 * @param androiddriver 初始化appium对象
	 * @param testCaseExternalId 用例编号
	 */
	public static void oneCasedebug(AndroidDriver<AndroidElement> androiddriver, String testCaseExternalId) {
		// 不记录日志到数据库
		serverOperation.exetype = 1;
		serverOperation caselog = new serverOperation();

		try {
			ProjectCase testcase = GetServerApi.cgetCaseBysign(testCaseExternalId);
			List<ProjectCaseParams> pcplist = GetServerApi
					.cgetParamsByProjectid(String.valueOf(testcase.getProjectId()));
			LogUtil.APP.info("开始执行用例：【{}】......",testCaseExternalId);
			List<ProjectCaseSteps> steps = GetServerApi.getStepsbycaseid(testcase.getCaseId());
			AndroidCaseExecution.caseExcution(testcase, steps, "888888",null, androiddriver, caselog, pcplist);

			LogUtil.APP.info("当前用例：【{}】执行完成......进入下一条",testcase.getCaseSign());
		} catch (Exception e) {
			LogUtil.APP.error("用户执行过程中抛出异常！", e);
		}
	}

	/**
	 * 用于做多条用例串行调试
	 * @param androiddriver 初始化appium对象
	 * @param projectname 项目名称
	 * @param addtestcase 用例集
	 * @author Seagull
	 * @date 2019年4月18日
	 */
	public static void moreCaseDebug(AndroidDriver<AndroidElement> androiddriver, String projectname,
			List<String> addtestcase) {
		System.out.println("当前调试用例总共："+addtestcase.size());
		for(String testCaseExternalId:addtestcase) {
			try {
				LogUtil.APP
						.info("开始调用方法，项目名：{}，用例编号：{}",projectname,testCaseExternalId);
				oneCasedebug(androiddriver, testCaseExternalId);
			} catch (Exception e) {
				LogUtil.APP.error("多用例调试过程中抛出异常！", e);
			}
		}
		// 关闭APP以及appium会话
		androiddriver.closeApp();
	}

}
