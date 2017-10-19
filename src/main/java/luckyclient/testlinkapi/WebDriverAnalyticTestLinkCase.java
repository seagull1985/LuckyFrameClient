package luckyclient.testlinkapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestCaseStep;
import luckyclient.dblog.LogOperation;
/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 此测试框架主要采用testlink做分层框架，负责数据驱动以及用例管理部分，有任何疑问欢迎联系作者讨论。
 * QQ:24163551 seagull1985
 * =================================================================
 * @ClassName: AnalyticCase 
 * @Description: 解析单个用例中描述部分的脚本
 * @author： seagull
 * @date 2016年9月18日 
 * 
 */
public class WebDriverAnalyticTestLinkCase {
	private static String splitFlag = "\\|";

	/**
	 * @param args
	 */
	@SuppressWarnings("finally")
	public static Map<String,String> AnalyticCaseStep(TestCase testcase,Integer ordersteps,String tastid,LogOperation caselog){
		String time = "0";
		Map<String,String> params = new HashMap<String,String>();

		String resultstr = null;
		try {	
		List<TestCaseStep> testcasesteps = (List<TestCaseStep>) testcase.getSteps();
		String stepsstr = testcasesteps.get(ordersteps-1).getActions();    //获取actions字符串
		String scriptstr = SubComment(stepsstr);
		if(scriptstr.substring(scriptstr.length()-6, scriptstr.length()).toLowerCase().indexOf("*wait;")>-1){                    //添加步骤之间等待时间
        	time = scriptstr.substring(scriptstr.lastIndexOf("|")+1,scriptstr.toLowerCase().lastIndexOf("*wait;"));
        	scriptstr = scriptstr.substring(0, scriptstr.lastIndexOf("|")+1);
        }
		resultstr = testcasesteps.get(ordersteps-1).getExpectedResults();   //获取预期结果字符串
		String temp[]=scriptstr.split(splitFlag,-1);
		for(int i=0;i<temp.length;i++){
			if(i==0&&temp[i].indexOf("=")>-1&&(temp.length>2||!"".equals(temp[1]))){
				String property = temp[i].substring(0, temp[i].indexOf("="));
				String property_value = temp[i].substring(temp[i].indexOf("=")+1, temp[i].length());

				params.put("property", property.trim().toLowerCase());   //set属性
				params.put("property_value", property_value.trim());   //set属性值
				luckyclient.publicclass.LogUtil.APP.info("对象属性解析结果：property:"+property.trim()+";  property_value:"+property_value.trim());
			}else if("".equals(temp[i])){
				continue;
			}else{
				String operation = null;
				String operation_value = null;
				if(temp[i].indexOf("(")>-1&&temp[i].indexOf(")")>-1){
					operation = temp[i].substring(0, temp[i].indexOf("("));
					operation_value = temp[i].substring(temp[i].indexOf("(")+1, temp[i].lastIndexOf(")"));
				}else{
					operation = temp[i];
				}
				params.put("operation", operation.toLowerCase());   //set操作方法
				params.put("operation_value", operation_value);   //set操作值
				luckyclient.publicclass.LogUtil.APP.info("对象操作解析结果：operation:"+operation+";  operation_value:"+operation_value);
			}
		}
		//set预期结果
		if(resultstr.equals("")){
			params.put("ExpectedResults", "");
		}else{
			String ExpectedResults = SubComment(resultstr);

			//处理check字段
			if(ExpectedResults.indexOf("check(")>-1){
				params.put("checkproperty", ExpectedResults.substring(ExpectedResults.indexOf("check(")+6, ExpectedResults.indexOf("=")));
				params.put("checkproperty_value", ExpectedResults.substring(ExpectedResults.indexOf("=")+1, ExpectedResults.indexOf(")")));
			}			
			params.put("ExpectedResults", ExpectedResults);
			luckyclient.publicclass.LogUtil.APP.info("预期结果解析：ExpectedResults:"+ExpectedResults);
		}
		params.put("StepWait", time);
		luckyclient.publicclass.LogUtil.APP.info("用例编号："+testcase.getFullExternalId()+" 步骤编号："+ordersteps+" 解析自动化用例步骤脚本完成！");
		if(null!=caselog){
			caselog.CaseLogDetail(tastid, testcase.getFullExternalId(),"步骤编号："+ordersteps+" 解析自动化用例步骤脚本完成！","info",String.valueOf(ordersteps),"");
		}
		}catch(Exception e) {
			luckyclient.publicclass.LogUtil.ERROR.error("用例编号："+testcase.getFullExternalId()+" 步骤编号："+ordersteps+" 解析自动化用例步骤脚本出错！");
			if(null!=caselog){
			  caselog.CaseLogDetail(tastid, testcase.getFullExternalId(),"步骤编号："+ordersteps+" 解析自动化用例步骤脚本出错！","error",String.valueOf(ordersteps),"");
			}
			luckyclient.publicclass.LogUtil.ERROR.error(e,e);
			params.put("exception","用例编号："+testcase.getFullExternalId()+"|解析异常,用例步骤为空或是用例脚本错误！");
     }finally{
 		return params;
     }
	}
	
	private static String SubComment(String htmlStr) throws InterruptedException{
    	String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
        String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
        String regEx_space = "\t|\r|\n";//定义空格回车换行符
        
        String scriptstr = null;
        if (htmlStr!=null) {
            Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            Matcher m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); // 过滤script标签
       
            Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            Matcher m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); // 过滤style标签
       
            Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            Matcher m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); // 过滤html标签
       
            Pattern p_space = Pattern.compile(regEx_space, Pattern.CASE_INSENSITIVE);
            Matcher m_space = p_space.matcher(htmlStr);
            htmlStr = m_space.replaceAll(""); // 过滤空格回车标签
            
        }
        if(htmlStr.indexOf("/*")>-1&&htmlStr.indexOf("*/")>-1){
    		String commentstr = htmlStr.substring(htmlStr.trim().indexOf("/*"),htmlStr.indexOf("*/")+2);
    		scriptstr = htmlStr.replace(commentstr, "");     //去注释
        }else{
        	scriptstr = htmlStr;
        }
        
        scriptstr = trimInnerSpaceStr(scriptstr);          //去掉字符串前后的空格
        scriptstr = scriptstr.replaceAll("&nbsp;", " ");  //替换空格转义
        scriptstr = scriptstr.replaceAll("&quot;", "\""); //转义双引号
        scriptstr = scriptstr.replaceAll("&#39;", "\'");  //转义单引号
        scriptstr = scriptstr.replaceAll("&amp;", "&");  //转义链接符
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
/*		Thread.sleep(20000);
		System.out.println(test.stopServer());*/

	}
    
}
