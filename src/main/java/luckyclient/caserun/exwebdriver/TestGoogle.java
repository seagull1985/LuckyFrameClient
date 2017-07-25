package luckyclient.caserun.exwebdriver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class TestGoogle {

	public static void main(String[] args) throws InterruptedException, IOException {
		// TODO Auto-generated method stub
		WebDriver driver = WebDriverInitialization.setWebDriverForLocal();
		driver.get("http://10.213.23.35:8080/ysuser_manager/login.do?method=login");
		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		Thread.sleep(20000);
		driver.findElement(By.xpath(".//*[@id='tt1']/li[1]/ul/li[2]/div/span[4]/a/span")).click();
		WebElement we = driver.findElement(By.xpath(".//*[@src='/ysuser_manager/login.do?method=geturl&limitid=2014020511&homepath=http:']"));
		driver.switchTo().frame(we);
//		driver.switchTo().frame("mainForm");
		WebElement element1 = driver.findElement(By.xpath(".//*[@id='mercId']"));
		element1.sendKeys("m00000000000329");
		WebElement element2 = driver.findElement(By.xpath("html/body/table[1]/tbody/tr/td[2]"));
		String aaa = element2.getText();
		String aaaa = element2.getTagName();
		element2.getLocation();
/*		driver.findElement(By.xpath("html/body/div[1]/div/span[2]/select")).click();
		Select select = new Select(driver.findElement(By.xpath("html/body/div[1]/div/span[2]/select")));
		List<WebElement> we= select.getAllSelectedOptions();
		select.selectByIndex(2);*/
	}

}
