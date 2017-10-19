package luckyclient.caserun.exwebdriver;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;

public class BaseWebDrive {

	/**
	 * @param args
	 * @throws IOException
	 * @throws IOException
	 */
	public static Boolean WebScreenShot(WebDriver driver,String imgname) {
		Boolean result = false;
		String relativelyPath = System.getProperty("user.dir");

		// 对远程系统进行截图
		driver = new Augmenter().augment(driver);
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(scrFile, new File(relativelyPath + "\\log\\ScreenShot\\" + imgname + ".png"));
		} catch (IOException e) {
			luckyclient.publicclass.LogUtil.APP.error("截图操作失败，抛出异常请查看日志...", e);
			e.printStackTrace();
		}
		luckyclient.publicclass.LogUtil.APP
				.info("已对当前界面进行截图操作，请前往服务器上查看...【" + relativelyPath + "\\log\\ScreenShot\\" + imgname + ".png】");
		return result;
	}

}
