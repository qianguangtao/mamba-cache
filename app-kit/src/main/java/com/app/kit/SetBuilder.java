package com.app.kit;

import java.io.Serializable;
import java.util.Set;

/**
 * 采用建造者模式，使得新建Set并加入数据可以链式调用
 * @param <T> 数据类型
 * @author qiangt
 * @since 2020-08-06
 */
public class SetBuilder<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Set<T> innerSet;

	public SetBuilder(final Set<T> list) {
		this.innerSet = list;
	}

	public static <T> SetBuilder<T> builder(final Set<T> set) {
		return new SetBuilder<>(set);
	}

	public SetBuilder<T> add(final T t) {
		this.innerSet.add(t);
		return this;
	}

	public SetBuilder<T> addAll(final Set<T> set) {
		this.innerSet.addAll(set);
		return this;
	}

	public Set<T> set() {
		return this.innerSet;
	}

	public Set<T> build() {
		return this.innerSet;
	}

}
