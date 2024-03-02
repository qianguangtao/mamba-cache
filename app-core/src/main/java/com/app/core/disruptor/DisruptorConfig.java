package com.app.core.disruptor;

import cn.hutool.core.collection.CollUtil;
import com.app.kit.ProxyUtil;
import com.app.kit.SpringKit;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/12/28 15:12
 * @description: disruptor配置类
 */
@Slf4j
@Configuration
public class DisruptorConfig {
    private static final int RING_BUFFER_SIZE = 1024;

    @Bean
    public Disruptor<Event> disruptor() {
        Disruptor<Event> disruptor = null;
        Map<String, AbstractEventHandler> map = SpringKit.getAppContext().getBeansOfType(AbstractEventHandler.class);
        Set<com.lmax.disruptor.EventHandler> eventHandlers = new HashSet<>();
        for (Map.Entry<String, AbstractEventHandler> entryMap : map.entrySet()) {
            try {
                // 通过反射获取相关的实现类的Object
                Object object = ProxyUtil.getTarget(entryMap.getValue());
                if (object != null) {
                    eventHandlers.add((com.lmax.disruptor.EventHandler) object);
                }
            } catch (Exception e) {
                log.error("反射获取@Handler对象失败", e);
            }
        }
        disruptor = new Disruptor<>(Event.FACTORY, RING_BUFFER_SIZE, Executors.defaultThreadFactory(), ProducerType.SINGLE, new BlockingWaitStrategy());
        // 手动添加handler
        if (CollUtil.isNotEmpty(eventHandlers)) {
            for (com.lmax.disruptor.EventHandler handler : eventHandlers) {
                disruptor.handleEventsWith(handler);
            }
        }
        disruptor.start();
        return disruptor;
    }
}
