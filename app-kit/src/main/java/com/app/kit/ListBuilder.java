package com.app.kit;

import java.io.Serializable;
import java.util.List;

/**
 * 采用建造者模式，使得新建List并加入数据可以链式调用
 * @param <T> 数据类型
 * @author qiangt
 * @since 2020-08-06
 */
public class ListBuilder<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<T> innerList;

	public ListBuilder(final List<T> list) {
		this.innerList = list;
	}

	public static <T> ListBuilder<T> builder(final List<T> list) {
		return new ListBuilder<>(list);
	}

	public ListBuilder<T> add(final T t) {
		this.innerList.add(t);
		return this;
	}

	public ListBuilder<T> addAll(final List<T> list) {
		this.innerList.addAll(list);
		return this;
	}

	public List<T> list() {
		return this.innerList;
	}

	public List<T> build() {
		return this.innerList;
	}

}
