package luckyclient.caserun.exappium;

import io.appium.java_client.AppiumDriver;

import java.io.IOException;

public class InputWord {
	/**
	 * @param args
	 * @throws IOException
	 * appium不支持中文输入 参考了robotium的以js方式为元素直接设置value的做法
	 * 利用Selenium中Webdriver执行js方法实现中文输入
	 */
    public static void SendChinese(AppiumDriver appium,String preferences,String value){
        org.openqa.selenium.JavascriptExecutor jse = (org.openqa.selenium.JavascriptExecutor) appium;
        jse.executeScript("document.getElementByName('"+preferences+"').value='"+value+"'");
    }
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
