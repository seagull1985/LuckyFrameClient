package luckyclient.publicclass;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 此测试框架主要采用testlink做分层框架，负责数据驱动以及用例管理部分，有任何疑问欢迎联系作者讨论。
 * QQ:24163551 seagull1985
 * =================================================================
 * @ClassName: InvokeMethod 
 * @Description: 动态调用方法
 * @author： seagull
 * @date 2014年6月24日 上午9:29:40  
 * 
 */
public class InvokeMethod {

	/**
	 * @param args
	 * @throws Throwable 
	 */
	public static String CallCase(String packagename,String functionname,Object[] getParameterValues){
		try{
		Object server = Class.forName(packagename).newInstance();   //调用非静态方法用到
		Class[] getParameterTypes = null;
		if(getParameterValues!=null){
			int paramscount = getParameterValues.length;
			//赋值数组，定义类型
			getParameterTypes  = new Class[paramscount];
			for(int i=0;i<paramscount;i++){			
				getParameterTypes[i]=String.class;
			}
		}
		Method method = getMethod(server.getClass().getMethods(), functionname,getParameterTypes);
		if (method==null){
			throw new Exception("客户端本地lib目录下没有在包名为【"+packagename+"】中找到被调用的方法【"
					+functionname+"】,请检查方法名称以及参数个数是否一致！");
		}
		Object str=method.invoke(server,getParameterValues);
		if(str==null){
			return  "返回结果是null";
		}else{
			return str.toString();
		}
	}catch(Throwable e){
		luckyclient.publicclass.LogUtil.ERROR.error(e.getMessage(), e);
		return "调用异常，请查看错误日志！";
	}
	//	return str==null?"返回结果是null":str;
	}
	
	public static Method getMethod(Method[] methods, String methodName, Class[] parameterTypes)
	{
		for (int i = 0; i < methods.length; i++)
		{
			if (!methods[i].getName().equals(methodName))
				continue;
			if (compareParameterTypes(parameterTypes, methods[i].getParameterTypes()))
				return methods[i];
		}
		return null;
	}
	
	public static boolean compareParameterTypes(Class[] parameterTypes, Class[] orgParameterTypes)
	{
		// parameterTypes 里面，int->Integer
		// orgParameterTypes是原始参数类型
		if (parameterTypes == null && orgParameterTypes == null)
			return true;
		if (parameterTypes == null && orgParameterTypes != null)
		{
			if (orgParameterTypes.length == 0)
				return true;
			else
				return false;
		}
		if (parameterTypes != null && orgParameterTypes == null)
		{
			if (parameterTypes.length == 0)
				return true;
			else
				return false;
		}
		if (parameterTypes.length != orgParameterTypes.length)
			return false;
		return true;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
