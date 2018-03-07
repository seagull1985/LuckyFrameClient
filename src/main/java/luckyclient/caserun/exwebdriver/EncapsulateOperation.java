package luckyclient.caserun.exwebdriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import luckyclient.publicclass.ChangString;
import luckyclient.publicclass.LogUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;

import luckyclient.caserun.exwebdriver.ocr.Ocr;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 *
 * @author： seagull
 * @date 2017年12月1日 上午9:29:40
 */
public class EncapsulateOperation {

    public static String selectOperation(WebElement we, String operation, String operationValue) throws Exception {
        String result = "";
        // 下拉框对象处理
        Select select = new Select(we);

        // 处理下拉框事件
        switch (operation) {
            case "selectbyvisibletext":
                select.selectByVisibleText(operationValue);
                result = "下拉框对象通过VisibleText属性选择...【VisibleText属性值:" + operationValue + "】";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "selectbyvalue":
                select.selectByValue(operationValue);
                result = "下拉框对象通过Value属性选择...【Value属性值:" + operationValue + "】";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "selectbyindex":
                select.selectByIndex(Integer.valueOf(operationValue));
                result = "下拉框对象通过Index属性选择...【Index属性值:" + operationValue + "】";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "isselect":
                result = "获取到的值是【" + we.isSelected() + "】";
                luckyclient.publicclass.LogUtil.APP.info("判断对象是否已经被选择...【结果值:" + we.isSelected() + "】");
                break;
            default:
                break;
        }
        return result;
    }

    public static String getOperation(WebDriver wd, WebElement we, String operation, String value) throws Exception {
        String result = "";
        // 获取对象处理
        switch (operation) {
            case "gettext":
                result = "获取到的值是【" + we.getText() + "】";
                luckyclient.publicclass.LogUtil.APP.info("getText获取对象text属性...【text属性值:" + result + "】");
                break; // 获取输入框内容
            case "gettagname":
                result = "获取到的值是【" + we.getTagName() + "】";
                luckyclient.publicclass.LogUtil.APP.info("getTagName获取对象tagname属性...【tagname属性值:" + result + "】");
                break;
            case "getattribute":
                result = "获取到的值是【" + we.getAttribute(value) + "】";
                luckyclient.publicclass.LogUtil.APP.info("getAttribute获取对象【" + value + "】属性...【" + value + "属性值:" + result + "】");
                break;
            case "getcssvalue":
                result = "获取到的值是【" + we.getCssValue(value) + "】";
                luckyclient.publicclass.LogUtil.APP.info("getCssValue获取对象【" + value + "】属性...【" + value + "属性值:" + result + "】");
                break;
            case "getcaptcha":
                result = "获取到的值是【" + Ocr.getCAPTCHA(wd, we) + "】";
                luckyclient.publicclass.LogUtil.APP.info("getcaptcha获取验证码...【验证码值:" + result + "】");
                break;
            default:
                break;
        }
        return result;
    }

