package luckyclient.driven;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;

/**
 * ��������
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸� ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944 Seagull
 * =================================================================
 * @author Seagull
 * @date 2019��1��15��
 */
public class SubString {
	/**
	 * ��ȡָ���ַ������м��ֶ�
	 * 
	 * @param str
	 * @param startstr
	 * @param endstr
	 * @return
	 */
	public static String subCentreStr(String str, String startstr, String endstr) {
		try{
			int startnum=0;
			int endnum=str.length();
			if(!"".equals(startstr)){
				startnum=str.indexOf(startstr) + startstr.length();
			}
			if(!"".equals(endstr)){
				endnum=str.indexOf(endstr, str.indexOf(startstr) + startstr.length());
			}
			String getstr = str.substring(startnum,endnum);
			return getstr;
		}catch(Exception e){
			return "��ȡ�ַ��������쳣�����������";
		}
	}

	/**
	 * ��ȡ�ַ�����ָ���ַ���ʼ
	 * 
	 * @param str
	 * @param startstr
	 * @return
	 */
	public static String subStartStr(String str, String startstr) {
		try{
			String getstr = str.substring(str.indexOf(startstr) + startstr.length());
			return getstr;
		}catch(Exception e){
			return "��ȡ�ַ��������쳣�����������";
		}
	}

	/**
	 * ��ȡ�ַ�����ָ���ַ�����
	 * 
	 * @param str
	 * @param startstr
	 * @return
	 */
	public static String subEndStr(String str, String endstr) {
		try{
			String getstr = str.substring(0, str.indexOf(endstr));
			return getstr;
		}catch(Exception e){
			return "��ȡ�ַ��������쳣�����������";
		}
	}

	/**
	 * ͨ���ַ���λ�ý�ȡָ���ַ������м��ֶ�
	 * 
	 * @param str
	 * @param startstr
	 * @param endstr
	 * @return
	 */
	public static String subCentreNum(String str, String startnum, String endnum) {
		String getstr = "";
		if("".equals(startnum)){
			startnum="0";
		}
		if("".equals(endnum)){
			endnum=String.valueOf(str.length());
		}
		try{
			if (isInteger(startnum) && isInteger(endnum)) {
				int start = Integer.valueOf(startnum);
				int end = Integer.valueOf(endnum);
				if (start > end) {
					getstr = "��ȡ�ַ�����ʼλ�����ֲ��ܴ��ڽ���λ������";
				} else if (start < 0 || end < 0) {
					getstr = "��ȡ�ַ���λ�õ����ֲ���С��0";
				} else if (start > str.length() || end > str.length()) {
					getstr = "��ȡ�ַ���λ�õ����ֲ��ܴ����ַ�������ĳ��ȡ�" + str.length() + "��";
				} else {
					getstr = str.substring(start, end);
				}
			} else {
				getstr = "ָ���Ŀ�ʼ���ǽ���λ���ַ������������ͣ����飡";
			}

			return getstr;
		}catch(Exception e){
			return "��ȡ�ַ��������쳣�����������";
		}
	}

	/**
	 * ͨ���ַ���λ�ý�ȡ�ַ�����ָ���ַ���ʼ
	 * 
	 * @param str
	 * @param startstr
	 * @return
	 */
	public static String subStartNum(String str, String startnum) {
		String getstr = "";
		try{
			if (isInteger(startnum)) {
				int start = Integer.valueOf(startnum);
				if (start < 0) {
					getstr = "��ȡ�ַ���λ�õ����ֲ���С��0";
				} else if (start > str.length()) {
					getstr = "��ȡ�ַ���λ�õ����ֲ��ܴ����ַ�������ĳ��ȡ�" + str.length() + "��";
				} else {
					getstr = str.substring(start);
				}
			} else {
				getstr = "ָ���Ŀ�ʼλ���ַ������������ͣ����飡";
			}

			return getstr;
		}catch(Exception e){
			return "��ȡ�ַ��������쳣�����������";
		}
	}

	/**
	 * ��ȡ�ַ�����ָ���ַ�����
	 * 
	 * @param str
	 * @param startstr
	 * @return
	 */
	public static String subEndNum(String str, String endnum) {
		String getstr = "";
		try{
			if (isInteger(endnum)) {
				int end = Integer.valueOf(endnum);
				if (end < 0) {
					getstr = "��ȡ�ַ���λ�õ����ֲ���С��0";
				} else if (end > str.length()) {
					getstr = "��ȡ�ַ���λ�õ����ֲ��ܴ����ַ�������ĳ��ȡ�" + str.length() + "��";
				} else {
					getstr = str.substring(0, end);
				}
			} else {
				getstr = "ָ���Ľ���λ���ַ������������ͣ����飡";
			}

			return getstr;
		}catch(Exception e){
			return "��ȡ�ַ��������쳣�����������";
		}
	}

