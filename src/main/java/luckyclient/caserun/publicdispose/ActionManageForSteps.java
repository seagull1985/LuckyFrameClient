package luckyclient.caserun.publicdispose;

import luckyclient.driven.SubString;

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
	 * @param projectcase
	 * @param step
	 * @param taskid
	 * @param caselog
	 * @return
	 */
	public static String actionManage(String stepsaction,String testresult){
		luckyclient.publicclass.LogUtil.APP.info("测试结果是：" + testresult);
		luckyclient.publicclass.LogUtil.APP.info("现在进入到Action(动作)处理......ACTION值："+stepsaction);
		if(null==stepsaction||"".equals(stepsaction.trim())){
			luckyclient.publicclass.LogUtil.APP.info("Action(动作)无需处理......");
			return testresult;
		}
		stepsaction=stepsaction.toLowerCase().trim();
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
	 * @param actionorder
	 * @param testresult
	 * @return
	 */
	private static String actionExecute(String actionorder,String testresult){
		try{
	        // 处理动作事件
			if(actionorder.endsWith("#wait")){
				if(ChangString.isInteger(actionorder.substring(0, actionorder.lastIndexOf("#wait")))){
		            try {
		                // 获取步骤间等待时间
		                int time=Integer.parseInt(actionorder.substring(0, actionorder.lastIndexOf("#wait")));
		                if (time > 0) {
		                	    luckyclient.publicclass.LogUtil.APP.info("Action(Wait):线程等待"+time+"秒...");
		    					Thread.sleep(time * 1000);
		                }
		    			} catch (InterruptedException e) {
		    				// TODO Auto-generated catch block
		    				e.printStackTrace();
		    			}
				}else{
					luckyclient.publicclass.LogUtil.APP.error("使用等待关键字的参数不是整数，直接跳过此动作，请检查！");
				}
			}else if(actionorder.endsWith("#getjv")){
				String actionparams=actionorder.substring(0, actionorder.lastIndexOf("#getjv"));
				String key="";
				String index="1";
				if(actionparams.endsWith("]")&&actionparams.contains("[")){
					key=actionparams.substring(0,actionparams.lastIndexOf("["));
					index=actionparams.substring(actionparams.lastIndexOf("[")+1, actionparams.lastIndexOf("]"));
					testresult=SubString.getJsonValue(testresult, key, index);
				}else{
					key=actionparams;
					testresult=SubString.getJsonValue(testresult, key, index);
				}
				luckyclient.publicclass.LogUtil.APP.info("Action(getJV):获取JSON字符串指定Key的值是："+testresult);
			}else if(actionorder.endsWith("#subcentrestr")){
				String actionparams=actionorder.substring(0, actionorder.lastIndexOf("#subcentrestr"));
				String startstr="";
				String endstr="";
				if(actionparams.startsWith("[")&&actionparams.endsWith("]")){
					startstr=actionparams.substring(actionparams.indexOf("[")+1, actionparams.indexOf("]"));
					endstr=actionparams.substring(actionparams.lastIndexOf("[")+1, actionparams.lastIndexOf("]"));
					testresult=SubString.subCentreStr(testresult, startstr, endstr);
					luckyclient.publicclass.LogUtil.APP.info("Action(subCentreStr):截取测试结果指定开始及结束位置字符串："+testresult);
				}else{
					testresult="步骤动作：subCentreStr 必须是[\"开始字符\"][\"结束字符\"]#subCentreStr 格式，请检查您的步骤动作关键字:"+actionorder;
					luckyclient.publicclass.LogUtil.APP.error("步骤动作：subCentreStr 必须是[\"开始字符\"][\"结束字符\"]#subCentreStr 格式，请检查您的步骤动作关键字:"+actionorder);
				}
			}else if(actionorder.endsWith("#subcentrenum")){
				String actionparams=actionorder.substring(0, actionorder.lastIndexOf("#subcentrenum"));
				String startnum="";
				String endnum="";
				if(actionparams.startsWith("[")&&actionparams.endsWith("]")){
					startnum=actionparams.substring(actionparams.indexOf("[")+1, actionparams.indexOf("]"));
					endnum=actionparams.substring(actionparams.lastIndexOf("[")+1, actionparams.lastIndexOf("]"));
					testresult=SubString.subCentreNum(testresult, startnum, endnum);
					luckyclient.publicclass.LogUtil.APP.info("Action(subCentreNum):截取测试结果指定开始及结束位置字符串："+testresult);
				}else{
					testresult="步骤动作：subCentreNum 必须是[\"开始字符\"][\"结束字符\"]#subCentreNum 格式，请检查您的步骤动作关键字:"+actionorder;
					luckyclient.publicclass.LogUtil.APP.error("步骤动作：subCentreNum 必须是[\"开始位置(整数)\"][\"结束位置(整数)\"]#subCentreNum 格式，请检查您的步骤动作关键字:"+actionorder);
				}
			}else if(actionorder.endsWith("#substrrgex")){
				String actionparams=actionorder.substring(0, actionorder.lastIndexOf("#substrrgex"));
				String key="";
				String index="1";
				if(actionparams.endsWith("]")&&actionparams.contains("[")){
					key=actionparams.substring(0,actionparams.lastIndexOf("["));
					index=actionparams.substring(actionparams.lastIndexOf("[")+1, actionparams.lastIndexOf("]"));
					testresult=SubString.subStrRgex(testresult, key, index);
				}else{
					key=actionparams;
					testresult=SubString.subStrRgex(testresult, key, index);
				}
				luckyclient.publicclass.LogUtil.APP.info("Action(subStrRgex):获取JSON字符串指定Key的值是："+testresult);
			}else{
				testresult="未检索到对应动作关键字，直接跳过此动作，请检查关键字："+actionorder;
				luckyclient.publicclass.LogUtil.APP.error("未检索到对应动作关键字，直接跳过此动作，请检查关键字："+actionorder);
			}
	        return testresult;
		}catch(Exception e){
			testresult="处理步骤动作事件过程中出现异常，直接返回测试结果："+actionorder;
			luckyclient.publicclass.LogUtil.APP.error("处理步骤动作事件过程中出现异常，直接返回测试结果："+actionorder);
			return testresult;
		}
	}

}
