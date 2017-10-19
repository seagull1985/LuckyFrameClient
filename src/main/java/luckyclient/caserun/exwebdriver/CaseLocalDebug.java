package luckyclient.caserun.exwebdriver;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import luckyclient.caserun.exwebdriver.ex.WebCaseExecution;
import luckyclient.dblog.DbLink;
import luckyclient.dblog.LogOperation;
import luckyclient.planapi.api.GetServerAPI;
import luckyclient.planapi.entity.ProjectCase;
import luckyclient.planapi.entity.ProjectCasesteps;

public class CaseLocalDebug{

	
	public static void OneCasedebug(WebDriver wd,String testCaseExternalId){
		DbLink.exetype = 1;   //不记录日志到数据库
		LogOperation caselog = new LogOperation(); // 初始化写用例结果以及日志模块
		try {
			ProjectCase testcase = GetServerAPI.cgetCaseBysign(testCaseExternalId);
			luckyclient.publicclass.LogUtil.APP.info("开始执行用例：【"+testCaseExternalId+"】......");
			List<ProjectCasesteps> steps=GetServerAPI.getStepsbycaseid(testcase.getId());
			WebCaseExecution.CaseExcution(testcase,steps, "888888",wd,caselog);
			luckyclient.publicclass.LogUtil.APP.info("当前用例：【"+testcase.getSign()+"】执行完成......进入下一条");
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error("用户执行过程中抛出异常！", e);
			e.printStackTrace();
		}
        //关闭浏览器
        wd.quit();
	}
	
	/**
	 * @param 项目名
	 * @param 用例编号
	 * @param 用例版本号
	 * 用于在testlink上配置好用例参数后，做多条用例串行调试
	 */
	public static void MoreCaseDebug(WebDriver wd,String projectname,Map<String,Integer> addtestcase){
		System.out.println(addtestcase.size());
		@SuppressWarnings("rawtypes")
		Iterator it=addtestcase.entrySet().iterator();
		while(it.hasNext()){
		    @SuppressWarnings("rawtypes")
			Map.Entry entry=(Map.Entry)it.next();
		    String testCaseExternalId = (String)entry.getKey();
		    Integer version = (Integer)entry.getValue();
		    try{
		    luckyclient.publicclass.LogUtil.APP.info("开始调用方法，项目名："+projectname+"，用例编号："+testCaseExternalId+"，用例版本："+version); 
		    OneCasedebug(wd,testCaseExternalId);
		    }catch(Exception e){
		    	continue;
		    }
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub	
		

	}

}
