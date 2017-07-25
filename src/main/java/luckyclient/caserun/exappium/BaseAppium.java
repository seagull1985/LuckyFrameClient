package luckyclient.caserun.exappium;

import io.appium.java_client.AppiumDriver;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.Augmenter;

public class BaseAppium {
	
	/**
	 * @param args
	 * @throws IOException
	 * ÊÖ»ú±¨´í½ØÍ¼
	 */
	public static void APPScreenShot(AppiumDriver driver,String workpath) throws IOException{		
		java.text.DateFormat timeformat = new java.text.SimpleDateFormat("MMdd-hhmmss");
        String time = timeformat.format(new Date())+".jpg";
		try {
            try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			File imageFile = ((TakesScreenshot) (new Augmenter().augment(driver))).getScreenshotAs(OutputType.FILE);
			File screenFile=new File(workpath+"\\images\\"+time);
			FileUtils.copyFile(imageFile,screenFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
