package luckyclient.execution.webdriver;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Augmenter;

import cn.hutool.core.util.BooleanUtil;
import luckyclient.utils.LogUtil;
import luckyclient.utils.config.SysConfig;
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
public class BaseWebDrive {

	/**
	 * 进测试结果进行截图
	 * @param driver 驱动
	 * @param imgname 图片名称
	 */
	public static void webScreenShot(WebDriver driver, String imgname) {
		String relativelyPath = RunService.APPLICATION_HOME;
		String pngpath=relativelyPath +File.separator+ "log"+File.separator+"ScreenShot" +File.separator+ imgname + ".png";

		// 对远程系统进行截图
		driver = new Augmenter().augment(driver);
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(scrFile, new File(pngpath));
		} catch (IOException e) {
			LogUtil.APP.error("截图操作失败，抛出异常请查看日志...", e);
		}
		scrFile.deleteOnExit();
		LogUtil.APP
				.info("已对当前界面进行截图操作，可通过用例执行界面的日志明细查看，也可以前往客户端上查看...【{}】",pngpath);
	}

	/**
	 * 在自动化过程中加入点击显示效果
	 * @param driver 驱动
	 * @param element 定位元素
	 * @author Seagull
	 * @date 2019年9月6日
	 */
    public static void highLightElement(WebDriver driver, WebElement element){
    	Properties properties = SysConfig.getConfiguration();
    	boolean highLight = BooleanUtil.toBoolean(properties.getProperty("webdriver.highlight"));

    	if(highLight){
            JavascriptExecutor js = (JavascriptExecutor) driver;
            /*调用js将传入参数的页面元素对象的背景颜色和边框颜色分别设定为黄色和红色*/
            js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, "background: yellow; border:2px solid red;");
    	}
    }

}
