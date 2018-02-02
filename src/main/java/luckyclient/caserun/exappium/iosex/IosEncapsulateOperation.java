package luckyclient.caserun.exappium.iosex;

import java.time.Duration;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.Select;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.ios.IOSTouchAction;
import io.appium.java_client.touch.LongPressOptions;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.ElementOption;
import io.appium.java_client.touch.offset.PointOption;
import luckyclient.publicclass.ChangString;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 seagull1985
 * =================================================================
 * 
 * @author Seagull
 * @date 2018年2月2日
 * 
 */
public class IosEncapsulateOperation {
	public static String selectOperation(IOSElement ie, String operation, String operationValue) throws Exception {
		String result = "";
		// 下拉框对象处理
		Select select = new Select(ie);

		// 处理下拉框事件
		switch (operation) {
		case "selectbyvisibletext":
			select.selectByVisibleText(operationValue);
			luckyclient.publicclass.LogUtil.APP
					.info("下拉框对象通过VisibleText属性选择...【VisibleText属性值:" + operationValue + "】");
			break;
		case "selectbyvalue":
			select.selectByValue(operationValue);
			luckyclient.publicclass.LogUtil.APP.info("下拉框对象通过Value属性选择...【Value属性值:" + operationValue + "】");
			break;
		case "selectbyindex":
			select.selectByIndex(Integer.valueOf(operationValue));
			luckyclient.publicclass.LogUtil.APP.info("下拉框对象通过Index属性选择...【Index属性值:" + operationValue + "】");
			break;
		case "isselect":
			result = "获取到的值是【" + ie.isSelected() + "】";
			luckyclient.publicclass.LogUtil.APP.info("判断对象是否已经被选择...【结果值:" + ie.isSelected() + "】");
			break;
		default:
			break;
		}
		return result;
	}

	public static String getOperation(IOSElement ie, String operation, String value) throws Exception {
		String result = "";
		// 获取对象处理
		switch (operation) {
		case "gettext":
			result = "获取到的值是【" + ie.getText() + "】";
			luckyclient.publicclass.LogUtil.APP.info("getText获取对象text属性...【text属性值:" + result + "】");
			break; // 获取输入框内容
		case "gettagname":
			result = "获取到的值是【" + ie.getTagName() + "】";
			luckyclient.publicclass.LogUtil.APP.info("getTagName获取对象tagname属性...【tagname属性值:" + result + "】");
			break;
		case "getattribute":
			result = "获取到的值是【" + ie.getAttribute(value) + "】";
			luckyclient.publicclass.LogUtil.APP
					.info("getAttribute获取对象【" + value + "】属性...【" + value + "属性值:" + result + "】");
			break;
		case "getcssvalue":
			result = "获取到的值是【" + ie.getCssValue(value) + "】";
			luckyclient.publicclass.LogUtil.APP
					.info("getCssValue获取对象【" + value + "】属性...【" + value + "属性值:" + result + "】");
			break;
		default:
			break;
		}
		return result;
	}

