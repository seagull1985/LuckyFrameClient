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
import luckyclient.caserun.publicdispose.ChangString;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸� ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944 seagull1985
 * =================================================================
 * 
 * @author Seagull
 * @date 2018��2��2��
 * 
 */
public class IosEncapsulateOperation {
	public static String selectOperation(IOSElement ie, String operation, String operationValue) throws Exception {
		String result = "";
		// �����������
		Select select = new Select(ie);

		// �����������¼�
		switch (operation) {
		case "selectbyvisibletext":
			select.selectByVisibleText(operationValue);
			luckyclient.publicclass.LogUtil.APP
					.info("���������ͨ��VisibleText����ѡ��...��VisibleText����ֵ:" + operationValue + "��");
			break;
		case "selectbyvalue":
			select.selectByValue(operationValue);
			luckyclient.publicclass.LogUtil.APP.info("���������ͨ��Value����ѡ��...��Value����ֵ:" + operationValue + "��");
			break;
		case "selectbyindex":
			select.selectByIndex(Integer.valueOf(operationValue));
			luckyclient.publicclass.LogUtil.APP.info("���������ͨ��Index����ѡ��...��Index����ֵ:" + operationValue + "��");
			break;
		case "isselect":
			result = "��ȡ����ֵ�ǡ�" + ie.isSelected() + "��";
			luckyclient.publicclass.LogUtil.APP.info("�ж϶����Ƿ��Ѿ���ѡ��...�����ֵ:" + ie.isSelected() + "��");
			break;
		default:
			break;
		}
		return result;
	}

