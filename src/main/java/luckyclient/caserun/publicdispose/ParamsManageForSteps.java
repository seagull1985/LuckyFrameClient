package luckyclient.caserun.publicdispose;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * �����ò������д���
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸� ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944 seagull
 * =================================================================
 * @author Seagull
 * @date 2019��1��15��
 */
public class ParamsManageForSteps {
	public static Map<String, String> GLOBAL_VARIABLE = new HashMap<>(0);
	/**
	 * �����ò�������
	 * @param params
	 * @return
	 * @author Seagull
	 * @date 2019��1��15��
	 */
	public static String paramsManage(String params) {
		ParamsManageForSteps pmfs = new ParamsManageForSteps();
		params = pmfs.replaceRandomInt(params);
		params = pmfs.replaceTimeNow(params);
		return params;
	}

	/**
	 * ���ò��������滻(�����������)
	 * @param params
	 * @return
	 * @author Seagull
	 * @date 2019��1��15��
	 */
	private String replaceRandomInt(String params) {
		try {
			Pattern pattern = Pattern.compile("(?i)@\\{random\\[(\\d+)\\]\\[(\\d+)\\]\\}");
			Matcher m = pattern.matcher(params);
			while (m.find()) {
				String matcherstr = m.group(0);
				int startnum = Integer
						.valueOf(matcherstr.substring(matcherstr.indexOf("[") + 1, matcherstr.indexOf("]")).trim());
				int endnum = Integer
						.valueOf(matcherstr.substring(matcherstr.lastIndexOf("[") + 1, matcherstr.lastIndexOf("]")).trim());
				Random random = new Random();
				String replacement = String.valueOf(random.nextInt(endnum - startnum + 1) + startnum);
				params = m.replaceFirst(replacement);
				luckyclient.publicclass.LogUtil.APP.info("Params(" + matcherstr + "):�滻����������ַ�����" + params);
				m = pattern.matcher(params);
			}
			return params;
		} catch (IllegalArgumentException iae) {
			luckyclient.publicclass.LogUtil.APP.error("����������ֲ��������г����쳣���������������Ƿ�������");
			return params;
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error("����������ֲ��������г����쳣��������ĸ�ʽ�Ƿ���ȷ��");
			return params;
		}
	}

	/**
	 * ���ò��������滻(���ɵ�ʱʱ��ָ����ʽ)
	 * @param params
	 * @return
	 * @author Seagull
	 * @date 2019��1��15��
	 */
	private String replaceTimeNow(String params) {
		try {
			Pattern pattern = Pattern.compile("(?i)@\\{timenow\\[(.*?)\\]\\}");
			Matcher m = pattern.matcher(params);
			while (m.find()) {
				String matcherstr = m.group(0);
				String formart = matcherstr.substring(matcherstr.indexOf("[") + 1, matcherstr.indexOf("]")).trim();
				SimpleDateFormat df = null;
				try {
					if("".equals(formart)||"timestamp".equals(formart.toLowerCase())){
						long time = System.currentTimeMillis();
						matcherstr=String.valueOf(time);
					}else{
						df = new SimpleDateFormat(formart);
						matcherstr=df.format(new Date());
					}
				} catch (IllegalArgumentException iae) {
					luckyclient.publicclass.LogUtil.APP.error("����������ֲ��������г����쳣��������ĸ�ʽ�Ƿ���ȷ��");
					df = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
					matcherstr=df.format(new Date());
				} finally {					
					params = m.replaceFirst(matcherstr);
					luckyclient.publicclass.LogUtil.APP.info("Params(" + matcherstr + "):�滻����������ַ�����" + params);
					m = pattern.matcher(params);
				}
			}
			return params;
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error("����������ֲ��������г����쳣��������ĸ�ʽ�Ƿ���ȷ��");
			return params;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
