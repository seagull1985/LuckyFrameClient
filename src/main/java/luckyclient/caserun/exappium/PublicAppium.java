package luckyclient.caserun.exappium;


import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class PublicAppium extends BaseAppium{
	//初始化对象
	public static AppiumDriver setUpAppium(String apkname,String version,String apppackage,String appactivity){
		AppiumDriver appium = null;
		 File app=new File(System.getProperty("user.dir")+"//"+apkname);    //"MobilePayment.apk"
         DesiredCapabilities capabilities = new DesiredCapabilities();
         capabilities.setCapability("deviceName","Android");
         capabilities.setCapability(CapabilityType.BROWSER_NAME, "");
         capabilities.setCapability(CapabilityType.VERSION, version);    //"4.2.2"
         capabilities.setCapability(CapabilityType.PLATFORM, "Android");
         capabilities.setCapability("unicodeKeyboard", "True");
         capabilities.setCapability("resetKeyboard", "True");
         capabilities.setCapability("app", app.getAbsolutePath());
         capabilities.setCapability("app-package", apppackage);   //"com.ysepay.mobileportal.activity"
         capabilities.setCapability("app-activity", appactivity);  //"com.ysepay.mobileportal.IndexActivity"         
         try {
       	 appium = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		appium.manage().timeouts().implicitlyWait(300, TimeUnit.SECONDS);
		return appium;
	}

}
