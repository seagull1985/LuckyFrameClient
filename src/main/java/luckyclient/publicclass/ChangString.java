package luckyclient.publicclass;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸� ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944 seagull1985
 * =================================================================
 * @author�� seagull
 * @date 2017��12��1�� ����9:29:40
 */
public class ChangString {

	/**
	 * �滻�����е��ַ�
	 * 
	 * @param str
	 * @param variable
	 * @param changname
	 * @return
	 */
	public static String changparams(String str, Map<String, String> variable, String changname) {
		try {
			if (null == str) {
				return null;
			}
			str = str.replace("&quot;", "\"");
			str = str.replace("&#39;", "\'");
			// @@����ע��@����������
			int varcount = counter(str, "@") - counter(str, "@@") * 2;

			// ������ڴ��Σ����д���
			if (varcount > 0) {
				luckyclient.publicclass.LogUtil.APP.info("��" + changname + "��" + str + "�����ҵ�" + varcount + "�����滻����");
				int changcount = 0;

				// ׼����HASHMAP����LINKMAP����KEY�������򣬽��Ҫ���滻�KEY������
				List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(variable.entrySet());
				// Ȼ��ͨ���Ƚ�����ʵ������
				Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
					// ��KEY���Ƚ�������
					public int compare(Entry<String, String> o1, Entry<String, String> o2) {
						return o2.getKey().length() - o1.getKey().length();
					}
				});

				Map<String, String> aMap2 = new LinkedHashMap<String, String>();
				for (Map.Entry<String, String> mapping : list) {
					aMap2.put(mapping.getKey(), mapping.getValue());
				}

				// �Ӳ����б��в���ƥ�����
				for (Map.Entry<String, String> entry : aMap2.entrySet()) {
					if (str.contains("@" + entry.getKey())) {
						if (str.contains("@@" + entry.getKey())) {
							str = str.replace("@@" + entry.getKey(), "////CHANG////");
						}
						// �����滻�ַ����д���\"����\'�ᵼ��\��ʧ������
						// entry.setValue(entry.getValue().replaceAll("\\\\\"",
						// "\\&quot;"));
						// entry.setValue(entry.getValue().replaceAll("\\\\\'",
						// "\\\\&#39;"));
						int viewcount = counter(str, "@" + entry.getKey());
						str = str.replace("@" + entry.getKey(), entry.getValue());
						luckyclient.publicclass.LogUtil.APP
								.info("��" + changname + "���ñ�����@" + entry.getKey() + "���滻��ֵ��" + entry.getValue() + "��");
						str = str.replace("////CHANG////", "@@" + entry.getKey());
						changcount = changcount + viewcount;
					}
				}

				if (varcount != changcount) {
					luckyclient.publicclass.LogUtil.APP.error(changname + "�����ñ���δ�ڲ��������ҵ������飡��������" + str + "��");
				}
			}
			str = str.replace("@@", "@");
			// �����ָ��ַ����д���\"����\'�ᵼ��\��ʧ������
			// str = str.replaceAll("\\&quot;", "\\\\\"");
			// str = str.replaceAll("\\&#39;", "\\\\\'");
			return str;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * ͳ���ַ�
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static int counter(String str1, String str2) {
		int total = 0;
		for (String tmp = str1; tmp != null && tmp.length() >= str2.length();) {
			if (tmp.indexOf(str2) == 0) {
				total++;
				tmp = tmp.substring(str2.length());
			} else {
				tmp = tmp.substring(1);
			}
		}
		return total;
	}

	/**
	 * �ж��Ƿ�������
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * �ж��Ƿ�������
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}

	/**
	 * �滻��������
	 * 
	 * @param object
	 * @param str
	 * @return
	 */
	public static Object settype(Object object, String str) {
		if (object instanceof Integer) {
			return Integer.valueOf(str);
		} else if (object instanceof Boolean) {
			return Boolean.valueOf(str);
		} else if (object instanceof Long) {
			return Long.valueOf(str);
		} else if (object instanceof Timestamp) {
			return Timestamp.valueOf(str);
		} else if (object instanceof JSONObject) {
			return JSONObject.parseObject(str);
		} else if (object instanceof JSONArray) {
			return JSONArray.parseArray(str);
		} else {
			return str;
		}
	}

