package com.app.kit;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 反射调用spring容器中bean的方法
 * @author qiangt
 * @since 2022-03-22
 */
@Service
@Slf4j
public class BeanInvoker implements ApplicationContextAware {

	private static final List<Class<?>> WRAP_CLASSES = Arrays.asList(
			Integer.class,
			Boolean.class,
			Double.class,
			Byte.class,
			Short.class,
			Long.class,
			Float.class,
			Double.class,
			BigDecimal.class,
			String.class
	);
	private static final List<Class<?>> PRIMITIVE_CLASSES = Arrays.asList(
			Long.TYPE,
			Double.TYPE,
			Float.TYPE,
			Boolean.TYPE,
			Character.TYPE,
			Byte.TYPE,
			Void.TYPE,
			Short.TYPE
	);
	private ApplicationContext applicationContext;

	/**
	 * 反射调用spring的bean
	 * 不管目标方法有多少个形参，实际的参数将所有的形参对应的值放在Map中，如果是对象则Map中存放的是对象各个属性的键值对
	 * @param clazzName  类名
	 * @param methodName 方法名
	 * @param paramMap   实际参数
	 */
	// @Async("asyncThreadPoolTaskExecutor")
	public void invokeSpringBean(String clazzName, final String methodName, final Map<String, Object> paramMap)
			throws InvocationTargetException, InstantiationException, IllegalAccessException {
		if (StringUtils.isEmpty(clazzName) || StringUtils.isEmpty(methodName)) {
			return;
		}
		if (!clazzName.endsWith("Impl")) {
			clazzName += "Impl";
		}
		if (!this.applicationContext.containsBean(clazzName)) {
			throw new RuntimeException("Spring找不到对应的Bean");
		}
		// 使用rid为了查日志时能找到结束日志
		final String rid = RandomUtil.randomStringUpper(16);
		log.info("开始反射执行方法[{}]，{}.{}: {}", rid, clazzName, methodName, JSON.toJSONString(paramMap));
		// 从Spring中获取代理对象（可能被JDK或者CGLIB代理）
		final Object proxyObject = this.applicationContext.getBean(clazzName);
		// 获取代理对象执行的方法
		final Method proxyMethod = this.getMethod(proxyObject.getClass(), methodName);
		if (Objects.isNull(proxyMethod)) {
			throw new RuntimeException(String.format("没有找到%s类的%s方法", proxyObject.getClass().getSimpleName(), methodName));
		}
		// 获取代理对象中的目标对象
		final Class<?> target = AopUtils.getTargetClass(proxyObject);
		// 获取目标对象的方法，为什么获取目标对象的方法：只有目标对象才能通过 DefaultParameterNameDiscoverer 获取参数的方法名，代理对象由于可能被JDK或CGLIB代理导致获取不到参数名
		final Method targetMethod = this.getMethod(target, methodName);
		// 获取方法执行的参数
		final List<Object> args = this.getMethodActualParameterValues(targetMethod, paramMap);
		final Instant start = Instant.now();
		// 执行方法
		proxyMethod.invoke(proxyObject, args.toArray());
		log.info("结束反射执行方法[{}]，{}.{}: {}，耗时（毫秒）: {}", rid, clazzName, methodName, JSON.toJSONString(paramMap), Instant.now().toEpochMilli() - start.toEpochMilli());
	}

	/**
	 * 获取对象方法
	 * @param target     目标对象
	 * @param methodName 方法名
	 * @return 目标对象的方法
	 */
	private Method getMethod(final Class<?> target, final String methodName) {
		final Method[] methods = target.getMethods();
		for (final Method method : methods) {
			if (method.getName().equalsIgnoreCase(methodName)) {
				return method;
			}
		}
		return null;
	}

	/**
	 * 获取方法实际参数，不支持基本类型
	 * @param method   方法
	 * @param paramMap 参数值
	 */
	private List<Object> getMethodActualParameterValues(final Method method, final Map<String, Object> paramMap)
			throws InstantiationException, IllegalAccessException, InvocationTargetException {
		// 实际参数值
		final List<Object> parameterValues = new ArrayList<>();
		// 参数名
		final String[] parameterNames = this.getParameterNames(method);
		// 参数类型
		final Class<?>[] parameterTypes = method.getParameterTypes();
		if (ArrayUtils.isEmpty(parameterNames)) {
			log.info("方法{}形参名为空", method.getName());
			return parameterValues;
		}
		if (ArrayUtil.isEmpty(parameterTypes)) {
			log.info("方法{}形参类型为空", method.getName());
			return parameterValues;
		}
		for (int i = 0, length = parameterTypes.length; i < length; i++) {
			final Class<?> parameterType = parameterTypes[i];
			assert parameterNames != null;
			final String parameterName = parameterNames[i];
			// 实际参数对象
			Object pv = null;
			// 包装类型
			if (WRAP_CLASSES.contains(parameterType)) {
				if (paramMap.containsKey(parameterName)) {
					pv = ConvertUtils.convert(paramMap.get(parameterName), parameterType);
				}
			}
			// 基本类型
			else if (PRIMITIVE_CLASSES.contains(parameterType)) {
				if (paramMap.containsKey(parameterName)) {
					pv = ConvertUtils.convert(paramMap.get(parameterName), parameterType);
				}
			}
			// 对象类型
			else if (!parameterType.isPrimitive()) {
				// 集合类型
				if (parameterType.isAssignableFrom(List.class)) {
					pv = paramMap.get(parameterName);
				} else if (parameterType.isAssignableFrom(Map.class)) {
					pv = paramMap.get(parameterName);
				} else if (parameterType.isAssignableFrom(Set.class)) {
					pv = paramMap.get(parameterName);
				}
				// 对象类型
				else {
					pv = parameterType.newInstance();
					// 赋值
					BeanUtils.populate(pv, paramMap);
				}
			}
			parameterValues.add(pv);
		}
		return parameterValues;
	}

	private String[] getParameterNames(final Method method) {
		// 利用Spring提供的类获取方法形参名
		final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
		// 参数名称
		String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
		// 如果拿不到原始参数名，则获取编译后的参数名，形如：arg0，arg1
		if (ArrayUtils.isEmpty(parameterNames)) {
			final List<String> pNames = Arrays.stream(method.getParameters()).map(Parameter::getName).collect(Collectors.toList());
			parameterNames = pNames.toArray(new String[0]);
		}
		return parameterNames;
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * 获取类型实例
	 * @param parameterType 参数类型
	 * @return 实例化后的对象
	 */
	private Object getInstance(final Class<?> parameterType) throws InstantiationException, IllegalAccessException {
		if (parameterType.isAssignableFrom(List.class)) {
			return new ArrayList<>();
		} else if (parameterType.isAssignableFrom(Map.class)) {
			return new HashMap<>();
		} else if (parameterType.isAssignableFrom(Set.class)) {
			return new HashSet<>();
		}
		return parameterType.newInstance();
	}

}
