package luckyclient.caserun.exwebdriver;

import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import luckyclient.caserun.exwebdriver.ocr.Ocr;


public class EncapsulateOperation {

	public static String SelectOperation(WebElement we,String operation,String operation_value) throws Exception{
		String result = "";
		// 下拉框对象处理
		Select select = new Select(we);

		// 处理下拉框事件
		switch (operation) {
		case "selectbyvisibletext":
			select.selectByVisibleText(operation_value);
			luckyclient.publicclass.LogUtil.APP.info("下拉框对象通过VisibleText属性选择...【VisibleText属性值:" + operation_value + "】");
			break;
		case "selectbyvalue":
			select.selectByValue(operation_value);
			luckyclient.publicclass.LogUtil.APP.info("下拉框对象通过Value属性选择...【Value属性值:" + operation_value + "】");
			break;
		case "selectbyindex":
			select.selectByIndex(Integer.valueOf(operation_value));
			luckyclient.publicclass.LogUtil.APP.info("下拉框对象通过Index属性选择...【Index属性值:" + operation_value + "】");
			break;
		case "isselect":
			result = "获取到的值是【"+we.isSelected()+"】";
			luckyclient.publicclass.LogUtil.APP.info("判断对象是否已经被选择...【结果值:" + we.isSelected() + "】");
			break;
		default:
			break;
		}
		return result;
	}
	
	public static String GetOperation(WebDriver wd,WebElement we,String operation) throws Exception{
		String result = "";
		// 获取对象处理
		switch (operation) {
		case "gettext":
			result = "获取到的值是【"+we.getText()+"】";
			luckyclient.publicclass.LogUtil.APP.info("getText获取对象text属性...【text属性值:" + result + "】");
			break; // 获取输入框内容
		case "gettagname":
			result = "获取到的值是【"+we.getTagName()+"】";
			luckyclient.publicclass.LogUtil.APP.info("getTagName获取对象tagname属性...【tagname属性值:" + result + "】");
			break;
		case "getcaptcha":
			result = "获取到的值是【"+Ocr.getCAPTCHA(wd, we)+"】";
			luckyclient.publicclass.LogUtil.APP.info("getcaptcha获取验证码...【验证码值:" + result + "】");
			break;
		default:
			break;
		}
		return result;
	}
	
	public static String ActionWeOperation(WebDriver wd,WebElement we,String operation,String operation_value,String property,String property_value) throws Exception{
		String result = "";
		Actions action = new Actions(wd);
		// action处理
		switch (operation) {
		case "mouselkclick":  //鼠标左键点击
			action.click(we).perform();
			result = "mouselkclick鼠标左键点击对象...【对象定位属性:"+property+"; 定位属性值:"+property_value+"】";
			luckyclient.publicclass.LogUtil.APP.info("mouselkclick鼠标左键点击对象...【对象定位属性:"+property+"; 定位属性值:"+property_value+"】");
			break;
		case "mouserkclick":
			action.contextClick(we).perform();
			result = "mouserkclick鼠标右键点击对象...【对象定位属性:"+property+"; 定位属性值:"+property_value+"】";
			luckyclient.publicclass.LogUtil.APP.info("mouserkclick鼠标右键点击对象...【对象定位属性:"+property+"; 定位属性值:"+property_value+"】");
			break;
		case "mousedclick":
			action.doubleClick(we).perform();
			result = "mousedclick鼠标双击对象...【对象定位属性:"+property+"; 定位属性值:"+property_value+"】";
			luckyclient.publicclass.LogUtil.APP.info("mousedclick鼠标双击对象...【对象定位属性:"+property+"; 定位属性值:"+property_value+"】");
			break;
		case "mouseclickhold":
			action.clickAndHold(we).perform();
			result = "mouseclickhold鼠标点击对象后不释放...【对象定位属性:"+property+"; 定位属性值:"+property_value+"】";
			luckyclient.publicclass.LogUtil.APP.info("mouseclickhold鼠标点击对象后不释放...【对象定位属性:"+property+"; 定位属性值:"+property_value+"】");
			break;
		case "mousedrag":
			String temp[]=operation_value.split(",",-1);
			action.dragAndDropBy(we, Integer.valueOf(temp[0]), Integer.valueOf(temp[1])).perform();
			result = "mousedrag鼠标移动至对象相对坐标...【对象定位属性:"+property+"; 定位属性值:"+property_value+"】  坐标x："+Integer.valueOf(temp[0])
			+" 坐标y："+Integer.valueOf(temp[1]);
			luckyclient.publicclass.LogUtil.APP.info("mousedrag鼠标移动至对象相对坐标...【对象定位属性:"+property+"; 定位属性值:"+property_value+"】  坐标x："+Integer.valueOf(temp[0])
			+" 坐标y："+Integer.valueOf(temp[1]));
			break;
		case "mouseto":
			String temp1[]=operation_value.split(",",-1);
			action.moveToElement(we, Integer.valueOf(temp1[0]), Integer.valueOf(temp1[1])).perform();
			result = "mouseto鼠标移动至对象相对坐标...【对象定位属性:"+property+"; 定位属性值:"+property_value+"】  坐标x："+Integer.valueOf(temp1[0])
			+" 坐标y："+Integer.valueOf(temp1[1]);
			luckyclient.publicclass.LogUtil.APP.info("mouseto鼠标移动至对象相对坐标...【对象定位属性:"+property+"; 定位属性值:"+property_value+"】  坐标x："+Integer.valueOf(temp1[0])
			+" 坐标y："+Integer.valueOf(temp1[1]));
			break;
		case "mouserelease":
			action.release(we).perform();
			result = "mouserelease鼠标释放...";
			luckyclient.publicclass.LogUtil.APP.info("mouserelease鼠标释放...");
			break;
		default:
			break;
		}
		return result;
	}
	
