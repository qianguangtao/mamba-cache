package com.app.core.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/12/28 10:33
 * @description: Disruptor消息体封装类
 */
public class Event<T> {

    public static final EventFactory<Event> FACTORY = () -> new Event();

    private T value;

    private Event() {
    }

    public Event(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

}
