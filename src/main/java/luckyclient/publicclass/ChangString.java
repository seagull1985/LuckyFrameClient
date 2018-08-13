package luckyclient.publicclass;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸�
 * ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944  seagull1985
 * =================================================================
 *
 * @author�� seagull
 * @date 2017��12��1�� ����9:29:40
 */
public class ChangString {

	/**
	 * �滻�����е��ַ�
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
            //@@����ע��@����������
            int varcount = counter(str, "@") - counter(str, "@@") * 2;

            //������ڴ��Σ����д���
            if (varcount > 0) {
                luckyclient.publicclass.LogUtil.APP.info("��" + changname + "��" + str + "�����ҵ�" + varcount + "�����滻����");
                int changcount = 0;
                //�Ӳ����б��в���ƥ�����
                for (Map.Entry<String, String> entry : variable.entrySet()) {
                    if (str.contains("@" + entry.getKey())) {
                        if (str.contains("@@" + entry.getKey())) {
                            str = str.replace("@@" + entry.getKey(), "////CHANG////");
                        }
                        //�����滻�ַ����д���\"����\'�ᵼ��\��ʧ������
                        //entry.setValue(entry.getValue().replaceAll("\\\\\"", "\\&quot;"));
                        //entry.setValue(entry.getValue().replaceAll("\\\\\'", "\\\\&#39;"));
                        int viewcount = counter(str, "@" + entry.getKey());
                        str = str.replace("@" + entry.getKey(), entry.getValue());
                        luckyclient.publicclass.LogUtil.APP.info("��" + changname + "���ñ�����@" + entry.getKey() + "���滻��ֵ��" + entry.getValue() + "��");
                        str = str.replace("////CHANG////", "@@" + entry.getKey());
                        changcount = changcount + viewcount;
                    }
                }

                if (varcount != changcount) {
                    luckyclient.publicclass.LogUtil.APP.error(changname + "�����ñ���δ�ڲ��������ҵ������飡��������" + str + "��");
                }
            }
            str = str.replace("@@", "@");
            //�����ָ��ַ����д���\"����\'�ᵼ��\��ʧ������
            //str = str.replaceAll("\\&quot;", "\\\\\"");
            //str = str.replaceAll("\\&#39;", "\\\\\'");
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * ͳ���ַ�
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
     * �ж��Ƿ�������
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
     * @param str
     * @return
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * �滻��������
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
     * �滻json�еı���
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
					luckyclient.publicclass.LogUtil.APP.info("JSON�ַ����滻�ɹ���ԭʼJSON:��"+json+"��   ��JSON:��"+jsonStr.toJSONString()+"��");
				}
			} catch (Exception e) {
				luckyclient.publicclass.LogUtil.APP.error("��ʽ����JSON�쳣�����������"+json,e);
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
			    	luckyclient.publicclass.LogUtil.APP.info("׼���滻JSONArray�еĲ���ֵ��δ��⵽ָ����������ţ�Ĭ���滻��1������...");
				}else{
					jsonStr = jsonarr.getJSONObject(index);
					luckyclient.publicclass.LogUtil.APP.info("׼���滻JSONArray�еĲ���ֵ���滻ָ����"+index+"������...");
				}
				
				if(jsonStr.containsKey(key)){
					jsonStr.put(key, settype(jsonStr.get(key),value));
					jsonarr.set(index, jsonStr);
					map.put("boolean", "true");
					map.put("json", jsonarr.toJSONString());
				}
				luckyclient.publicclass.LogUtil.APP.info("JSONARRAY�ַ����滻�ɹ���ԭʼJSONARRAY:��"+json+"��   ��JSONARRAY:��"+jsonarr.toJSONString()+"��");
			} catch (Exception e) {
				luckyclient.publicclass.LogUtil.APP.error("��ʽ����JSONArray�쳣�����������"+json,e);
				return map;
			}
		}
		return map;
	}
    
    public static void main(String[] args) {

    }

}
