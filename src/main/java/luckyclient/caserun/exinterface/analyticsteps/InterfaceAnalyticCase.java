package luckyclient.caserun.exinterface.analyticsteps;

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
 * @date 2017��7��14�� ����9:29:40  
 * 
 */
public class InterfaceAnalyticCase{
	/**
	 * ������������
	 * @param projectcase
	 * @param step
	 * @param taskid
	 * @param caselog
	 * @return
	 */
	public static Map<String,String> analyticCaseStep(ProjectCase projectcase,ProjectCasesteps step,String taskid,LogOperation caselog){
		Map<String,String> params = new HashMap<String,String>(0);

		try {
	    String packagenage = step.getPath();
	    String functionname = step.getOperation();
	    String resultstr = step.getExpectedresult();
		params.put("Action", step.getAction());
	    params.put("PackageName", packagenage.trim()); 
		params.put("FunctionName", functionname.trim());
		String stepParams = replaceSpi(step.getParameters(),0);
		String[] temp=stepParams.split("\\|",-1);
		for(int i=0;i<temp.length;i++){
            if("".equals(temp[i])){
				continue;
			}if(" ".equals(temp[i])){
				 //��һ���ո��ʱ�򣬴�����ַ���
				params.put("FunctionParams"+(i+1), "");  
			}else{
				 //set��N���������
				params.put("FunctionParams"+(i+1), replaceSpi(temp[i],1));  
			}
		}
		//setԤ�ڽ��
		if(null==resultstr||"".equals(resultstr)){
			params.put("ExpectedResults", "");
		}else{
			params.put("ExpectedResults", subComment(resultstr));
		}
		luckyclient.publicclass.LogUtil.APP.info("������ţ�"+projectcase.getSign()+" �����ţ�"+step.getStepnum()+" �����Զ�����������ű���ɣ�");
		if(null!=caselog){
			caselog.caseLogDetail(taskid, projectcase.getSign(),"�����ţ�"+step.getStepnum()+" �����Զ�����������ű���ɣ�","info",String.valueOf(step.getStepnum()),"");
		}
		}catch(Exception e) {
			luckyclient.publicclass.LogUtil.ERROR.error("������ţ�"+projectcase.getSign()+" �����ţ�"+step.getStepnum()+" �����Զ�����������ű�����");
			if(null!=caselog){
			caselog.caseLogDetail(taskid, projectcase.getSign(),"�����ţ�"+step.getStepnum()+" �����Զ�����������ű�����","error",String.valueOf(step.getStepnum()),"");
			}
			luckyclient.publicclass.LogUtil.ERROR.error(e,e);
			params.put("exception","������ţ�"+projectcase.getSign()+"|�����쳣,��������Ϊ�ջ��������ű�����");
			return params;
     }
 	 return params;

	}
	
	public static String subComment(String htmlStr) throws InterruptedException{
		// ����script��������ʽ
    	String regExscript = "<script[^>]*?>[\\s\\S]*?<\\/script>"; 
    	// ����style��������ʽ
        String regExstyle = "<style[^>]*?>[\\s\\S]*?<\\/style>"; 
        // ����HTML��ǩ��������ʽ
        String regExhtml = "<[^>]+>";
        //����ո�س����з�
        String regExspace = "\t|\r|\n";
        
        String scriptstr = null;
        if (htmlStr!=null) {
            Pattern pScript = Pattern.compile(regExscript, Pattern.CASE_INSENSITIVE);
            Matcher mScript = pScript.matcher(htmlStr);
            // ����script��ǩ
            htmlStr = mScript.replaceAll(""); 
       
            Pattern pStyle = Pattern.compile(regExstyle, Pattern.CASE_INSENSITIVE);
            Matcher mStyle = pStyle.matcher(htmlStr);
            // ����style��ǩ
            htmlStr = mStyle.replaceAll(""); 
       
            Pattern pHtml = Pattern.compile(regExhtml, Pattern.CASE_INSENSITIVE);
            Matcher mHtml = pHtml.matcher(htmlStr);
            // ����html��ǩ
            htmlStr = mHtml.replaceAll(""); 
       
            Pattern pSpace = Pattern.compile(regExspace, Pattern.CASE_INSENSITIVE);
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
    public static String trimInnerSpaceStr(String str){
        str = str.trim();
        while(str.startsWith(" ")){
        str = str.substring(1,str.length()).trim();
        }
        while(str.startsWith("&nbsp;")){
        str = str.substring(6,str.length()).trim();
        }
        while(str.endsWith(" ")){
        str = str.substring(0,str.length()-1).trim();
        }
        while(str.endsWith("&nbsp;")){
            str = str.substring(0,str.length()-6).trim();
            }
        return str;
    } 
    
    /**
     * �����������д���|�ַ���ʱ���ڽ���\\|����ת��
     * @param str
     * @param flag
     * @return
     */
    private static String replaceSpi(String str,int flag){
    	String replacestr="&brvbar_rep;";
    	String result=str;
    	if(str.contains("\\\\|")&&flag==0){
    		result=str.replace("\\\\|", replacestr);
    	}
    	if(str.contains(replacestr)&&flag==1){
    		result=str.replace(replacestr,"|");
    	}
    	return result;
    }
    
    public static void main(String[] args){
		// TODO Auto-generated method stub
    	
	}
}
