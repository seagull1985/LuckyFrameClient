package luckyclient.execution.appium.androidex;

import java.time.Duration;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.Select;

import cn.hutool.core.util.StrUtil;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.android.AndroidTouchAction;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.touch.LongPressOptions;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.ElementOption;
import io.appium.java_client.touch.offset.PointOption;
import luckyclient.execution.dispose.ChangString;
import luckyclient.utils.LogUtil;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 seagull1985
 * =================================================================
 * 
 * @author Seagull
 * @date 2017年1月29日 上午9:31:42
 */
public class AndroidEncapsulateOperation {
	/**
	 * select控件关键字处理
	 * @param ae UI对象
	 * @param operation 操作关键字
	 * @param operationValue 操作值
	 * @return 返回操作结果
	 */
	public static String selectOperation(AndroidElement ae, String operation, String operationValue) {
		String result = "";
		// 下拉框对象处理
		Select select = new Select(ae);

		// 处理下拉框事件
		switch (operation) {
		case "selectbyvisibletext":
			select.selectByVisibleText(operationValue);
			LogUtil.APP
					.info("下拉框对象通过VisibleText属性选择...【VisibleText属性值:{}】",operationValue);
			break;
		case "selectbyvalue":
			select.selectByValue(operationValue);
			LogUtil.APP.info("下拉框对象通过Value属性选择...【Value属性值:{}】",operationValue);
			break;
		case "selectbyindex":
			select.selectByIndex(Integer.parseInt(operationValue));
			LogUtil.APP.info("下拉框对象通过Index属性选择...【Index属性值:{}】",operationValue);
			break;
		case "isselect":
			result = "获取到的值是【" + ae.isSelected() + "】";
			LogUtil.APP.info("判断对象是否已经被选择...【结果值:{}】",ae.isSelected());
			break;
		default:
			break;
		}
		return result;
	}

	public static String getOperation(AndroidElement ae, String operation, String value) {
		String result = "";
		// 获取对象处理
		switch (operation) {
		case "gettext":
			result = "获取到的值是【" + ae.getText() + "】";
			LogUtil.APP.info("getText获取对象text属性...【text属性值:{}】",result);
			break; // 获取输入框内容
		case "gettagname":
			result = "获取到的值是【" + ae.getTagName() + "】";
			LogUtil.APP.info("getTagName获取对象tagname属性...【tagname属性值:{}】",result);
			break;
		case "getattribute":
			result = "获取到的值是【" + ae.getAttribute(value) + "】";
			LogUtil.APP
					.info("getAttribute获取对象【{}】属性...【{}属性值:{}】",value,value,result);
			break;
		case "getcssvalue":
			result = "获取到的值是【" + ae.getCssValue(value) + "】";
			LogUtil.APP
					.info("getCssValue获取对象【{}】属性...【{}属性值:{}】",value,value,result);
			break;
		default:
			break;
		}
		return result;
	}

