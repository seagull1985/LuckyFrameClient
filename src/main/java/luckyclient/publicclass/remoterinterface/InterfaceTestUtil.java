package luckyclient.publicclass.remoterinterface;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
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
public class InterfaceTestUtil {
	private static Logger log = LoggerFactory
			.getLogger(InterfaceTestUtil.class);
	private static final ObjectMapper MAP = new ObjectMapper();
	private static final int READ_TIME_OUT = 30 * 1000;
	private static final Map<String, Class<?>> BASE_TYPE = new HashMap<String, Class<?>>();
	static {
		BASE_TYPE.put("long", long.class);
		BASE_TYPE.put("double", double.class);
		BASE_TYPE.put("float", float.class);
		BASE_TYPE.put("bool", boolean.class);
		BASE_TYPE.put("char", char.class);
		BASE_TYPE.put("byte", byte.class);
		BASE_TYPE.put("void", void.class);
		BASE_TYPE.put("short", short.class);
	}

/*	public static Map<String, Object> doTest(InterfaceObject object)
			throws Exception {
		// 获取带请求参数列表将（参数类型，参数值）数组转成InterfaceParamObject数组
		InterfaceParamObject[] paramContent = MAP.readValue(object.getParams(),
				InterfaceParamObject[].class);
		Object result = hessian(object.getRemoteUrl(),
				object.getInterfaceClass(), object.getInterfaceMethod(),
				paramContent);

		if (result != null) {
			log.info("返回结果对象是{},返回原始结果是{}", result.getClass(),
					result.toString());
			return objectToMap(result);
		}
		log.info("返回结果对象是null");
		return null;

	}

	public static Map<String, Object> objectToMap(Object obj) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>(0);
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
		for (Object key : map.keySet()) {
			System.out.println("KEY:" + key + ", VALUE:" + map.get(key));
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

	public static String genJsonStr(Object object)
			throws JsonProcessingException {
		return MAP.writeValueAsString(object);
	}

	private static Object hessian(String remoteUrl, String className,
			String methodName, InterfaceParamObject[] paramContent)
			throws Exception {
		HessianProxyFactory factory = new HessianProxyFactory();
		factory.setReadTimeout(READ_TIME_OUT);
		Object interfaceObj = null;
		Class<?> interfaceClassName = null;

		try {
			interfaceClassName = Class.forName(className);
			interfaceObj = factory.create(interfaceClassName, remoteUrl);
			log.info("调用MyHessianProxyFactory返回的hessian代理对象为：{}",
					interfaceObj.getClass());
		} catch (Exception ex) {
			log.info("程序在反射获取接口的hesian代理对象时出现问题。异常信息：{}", ex.getMessage());
		}
		Object[] allRequstParas = new Object[paramContent.length];
		for (int i = 0; i < paramContent.length; i++) {
			InterfaceParamObject paramObject = paramContent[i];
			Class<?> paramClass = null;

			try {
				String paramClassName = paramObject.getClassname();

				paramClass = getBaseTypeClassByName(paramClassName);
				if (paramClass == null) {
					paramClass = Class.forName(paramObject.getClassname());
				}
			} catch (Exception ex) {
				log.info("程序在反射获取接口的参数类类型时出现问题。异常信息{}", ex.getMessage());
			}
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(
					DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Object requestParamObject = null;
			if (paramObject.getValue() != null
					&& paramObject.getValue().trim().length() > 0) {
				try {

					requestParamObject = objectMapper
							.readValue(paramObject.getValue(), paramClass);
				} catch (Exception ex) {
					log.info("根据Json字符串参数转成类%s对象时发生异常，对象预期值为%s",
							requestParamObject.getClass(),
							paramObject.getValue());
				}
			} else {
				requestParamObject = paramClass.getInterfaces();
			}
			allRequstParas[i] = requestParamObject;
		}

		if (interfaceObj != null && allRequstParas.length > 0) {
			return getMethod(methodName, interfaceObj, allRequstParas);
		}
		log.info("没有请求参数，返回空对象");
		return null;
	}

	private static Object getMethod(String methodName, Object o,
			Object[] allRequstParas) {
		int totalRequestParam = allRequstParas.length;

		Class<?>[] allRequstParasClass = null;
		// 存在
		if (allRequstParas != null) {
			int len = allRequstParas.length;
			allRequstParasClass = new Class[len];
			for (int i = 0; i < len; ++i) {
				allRequstParasClass[i] = allRequstParas[i].getClass();
			}
		}
		Object result = new Object[totalRequestParam];
		Method method = null;
		try {
			// 根据方法名以及方法参数列表获取方法对象
			method = o.getClass().getDeclaredMethod(methodName,
					allRequstParasClass);
		} catch (NoSuchMethodException ex) {
			log.error(String.format("获取方法对象发生找不到方法，方法的类为%s,异常信息:%s",
					o.getClass(), ex.getMessage()));
		} catch (SecurityException ex) {
			log.error(String.format("获取方法对象发生发生安全异常，方法的类为%s,异常信息:%s",
					o.getClass(), ex.getMessage()));
		}
		try {
			result = method.invoke(o, allRequstParas);
		} catch (Exception ex) {
			log.error(String.format("调用方法对象执行调用发生异常，方法的类为%s,异常信息:%s",
					o.getClass(), ex.getMessage()));
		}
		return result;
	}

	*//**
	 * 1、转换基本数据类型为包装类型<br>
	 * 2、…<br>
	 * 
	 * @param className
	 * @return
	 * @see
	 *//*
	private static Class<?> getBaseTypeClassByName(String className) {
		return BASE_TYPE.get(className);
	}

	private static Boolean isBaseType(String className) {
		Boolean istrue = false;
		for (String key : BASE_TYPE.keySet()) {
			istrue = className.equals(key) ? true : false;
		}
		return istrue;
	}*/
}