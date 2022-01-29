package luckyclient.execution.appium;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 seagull1985
 * =================================================================
 * 
 * @author： seagull
 * 
 * @date 2017年12月1日 上午9:29:40
 * 
 */
public class AppiumInitialization {
	/**
	 * 初始化AndroidAppium
	 * @param properties 配置文件对象
	 * @return 返回安卓appium对象
	 * @throws IOException 抛异常
	 */
	public static AndroidDriver<AndroidElement> setAndroidAppium(Properties properties) throws IOException {
		AndroidDriver<AndroidElement> appium;
		DesiredCapabilities capabilities = new DesiredCapabilities();
//		File directory = new File("");
//		File app = new File(directory.getCanonicalPath() + File.separator + properties.getProperty("appname"));
//		capabilities.setCapability("app", app.getAbsolutePath());
		// 自动化测试服务
		capabilities.setCapability("automationName", properties.getProperty("automationName"));
		// 设备名称
		capabilities.setCapability("deviceName", properties.getProperty("deviceName"));
		// 平台类型
		capabilities.setCapability("platformName", properties.getProperty("platformName"));
		// 系统版本
		capabilities.setCapability("platformVersion", properties.getProperty("platformVersion"));
		// 模拟器上的ip地址
		capabilities.setCapability("udid", properties.getProperty("udid"));
		// Android应用的包名
		capabilities.setCapability("appPackage", properties.getProperty("appPackage"));
		// 启动的Android Activity
		capabilities.setCapability("appActivity", properties.getProperty("appActivity"));
		// 支持中文输入，会自动安装Unicode输入
		capabilities.setCapability("unicodeKeyboard", Boolean.valueOf(properties.getProperty("unicodeKeyboard")));
		// 重置输入法到原有状态
		capabilities.setCapability("resetKeyboard", Boolean.valueOf(properties.getProperty("resetKeyboard")));
		// 不重新签名apk
		capabilities.setCapability("noSign", Boolean.valueOf(properties.getProperty("noSign")));
		// 是否避免重新安装APP
		capabilities.setCapability("noReset", Boolean.valueOf(properties.getProperty("noReset")));
		// 等待超时没接收到命令关闭appium
		capabilities.setCapability("newCommandTimeout", properties.getProperty("newCommandTimeout"));
		String url="http://" + properties.getProperty("appiumsever") + "/wd/hub";
		appium = new AndroidDriver<>(new URL(url), capabilities);
		int waittime = Integer.parseInt(properties.getProperty("implicitlyWait"));
		appium.manage().timeouts().implicitlyWait(waittime, TimeUnit.SECONDS);
		return appium;
	}

	/**
	 * 初始化IOSAppium
	 * @param properties 配置文件对象
	 * @return 返回IOS appium对象
	 * @throws IOException 抛出IO异常
	 */
	public static IOSDriver<IOSElement> setIosAppium(Properties properties) throws IOException {
		IOSDriver<IOSElement> appium;
		DesiredCapabilities capabilities = new DesiredCapabilities();
		File directory = new File("");
		File app = new File(directory.getCanonicalPath() + File.separator + properties.getProperty("appname"));
		capabilities.setCapability("app", app.getAbsolutePath());
		// 自动化测试服务
		capabilities.setCapability("automationName", properties.getProperty("automationName"));
		// 设备名称
		capabilities.setCapability("deviceName", properties.getProperty("deviceName"));
		// 平台类型
		capabilities.setCapability("platformName", properties.getProperty("platformName"));
		// 系统版本
		capabilities.setCapability("platformVersion", properties.getProperty("platformVersion"));
		// 模拟器上的ip地址
		capabilities.setCapability("udid", properties.getProperty("udid"));
		// 支持中文输入，会自动安装Unicode输入
		capabilities.setCapability("unicodeKeyboard", Boolean.valueOf(properties.getProperty("unicodeKeyboard")));
		// 重置输入法到原有状态
		capabilities.setCapability("resetKeyboard", Boolean.valueOf(properties.getProperty("resetKeyboard")));
		// 不重新签名apk
		capabilities.setCapability("noSign", Boolean.valueOf(properties.getProperty("noSign")));
		// 是否避免重新安装APP
		capabilities.setCapability("noReset", Boolean.valueOf(properties.getProperty("noReset")));
		// 等待超时没接收到命令关闭appium
		capabilities.setCapability("newCommandTimeout", properties.getProperty("newCommandTimeout"));
		String url="http://" + properties.getProperty("appiumsever") + "/wd/hub";
		appium = new IOSDriver<>(new URL(url), capabilities);
		int waittime = Integer.parseInt(properties.getProperty("implicitlyWait"));
		appium.manage().timeouts().implicitlyWait(waittime, TimeUnit.SECONDS);
		return appium;
	}
	
}
