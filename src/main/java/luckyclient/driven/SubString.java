package luckyclient.driven;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubString {
	
	/**
	 * ��ȡָ���ַ������м��ֶ�
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
	 * ��ȡ�ַ�����ָ���ַ���ʼ
	 * @param str
	 * @param startstr
	 * @return
	 */
	public static String subStartStr(String str,String startstr){
		String getstr=str.substring(str.indexOf(startstr)+startstr.length());
		return getstr;
	}
	
	/**
	 * ��ȡ�ַ�����ָ���ַ�����
	 * @param str
	 * @param startstr
	 * @return
	 */
	public static String subEndStr(String str,String endstr){
		String getstr=str.substring(0,str.indexOf(endstr));
		return getstr;
	}
	
	/**
	 * ͨ���ַ���λ�ý�ȡָ���ַ������м��ֶ�
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
				getstr="��ȡ�ַ�����ʼλ�����ֲ��ܴ��ڽ���λ������";
			}else if(start<0||end<0){
				getstr="��ȡ�ַ���λ�õ����ֲ���С��0";
			}else if(start>str.length()||end>str.length()){
				getstr="��ȡ�ַ���λ�õ����ֲ��ܴ����ַ�������ĳ��ȡ�"+str.length()+"��";
			}else{
				getstr=str.substring(start,end);
			}
		}else{
			getstr="ָ���Ŀ�ʼ���ǽ���λ���ַ������������ͣ����飡";
		}

		return getstr;
	}
	
	/**
	 * ͨ���ַ���λ�ý�ȡ�ַ�����ָ���ַ���ʼ
	 * @param str
	 * @param startstr
	 * @return
	 */
	public static String subStartNum(String str,String startnum){
		String getstr="";
		if(isInteger(startnum)){
			int start=Integer.valueOf(startnum);
			if(start<0){
				getstr="��ȡ�ַ���λ�õ����ֲ���С��0";
			}else if(start>str.length()){
				getstr="��ȡ�ַ���λ�õ����ֲ��ܴ����ַ�������ĳ��ȡ�"+str.length()+"��";
			}else{
				getstr=str.substring(start);
			}
		}else{
			getstr="ָ���Ŀ�ʼλ���ַ������������ͣ����飡";
		}

		return getstr;
	}
	
	/**
	 * ��ȡ�ַ�����ָ���ַ�����
	 * @param str
	 * @param startstr
	 * @return
	 */
	public static String subEndNum(String str,String endnum){
		String getstr="";
		if(isInteger(endnum)){
			int end=Integer.valueOf(endnum);
			if(end<0){
				getstr="��ȡ�ַ���λ�õ����ֲ���С��0";
			}else if(end>str.length()){
				getstr="��ȡ�ַ���λ�õ����ֲ��ܴ����ַ�������ĳ��ȡ�"+str.length()+"��";
			}else{
				getstr=str.substring(0,end);
			}
		}else{
			getstr="ָ���Ľ���λ���ַ������������ͣ����飡";
		}

		return getstr;
	}
	
    public static String subStrRgex(String str,String rgex,String num){    
        List<String> list = new ArrayList<String>();    
        Pattern pattern = Pattern.compile(rgex);// ƥ���ģʽ    
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
				getstr="��ȡ�ַ����������ֲ���С��0";
			}else if(index>str.length()){
				getstr="��ȡ�ַ������������ֲ��ܴ����ַ�������ĳ��ȡ�"+str.length()+"��";
			}else if(index>list.size()){
				getstr="δ����ָ���ַ����и�������ʽ�ҵ�ƥ����ַ�������ָ�����������ִ������ҵ���ƥ���ַ���������";
			}else{
				getstr=list.get(index-1);
			}
		}else{
			getstr="ָ��������λ���ַ������������ͣ����飡";
		}
        return getstr;    
    }    
	
	/**
	 * ȡ��JSON��ָ�����key��ֵ
	 * @param jsonstr
	 * @param key
	 * @param indexstr ��Ŵ�1��ʼ
	 * @return
	 */
	public static String getJsonValue(String jsonstr, String key, String indexstr) {
		String result = "��ʽ����JSON�쳣";
		int index = 0;
		if (isInteger(indexstr)&&!"0".equals(indexstr)) {
			index = Integer.valueOf(indexstr)-1;
		}else{
			result = "ָ����keyֵ��Ų�����������0(��Ŵ�1��ʼ)�����飡";
			return result;
		}

		try {
			String[] restr = jsonstr.split("\"" + key + "\":",0);
			if (restr.length >= index) {
				if(restr[index].contains(",\"")){
					result = restr[index].substring(0,restr[index].indexOf(",\""));
				}
				if(result.contains("\"}")||"��ʽ����JSON�쳣".equals(result)){
					if("��ʽ����JSON�쳣".equals(result)){
						result = restr[index].substring(0,restr[index].indexOf("\"}"));
					}else{
						result = result.substring(0,result.indexOf("\"}"));
					}

				}
				if(result.contains("}")||"��ʽ����JSON�쳣".equals(result)){
					if("��ʽ����JSON�쳣".equals(result)){
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
					result = "û����JSON���ҵ�Key:"+key+" ��������JSON:" + jsonstr;
				}else{
					result = "ָ�����"+index+" С����JSON���ҵ���Key:"+key+" ����������JSON:" + jsonstr;
				}
			}
			
		} catch (Exception e) {
			result = "��ȡJSON��Key:"+key+" ��Value�����쳣�����������" + jsonstr;
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
