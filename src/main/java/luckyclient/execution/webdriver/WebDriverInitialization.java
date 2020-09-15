package luckyclient.execution.webdriver;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;

import luckyclient.utils.LogUtil;
import springboot.RunService;


/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 * 
 * @author： seagull
 * @date 2017年12月1日 上午9:29:40
 * 
 */
public class WebDriverInitialization{
	private static final String OS=System.getProperty("os.name").toLowerCase();
	/**
	 * 初始化WebDriver
	 * @param drivertype 浏览器类型
	 * @return 返回初始化结果
	 * @throws WebDriverException 驱动抛出异常
	 * @throws IOException 读取配置文件异常
	 */
	public static WebDriver setWebDriverForTask(int drivertype) throws WebDriverException,IOException{
		// 参数为空
		String drivenpath= RunService.APPLICATION_HOME + File.separator+"BrowserDriven"+File.separator;
		WebDriver webDriver = null;
		LogUtil.APP.info("准备初始化WebDriver对象...检查到当前操作系统是:{}",OS);
		if(drivertype==0){
			if(OS.startsWith("win")){
				System.setProperty("webdriver.ie.driver",drivenpath+"IEDriverServer.exe");
				webDriver = new InternetExplorerDriver();
			}else{
				LogUtil.APP.warn("当前操作系统无法进行IE浏览器的Web UI测试，请选择火狐或是谷歌浏览器！");
			}		
		}else if(drivertype==1){
			FirefoxOptions options = new FirefoxOptions();
			if(OS.startsWith("win")){
				System.setProperty("webdriver.gecko.driver",drivenpath+"geckodriver.exe");
			}else if(OS.contains("mac")){
				options.addArguments("start-maximized");
				System.setProperty("webdriver.gecko.driver",drivenpath+"geckodriver_mac");
			}else{
				LogUtil.APP.info("检测到当前系统环境是Linux,默认使用headless方式运行Firefox浏览器的Web UI自动化...");
				//无界面参数
				options.setHeadless(true);
				//禁用沙盒
				options.addArguments("no-sandbox");
				options.addArguments("start-maximized");
				System.setProperty("webdriver.gecko.driver",drivenpath+"geckodriver_linux64");
			}
			webDriver = new FirefoxDriver(options);
		}else if(drivertype==2){
			ChromeOptions options = new ChromeOptions();
			if(OS.startsWith("win")){
				System.setProperty("webdriver.chrome.driver",drivenpath+"chromedriver.exe");
			}else if(OS.contains("mac")){
				options.addArguments("start-maximized");
				System.setProperty("webdriver.chrome.driver",drivenpath+"chromedriver_mac");
			}else{
				LogUtil.APP.info("检测到当前系统环境是Linux,默认使用headless方式运行Chrome浏览器的Web UI自动化...");
				//无界面参数
				options.setHeadless(true);
				//禁用沙盒
				options.addArguments("no-sandbox");
				options.addArguments("start-maximized");
				System.setProperty("webdriver.chrome.driver",drivenpath+"chromedriver_linux64");
			}			
			webDriver = new ChromeDriver(options);
		}else if(drivertype==3){
			if(OS.startsWith("win")){
				System.setProperty("webdriver.edge.driver",drivenpath+"msedgedriver.exe");
				webDriver = new EdgeDriver();
			}else if(OS.contains("mac")){
				System.setProperty("webdriver.edge.driver",drivenpath+"msedgedriver_mac");
				webDriver = new EdgeDriver();
			}else{
				LogUtil.APP.warn("当前操作系统无法进行Edge浏览器的Web UI测试，请选择火狐或是谷歌浏览器！");
			}
		}else{
			LogUtil.APP.warn("浏览器类型标识:{}，获取到的浏览器类型标识未定义，默认IE浏览器进行执行....",drivertype);
			System.setProperty("webdriver.ie.driver",drivenpath+"IEDriverServer.exe");
			webDriver = new InternetExplorerDriver();
		}
		
		//解决webdriver在unix环境中，最大化会出现异常的bug，unix最大化在options中单独设置
		if(OS.startsWith("win")){
			assert webDriver != null;
			webDriver.manage().window().maximize();
		}

		//设置页面加载最大时长30秒
		assert webDriver != null;
		webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		//设置元素出现最大时长30秒  
		webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		
        return webDriver;
	}

	/**
	 * 初始化WebDriver
	 * @return 返回初始化结果
	 * @throws IOException 读取配置文件异常
	 */
	public static WebDriver setWebDriverForLocal() throws IOException{
		File directory = new File("");
		String drivenpath=directory.getCanonicalPath()+File.separator+"BrowserDriven"+File.separator;
		System.setProperty("webdriver.ie.driver",drivenpath+"IEDriverServer.exe");
		WebDriver webDriver = new InternetExplorerDriver();
		webDriver.manage().window().maximize();
		//设置页面加载最大时长30秒
		webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		//设置元素出现最大时长30秒  
		webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);  
        return webDriver;
	}

}
