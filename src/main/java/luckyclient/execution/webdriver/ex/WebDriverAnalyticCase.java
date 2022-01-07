package luckyclient.execution.webdriver.ex;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import luckyclient.execution.dispose.ChangString;
import luckyclient.remote.api.serverOperation;
import luckyclient.remote.entity.ProjectCase;
import luckyclient.remote.entity.ProjectCaseSteps;
import luckyclient.utils.LogUtil;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 seagull1985
 * =================================================================
 * 
 * @ClassName: AnalyticCase
 * @Description: 解析单个用例中描述部分的脚本 @author： seagull
 * @date 2016年9月18日
 * 
 */
public class WebDriverAnalyticCase {
	// private static String splitFlag = "\\|";

	/**
	 * Web UI类型的步骤解析
	 * 
	 * @param projectcase 用例对象
	 * @param step 用例步骤对象
	 * @param taskid 任务ID
	 * @param caselog 日志对象
	 * @return 返回解析结果MAP
	 * @author Seagull
	 * @date 2019年1月17日
	 */
	public static Map<String, String> analyticCaseStep(ProjectCase projectcase, ProjectCaseSteps step, String taskid,
			serverOperation caselog, Map<String, String> variable) {
		Map<String, String> params = new HashMap<>(0);

		String resultstr;
		try {
			// 处理值传递
			String path = ChangString.changparams(step.getStepPath(), variable, "包路径|定位路径");
			if (null != path && path.contains("=")) {
				String property = path.substring(0, path.indexOf("=")).trim();
				String propertyValue = path.substring(path.indexOf("=") + 1).trim();
				// set属性
				params.put("property", property.toLowerCase());
				// set属性值
				params.put("property_value", propertyValue);
				LogUtil.APP.info("对象属性解析结果：property:{};  property_value:{}", property, propertyValue);
			}
			// set操作方法,处理值传递
			String operation = ChangString.changparams(step.getStepOperation().toLowerCase(), variable, "操作");
			params.put("operation", operation);
			// set属性值,处理值传递
			String operationValue = ChangString.changparams(step.getStepParameters(), variable, "操作参数");
			if (StringUtils.isNotEmpty(operationValue)) {
				params.put("operation_value", operationValue);
			}
			LogUtil.APP.info("对象操作解析结果：operation:{};  operation_value:{}", operation,operationValue);
			// 获取预期结果字符串
			resultstr = step.getExpectedResult();

			// set预期结果
			if (null == resultstr || "".equals(resultstr)) {
				params.put("ExpectedResults", "");
			} else {
				String expectedResults = subComment(resultstr);

				// 处理check字段
				if (expectedResults.toLowerCase().startsWith("check(")) {
					expectedResults = expectedResults.replace("Check(", "check(");
					params.put("checkproperty", expectedResults.substring(expectedResults.indexOf("check(") + 6,
							expectedResults.indexOf("=")));
					params.put("checkproperty_value", expectedResults.substring(expectedResults.indexOf("=") + 1,
							expectedResults.lastIndexOf(")")));
				}
				//处理值传递
	            expectedResults = ChangString.changparams(expectedResults, variable, "预期结果");
				params.put("ExpectedResults", expectedResults);
				LogUtil.APP.info("预期结果解析：ExpectedResults:{}", expectedResults);
			}

			LogUtil.APP.info("用例编号:{} 第{}步，解析自动化用例步骤脚本完成！", projectcase.getCaseSign(), step.getStepSerialNumber());
			if (null != caselog) {
				caselog.insertTaskCaseLog(taskid, projectcase.getCaseId(),
						"步骤编号：" + step.getStepSerialNumber() + " 解析自动化用例步骤脚本完成！", "info",
						String.valueOf(step.getStepSerialNumber()), "");
			}
		} catch (Exception e) {
			LogUtil.APP.error("用例编号:{} 第{}步，解析自动化用例步骤脚本出现异常！", projectcase.getCaseSign(), step.getStepSerialNumber(),
					e);
			if (null != caselog) {
				caselog.insertTaskCaseLog(taskid, projectcase.getCaseId(),
						"步骤编号：" + step.getStepSerialNumber() + " 解析自动化用例步骤脚本出错！", "error",
						String.valueOf(step.getStepSerialNumber()), "");
			}
			params.put("exception", "用例编号：" + projectcase.getCaseSign() + "|解析异常,用例步骤为空或是用例脚本错误！");
			return params;
		}
		return params;
	}

