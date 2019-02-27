package luckyclient.caserun.exappium;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;

public class AppiumService extends Thread{
	
	public void run(){
		try{
			Properties properties = luckyclient.publicclass.AppiumConfig.getConfiguration();
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
	        	luckyclient.publicclass.LogUtil.APP.error("�Զ�����Appium����ʧ�ܣ����飡");
	        }else{
	        	luckyclient.publicclass.LogUtil.APP.info("�Զ�����Appium����ɹ�������IP:"+ip.split(":")[0].trim()+" �����˿�:"+ip.split(":")[1].trim());
	        }
		}catch(Exception e){
			luckyclient.publicclass.LogUtil.APP.error("�Զ�����Appium�����׳��쳣�����飡",e);
		}
	}

}
