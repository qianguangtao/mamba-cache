package com.app.demo.cache.init;

import com.app.cache.service.CacheSubscriber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author qiangt
 * @date 2023/9/14
 * @apiNote
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheApplicationRunner implements ApplicationRunner {

    private final CacheSubscriber cacheSubscriber;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        cacheSubscriber.subscribeCacheTopic();
    }
}