	public static String objectOperation(IOSDriver<IOSElement> appium, IOSElement ie, String operation,
			String operationValue, String property, String propertyValue) throws Exception {
		String result = "";
		IOSTouchAction action = new IOSTouchAction(appium);
		// 处理WebElement对象操作
		switch (operation) {
		case "click":
			ie.click();
			result = "click点击对象...【对象定位属性:" + property + "; 定位属性值:" + propertyValue + "】";
			luckyclient.publicclass.LogUtil.APP
					.info("click点击对象...【对象定位属性:" + property + "; 定位属性值:" + propertyValue + "】");
			break;
		case "sendkeys":
			ie.sendKeys(operationValue);
			result = "sendKeys对象输入...【对象定位属性:" + property + "; 定位属性值:" + propertyValue + "; 操作值:" + operationValue
					+ "】";
			luckyclient.publicclass.LogUtil.APP.info("sendkeys对象输入...【对象定位属性:" + property + "; 定位属性值:" + propertyValue
					+ "; 操作值:" + operationValue + "】");
			break;
		case "clear":
			ie.clear();
			result = "clear清空输入框...【对象定位属性:" + property + "; 定位属性值:" + propertyValue + "】";
			luckyclient.publicclass.LogUtil.APP
					.info("clear清空输入框...【对象定位属性:" + property + "; 定位属性值:" + propertyValue + "】");
			break; // 清空输入框
		case "isenabled":
			result = "获取到的值是【" + ie.isEnabled() + "】";
			luckyclient.publicclass.LogUtil.APP.info("当前对象判断是否可用布尔值为【" + ie.isEnabled() + "】");
			break;
		case "isdisplayed":
			result = "获取到的值是【" + ie.isDisplayed() + "】";
			luckyclient.publicclass.LogUtil.APP.info("当前对象判断是否可见布尔值为【" + ie.isDisplayed() + "】");
			break;
		case "exjsob":
			JavascriptExecutor jse = (JavascriptExecutor) appium;
			jse.executeScript(operationValue, ie);
			result = "执行JS...【" + operationValue + "】";
			luckyclient.publicclass.LogUtil.APP.info("执行JS...【" + operationValue + "】");
			break;
		case "longpresselement":
			LongPressOptions lpoptions = new LongPressOptions();
			lpoptions.withElement(ElementOption.element(ie));
			if (null != operationValue && ChangString.isNumeric(operationValue)) {
				int nanos = Integer.valueOf(operationValue) * 1000;
				Duration duration = Duration.ofNanos(nanos);
				lpoptions.withDuration(duration);
			}
			action.longPress(lpoptions).release().perform();
			result = "longpresselement在屏幕指定元素上按住" + operationValue + "秒...【对象定位属性:" + property + "; 定位属性值:"
					+ propertyValue + "】";
			luckyclient.publicclass.LogUtil.APP.info(result);
			break;
		default:
			break;
		}
		return result;
	}

