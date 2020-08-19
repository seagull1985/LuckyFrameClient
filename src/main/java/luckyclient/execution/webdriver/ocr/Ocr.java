package luckyclient.execution.webdriver.ocr;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Augmenter;
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
public class Ocr {
	/**
	 * 默认读取工程根目录下的文件
	 */
	private static final String readtextpath = RunService.APPLICATION_HOME+"\\CAPTCHA.txt";
	/**
	 * 默认把截图放在工程根目录
	 */
	private static final String screenshotpath = RunService.APPLICATION_HOME+"\\CAPTCHA.png";
	/**
	 * 批处理文件路径
	 */
	private static final String cmdpath = RunService.APPLICATION_HOME;

	/**
	 * 读取生成的TXT文件中的验证码
	 * @return 返回结果
	 */
	private static String readTextFile() {
		String lineTxt;
		try {
			String encoding = "GBK";
			File file = new File(readtextpath); 
			 // 判断文件是否存在
			if (file.isFile() && file.exists()) {
				// 考虑到编码格式
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				while ((lineTxt = bufferedReader.readLine()) != null) {
					  return lineTxt;
				}
				read.close();
			} else {
				return "找不到指定的文件";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "读取文件内容出错";
		}
		return null;
	}

	/**
	 * 截取验证码位置的图片
	 * @param driver webDriver驱动
	 * @param element 对象定位
	 */
	private static void screenShotForElement(WebDriver driver, WebElement element){
		driver = new Augmenter().augment(driver);
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        
		try {
			Point p = element.getLocation();
			int width = element.getSize().getWidth();
			int height = element.getSize().getHeight();
			Rectangle rect = new Rectangle(width, height);
			BufferedImage img = ImageIO.read(scrFile);
			BufferedImage dest = img.getSubimage(p.getX()-9, p.getY()+1, rect.width+2, rect.height+2);
			ImageIO.write(dest, "png", scrFile);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			FileUtils.copyFile(scrFile, new File(screenshotpath));   
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static String getCAPTCHA(WebDriver driver, WebElement element) {
		String code;
		screenShotForElement(driver, element);
		Runtime run = Runtime.getRuntime();
		try {
			//默认把截图放在C盘根目录
			String cmdname = "handlingCAPTCHA.bat";
			run.exec("cmd.exe /k start " + cmdname, null, new File(cmdpath));
			Thread.sleep(1000);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		code = readTextFile();
/*		if (new File(readtextpath).exists()) {
			new File(readtextpath).delete();
		}
		if (new File(screenshotpath).exists()) {
			new File(screenshotpath).delete();
		}*/
		return code;
	}

}