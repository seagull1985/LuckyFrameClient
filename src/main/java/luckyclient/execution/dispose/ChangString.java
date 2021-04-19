package luckyclient.execution.dispose;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import luckyclient.utils.LogUtil;

/**
 * 对参数替换进行处理
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 seagull
 * =================================================================
 * @author Seagull
 * @date 2019年1月15日
 */
public class ChangString {

	/**
	 * 替换变量中的字符
	 * 
	 * @param str 待处理字符串
	 * @param variable 变量集（公共变量、全局变量、局部变量）
	 * @param changname 变量key
	 * @return 返回替换后的字符串
	 */
	public static String changparams(String str, Map<String, String> variable, String changname) {
		try {
			if (null == str) {
				return null;
			}
			str = str.trim();
			str = str.replace("&quot;", "\"");
			str = str.replace("&#39;", "'");
			// @@用来注释@的引用作用
			int varcount = counter(str, "@") - counter(str, "@@") * 2;

			// 如果存在传参，进行处理
			if (varcount > 0) {
				LogUtil.APP.info("在{}【{}】中找到{}个可替换参数",changname,str,varcount);
				int changcount = 0;

				// 准备将HASHMAP换成LINKMAP，对KEY进行排序，解决要先替换最长KEY的问题
				List<Map.Entry<String, String>> list = new ArrayList<>(variable.entrySet());
				// 然后通过比较器来实现排序
				// 按KEY长度降序排序
				// 然后通过比较器来实现排序
				// 按KEY长度降序排序
				list.sort((o1, o2) -> o2.getKey().length() - o1.getKey().length());

				Map<String, String> aMap2 = new LinkedHashMap<>();
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
						LogUtil.APP.info("将{}引用变量【@{}】替换成值【{}】",changname,entry.getKey(),entry.getValue());
						str = str.replace("////CHANG////", "@@" + entry.getKey());
						changcount = changcount + viewcount;
					}
				}

				if (varcount != changcount) {
					LogUtil.APP.warn(changname + "有引用变量未在参数列中找到，请检查！处理结果【{}】",str);
				}
			}
			str = str.replace("@@", "@");
			//对内置函数进行处理
			str=ParamsManageForSteps.paramsManage(str);
			return str;
		} catch (Exception e) {
			LogUtil.APP.error("替换参数过程中出现异常，请检查！",e);
			return "";
		}
	}

	/**
	 * 统计字符
	 * @param str1 原始字符串
	 * @param str2 待统计字符串
	 * @return 返回个数
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
	 * @param str 数字字符
	 * @return 返回布尔值
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
	 * @param str 数字字符
	 * @return 返回布尔值
	 */
	public static boolean isInteger(String str) {
		String patternStr="^[-+]?[\\d]*$";
		Pattern pattern = Pattern.compile(patternStr);
		return pattern.matcher(str).matches();
	}

	/**
	 * 替换变量类型
	 * @param object 替换对象
	 * @param str 替换字符串
	 * @return 返回对象
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
	 * @param json 原始json
	 * @param key 替换key
	 * @param value 替换值
	 * @param keyindex 替换key索引
	 * @return 返回json对象
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
	 * @param entry json对象转换成MAP
	 * @param key 待替换key
	 * @param value 替换值
	 * @param keyindex 替换key索引
	 */
	@SuppressWarnings("unchecked")
	public static void parseJsonMap(Entry<String, Object> entry, String key, String value, int keyindex){
		//如果是字符串型的null直接把对象设置为对象null
		if("NULL".equals(value)){
			value = null;
		}
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
			if(key.equals(entry.getKey())){
				if(keyindex==COUNTER){
					LogUtil.APP.info("对象原始String值:【{}】",entry.getValue());
					JSONArray jsonarr = JSONArray.parseArray(value);
					entry.setValue(jsonarr);
					LogUtil.APP.info("对象替换后String值:【{}】",entry.getValue());
					BCHANG=true;
				}			
				COUNTER++;
			}else{
				@SuppressWarnings("rawtypes")
				List list = (List)entry.getValue();
				for (int i = 0; i < list.size(); i++) {
					//如何还有，循环提取
					try{
						list.set(i, parseJsonString(list.get(i).toString(),key,value,keyindex));
						entry.setValue(list);
					}catch(JSONException jsone){
						if(key.equals(entry.getKey())){
							if(keyindex==COUNTER){
								LogUtil.APP.info("对象原始List值:【{}】",entry.getValue());
								JSONArray jsonarr = JSONArray.parseArray(value);
								entry.setValue(jsonarr);
								LogUtil.APP.info("对象替换后List值:【{}】",entry.getValue());
								BCHANG=true;
							}			
							COUNTER++;
						}
						break;
					}
					}
			  }
			}
		//如果是String就获取它的值
		if(entry.getValue() instanceof String){
			if(key.equals(entry.getKey())){
				if(keyindex==COUNTER){
					LogUtil.APP.info("对象原始String值:【{}】",entry.getValue());
					entry.setValue(value);
					LogUtil.APP.info("对象替换后String值:【{}】",entry.getValue());
					BCHANG=true;
				}			
				COUNTER++;
			}
		}
		//如果是Integer就获取它的值
		if(entry.getValue() instanceof Integer){
			if(key.equals(entry.getKey())){
				if(keyindex==COUNTER){
					LogUtil.APP.info("对象原始Integer值:【{}】",entry.getValue());
					assert value != null;
					entry.setValue(Integer.valueOf(value));
					LogUtil.APP.info("对象替换后Integer值:【{}】",entry.getValue());
					BCHANG=true;
				}
				COUNTER++;
			}
		}
		//如果是Long就获取它的值
		if(entry.getValue() instanceof Long){
			if(key.equals(entry.getKey())){
				if(keyindex==COUNTER){
					LogUtil.APP.info("对象原始Long值:【{}】",entry.getValue());
					assert value != null;
					entry.setValue(Long.valueOf(value));
					LogUtil.APP.info("对象替换后Long值:【{}】",entry.getValue());
					BCHANG=true;
				}
				COUNTER++;
			}
		}
		//如果是Double就获取它的值
		if(entry.getValue() instanceof BigDecimal){
			if(key.equals(entry.getKey())){
				if(keyindex==COUNTER){
					LogUtil.APP.info("对象原始BigDecimal值:【{}】",entry.getValue());
					assert value != null;
					BigDecimal bd = new BigDecimal(value);
					entry.setValue(bd);
					LogUtil.APP.info("对象替换后BigDecimal值:【{}】",entry.getValue());
					BCHANG=true;
				}
				COUNTER++;
			}
		}
		//如果是Boolean就获取它的值
		if(entry.getValue() instanceof Boolean){
			if(key.equals(entry.getKey())){
				if(keyindex==COUNTER){
					LogUtil.APP.info("对象原始Boolean值:【{}】",entry.getValue());
					entry.setValue(Boolean.valueOf(value));
					LogUtil.APP.info("对象替换后Boolean值:【{}】",entry.getValue());
					BCHANG=true;
				}
				COUNTER++;
			}
		}

	}

	/**
	 * 替换json对象中指定KEY入口方法
	 * @param json 待替换原始json
	 * @param key 替换key
	 * @param value 替换值
	 * @param index 替换key索引
	 * @return 返回替换后的MAP对象
	 */
	public static Map<String, String> changjson(String json, String key, String value,int index) {
		json=json.trim();
		LogUtil.APP.info("原始JSON:【{}】，待替换JSON KEY:【{}】，待替换JSON VALUE:【{}】，待替换JSON KEY序号:【{}】",json,key,value,index);
		Map<String, String> map = new HashMap<>(0);
		map.put("json", json);
		map.put("boolean", BCHANG.toString().toLowerCase());
		
		if (json.startsWith("{") && json.endsWith("}")) {
			try {
				JSONObject jsonStr;
				jsonStr=parseJsonString(json,key,value,index);
				if (BCHANG) {
					LogUtil.APP.info("JSON字符串替换成功，新JSON:【{}】",jsonStr.toJSONString());
				}
				map.put("json", jsonStr.toJSONString());
			} catch (Exception e) {
				LogUtil.APP.error("格式化成JSON异常，请检查参数:{}",json, e);
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
						LogUtil.APP.info("JSONARRAY字符串替换成功，新JSONARRAY:【{}】",jsonarr.toJSONString());
						break;
					}
				}
				map.put("json", jsonarr.toJSONString());
				
			} catch (Exception e) {
				LogUtil.APP.error("格式化成JSONArray异常，请检查参数:{}",json, e);
				return map;
			}
		}
		map.put("boolean", BCHANG.toString().toLowerCase());
		BCHANG=false;
		COUNTER=1;
		return map;
	}

}
