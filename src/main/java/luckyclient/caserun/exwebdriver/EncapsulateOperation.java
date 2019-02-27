package luckyclient.caserun.exwebdriver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import luckyclient.caserun.exwebdriver.ocr.Ocr;
import luckyclient.caserun.publicdispose.ChangString;
import luckyclient.publicclass.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸�
 * ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944  seagull1985
 * =================================================================
 *
 * @author�� seagull
 * @date 2017��12��1�� ����9:29:40
 */
public class EncapsulateOperation {

    public static String selectOperation(WebElement we, String operation, String operationValue) throws Exception {
        String result = "";
        // �����������
        Select select = new Select(we);

        // �����������¼�
        switch (operation) {
            case "selectbyvisibletext":
                select.selectByVisibleText(operationValue);
                result = "���������ͨ��VisibleText����ѡ��...��VisibleText����ֵ:" + operationValue + "��";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "selectbyvalue":
                select.selectByValue(operationValue);
                result = "���������ͨ��Value����ѡ��...��Value����ֵ:" + operationValue + "��";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "selectbyindex":
                select.selectByIndex(Integer.valueOf(operationValue));
                result = "���������ͨ��Index����ѡ��...��Index����ֵ:" + operationValue + "��";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "isselect":
                result = "��ȡ����ֵ�ǡ�" + we.isSelected() + "��";
                luckyclient.publicclass.LogUtil.APP.info("�ж϶����Ƿ��Ѿ���ѡ��...�����ֵ:" + we.isSelected() + "��");
                break;
            default:
                break;
        }
        return result;
    }

    public static String getOperation(WebDriver wd, WebElement we, String operation, String value) throws Exception {
        String result = "";
        // ��ȡ������
        switch (operation) {
            case "gettext":
                result = "��ȡ����ֵ�ǡ�" + we.getText() + "��";
                luckyclient.publicclass.LogUtil.APP.info("getText��ȡ����text����...��text����ֵ:" + result + "��");
                break; // ��ȡ���������
            case "gettagname":
                result = "��ȡ����ֵ�ǡ�" + we.getTagName() + "��";
                luckyclient.publicclass.LogUtil.APP.info("getTagName��ȡ����tagname����...��tagname����ֵ:" + result + "��");
                break;
            case "getattribute":
                result = "��ȡ����ֵ�ǡ�" + we.getAttribute(value) + "��";
                luckyclient.publicclass.LogUtil.APP.info("getAttribute��ȡ����" + value + "������...��" + value + "����ֵ:" + result + "��");
                break;
            case "getcssvalue":
                result = "��ȡ����ֵ�ǡ�" + we.getCssValue(value) + "��";
                luckyclient.publicclass.LogUtil.APP.info("getCssValue��ȡ����" + value + "������...��" + value + "����ֵ:" + result + "��");
                break;
            case "getcaptcha":
                result = "��ȡ����ֵ�ǡ�" + Ocr.getCAPTCHA(wd, we) + "��";
                luckyclient.publicclass.LogUtil.APP.info("getcaptcha��ȡ��֤��...����֤��ֵ:" + result + "��");
                break;
            default:
                break;
        }
        return result;
    }

    public static String actionWeOperation(WebDriver wd, WebElement we, String operation, String operationValue, String property, String propertyValue) throws Exception {
        String result = "";
        Actions action = new Actions(wd);
        // action����
        switch (operation) {
            //���������
            case "mouselkclick":
                action.click(we).perform();
                result = "mouselkclick�������������...������λ����:" + property + "; ��λ����ֵ:" + propertyValue + "��";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "mouserkclick":
                action.contextClick(we).perform();
                result = "mouserkclick����Ҽ��������...������λ����:" + property + "; ��λ����ֵ:" + propertyValue + "��";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "mousedclick":
                action.doubleClick(we).perform();
                result = "mousedclick���˫������...������λ����:" + property + "; ��λ����ֵ:" + propertyValue + "��";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "mouseclickhold":
                action.clickAndHold(we).perform();
                result = "mouseclickhold�����������ͷ�...������λ����:" + property + "; ��λ����ֵ:" + propertyValue + "��";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "mousedrag":
                int[] location = getLocationFromParam(operationValue, ",");
//                String[] temp = operationValue.split(",", -1);
                action.dragAndDropBy(we, location[0], location[1]).perform();
                result = "mousedrag����ƶ��������������...������λ����:" + property + "; ��λ����ֵ:" + propertyValue + "; �������(x,y):" + location[0] + "," + location[1] + "��";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "mouseto":
                int[] location1 = getLocationFromParam(operationValue, ",");
//                String[] temp1 = operationValue.split(",", -1);
                action.moveToElement(we, location1[0], location1[1]).perform();
                result = "mouseto����ƶ��������������...������λ����:" + property + "; ��λ����ֵ:" + propertyValue + "; �������(x,y):" + location1[0] + "," + location1[1] + "��";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "mouserelease":
                action.release(we).perform();
                result = "mouserelease����ͷ�...";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            default:
                break;
        }
        return result;
    }