	public static String alertOperation(IOSDriver<IOSElement> appium, String operation) throws Exception {
		String result = "";
		Alert alert = appium.switchTo().alert();
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
			result = "获取到的值是【" + alert.getText() + "】";
			luckyclient.publicclass.LogUtil.APP.info("弹出框对象通过getText获取对象text属性...【Text属性值:" + alert.getText() + "】");
			break;
		default:
			break;
		}
		return result;
	}

	public static String driverOperation(IOSDriver<IOSElement> appium, String operation, String operationValue)
			throws Exception {
		String result = "";
		IOSTouchAction action = new IOSTouchAction(appium);
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
			luckyclient.publicclass.LogUtil.APP.info("getContext获取窗口句柄..." + result);
			break;
		case "exjs":
			JavascriptExecutor jse = (JavascriptExecutor) appium;
			jse.executeScript(operationValue);
			result = "执行JS...【" + operationValue + "】";
			luckyclient.publicclass.LogUtil.APP.info("执行JS...【" + operationValue + "】");
			break;
		// 隐藏手机键盘
		case "hideKeyboard":
			appium.hideKeyboard();
			result = "隐藏手机键盘...【hideKeyboard】";
			luckyclient.publicclass.LogUtil.APP.info(result);
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
				luckyclient.publicclass.LogUtil.APP.info(result);
			} else {
				result = "切换context失败，未找到contextName值为【" + operationValue + "】的对象";
				luckyclient.publicclass.LogUtil.APP.info(result);
			}
			break;
		case "getcontext":
			result = "获取到的值是【" + appium.getContext() + "】";
			luckyclient.publicclass.LogUtil.APP.info("获取页面Context...【" + appium.getContext() + "】");
			break;
		case "gettitle":
			result = "获取到的值是【" + appium.getTitle() + "】";
			luckyclient.publicclass.LogUtil.APP.info("获取页面gettitle...【" + appium.getTitle() + "】");
			break;
		case "swipeup":
			String[] tempup = operationValue.split("\\|", -1);
			if (null != tempup[0] && ChangString.isNumeric(tempup[0])) {
				Double second = Double.valueOf(tempup[0]);
				if (null != tempup[1] && ChangString.isNumeric(tempup[1])) {
					int num = Integer.valueOf(tempup[1]);
					IosBaseAppium.swipePageUp(appium, second, num);
					result = "swipeup页面向上滑动参数...秒|次数【" + second + "|" + num + "】";
					luckyclient.publicclass.LogUtil.APP.info(result);
				} else {
					result = "swipeup页面向上滑动参数判断次数出现异常【" + tempup[1] + "】";
					luckyclient.publicclass.LogUtil.APP.info(result);
				}
			} else {
				result = "swipeup页面向上滑动参数判断时间出现异常【" + tempup[0] + "】";
				luckyclient.publicclass.LogUtil.APP.info(result);
			}
			break;
		case "swipedown":
			String[] tempdown = operationValue.split("\\|", -1);
			if (null != tempdown[0] && ChangString.isNumeric(tempdown[0])) {
				Double second = Double.valueOf(tempdown[0]);
				if (null != tempdown[1] && ChangString.isNumeric(tempdown[1])) {
					int num = Integer.valueOf(tempdown[1]);
					IosBaseAppium.swipePageDown(appium, second, num);
					result = "swipedown页面向下滑动参数...秒|次数【" + second + "|" + num + "】";
					luckyclient.publicclass.LogUtil.APP.info(result);
				} else {
					result = "swipedown页面向下滑动参数判断次数出现异常【" + tempdown[1] + "】";
					luckyclient.publicclass.LogUtil.APP.info(result);
				}
			} else {
				result = "swipedown页面向下滑动参数判断时间出现异常【" + tempdown[0] + "】";
				luckyclient.publicclass.LogUtil.APP.info(result);
			}
			break;
		case "swipleft":
			String[] templeft = operationValue.split("\\|", -1);
			if (null != templeft[0] && ChangString.isNumeric(templeft[0])) {
				Double second = Double.valueOf(templeft[0]);
				if (null != templeft[1] && ChangString.isNumeric(templeft[1])) {
					int num = Integer.valueOf(templeft[1]);
					IosBaseAppium.swipePageLeft(appium, second, num);
					result = "swipleft页面向左滑动参数...秒|次数【" + second + "|" + num + "】";
					luckyclient.publicclass.LogUtil.APP.info(result);
				} else {
					result = "swipleft页面向左滑动参数判断次数出现异常【" + templeft[1] + "】";
					luckyclient.publicclass.LogUtil.APP.info(result);
				}
			} else {
				result = "swipleft页面向左滑动参数判断时间出现异常【" + templeft[0] + "】";
				luckyclient.publicclass.LogUtil.APP.info(result);
			}
			break;
		case "swipright":
			String[] tempright = operationValue.split("\\|", -1);
			if (null != tempright[0] && ChangString.isNumeric(tempright[0])) {
				Double second = Double.valueOf(tempright[0]);
				if (null != tempright[1] && ChangString.isNumeric(tempright[1])) {
					int num = Integer.valueOf(tempright[1]);
					IosBaseAppium.swipePageRight(appium, second, num);
					result = "swipright页面向右滑动参数...秒|次数【" + second + "|" + num + "】";
					luckyclient.publicclass.LogUtil.APP.info(result);
				} else {
					result = "swipright页面向右滑动参数判断次数出现异常【" + tempright[1] + "】";
					luckyclient.publicclass.LogUtil.APP.info(result);
				}
			} else {
				result = "swipright页面向右滑动参数判断时间出现异常【" + tempright[0] + "】";
				luckyclient.publicclass.LogUtil.APP.info(result);
			}
			break;
		case "longpressxy":
			String[] longpressxy = operationValue.split("\\|", -1);
			if (null != longpressxy[0] && ChangString.isNumeric(longpressxy[0])) {
				int longpressx = Integer.valueOf(longpressxy[0]);
				if (null != longpressxy[1] && ChangString.isNumeric(longpressxy[1])) {
					int longpressy = Integer.valueOf(longpressxy[1]);
					if (null != longpressxy[2] && ChangString.isNumeric(longpressxy[2])) {
						LongPressOptions lpoptions = new LongPressOptions();
						lpoptions.withPosition(PointOption.point(longpressx, longpressy));
						int nanos = Integer.valueOf(longpressxy[2]) * 1000;
						Duration duration = Duration.ofNanos(nanos);
						lpoptions.withDuration(duration);
						action.longPress(lpoptions).release().perform();
						result = "longpressxy在屏幕指定XY坐标上按住" + longpressxy[2] + "秒...X|Y【" + longpressx + "|" + longpressy
								+ "】";
						luckyclient.publicclass.LogUtil.APP.info(result);
					} else {
						action.longPress(PointOption.point(longpressx, longpressy)).release().perform();
						result = "longpressxy在屏幕指定XY坐标上长按...X|Y【" + longpressx + "|" + longpressy + "】";
						luckyclient.publicclass.LogUtil.APP.info(result);
					}
				} else {
					result = "longpressxy参数指定的Y坐标处理出现异常【" + longpressxy[1] + "】";
					luckyclient.publicclass.LogUtil.APP.info(result);
				}
			} else {
				result = "longpressxy参数指定的X坐标处理出现异常【" + longpressxy[0] + "】";
				luckyclient.publicclass.LogUtil.APP.info(result);
			}
			break;
		case "pressxy":
			String[] pressxy = operationValue.split("\\|", -1);
			if (null != pressxy[0] && ChangString.isNumeric(pressxy[0])) {
				int pressx = Integer.valueOf(pressxy[0]);
				if (null != pressxy[1] && ChangString.isNumeric(pressxy[1])) {
					int pressy = Integer.valueOf(pressxy[1]);
					action.press(PointOption.point(pressx, pressy)).release().perform();
					result = "pressxy在屏幕指定XY坐标上点击...X|Y【" + pressx + "|" + pressy + "】";
					luckyclient.publicclass.LogUtil.APP.info(result);
				} else {
					result = "pressxy参数指定的Y坐标处理出现异常【" + pressxy[1] + "】";
					luckyclient.publicclass.LogUtil.APP.info(result);
				}
			} else {
				result = "pressxy参数指定的X坐标处理出现异常【" + pressxy[0] + "】";
				luckyclient.publicclass.LogUtil.APP.info(result);
			}
			break;
		case "tapxy":
			String[] tapxy = operationValue.split("\\|", -1);
			if (null != tapxy[0] && ChangString.isNumeric(tapxy[0])) {
				int tapx = Integer.valueOf(tapxy[0]);
				if (null != tapxy[1] && ChangString.isNumeric(tapxy[1])) {
					int tapy = Integer.valueOf(tapxy[1]);
					action.tap(PointOption.point(tapx, tapy)).release().perform();
					result = "tapxy在屏幕指定XY坐标上轻击...X|Y【" + tapx + "|" + tapy + "】";
					luckyclient.publicclass.LogUtil.APP.info(result);
				} else {
					result = "tapxy参数指定的Y坐标处理出现异常【" + tapxy[1] + "】";
					luckyclient.publicclass.LogUtil.APP.info(result);
				}
			} else {
				result = "tapxy参数指定的X坐标处理出现异常【" + tapxy[0] + "】";
				luckyclient.publicclass.LogUtil.APP.info(result);
			}
			break;
		case "jspressxy":
			String[] jspressxy = operationValue.split("\\|", -1);
			if (null != jspressxy[0] && ChangString.isNumeric(jspressxy[0])) {
				int jspressx = Integer.valueOf(jspressxy[0]);
				if (null != jspressxy[1] && ChangString.isNumeric(jspressxy[1])) {
					int jspressy = Integer.valueOf(jspressxy[1]);
					if (null != jspressxy[2] && ChangString.isNumeric(jspressxy[2])) {
						IosBaseAppium.clickScreenForJs(appium, jspressx, jspressy, Integer.valueOf(jspressxy[2]));
						result = "jspressxy在屏幕指定XY坐标上按" + jspressxy[2] + "秒...X|Y【" + jspressx + "|" + jspressy + "】";
						luckyclient.publicclass.LogUtil.APP.info(result);
					} else {
						IosBaseAppium.clickScreenForJs(appium, jspressx, jspressy, 2);
						result = "jspressxy在屏幕指定XY坐标上按2秒(持续时间判断异常，使用默认2秒时间)...X|Y【" + jspressx + "|" + jspressy + "】";
						luckyclient.publicclass.LogUtil.APP.info(result);
					}
				} else {
					result = "jspressxy参数指定的Y坐标处理出现异常【" + jspressxy[1] + "】";
					luckyclient.publicclass.LogUtil.APP.info(result);
				}
			} else {
				result = "jspressxy参数指定的X坐标处理出现异常【" + jspressxy[0] + "】";
				luckyclient.publicclass.LogUtil.APP.info(result);
			}
			break;
		case "moveto":
			String[] movexy = operationValue.split("\\|", -1);
			if (null != movexy[0] && !"".equals(movexy[0])) {
				String[] startxy = movexy[0].split(",", -1);
				int startx = Integer.valueOf(startxy[0]);
				int starty = Integer.valueOf(startxy[1]);
				for (int movexyi = 1; movexyi < movexy.length; movexyi++) {
					if (null != movexy[movexyi] && !"".equals(movexy[movexyi])) {
						String[] endxy = movexy[movexyi].split(",", -1);
						int endx = Integer.valueOf(endxy[0]);
						int endy = Integer.valueOf(endxy[1]);
						// 不确定是否是API的bug,只有一次MOVE时，是XY坐标，当大于一次MOVE时，坐标是偏移坐标
						if (movexy.length < 3) {
							action.press(PointOption.point(startx, starty))
									.waitAction(WaitOptions.waitOptions(Duration.ofNanos(1500)))
									.moveTo(PointOption.point(endx, endy));
							luckyclient.publicclass.LogUtil.APP.info("从开始坐标【" + startxy[0] + "," + startxy[1]
									+ "】拖动至坐标【" + endxy[0] + "," + endxy[1] + "】");
						} else {
							if (movexyi == 1) {
								action.press(PointOption.point(startx, starty))
										.waitAction(WaitOptions.waitOptions(Duration.ofNanos(1500)))
										.moveTo(PointOption.point(endx - startx, endy - starty));
								luckyclient.publicclass.LogUtil.APP.info("从开始坐标【" + startxy[0] + "," + startxy[1]
										+ "】拖动至坐标【" + endxy[0] + "," + endxy[1] + "】");
							} else {
								action.waitAction(WaitOptions.waitOptions(Duration.ofNanos(1500)))
										.moveTo(PointOption.point(endx - startx, endy - starty));
								luckyclient.publicclass.LogUtil.APP
										.info("第" + movexyi + "次拖动至坐标【" + endxy[0] + "," + endxy[1] + "】");
							}
							startx = endx;
							starty = endy;
						}
					} else {
						luckyclient.publicclass.LogUtil.APP.error("判断结束坐标位置出现异常，结束坐标参数【" + movexy[movexyi] + "】");
					}
				}
				action.release().perform();
				result = "moveto全部拖动释放并完成发送...开始位置【" + startxy[0] + "," + startxy[1] + "】";
				luckyclient.publicclass.LogUtil.APP.info(result);
			} else {
				result = "moveto参数指定的起始坐标处理出现异常【" + movexy[0] + "】,请检查！";
				luckyclient.publicclass.LogUtil.APP.info(result);
			}
			break;
		case "timeout":
			if (null != operationValue && ChangString.isNumeric(operationValue)) {
				// 设置页面加载最大时长30秒
				appium.manage().timeouts().pageLoadTimeout(Integer.valueOf(operationValue), TimeUnit.SECONDS);
				// 设置元素出现最大时长30秒
				appium.manage().timeouts().implicitlyWait(Integer.valueOf(operationValue), TimeUnit.SECONDS);
				result = "设置全局页面加载&元素出现最大等待时间【" + operationValue + "】秒...";
				luckyclient.publicclass.LogUtil.APP.info(result);
			} else {
				result = "【等待时间转换出错，请检查参数】";
				luckyclient.publicclass.LogUtil.APP.info(result + "原因是因为判断你的等待时间不是数字...");
			}
			break;
		case "screenshot":
			java.text.DateFormat timeformat = new java.text.SimpleDateFormat("MMdd-HHmmss");
			String imagname = "FunctionScreenShot_" + timeformat.format(new Date());
			IosBaseAppium.screenShot(appium, imagname);
			result = "截图名称【" + imagname + "】...";
			luckyclient.publicclass.LogUtil.APP.info("使用方法主动截取当前屏幕..." + result);
			break;
		default:
			break;
		}
		return result;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
