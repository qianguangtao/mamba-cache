package com.app.core.disruptor;

import cn.hutool.core.util.ObjectUtil;
import com.app.kit.SpringKit;
import com.lmax.disruptor.dsl.Disruptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/12/28 15:12
 * @description: disruptor消息发送
 */
@Slf4j
@RequiredArgsConstructor
public class DisruptorPublisher {
    public static <T> void send(T msg) {

        Disruptor<Event> disruptor = null;
        Long seq = null;
        try {
            disruptor = SpringKit.getBean(Disruptor.class);
            Event commEvent = new Event(msg);
            seq = disruptor.getRingBuffer().next();
            Event userEvent = disruptor.get(seq);
            userEvent.setValue(commEvent.getValue());
        } catch (Exception e) {
            log.error("disruptor发送消息失败：{}", e.getMessage(), e);
        } finally {
            if (ObjectUtil.isNotNull(disruptor)) {
                disruptor.getRingBuffer().publish(seq);
            }
        }


    }
}
