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
	private static final String os=System.getProperty("os.name").toLowerCase();
	/**
	 * 初始化WebDriver
	 * @param drivertype
	 * @return
	 * @throws WebDriverException
	 * @throws IOException
	 */
	public static WebDriver setWebDriverForTask(int drivertype) throws WebDriverException,IOException{
		// 参数为空
		File directory = new File("");
		String drivenpath=directory.getCanonicalPath()+File.separator+"BrowserDriven"+File.separator;
		WebDriver webDriver = null;
		luckyclient.publicclass.LogUtil.APP.info("准备初始化WebDriver对象...检查到当前操作系统是："+os);
		if(drivertype==0){
			if(os.startsWith("win")){
				System.setProperty("webdriver.ie.driver",drivenpath+"IEDriverServer.exe");
				webDriver = new InternetExplorerDriver();
			}else{
				luckyclient.publicclass.LogUtil.ERROR.info("当前操作系统无法进行IE浏览器的Web UI测试，请选择火狐或是谷歌浏览器！");
			}		
		}else if(drivertype==1){
			if(os.startsWith("win")){
				System.setProperty("webdriver.gecko.driver",drivenpath+"geckodriver.exe");
			}else if(os.indexOf("mac")>=0){
				System.setProperty("webdriver.gecko.driver",drivenpath+"geckodriver_mac");
			}else{
				System.setProperty("webdriver.gecko.driver",drivenpath+"geckodriver_linux64");
			}
			webDriver = new FirefoxDriver();
		}else if(drivertype==2){
			if(os.startsWith("win")){
				System.setProperty("webdriver.chrome.driver",drivenpath+"chromedriver.exe");
			}else if(os.indexOf("mac")>=0){
				System.setProperty("webdriver.gecko.driver",drivenpath+"chromedriver_mac");
			}else{
				System.setProperty("webdriver.gecko.driver",drivenpath+"chromedriver_linux64");
			}			
			webDriver = new ChromeDriver();
		}else if(drivertype==3){
			if(os.startsWith("win")){
				System.setProperty("webdriver.edge.driver",drivenpath+"MicrosoftWebDriver.exe");
				webDriver = new EdgeDriver();
			}else{
				luckyclient.publicclass.LogUtil.ERROR.info("当前操作系统无法进行Edge浏览器的Web UI测试，请选择火狐或是谷歌浏览器！");
			}
		}else{
			luckyclient.publicclass.LogUtil.ERROR.info("浏览器类型标识："+drivertype);
			luckyclient.publicclass.LogUtil.ERROR.info("获取到的浏览器类型标识未定义，默认IE浏览器进行执行....");
			System.setProperty("webdriver.ie.driver",drivenpath+"IEDriverServer.exe");
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
	
	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub

	}

}
