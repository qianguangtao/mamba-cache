package com.app.core.disruptor;

import com.lmax.disruptor.EventHandler;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/12/28 10:33
 * @description: 抽象EventHandler
 */
public abstract class AbstractEventHandler<T> implements EventHandler<Event<T>> {

    @Override
    public void onEvent(Event<T> tEvent, long l, boolean b) {
        if (filter(tEvent)) {
            T t = tEvent.getValue();
            handler(t);
        }
    }

    /**
     * 判断是否处理接收的消息（由于共用发送DTO Event）
     * @param event
     * @return
     */
    public boolean filter(Event<T> event) {
        return false;
    }

    /**
     * 消息消费者处理具体消息
     * @param t
     */
    public abstract void handler(T t);
}


