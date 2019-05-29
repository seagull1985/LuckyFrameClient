package luckyclient.caserun.publicdispose;

import luckyclient.publicclass.LogUtil;

/**
 * 动作关键字处理
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 seagull
 * =================================================================
 * @author Seagull
 * @date 2019年1月15日
 */
public class ActionManageForSteps {

	/**
	 * 解析用例步骤
	 * @param stepsaction
	 * @param testresult
	 * @return
	 */
	public static String actionManage(String stepsaction,String testresult){
		LogUtil.APP.info("测试结果是：" + testresult);
		LogUtil.APP.info("现在进入到Action(动作)处理......ACTION值："+stepsaction);
		if(null==stepsaction||"".equals(stepsaction.trim())){
			LogUtil.APP.info("Action(动作)无需处理......");
			return testresult;
		}
		stepsaction=stepsaction.trim();
		String[] temp=stepsaction.split("\\|",-1);
		for(String actionorder:temp){
			if(null!=actionorder&&!"".equals(actionorder.trim())){
				testresult=actionExecute(actionorder,testresult);
			}
		}
		return testresult;
	}

	/**
	 * 动作关键字执行
	 * @param actionKeyWord
	 * @param testResult
	 * @return
	 */
	private static String actionExecute(String actionKeyWord,String testResult){
		try{

			String[] actionArr = actionKeyWord.split("#");
			if(actionArr.length == 2){
				ActionContext actionContext = new ActionContext(actionArr[1]);
				testResult = actionContext.parse(actionKeyWord, testResult);
			}else {
				testResult="关键字语法书写有误，请检查关键字："+actionKeyWord;
				LogUtil.APP.error("关键字语法书写有误，请检查关键字："+actionKeyWord);
			}
			return testResult;
		}catch(Exception e){
			testResult="处理步骤动作事件过程中出现异常，直接返回测试结果："+actionKeyWord;
			LogUtil.APP.error("处理步骤动作事件过程中出现异常，直接返回测试结果：" ,e);
			return testResult;
		}
	}

}
