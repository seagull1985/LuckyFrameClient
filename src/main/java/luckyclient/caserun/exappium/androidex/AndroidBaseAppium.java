package luckyclient.caserun.exappium.androidex;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.Augmenter;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.android.AndroidTouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 seagull1985
 * =================================================================
 * 
 * @author： seagull
 * 
 * @date 2017年12月1日 上午9:29:40
 * 
 */
public class AndroidBaseAppium {

	/**
	 * @param args
	 * @throws IOException
	 * 安卓手机报错截图
	 */
	public static void screenShot(AndroidDriver<AndroidElement> appium, String imagname) throws IOException {
		imagname = imagname + ".png";
		String relativelyPath = System.getProperty("user.dir");
		try {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			File imageFile = ((TakesScreenshot) (new Augmenter().augment(appium))).getScreenshotAs(OutputType.FILE);
			File screenFile = new File(relativelyPath + "\\log\\ScreenShot\\" + imagname);
			FileUtils.copyFile(imageFile, screenFile);
			imageFile.deleteOnExit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param args
	 * @throws IOException
	 * appium不支持中文输入 参考了robotium的以js方式为元素直接设置value的做法
	 * 利用Selenium中Webdriver执行js方法实现中文输入
	 */
	public static void sendChinese(AndroidDriver<AndroidElement> appium, String preferences, String value) {
		org.openqa.selenium.JavascriptExecutor jse = (org.openqa.selenium.JavascriptExecutor) appium;
		jse.executeScript("document.getElementByName('" + preferences + "').value='" + value + "'");
	}

	/**
	 * @param args
	 *            js webview 支持4.1～4.4
	 */
	public static void webViewSwipe(AndroidDriver<AndroidElement> appium, Double sX, Double sY, Double eX, Double eY, Double duration)
			throws Exception {
		JavascriptExecutor js;
		HashMap<String, Double> swipeObject;
		try {
			// 滑动
			js = (JavascriptExecutor) appium;
			swipeObject = new HashMap<String, Double>(5);
			swipeObject.put("startX", sX);
			swipeObject.put("startY", sY);
			swipeObject.put("endX", eX);
			swipeObject.put("endY", eY);
			swipeObject.put("duration", duration);
			js.executeScript("mobile: swipe", swipeObject);
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
			throw ex;
		} finally {
			// 释放变量
		}

	}

	/**
	 * @param args
	 *            调用 ADB直接滑动 支持4.1～4.4
	 */
	public static void adbSwipe(AndroidDriver<AndroidElement> appium, Double sX, Double sY, Double eX, Double eY) throws Exception {
		int xLine;
		int yLine;
		int sX2;
		int sY2;
		int eX2;
		int eY2;
		try {
			// 滑动
			xLine = appium.manage().window().getSize().getWidth();
			yLine = appium.manage().window().getSize().getHeight();

			sX2 = (int) (xLine * sX);
			sY2 = (int) (yLine * sY);
			eX2 = (int) (xLine * eX);
			eY2 = (int) (yLine * eY);
			// logger.info("滑动11111111");
			Runtime.getRuntime()
					.exec("adb -s " + "Android" + " shell input swipe " + sX2 + " " + sY2 + " " + eX2 + " " + eY2);
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
			throw ex;
		} finally {
			// 释放变量
		}

	}

	/**
	 * @param args
	 * 屏幕点击事件
	 */
	public static void clickScreenForJs(AndroidDriver<AndroidElement> drivers, int x, int y, int duration) {
		JavascriptExecutor js = (JavascriptExecutor) drivers;
		HashMap<String, Integer> tapObject = new HashMap<String, Integer>(3);
		tapObject.put("x", x);
		tapObject.put("y", y);
		tapObject.put("duration", duration);
		js.executeScript("mobile: tap", tapObject);
	}
	
	/**
	 * 拖住页面按屏幕比例向上滑动(手指向下，页面向上)
	 * @param driver
	 * @param second 持续时间
	 * @param num 滚动次数
	 */
	public static void swipePageUp(AndroidDriver<AndroidElement> driver, Double second, int num) {
		int nanos = (int) (second * 1000);
		Duration duration = Duration.ofNanos(nanos);
		int width = driver.manage().window().getSize().width;
		int height = driver.manage().window().getSize().height;
		AndroidTouchAction action = new AndroidTouchAction(driver);
		
		for (int i = 0; i <= num; i++) {
			action.press(PointOption.point(width / 2, height / 4)).waitAction(WaitOptions.waitOptions(duration))
					.moveTo(PointOption.point(width / 2, height / 2)).release().perform();
		}
	}

	/**
	 * 拖住页面按屏幕比例向下滑动(手指向上，页面向下)
	 * @param driver
	 * @param second
	 * @param num
	 */
	public static void swipePageDown(AndroidDriver<AndroidElement> driver,Double second,int num){
		int nanos = (int) (second * 1000);
		Duration duration = Duration.ofNanos(nanos);
		int width = driver.manage().window().getSize().width;
		int height = driver.manage().window().getSize().height;
		AndroidTouchAction action = new AndroidTouchAction(driver);
		for (int i = 0; i <= num; i++) {
			action.press(PointOption.point(width / 2, height / 2)).waitAction(WaitOptions.waitOptions(duration))
					.moveTo(PointOption.point(width / 2, height / 4)).release().perform();
		}
	}

	/**
	 * 拖住页面按屏幕比例向左滑动(手指向左，页面向左滚动)
	 * @param driver
	 * @param second
	 * @param num
	 */
	public static void swipePageLeft(AndroidDriver<AndroidElement> driver, Double second, int num) {
		int nanos = (int) (second * 1000);
		Duration duration = Duration.ofNanos(nanos);
		int width = driver.manage().window().getSize().width;
		int height = driver.manage().window().getSize().height;
		AndroidTouchAction action = new AndroidTouchAction(driver);
		for (int i = 0; i <= num; i++) {
			action.press(PointOption.point(width - 10, height / 2)).waitAction(WaitOptions.waitOptions(duration))
					.moveTo(PointOption.point(10, height / 2)).release().perform();
		}
	}

	/**
	 * 拖住页面按屏幕比例向右滑动(手指向右，页面向右)
	 * @param driver
	 * @param second
	 * @param num
	 */
	public static void swipePageRight(AndroidDriver<AndroidElement> driver, Double second, int num) {
		int nanos = (int) (second * 1000);
		Duration duration = Duration.ofNanos(nanos);
		int width = driver.manage().window().getSize().width;
		int height = driver.manage().window().getSize().height;
		AndroidTouchAction action = new AndroidTouchAction(driver);
		for (int i = 0; i <= num; i++) {
			action.press(PointOption.point(10, height / 2)).waitAction(WaitOptions.waitOptions(duration))
					.moveTo(PointOption.point(width - 10, height / 2)).release().perform();
		}
	}
     
}
