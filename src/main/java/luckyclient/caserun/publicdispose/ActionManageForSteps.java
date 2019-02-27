package luckyclient.caserun.publicdispose;

import luckyclient.driven.SubString;

/**
 * �����ؼ��ִ���
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸� ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944 seagull
 * =================================================================
 * @author Seagull
 * @date 2019��1��15��
 */
public class ActionManageForSteps {
	/**
	 * ������������
	 * @param projectcase
	 * @param step
	 * @param taskid
	 * @param caselog
	 * @return
	 */
	public static String actionManage(String stepsaction,String testresult){
		luckyclient.publicclass.LogUtil.APP.info("���Խ���ǣ�" + testresult);
		luckyclient.publicclass.LogUtil.APP.info("���ڽ��뵽Action(����)����......ACTIONֵ��"+stepsaction);
		if(null==stepsaction||"".equals(stepsaction.trim())){
			luckyclient.publicclass.LogUtil.APP.info("Action(����)���账��......");
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
	 * �����ؼ���ִ��
	 * @param actionorder
	 * @param testresult
	 * @return
	 */
	private static String actionExecute(String actionorder,String testresult){
		try{
	        // �������¼�
			if(actionorder.endsWith("#wait")){
				if(ChangString.isInteger(actionorder.substring(0, actionorder.lastIndexOf("#wait")))){
		            try {
		                // ��ȡ�����ȴ�ʱ��
		                int time=Integer.parseInt(actionorder.substring(0, actionorder.lastIndexOf("#wait")));
		                if (time > 0) {
		                	    luckyclient.publicclass.LogUtil.APP.info("Action(Wait):�̵߳ȴ�"+time+"��...");
		    					Thread.sleep(time * 1000);
		                }
		    			} catch (InterruptedException e) {
		    				// TODO Auto-generated catch block
		    				e.printStackTrace();
		    			}
				}else{
					luckyclient.publicclass.LogUtil.APP.error("ʹ�õȴ��ؼ��ֵĲ�������������ֱ�������˶��������飡");
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
				luckyclient.publicclass.LogUtil.APP.info("Action(getJV):��ȡJSON�ַ���ָ��Key��ֵ�ǣ�"+testresult);
			}else if(actionorder.endsWith("#subcentrestr")){
				String actionparams=actionorder.substring(0, actionorder.lastIndexOf("#subcentrestr"));
				String startstr="";
				String endstr="";
				if(actionparams.startsWith("[")&&actionparams.endsWith("]")){
					startstr=actionparams.substring(actionparams.indexOf("[")+1, actionparams.indexOf("]"));
					endstr=actionparams.substring(actionparams.lastIndexOf("[")+1, actionparams.lastIndexOf("]"));
					testresult=SubString.subCentreStr(testresult, startstr, endstr);
					luckyclient.publicclass.LogUtil.APP.info("Action(subCentreStr):��ȡ���Խ��ָ����ʼ������λ���ַ�����"+testresult);
				}else{
					testresult="���趯����subCentreStr ������[\"��ʼ�ַ�\"][\"�����ַ�\"]#subCentreStr ��ʽ���������Ĳ��趯���ؼ���:"+actionorder;
					luckyclient.publicclass.LogUtil.APP.error("���趯����subCentreStr ������[\"��ʼ�ַ�\"][\"�����ַ�\"]#subCentreStr ��ʽ���������Ĳ��趯���ؼ���:"+actionorder);
				}
			}else if(actionorder.endsWith("#subcentrenum")){
				String actionparams=actionorder.substring(0, actionorder.lastIndexOf("#subcentrenum"));
				String startnum="";
				String endnum="";
				if(actionparams.startsWith("[")&&actionparams.endsWith("]")){
					startnum=actionparams.substring(actionparams.indexOf("[")+1, actionparams.indexOf("]"));
					endnum=actionparams.substring(actionparams.lastIndexOf("[")+1, actionparams.lastIndexOf("]"));
					testresult=SubString.subCentreNum(testresult, startnum, endnum);
					luckyclient.publicclass.LogUtil.APP.info("Action(subCentreNum):��ȡ���Խ��ָ����ʼ������λ���ַ�����"+testresult);
				}else{
					testresult="���趯����subCentreNum ������[\"��ʼ�ַ�\"][\"�����ַ�\"]#subCentreNum ��ʽ���������Ĳ��趯���ؼ���:"+actionorder;
					luckyclient.publicclass.LogUtil.APP.error("���趯����subCentreNum ������[\"��ʼλ��(����)\"][\"����λ��(����)\"]#subCentreNum ��ʽ���������Ĳ��趯���ؼ���:"+actionorder);
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
				luckyclient.publicclass.LogUtil.APP.info("Action(subStrRgex):��ȡJSON�ַ���ָ��Key��ֵ�ǣ�"+testresult);
			}else{
				testresult="δ��������Ӧ�����ؼ��֣�ֱ�������˶���������ؼ��֣�"+actionorder;
				luckyclient.publicclass.LogUtil.APP.error("δ��������Ӧ�����ؼ��֣�ֱ�������˶���������ؼ��֣�"+actionorder);
			}
	        return testresult;
		}catch(Exception e){
			testresult="�����趯���¼������г����쳣��ֱ�ӷ��ز��Խ����"+actionorder;
			luckyclient.publicclass.LogUtil.APP.error("�����趯���¼������г����쳣��ֱ�ӷ��ز��Խ����"+actionorder);
			return testresult;
		}
	}

}