	public static String ActionOperation(WebDriver wd,String operation,String operation_value) throws Exception{
		String result = "";
		Actions action = new Actions(wd);
		// action处理
		switch (operation) {
		case "mouselkclick":  //鼠标左键点击
			action.click().perform();
			result = "mouselkclick鼠标左键点击当前位置...";
			luckyclient.publicclass.LogUtil.APP.info("mouselkclick鼠标左键点击当前位置...");
			break;
		case "mouserkclick":
			action.contextClick().perform();
			result = "mouserkclick鼠标右键点击当前位置...";
			luckyclient.publicclass.LogUtil.APP.info("mouserkclick鼠标右键点击当前位置...");
			break;
		case "mousedclick":
			action.doubleClick().perform();
			result = "mousedclick鼠标双击当前位置...";
			luckyclient.publicclass.LogUtil.APP.info("mousedclick鼠标双击当前位置...");
			break;
		case "mouseclickhold":
			action.clickAndHold().perform();
			result = "mouseclickhold鼠标点击当前位置后不释放...";
			luckyclient.publicclass.LogUtil.APP.info("mouseclickhold鼠标点击当前位置后不释放...");
			break;
		case "mouseto":
			String temp1[]=operation_value.split(",",-1);
			action.moveByOffset(Integer.valueOf(temp1[0]), Integer.valueOf(temp1[1])).perform();
			result = "mouseto鼠标移动至对象相对坐标...坐标x："+Integer.valueOf(temp1[0])
			+" 坐标y："+Integer.valueOf(temp1[1]);
			luckyclient.publicclass.LogUtil.APP.info("mouseto鼠标移动至对象相对坐标... 坐标x："+Integer.valueOf(temp1[0])
			+" 坐标y："+Integer.valueOf(temp1[1]));
			break;
		case "mouserelease":
			action.release().perform();
			result = "mouserelease鼠标释放...";
			luckyclient.publicclass.LogUtil.APP.info("mouserelease鼠标释放...");
			break;
		case "mousekey":
			switch (operation_value) {
			case "tab":
				action.sendKeys(Keys.TAB).perform();
				result = "键盘操作TAB键...";
				luckyclient.publicclass.LogUtil.APP.info("键盘操作TAB键...");
				break;
			case "space":
				action.sendKeys(Keys.SPACE).perform();
				result = "键盘操作SPACE键...";
				luckyclient.publicclass.LogUtil.APP.info("键盘操作SPACE键...");
				break;
			case "ctrl":
				action.sendKeys(Keys.CONTROL).perform();
				result = "键盘操作CONTROL键...";
				luckyclient.publicclass.LogUtil.APP.info("键盘操作CONTROL键...");
				break;
			case "shift":
				action.sendKeys(Keys.SHIFT).perform();
				result = "键盘操作SHIFT键...";
				luckyclient.publicclass.LogUtil.APP.info("键盘操作SHIFT键...");
				break;
			case "enter":
				action.sendKeys(Keys.ENTER).perform();
				result = "键盘操作SHIFT键...";
				luckyclient.publicclass.LogUtil.APP.info("键盘操作SHIFT键...");
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
		return result;
	}
	
	public static String ObjectOperation(WebDriver wd,WebElement we,String operation,String operation_value,String property,String property_value) throws Exception{
		String result = "";
		// 处理WebElement对象操作
		switch (operation) {
		case "click":
			we.click();
			result = "click点击对象...【对象定位属性:"+property+"; 定位属性值:"+property_value+"】";
			luckyclient.publicclass.LogUtil.APP.info("click点击对象...【对象定位属性:"+property+"; 定位属性值:"+property_value+"】");
			break;
		case "sendkeys":
			we.sendKeys(operation_value);
			result = "sendKeys对象输入...【对象定位属性:"+property+"; 定位属性值:"+property_value+"; 操作值:"+operation_value+"】";
			luckyclient.publicclass.LogUtil.APP.info("sendkeys对象输入...【对象定位属性:"+property+"; 定位属性值:"+property_value+"; 操作值:"+operation_value+"】");
			break;
		case "clear":
			we.clear();
			result = "clear清空输入框...【对象定位属性:"+property+"; 定位属性值:"+property_value+"】";
			luckyclient.publicclass.LogUtil.APP.info("clear清空输入框...【对象定位属性:"+property+"; 定位属性值:"+property_value+"】");
			break; // 清空输入框
		case "gotoframe":
			wd.switchTo().frame(we);
			luckyclient.publicclass.LogUtil.APP.info("gotoframe切换Frame...【对象定位属性:"+property+"; 定位属性值:"+property_value+"】");
			break;
		case "isenabled":
			result = "获取到的值是【"+we.isEnabled()+"】";
			luckyclient.publicclass.LogUtil.APP.info("当前对象判断是否可用布尔值为【"+we.isEnabled()+"】");
			break;
		case "isdisplayed":
			result = "获取到的值是【"+we.isDisplayed()+"】";
			luckyclient.publicclass.LogUtil.APP.info("当前对象判断是否可见布尔值为【"+we.isDisplayed()+"】");
			break;
		case "exjsob":
			JavascriptExecutor jse = (JavascriptExecutor)wd;
			jse.executeScript(operation_value,we);
			result = "执行JS...【"+operation_value+"】";
			luckyclient.publicclass.LogUtil.APP.info("执行JS...【"+operation_value+"】");
			break;
		default:
			break;
		}
		return result;
	}
	
	public static String AlertOperation(WebDriver wd,String operation) throws Exception{
		String result = "";
		Alert alert = wd.switchTo().alert();
		switch (operation) {
		case "alertaccept":
			alert.accept();
			luckyclient.publicclass.LogUtil.APP.info("弹出框对象点击同意...");
			break;
		case "alertdismiss":
			alert.dismiss();
			luckyclient.publicclass.LogUtil.APP.info("弹出框对象点击取消...");
			break;
		case "alertgettext":
			result = "获取到的值是【"+alert.getText()+"】";
			luckyclient.publicclass.LogUtil.APP.info("弹出框对象通过getText获取对象text属性...【Text属性值:" + alert.getText() + "】");
			break;
		default:
			break;
		}
		return result;
	}
	
	public static String DriverOperation(WebDriver wd,String operation,String operation_value) throws Exception{
		String result = "";
		// 处理页面对象操作
		switch (operation) {
		case "open":
			operation_value = "http://"+operation_value;
			wd.get(operation_value);
			result = "Open页面...【"+operation_value+"】";
			luckyclient.publicclass.LogUtil.APP.info("Open页面...【"+operation_value+"】");
			break;
		case "exjs":
			JavascriptExecutor jse = (JavascriptExecutor)wd;
			jse.executeScript(operation_value);
			result = "执行JS...【"+operation_value+"】";
			luckyclient.publicclass.LogUtil.APP.info("执行JS...【"+operation_value+"】");
			break;
		case "gotodefaultcontent":
			wd.switchTo().defaultContent();
			luckyclient.publicclass.LogUtil.APP.info("gotodefaultcontent切换至默认页面位置...");
			break;
		case "gettitle":
			result = "获取到的值是【"+wd.getTitle()+"】";
			luckyclient.publicclass.LogUtil.APP.info("获取页面Title...【"+result+"】");
			break;
		case "getwindowhandle":
			result = "获取到的值是【"+wd.getWindowHandle()+"】";
			luckyclient.publicclass.LogUtil.APP.info("getWindowHandle获取窗口句柄...【句柄值:" + result + "】");
			break;
		case "gotowindow":
			wd.switchTo().window(operation_value);
			luckyclient.publicclass.LogUtil.APP.info("gotowindow切换句柄指定窗口...");
			break;
		case "wait":
			try {
				wd.wait(Integer.valueOf(operation_value) * 1000);
				result = "当前任务操作等待【"+operation_value+"】秒...";
				luckyclient.publicclass.LogUtil.APP.info("当前任务操作等待【"+operation_value+"】秒...");
				break;
			} catch (NumberFormatException | InterruptedException e) {
				luckyclient.publicclass.LogUtil.APP.error("等待时间转换出错 ！");
				e.printStackTrace();
				result = "等待时间转换出错，请检查参数";
				break;
			}
		default:
			break;
		}
		return result;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