	public static String objectOperation(AndroidDriver<AndroidElement> appium, AndroidElement ae, String operation,
			String operationValue, String property, String propertyValue) {
		String result = "";
		AndroidTouchAction action = new AndroidTouchAction(appium);

		// 处理WebElement对象操作
		switch (operation) {
		case "click":
			ae.click();
			result = "click点击对象...【对象定位属性:" + property + "; 定位属性值:" + propertyValue + "】";
			LogUtil.APP
					.info("click点击对象...【对象定位属性:{}; 定位属性值:{}】",property,propertyValue);
			break;
		case "sendkeys":
			ae.sendKeys(operationValue);
			result = "sendKeys对象输入...【对象定位属性:" + property + "; 定位属性值:" + propertyValue + "; 操作值:" + operationValue
					+ "】";
			LogUtil.APP.info("sendkeys对象输入...【对象定位属性:{}; 定位属性值:{}; 操作值:{}】",property,propertyValue,operationValue);
			break;
		case "clear":
			ae.clear();
			result = "clear清空输入框...【对象定位属性:" + property + "; 定位属性值:" + propertyValue + "】";
			LogUtil.APP
					.info("clear清空输入框...【对象定位属性:{}; 定位属性值:{}】",property,propertyValue);
			break; // 清空输入框
		case "isenabled":
			result = "获取到的值是【" + ae.isEnabled() + "】";
			LogUtil.APP.info("当前对象判断是否可用布尔值为【{}】",ae.isEnabled());
			break;
		case "isdisplayed":
			result = "获取到的值是【" + ae.isDisplayed() + "】";
			LogUtil.APP.info("当前对象判断是否可见布尔值为【{}】",ae.isDisplayed());
			break;
		case "exjsob":
			((JavascriptExecutor) appium).executeScript(operationValue, ae);
			result = "执行JS...【" + operationValue + "】";
			LogUtil.APP.info("执行JS...【{}】",operationValue);
			break;
		case "longpresselement":
			LongPressOptions lpoptions = new LongPressOptions();
			lpoptions.withElement(ElementOption.element(ae));
			if (null != operationValue && ChangString.isNumeric(operationValue)) {
				int nanos = Integer.parseInt(operationValue) * 1000;
				Duration duration = Duration.ofNanos(nanos);
				lpoptions.withDuration(duration);
			}
			action.longPress(lpoptions).release().perform();
			result = "longpresselement在屏幕指定元素上按住" + operationValue + "秒...【对象定位属性:" + property + "; 定位属性值:"
					+ propertyValue + "】";
			LogUtil.APP.info("longpresselement在屏幕指定元素上按住{}秒...【对象定位属性:{}; 定位属性值:{}】",operationValue,property,propertyValue);
			break;
		default:
			break;
		}
		return result;
	}

	public static String alertOperation(AndroidDriver<AndroidElement> appium, String operation) {
		String result = "";
		Alert alert = appium.switchTo().alert();
		switch (operation) {
		case "alertaccept":
			alert.accept();
			LogUtil.APP.info("弹出框对象点击同意...");
			break;
		case "alertdismiss":
			alert.dismiss();
			LogUtil.APP.info("弹出框对象点击取消...");
			break;
		case "alertgettext":
			result = "获取到的值是【" + alert.getText() + "】";
			LogUtil.APP.info("弹出框对象通过getText获取对象text属性...【Text属性值:{}】",alert.getText());
			break;
		default:
			break;
		}
		return result;
	}

