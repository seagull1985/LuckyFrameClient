package luckyclient.utils.config;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import luckyclient.utils.LogUtil;
import luckyclient.utils.proxy.PropertiesProxy;

/**
 * 初始化数据库驱动配置
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 Seagull
 * =================================================================
 * @author Seagull
 * @date 2020年2月17日
 */
public class DrivenConfig {
	private static final Properties SYS_CONFIG = new Properties();
	private static final String SYS_CONFIG_FILE = "/TestDriven/driven_config.properties";
	private static PropertiesProxy proxy=new PropertiesProxy();
	static{
		try {
		    InputStream in = new BufferedInputStream(DrivenConfig.class.getResourceAsStream(SYS_CONFIG_FILE));
			SYS_CONFIG.load(new InputStreamReader(in, StandardCharsets.UTF_8));
			proxy.setWapper(SYS_CONFIG);
		} catch (IOException e) {
			LogUtil.APP.error("读取测试驱动driven_config.properties配置文件出现异常，请检查！", e);
		}
	}
	private DrivenConfig(){}
	public static Properties getConfiguration(){
		return proxy;
	}
}
