package com.app.kit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 初始化单例类、实体类、接口需要的bean，因为单例类无法直接注入bean
 *
 * @author 10071
 */
@Component
@Slf4j
@RequiredArgsConstructor
@Order(-1)
public class SpringKit implements BeanFactoryPostProcessor, ApplicationContextAware {

	/**
	 * 缓存从 spring application context 获取到的 bean， 避免每次通过反射获取
	 */
	private static final Map<String, Object> BEAN_MAP = new ConcurrentHashMap<>();
	private static ApplicationContext APP_CONTEXT;

	/**
	 * 获取 Spring Context 对象
	 *
	 * @return ApplicationContext
	 */
	public static ApplicationContext getAppContext() {
		return APP_CONTEXT;
	}

	/**
	 * 获取 bean， 并缓存到 map 集合， 避免每次通过反射获取
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(final Class<T> clazz) {
		if (!BEAN_MAP.containsKey(clazz.getName())) {
			// 缓存从 spring application context 获取到的 bean， 避免每次通过反射获取
			BEAN_MAP.put(clazz.getName(), APP_CONTEXT.getBean(clazz));
		}
		return (T)BEAN_MAP.get(clazz.getName());
	}

	/**
	 * 获取 bean， 并缓存到 map 集合， 避免每次通过反射获取
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(final String beanName, final Class<T> clazz) {
		if (!BEAN_MAP.containsKey(beanName)) {
			// 缓存从 spring application context 获取到的 bean， 避免每次通过反射获取
			BEAN_MAP.put(beanName, APP_CONTEXT.getBean(beanName, clazz));
		}
		return (T)BEAN_MAP.get(clazz.getName());
	}

	public static <T> T getBean(final String beanName) {
		if (!BEAN_MAP.containsKey(beanName)) {
			// 缓存从 spring application context 获取到的 bean， 避免每次通过反射获取
			BEAN_MAP.put(beanName, APP_CONTEXT.getBean(beanName));
		}
		return (T)BEAN_MAP.get(beanName);
	}

	private static synchronized void setContext(ApplicationContext applicationContext) {
		APP_CONTEXT = applicationContext;
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		setContext(applicationContext);
	}

	/**
	 * 实现BeanFactoryPostProcessor的postProcessBeanFactory方法，是为了更早的实例化ApplicationContext， 这里不用添加代码逻辑
	 *
	 * @param configurableListableBeanFactory
	 * @throws BeansException
	 */
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory)
			throws BeansException {

	}
}
