package luckyclient.caserun.exwebdriver;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

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
	/**
	 * @param args
	 * 初始化WebDriver
	 * @throws IOException 
	 */
	public static WebDriver setWebDriverForTask(int drivertype) throws WebDriverException,IOException{
		// 参数为空
		File directory = new File("");
/*		System.setProperty("webdriver.ie.driver",directory.getCanonicalPath()+"\\IEDriverServer.exe");
        DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
        ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
        WebDriver webDriver = new InternetExplorerDriver(ieCapabilities);*/
		WebDriver webDriver = null;

		if(drivertype==0){
			System.setProperty("webdriver.ie.driver",directory.getCanonicalPath()+"\\IEDriverServer.exe");
			webDriver = new InternetExplorerDriver();
		}else if(drivertype==1){
			System.setProperty("webdriver.gecko.driver",directory.getCanonicalPath()+"\\geckodriver.exe");
			webDriver = new FirefoxDriver();
		}else if(drivertype==2){
			System.setProperty("webdriver.chrome.driver",directory.getCanonicalPath()+"\\chromedriver.exe");
			webDriver = new ChromeDriver();
		}else if(drivertype==3){
			System.setProperty("webdriver.edge.driver",directory.getCanonicalPath()+"\\MicrosoftWebDriver.exe");
			webDriver = new EdgeDriver();
		}else{
			System.setProperty("webdriver.ie.driver",directory.getCanonicalPath()+"\\IEDriverServer.exe");
			webDriver = new InternetExplorerDriver();
		}
		
		webDriver.manage().window().maximize();
		//设置页面加载最大时长30秒
		webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		//设置元素出现最大时长30秒  
		webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		
        return webDriver;
	}

	/**
	 * 初始化WebDriver
	 * @return
	 * @throws IOException
	 */
	public static WebDriver setWebDriverForLocal() throws IOException{
		File directory = new File("");
		System.setProperty("webdriver.ie.driver",directory.getCanonicalPath()+"\\IEDriverServer.exe");
		WebDriver webDriver = new InternetExplorerDriver();
		webDriver.manage().window().maximize();
		//设置页面加载最大时长30秒
		webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		//设置元素出现最大时长30秒  
		webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);  
        return webDriver;
	}
	
	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub

	}

}
