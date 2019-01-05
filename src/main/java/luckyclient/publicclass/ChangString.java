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
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 seagull1985
 * =================================================================
 * @author： seagull
 * @date 2017年12月1日 上午9:29:40
 */
public class ChangString {

	/**
	 * 替换变量中的字符
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
			// @@用来注释@的引用作用
			int varcount = counter(str, "@") - counter(str, "@@") * 2;

			// 如果存在传参，进行处理
			if (varcount > 0) {
				luckyclient.publicclass.LogUtil.APP.info("在" + changname + "【" + str + "】中找到" + varcount + "个可替换参数");
				int changcount = 0;

				// 准备将HASHMAP换成LINKMAP，对KEY进行排序，解决要先替换最长KEY的问题
				List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(variable.entrySet());
				// 然后通过比较器来实现排序
				Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
					// 按KEY长度降序排序
					public int compare(Entry<String, String> o1, Entry<String, String> o2) {
						return o2.getKey().length() - o1.getKey().length();
					}
				});

				Map<String, String> aMap2 = new LinkedHashMap<String, String>();
				for (Map.Entry<String, String> mapping : list) {
					aMap2.put(mapping.getKey(), mapping.getValue());
				}

				// 从参数列表中查找匹配变量
				for (Map.Entry<String, String> entry : aMap2.entrySet()) {
					if (str.contains("@" + entry.getKey())) {
						if (str.contains("@@" + entry.getKey())) {
							str = str.replace("@@" + entry.getKey(), "////CHANG////");
						}
						// 用来替换字符串中带了\"或是\'会导致\消失的问题
						// entry.setValue(entry.getValue().replaceAll("\\\\\"",
						// "\\&quot;"));
						// entry.setValue(entry.getValue().replaceAll("\\\\\'",
						// "\\\\&#39;"));
						int viewcount = counter(str, "@" + entry.getKey());
						str = str.replace("@" + entry.getKey(), entry.getValue());
						luckyclient.publicclass.LogUtil.APP
								.info("将" + changname + "引用变量【@" + entry.getKey() + "】替换成值【" + entry.getValue() + "】");
						str = str.replace("////CHANG////", "@@" + entry.getKey());
						changcount = changcount + viewcount;
					}
				}

				if (varcount != changcount) {
					luckyclient.publicclass.LogUtil.APP.error(changname + "有引用变量未在参数列中找到，请检查！处理结果【" + str + "】");
				}
			}
			str = str.replace("@@", "@");
			// 用来恢复字符串中带了\"或是\'会导致\消失的问题
			// str = str.replaceAll("\\&quot;", "\\\\\"");
			// str = str.replaceAll("\\&#39;", "\\\\\'");
			return str;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 统计字符
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
	 * 判断是否是数字
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
	 * 判断是否是整数
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}

	/**
	 * 替换变量类型
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
	 * 用于计数替换KEY的序号
	 */
	private static int COUNTER=1;
	/**
	 * 用于分辩是否把参数替换成功
	 */
	private static Boolean BCHANG=false;
	/**
	 * 遍历JSON对象
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
	 * 替换遍历后JSON对象中的KEY
	 * @param entry
	 * @param key
	 * @param value
	 * @param keyindex
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map.Entry<String, Object> parseJsonMap(Map.Entry<String, Object> entry,String key,String value,int keyindex){
		//如果是单个map继续遍历
		if(entry.getValue() instanceof Map){
		LinkedHashMap<String, Object> jsonMap = JSON.parseObject(entry.getValue().toString(), new TypeReference<LinkedHashMap<String, Object>>(){});
		for (Map.Entry<String, Object> entry2 : jsonMap.entrySet()) {
			parseJsonMap(entry2,key,value,keyindex);
			}
		entry.setValue(jsonMap);
		}
		//如果是list就提取出来
		if(entry.getValue() instanceof List){
			@SuppressWarnings("rawtypes")
			List list = (List)entry.getValue();
			for (int i = 0; i < list.size(); i++) {
				//如何还有，循环提取
				list.set(i, parseJsonString(list.get(i).toString(),key,value,keyindex));
				}
			entry.setValue(list);
			}
		//如果是String就获取它的值
		if(entry.getValue() instanceof String){
			if(key.equals(entry.getKey())){
				if(keyindex==COUNTER){
					luckyclient.publicclass.LogUtil.APP.info("对象原始String值：【"+entry.getValue()+"】");
					entry.setValue(value);
					luckyclient.publicclass.LogUtil.APP.info("对象替换后String值：【"+entry.getValue()+"】");
					BCHANG=true;
				}			
				COUNTER++;
			}
		}
		//如果是Integer就获取它的值
		if(entry.getValue() instanceof Integer){
			if(key.equals(entry.getKey())){
				if(keyindex==COUNTER){
					luckyclient.publicclass.LogUtil.APP.info("对象原始Integer值：【"+entry.getValue()+"】");
					entry.setValue(Integer.valueOf(value));
					luckyclient.publicclass.LogUtil.APP.info("对象替换后Integer值：【"+entry.getValue()+"】");
					BCHANG=true;
				}
				COUNTER++;
			}
		}
		//如果是Long就获取它的值
		if(entry.getValue() instanceof Long){
			if(key.equals(entry.getKey())){
				if(keyindex==COUNTER){
					luckyclient.publicclass.LogUtil.APP.info("对象原始Long值：【"+entry.getValue()+"】");
					entry.setValue(Long.valueOf(value));
					luckyclient.publicclass.LogUtil.APP.info("对象替换后Long值：【"+entry.getValue()+"】");
					BCHANG=true;
				}
				COUNTER++;
			}
		}
		//如果是Double就获取它的值
		if(entry.getValue() instanceof BigDecimal){
			if(key.equals(entry.getKey())){
				if(keyindex==COUNTER){
					luckyclient.publicclass.LogUtil.APP.info("对象原始BigDecimal值：【"+entry.getValue()+"】");
					BigDecimal bd = new BigDecimal(value);
					entry.setValue(bd);
					luckyclient.publicclass.LogUtil.APP.info("对象替换后BigDecimal值：【"+entry.getValue()+"】");
					BCHANG=true;
				}
				COUNTER++;
			}
		}
		//如果是Boolean就获取它的值
		if(entry.getValue() instanceof Boolean){
			if(key.equals(entry.getKey())){
				if(keyindex==COUNTER){
					luckyclient.publicclass.LogUtil.APP.info("对象原始Boolean值：【"+entry.getValue()+"】");
					entry.setValue(Boolean.valueOf(value));
					luckyclient.publicclass.LogUtil.APP.info("对象替换后Boolean值：【"+entry.getValue()+"】");
					BCHANG=true;
				}
				COUNTER++;
			}
		}
		
		return entry;
		}

	/**
	 * 替换json对象中指定KEY入口方法
	 * @param json
	 * @param key
	 * @param value
	 * @param index
	 * @return
	 */
	public static Map<String, String> changjson(String json, String key, String value,int index) {
		json=json.trim();
		luckyclient.publicclass.LogUtil.APP.info("原始JSON：【"+json+"】");
		luckyclient.publicclass.LogUtil.APP.info("待替换JSON KEY：【"+key+"】");
		luckyclient.publicclass.LogUtil.APP.info("待替换JSON VALUE：【"+value+"】");
		luckyclient.publicclass.LogUtil.APP.info("待替换JSON KEY序号：【"+index+"】");
		Map<String, String> map = new HashMap<String, String>(0);
		map.put("json", json);
		map.put("boolean", BCHANG.toString().toLowerCase());
		
		if (json.startsWith("{") && json.endsWith("}")) {
			try {
				JSONObject jsonStr = JSONObject.parseObject(json);				
				jsonStr=parseJsonString(json,key,value,index);
				if (BCHANG) {
					luckyclient.publicclass.LogUtil.APP
							.info("JSON字符串替换成功，新JSON:【" + jsonStr.toJSONString() + "】");
				}
				map.put("json", jsonStr.toJSONString());
			} catch (Exception e) {
				luckyclient.publicclass.LogUtil.APP.error("格式化成JSON异常，请检查参数：" + json, e);
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
								"JSONARRAY字符串替换成功，新JSONARRAY:【" + jsonarr.toJSONString() + "】");
						break;
					}
				}
				map.put("json", jsonarr.toJSONString());
				
			} catch (Exception e) {
				luckyclient.publicclass.LogUtil.APP.error("格式化成JSONArray异常，请检查参数：" + json, e);
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
