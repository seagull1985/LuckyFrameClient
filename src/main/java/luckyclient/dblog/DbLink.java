package luckyclient.dblog;

import java.util.Properties;

import luckyclient.publicclass.DBOperation;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 * 
 * @author： seagull
 * @date 2017年12月1日 上午9:29:40
 * 
 */
public class DbLink {
	
	/**
	 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 此测试框架主要采用testlink做分层框架，负责数据驱动以及用例管理部分，有任何疑问欢迎联系作者讨论。
 * QQ:24163551 seagull1985
	 * =================================================================
	 * @ClassName: DbLogLink 
	 * @Description: 定义数据日志数据库链接地址
	 * @author： seagull
	 * @date 2015年4月20日 上午9:29:40  
	 * 
	 */
	public  static DBOperation dbLogLink(){
		Properties properties = luckyclient.publicclass.SysConfig.getConfiguration();
		String urlBase = "jdbc:mysql://"+properties.getProperty("mysql.db.ip")+":"+properties.getProperty("mysql.db.port")
		+"/"+properties.getProperty("mysql.db.dbname")+"?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false";
		String userNameBase = properties.getProperty("mysql.db.username");
		String passwordBase = properties.getProperty("mysql.db.userpwd");
		return new DBOperation(urlBase, userNameBase, passwordBase);
	}
	/**
	 * 任务执行类型： 0   任务调度模式    1   控制台模式
	 */
	public static int exetype;      

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
