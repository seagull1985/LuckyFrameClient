package luckyclient.driven;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubString {
	
	/**
	 * 截取指定字符串的中间字段
	 * @param str
	 * @param startstr
	 * @param endstr
	 * @return
	 */
	public static String subCentreStr(String str,String startstr,String endstr){
		String getstr=str.substring(str.indexOf(startstr)+startstr.length(), str.indexOf(endstr,str.indexOf(startstr)+startstr.length()));
		return getstr;
	}
	
	/**
	 * 截取字符串从指定字符开始
	 * @param str
	 * @param startstr
	 * @return
	 */
	public static String subStartStr(String str,String startstr){
		String getstr=str.substring(str.indexOf(startstr)+startstr.length());
		return getstr;
	}
	
	/**
	 * 截取字符串到指定字符结束
	 * @param str
	 * @param startstr
	 * @return
	 */
	public static String subEndStr(String str,String endstr){
		String getstr=str.substring(0,str.indexOf(endstr));
		return getstr;
	}
	
	/**
	 * 通过字符串位置截取指定字符串的中间字段
	 * @param str
	 * @param startstr
	 * @param endstr
	 * @return
	 */
	public static String subCentreNum(String str,String startnum,String endnum){
		String getstr="";
		if(isInteger(startnum)&&isInteger(endnum)){
			int start=Integer.valueOf(startnum);
			int end=Integer.valueOf(endnum);
			if(start>end){
				getstr="截取字符串开始位置数字不能大于结束位置数字";
			}else if(start<0||end<0){
				getstr="截取字符串位置的数字不能小于0";
			}else if(start>str.length()||end>str.length()){
				getstr="截取字符串位置的数字不能大于字符串本身的长度【"+str.length()+"】";
			}else{
				getstr=str.substring(start,end);
			}
		}else{
			getstr="指定的开始或是结束位置字符不是数字类型，请检查！";
		}

		return getstr;
	}
	
	/**
	 * 通过字符串位置截取字符串从指定字符开始
	 * @param str
	 * @param startstr
	 * @return
	 */
	public static String subStartNum(String str,String startnum){
		String getstr="";
		if(isInteger(startnum)){
			int start=Integer.valueOf(startnum);
			if(start<0){
				getstr="截取字符串位置的数字不能小于0";
			}else if(start>str.length()){
				getstr="截取字符串位置的数字不能大于字符串本身的长度【"+str.length()+"】";
			}else{
				getstr=str.substring(start);
			}
		}else{
			getstr="指定的开始位置字符不是数字类型，请检查！";
		}

		return getstr;
	}
	
	/**
	 * 截取字符串到指定字符结束
	 * @param str
	 * @param startstr
	 * @return
	 */
	public static String subEndNum(String str,String endnum){
		String getstr="";
		if(isInteger(endnum)){
			int end=Integer.valueOf(endnum);
			if(end<0){
				getstr="截取字符串位置的数字不能小于0";
			}else if(end>str.length()){
				getstr="截取字符串位置的数字不能大于字符串本身的长度【"+str.length()+"】";
			}else{
				getstr=str.substring(0,end);
			}
		}else{
			getstr="指定的结束位置字符不是数字类型，请检查！";
		}

		return getstr;
	}
	
    public static String subStrRgex(String str,String rgex,String num){    
        List<String> list = new ArrayList<String>();    
        Pattern pattern = Pattern.compile(rgex);// 匹配的模式    
        Matcher m = pattern.matcher(str);    
        while (m.find()) {    
            int i = 1;    
            list.add(m.group(i));    
            i++;    
        }
        
		String getstr="";
		if(isInteger(num)){
			int index=Integer.valueOf(num);
			if(index<0){
				getstr="截取字符串索引数字不能小于0";
			}else if(index>str.length()){
				getstr="截取字符串索引的数字不能大于字符串本身的长度【"+str.length()+"】";
			}else if(index>list.size()){
				getstr="未能在指定字符串中根据正则式找到匹配的字符串或是指定的索引数字大于能找到的匹配字符串索引量";
			}else{
				getstr=list.get(index-1);
			}
		}else{
			getstr="指定的索引位置字符不是数字类型，请检查！";
		}
        return getstr;    
    }    
	
	/**
	 * 取出JSON中指定序号key的值
	 * @param jsonstr
	 * @param key
	 * @param indexstr 序号从1开始
	 * @return
	 */
	public static String getJsonValue(String jsonstr, String key, String indexstr) {
		String result = "格式化成JSON异常";
		int index = 0;
		if (isInteger(indexstr)&&!"0".equals(indexstr)) {
			index = Integer.valueOf(indexstr)-1;
		}else{
			result = "指定的key值序号不是整数或是0(序号从1开始)，请检查！";
			return result;
		}

		try {
			String[] restr = jsonstr.split("\"" + key + "\":",0);
			if (restr.length >= index) {
				if(restr[index].contains(",\"")){
					result = restr[index].substring(0,restr[index].indexOf(",\""));
				}
				if(result.contains("\"}")||"格式化成JSON异常".equals(result)){
					if("格式化成JSON异常".equals(result)){
						result = restr[index].substring(0,restr[index].indexOf("\"}"));
					}else{
						result = result.substring(0,result.indexOf("\"}"));
					}

				}
				if(result.contains("}")||"格式化成JSON异常".equals(result)){
					if("格式化成JSON异常".equals(result)){
						result = restr[index].substring(0,restr[index].indexOf("\"}"));
					}else{
						result = result.substring(0,result.indexOf("}"));
					}
				}
				if(result.startsWith("\"")){
					result=result.substring(1);
				}
			}else{
				if(restr.length==1){
					result = "没有在JSON中找到Key:"+key+" 对象，请检查JSON:" + jsonstr;
				}else{
					result = "指定序号"+index+" 小于在JSON中找到的Key:"+key+" 个数，请检查JSON:" + jsonstr;
				}
			}
			
		} catch (Exception e) {
			result = "获取JSON中Key:"+key+" 的Value出现异常，请检查参数：" + jsonstr;
			return result;
		}
		return result;
	}
	
	private static boolean isInteger(String str) {  
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");  
        return pattern.matcher(str).matches();  
  }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
