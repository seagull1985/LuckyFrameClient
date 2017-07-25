package luckyclient.caserun.exwebdriver;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import luckyclient.dblog.LogOperation;


public class WebDriverInitialization{
	/**
	 * @param args
	 * @throws IOException 
	 */
	//初始化WebDriver
	public static WebDriver setWebDriverForTask(String taskid,int drivertype) throws IOException{
		File directory = new File("");// 参数为空
/*		System.setProperty("webdriver.ie.driver",directory.getCanonicalPath()+"\\IEDriverServer.exe");
        DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
        ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
        WebDriver webDriver = new InternetExplorerDriver(ieCapabilities);*/
		WebDriver webDriver = null;
		if(drivertype==0){
			System.setProperty("webdriver.ie.driver",directory.getCanonicalPath()+"\\IEDriverServer.exe");
			DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
		    ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
			webDriver = new InternetExplorerDriver();
		}else if(drivertype==1){
			System.setProperty("webdriver.gecko.driver",directory.getCanonicalPath()+"\\geckodriver.exe");
			DesiredCapabilities capabilities = DesiredCapabilities.firefox();
			capabilities.setCapability("marionette", true);
			webDriver = new FirefoxDriver(capabilities);
		}else if(drivertype==2){
			System.setProperty("webdriver.chrome.driver",directory.getCanonicalPath()+"\\chromedriver.exe");
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			webDriver = new ChromeDriver(capabilities);
		}else if(drivertype==3){
			System.setProperty("webdriver.edge.driver",directory.getCanonicalPath()+"\\MicrosoftWebDriver.exe");
			webDriver = new EdgeDriver();
		}else{
			System.setProperty("webdriver.ie.driver",directory.getCanonicalPath()+"\\IEDriverServer.exe");
			DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
		    ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
			webDriver = new InternetExplorerDriver();
		}
		
		webDriver.manage().window().maximize();
		//设置页面加载最大时长30秒
		webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		//设置元素出现最大时长30秒  
		webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);  
        return webDriver;
	}

	//初始化WebDriver
	public static WebDriver setWebDriverForLocal() throws IOException{
		File directory = new File("");
		System.setProperty("webdriver.ie.driver",directory.getCanonicalPath()+"\\IEDriverServer.exe");
		DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
	    ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
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
