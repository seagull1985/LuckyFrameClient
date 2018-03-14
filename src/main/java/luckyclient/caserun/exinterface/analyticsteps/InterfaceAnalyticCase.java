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
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 * @ClassName: AnalyticCase 
 * @Description: 解析单个用例中描述部分的脚本
 * @author： seagull
 * @date 2017年7月14日 上午9:29:40  
 * 
 */
public class InterfaceAnalyticCase{
	private static String splitFlag = "\\|";

	/**
	 */
	public static Map<String,String> analyticCaseStep(ProjectCase projectcase,ProjectCasesteps step,String taskid,LogOperation caselog){
		String time = "0";
		Map<String,String> params = new HashMap<String,String>(0);

		try {
	    String packagenage = step.getPath();
	    String functionname = step.getOperation();
	    String resultstr = step.getExpectedresult();
	    String action = step.getAction();
	    params.put("PackageName", packagenage.trim()); 
		params.put("FunctionName", functionname.trim());
		String stepParams = step.getParameters();
		String[] temp=stepParams.split(splitFlag,-1);
		for(int i=0;i<temp.length;i++){
            if("".equals(temp[i])){
				continue;
			}else{
				 //set第N个传入参数
				params.put("FunctionParams"+(i+1), temp[i]);  
			}
		}
		//set预期结果
		if(null==resultstr||"".equals(resultstr)){
			params.put("ExpectedResults", "");
		}else{
			params.put("ExpectedResults", subComment(resultstr));
		}
		//set后续操作
		if(null!=action&&action.toLowerCase().indexOf("*wait")>-1){
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
	
	public static String subComment(String htmlStr) throws InterruptedException{
		// 定义script的正则表达式
    	String regExscript = "<script[^>]*?>[\\s\\S]*?<\\/script>"; 
    	// 定义style的正则表达式
        String regExstyle = "<style[^>]*?>[\\s\\S]*?<\\/style>"; 
        // 定义HTML标签的正则表达式
        String regExhtml = "<[^>]+>";
        //定义空格回车换行符
        String regExspace = "\t|\r|\n";
        
        String scriptstr = null;
        if (htmlStr!=null) {
            Pattern pScript = Pattern.compile(regExscript, Pattern.CASE_INSENSITIVE);
            Matcher mScript = pScript.matcher(htmlStr);
            // 过滤script标签
            htmlStr = mScript.replaceAll(""); 
       
            Pattern pStyle = Pattern.compile(regExstyle, Pattern.CASE_INSENSITIVE);
            Matcher mStyle = pStyle.matcher(htmlStr);
            // 过滤style标签
            htmlStr = mStyle.replaceAll(""); 
       
            Pattern pHtml = Pattern.compile(regExhtml, Pattern.CASE_INSENSITIVE);
            Matcher mHtml = pHtml.matcher(htmlStr);
            // 过滤html标签
            htmlStr = mHtml.replaceAll(""); 
       
            Pattern pSpace = Pattern.compile(regExspace, Pattern.CASE_INSENSITIVE);
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
    

    public static void main(String[] args){
		// TODO Auto-generated method stub
	}
}
