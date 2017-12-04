package luckyclient.caserun.exwebdriver;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;

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
	 * @param args
	 * @throws IOException
	 * @throws IOException
	 */
	public static Boolean webScreenShot(WebDriver driver,String imgname) {
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
