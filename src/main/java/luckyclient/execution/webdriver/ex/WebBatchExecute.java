package luckyclient.execution.webdriver.ex;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.openqa.selenium.WebDriver;

import luckyclient.execution.httpinterface.TestControl;
import luckyclient.execution.webdriver.WebDriverInitialization;
import luckyclient.remote.api.GetServerApi;
import luckyclient.remote.api.serverOperation;
import luckyclient.remote.entity.ProjectCase;
import luckyclient.remote.entity.ProjectCaseParams;
import luckyclient.remote.entity.ProjectCaseSteps;
import luckyclient.remote.entity.TaskExecute;
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
public class WebBatchExecute{
	
	public static void batchCaseExecuteForTast(String taskid, String batchcase) throws IOException{
		//记录日志到数据库
		serverOperation.exetype = 0;   
		TestControl.TASKID = taskid;
		int drivertype = serverOperation.querydrivertype(taskid);
		WebDriver wd = null;
		try {
			wd = WebDriverInitialization.setWebDriverForTask(drivertype);
		} catch (MalformedURLException e1) {
			LogUtil.APP.error("初始化WebDriver出现异常！", e1);
		}
		serverOperation caselog = new serverOperation();
		TaskExecute task=GetServerApi.cgetTaskbyid(Integer.parseInt(taskid));
		List<ProjectCaseParams> pcplist=GetServerApi.cgetParamsByProjectid(task.getProjectId().toString());
		 //执行全部非成功状态用例
		if(batchcase.contains("ALLFAIL")){
			List<Integer> caseIdList = caselog.getCaseListForUnSucByTaskId(taskid);
			for (Integer integer : caseIdList) {
				ProjectCase testcase = GetServerApi.cGetCaseByCaseId(integer);
				List<ProjectCaseSteps> steps = GetServerApi.getStepsbycaseid(testcase.getCaseId());
				//删除旧的日志
				serverOperation.deleteTaskCaseLog(testcase.getCaseId(), taskid);
				try {
					WebCaseExecution.caseExcution(testcase, steps, taskid,null, wd, caselog, pcplist);
				} catch (Exception e) {
					LogUtil.APP.error("用户执行过程中抛出异常！", e);
				}
			}			
		}else{                                           //批量执行用例
			String[] temp=batchcase.split("#");
			for (String s : temp) {
				ProjectCase testcase = GetServerApi.cGetCaseByCaseId(Integer.valueOf(s));
				List<ProjectCaseSteps> steps = GetServerApi.getStepsbycaseid(testcase.getCaseId());
				//删除旧的日志
				serverOperation.deleteTaskCaseLog(testcase.getCaseId(), taskid);
				try {
					WebCaseExecution.caseExcution(testcase, steps, taskid,null, wd, caselog, pcplist);
				} catch (Exception e) {
					LogUtil.APP.error("用户执行过程中抛出异常！", e);
				}
			}
		}
		serverOperation.updateTaskExecuteData(taskid, 0,2);
        //关闭浏览器
		assert wd != null;
		wd.quit();
	}
	
}
