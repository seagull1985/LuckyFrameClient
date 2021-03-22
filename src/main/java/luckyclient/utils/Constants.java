package luckyclient.utils;

/**
 * 通用常量定义
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 Seagull
 * =================================================================
 * @author Seagull
 * @date 2019年10月22日
 */
public class Constants {	
	/******************预期结果常量定义*************************
	 * 预期结果赋值符---赋值单条用例作用域变量
	 */	
    public static final String ASSIGNMENT_SIGN = "$=";
	/**
	 * 预期结果赋值符---赋值测试任务作用域变量
	 */	
    public static final String ASSIGNMENT_GLOBALSIGN = "$A=";
	/**
	 * 预期结果匹配符---模糊匹配
	 */	
    public static final String FUZZY_MATCHING_SIGN = "%=";
	/**
	 * 预期结果匹配符---正则匹配
	 */	
    public static final String REGULAR_MATCHING_SIGN = "~=";
	/**
	 * 预期结果匹配符---json匹配
	 */	
    public static final String JSONPATH_SIGN = "$JP#";
	/**
	 * 语法链接符
	 */
	public static final String  SYMLINK = ">>";
	/**
	 * 匹配预期结果失败跳转处理
	 */
	public static final String IFFAIL_JUMP = "fail"+SYMLINK;
    
	/************************HTTP 返回结果头域 响应码分隔符定义***************************
	 * HTTP测试返回结果 是否接收头域
	 */	
    public static final String RESPONSE_HEAD = "RESPONSE_HEAD:【";
	/**
	 * HTTP测试返回结果 是否接收响应码
	 */	
    public static final String RESPONSE_CODE = "RESPONSE_CODE:【";    
	/**
	 * HTTP测试返回结果 尾部链接符
	 */	
    public static final String RESPONSE_END = "】 ";
}
