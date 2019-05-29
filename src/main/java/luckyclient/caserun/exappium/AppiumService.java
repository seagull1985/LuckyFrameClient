package luckyclient.caserun.exappium;

import java.io.File;
import java.util.Properties;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import luckyclient.publicclass.AppiumConfig;
import luckyclient.publicclass.LogUtil;

public class AppiumService extends Thread{
	
	public void run(){
		try{
			Properties properties = AppiumConfig.getConfiguration();
			File mainjsFile = new File(properties.getProperty("mainjsPath"));
			String ip=properties.getProperty("appiumsever");
			AppiumServiceBuilder builder =
	                new AppiumServiceBuilder().withArgument(GeneralServerFlag.SESSION_OVERRIDE)
	                        .withIPAddress(ip.split(":")[0].trim())
	                        .withAppiumJS(mainjsFile)
	                        .usingPort(Integer.valueOf(ip.split(":")[1].trim()));

			AppiumDriverLocalService service = AppiumDriverLocalService.buildService(builder);
	        service.start();
	        
	        if (service == null || !service.isRunning()){
	        	LogUtil.APP.warn("自动启动Appium服务失败，请检查！");
	        }else{
	        	LogUtil.APP.info("自动启动Appium服务成功，监听IP:"+ip.split(":")[0].trim()+" 监听端口:"+ip.split(":")[1].trim());
	        }
		}catch(Exception e){
			LogUtil.APP.error("自动启动Appium服务抛出异常，请检查！",e);
		}
	}

}
