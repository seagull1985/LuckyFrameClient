package luckyclient.caserun.exinterface.analyticsteps;

import luckyclient.publicclass.ChangString;

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
			luckyclient.publicclass.LogUtil.APP.info("Action(动作)处理无需处理......");
			return testresult;
		}
		stepsaction=stepsaction.toLowerCase().trim();
		String[] temp=stepsaction.split("\\|",-1);
		for(String actionorder:temp){
			testresult=actionExecute(actionorder,testresult);
		}
		return testresult;
	}
	
	private static String actionExecute(String actionorder,String testresult){
        // 处理动作事件
		if(actionorder.endsWith("*wait")){
			if(ChangString.isInteger(actionorder.substring(0, actionorder.lastIndexOf("*wait")))){
	            try {
	                // 获取步骤间等待时间
	                int time=Integer.parseInt(actionorder.substring(0, actionorder.lastIndexOf("*wait")));
	                if (time > 0) {
	    					Thread.sleep(time * 1000);
	                }
	    			} catch (InterruptedException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}
			}else{
				luckyclient.publicclass.LogUtil.APP.error("等待不是整数，直接跳过此动作，请检查！");
			}
		}
        return testresult;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
