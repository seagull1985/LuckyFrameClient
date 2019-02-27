package luckyclient.caserun.exwebdriver.ex;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import luckyclient.dblog.LogOperation;
import luckyclient.planapi.entity.ProjectCase;
import luckyclient.planapi.entity.ProjectCasesteps;
/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸�
 * ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944  seagull1985
 * =================================================================
 * @ClassName: AnalyticCase 
 * @Description: ���������������������ֵĽű�
 * @author�� seagull
 * @date 2016��9��18�� 
 * 
 */
public class WebDriverAnalyticCase {
	//private static String splitFlag = "\\|";

	/**
	 * Web UI���͵Ĳ������
	 * @param projectcase
	 * @param step
	 * @param taskid
	 * @param caselog
	 * @return
	 * @author Seagull
	 * @date 2019��1��17��
	 */
	public static Map<String,String> analyticCaseStep(ProjectCase projectcase,ProjectCasesteps step,String taskid,LogOperation caselog){
		Map<String,String> params = new HashMap<String,String>(0);

		String resultstr = null;
		try {
		if(null!=step.getPath()&&step.getPath().indexOf("=")>-1){
			String property = step.getPath().substring(0, step.getPath().indexOf("="));
			String propertyValue = step.getPath().substring(step.getPath().indexOf("=")+1, step.getPath().length());
			//set����
			params.put("property", property.trim().toLowerCase());   
			//set����ֵ
			params.put("property_value", propertyValue.trim());  
			luckyclient.publicclass.LogUtil.APP.info("�������Խ��������property:"+property.trim()+";  property_value:"+propertyValue.trim());		
		}
		//set��������
		params.put("operation", step.getOperation().toLowerCase());   
		if(null!=step.getParameters()&&!"".equals(step.getParameters())){
			 //set����ֵ
			params.put("operation_value", step.getParameters());  
		}
		luckyclient.publicclass.LogUtil.APP.info("����������������operation:"+step.getOperation().toLowerCase()+";  operation_value:"+step.getParameters());
		 //��ȡԤ�ڽ���ַ���
		resultstr = step.getExpectedresult();  

		//setԤ�ڽ��
		if(null==resultstr||"".equals(resultstr)){
			params.put("ExpectedResults", "");
		}else if(null!=resultstr){
			String expectedResults = subComment(resultstr);

			//����check�ֶ�
			if(expectedResults.toLowerCase().startsWith("check(")){
				expectedResults=expectedResults.replace("Check(", "check(");
				params.put("checkproperty", expectedResults.substring(expectedResults.indexOf("check(")+6, expectedResults.indexOf("=")));
				params.put("checkproperty_value", expectedResults.substring(expectedResults.indexOf("=")+1, expectedResults.lastIndexOf(")")));
			}			
			params.put("ExpectedResults", expectedResults);
			luckyclient.publicclass.LogUtil.APP.info("Ԥ�ڽ��������ExpectedResults:"+expectedResults);
		}
		
		luckyclient.publicclass.LogUtil.APP.info("������ţ�"+projectcase.getSign()+" �����ţ�"+step.getStepnum()+" �����Զ�����������ű���ɣ�");
		if(null!=caselog){
		  caselog.caseLogDetail(taskid, projectcase.getSign(),"�����ţ�"+step.getStepnum()+" �����Զ�����������ű���ɣ�","info",String.valueOf(step.getStepnum()),"");
		}
		}catch(Exception e) {
			luckyclient.publicclass.LogUtil.APP.error("������ţ�"+projectcase.getSign()+" �����ţ�"+step.getStepnum()+" �����Զ�����������ű�����");
			if(null!=caselog){
			  caselog.caseLogDetail(taskid, projectcase.getSign(),"�����ţ�"+step.getStepnum()+" �����Զ�����������ű�����","error",String.valueOf(step.getStepnum()),"");
			}
			luckyclient.publicclass.LogUtil.APP.error(e,e);
			params.put("exception","������ţ�"+projectcase.getSign()+"|�����쳣,��������Ϊ�ջ��������ű�����");
			return params;
     }
		return params;
	}
	
	private static String subComment(String htmlStr) throws InterruptedException{
		// ����script��������ʽ
    	String regExScript = "<script[^>]*?>[\\s\\S]*?<\\/script>"; 
    	// ����style��������ʽ
        String regExStyle = "<style[^>]*?>[\\s\\S]*?<\\/style>"; 
        // ����HTML��ǩ��������ʽ
        String regExHtml = "<[^>]+>"; 
        //����ո�س����з�
        String regExSpace = "\t|\r|\n";
        
        String scriptstr = null;
        if (htmlStr!=null) {
            Pattern pScript = Pattern.compile(regExScript, Pattern.CASE_INSENSITIVE);
            Matcher mScript = pScript.matcher(htmlStr);
            // ����script��ǩ
            htmlStr = mScript.replaceAll(""); 
       
            Pattern pStyle = Pattern.compile(regExStyle, Pattern.CASE_INSENSITIVE);
            Matcher mStyle = pStyle.matcher(htmlStr);
            // ����style��ǩ
            htmlStr = mStyle.replaceAll(""); 
       
            Pattern pHtml = Pattern.compile(regExHtml, Pattern.CASE_INSENSITIVE);
            Matcher mHtml = pHtml.matcher(htmlStr);
            // ����html��ǩ
            htmlStr = mHtml.replaceAll(""); 
       
            Pattern pSpace = Pattern.compile(regExSpace, Pattern.CASE_INSENSITIVE);
            Matcher mSpace = pSpace.matcher(htmlStr);
            // ���˿ո�س���ǩ
            htmlStr = mSpace.replaceAll(""); 
            
        }
        if(htmlStr.indexOf("/*")>-1&&htmlStr.indexOf("*/")>-1){
    		String commentstr = htmlStr.substring(htmlStr.trim().indexOf("/*"),htmlStr.indexOf("*/")+2);
    		 //ȥע��
    		scriptstr = htmlStr.replace(commentstr, "");    
        }else{
        	scriptstr = htmlStr;
        }
        //ȥ���ַ���ǰ��Ŀո�
        scriptstr = trimInnerSpaceStr(scriptstr);  
        //�滻�ո�ת��
        scriptstr = scriptstr.replaceAll("&nbsp;", " "); 
        //ת��˫����
        scriptstr = scriptstr.replaceAll("&quot;", "\""); 
        //ת�嵥����
        scriptstr = scriptstr.replaceAll("&#39;", "\'");  
        //ת�����ӷ�
        scriptstr = scriptstr.replaceAll("&amp;", "&");  
        scriptstr = scriptstr.replaceAll("&lt;", "<");  
        scriptstr = scriptstr.replaceAll("&gt;", ">");  
        
		return scriptstr;
	}

	/***
     * ȥ���ַ���ǰ��Ŀո��м�Ŀո���
     * @param str
     * @return
     */
	public static String trimInnerSpaceStr(String str) {
		str = str.trim();
		while (str.startsWith(" ")) {
			str = str.substring(1, str.length()).trim();
		}
		while (str.startsWith("&nbsp;")) {
			str = str.substring(6, str.length()).trim();
		}
		while (str.endsWith(" ")) {
			str = str.substring(0, str.length() - 1).trim();
		}
		while (str.endsWith("&nbsp;")) {
			str = str.substring(0, str.length() - 6).trim();
		}
		return str;
	}

    public static void main(String[] args){
		// TODO Auto-generated method stub
	}
    
}