	public static String driverOperation(AndroidDriver<AndroidElement> appium, String operation, String operationValue)
			throws Exception {
		String result = "";
		AndroidTouchAction action = new AndroidTouchAction(appium);
		// 处理页面对象操作
		switch (operation) {
		case "getcontexthandles":
			Set<String> handles = appium.getContextHandles();
			int handlenum = 1;
			for (String handle : handles) {
				if (String.valueOf(handlenum).equals(operationValue)) {
					if (appium.getContext().equals(handle)) {
						result = "请注意，你指定的ContextHandle就是当前页面哦，获取到的值是【" + handle + "】";
					} else {
						result = "指定ContextHandler的顺序值是" + operationValue + ",获取到的值是【" + handle + "】";
					}
					break;
				}
				handlenum++;
			}
			LogUtil.APP.info("getContext获取窗口句柄...{}",result);
			break;
		case "exjs":
			((JavascriptExecutor) appium).executeScript(operationValue);
			result = "执行JS...【" + operationValue + "】";
			LogUtil.APP.info("执行JS...【{}】",operationValue);
			break;
		case "exAdbShell":
			Runtime.getRuntime().exec(operationValue);
			result = "执行安卓adb命令...【" + operationValue + "】";
			LogUtil.APP.info("执行安卓adb命令...【{}】",operationValue);		   
		    break;
		case "androidkeycode":
			// 模拟手机键盘
			try {
				if (StrUtil.isNotBlank(operationValue)) {
					KeyEvent keyEvent = new KeyEvent();
					appium.pressKey(keyEvent.withKey(AndroidKey.valueOf(operationValue)));
					result = "模拟手机键盘发送指令...keycode【" + operationValue + "】";
					LogUtil.APP.info("模拟手机键盘发送指令...keycode【{}】", operationValue);
				} else {
					result = "模拟手机键盘失败，键盘参数为空，请检查你的参数！";
					LogUtil.APP.info("模拟手机键盘失败，键盘参数为空，请检查你的参数！");
				}
			} catch (IllegalArgumentException ae) {
				result = "模拟手机按键失败，没有找到对应的按键参数，请检查你的参数！";
				LogUtil.APP.info("模拟手机按键失败，没有找到对应的按键参数，请检查你的参数！");
			}
			break;
		// 隐藏手机键盘
		case "hidekeyboard":
			appium.hideKeyboard();
			result = "隐藏手机键盘...【hideKeyboard】";
			LogUtil.APP.info("隐藏手机键盘...【hideKeyboard】");
			break;
		case "gotocontext":
			Set<String> ctNames = appium.getContextHandles();
			int flag = 0;
			for (String contextName : ctNames) {
				if (contextName.contains(operationValue)) {
					flag = 1;
					appium.context(contextName);
					break;
				}
			}
			if (flag == 1) {
				result = "切换context至【" + operationValue + "】";
				LogUtil.APP.info("切换context至【{}】",operationValue);
			} else {
				result = "切换context失败，未找到contextName值为【" + operationValue + "】的对象";
				LogUtil.APP.info("切换context失败，未找到contextName值为【{}】的对象",operationValue);
			}
			break;
		case "getcontext":
			result = "获取到的值是【" + appium.getContext() + "】";
			LogUtil.APP.info("获取页面Context...【{}】",appium.getContext());
			break;
		case "gettitle":
			result = "获取到的值是【" + appium.getTitle() + "】";
			LogUtil.APP.info("获取页面gettitle...【{}】",appium.getTitle());
			break;
		case "swipeup":
			String[] tempup = operationValue.split("\\|", -1);
			if (null != tempup[0] && ChangString.isNumeric(tempup[0])) {
				Double second = Double.valueOf(tempup[0]);
				if (null != tempup[1] && ChangString.isNumeric(tempup[1])) {
					int num = Integer.parseInt(tempup[1]);
					AndroidBaseAppium.swipePageDown(appium, second, num);
					result = "swipeup手指向上滑动参数...秒|次数【" + second + "|" + num + "】";
					LogUtil.APP.info("swipeup手指向上滑动参数...秒|次数【{}|{}】",second,num);
				} else {
					result = "swipeup手指向上滑动参数判断次数出现异常【" + tempup[1] + "】";
					LogUtil.APP.info("swipeup手指向上滑动参数判断次数出现异常【{}】",tempup[1]);
				}
			} else {
				result = "swipeup手指向上滑动参数判断时间出现异常【" + tempup[0] + "】";
				LogUtil.APP.info("swipeup手指向上滑动参数判断时间出现异常【{}】",tempup[0]);
			}
			break;
		case "swipedown":
			String[] tempdown = operationValue.split("\\|", -1);
			if (null != tempdown[0] && ChangString.isNumeric(tempdown[0])) {
				Double second = Double.valueOf(tempdown[0]);
				if (null != tempdown[1] && ChangString.isNumeric(tempdown[1])) {
					int num = Integer.parseInt(tempdown[1]);
					AndroidBaseAppium.swipePageUp(appium, second, num);
					result = "swipedown手指向下滑动参数...秒|次数【" + second + "|" + num + "】";
					LogUtil.APP.info("swipedown手指向下滑动参数...秒|次数【{}|{}】",second,num);
				} else {
					result = "swipedown手指向下滑动参数判断次数出现异常【" + tempdown[1] + "】";
					LogUtil.APP.info("swipedown手指向下滑动参数判断次数出现异常【{}】",tempdown[1]);
				}
			} else {
				result = "swipedown手指向下滑动参数判断时间出现异常【" + tempdown[0] + "】";
				LogUtil.APP.info("swipedown手指向下滑动参数判断时间出现异常【{}】",tempdown[0]);
			}
			break;
		case "swipeleft":
			String[] templeft = operationValue.split("\\|", -1);
			if (null != templeft[0] && ChangString.isNumeric(templeft[0])) {
				Double second = Double.valueOf(templeft[0]);
				if (null != templeft[1] && ChangString.isNumeric(templeft[1])) {
					int num = Integer.parseInt(templeft[1]);
					AndroidBaseAppium.swipePageRight(appium, second, num);
					result = "swipleft手指向左滑动参数...秒|次数【" + second + "|" + num + "】";
					LogUtil.APP.info("swipleft手指向左滑动参数...秒|次数【{}|{}】",second,num);
				} else {
					result = "swipleft手指向左滑动参数判断次数出现异常【" + templeft[1] + "】";
					LogUtil.APP.info("swipleft手指向左滑动参数判断次数出现异常【{}】",templeft[1]);
				}
			} else {
				result = "swipleft手指向左滑动参数判断时间出现异常【" + templeft[0] + "】";
				LogUtil.APP.info("swipleft手指向左滑动参数判断时间出现异常【{}】",templeft[0]);
			}
			break;
		case "swiperight":
			String[] tempright = operationValue.split("\\|", -1);
			if (null != tempright[0] && ChangString.isNumeric(tempright[0])) {
				Double second = Double.valueOf(tempright[0]);
				if (null != tempright[1] && ChangString.isNumeric(tempright[1])) {
					int num = Integer.parseInt(tempright[1]);
					AndroidBaseAppium.swipePageLeft(appium, second, num);
					result = "swipright手指向右滑动参数...秒|次数【" + second + "|" + num + "】";
					LogUtil.APP.info("swipright手指向右滑动参数...秒|次数【{}|{}】",second,num);
				} else {
					result = "swipright手指向右滑动参数判断次数出现异常【" + tempright[1] + "】";
					LogUtil.APP.info("swipright手指向右滑动参数判断次数出现异常【{}】",tempright[1]);
				}
			} else {
				result = "swipright手指向右滑动参数判断时间出现异常【" + tempright[0] + "】";
				LogUtil.APP.info("swipright手指向右滑动参数判断时间出现异常【{}】",tempright[0]);
			}
			break;
		case "swipepageup":
			String[] tempPageUp = operationValue.split("\\|", -1);
			if (null != tempPageUp[0] && ChangString.isNumeric(tempPageUp[0])) {
				Double second = Double.valueOf(tempPageUp[0]);
				if (null != tempPageUp[1] && ChangString.isNumeric(tempPageUp[1])) {
					int num = Integer.parseInt(tempPageUp[1]);
					AndroidBaseAppium.swipePageUp(appium, second, num);
					result = "swipeup页面向上滑动参数...秒|次数【" + second + "|" + num + "】";
					LogUtil.APP.info("swipeup页面向上滑动参数...秒|次数【{}|{}】",second,num);
				} else {
					result = "swipeup页面向上滑动参数判断次数出现异常【" + tempPageUp[1] + "】";
					LogUtil.APP.info("swipeup页面向上滑动参数判断次数出现异常【{}】",tempPageUp[1]);
				}
			} else {
				result = "swipeup页面向上滑动参数判断时间出现异常【" + tempPageUp[0] + "】";
				LogUtil.APP.info("swipeup页面向上滑动参数判断时间出现异常【{}】",tempPageUp[0]);
			}
			break;
		case "swipepagedown":
			String[] tempPageDown = operationValue.split("\\|", -1);
			if (null != tempPageDown[0] && ChangString.isNumeric(tempPageDown[0])) {
				Double second = Double.valueOf(tempPageDown[0]);
				if (null != tempPageDown[1] && ChangString.isNumeric(tempPageDown[1])) {
					int num = Integer.parseInt(tempPageDown[1]);
					AndroidBaseAppium.swipePageDown(appium, second, num);
					result = "swipedown页面向下滑动参数...秒|次数【" + second + "|" + num + "】";
					LogUtil.APP.info("swipedown页面向下滑动参数...秒|次数【{}|{}】",second,num);
				} else {
					result = "swipedown页面向下滑动参数判断次数出现异常【" + tempPageDown[1] + "】";
					LogUtil.APP.info("swipedown页面向下滑动参数判断次数出现异常【{}】",tempPageDown[1]);
				}
			} else {
				result = "swipedown页面向下滑动参数判断时间出现异常【" + tempPageDown[0] + "】";
				LogUtil.APP.info("swipedown页面向下滑动参数判断时间出现异常【{}】",tempPageDown[0]);
			}
			break;
		case "swipepageleft":
			String[] tempPageLeft = operationValue.split("\\|", -1);
			if (null != tempPageLeft[0] && ChangString.isNumeric(tempPageLeft[0])) {
				Double second = Double.valueOf(tempPageLeft[0]);
				if (null != tempPageLeft[1] && ChangString.isNumeric(tempPageLeft[1])) {
					int num = Integer.parseInt(tempPageLeft[1]);
					AndroidBaseAppium.swipePageLeft(appium, second, num);
					result = "swipleft页面向左滑动参数...秒|次数【" + second + "|" + num + "】";
					LogUtil.APP.info("swipleft页面向左滑动参数...秒|次数【{}|{}】",second,num);
				} else {
					result = "swipleft页面向左滑动参数判断次数出现异常【" + tempPageLeft[1] + "】";
					LogUtil.APP.info("swipleft页面向左滑动参数判断次数出现异常【{}】",tempPageLeft[1]);
				}
			} else {
				result = "swipleft页面向左滑动参数判断时间出现异常【" + tempPageLeft[0] + "】";
				LogUtil.APP.info("swipleft页面向左滑动参数判断时间出现异常【{}】",tempPageLeft[0]);
			}
			break;
		case "swipepageright":
			String[] tempPageRight = operationValue.split("\\|", -1);
			if (null != tempPageRight[0] && ChangString.isNumeric(tempPageRight[0])) {
				Double second = Double.valueOf(tempPageRight[0]);
				if (null != tempPageRight[1] && ChangString.isNumeric(tempPageRight[1])) {
					int num = Integer.parseInt(tempPageRight[1]);
					AndroidBaseAppium.swipePageRight(appium, second, num);
					result = "swipright页面向右滑动参数...秒|次数【" + second + "|" + num + "】";
					LogUtil.APP.info("swipright页面向右滑动参数...秒|次数【{}|{}】",second,num);
				} else {
					result = "swipright页面向右滑动参数判断次数出现异常【" + tempPageRight[1] + "】";
					LogUtil.APP.info("swipright页面向右滑动参数判断次数出现异常【{}】",tempPageRight[1]);
				}
			} else {
				result = "swipright页面向右滑动参数判断时间出现异常【" + tempPageRight[0] + "】";
				LogUtil.APP.info("swipright页面向右滑动参数判断时间出现异常【{}】",tempPageRight[0]);
			}
			break;
		case "longpressxy":
			String[] longpressxy = operationValue.split("\\|", -1);
			if (null != longpressxy[0] && ChangString.isNumeric(longpressxy[0])) {
				int longpressx = Integer.parseInt(longpressxy[0]);
				if (null != longpressxy[1] && ChangString.isNumeric(longpressxy[1])) {
					int longpressy = Integer.parseInt(longpressxy[1]);
					if (null != longpressxy[2] && ChangString.isNumeric(longpressxy[2])) {
						LongPressOptions lpoptions = new LongPressOptions();
						lpoptions.withPosition(PointOption.point(longpressx, longpressy));
						int nanos = Integer.parseInt(longpressxy[2]) * 1000;
						Duration duration = Duration.ofNanos(nanos);
						lpoptions.withDuration(duration);
						action.longPress(lpoptions).release().perform();
						result = "longpressxy在屏幕指定XY坐标上按住" + longpressxy[2] + "秒...X|Y【" + longpressx + "|" + longpressy
								+ "】";
						LogUtil.APP.info("longpressxy在屏幕指定XY坐标上按住{}秒...X|Y【{}|{}】",longpressxy[2],longpressx,longpressy);
					} else {
						action.longPress(PointOption.point(longpressx, longpressy)).release().perform();
						result = "longpressxy在屏幕指定XY坐标上长按...X|Y【" + longpressx + "|" + longpressy + "】";
						LogUtil.APP.info("longpressxy在屏幕指定XY坐标上长按...X|Y【{}|{}】",longpressx,longpressy);
					}
				} else {
					result = "longpressxy参数指定的Y坐标处理出现异常【" + longpressxy[1] + "】";
					LogUtil.APP.info("longpressxy参数指定的Y坐标处理出现异常【{}】",longpressxy[1]);
				}
			} else {
				result = "longpressxy参数指定的X坐标处理出现异常【" + longpressxy[0] + "】";
				LogUtil.APP.info("longpressxy参数指定的X坐标处理出现异常【{}】",longpressxy[0]);
			}
			break;
		case "pressxy":
			String[] pressxy = operationValue.split("\\|", -1);
			if (null != pressxy[0] && ChangString.isNumeric(pressxy[0])) {
				int pressx = Integer.parseInt(pressxy[0]);
				if (null != pressxy[1] && ChangString.isNumeric(pressxy[1])) {
					int pressy = Integer.parseInt(pressxy[1]);
					action.press(PointOption.point(pressx, pressy)).release().perform();
					result = "pressxy在屏幕指定XY坐标上点击...X|Y【" + pressx + "|" + pressy + "】";
					LogUtil.APP.info("pressxy在屏幕指定XY坐标上点击...X|Y【{}|{}】",pressx,pressy);
				} else {
					result = "pressxy参数指定的Y坐标处理出现异常【" + pressxy[1] + "】";
					LogUtil.APP.info("pressxy参数指定的Y坐标处理出现异常【{}】",pressxy[1]);
				}
			} else {
				result = "pressxy参数指定的X坐标处理出现异常【" + pressxy[0] + "】";
				LogUtil.APP.info("pressxy参数指定的X坐标处理出现异常【{}】",pressxy[0]);
			}
			break;
		case "tapxy":
			String[] tapxy = operationValue.split("\\|", -1);
			if (null != tapxy[0] && ChangString.isNumeric(tapxy[0])) {
				int tapx = Integer.parseInt(tapxy[0]);
				if (null != tapxy[1] && ChangString.isNumeric(tapxy[1])) {
					int tapy = Integer.parseInt(tapxy[1]);
					action.tap(PointOption.point(tapx, tapy)).release().perform();
					result = "tapxy在屏幕指定XY坐标上轻击...X|Y【" + tapx + "|" + tapy + "】";
					LogUtil.APP.info("tapxy在屏幕指定XY坐标上轻击...X|Y【{}|{}】",tapx,tapy);
				} else {
					result = "tapxy参数指定的Y坐标处理出现异常【" + tapxy[1] + "】";
					LogUtil.APP.info("tapxy参数指定的Y坐标处理出现异常【{}】",tapxy[1]);
				}
			} else {
				result = "tapxy参数指定的X坐标处理出现异常【" + tapxy[0] + "】";
				LogUtil.APP.info("tapxy参数指定的X坐标处理出现异常【{}】",tapxy[0]);
			}
			break;
		case "jspressxy":
			String[] jspressxy = operationValue.split("\\|", -1);
			if (null != jspressxy[0] && ChangString.isNumeric(jspressxy[0])) {
				int jspressx = Integer.parseInt(jspressxy[0]);
				if (null != jspressxy[1] && ChangString.isNumeric(jspressxy[1])) {
					int jspressy = Integer.parseInt(jspressxy[1]);
					if (null != jspressxy[2] && ChangString.isNumeric(jspressxy[2])) {
						AndroidBaseAppium.clickScreenForJs(appium, jspressx, jspressy, Integer.parseInt(jspressxy[2]));
						result = "jspressxy在屏幕指定XY坐标上按" + jspressxy[2] + "秒...X|Y【" + jspressx + "|" + jspressy + "】";
						LogUtil.APP.info("jspressxy在屏幕指定XY坐标上按{}秒...X|Y【{}|{}】",jspressxy[2],jspressx,jspressy);
					} else {
						AndroidBaseAppium.clickScreenForJs(appium, jspressx, jspressy, 2);
						result = "jspressxy在屏幕指定XY坐标上按2秒(持续时间判断异常，使用默认2秒时间)...X|Y【" + jspressx + "|" + jspressy + "】";
						LogUtil.APP.info("jspressxy在屏幕指定XY坐标上按2秒(持续时间判断异常，使用默认2秒时间)...X|Y【{}|{}】",jspressx,jspressy);
					}
				} else {
					result = "jspressxy参数指定的Y坐标处理出现异常【" + jspressxy[1] + "】";
					LogUtil.APP.info("jspressxy参数指定的Y坐标处理出现异常【{}】",jspressxy[1]);
				}
			} else {
				result = "jspressxy参数指定的X坐标处理出现异常【" + jspressxy[0] + "】";
				LogUtil.APP.info("jspressxy参数指定的X坐标处理出现异常【{}】",jspressxy[0]);
			}
			break;
		case "moveto":
			String[] movexy = operationValue.split("\\|", -1);
			if (null != movexy[0] && !"".equals(movexy[0])) {
				String[] startxy = movexy[0].split(",", -1);
				int startx = Integer.parseInt(startxy[0]);
				int starty = Integer.parseInt(startxy[1]);
				for (int movexyi = 1; movexyi < movexy.length; movexyi++) {
					if (null != movexy[movexyi] && !"".equals(movexy[movexyi])) {
						String[] endxy = movexy[movexyi].split(",", -1);
						int endx = Integer.parseInt(endxy[0]);
						int endy = Integer.parseInt(endxy[1]);
						// 不确定是否是API的bug,只有一次MOVE时，是XY坐标，当大于一次MOVE时，坐标是偏移坐标
						if (movexy.length < 3) {
							action.press(PointOption.point(startx, starty))
									.waitAction(WaitOptions.waitOptions(Duration.ofNanos(1500)))
									.moveTo(PointOption.point(endx, endy));
							LogUtil.APP.info("从开始坐标【{},{}】拖动至坐标【{},{}】",startxy[0],startxy[1],endxy[0],endxy[1]);
						} else {
							if (movexyi == 1) {
								action.press(PointOption.point(startx, starty))
										.waitAction(WaitOptions.waitOptions(Duration.ofNanos(1500)))
										.moveTo(PointOption.point(endx - startx, endy - starty));
								LogUtil.APP.info("从开始坐标【{},{}】拖动至坐标【{},{}】",startxy[0],startxy[1],endxy[0],endxy[1]);
							} else {
								action.waitAction(WaitOptions.waitOptions(Duration.ofNanos(1500)))
										.moveTo(PointOption.point(endx - startx, endy - starty));
								LogUtil.APP
										.info("第{}次拖动至坐标【{},{}】",movexyi,endxy[0],endxy[1]);
							}
							startx = endx;
							starty = endy;
						}
					} else {
						LogUtil.APP.warn("判断结束坐标位置出现异常，结束坐标参数【{}】",movexy[movexyi]);
					}
				}
				action.release().perform();
				result = "moveto全部拖动释放并完成发送...开始位置【" + startxy[0] + "," + startxy[1] + "】";
				LogUtil.APP.info("moveto全部拖动释放并完成发送...开始位置【{},{}】",startxy[0],startxy[1]);
			} else {
				result = "moveto参数指定的起始坐标处理出现异常【" + movexy[0] + "】,请检查！";
				LogUtil.APP.info("moveto参数指定的起始坐标处理出现异常【{}】,请检查！",movexy[0]);
			}
			break;
		case "timeout":
			if (null != operationValue && ChangString.isNumeric(operationValue)) {
				// 设置页面加载最大时长30秒
				appium.manage().timeouts().pageLoadTimeout(Integer.parseInt(operationValue), TimeUnit.SECONDS);
				// 设置元素出现最大时长30秒
				appium.manage().timeouts().implicitlyWait(Integer.parseInt(operationValue), TimeUnit.SECONDS);
				result = "设置全局页面加载&元素出现最大等待时间【" + operationValue + "】秒...";
				LogUtil.APP.info("设置全局页面加载&元素出现最大等待时间【{}】秒...",operationValue);
			} else {
				result = "【等待时间转换出错，请检查参数】";
				LogUtil.APP.info(result + "原因是因为判断你的等待时间不是数字...");
			}
			break;
		case "screenshot":
			java.text.DateFormat timeformat = new java.text.SimpleDateFormat("MMdd-HHmmss");
			String imagname = "FunctionScreenShot_" + timeformat.format(new Date());
			AndroidBaseAppium.screenShot(appium, imagname);
			result = "截图名称【" + imagname + "】...";
			LogUtil.APP.info("使用方法主动截取当前屏幕...{}",result);
			break;
		default:
			break;
		}
		return result;
	}

}
