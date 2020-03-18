package luckyclient.driven;

import java.util.Properties;

import luckyclient.utils.DbOperation;
import luckyclient.utils.config.DrivenConfig;

/**
 * 提供数据库查询操作的默认测试驱动
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 Seagull
 * =================================================================
 * @author Seagull
 * @date 2020年2月17日
 */
public class DbDriven {

	/**
	 * 执行SQL语句
	 * @param sql 执行SQL语句
	 * @return 返回执行结果条数才及提示
	 */
	public String executeSql(String sql) {
		Properties properties = DrivenConfig.getConfiguration();
		String url = properties.getProperty("db.url");
		String username = properties.getProperty("db.username");
		String password = properties.getProperty("db.password");
		DbOperation db=new DbOperation(url,username,password);
		return db.executeSql(sql);
	}

	/**
	 * 查询SQL语句
	 * @param sql 查询SQL
	 * @return 返回查询结果
	 * @throws Exception 异常信息
	 */
	public String executeQuery(String sql) throws Exception{
		Properties properties = DrivenConfig.getConfiguration();
		String url = properties.getProperty("db.url");
		String username = properties.getProperty("db.username");
		String password = properties.getProperty("db.password");
		DbOperation db=new DbOperation(url,username,password);
		return db.executeQuery(sql);
	}

}
