package luckyclient.execution.appium;

import java.io.File;
import java.util.Properties;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import luckyclient.utils.LogUtil;
import luckyclient.utils.config.AppiumConfig;

/**
 * 
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 Seagull
 * =================================================================
 * @author Seagull
 * @date 2019年8月8日
 */
public class AppiumService extends Thread{
	
	@Override
	public void run(){
		try{
			Properties properties = AppiumConfig.getConfiguration();
			File mainjsFile = new File(properties.getProperty("mainjsPath"));
			String ip=properties.getProperty("appiumsever");
			AppiumServiceBuilder builder =
	                new AppiumServiceBuilder().withArgument(GeneralServerFlag.SESSION_OVERRIDE)
	                        .withIPAddress(ip.split(":")[0].trim())
	                        .withAppiumJS(mainjsFile)
	                        .usingPort(Integer.parseInt(ip.split(":")[1].trim()));

			AppiumDriverLocalService service = AppiumDriverLocalService.buildService(builder);
	        service.start();
	        
	        if (!service.isRunning()){
	        	LogUtil.APP.warn("自动启动Appium服务失败，请检查！");
	        }else{
	        	LogUtil.APP.info("自动启动Appium服务成功，监听IP:{} 监听端口:{}",ip.split(":")[0].trim(),ip.split(":")[1].trim());
	        }
		}catch(Exception e){
			LogUtil.APP.error("自动启动Appium服务抛出异常，请检查！",e);
		}
	}

}
