package luckyclient.publicclass;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ϵͳ��־��¼
 * @author Seagull
 *
 */
public class LogUtil {
	
	/**
	 * ��¼ϵͳ������־�����ϵͳ�������
	 */
    public static final  Log APP = LogFactory.getLog("app");
    
    /**
     * ��¼ҵ����־�����ҵ��ִ�����
     */
    public static final  Log MSG = LogFactory.getLog("msg");
    
    /**
     * ��¼ϵͳ���󣬼�س����Ƿ����
     */
    public static final  Log ERROR = LogFactory.getLog("error");
    
   
    public static StringBuffer getFieldValue(Object bean){
    	StringBuffer sb = new StringBuffer();
    	
    	try{
    	    if(bean==null){
    	        return null;
    	    }
	    	Field[] fieldArray = bean.getClass().getDeclaredFields();
	    	if(fieldArray != null){
				int indexId = 0;
				Object obj = null;
	    		for(Field field:fieldArray){
	    			field.setAccessible(true);
	    			obj = field.get(bean);
	    			if(!(obj instanceof List) && !"serialVersionUID".equals(field.getName())){
	    			   if(indexId>0){
	    				   sb.append(",");
	    			   }    				   
	    			   if(obj != null){
	    				   sb.append(field.getName()).append("=").append( obj.toString());
	    			   }else{
	    				   sb.append(field.getName()).append("=");   
	    			   }	    				   
	       			   indexId += 1;    			   
	    			}
	    		}
	    	}
    	}
    	catch(Exception ex){
    		LogUtil.ERROR.error(ex,ex);
    	}
    	return sb;
    }    
}