	public static String subStrRgex(String str, String rgex, String num) {
		List<String> list = new ArrayList<String>();
		try{
			Pattern pattern = Pattern.compile(rgex);// ƥ���ģʽ
			Matcher m = pattern.matcher(str);
			while (m.find()) {
				int i = 1;
				list.add(m.group(i));
				i++;
			}

			String getstr = "";
			if (isInteger(num)) {
				int index = Integer.valueOf(num);
				if (index < 0) {
					getstr = "��ȡ�ַ����������ֲ���С��0";
				} else if (index > str.length()) {
					getstr = "��ȡ�ַ������������ֲ��ܴ����ַ�������ĳ��ȡ�" + str.length() + "��";
				} else if (index > list.size()) {
					getstr = "δ����ָ���ַ����и�������ʽ�ҵ�ƥ����ַ�������ָ�����������ִ������ҵ���ƥ���ַ���������";
				} else {
					getstr = list.get(index - 1);
				}
			} else {
				getstr = "ָ��������λ���ַ������������ͣ����飡";
			}
			return getstr;
		}catch(Exception e){
			return "��ȡ�ַ��������쳣�����������";
		}
	}

	private static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}

	/**
	 * ��ʼ������JSON��Value��ֵ
	 */
	private static String JSONVALUE = "����ȡJSON KEY�е�Value�쳣��";

	/**
	 * ���ڼ���KEY�����
	 */
	private static int COUNTER = 1;

	/**
	 * ����JSON����
	 * 
	 * @param json
	 * @param key
	 * @param keyindex
	 * @return
	 */
	private static JSONObject parseJsonString(String json, String key, int keyindex) {
		LinkedHashMap<String, Object> jsonMap = JSON.parseObject(json,
				new TypeReference<LinkedHashMap<String, Object>>() {
				}, Feature.OrderedField);
		for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
			parseJsonMap(entry, key, keyindex);
		}
		return new JSONObject(jsonMap);
	}

	/**
	 * ������JSON�����е�key�Լ�value
	 * 
	 * @param entry
	 * @param key
	 * @param keyindex
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Map.Entry<String, Object> parseJsonMap(Map.Entry<String, Object> entry, String key, int keyindex) {
		// ����ǵ���map��������
		if (entry.getValue() instanceof Map) {
			LinkedHashMap<String, Object> jsonMap = JSON.parseObject(entry.getValue().toString(),
					new TypeReference<LinkedHashMap<String, Object>>() {
					}, Feature.OrderedField);
			for (Map.Entry<String, Object> entry2 : jsonMap.entrySet()) {
				parseJsonMap(entry2, key, keyindex);
			}
		}
		// �����list����ȡ����
		if (entry.getValue() instanceof List) {
			List list = (List) entry.getValue();
			for (int i = 0; i < list.size(); i++) {
				// ������У�ѭ����ȡ
				//list.set(i, parseJsonString(list.get(i).toString(), key, keyindex));
				//��λ��У�ѭ����ȡ
				try{
					list.set(i, parseJsonString(list.get(i).toString(), key, keyindex));
				}catch(JSONException jsone){
					if(key.equals(entry.getKey())){
						if(keyindex==COUNTER){
							JSONVALUE = entry.getValue().toString();
						}			
						COUNTER++;
					}
					break;
				}
			}
		}
		// ��ȡkey�е�value
		if (key.equals(entry.getKey())) {
			if (keyindex == COUNTER) {
				JSONVALUE = entry.getValue().toString();
			}
			COUNTER++;
		}

		return entry;
	}

	/**
	 * ��ȡJSON����JSONArray����ָ�����Key�е�Value
	 * 
	 * @param json
	 * @param key
	 * @param indexstr
	 * @return
	 */
	public static String getJsonValue(String json, String key, String indexstr) {
		json = json.trim();
		int index = 1;
		String result = JSONVALUE;
		if (isInteger(indexstr) && !"0".equals(indexstr)) {
			index = Integer.valueOf(indexstr);
		} else {
			result = JSONVALUE + "ָ����keyֵ��Ų��Ǵ���0������(��Ŵ�1��ʼ)�����飡";
			return result;
		}

		if (json.startsWith("{") && json.endsWith("}")) {
			try {
				JSONObject jsonStr = JSONObject.parseObject(json, Feature.OrderedField);
				parseJsonString(jsonStr.toString(), key, index);
				result = JSONVALUE;
			} catch (Exception e) {
				result = JSONVALUE + "��ʽ����JSON�쳣�����������" + json;
				return result;
			}
		} else if (json.startsWith("[") && json.endsWith("]")) {
			try {
				// JSONArray jsonarr = JSONArray.parseArray(json);
				// ֱ��ʹ��fastjson�Ľӿ�ʵ���������
				JSONArray jsonarr = JSONArray.parseObject(json.getBytes(), JSONArray.class, Feature.OrderedField);
				for (int i = 0; i < jsonarr.size(); i++) {
					JSONObject jsonStr = jsonarr.getJSONObject(i);
					parseJsonString(jsonStr.toJSONString(), key, index);
					if (!JSONVALUE.startsWith("����ȡJSON KEY�е�Value�쳣��")) {
						result = JSONVALUE;
						break;
					}
				}
			} catch (Exception e) {
				result = JSONVALUE + "��ʽ����JSONArray�쳣�����������" + json;
				return result;
			}
		} else {
			result = JSONVALUE + "��ʽ����JSON����JSONArrayʱ�����쳣�����������" + json;
		}

		if (result.equals("����ȡJSON KEY�е�Value�쳣��")) {
			result = JSONVALUE + "û���ҵ���Ӧ��KEYֵ����ȷ�ϣ�";
		}

		COUNTER = 1;
		JSONVALUE = "����ȡJSON KEY�е�Value�쳣��";
		return result;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
