package com.app.kit;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 反射工具类
 */
@Slf4j
public class BeanRefUtil {

	public static void checkAllFieldNull(final Object obj) {
		final Class stuCla = obj.getClass();// 得到类对象
		final Field[] fs = stuCla.getDeclaredFields();// 得到属性集合
		final boolean flag = true;
		for (final Field f : fs) {// 遍历属性
			f.setAccessible(true); // 设置属性是可以访问的(私有的也可以)
			Object val = null;// 得到此属性的值
			try {
				val = f.get(obj);
				if (val != null) {// 只要有1个属性不为空,那么就不是所有的属性值都为空
					throw new IllegalArgumentException("字段【" + f.getName() + "】不为空");
				}
			} catch (final IllegalAccessException e) {
				log.error("反射错误", e);
				throw new IllegalStateException("反射错误");
			}

		}
	}

	public static void checkAllFieldNotNull(final Object obj) {
		if (Objects.isNull(obj)) {
			throw new IllegalArgumentException("待校验对象不能为空");
		}
		final Class stuCla = obj.getClass();// 得到类对象
		final Field[] fs = stuCla.getDeclaredFields();// 得到属性集合
		for (final Field f : fs) {// 遍历属性
			f.setAccessible(true); // 设置属性是可以访问的(私有的也可以)
			Object val = null;// 得到此属性的值
			try {
				val = f.get(obj);
				if (Objects.isNull(val)) {
					throw new IllegalArgumentException("字段【" + f.getName() + "】为空");
				}
			} catch (final IllegalAccessException e) {
				log.error("反射错误", e);
				throw new IllegalStateException("反射错误");
			}

		}
	}

	public static void checkFieldNotNull(@NotNull final Object object, final MethodSupplier... methods) {
		if (null != methods && methods.length > 0) {
			for (int i = 0; i < methods.length; i++) {
				final String methodName = getMethodName(methods[i]);
				try {
					final Object value = object.getClass().getDeclaredMethod(methodName).invoke(object);
					final String fieldName = StrUtil.lowerFirst(methodName.replace("get", ""));
					if (Objects.isNull(value)) {
						throw new IllegalArgumentException("字段【" + fieldName + "】不能为空！");
					}
				} catch (final NoSuchMethodException e) {
					throw new IllegalArgumentException("方法未找到异常");
				} catch (final IllegalAccessException e) {
					throw new IllegalArgumentException("非法访问异常");
				} catch (final InvocationTargetException e) {
					throw new IllegalArgumentException("调用异常");
				}
			}
		}
	}

	private static <T> String getMethodName(final MethodSupplier lambda) {
		try {
			final Class lambdaClass = lambda.getClass();
			final Method method = lambdaClass.getDeclaredMethod("writeReplace");
			// writeReplace是私有方法，需要去掉私有属性
			method.setAccessible(Boolean.TRUE);
			// 手动调用writeReplace()方法，返回一个SerializedLambda对象
			final SerializedLambda serializedLambda = (SerializedLambda) method.invoke(lambda);
			// 得到lambda表达式中调用的方法名，如 "User::getSex"，则得到的是"getSex"
			return serializedLambda.getImplMethodName();
		} catch (final ReflectiveOperationException e) {
			throw new IllegalArgumentException("获取MethodFunc方法名异常");
		}
	}

	/**
	 * 取Bean的属性和值对应关系的MAP
	 * @return Map
	 */
	public static Map<String, String> getFieldValueMap(final Object bean) {
		final Class<?> cls = bean.getClass();
		final Map<String, String> valueMap = new HashMap<String, String>();
		final Method[] methods = cls.getDeclaredMethods();
		final Field[] fields = cls.getDeclaredFields();
		for (final Field field : fields) {
			try {
				final String fieldType = field.getType().getSimpleName();
				final String fieldGetName = parGetName(field.getName());
				if (!checkGetMet(methods, fieldGetName)) {
					continue;
				}
				final Method fieldGetMet = cls
						.getMethod(fieldGetName);
				final Object fieldVal = fieldGetMet.invoke(bean);
				String result = null;
				if ("Date".equals(fieldType)) {
					result = fmtDate((Date) fieldVal);
				} else {
					if (null != fieldVal) {
						result = String.valueOf(fieldVal);
					}
				}
				// String fieldKeyName = parKeyName(field.getName());
				valueMap.put(field.getName(), result);
			} catch (final Exception e) {
				continue;
			}
		}
		return valueMap;
	}

	/**
	 * 拼接某属性的 get方法
	 * @return String
	 */
	public static String parGetName(final String fieldName) {
		if (null == fieldName || "".equals(fieldName)) {
			return null;
		}
		int startIndex = 0;
		if (fieldName.charAt(0) == '_')
			startIndex = 1;
		return "get"
				+ fieldName.substring(startIndex, startIndex + 1).toUpperCase()
				+ fieldName.substring(startIndex + 1);
	}

