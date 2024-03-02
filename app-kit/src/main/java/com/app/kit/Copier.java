package com.app.kit;

import org.apache.commons.collections4.MapUtils;
import org.springframework.cglib.beans.BeanCopier;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对象属性复制工具类
 * @author yangjain
 * @since 2022-10-20
 */
public class Copier {

	/**
	 * beanCopier缓存，由sourceClass和targetClass可以确定一个唯一的BeanCopier，因此使用二级Map；
	 */
	private static final Map<Class<?>, Map<Class<?>, BeanCopier>> beanCopierCache = new ConcurrentHashMap<>();

	/**
	 * 指定目标类进行拷贝
	 * @param sourceBean  源对象
	 * @param targetClass 目标类
	 */
	public static <S, T> T copy(final S sourceBean, final Class<T> targetClass) {
		try {
			Objects.requireNonNull(sourceBean, "源对象不能为空");
			Objects.requireNonNull(targetClass, "目标类不能为空");
			final T targetBean = targetClass.newInstance();
			copy(sourceBean, targetClass.newInstance());
			return targetBean;
		} catch (final InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 直接指定目标对象进行拷贝
	 * @param sourceBean 源对象
	 * @param targetBean 目标对象
	 */
	public static <S, T> void copy(final S sourceBean, final T targetBean) {
		@SuppressWarnings("unchecked") final Class<S> sourceClass = (Class<S>) sourceBean.getClass();
		@SuppressWarnings("unchecked") final Class<T> targetClass = (Class<T>) targetBean.getClass();
		final BeanCopier beanCopier = getBeanCopier(sourceClass, targetClass);
		beanCopier.copy(sourceBean, targetBean, null);
	}

	private static <S, T> BeanCopier getBeanCopier(final Class<S> sourceClass, final Class<T> targetClass) {
		final Map<Class<?>, BeanCopier> map = beanCopierCache.get(sourceClass);
		if (MapUtils.isEmpty(map)) {
			final BeanCopier newBeanCopier = BeanCopier.create(sourceClass, targetClass, false);
			final Map<Class<?>, BeanCopier> newMap = new ConcurrentHashMap<>();
			newMap.put(targetClass, newBeanCopier);
			beanCopierCache.put(sourceClass, newMap);
			return newBeanCopier;
		}
		final BeanCopier beanCopier = map.get(targetClass);
		if (Objects.isNull(beanCopier)) {
			final BeanCopier newBeanCopier = BeanCopier.create(sourceClass, targetClass, false);
			map.put(targetClass, newBeanCopier);
			return newBeanCopier;
		}
		return beanCopier;
	}

}


