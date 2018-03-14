package luckyclient.caserun.exappium;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import luckyclient.dblog.LogOperation;
import luckyclient.planapi.entity.ProjectCase;
import luckyclient.planapi.entity.ProjectCasesteps;
/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 * @ClassName: AnalyticCase 
 * @Description: 解析单个用例中描述部分的脚本
 * @author： seagull
 * @date 2016年9月18日 
 * 
 */
public class AppDriverAnalyticCase {
	//private static String splitFlag = "\\|";

	/**
	 * @param args
	 */
	public static Map<String,String> analyticCaseStep(ProjectCase projectcase,ProjectCasesteps step,String taskid,LogOperation caselog){
		String time = "0";
		Map<String,String> params = new HashMap<String,String>(0);

		String resultstr = null;
		try {
		if(null!=step.getPath()&&step.getPath().indexOf("=")>-1){
			String property = step.getPath().substring(0, step.getPath().indexOf("="));
			String propertyValue = step.getPath().substring(step.getPath().indexOf("=")+1, step.getPath().length());
			//set属性
			params.put("property", property.trim().toLowerCase());   
			//set属性值
			params.put("property_value", propertyValue.trim());  
			luckyclient.publicclass.LogUtil.APP.info("对象属性解析结果：property:"+property.trim()+";  property_value:"+propertyValue.trim());		
		}
		//set操作方法
		params.put("operation", step.getOperation().toLowerCase());   
		if(null!=step.getParameters()&&!"".equals(step.getParameters())){
			 //set属性值
			params.put("operation_value", step.getParameters());  
		}
		luckyclient.publicclass.LogUtil.APP.info("对象操作解析结果：operation:"+step.getOperation().toLowerCase()+";  operation_value:"+step.getParameters());
		 //获取预期结果字符串
		resultstr = step.getExpectedresult();  

		//set预期结果
		if(null==resultstr||"".equals(resultstr)){
			params.put("ExpectedResults", "");
		}else if(null!=resultstr){
			String expectedResults = subComment(resultstr);

			//处理check字段
			if(expectedResults.startsWith("check(")){
				params.put("checkproperty", expectedResults.substring(expectedResults.indexOf("check(")+6, expectedResults.indexOf("=")));
				params.put("checkproperty_value", expectedResults.substring(expectedResults.indexOf("=")+1, expectedResults.lastIndexOf(")")));
			}			
			params.put("ExpectedResults", expectedResults);
			luckyclient.publicclass.LogUtil.APP.info("预期结果解析：ExpectedResults:"+expectedResults);
		}
		
		//set wait时间
		 //添加步骤之间等待时间
		if(null!=step.getAction()&&step.getAction().toLowerCase().indexOf("*wait")>-1){                   
			String action=step.getAction();
			time=action.substring(0, action.toLowerCase().lastIndexOf("*wait"));
        }
		
		params.put("StepWait", time);
		luckyclient.publicclass.LogUtil.APP.info("用例编号："+projectcase.getSign()+" 步骤编号："+step.getStepnum()+" 解析自动化用例步骤脚本完成！");
		if(null!=caselog){
		  caselog.caseLogDetail(taskid, projectcase.getSign(),"步骤编号："+step.getStepnum()+" 解析自动化用例步骤脚本完成！","info",String.valueOf(step.getStepnum()),"");
		}
		}catch(Exception e) {
			luckyclient.publicclass.LogUtil.ERROR.error("用例编号："+projectcase.getSign()+" 步骤编号："+step.getStepnum()+" 解析自动化用例步骤脚本出错！");
			if(null!=caselog){
			  caselog.caseLogDetail(taskid, projectcase.getSign(),"步骤编号："+step.getStepnum()+" 解析自动化用例步骤脚本出错！","error",String.valueOf(step.getStepnum()),"");
			}
			luckyclient.publicclass.LogUtil.ERROR.error(e,e);
			params.put("exception","用例编号："+projectcase.getSign()+"|解析异常,用例步骤为空或是用例脚本错误！");
			return params;
     }
		return params;
	}
	
	private static String subComment(String htmlStr) throws InterruptedException{
		// 定义script的正则表达式
    	String regExScript = "<script[^>]*?>[\\s\\S]*?<\\/script>"; 
    	// 定义style的正则表达式
        String regExStyle = "<style[^>]*?>[\\s\\S]*?<\\/style>"; 
        // 定义HTML标签的正则表达式
        String regExHtml = "<[^>]+>"; 
        //定义空格回车换行符
        String regExSpace = "\t|\r|\n";
        
        String scriptstr = null;
        if (htmlStr!=null) {
            Pattern pScript = Pattern.compile(regExScript, Pattern.CASE_INSENSITIVE);
            Matcher mScript = pScript.matcher(htmlStr);
            // 过滤script标签
            htmlStr = mScript.replaceAll(""); 
       
            Pattern pStyle = Pattern.compile(regExStyle, Pattern.CASE_INSENSITIVE);
            Matcher mStyle = pStyle.matcher(htmlStr);
            // 过滤style标签
            htmlStr = mStyle.replaceAll(""); 
       
            Pattern pHtml = Pattern.compile(regExHtml, Pattern.CASE_INSENSITIVE);
            Matcher mHtml = pHtml.matcher(htmlStr);
            // 过滤html标签
            htmlStr = mHtml.replaceAll(""); 
       
            Pattern pSpace = Pattern.compile(regExSpace, Pattern.CASE_INSENSITIVE);
            Matcher mSpace = pSpace.matcher(htmlStr);
            // 过滤空格回车标签
            htmlStr = mSpace.replaceAll(""); 
            
        }
        if(htmlStr.indexOf("/*")>-1&&htmlStr.indexOf("*/")>-1){
    		String commentstr = htmlStr.substring(htmlStr.trim().indexOf("/*"),htmlStr.indexOf("*/")+2);
    		 //去注释
    		scriptstr = htmlStr.replace(commentstr, "");    
        }else{
        	scriptstr = htmlStr;
        }
        //去掉字符串前后的空格
        scriptstr = trimInnerSpaceStr(scriptstr);  
        //替换空格转义
        scriptstr = scriptstr.replaceAll("&nbsp;", " "); 
        //转义双引号
        scriptstr = scriptstr.replaceAll("&quot;", "\""); 
        //转义单引号
        scriptstr = scriptstr.replaceAll("&#39;", "\'");  
        //转义链接符
        scriptstr = scriptstr.replaceAll("&amp;", "&");  
        scriptstr = scriptstr.replaceAll("&lt;", "<");  
        scriptstr = scriptstr.replaceAll("&gt;", ">");  
        
		return scriptstr;
	}

	/***
     * 去掉字符串前后的空格，中间的空格保留
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
