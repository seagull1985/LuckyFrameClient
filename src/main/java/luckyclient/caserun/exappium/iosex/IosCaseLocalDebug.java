package luckyclient.caserun.exappium.iosex;

import java.io.IOException;
import java.util.List;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
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
 * @author seagull
 * @date 2018年1月29日
 * 
 */
public class IosCaseLocalDebug {

	public static void oneCasedebug(IOSDriver<IOSElement> iosdriver, String testCaseExternalId) {
		// 不记录日志到数据库
		DbLink.exetype = 1;
		LogOperation caselog = new LogOperation();

		try {
			ProjectCase testcase = GetServerAPI.cgetCaseBysign(testCaseExternalId);
			List<PublicCaseParams> pcplist = GetServerAPI
					.cgetParamsByProjectid(String.valueOf(testcase.getProjectid()));
			luckyclient.publicclass.LogUtil.APP.info("开始执行用例：【" + testCaseExternalId + "】......");
			List<ProjectCasesteps> steps = GetServerAPI.getStepsbycaseid(testcase.getId());
			IosCaseExecution.caseExcution(testcase, steps, "888888", iosdriver, caselog, pcplist);

			luckyclient.publicclass.LogUtil.APP.info("当前用例：【" + testcase.getSign() + "】执行完成......进入下一条");
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error("用户执行过程中抛出异常！", e);
			e.printStackTrace();
		}
	}

	/**
	 * @param 项目名
	 * @param 用例编号
	 * @param 用例版本号
	 *            用于在testlink上配置好用例参数后，做多条用例串行调试
	 */
	public static void moreCaseDebug(IOSDriver<IOSElement> iosdriver, String projectname,
			List<String> addtestcase) {
		System.out.println("当前调试用例总共："+addtestcase.size());
		for(String testCaseExternalId:addtestcase) {
			try {
				luckyclient.publicclass.LogUtil.APP
						.info("开始调用方法，项目名：" + projectname + "，用例编号：" + testCaseExternalId);
				oneCasedebug(iosdriver, testCaseExternalId);
			} catch (Exception e) {
				continue;
			}
		}
		// 关闭APP以及appium会话
		iosdriver.closeApp();
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub	
	}

}
