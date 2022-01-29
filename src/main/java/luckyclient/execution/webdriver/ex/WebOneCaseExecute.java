package luckyclient.execution.webdriver.ex;

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.WebDriver;

import luckyclient.execution.httpinterface.TestControl;
import luckyclient.execution.webdriver.WebDriverInitialization;
import luckyclient.remote.api.GetServerApi;
import luckyclient.remote.api.serverOperation;
import luckyclient.remote.entity.ProjectCase;
import luckyclient.remote.entity.ProjectCaseParams;
import luckyclient.remote.entity.ProjectCaseSteps;
import luckyclient.utils.LogUtil;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 * 
 * @author： seagull
 * @date 2017年12月1日 上午9:29:40
 * 
 */
public class WebOneCaseExecute{
	
	public static void oneCaseExecuteForTast(Integer caseId, String taskid){
		//记录日志到数据库
		serverOperation.exetype = 0;   
		TestControl.TASKID = taskid;
		int drivertype = serverOperation.querydrivertype(taskid);
		WebDriver wd = null;
		try {
			wd = WebDriverInitialization.setWebDriverForTask(drivertype);
		} catch (IOException e1) {
			LogUtil.APP.error("初始化WebDriver出错！", e1);
		}
		serverOperation caselog = new serverOperation();
		ProjectCase testcase = GetServerApi.cGetCaseByCaseId(caseId);
		//删除旧的日志
		serverOperation.deleteTaskCaseLog(testcase.getCaseId(), taskid);    

		List<ProjectCaseParams> pcplist=GetServerApi.cgetParamsByProjectid(String.valueOf(testcase.getProjectId()));
		LogUtil.APP.info("开始执行用例:【{}】......",testcase.getCaseSign());
		try {
			List<ProjectCaseSteps> steps=GetServerApi.getStepsbycaseid(testcase.getCaseId());
			WebCaseExecution.caseExcution(testcase, steps, taskid,null,wd,caselog,pcplist);
			LogUtil.APP.info("当前用例:【{}】执行完成......进入下一条",testcase.getCaseSign());
		} catch (Exception e) {
			LogUtil.APP.error("用户执行过程中抛出异常！", e);
		}
		serverOperation.updateTaskExecuteData(taskid, 0,2);
        //关闭浏览器
		assert wd != null;
		wd.quit();
	}

	public static void debugoneCaseExecute(int caseId, int drivertype){
//		//记录日志到数据库
//		serverOperation.exetype = 1;
		WebDriver wd = null;
		try {
			wd = WebDriverInitialization.setWebDriverForTask(drivertype);
		} catch (IOException e1) {
			LogUtil.APP.error("初始化WebDriver出错！", e1);
		}
//		serverOperation caselog = new serverOperation();
		ProjectCase testcase = GetServerApi.cGetCaseByCaseId(caseId);
		//删除旧的日志
//		serverOperation.deleteTaskCaseLog(testcase.getCaseId(), taskid);

		List<ProjectCaseParams> pcplist=GetServerApi.cgetParamsByProjectid(String.valueOf(testcase.getProjectId()));
		LogUtil.APP.info("开始执行用例:【{}】......",testcase.getCaseSign());
		try {
			List<ProjectCaseSteps> steps=GetServerApi.getStepsbycaseid(testcase.getCaseId());
			WebCaseExecution.caseExcution(testcase, steps, wd,pcplist);
			LogUtil.APP.info("当前用例:【{}】执行完成......进入下一条",testcase.getCaseSign());
		} catch (Exception e) {
			LogUtil.APP.error("用户执行过程中抛出异常！", e);
		}
//		serverOperation.updateTaskExecuteData(taskid, 0,2);
		//关闭浏览器
		assert wd != null;
		wd.quit();
	}

}