    public static String actionWeOperation(WebDriver wd, WebElement we, String operation, String operationValue, String property, String propertyValue) throws Exception {
        String result = "";
        Actions action = new Actions(wd);
        // action处理
        switch (operation) {
            //鼠标左键点击
            case "mouselkclick":
                action.click(we).perform();
                result = "mouselkclick鼠标左键点击对象...【对象定位属性:" + property + "; 定位属性值:" + propertyValue + "】";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "mouserkclick":
                action.contextClick(we).perform();
                result = "mouserkclick鼠标右键点击对象...【对象定位属性:" + property + "; 定位属性值:" + propertyValue + "】";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "mousedclick":
                action.doubleClick(we).perform();
                result = "mousedclick鼠标双击对象...【对象定位属性:" + property + "; 定位属性值:" + propertyValue + "】";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "mouseclickhold":
                action.clickAndHold(we).perform();
                result = "mouseclickhold鼠标点击对象后不释放...【对象定位属性:" + property + "; 定位属性值:" + propertyValue + "】";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "mousedrag":
                String[] temp = operationValue.split(",", -1);
                action.dragAndDropBy(we, Integer.valueOf(temp[0]), Integer.valueOf(temp[1])).perform();
                result = "mousedrag鼠标移动至对象相对坐标...【对象定位属性:" + property + "; 定位属性值:" + propertyValue + "】  坐标x：" + Integer.valueOf(temp[0]) + " 坐标y：" + Integer.valueOf(temp[1]);
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "mouseto":
                String[] temp1 = operationValue.split(",", -1);
                action.moveToElement(we, Integer.valueOf(temp1[0]), Integer.valueOf(temp1[1])).perform();
                result = "mouseto鼠标移动至对象相对坐标...【对象定位属性:" + property + "; 定位属性值:" + propertyValue + "】  坐标x：" + Integer.valueOf(temp1[0]) + " 坐标y：" + Integer.valueOf(temp1[1]);
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "mouserelease":
                action.release(we).perform();
                result = "mouserelease鼠标释放...";
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
        // action处理
        switch (operation) {
            //鼠标左键点击
            case "mouselkclick":
                action.click().perform();
                result = "mouselkclick鼠标左键点击当前位置...";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "mouserkclick":
                action.contextClick().perform();
                result = "mouserkclick鼠标右键点击当前位置...";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "mousedclick":
                action.doubleClick().perform();
                result = "mousedclick鼠标双击当前位置...";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "mouseclickhold":
                action.clickAndHold().perform();
                result = "mouseclickhold鼠标点击当前位置后不释放...";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "mouseto":
                String[] temp1 = operationValue.split(",", -1);
                action.moveByOffset(Integer.valueOf(temp1[0]), Integer.valueOf(temp1[1])).perform();
                result = "mouseto鼠标移动至对象相对坐标...坐标x：" + Integer.valueOf(temp1[0]) + " 坐标y：" + Integer.valueOf(temp1[1]);
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "mouserelease":
                action.release().perform();
                result = "mouserelease鼠标释放...";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "mousekey":
                switch (operationValue) {
                    case "tab":
                        action.sendKeys(Keys.TAB).perform();
                        result = "键盘操作TAB键...";
                        luckyclient.publicclass.LogUtil.APP.info(result);
                        break;
                    case "space":
                        action.sendKeys(Keys.SPACE).perform();
                        result = "键盘操作SPACE键...";
                        luckyclient.publicclass.LogUtil.APP.info(result);
                        break;
                    case "ctrl":
                        action.sendKeys(Keys.CONTROL).perform();
                        result = "键盘操作CONTROL键...";
                        luckyclient.publicclass.LogUtil.APP.info(result);
                        break;
                    case "shift":
                        action.sendKeys(Keys.SHIFT).perform();
                        result = "键盘操作SHIFT键...";
                        luckyclient.publicclass.LogUtil.APP.info(result);
                        break;
                    case "enter":
                        action.sendKeys(Keys.ENTER).perform();
                        result = "键盘操作SHIFT键...";
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
        // 处理WebElement对象操作
        switch (operation) {
            case "click":
                we.click();
                result = "click点击对象...【对象定位属性:"+property+"; 定位属性值:"+propertyValue+"】";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "sendkeys":
                we.sendKeys(operationValue);
                result = "sendKeys对象输入...【对象定位属性:"+property+"; 定位属性值:"+propertyValue+"; 操作值:"+operationValue+"】";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "clear":
                we.clear();
                result = "clear清空输入框...【对象定位属性:"+property+"; 定位属性值:"+propertyValue+"】";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break; // 清空输入框
            case "gotoframe":
                wd.switchTo().frame(we);
                result = "gotoframe切换Frame...【对象定位属性:"+property+"; 定位属性值:"+propertyValue+"】";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "isenabled":
                result = "获取到的值是【"+we.isEnabled()+"】";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "isdisplayed":
                result = "获取到的值是【"+we.isDisplayed()+"】";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "exjsob":
                JavascriptExecutor jse = (JavascriptExecutor) wd;
                jse.executeScript(operationValue, we);
                result = "执行JS...【"+operationValue+"】";
                luckyclient.publicclass.LogUtil.APP.info(result);
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
                result = "弹出框对象点击同意...";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "alertdismiss":
                alert.dismiss();
                result = "弹出框对象点击取消...";
                luckyclient.publicclass.LogUtil.APP.info(result);
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

    public static String driverOperation(WebDriver wd, String operation, String operationValue) throws Exception {
        String result = "";
        // 处理页面对象操作
        switch (operation) {
            case "open":
                wd.get(operationValue);
                result = "Open页面...【"+operationValue+"】";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "exjs":
                JavascriptExecutor jse = (JavascriptExecutor) wd;
                jse.executeScript(operationValue);
                result = "执行JS...【"+operationValue+"】";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "gotodefaultcontent":
                wd.switchTo().defaultContent();
                result = "gotodefaultcontent切换至默认页面位置...";
                luckyclient.publicclass.LogUtil.APP.info(result);
                break;
            case "gettitle":
                result = "获取到的值是【"+wd.getTitle()+"】";
                luckyclient.publicclass.LogUtil.APP.info("获取页面Title...【"+wd.getTitle()+"】");
                break;
            case "getwindowhandle":
                result = getTargetWindowHandle(wd, operationValue);
                break;
            case "gotowindow":
                result = switchToTargetWindow(wd, operationValue);
                break;
            case "timeout":
                try {
                    // 设置页面加载最大时长30秒
                    wd.manage().timeouts().pageLoadTimeout(Integer.valueOf(operationValue), TimeUnit.SECONDS);
                    // 设置元素出现最大时长30秒
                    wd.manage().timeouts().implicitlyWait(Integer.valueOf(operationValue), TimeUnit.SECONDS);
                    result = "当前任务操作等待【"+operationValue+"】秒...";
                    luckyclient.publicclass.LogUtil.APP.info(result);
                    break;
                } catch (NumberFormatException e) {
                    luckyclient.publicclass.LogUtil.APP.error("等待时间转换出错！");
                    e.printStackTrace();
                    result = "【等待时间转换出错，请检查参数】";
                    break;
                }
            default:
                break;
        }
        return result;
    }

    // operationValue为目标窗口句柄的下标, 1开始; 小于等于0即获取当前窗口的句柄值
    // 窗口句柄值都已CDwindow-开头, 可以作为预期结果的断言
    private static String getTargetWindowHandle(WebDriver driver, String target) {
        String result;
        if (null != driver) {
            if (! ChangString.isInteger(target)) {
                result = windowHandleByTitle(driver, target);
            } else {
                int index = Integer.valueOf(target);
                result = windowHandleByIndex(driver, index);
            }
        } else {
            result = "获取窗口句柄值失败，WebDriver为空";
        }
        if (result.contains("获取窗口句柄值失败")) LogUtil.APP.error(result);
        else LogUtil.APP.info("获取窗口句柄值成功，目标窗口句柄值为【" + result + "】");
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
        result = result.isEmpty() ? "获取窗口句柄值失败，需要获取窗口句柄值的标题【" + title + "】没有找到" : "获取到的值是【" + result + "】";
        return result;
    }

    private static String windowHandleByIndex(WebDriver driver, int index) {
        String result;
        try {
            List<String> windowHandles = new ArrayList<>(driver.getWindowHandles());
            if (index > windowHandles.size()) {
                result = "获取窗口句柄值失败，需要获取窗口句柄值的下标【" + index + "】大于当前窗口句柄总数【" + windowHandles.size() + "】";
            } else {
                if (0 >= index) result = "获取到的值是【" + driver.getWindowHandle() + "】";
                else result = "获取到的值是【" + windowHandles.get(index - 1) + "】";
            }
        } catch (IndexOutOfBoundsException e) {
            result = "获取窗口句柄值失败，需要获取窗口句柄值的下标【" + index + "】越界";
        }
        return result;
    }

    // 最长等待30秒, 每500毫秒轮询一次
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
                result = "切换窗口句柄失败，未找到句柄值为【" + target + "】的对象";
                LogUtil.APP.error(result);
            } else {
                result = "切换窗口句柄成功，找到句柄值为【" + target + "】的对象";
                LogUtil.APP.info(result);
            }
            return result;
        } catch (TimeoutException e) {
            result = "切换窗口句柄失败，等待超时，未找到句柄值为【" + target + "】的对象";
            LogUtil.APP.error(result);
            return result;
        }
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
