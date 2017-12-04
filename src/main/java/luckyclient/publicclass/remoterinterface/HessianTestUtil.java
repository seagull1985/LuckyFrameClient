package luckyclient.publicclass.remoterinterface;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caucho.hessian.client.HessianProxyFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 * 
 * @author： seagull
 * @date 2017年12月1日 上午9:29:40
 * 
 */
public class HessianTestUtil {
	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final int CONNECT_TIME_OUT = 30 * 1000;
	private static final Map<String, Class<?>> BASE_TYPE = new HashMap<String, Class<?>>();
	static {
		BASE_TYPE.put("long.class", long.class);
		BASE_TYPE.put("long.class", long.class);
		BASE_TYPE.put("float.class", float.class);
		BASE_TYPE.put("boolean.class", boolean.class);
		BASE_TYPE.put("char.class", char.class);
		BASE_TYPE.put("byte.class", byte.class);
		// BASE_TYPE.put("void.class", void.class);
		BASE_TYPE.put("short.class", short.class);
		BASE_TYPE.put("long", long.class);
		BASE_TYPE.put("float", float.class);
		BASE_TYPE.put("boolean", boolean.class);
		BASE_TYPE.put("char", char.class);
		BASE_TYPE.put("byte", byte.class);
		// BASE_TYPE.put("void", void.class);
		BASE_TYPE.put("short", short.class);

	}
	
	public static Map<String, Object> startInvoke(InterfaceObject object)
			throws Exception {
		// 获取带请求参数列表将（参数类型，参数值）数组转成InterfaceParamObject数组
		InterfaceParamObject[] paramContent = MAPPER
				.readValue(object.getParams(), InterfaceParamObject[].class);
		Map<String, Object> resultMap = hessianInvoke(object.getRemoteUrl(),
				object.getInterfaceClass(), object.getInterfaceMethod(),
				paramContent);
		return resultMap;

	}
	
	private static Map<String, Object> hessianInvoke(String remoteUrl,
			String className, String methodName,
			InterfaceParamObject[] interfaceParamArray) throws Exception {
		HessianProxyFactory factory = new HessianProxyFactory();
		factory.setReadTimeout(CONNECT_TIME_OUT);
		// factory.setConnectTimeout(connect_time_out);
		// factory.setReadTimeout(read_time_out);
		Object interfaceObject = null;
		Class<?>[] allRequstParasClass = new Class[interfaceParamArray.length];
		Object[] allRequstParas = new Object[interfaceParamArray.length];
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			interfaceObject = factory.create(Class.forName(className),remoteUrl);
		} catch (Exception ex) {
			throw ex;
		}
		
		// 获取到全部参数的类类型以便值反射获取方法时使用
				try {
					for (int index = 0; index < interfaceParamArray.length; index++) {
						String tempParaClassName = interfaceParamArray[index]
								.getClassname();
						if (isBaseTypeForArray(tempParaClassName)) {
							// 如果是基础数据类型，那么mapper转换再获取类类型就会有问题，所以值和类分开
							allRequstParasClass[index] = BASE_TYPE
									.get(tempParaClassName);
							allRequstParas[index] = MAPPER.readValue(
									interfaceParamArray[index].getValue(),
									BASE_TYPE.get(tempParaClassName));
						} else {
							allRequstParasClass[index] = Class
									.forName(tempParaClassName);
							allRequstParas[index] = MAPPER.readValue(
									interfaceParamArray[index].getValue(),
									allRequstParasClass[index]);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw e;
				}
				
				try {
					Method method = interfaceObject.getClass().getMethod(methodName,
							allRequstParasClass);
					Class<?> returnClass = method.getReturnType();
					Object result = method.invoke(interfaceObject, allRequstParas);

					if (returnClass == void.class) {
						resultMap.put("参数返回类型为void", "执行结束，没有返回值");
					} else if (isBaseTypeForArray(returnClass.getName())) {
						resultMap.put(returnClass.getName(), result);
					} else {
						resultMap = objectToMap(result);
					}
				} catch (NoSuchMethodException e) {
					resultMap.put("无法获取方法", methodName);
					e.printStackTrace();
					throw e;
				} catch (SecurityException e) {
					resultMap.put("无法获取方法", methodName);
					e.printStackTrace();
					throw e;
				} catch (IllegalAccessException e) {
					resultMap.put("无方法访问权限", methodName);
					e.printStackTrace();
					throw e;
				} catch (IllegalArgumentException e) {
					resultMap.put("方法参数非法", methodName);
					e.printStackTrace();
					throw e;
				} catch (InvocationTargetException e) {
					resultMap.put("执行方法时出错", methodName);
					resultMap.put("错误信息", e.getCause().getMessage());
					e.printStackTrace();
					throw e;
				} catch (Throwable t) {
					resultMap.put("未知错误描述", t.getCause().getMessage());
					t.printStackTrace();
					throw t;
				}
				return resultMap;
			}

			public static Map<String, Object> objectToMap(Object obj) throws Exception {
				Map<String, Object> map = new HashMap<String, Object>();
				String objClass = obj.getClass().getName();
				if (isBaseTypeForArray(objClass)) {
					map.put(objClass, obj.toString());
				} else {
					// Field[] allField = obj.getClass().getDeclaredFields();
					Field[] allField = getAllFields(obj);
					for (Field field : allField) {
						field.setAccessible(true);
						map.put(field.getName(), field.get(obj));
					}

				}
				return map;
			}

			private static Field[] getAllFields(Object object) {
				Class<?> clazz = object.getClass();
				List<Field> fieldList = new ArrayList<>();
				while (clazz != null) {
					fieldList.addAll(
							new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
					clazz = clazz.getSuperclass();
				}
				Field[] fields = new Field[fieldList.size()];
				fieldList.toArray(fields);
				return fields;
			}

			private static boolean isBaseTypeForArray(String objClassName) {

				boolean isBase = false;
				if (objClassName != null) {
					isBase = BASE_TYPE.get(objClassName) != null ? true : false;
				}
				return isBase;
			}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
