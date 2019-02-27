package luckyclient.publicclass;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * ϵͳ���ò���
 * @author seagull
 *
 */
public class SysConfig {
	private static final Properties SYS_CONFIG = new Properties();
	private static final String SYS_CONFIG_FILE = "/sys_config.properties";
	static{
		try {
		    InputStream in = new BufferedInputStream(AppiumConfig.class.getResourceAsStream(SYS_CONFIG_FILE));
			SYS_CONFIG.load(new InputStreamReader(in, "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private SysConfig(){}
	public static Properties getConfiguration(){
		return SYS_CONFIG;
	}
}
