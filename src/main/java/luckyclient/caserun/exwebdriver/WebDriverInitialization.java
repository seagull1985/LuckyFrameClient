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
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸�
 * ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944  seagull1985
 * =================================================================
 * 
 * @author�� seagull
 * @date 2017��12��1�� ����9:29:40
 * 
 */
public class WebDriverInitialization{
	private static final String os=System.getProperty("os.name").toLowerCase();
	/**
	 * ��ʼ��WebDriver
	 * @param drivertype
	 * @return
	 * @throws WebDriverException
	 * @throws IOException
	 */
	public static WebDriver setWebDriverForTask(int drivertype) throws WebDriverException,IOException{
		// ����Ϊ��
		File directory = new File("");
		String drivenpath=directory.getCanonicalPath()+File.separator+"BrowserDriven"+File.separator;
		WebDriver webDriver = null;
		luckyclient.publicclass.LogUtil.APP.info("׼����ʼ��WebDriver����...��鵽��ǰ����ϵͳ�ǣ�"+os);
		if(drivertype==0){
			if(os.startsWith("win")){
				System.setProperty("webdriver.ie.driver",drivenpath+"IEDriverServer.exe");
				webDriver = new InternetExplorerDriver();
			}else{
				luckyclient.publicclass.LogUtil.ERROR.info("��ǰ����ϵͳ�޷�����IE�������Web UI���ԣ���ѡ�������ǹȸ��������");
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
				luckyclient.publicclass.LogUtil.ERROR.info("��ǰ����ϵͳ�޷�����Edge�������Web UI���ԣ���ѡ�������ǹȸ��������");
			}
		}else{
			luckyclient.publicclass.LogUtil.ERROR.info("��������ͱ�ʶ��"+drivertype);
			luckyclient.publicclass.LogUtil.ERROR.info("��ȡ������������ͱ�ʶδ���壬Ĭ��IE���������ִ��....");
			System.setProperty("webdriver.ie.driver",drivenpath+"IEDriverServer.exe");
			webDriver = new InternetExplorerDriver();
		}
		
		webDriver.manage().window().maximize();
		//����ҳ��������ʱ��30��
		webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		//����Ԫ�س������ʱ��30��  
		webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		
        return webDriver;
	}

	/**
	 * ��ʼ��WebDriver
	 * @return
	 * @throws IOException
	 */
	public static WebDriver setWebDriverForLocal() throws IOException{
		File directory = new File("");
		String drivenpath=directory.getCanonicalPath()+File.separator+"BrowserDriven"+File.separator;
		System.setProperty("webdriver.ie.driver",drivenpath+"IEDriverServer.exe");
		WebDriver webDriver = new InternetExplorerDriver();
		webDriver.manage().window().maximize();
		//����ҳ��������ʱ��30��
		webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		//����Ԫ�س������ʱ��30��  
		webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);  
        return webDriver;
	}
	
	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub

	}

}
