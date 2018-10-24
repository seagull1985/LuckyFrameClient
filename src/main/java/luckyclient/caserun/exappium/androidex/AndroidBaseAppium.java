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
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸� ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944 seagull1985
 * =================================================================
 * 
 * @author�� seagull
 * 
 * @date 2017��12��1�� ����9:29:40
 * 
 */
public class AndroidBaseAppium {

	/**
	 * ��׿�ֻ������ͼ
	 * @param appium
	 * @param imagname
	 * @throws IOException
	 */
	public static void screenShot(AndroidDriver<AndroidElement> appium, String imagname){
		imagname = imagname + ".png";
		String relativelyPath = System.getProperty("user.dir");
		String pngpath=relativelyPath +File.separator+ "log"+File.separator+"ScreenShot" +File.separator+ imagname;
		
		try {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			File imageFile = ((TakesScreenshot) (new Augmenter().augment(appium))).getScreenshotAs(OutputType.FILE);
			File screenFile = new File(pngpath);
			FileUtils.copyFile(imageFile, screenFile);
			imageFile.deleteOnExit();
			luckyclient.publicclass.LogUtil.APP
			.info("�ѶԵ�ǰ������н�ͼ��������ͨ������ִ�н������־��ϸ�鿴��Ҳ����ǰ���ͻ����ϲ鿴...��" + pngpath + "��");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param args
	 * @throws IOException
	 * appium��֧���������� �ο���robotium����js��ʽΪԪ��ֱ������value������
	 * ����Selenium��Webdriverִ��js����ʵ����������
	 */
	public static void sendChinese(AndroidDriver<AndroidElement> appium, String preferences, String value) {
		org.openqa.selenium.JavascriptExecutor jse = (org.openqa.selenium.JavascriptExecutor) appium;
		jse.executeScript("document.getElementByName('" + preferences + "').value='" + value + "'");
	}

	/**
	 * @param args
	 *            js webview ֧��4.1��4.4
	 */
	public static void webViewSwipe(AndroidDriver<AndroidElement> appium, Double sX, Double sY, Double eX, Double eY, Double duration)
			throws Exception {
		JavascriptExecutor js;
		HashMap<String, Double> swipeObject;
		try {
			// ����
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
			// �ͷű���
		}

	}

	/**
	 * @param args
	 *            ���� ADBֱ�ӻ��� ֧��4.1��4.4
	 */
	public static void adbSwipe(AndroidDriver<AndroidElement> appium, Double sX, Double sY, Double eX, Double eY) throws Exception {
		int xLine;
		int yLine;
		int sX2;
		int sY2;
		int eX2;
		int eY2;
		try {
			// ����
			xLine = appium.manage().window().getSize().getWidth();
			yLine = appium.manage().window().getSize().getHeight();

			sX2 = (int) (xLine * sX);
			sY2 = (int) (yLine * sY);
			eX2 = (int) (xLine * eX);
			eY2 = (int) (yLine * eY);
			// logger.info("����11111111");
			Runtime.getRuntime()
					.exec("adb -s " + "Android" + " shell input swipe " + sX2 + " " + sY2 + " " + eX2 + " " + eY2);
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
			throw ex;
		} finally {
			// �ͷű���
		}

	}

	/**
	 * @param args
	 * ��Ļ����¼�
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
	 * ��סҳ�水��Ļ�������ϻ���(��ָ���£�ҳ������)
	 * @param driver
	 * @param second ����ʱ��
	 * @param num ��������
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
	 * ��סҳ�水��Ļ�������»���(��ָ���ϣ�ҳ������)
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
	 * ��סҳ�水��Ļ�������󻬶�(��ָ����ҳ���������)
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
	 * ��סҳ�水��Ļ�������һ���(��ָ���ң�ҳ������)
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
