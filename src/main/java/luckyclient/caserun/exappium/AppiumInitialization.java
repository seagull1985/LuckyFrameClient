package luckyclient.caserun.exappium;

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
	 * 
	 * @throws IOException
	 */
	public static AndroidDriver<AndroidElement> setAndroidAppium() throws IOException {
		AndroidDriver<AndroidElement> appium = null;
		Properties properties = luckyclient.publicclass.AppiumConfig.getConfiguration();
		DesiredCapabilities capabilities = new DesiredCapabilities();
		File directory = new File("");
		File app = new File(directory.getCanonicalPath() + "//" + properties.getProperty("appname"));
		capabilities.setCapability("app", app.getAbsolutePath());
		// 自动化测试服务
		capabilities.setCapability("automationName", properties.getProperty("automationName"));
		// 设备名称
		capabilities.setCapability("deviceName", properties.getProperty("deviceName"));
		// 系统版本
		capabilities.setCapability("platformVersion", properties.getProperty("platformVersion"));
		// 模拟器上的ip地址
		capabilities.setCapability("udid", properties.getProperty("udid"));
		// Android应用的包名
		capabilities.setCapability("appPackage", properties.getProperty("appPackage"));
		// 启动的Android Activity
		capabilities.setCapability("appActivity", properties.getProperty("appActivity"));
		// 支持中文输入，会自动安装Unicode输入
		capabilities.setCapability("unicodeKeyboard", properties.getProperty("unicodeKeyboard"));
		// 重置输入法到原有状态
		capabilities.setCapability("resetKeyboard", properties.getProperty("resetKeyboard"));
		// 不重新签名apk
		capabilities.setCapability("noSign", properties.getProperty("noSign"));
		// 等待超时没接收到命令关闭appium
		capabilities.setCapability("newCommandTimeout", properties.getProperty("newCommandTimeout"));
		appium = new AndroidDriver<AndroidElement>(
				new URL("http://" + properties.getProperty("appiumsever") + "/wd/hub"), capabilities);
		int waittime = Integer.valueOf(properties.getProperty("implicitlyWait"));
		appium.manage().timeouts().implicitlyWait(waittime, TimeUnit.SECONDS);
		return appium;
	}

	/**
	 * 初始化IOSAppium
	 * 
	 * @throws IOException
	 */
	public static IOSDriver<IOSElement> setIosAppium() throws IOException {
		IOSDriver<IOSElement> appium = null;
		Properties properties = luckyclient.publicclass.AppiumConfig.getConfiguration();
		DesiredCapabilities capabilities = new DesiredCapabilities();
		File directory = new File("");
		File app = new File(directory.getCanonicalPath() + "//" + properties.getProperty("appname"));
		capabilities.setCapability("app", app.getAbsolutePath());
		// 自动化测试服务
		capabilities.setCapability("automationName", properties.getProperty("automationName"));
		// 设备名称
		capabilities.setCapability("deviceName", properties.getProperty("deviceName"));
		// 系统版本
		capabilities.setCapability("platformVersion", properties.getProperty("platformVersion"));
		// 模拟器上的ip地址
		capabilities.setCapability("udid", properties.getProperty("udid"));
		// Android应用的包名
		capabilities.setCapability("appPackage", properties.getProperty("appPackage"));
		// 启动的Android Activity
		capabilities.setCapability("appActivity", properties.getProperty("appActivity"));
		// 支持中文输入，会自动安装Unicode输入
		capabilities.setCapability("unicodeKeyboard", properties.getProperty("unicodeKeyboard"));
		// 重置输入法到原有状态
		capabilities.setCapability("resetKeyboard", properties.getProperty("resetKeyboard"));
		// 不重新签名apk
		capabilities.setCapability("noSign", properties.getProperty("noSign"));
		// 等待超时没接收到命令关闭appium
		capabilities.setCapability("newCommandTimeout", properties.getProperty("newCommandTimeout"));
		appium = new IOSDriver<IOSElement>(new URL("http://" + properties.getProperty("appiumsever") + "/wd/hub"),
				capabilities);
		int waittime = Integer.valueOf(properties.getProperty("implicitlyWait"));
		appium.manage().timeouts().implicitlyWait(waittime, TimeUnit.SECONDS);
		return appium;
	}

	public static void main(String args[]) throws IOException, InterruptedException {

	}
}