	/**
	 * 判断是否存在某属性的 get方法
	 * @return boolean
	 */
	public static boolean checkGetMet(final Method[] methods, final String fieldGetMet) {
		for (final Method met : methods) {
			if (fieldGetMet.equals(met.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 日期转化为String
	 * @return date string
	 */
	public static String fmtDate(final Date date) {
		if (null == date) {
			return null;
		}
		try {
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdf.format(date);
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * set属性的值到Bean
	 */
	public static void setFieldValue(final Object bean, final Map<String, String> valMap) {
		final Class<?> cls = bean.getClass();
		// 取出bean里的所有方法
		final Method[] methods = cls.getDeclaredMethods();
		final Field[] fields = cls.getDeclaredFields();

		for (final Field field : fields) {
			try {
				final String fieldSetName = parSetName(field.getName());
				if (!checkSetMet(methods, fieldSetName)) {
					continue;
				}
				final Method fieldSetMet = cls.getMethod(fieldSetName,
						field.getType());
				// String fieldKeyName = parKeyName(field.getName());
				final String fieldKeyName = field.getName();

				final String value = valMap.get(fieldKeyName);

				if (null != value && !"".equals(value)) {
					final String fieldType = field.getType().getSimpleName();
					if ("String".equals(fieldType)) {
						fieldSetMet.invoke(bean, value);
					} else if ("Date".equals(fieldType)) {
						final Date temp = parseDate(value);
						fieldSetMet.invoke(bean, temp);
					} else if ("Integer".equals(fieldType)
							|| "int".equals(fieldType)) {
						final Integer intval = Integer.parseInt(value);
						fieldSetMet.invoke(bean, intval);
					} else if ("Long".equalsIgnoreCase(fieldType)) {
						final Long temp = Long.parseLong(value);
						fieldSetMet.invoke(bean, temp);
					} else if ("Double".equalsIgnoreCase(fieldType)) {
						final Double temp = Double.parseDouble(value);
						fieldSetMet.invoke(bean, temp);
					} else if ("Boolean".equalsIgnoreCase(fieldType)) {
						final Boolean temp = Boolean.parseBoolean(value);
						fieldSetMet.invoke(bean, temp);
					} else {
						System.out.println("not supper type" + fieldType);
					}
				}
			} catch (final Exception e) {
				continue;
			}
		}
	}

	/**
	 * 拼接在某属性的 set方法
	 * @return String
	 */
	public static String parSetName(final String fieldName) {
		if (null == fieldName || "".equals(fieldName)) {
			return null;
		}
		int startIndex = 0;
		if (fieldName.charAt(0) == '_')
			startIndex = 1;
		return "set"
				+ fieldName.substring(startIndex, startIndex + 1).toUpperCase()
				+ fieldName.substring(startIndex + 1);
	}

	/**
	 * 判断是否存在某属性的 set方法
	 * @return boolean
	 */
	public static boolean checkSetMet(final Method[] methods, final String fieldSetMet) {
		for (final Method met : methods) {
			if (fieldSetMet.equals(met.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 格式化string为Date
	 * @return date
	 */
	public static Date parseDate(final String datestr) {
		if (null == datestr || "".equals(datestr)) {
			return null;
		}
		try {
			String fmtstr = null;
			if (datestr.indexOf(':') > 0) {
				fmtstr = "yyyy-MM-dd HH:mm:ss";
			} else {
				fmtstr = "yyyy-MM-dd";
			}
			final SimpleDateFormat sdf = new SimpleDateFormat(fmtstr);
			return sdf.parse(datestr);
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * 获取存储的键名称（调用parGetName）
	 * @return 去掉开头的get
	 */
	public static String parKeyName(final String fieldName) {
		final String fieldGetName = parGetName(fieldName);
		if (fieldGetName != null && fieldGetName.trim() != ""
				&& fieldGetName.length() > 3) {
			return fieldGetName.substring(3);
		}
		return fieldGetName;
	}

	/**
	 * <p>
	 * 反射对象获取泛型
	 * </p>
	 * @param clazz 对象
	 * @param index 泛型所在位置
	 * @return Class
	 */
	@SuppressWarnings("rawtypes")
	public static Class getSuperClassGenricType(final Class clazz, final int index) {
		final Type genType = clazz.getGenericSuperclass();
		if (!(genType instanceof ParameterizedType)) {
			log.warn(String.format("Warn: %s's superclass not ParameterizedType", clazz.getSimpleName()));
			return Object.class;
		}
		final Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		if (index >= params.length || index < 0) {
			log.warn(String.format("Warn: Index: %s, Size of %s's Parameterized Type: %s .", index, clazz.getSimpleName(),
					params.length));
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			log.warn(String.format("Warn: %s not set the actual class on superclass generic parameter", clazz.getSimpleName()));
			return Object.class;
		}
		return (Class) params[index];
	}

	@FunctionalInterface
	public interface MethodSupplier extends Serializable {
		Object apply();
	}

}