	public static Map<String, String> analyticCaseStep(ProjectCase projectcase, ProjectCaseSteps step, Map<String, String> variable) {
		Map<String, String> params = new HashMap<>(0);

		String resultstr;
		try {
			// 处理值传递
			String path = ChangString.changparams(step.getStepPath(), variable, "包路径|定位路径");
			if (null != path && path.contains("=")) {
				String property = path.substring(0, path.indexOf("=")).trim();
				String propertyValue = path.substring(path.indexOf("=") + 1).trim();
				// set属性
				params.put("property", property.toLowerCase());
				// set属性值
				params.put("property_value", propertyValue);
				LogUtil.APP.info("对象属性解析结果：property:{};  property_value:{}", property, propertyValue);
			}
			// set操作方法,处理值传递
			String operation = ChangString.changparams(step.getStepOperation().toLowerCase(), variable, "操作");
			params.put("operation", operation);
			// set属性值,处理值传递
			String operationValue = ChangString.changparams(step.getStepParameters(), variable, "操作参数");
			if (StringUtils.isNotEmpty(operationValue)) {
				params.put("operation_value", operationValue);
			}
			LogUtil.APP.info("对象操作解析结果：operation:{};  operation_value:{}", operation,operationValue);
			// 获取预期结果字符串
			resultstr = step.getExpectedResult();

			// set预期结果
			if (null == resultstr || "".equals(resultstr)) {
				params.put("ExpectedResults", "");
			} else {
				String expectedResults = subComment(resultstr);

				// 处理check字段
				if (expectedResults.toLowerCase().startsWith("check(")) {
					expectedResults = expectedResults.replace("Check(", "check(");
					params.put("checkproperty", expectedResults.substring(expectedResults.indexOf("check(") + 6,
							expectedResults.indexOf("=")));
					params.put("checkproperty_value", expectedResults.substring(expectedResults.indexOf("=") + 1,
							expectedResults.lastIndexOf(")")));
				}
				//处理值传递
				expectedResults = ChangString.changparams(expectedResults, variable, "预期结果");
				params.put("ExpectedResults", expectedResults);
				LogUtil.APP.info("预期结果解析：ExpectedResults:{}", expectedResults);
			}

			LogUtil.APP.info("用例编号:{} 第{}步，解析自动化用例步骤脚本完成！", projectcase.getCaseSign(), step.getStepSerialNumber());
//			if (null != caselog) {
//				caselog.insertTaskCaseLog(taskid, projectcase.getCaseId(),
//						"步骤编号：" + step.getStepSerialNumber() + " 解析自动化用例步骤脚本完成！", "info",
//						String.valueOf(step.getStepSerialNumber()), "");
//			}
		} catch (Exception e) {
			LogUtil.APP.error("用例编号:{} 第{}步，解析自动化用例步骤脚本出现异常！", projectcase.getCaseSign(), step.getStepSerialNumber(),
					e);
//			if (null != caselog) {
//				caselog.insertTaskCaseLog(taskid, projectcase.getCaseId(),
//						"步骤编号：" + step.getStepSerialNumber() + " 解析自动化用例步骤脚本出错！", "error",
//						String.valueOf(step.getStepSerialNumber()), "");
//			}
			params.put("exception", "用例编号：" + projectcase.getCaseSign() + "|解析异常,用例步骤为空或是用例脚本错误！");
			return params;
		}
		return params;
	}

	private static String subComment(String htmlStr) {
		// 定义script的正则表达式
		String regExScript = "<script[^>]*?>[\\s\\S]*?</script>";
		// 定义style的正则表达式
		String regExStyle = "<style[^>]*?>[\\s\\S]*?</style>";
		// 定义HTML标签的正则表达式
		String regExHtml = "<[^>]+>";
		// 定义空格回车换行符
		String regExSpace = "[\t\r\n]";

		String scriptstr;
		if (htmlStr != null) {
			Pattern pScript = Pattern.compile(regExScript, Pattern.CASE_INSENSITIVE);
			Matcher mScript = pScript.matcher(htmlStr);
			// 过滤script标签
			htmlStr = mScript.replaceAll("");

			Pattern pStyle = Pattern.compile(regExStyle, Pattern.CASE_INSENSITIVE);
			Matcher mStyle = pStyle.matcher(htmlStr);
			// 过滤style标签
			htmlStr = mStyle.replaceAll("");

			Pattern pHtml = Pattern.compile(regExHtml, Pattern.CASE_INSENSITIVE);
			Matcher mHtml = pHtml.matcher(htmlStr);
			// 过滤html标签
			htmlStr = mHtml.replaceAll("");

			Pattern pSpace = Pattern.compile(regExSpace, Pattern.CASE_INSENSITIVE);
			Matcher mSpace = pSpace.matcher(htmlStr);
			// 过滤空格回车标签
			htmlStr = mSpace.replaceAll("");

		}
		assert htmlStr != null;
		if (htmlStr.contains("/*") && htmlStr.contains("*/")) {
			String commentstr = htmlStr.substring(htmlStr.trim().indexOf("/*"), htmlStr.indexOf("*/") + 2);
			// 去注释
			scriptstr = htmlStr.replace(commentstr, "");
		} else {
			scriptstr = htmlStr;
		}
		// 去掉字符串前后的空格
		scriptstr = trimInnerSpaceStr(scriptstr);
		// 替换空格转义
		scriptstr = scriptstr.replaceAll("&nbsp;", " ");
		// 转义双引号
		scriptstr = scriptstr.replaceAll("&quot;", "\"");
		// 转义单引号
		scriptstr = scriptstr.replaceAll("&#39;", "'");
		// 转义链接符
		scriptstr = scriptstr.replaceAll("&amp;", "&");
		scriptstr = scriptstr.replaceAll("&lt;", "<");
		scriptstr = scriptstr.replaceAll("&gt;", ">");

		return scriptstr;
	}

	/***
	 * 去掉字符串前后的空格，中间的空格保留
	 * 
	 * @param str 原始字符串
	 * @return 返回去除结果
	 */
	public static String trimInnerSpaceStr(String str) {
		str = str.trim();
		while (str.startsWith(" ")) {
			str = str.substring(1).trim();
		}
		while (str.startsWith("&nbsp;")) {
			str = str.substring(6).trim();
		}
		while (str.endsWith(" ")) {
			str = str.substring(0, str.length() - 1).trim();
		}
		while (str.endsWith("&nbsp;")) {
			str = str.substring(0, str.length() - 6).trim();
		}
		return str;
	}

}
