package com.app.kit;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 构建链式调用，只能操作对象，不支持原始数据类型
 * @author qiangt 2019/1/5 .
 */
@SuppressWarnings({"unchecked"})
public final class Then<T> {
	private T obj;

	private Then() {
		this.obj = null;
	}

	public Then(final T obj) {
		this.obj = obj;
	}

	/**
	 * hasTrue == true 时才执行 supplier，获得新
	 * @param hasTrue  boolean
	 * @param supplier {@link Supplier}
	 * @return {@link Then}
	 */
	public static <T> Then<T> of(final boolean hasTrue, final Supplier<T> supplier) {
		return new Then<>(hasTrue ? supplier.get() : null);
	}

	/**
	 * 判断 Then 数组任何一个值 != null ，就返回 true
	 * @param array {@link Then}[]
	 * @return {@link Then<Boolean>} true:有非空，false:无非空
	 */
	public static Then<Boolean> parallelNonNull(final Then<Boolean>... array) {
		for (final Then<Boolean> arr : array) {
			if (arr.isPresent()) {
				return Then.of(true);
			}
		}
		return Then.of(null);
	}

	/**
	 * 判断 obj != null
	 * @return {@link Boolean} true:非空，false:空
	 */
	public boolean isPresent() {
		return Objects.nonNull(this.obj);
	}

	/**
	 * 构造链式调用对象
	 * @param obj T
	 * @return {@link Then}
	 */
	public static <T> Then<T> of(final T obj) {
		return new Then<>(obj);
	}

	/**
	 * 获取链式操作的值
	 * @return T
	 */
	public T get() {
		return this.obj;
	}

	/**
	 * 执行 consumer
	 * @param consumer {@link Consumer}
	 * @return {@link Then}
	 */
	public Then<T> then(final Consumer<T> consumer) {
		Objects.requireNonNull(consumer, "参数【consumer】不能为null").accept(this.obj);
		return this;
	}

	/**
	 * Objects.nonNull(value) 时才执行 consumer
	 * @param value    {@link Object}
	 * @param consumer {@link Consumer}
	 * @return {@link Then}
	 */
	public Then<T> then(final Object value, final Consumer<T> consumer) {
		if (Objects.nonNull(value)) {
			Objects.requireNonNull(consumer, "参数【consumer】不能为null").accept(this.obj);
		}
		return this;
	}

	/**
	 * hasTrue == true 时才执行 consumer
	 * @param hasTrue  boolean
	 * @param consumer {@link Consumer}
	 * @return {@link Then}
	 */
	public Then<T> then(final boolean hasTrue, final Consumer<T> consumer) {
		if (hasTrue) {
			Objects.requireNonNull(consumer, "参数【consumer】不能为null").accept(this.obj);
		}
		return this;
	}

	//    /**
	//     * Objects.nonNull(value) 时才执行 supplier，获得新值
	//     *
	//     * @param value    {@link Object}
	//     * @param supplier {@link Supplier}
	//     * @return {@link Then}
	//     */
	//    public Then<T> map(final Object value, final Supplier<T> supplier) {
	//        if (Objects.nonNull(value))
	//            this.obj = Objects.requireNonNull(supplier, "参数【supplier】不能为null").get();
	//        return this;
	//    }

	/**
	 * 重新设置值
	 * @param value <T>
	 * @return {@link Then}
	 */
	public Then<T> map(final T value) {
		this.obj = value;
		return this;
	}

	/**
	 * 执行 function，获得新值
	 * @param function {@link Function}
	 * @return {@link Then}
	 */
	public <R> Then<R> map(final Function<T, R> function) {
		return Then.of(Objects.requireNonNull(function, "参数【function】不能为null").apply(this.obj));
	}

	/**
	 * hasTrue == true 时才执行 supplier，获得新值
	 * @param hasTrue  boolean
	 * @param supplier {@link Supplier}
	 * @return {@link Then}
	 */
	public Then<T> map(final boolean hasTrue, final Supplier<T> supplier) {
		if (hasTrue) {
			this.obj = Objects.requireNonNull(supplier, "参数【supplier】不能为null").get();
		}
		return this;
	}

	// /**
	//  * 当 obj == null 时 执行 call 方法
	//  * @param call {@link ICall}
	//  * @return {@link Then}
	//  */
	// public Then<T> elsePresent(final Call call) {
	//     if (Objects.isNull(obj)) call.call();
	//     return this;
	// }

	/**
	 * <pre>
	 * 当 obj != null 时 执行 consumer 方法
	 * ifPresent(hasError->{})
	 * @param consumer {@link Consumer}
	 * @return {@link Then}
	 */
	public Then<T> ifPresent(final Consumer<T> consumer) {
		if (Objects.nonNull(this.obj)) {
			consumer.accept(this.obj);
		}
		return this;
	}

	/**
	 * 将 obj 置空
	 * @return {@link Then}
	 */
	public Then<T> end() {
		this.obj = null;
		return this;
	}

}
