package luckyclient.publicclass;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 系统日志记录
 * @author HuGuobo
 *
 */
public class LogUtil {
	
	/**
	 * 记录系统运行日志，监控系统运行情况
	 */
    public static final  Log APP = LogFactory.getLog("app");
    
    /**
     * 记录业务日志，监控业务执行情况
     */
    public static final  Log MSG = LogFactory.getLog("msg");
    
    /**
     * 记录系统错误，监控程序是否出错
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
