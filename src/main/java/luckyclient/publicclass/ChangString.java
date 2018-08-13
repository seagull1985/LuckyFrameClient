package luckyclient.publicclass;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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
public class ChangString {

	/**
	 * 替换变量中的字符
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
            //@@用来注释@的引用作用
            int varcount = counter(str, "@") - counter(str, "@@") * 2;

            //如果存在传参，进行处理
            if (varcount > 0) {
                luckyclient.publicclass.LogUtil.APP.info("在" + changname + "【" + str + "】中找到" + varcount + "个可替换参数");
                int changcount = 0;
                //从参数列表中查找匹配变量
                for (Map.Entry<String, String> entry : variable.entrySet()) {
                    if (str.contains("@" + entry.getKey())) {
                        if (str.contains("@@" + entry.getKey())) {
                            str = str.replace("@@" + entry.getKey(), "////CHANG////");
                        }
                        //用来替换字符串中带了\"或是\'会导致\消失的问题
                        //entry.setValue(entry.getValue().replaceAll("\\\\\"", "\\&quot;"));
                        //entry.setValue(entry.getValue().replaceAll("\\\\\'", "\\\\&#39;"));
                        int viewcount = counter(str, "@" + entry.getKey());
                        str = str.replace("@" + entry.getKey(), entry.getValue());
                        luckyclient.publicclass.LogUtil.APP.info("将" + changname + "引用变量【@" + entry.getKey() + "】替换成值【" + entry.getValue() + "】");
                        str = str.replace("////CHANG////", "@@" + entry.getKey());
                        changcount = changcount + viewcount;
                    }
                }

                if (varcount != changcount) {
                    luckyclient.publicclass.LogUtil.APP.error(changname + "有引用变量未在参数列中找到，请检查！处理结果【" + str + "】");
                }
            }
            str = str.replace("@@", "@");
            //用来恢复字符串中带了\"或是\'会导致\消失的问题
            //str = str.replaceAll("\\&quot;", "\\\\\"");
            //str = str.replaceAll("\\&#39;", "\\\\\'");
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 统计字符
     * @param str1
     * @param str2
     * @return
     */
    public static int counter(String str1, String str2) {
        int total = 0;
        for (String tmp = str1; tmp != null && tmp.length() >= str2.length(); ) {
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
     * @param str
     * @return
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 替换变量类型
     * @param object
     * @param str
     * @return
     */
    public static Object settype(Object object,String str){
    	if(object instanceof Integer){
    		return Integer.valueOf(str);
    	}else if(object instanceof Boolean){
    		return Boolean.valueOf(str);
    	}else if(object instanceof Long){
    		return Long.valueOf(str);
    	}else if(object instanceof Timestamp){
    		return Timestamp.valueOf(str);
    	}else if(object instanceof JSONObject){
    		return JSONObject.parseObject(str);
    	}else if(object instanceof JSONArray){
    		return JSONArray.parseArray(str);
    	}else{
    		return str;
    	}
    } 
    
    /**
     * 替换json中的变量
     * @param json
     * @param key
     * @param value
     * @return
     */
	public static Map<String,String> changjson(String json, String key, String value) {
		Map<String,String> map=new HashMap<String,String>(0);
		map.put("boolean", "false");
		map.put("json", json);
		if (json.startsWith("{") && json.endsWith("}")) {
			try {
				JSONObject jsonStr = JSONObject.parseObject(json);
				if(jsonStr.containsKey(key)){
					jsonStr.put(key, settype(jsonStr.get(key),value));
					map.put("boolean", "true");
					map.put("json", jsonStr.toJSONString());
					luckyclient.publicclass.LogUtil.APP.info("JSON字符串替换成功，原始JSON:【"+json+"】   新JSON:【"+jsonStr.toJSONString()+"】");
				}
			} catch (Exception e) {
				luckyclient.publicclass.LogUtil.APP.error("格式化成JSON异常，请检查参数："+json,e);
				return map;
			}
		} else if (json.startsWith("[") && json.endsWith("]")) {
			try {
				JSONArray jsonarr = JSONArray.parseArray(json);
				JSONObject jsonStr=new JSONObject();
				int index=0;
				if(key.indexOf("[")>=0 && key.endsWith("]")){
			    	index=Integer.valueOf(key.substring(key.lastIndexOf("[")+1,key.lastIndexOf("]")));
			    	key=key.substring(0, key.lastIndexOf("["));
			    	jsonStr = jsonarr.getJSONObject(index);
			    	luckyclient.publicclass.LogUtil.APP.info("准备替换JSONArray中的参数值，未检测到指定参数名序号，默认替换第1个参数...");
				}else{
					jsonStr = jsonarr.getJSONObject(index);
					luckyclient.publicclass.LogUtil.APP.info("准备替换JSONArray中的参数值，替换指定第"+index+"个参数...");
				}
				
				if(jsonStr.containsKey(key)){
					jsonStr.put(key, settype(jsonStr.get(key),value));
					jsonarr.set(index, jsonStr);
					map.put("boolean", "true");
					map.put("json", jsonarr.toJSONString());
				}
				luckyclient.publicclass.LogUtil.APP.info("JSONARRAY字符串替换成功，原始JSONARRAY:【"+json+"】   新JSONARRAY:【"+jsonarr.toJSONString()+"】");
			} catch (Exception e) {
				luckyclient.publicclass.LogUtil.APP.error("格式化成JSONArray异常，请检查参数："+json,e);
				return map;
			}
		}
		return map;
	}
    
    public static void main(String[] args) {

    }

}