	public static String getOperation(IOSElement ie, String operation, String value) throws Exception {
		String result = "";
		// ��ȡ������
		switch (operation) {
		case "gettext":
			result = "��ȡ����ֵ�ǡ�" + ie.getText() + "��";
			luckyclient.publicclass.LogUtil.APP.info("getText��ȡ����text����...��text����ֵ:" + result + "��");
			break; // ��ȡ���������
		case "gettagname":
			result = "��ȡ����ֵ�ǡ�" + ie.getTagName() + "��";
			luckyclient.publicclass.LogUtil.APP.info("getTagName��ȡ����tagname����...��tagname����ֵ:" + result + "��");
			break;
		case "getattribute":
			result = "��ȡ����ֵ�ǡ�" + ie.getAttribute(value) + "��";
			luckyclient.publicclass.LogUtil.APP
					.info("getAttribute��ȡ����" + value + "������...��" + value + "����ֵ:" + result + "��");
			break;
		case "getcssvalue":
			result = "��ȡ����ֵ�ǡ�" + ie.getCssValue(value) + "��";
			luckyclient.publicclass.LogUtil.APP
					.info("getCssValue��ȡ����" + value + "������...��" + value + "����ֵ:" + result + "��");
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
		// ����WebElement�������
		switch (operation) {
		case "click":
			ie.click();
			result = "click�������...������λ����:" + property + "; ��λ����ֵ:" + propertyValue + "��";
			luckyclient.publicclass.LogUtil.APP
					.info("click�������...������λ����:" + property + "; ��λ����ֵ:" + propertyValue + "��");
			break;
		case "sendkeys":
			ie.sendKeys(operationValue);
			result = "sendKeys��������...������λ����:" + property + "; ��λ����ֵ:" + propertyValue + "; ����ֵ:" + operationValue
					+ "��";
			luckyclient.publicclass.LogUtil.APP.info("sendkeys��������...������λ����:" + property + "; ��λ����ֵ:" + propertyValue
					+ "; ����ֵ:" + operationValue + "��");
			break;
		case "clear":
			ie.clear();
			result = "clear��������...������λ����:" + property + "; ��λ����ֵ:" + propertyValue + "��";
			luckyclient.publicclass.LogUtil.APP
					.info("clear��������...������λ����:" + property + "; ��λ����ֵ:" + propertyValue + "��");
			break; // ��������
		case "isenabled":
			result = "��ȡ����ֵ�ǡ�" + ie.isEnabled() + "��";
			luckyclient.publicclass.LogUtil.APP.info("��ǰ�����ж��Ƿ���ò���ֵΪ��" + ie.isEnabled() + "��");
			break;
		case "isdisplayed":
			result = "��ȡ����ֵ�ǡ�" + ie.isDisplayed() + "��";
			luckyclient.publicclass.LogUtil.APP.info("��ǰ�����ж��Ƿ�ɼ�����ֵΪ��" + ie.isDisplayed() + "��");
			break;
		case "exjsob":
			JavascriptExecutor jse = (JavascriptExecutor) appium;
			jse.executeScript(operationValue, ie);
			result = "ִ��JS...��" + operationValue + "��";
			luckyclient.publicclass.LogUtil.APP.info("ִ��JS...��" + operationValue + "��");
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
			result = "longpresselement����Ļָ��Ԫ���ϰ�ס" + operationValue + "��...������λ����:" + property + "; ��λ����ֵ:"
					+ propertyValue + "��";
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
			luckyclient.publicclass.LogUtil.APP.info("�����������ͬ��...");
			break;
		case "alertdismiss":
			alert.dismiss();
			luckyclient.publicclass.LogUtil.APP.info("�����������ȡ��...");
			break;
		case "alertgettext":
			result = "��ȡ����ֵ�ǡ�" + alert.getText() + "��";
			luckyclient.publicclass.LogUtil.APP.info("���������ͨ��getText��ȡ����text����...��Text����ֵ:" + alert.getText() + "��");
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
		// ����ҳ��������
		switch (operation) {
		case "getcontexthandles":
			Set<String> handles = appium.getContextHandles();
			int handlenum = 1;
			for (String handle : handles) {
				if (String.valueOf(handlenum).equals(operationValue)) {
					if (appium.getContext().equals(handle)) {
						result = "��ע�⣬��ָ����ContextHandle���ǵ�ǰҳ��Ŷ����ȡ����ֵ�ǡ�" + handle + "��";
					} else {
						result = "ָ��ContextHandler��˳��ֵ��" + operationValue + ",��ȡ����ֵ�ǡ�" + handle + "��";
					}
					break;
				}
				handlenum++;
			}
			luckyclient.publicclass.LogUtil.APP.info("getContext��ȡ���ھ��..." + result);
			break;
		case "exjs":
			JavascriptExecutor jse = (JavascriptExecutor) appium;
			jse.executeScript(operationValue);
			result = "ִ��JS...��" + operationValue + "��";
			luckyclient.publicclass.LogUtil.APP.info("ִ��JS...��" + operationValue + "��");
			break;
		// �����ֻ�����
		case "hideKeyboard":
			appium.hideKeyboard();
			result = "�����ֻ�����...��hideKeyboard��";
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
				result = "�л�context����" + operationValue + "��";
				luckyclient.publicclass.LogUtil.APP.info(result);
			} else {
				result = "�л�contextʧ�ܣ�δ�ҵ�contextNameֵΪ��" + operationValue + "���Ķ���";
				luckyclient.publicclass.LogUtil.APP.info(result);
			}
			break;
		case "getcontext":
			result = "��ȡ����ֵ�ǡ�" + appium.getContext() + "��";
			luckyclient.publicclass.LogUtil.APP.info("��ȡҳ��Context...��" + appium.getContext() + "��");
			break;
		case "gettitle":
			result = "��ȡ����ֵ�ǡ�" + appium.getTitle() + "��";
			luckyclient.publicclass.LogUtil.APP.info("��ȡҳ��gettitle...��" + appium.getTitle() + "��");
			break;
		case "swipeup":
			String[] tempup = operationValue.split("\\|", -1);
			if (null != tempup[0] && ChangString.isNumeric(tempup[0])) {
				Double second = Double.valueOf(tempup[0]);
				if (null != tempup[1] && ChangString.isNumeric(tempup[1])) {
					int num = Integer.valueOf(tempup[1]);
					IosBaseAppium.swipePageUp(appium, second, num);
					result = "swipeupҳ�����ϻ�������...��|������" + second + "|" + num + "��";
					luckyclient.publicclass.LogUtil.APP.info(result);
				} else {
					result = "swipeupҳ�����ϻ��������жϴ��������쳣��" + tempup[1] + "��";
					luckyclient.publicclass.LogUtil.APP.info(result);
				}
			} else {
				result = "swipeupҳ�����ϻ��������ж�ʱ������쳣��" + tempup[0] + "��";
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
					result = "swipedownҳ�����»�������...��|������" + second + "|" + num + "��";
					luckyclient.publicclass.LogUtil.APP.info(result);
				} else {
					result = "swipedownҳ�����»��������жϴ��������쳣��" + tempdown[1] + "��";
					luckyclient.publicclass.LogUtil.APP.info(result);
				}
			} else {
				result = "swipedownҳ�����»��������ж�ʱ������쳣��" + tempdown[0] + "��";
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
					result = "swipleftҳ�����󻬶�����...��|������" + second + "|" + num + "��";
					luckyclient.publicclass.LogUtil.APP.info(result);
				} else {
					result = "swipleftҳ�����󻬶������жϴ��������쳣��" + templeft[1] + "��";
					luckyclient.publicclass.LogUtil.APP.info(result);
				}
			} else {
				result = "swipleftҳ�����󻬶������ж�ʱ������쳣��" + templeft[0] + "��";
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
					result = "swiprightҳ�����һ�������...��|������" + second + "|" + num + "��";
					luckyclient.publicclass.LogUtil.APP.info(result);
				} else {
					result = "swiprightҳ�����һ��������жϴ��������쳣��" + tempright[1] + "��";
					luckyclient.publicclass.LogUtil.APP.info(result);
				}
			} else {
				result = "swiprightҳ�����һ��������ж�ʱ������쳣��" + tempright[0] + "��";
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
						result = "longpressxy����Ļָ��XY�����ϰ�ס" + longpressxy[2] + "��...X|Y��" + longpressx + "|" + longpressy
								+ "��";
						luckyclient.publicclass.LogUtil.APP.info(result);
					} else {
						action.longPress(PointOption.point(longpressx, longpressy)).release().perform();
						result = "longpressxy����Ļָ��XY�����ϳ���...X|Y��" + longpressx + "|" + longpressy + "��";
						luckyclient.publicclass.LogUtil.APP.info(result);
					}
				} else {
					result = "longpressxy����ָ����Y���괦������쳣��" + longpressxy[1] + "��";
					luckyclient.publicclass.LogUtil.APP.info(result);
				}
			} else {
				result = "longpressxy����ָ����X���괦������쳣��" + longpressxy[0] + "��";
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
					result = "pressxy����Ļָ��XY�����ϵ��...X|Y��" + pressx + "|" + pressy + "��";
					luckyclient.publicclass.LogUtil.APP.info(result);
				} else {
					result = "pressxy����ָ����Y���괦������쳣��" + pressxy[1] + "��";
					luckyclient.publicclass.LogUtil.APP.info(result);
				}
			} else {
				result = "pressxy����ָ����X���괦������쳣��" + pressxy[0] + "��";
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
					result = "tapxy����Ļָ��XY���������...X|Y��" + tapx + "|" + tapy + "��";
					luckyclient.publicclass.LogUtil.APP.info(result);
				} else {
					result = "tapxy����ָ����Y���괦������쳣��" + tapxy[1] + "��";
					luckyclient.publicclass.LogUtil.APP.info(result);
				}
			} else {
				result = "tapxy����ָ����X���괦������쳣��" + tapxy[0] + "��";
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
						result = "jspressxy����Ļָ��XY�����ϰ�" + jspressxy[2] + "��...X|Y��" + jspressx + "|" + jspressy + "��";
						luckyclient.publicclass.LogUtil.APP.info(result);
					} else {
						IosBaseAppium.clickScreenForJs(appium, jspressx, jspressy, 2);
						result = "jspressxy����Ļָ��XY�����ϰ�2��(����ʱ���ж��쳣��ʹ��Ĭ��2��ʱ��)...X|Y��" + jspressx + "|" + jspressy + "��";
						luckyclient.publicclass.LogUtil.APP.info(result);
					}
				} else {
					result = "jspressxy����ָ����Y���괦������쳣��" + jspressxy[1] + "��";
					luckyclient.publicclass.LogUtil.APP.info(result);
				}
			} else {
				result = "jspressxy����ָ����X���괦������쳣��" + jspressxy[0] + "��";
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
						// ��ȷ���Ƿ���API��bug,ֻ��һ��MOVEʱ����XY���꣬������һ��MOVEʱ��������ƫ������
						if (movexy.length < 3) {
							action.press(PointOption.point(startx, starty))
									.waitAction(WaitOptions.waitOptions(Duration.ofNanos(1500)))
									.moveTo(PointOption.point(endx, endy));
							luckyclient.publicclass.LogUtil.APP.info("�ӿ�ʼ���꡾" + startxy[0] + "," + startxy[1]
									+ "���϶������꡾" + endxy[0] + "," + endxy[1] + "��");
						} else {
							if (movexyi == 1) {
								action.press(PointOption.point(startx, starty))
										.waitAction(WaitOptions.waitOptions(Duration.ofNanos(1500)))
										.moveTo(PointOption.point(endx - startx, endy - starty));
								luckyclient.publicclass.LogUtil.APP.info("�ӿ�ʼ���꡾" + startxy[0] + "," + startxy[1]
										+ "���϶������꡾" + endxy[0] + "," + endxy[1] + "��");
							} else {
								action.waitAction(WaitOptions.waitOptions(Duration.ofNanos(1500)))
										.moveTo(PointOption.point(endx - startx, endy - starty));
								luckyclient.publicclass.LogUtil.APP
										.info("��" + movexyi + "���϶������꡾" + endxy[0] + "," + endxy[1] + "��");
							}
							startx = endx;
							starty = endy;
						}
					} else {
						luckyclient.publicclass.LogUtil.APP.error("�жϽ�������λ�ó����쳣���������������" + movexy[movexyi] + "��");
					}
				}
				action.release().perform();
				result = "movetoȫ���϶��ͷŲ���ɷ���...��ʼλ�á�" + startxy[0] + "," + startxy[1] + "��";
				luckyclient.publicclass.LogUtil.APP.info(result);
			} else {
				result = "moveto����ָ������ʼ���괦������쳣��" + movexy[0] + "��,���飡";
				luckyclient.publicclass.LogUtil.APP.info(result);
			}
			break;
		case "timeout":
			if (null != operationValue && ChangString.isNumeric(operationValue)) {
				// ����ҳ��������ʱ��30��
				appium.manage().timeouts().pageLoadTimeout(Integer.valueOf(operationValue), TimeUnit.SECONDS);
				// ����Ԫ�س������ʱ��30��
				appium.manage().timeouts().implicitlyWait(Integer.valueOf(operationValue), TimeUnit.SECONDS);
				result = "����ȫ��ҳ�����&Ԫ�س������ȴ�ʱ�䡾" + operationValue + "����...";
				luckyclient.publicclass.LogUtil.APP.info(result);
			} else {
				result = "���ȴ�ʱ��ת���������������";
				luckyclient.publicclass.LogUtil.APP.info(result + "ԭ������Ϊ�ж���ĵȴ�ʱ�䲻������...");
			}
			break;
		case "screenshot":
			java.text.DateFormat timeformat = new java.text.SimpleDateFormat("MMdd-HHmmss");
			String imagname = "FunctionScreenShot_" + timeformat.format(new Date());
			IosBaseAppium.screenShot(appium, imagname);
			result = "��ͼ���ơ�" + imagname + "��...";
			luckyclient.publicclass.LogUtil.APP.info("ʹ�÷���������ȡ��ǰ��Ļ..." + result);
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
