package luckyclient.execution.webdriver;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import luckyclient.execution.dispose.ChangString;
import luckyclient.execution.webdriver.ocr.Ocr;
import luckyclient.utils.LogUtil;

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

    public static String selectOperation(WebElement we, String operation, String operationValue) {
        String result = "";
        // 下拉框对象处理
        Select select = new Select(we);

        // 处理下拉框事件
        switch (operation) {
            case "selectbyvisibletext":
                select.selectByVisibleText(operationValue);
                result = "下拉框对象通过VisibleText属性选择...【VisibleText属性值:" + operationValue + "】";
                LogUtil.APP.info("下拉框对象通过VisibleText属性选择...【VisibleText属性值:{}】",operationValue);
                break;
            case "selectbyvalue":
                select.selectByValue(operationValue);
                result = "下拉框对象通过Value属性选择...【Value属性值:" + operationValue + "】";
                LogUtil.APP.info("下拉框对象通过Value属性选择...【Value属性值:{}】",operationValue);
                break;
            case "selectbyindex":
                select.selectByIndex(Integer.parseInt(operationValue));
                result = "下拉框对象通过Index属性选择...【Index属性值:" + operationValue + "】";
                LogUtil.APP.info("下拉框对象通过Index属性选择...【Index属性值:{}】",operationValue);
                break;
            case "isselect":
                result = "获取到的值是【" + we.isSelected() + "】";
                LogUtil.APP.info("判断对象是否已经被选择...【结果值:{}】",we.isSelected());
                break;
            default:
                break;
        }
        return result;
    }

    public static String getOperation(WebDriver wd, WebElement we, String operation, String value) {
        String result = "";
        // 获取对象处理
        switch (operation) {
            case "gettext":
                result = "获取到的值是【" + we.getText() + "】";
                LogUtil.APP.info("getText获取对象text属性...【text属性值:{}】",result);
                break; // 获取输入框内容
            case "gettagname":
                result = "获取到的值是【" + we.getTagName() + "】";
                LogUtil.APP.info("getTagName获取对象tagname属性...【tagname属性值:{}】",result);
                break;
            case "getvalue":
                result = "获取到的值是【" + we.getAttribute("value") + "】";
                LogUtil.APP.info("getAttribute获取对象【value】属性...【value属性值:{}】",result);
                break;
            case "getattribute":
                result = "获取到的值是【" + we.getAttribute(value) + "】";
                LogUtil.APP.info("getAttribute获取对象【{}】属性...【{}属性值:{}】",value,value,result);
                break;
            case "getcssvalue":
                result = "获取到的值是【" + we.getCssValue(value) + "】";
                LogUtil.APP.info("getCssValue获取对象【{}】属性...【{}属性值:{}】",value,value,result);
                break;
            case "getcaptcha":
                result = "获取到的值是【" + Ocr.getCAPTCHA(wd, we) + "】";
                LogUtil.APP.info("getcaptcha获取验证码...【验证码值:{}】",result);
                break;
            default:
                break;
        }
        return result;
    }

    public static String actionWeOperation(WebDriver wd, WebElement we, String operation, String operationValue, String property, String propertyValue) {
        String result = "";
        Actions action = new Actions(wd);
        // action处理
        switch (operation) {
            //鼠标左键点击
            case "mouselkclick":
                action.click(we).perform();
                result = "mouselkclick鼠标左键点击对象...【对象定位属性:" + property + "; 定位属性值:" + propertyValue + "】";
                LogUtil.APP.info("mouselkclick鼠标左键点击对象...【对象定位属性:{}; 定位属性值:{}】",property,propertyValue);
                break;
            case "mouserkclick":
                action.contextClick(we).perform();
                result = "mouserkclick鼠标右键点击对象...【对象定位属性:" + property + "; 定位属性值:" + propertyValue + "】";
                LogUtil.APP.info("mouserkclick鼠标右键点击对象...【对象定位属性:{}; 定位属性值:{}】",property,propertyValue);
                break;
            case "mousedclick":
                action.doubleClick(we).perform();
                result = "mousedclick鼠标双击对象...【对象定位属性:" + property + "; 定位属性值:" + propertyValue + "】";
                LogUtil.APP.info("mousedclick鼠标双击对象...【对象定位属性:{}; 定位属性值:{}】",property,propertyValue);
                break;
            case "mouseclickhold":
                action.clickAndHold(we).perform();
                result = "mouseclickhold鼠标点击对象后不释放...【对象定位属性:" + property + "; 定位属性值:" + propertyValue + "】";
                LogUtil.APP.info("mouseclickhold鼠标点击对象后不释放...【对象定位属性:{}; 定位属性值:{}】",property,propertyValue);
                break;
            case "mousedrag":
                int[] location = getLocationFromParam(operationValue);
//                String[] temp = operationValue.split(",", -1);
                action.dragAndDropBy(we, location[0], location[1]).perform();
                result = "mousedrag鼠标移动至对象相对坐标...【对象定位属性:" + property + "; 定位属性值:" + propertyValue + "; 相对坐标(x,y):" + location[0] + "," + location[1] + "】";
                LogUtil.APP.info("mousedrag鼠标移动至对象相对坐标...【对象定位属性:{}; 定位属性值:{}; 相对坐标(x,y):{},{}】",property,propertyValue,location[0],location[1]);
                break;
            case "mouseto":
                int[] location1 = getLocationFromParam(operationValue);
//                String[] temp1 = operationValue.split(",", -1);
                action.moveToElement(we, location1[0], location1[1]).perform();
                result = "mouseto鼠标移动至对象相对坐标...【对象定位属性:" + property + "; 定位属性值:" + propertyValue + "; 相对坐标(x,y):" + location1[0] + "," + location1[1] + "】";
                LogUtil.APP.info("mouseto鼠标移动至对象相对坐标...【对象定位属性:{}; 定位属性值:{}; 相对坐标(x,y):{},{}】",property,propertyValue,location1[0],location1[1]);
                break;
            case "mouserelease":
                action.release(we).perform();
                result = "mouserelease鼠标释放...";
                LogUtil.APP.info("mouserelease鼠标释放...");
                break;
            default:
                break;
        }
        return result;
    }

    public static String actionOperation(WebDriver wd, String operation, String operationValue) {
        String result = "";
        Actions action = new Actions(wd);
        // action处理
        switch (operation) {
            //鼠标左键点击
            case "mouselkclick":
                action.click().perform();
                result = "mouselkclick鼠标左键点击当前位置...";
                LogUtil.APP.info(result);
                break;
            case "mouserkclick":
                action.contextClick().perform();
                result = "mouserkclick鼠标右键点击当前位置...";
                LogUtil.APP.info(result);
                break;
            case "mousedclick":
                action.doubleClick().perform();
                result = "mousedclick鼠标双击当前位置...";
                LogUtil.APP.info(result);
                break;
            case "mouseclickhold":
                action.clickAndHold().perform();
                result = "mouseclickhold鼠标点击当前位置后不释放...";
                LogUtil.APP.info(result);
                break;
            case "mouseto":
                int[] location = getLocationFromParam(operationValue);
//                String[] temp1 = operationValue.split(",", -1);
                action.moveByOffset(location[0], location[1]).perform();
                result = "mouseto鼠标移动至对象相对坐标...坐标x：" + location[0] + " 坐标y：" + location[1];
                LogUtil.APP.info("mouseto鼠标移动至对象相对坐标...坐标x:{} 坐标y:{}",location[0],location[1]);
                break;
            case "mouserelease":
                action.release().perform();
                result = "mouserelease鼠标释放...";
                LogUtil.APP.info(result);
                break;
            case "mousekey":
                switch (operationValue) {
                    case "tab":
                        action.sendKeys(Keys.TAB).perform();
                        result = "键盘操作TAB键...";
                        LogUtil.APP.info(result);
                        break;
                    case "space":
                        action.sendKeys(Keys.SPACE).perform();
                        result = "键盘操作SPACE键...";
                        LogUtil.APP.info(result);
                        break;
                    case "ctrl":
                        action.sendKeys(Keys.CONTROL).perform();
                        result = "键盘操作CONTROL键...";
                        LogUtil.APP.info(result);
                        break;
                    case "shift":
                        action.sendKeys(Keys.SHIFT).perform();
                        result = "键盘操作SHIFT键...";
                        LogUtil.APP.info(result);
                        break;
                    case "enter":
                        action.sendKeys(Keys.ENTER).perform();
                        result = "键盘操作ENTER键...";
                        LogUtil.APP.info(result);
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

    public static String objectOperation(WebDriver wd, WebElement we, String operation, String operationValue, String property, String propertyValue) {
        String result = "";
        // 处理WebElement对象操作
        switch (operation) {
            case "click":
                we.click();
                result = "click点击对象...【对象定位属性:" + property + "; 定位属性值:" + propertyValue + "】";
                LogUtil.APP.info("click点击对象...【对象定位属性:{}; 定位属性值:{}】",property,propertyValue);
                break;
            case "sendkeys":
                we.sendKeys(operationValue);
                result = "sendKeys对象输入...【对象定位属性:" + property + "; 定位属性值:" + propertyValue + "; 操作值:" + operationValue + "】";
                LogUtil.APP.info("sendKeys对象输入...【对象定位属性:{}; 定位属性值:{}; 操作值:{}】",property,propertyValue,operationValue);
                break;
            case "clear":
                we.clear();
                result = "clear清空输入框...【对象定位属性:" + property + "; 定位属性值:" + propertyValue + "】";
                LogUtil.APP.info("clear清空输入框...【对象定位属性:{}; 定位属性值:{}】",property,propertyValue);
                break; // 清空输入框
            case "gotoframe":
                wd.switchTo().frame(we);
                result = "gotoframe切换Frame...【对象定位属性:" + property + "; 定位属性值:" + propertyValue + "】";
                LogUtil.APP.info("gotoframe切换Frame...【对象定位属性:{}; 定位属性值:{}】",property,propertyValue);
                break;
            case "isenabled":
                result = "获取到的值是【"+we.isEnabled()+"】";
                LogUtil.APP.info("获取到的值是【{}】",we.isEnabled());
                break;
            case "isdisplayed":
                result = "获取到的值是【" + we.isDisplayed() + "】";
                LogUtil.APP.info("获取到的值是【{}】", we.isDisplayed());
                break;
            case "exjsob":
                JavascriptExecutor jse = (JavascriptExecutor) wd;
                Object obj = jse.executeScript(operationValue, we);
                if (null != obj) {
                    String tmp = obj.toString();
                    result = (100 < tmp.length()) ? tmp.substring(0, 100) + "..." : tmp;
                    result = "获取到的值是【" + result + "】";
                    LogUtil.APP.info("执行JS...【{}】，返回的结果为:{}",operationValue,result);
                } else {
                    result = "执行JS...【" + operationValue + "】";
                    LogUtil.APP.info("执行JS...【{}】",operationValue);
                }
                break;
            case "scrollto":
                Point location = we.getLocation();
                ((JavascriptExecutor) wd).executeScript("window.scrollTo(" + location.getX() + ", " + location.getY() + ")");
                result = "滚动到目标对象...【对象定位属性:" + property + "; 定位属性值:" + propertyValue + "; 对象坐标(x,y):" + location.getX() + "," + location.getY() + "】";
                LogUtil.APP.info("滚动到目标对象...【对象定位属性:{}; 定位属性值:{}; 对象坐标(x,y):{},{}】",property,propertyValue,location.getX(),location.getY());
                break;
            case "scrollintoview":
                // 此方法可以用执行js命令来代替
                ((JavascriptExecutor) wd).executeScript("arguments[0].scrollIntoView(" + operationValue + ")", we);
                result = "将目标对象滚动到可视...【对象定位属性:" + property + "; 定位属性值:" + propertyValue + "】";
                LogUtil.APP.info("将目标对象滚动到可视...【对象定位属性:{}; 定位属性值:{}】",property,propertyValue);
                break;
            default:
                break;
        }
        return result;
    }

    public static String alertOperation(WebDriver wd, String operation) {
        String result = "";
        Alert alert = wd.switchTo().alert();
        switch (operation) {
            case "alertaccept":
                alert.accept();
                result = "弹出框对象点击同意...";
                LogUtil.APP.info(result);
                break;
            case "alertdismiss":
                alert.dismiss();
                result = "弹出框对象点击取消...";
                LogUtil.APP.info(result);
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

    public static String driverOperation(WebDriver wd, String operation, String operationValue) {
        String result = "";
        // 处理页面对象操作
        switch (operation) {
            case "open":
                wd.get(operationValue);
                result = "Open页面...【" + operationValue + "】";
                LogUtil.APP.info("Open页面...【{}】",operationValue);
                break;
            case "addcookie":
                List<Cookie> cookies = buildCookie(operationValue);
                if (null != cookies && cookies.size() > 0) {
                    for (Cookie cookie : cookies) {
                        wd.manage().addCookie(cookie);
                        LogUtil.APP.info("添加Cookie:【{}】成功！",cookie);
                    }
                }
                result = "添加cookie...【" + operationValue + "】";
                break;
            case "exjs":
                JavascriptExecutor jse = (JavascriptExecutor) wd;
                Object obj = jse.executeScript(operationValue);
                if (null != obj) {
                    String tmp = obj.toString();
                    result = (100 < tmp.length()) ? tmp.substring(0, 100) + "..." : tmp;
                    result = "获取到的值是【" + result + "】";
                    LogUtil.APP.info("执行JS...【{}】，返回的结果为:{}",operationValue,result);
                } else {
                    result = "执行JS...【" + operationValue + "】";
                    LogUtil.APP.info("{}，执行JS返回null或没有返回",result);
                }
                break;
            case "gotodefaultcontent":
                wd.switchTo().defaultContent();
                result = "gotodefaultcontent切换至默认页面位置...";
                LogUtil.APP.info(result);
                break;
            case "gotoparentframe":
                wd.switchTo().parentFrame();
                result = "gotoparentframe切换至上一级frame位置...";
                LogUtil.APP.info(result);
                break;
            case "gettitle":
                result = "获取到的值是【" + wd.getTitle() + "】";
                LogUtil.APP.info("获取页面Title...【{}】",wd.getTitle());
                break;
            case "getwindowhandle":
                result = getTargetWindowHandle(wd, operationValue);
                break;
            case "gotowindow":
                result = switchToTargetWindow(wd, operationValue);
                break;
            case "switchtowindow":
                switchToWindow(wd);
                break;
            case "windowsetsize":
                manageWindowSetSize(wd,operationValue);
                break;
            case "closewindow":
                wd.close();
                result = "关闭当前浏览器窗口...";
                break;
            case "pagerefresh":
                wd.navigate().refresh();
                result = "刷新当前浏览器窗口...";
                break;
            case "pageforward":
                wd.navigate().forward();
                result = "前进当前浏览器窗口...";
                break;
            case "pageback":
                wd.navigate().back();
                result = "回退当前浏览器窗口...";
                break;
            case "timeout":
                try {
                    // 设置页面加载最大时长30秒
                    wd.manage().timeouts().pageLoadTimeout(Integer.parseInt(operationValue), TimeUnit.SECONDS);
                    // 设置元素出现最大时长30秒
                    wd.manage().timeouts().implicitlyWait(Integer.parseInt(operationValue), TimeUnit.SECONDS);
                    result = "当前任务操作等待【" + operationValue + "】秒...";
                    LogUtil.APP.info("当前任务操作等待【{}】秒...",operationValue);
                    break;
                } catch (NumberFormatException e) {
                    LogUtil.APP.error("等待时间转换出现异常！",e);
                    result = "【等待时间转换出错，请检查参数】";
                    break;
                }
            default:
                break;
        }
        return result;
    }

    private static List<Cookie> buildCookie(String operationValue) {
        if (StringUtils.isBlank(operationValue)) {
        	LogUtil.APP.info("获取Cookie值：operationValue为空！");
            return null;
        }
        try {
            JSONArray objects = JSON.parseArray(operationValue);
            if (null == objects) {
            	LogUtil.APP.info("格式化Cookie字符串成JSONArray，对象为空！");
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
                // TODO 缓存多长时间，算出失效时间,单位：秒
                //String expire = jsonObject.getString("expire");
                if (!StringUtils.isBlank(name) && !StringUtils.isBlank(val)) {
                    Cookie cookie = new Cookie(name, val, domain, path, null);
                	LogUtil.APP.info("解析Cookie成功：【{}】",cookie);
                    result.add(cookie);
                }else{
                    LogUtil.APP.warn("cookie:{} 错误,name或是val为空！",jsonObject);
                }
            }
            return result;
        } catch (Exception e) {
            LogUtil.APP.error("格式化Cookie对象出错，请检查您的格式是否正确！【{}】",operationValue,e);
            return null;
        }
    }

    private static int[] getLocationFromParam(String param) {
        int[] location = {0, 0};
        if (null == param || param.trim().isEmpty()) {
            return location;
        } else {
            // 不包含分隔符
            if (! param.contains(",")) {
                location[0] = Integer.parseInt(param.trim());
            } else {
                String[] tmp = param.split(",", 2);
                for (int i = 0; i < 2; i++) {
                    if (! tmp[i].trim().isEmpty()){
                    	location[i] = Integer.parseInt(tmp[i].trim());
                    } 
                }
            }
        }
        return location;
    }

    /**
     * operationValue为目标窗口句柄的下标, 1开始; 小于等于0即获取当前窗口的句柄值
     * 窗口句柄值都已CDwindow-开头, 可以作为预期结果的断言
     * @param driver 驱动
     * @param target 句柄字符串
     * @return 返回获取结果
     * @author Seagull
     * @date 2019年8月9日
     */
    private static String getTargetWindowHandle(WebDriver driver, String target) {
        String result;
        if (null != driver) {
            if (!ChangString.isInteger(target)) {
                result = windowHandleByTitle(driver, target);
            } else {
                int index = Integer.parseInt(target);
                result = windowHandleByIndex(driver, index);
            }
        } else {
            result = "获取窗口句柄值失败，WebDriver为空";
        }
        if (result.contains("获取窗口句柄值失败")){
        	LogUtil.APP.warn(result);
        } else {
        	LogUtil.APP.info("获取窗口句柄值成功，目标窗口句柄值为【{}】",result);
        }
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
            if (0 < windowHandles.size()){
            	driver.switchTo().window(original);
            } 
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
                if (0 >= index){
                	result = "获取到的值是【" + driver.getWindowHandle() + "】";
                } else{
                	result = "获取到的值是【" + windowHandles.get(index - 1) + "】";
                } 
            }
        } catch (IndexOutOfBoundsException e) {
        	LogUtil.APP.error("获取窗口句柄值出现异常，需要获取窗口句柄值的下标【{}】越界",index,e);
            result = "获取窗口句柄值失败，需要获取窗口句柄值的下标【" + index + "】越界";
        }
        return result;
    }

    // 最长等待30秒, 每500毫秒轮询一次
    private static FluentWait<WebDriver> wait(WebDriver driver) {
        return new FluentWait<>(driver).withTimeout(Duration.ofSeconds(30)).pollingEvery(Duration.ofMillis(500));
    }

    private static ExpectedCondition<WebDriver> windowToBeAvailableAndSwitchToIt(final String nameOrHandleOrTitle) {
        return driver -> {
            try {
                if (null != driver){
                	return driver.switchTo().window(nameOrHandleOrTitle);
                } else{
                	return null;
                }
                    
            } catch (NoSuchWindowException windowWithNameOrHandleNotFound) {
                try {
                    return windowByTitle(driver, nameOrHandleOrTitle);
                } catch (NoSuchWindowException windowWithTitleNotFound) {
                    if (ChangString.isInteger(nameOrHandleOrTitle)){
                    	return windowByIndex(driver, Integer.parseInt(nameOrHandleOrTitle));
                    } else{
                    	return null;
                    }                       
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
        if (0 < windowHandles.size()){
        	driver.switchTo().window(original);
        } 
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
            if (null == wait(driver).until(windowToBeAvailableAndSwitchToIt(target))) {
                result = "步骤执行失败：切换窗口句柄失败，未找到句柄值为【" + target + "】的对象";
                LogUtil.APP.warn("切换窗口句柄失败，未找到句柄值为【{}】的对象",target);
            } else {
                result = "切换窗口句柄成功，找到句柄值为【" + target + "】的对象";
                LogUtil.APP.info("切换窗口句柄成功，找到句柄值为【{}】的对象",target);
            }
            return result;
        } catch (TimeoutException e) {
            result = "步骤执行失败：切换窗口句柄失败，等待超时，未找到句柄值为【" + target + "】的对象";
            LogUtil.APP.error("切换窗口句柄失败，等待超时，未找到句柄值为【{}】的对象",target,e);
            return result;
        }
    }

    //新增修改点 切换句柄
    private static void switchToWindow(WebDriver driver) {
        String current_window_handle=driver.getWindowHandle();
        LogUtil.APP.info("当前句柄："+current_window_handle);
        driver.close();
        LogUtil.APP.info("关闭当前句柄："+current_window_handle);
        for (String s : driver.getWindowHandles()) {
            if (!s.equals(current_window_handle)) {
                driver.switchTo().window(s);
            }
        }
    }

    //新增修改点 设置浏览器窗口大小
    private static void manageWindowSetSize(WebDriver driver,String operationValue) {
        if(operationValue.contains(",")){
            String[] index=operationValue.split(",");

            if (index.length==2){
                driver.manage().window().setSize(new Dimension(Integer.parseInt(index[0].trim()), Integer.parseInt(index[1].trim())));
            }else {
                LogUtil.APP.info("参数格式不正确，参数格式为：xxx,xxx");
            }
        }else {
            LogUtil.APP.info("参数格式不正确，参数格式为：xxx,xxx");
        }
    }

}

