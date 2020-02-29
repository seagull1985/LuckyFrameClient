package luckyclient.utils.config;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 系统配置参数
 * @author seagull
 *
 */
public class SysConfig {
	private static final Properties SYS_CONFIG = new Properties();
	private static final String SYS_CONFIG_FILE = "/sys_config.properties";
	static{
		try {
		    InputStream in = new BufferedInputStream(SysConfig.class.getResourceAsStream(SYS_CONFIG_FILE));
			SYS_CONFIG.load(new InputStreamReader(in, "GBK"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private SysConfig(){}
	public static Properties getConfiguration(){
		return SYS_CONFIG;
	}
}
