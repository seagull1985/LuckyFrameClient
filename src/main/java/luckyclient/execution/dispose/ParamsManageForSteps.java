package luckyclient.execution.dispose;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import luckyclient.utils.LogUtil;

/**
 * 对内置参数进行处理
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 seagull
 * =================================================================
 * @author Seagull
 * @date 2019年1月15日
 */
public class ParamsManageForSteps {
	public static Map<String, String> GLOBAL_VARIABLE = new HashMap<>(0);
	/**
	 * 进内置参数管理
	 * @param params 内置参数关键字
	 * @return 返回参数处理结果
	 * @author Seagull
	 * @date 2019年1月15日
	 */
	public static String paramsManage(String params) {
		if(null==params){
			return params;
		}
		ParamsManageForSteps pmfs = new ParamsManageForSteps();
		params = pmfs.replaceRandomInt(params);
		params = pmfs.replaceTimeNow(params);
		return params;
	}

	/**
	 * 内置参数生成替换(生成随机整数)
	 * @param params 参数关键字
	 * @return 返回随机数
	 * @author Seagull
	 * @date 2019年1月15日
	 */
	private String replaceRandomInt(String params) {
		try {
			String str="(?i)@\\{random\\[(\\d+)]\\[(\\d+)]}";
			Pattern pattern = Pattern.compile(str);
			Matcher m = pattern.matcher(params);
			while (m.find()) {
				String matcherstr = m.group(0);
				int startnum = Integer
						.parseInt(matcherstr.substring(matcherstr.indexOf("[") + 1, matcherstr.indexOf("]")).trim());
				int endnum = Integer
						.parseInt(matcherstr.substring(matcherstr.lastIndexOf("[") + 1, matcherstr.lastIndexOf("]")).trim());
				Random random = new Random();
				String replacement = String.valueOf(random.nextInt(endnum - startnum + 1) + startnum);
				params = m.replaceFirst(replacement);
				LogUtil.APP.info("Params({}):替换成随机数后，字符串:{}",matcherstr,params);
				m = pattern.matcher(params);
			}
			return params;
		} catch (IllegalArgumentException iae) {
			LogUtil.APP.error("处理随机数字参数过程中出现异常，请检查数字区间是否正常！",iae);
			return params;
		} catch (Exception e) {
			LogUtil.APP.error("处理随机数字参数过程中出现异常，请检查你的格式是否正确！",e);
			return params;
		}
	}

	/**
	 * 内置参数生成替换(生成当时时间指定格式)
	 * @param params 参数关键字
	 * @return 返回生成的参数
	 * @author Seagull
	 * @date 2019年1月15日
	 */
	private String replaceTimeNow(String params) {
		try {
			String str="(?i)@\\{timenow\\[(.*?)]}";
			Pattern pattern = Pattern.compile(str);
			Matcher m = pattern.matcher(params);
			while (m.find()) {
				String matcherstr = m.group(0);
				String formart = matcherstr.substring(matcherstr.indexOf("[") + 1, matcherstr.indexOf("]")).trim();
				SimpleDateFormat df;
				try {
					if("".equals(formart)||"timestamp".equals(formart.toLowerCase())){
						long time = System.currentTimeMillis();
						matcherstr=String.valueOf(time);
					}else{
						df = new SimpleDateFormat(formart);
						matcherstr=df.format(new Date());
					}
				} catch (IllegalArgumentException iae) {
					LogUtil.APP.error("处理随机数字参数过程中出现异常，请检查你的格式是否正确！",iae);
					df = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
					matcherstr=df.format(new Date());
				} finally {					
					params = m.replaceFirst(matcherstr);
					LogUtil.APP.info("Params({}):替换成随机数后，字符串:{}",matcherstr,params);
					m = pattern.matcher(params);
				}
			}
			return params;
		} catch (Exception e) {
			LogUtil.APP.error("处理随机数字参数过程中出现异常，请检查你的格式是否正确！",e);
			return params;
		}
	}

}
