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
 * 初始化Appium配置参数
 * @author seagull
 *
 */
public class AppiumConfig {
	private static final Properties SYS_CONFIG = new Properties();
	private static final String SYS_CONFIG_FILE = "/appium_config.properties";
	private static PropertiesProxy proxy=new PropertiesProxy();
	static{
		try {
		    InputStream in = new BufferedInputStream(AppiumConfig.class.getResourceAsStream(SYS_CONFIG_FILE));
			SYS_CONFIG.load(new InputStreamReader(in, StandardCharsets.UTF_8));
			proxy.setWapper(SYS_CONFIG);
		} catch (IOException e) {
			LogUtil.APP.error("读取移动端appium_config.properties配置文件出现异常，请检查！", e);
		}
	}
	private AppiumConfig(){}
	public static Properties getConfiguration(){
		return proxy;
	}
}
