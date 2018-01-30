package luckyclient.publicclass;

import java.io.IOException;
import java.util.Properties;

/**
 * 初始化Appium配置参数
 * @author seagull
 *
 */
public class AppiumConfig {
	private static final Properties SYS_CONFIG = new Properties();
	private static final String SYS_CONFIG_FILE = "/appium_config.properties";
	static{
		try {
			SYS_CONFIG.load(AppiumConfig.class.getResourceAsStream(SYS_CONFIG_FILE));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private AppiumConfig(){}
	public static Properties getConfiguration(){
		return SYS_CONFIG;
	}
}