    public static String actionOperation(WebDriver wd, String operation, String operationValue) throws Exception {
        String result = "";
        Actions action = new Actions(wd);
        // action����
        switch (operation) {
            //���������
            case "mouselkclick":
                action.click().perform();
                result = "mouselkclick�����������ǰλ��...";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "mouserkclick":
                action.contextClick().perform();
                result = "mouserkclick����Ҽ������ǰλ��...";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "mousedclick":
                action.doubleClick().perform();
                result = "mousedclick���˫����ǰλ��...";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "mouseclickhold":
                action.clickAndHold().perform();
                result = "mouseclickhold�������ǰλ�ú��ͷ�...";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "mouseto":
                int[] location = getLocationFromParam(operationValue, ",");
//                String[] temp1 = operationValue.split(",", -1);
                action.moveByOffset(location[0], location[1]).perform();
                result = "mouseto����ƶ��������������...����x��" + location[0] + " ����y��" + location[1];
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "mouserelease":
                action.release().perform();
                result = "mouserelease����ͷ�...";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "mousekey":
                switch (operationValue) {
                    case "tab":
                        action.sendKeys(Keys.TAB).perform();
                        result = "���̲���TAB��...";
                        luckyclient.publicclass.LogUtil.APP.info(result);
                        break;
                    case "space":
                        action.sendKeys(Keys.SPACE).perform();
                        result = "���̲���SPACE��...";
                        luckyclient.publicclass.LogUtil.APP.info(result);
                        break;
                    case "ctrl":
                        action.sendKeys(Keys.CONTROL).perform();
                        result = "���̲���CONTROL��...";
                        luckyclient.publicclass.LogUtil.APP.info(result);
                        break;
                    case "shift":
                        action.sendKeys(Keys.SHIFT).perform();
                        result = "���̲���SHIFT��...";
                        luckyclient.publicclass.LogUtil.APP.info(result);
                        break;
                    case "enter":
                        action.sendKeys(Keys.ENTER).perform();
                        result = "���̲���ENTER��...";
                        luckyclient.publicclass.LogUtil.APP.info(result);
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

    public static String objectOperation(WebDriver wd, WebElement we, String operation, String operationValue, String property, String propertyValue) throws Exception {
        String result = "";
        // ����WebElement�������
        switch (operation) {
            case "click":
                we.click();
                result = "click�������...������λ����:" + property + "; ��λ����ֵ:" + propertyValue + "��";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "sendkeys":
                we.sendKeys(operationValue);
                result = "sendKeys��������...������λ����:" + property + "; ��λ����ֵ:" + propertyValue + "; ����ֵ:" + operationValue + "��";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "clear":
                we.clear();
                result = "clear��������...������λ����:" + property + "; ��λ����ֵ:" + propertyValue + "��";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break; // ��������
            case "gotoframe":
                wd.switchTo().frame(we);
                result = "gotoframe�л�Frame...������λ����:" + property + "; ��λ����ֵ:" + propertyValue + "��";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "isenabled":
                result = "��ȡ����ֵ�ǡ�" + we.isEnabled() + "��";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "isdisplayed":
                result = "��ȡ����ֵ�ǡ�" + we.isDisplayed() + "��";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "exjsob":
                JavascriptExecutor jse = (JavascriptExecutor) wd;
                Object obj = jse.executeScript(operationValue, we);
                if (null != obj) {
                    String tmp = obj.toString();
                    result = (100 < tmp.length()) ? tmp.substring(0, 100) + "..." : tmp;
                    result = "��ȡ����ֵ�ǡ�" + result + "��";
                    LogUtil.APP.info("ִ��JS...��" + operationValue + "�������صĽ��Ϊ��" + result);
                } else {
                    result = "ִ��JS...��" + operationValue + "��";
                    LogUtil.APP.info(result);
                }
                break;
            case "scrollto":
                Point location = we.getLocation();
                ((JavascriptExecutor) wd).executeScript("window.scrollTo(" + location.getX() + ", " + location.getY() + ")");
                result = "������Ŀ�����...������λ����:" + property + "; ��λ����ֵ:" + propertyValue + "; ��������(x,y):" + location.getX() + "," + location.getY() + "��";;
                LogUtil.APP.info(result);
                break;
            case "scrollintoview":
                // �˷���������ִ��js����������
                ((JavascriptExecutor) wd).executeScript("arguments[0].scrollIntoView(" + operationValue + ")", we);
                result = "��Ŀ��������������...������λ����:" + property + "; ��λ����ֵ:" + propertyValue + "��";
                LogUtil.APP.info(result);
                break;
            default:
                break;
        }
        return result;
    }

    public static String alertOperation(WebDriver wd, String operation) throws Exception {
        String result = "";
        Alert alert = wd.switchTo().alert();
        switch (operation) {
            case "alertaccept":
                alert.accept();
                result = "�����������ͬ��...";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "alertdismiss":
                alert.dismiss();
                result = "�����������ȡ��...";
                luckyclient.publicclass.LogUtil.APP.info(result);
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

    public static String driverOperation(WebDriver wd, String operation, String operationValue) throws Exception {
        String result = "";
        // ����ҳ��������
        switch (operation) {
            case "open":
                wd.get(operationValue);
                result = "Openҳ��...��" + operationValue + "��";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "addcookie":
                List<Cookie> cookies = buildCookie(operationValue);
                if (null != cookies && cookies.size() > 0) {
                    for (Cookie cookie : cookies) {
                        wd.manage().addCookie(cookie);
                        luckyclient.publicclass.LogUtil.APP.info("���Cookie:��"+cookie+"���ɹ���");
                    }
                }
                result = "���cookie...��" + operationValue + "��";
                break;
            case "exjs":
                JavascriptExecutor jse = (JavascriptExecutor) wd;
                Object obj = jse.executeScript(operationValue);
                if (null != obj) {
                    String tmp = obj.toString();
                    result = (100 < tmp.length()) ? tmp.substring(0, 100) + "..." : tmp;
                    result = "��ȡ����ֵ�ǡ�" + result + "��";
                    LogUtil.APP.info("ִ��JS...��" + operationValue + "�������صĽ��Ϊ��" + result);
                } else {
                    result = "ִ��JS...��" + operationValue + "��";
                    LogUtil.APP.info(result+"��ִ��JS����null��û�з���");
                }
                break;
            case "gotodefaultcontent":
                wd.switchTo().defaultContent();
                result = "gotodefaultcontent�л���Ĭ��ҳ��λ��...";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "gotoparentframe":
                wd.switchTo().parentFrame();
                result = "gotoparentframe�л�����һ��frameλ��...";
                LogUtil.APP.info(result);
                break;
            case "gettitle":
                result = "��ȡ����ֵ�ǡ�" + wd.getTitle() + "��";
                luckyclient.publicclass.LogUtil.APP.info("��ȡҳ��Title...��" + wd.getTitle() + "��");
                break;
            case "getwindowhandle":
                result = getTargetWindowHandle(wd, operationValue);
                break;
            case "gotowindow":
                result = switchToTargetWindow(wd, operationValue);
                break;
            case "closewindow":
                wd.close();
                result = "�رյ�ǰ���������...";
                break;
            case "timeout":
                try {
                    // ����ҳ��������ʱ��30��
                    wd.manage().timeouts().pageLoadTimeout(Integer.valueOf(operationValue), TimeUnit.SECONDS);
                    // ����Ԫ�س������ʱ��30��
                    wd.manage().timeouts().implicitlyWait(Integer.valueOf(operationValue), TimeUnit.SECONDS);
                    result = "��ǰ��������ȴ���" + operationValue + "����...";
                    luckyclient.publicclass.LogUtil.APP.info(result);
                    break;
                } catch (NumberFormatException e) {
                    luckyclient.publicclass.LogUtil.APP.error("�ȴ�ʱ��ת������");
                    e.printStackTrace();
                    result = "���ȴ�ʱ��ת���������������";
                    break;
                }
            default:
                break;
        }
        return result;
    }

    private static List<Cookie> buildCookie(String operationValue) {
        if (StringUtils.isBlank(operationValue)) {
        	luckyclient.publicclass.LogUtil.APP.info("��ȡCookieֵ��operationValueΪ�գ�");
            return null;
        }
        try {
            JSONArray objects = JSON.parseArray(operationValue);
            if (null == objects) {
            	luckyclient.publicclass.LogUtil.APP.info("��ʽ��Cookie�ַ�����JSONArray������Ϊ�գ�");
                return null;
            }
            List<Cookie> result = new ArrayList<>(objects.size());
            for (int i = 0; i < objects.size(); i++) {
                JSONObject jsonObject = objects.getJSONObject(i);
                if (null == jsonObject) {
                    continue;
                }
                String name = jsonObject.getString("name");
                String val = jsonObject.getString("val");
                String domain = jsonObject.getString("domain");
                String path = jsonObject.getString("path");
                // TODO ����೤ʱ�䣬���ʧЧʱ��,��λ����
                //String expire = jsonObject.getString("expire");
                if (!StringUtils.isBlank(name) && !StringUtils.isBlank(val)) {
                    Cookie cookie = new Cookie(name, val, domain, path, null);
                	luckyclient.publicclass.LogUtil.APP.info("����Cookie�ɹ�����"+cookie+"��");
                    result.add(cookie);
                }else{
                    luckyclient.publicclass.LogUtil.APP.error("cookie:" + jsonObject + "����,name����valΪ�գ�");
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            luckyclient.publicclass.LogUtil.APP.error("��ʽ��Cookie��������������ĸ�ʽ�Ƿ���ȷ����"+operationValue+"��");
            return null;
        }
    }

    private static int[] getLocationFromParam(String param, String split) {
        int[] location = {0, 0};
        if (null == param || param.trim().isEmpty()) {
            return location;
        } else {
            // �������ָ���
            if (! param.contains(split)) {
                location[0] = Integer.valueOf(param.trim());
            } else {
                String[] tmp = param.split(split, 2);
                for (int i = 0; i < 2; i++) {
                    if (! tmp[i].trim().isEmpty()) location[i] = Integer.valueOf(tmp[i].trim());
                }
            }
        }
        return location;
    }

    // operationValueΪĿ�괰�ھ�����±�, 1��ʼ; С�ڵ���0����ȡ��ǰ���ڵľ��ֵ
    // ���ھ��ֵ����CDwindow-��ͷ, ������ΪԤ�ڽ���Ķ���
    private static String getTargetWindowHandle(WebDriver driver, String target) {
        String result;
        if (null != driver) {
            if (!ChangString.isInteger(target)) {
                result = windowHandleByTitle(driver, target);
            } else {
                int index = Integer.valueOf(target);
                result = windowHandleByIndex(driver, index);
            }
        } else {
            result = "��ȡ���ھ��ֵʧ�ܣ�WebDriverΪ��";
        }
        if (result.contains("��ȡ���ھ��ֵʧ��")) LogUtil.APP.error(result);
        else LogUtil.APP.info("��ȡ���ھ��ֵ�ɹ���Ŀ�괰�ھ��ֵΪ��" + result + "��");
        return result;
    }

    private static String windowHandleByTitle(WebDriver driver, String title) {
        String result = "";
        String original = driver.getWindowHandle();
        if (title.isEmpty()) {
            result = original;
        } else {
            Set<String> windowHandles = driver.getWindowHandles();
            for (String windowHandle : windowHandles) {
                driver.switchTo().window(windowHandle);
                if (title.equals(driver.getTitle())) {
                    result = windowHandle;
                    break;
                }
            }
            if (0 < windowHandles.size()) driver.switchTo().window(original);
        }
        result = result.isEmpty() ? "��ȡ���ھ��ֵʧ�ܣ���Ҫ��ȡ���ھ��ֵ�ı��⡾" + title + "��û���ҵ�" : "��ȡ����ֵ�ǡ�" + result + "��";
        return result;
    }

    private static String windowHandleByIndex(WebDriver driver, int index) {
        String result;
        try {
            List<String> windowHandles = new ArrayList<>(driver.getWindowHandles());
            if (index > windowHandles.size()) {
                result = "��ȡ���ھ��ֵʧ�ܣ���Ҫ��ȡ���ھ��ֵ���±꡾" + index + "�����ڵ�ǰ���ھ��������" + windowHandles.size() + "��";
            } else {
                if (0 >= index) result = "��ȡ����ֵ�ǡ�" + driver.getWindowHandle() + "��";
                else result = "��ȡ����ֵ�ǡ�" + windowHandles.get(index - 1) + "��";
            }
        } catch (IndexOutOfBoundsException e) {
            result = "��ȡ���ھ��ֵʧ�ܣ���Ҫ��ȡ���ھ��ֵ���±꡾" + index + "��Խ��";
        }
        return result;
    }

    // ��ȴ�30��, ÿ500������ѯһ��
    private static FluentWait<WebDriver> Wait(WebDriver driver) {
        return new FluentWait<>(driver).withTimeout(30 * 1000, MILLISECONDS).pollingEvery(500, MILLISECONDS);
    }

    private static ExpectedCondition<WebDriver> windowToBeAvailableAndSwitchToIt(final String nameOrHandleOrTitle) {
        return driver -> {
            try {
                if (null != driver)
                    return driver.switchTo().window(nameOrHandleOrTitle);
                else
                    return null;
            } catch (NoSuchWindowException windowWithNameOrHandleNotFound) {
                try {
                    return windowByTitle(driver, nameOrHandleOrTitle);
                } catch (NoSuchWindowException windowWithTitleNotFound) {
                    if (ChangString.isInteger(nameOrHandleOrTitle))
                        return windowByIndex(driver, Integer.valueOf(nameOrHandleOrTitle));
                    else
                        return null;
                }
            }
        };
    }

    private static WebDriver windowByTitle(WebDriver driver, String title) {
        String original = driver.getWindowHandle();
        Set<String> windowHandles = driver.getWindowHandles();
        for (String windowHandle : windowHandles) {
            driver.switchTo().window(windowHandle);
            if (title.equals(driver.getTitle())) {
                return driver;
            }
        }
        if (0 < windowHandles.size()) driver.switchTo().window(original);
        throw new NoSuchWindowException("Window with title[" + title + "] not found");
    }

    private static WebDriver windowByIndex(WebDriver driver, int index) {
        try {
            List<String> windowHandles = new ArrayList<>(driver.getWindowHandles());
            return driver.switchTo().window(windowHandles.get(index));
        } catch (IndexOutOfBoundsException windowWithIndexNotFound) {
            return null;
        }
    }

    private static String switchToTargetWindow(WebDriver driver, String target) {
        String result;
        try {
            if (null == Wait(driver).until(windowToBeAvailableAndSwitchToIt(target))) {
                result = "�л����ھ��ʧ�ܣ�δ�ҵ����ֵΪ��" + target + "���Ķ���";
                LogUtil.APP.error(result);
            } else {
                result = "�л����ھ���ɹ����ҵ����ֵΪ��" + target + "���Ķ���";
                LogUtil.APP.info(result);
            }
            return result;
        } catch (TimeoutException e) {
            result = "�л����ھ��ʧ�ܣ��ȴ���ʱ��δ�ҵ����ֵΪ��" + target + "���Ķ���";
            LogUtil.APP.error(result);
            return result;
        }
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}