	/**
	 * ���ڼ����滻KEY�����
	 */
	private static int COUNTER=1;
	/**
	 * ���ڷֱ��Ƿ�Ѳ����滻�ɹ�
	 */
	private static Boolean BCHANG=false;
	/**
	 * ����JSON����
	 * @param json
	 * @param key
	 * @param value
	 * @param keyindex
	 * @return
	 */
	public static JSONObject parseJsonString(String json,String key,String value,int keyindex){
		LinkedHashMap<String, Object> jsonMap = JSON.parseObject(json, new TypeReference<LinkedHashMap<String, Object>>(){});
		for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
			parseJsonMap(entry,key,value,keyindex);
			}
		return new JSONObject(jsonMap);
		}
	
	/**
	 * �滻������JSON�����е�KEY
	 * @param entry
	 * @param key
	 * @param value
	 * @param keyindex
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map.Entry<String, Object> parseJsonMap(Map.Entry<String, Object> entry,String key,String value,int keyindex){
		//����ǵ���map��������
		if(entry.getValue() instanceof Map){
		LinkedHashMap<String, Object> jsonMap = JSON.parseObject(entry.getValue().toString(), new TypeReference<LinkedHashMap<String, Object>>(){});
		for (Map.Entry<String, Object> entry2 : jsonMap.entrySet()) {
			parseJsonMap(entry2,key,value,keyindex);
			}
		entry.setValue(jsonMap);
		}
		//�����list����ȡ����
		if(entry.getValue() instanceof List){
			@SuppressWarnings("rawtypes")
			List list = (List)entry.getValue();
			for (int i = 0; i < list.size(); i++) {
				//��λ��У�ѭ����ȡ
				list.set(i, parseJsonString(list.get(i).toString(),key,value,keyindex));
				}
			entry.setValue(list);
			}
		//�����String�ͻ�ȡ����ֵ
		if(entry.getValue() instanceof String){
			if(key.equals(entry.getKey())){
				if(keyindex==COUNTER){
					luckyclient.publicclass.LogUtil.APP.info("����ԭʼStringֵ����"+entry.getValue()+"��");
					entry.setValue(value);
					luckyclient.publicclass.LogUtil.APP.info("�����滻��Stringֵ����"+entry.getValue()+"��");
					BCHANG=true;
				}			
				COUNTER++;
			}
		}
		//�����Integer�ͻ�ȡ����ֵ
		if(entry.getValue() instanceof Integer){
			if(key.equals(entry.getKey())){
				if(keyindex==COUNTER){
					luckyclient.publicclass.LogUtil.APP.info("����ԭʼIntegerֵ����"+entry.getValue()+"��");
					entry.setValue(Integer.valueOf(value));
					luckyclient.publicclass.LogUtil.APP.info("�����滻��Integerֵ����"+entry.getValue()+"��");
					BCHANG=true;
				}
				COUNTER++;
			}
		}
		//�����Long�ͻ�ȡ����ֵ
		if(entry.getValue() instanceof Long){
			if(key.equals(entry.getKey())){
				if(keyindex==COUNTER){
					luckyclient.publicclass.LogUtil.APP.info("����ԭʼLongֵ����"+entry.getValue()+"��");
					entry.setValue(Long.valueOf(value));
					luckyclient.publicclass.LogUtil.APP.info("�����滻��Longֵ����"+entry.getValue()+"��");
					BCHANG=true;
				}
				COUNTER++;
			}
		}
		//�����Double�ͻ�ȡ����ֵ
		if(entry.getValue() instanceof BigDecimal){
			if(key.equals(entry.getKey())){
				if(keyindex==COUNTER){
					luckyclient.publicclass.LogUtil.APP.info("����ԭʼBigDecimalֵ����"+entry.getValue()+"��");
					BigDecimal bd = new BigDecimal(value);
					entry.setValue(bd);
					luckyclient.publicclass.LogUtil.APP.info("�����滻��BigDecimalֵ����"+entry.getValue()+"��");
					BCHANG=true;
				}
				COUNTER++;
			}
		}
		//�����Boolean�ͻ�ȡ����ֵ
		if(entry.getValue() instanceof Boolean){
			if(key.equals(entry.getKey())){
				if(keyindex==COUNTER){
					luckyclient.publicclass.LogUtil.APP.info("����ԭʼBooleanֵ����"+entry.getValue()+"��");
					entry.setValue(Boolean.valueOf(value));
					luckyclient.publicclass.LogUtil.APP.info("�����滻��Booleanֵ����"+entry.getValue()+"��");
					BCHANG=true;
				}
				COUNTER++;
			}
		}
		
		return entry;
		}

	/**
	 * �滻json������ָ��KEY��ڷ���
	 * @param json
	 * @param key
	 * @param value
	 * @param index
	 * @return
	 */
	public static Map<String, String> changjson(String json, String key, String value,int index) {
		json=json.trim();
		luckyclient.publicclass.LogUtil.APP.info("ԭʼJSON����"+json+"��");
		luckyclient.publicclass.LogUtil.APP.info("���滻JSON KEY����"+key+"��");
		luckyclient.publicclass.LogUtil.APP.info("���滻JSON VALUE����"+value+"��");
		luckyclient.publicclass.LogUtil.APP.info("���滻JSON KEY��ţ���"+index+"��");
		Map<String, String> map = new HashMap<String, String>(0);
		map.put("json", json);
		map.put("boolean", BCHANG.toString().toLowerCase());
		
		if (json.startsWith("{") && json.endsWith("}")) {
			try {
				JSONObject jsonStr = JSONObject.parseObject(json);				
				jsonStr=parseJsonString(json,key,value,index);
				if (BCHANG) {
					luckyclient.publicclass.LogUtil.APP
							.info("JSON�ַ����滻�ɹ�����JSON:��" + jsonStr.toJSONString() + "��");
				}
				map.put("json", jsonStr.toJSONString());
			} catch (Exception e) {
				luckyclient.publicclass.LogUtil.APP.error("��ʽ����JSON�쳣�����������" + json, e);
				return map;
			}
		} else if (json.startsWith("[") && json.endsWith("]")) {
			try {
				JSONArray jsonarr = JSONArray.parseArray(json);
				
				for(int i=0;i<jsonarr.size();i++){
					JSONObject jsonStr = jsonarr.getJSONObject(i);		
					jsonStr=parseJsonString(jsonStr.toJSONString(),key,value,index);
					if(BCHANG){
						jsonarr.set(i, jsonStr);
						luckyclient.publicclass.LogUtil.APP.info(
								"JSONARRAY�ַ����滻�ɹ�����JSONARRAY:��" + jsonarr.toJSONString() + "��");
						break;
					}
				}
				map.put("json", jsonarr.toJSONString());
				
			} catch (Exception e) {
				luckyclient.publicclass.LogUtil.APP.error("��ʽ����JSONArray�쳣�����������" + json, e);
				return map;
			}
		}
		map.put("boolean", BCHANG.toString().toLowerCase());
		BCHANG=false;
		COUNTER=1;
		return map;
	}

	public static void main(String[] args) {
		
	}

}
