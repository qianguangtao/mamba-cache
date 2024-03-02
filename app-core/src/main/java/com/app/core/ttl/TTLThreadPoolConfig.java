package com.app.core.ttl;

import com.alibaba.ttl.threadpool.TtlExecutors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * alibaba线程池
 */
@Configuration
@EnableAsync
public class TTLThreadPoolConfig {

    /**
     * 核心线程数
     */
    private final int corePoolSize = 50;
    /**
     * 最大线程数
     */
    private final int maxPoolSize = 50;
    /**
     * 队列数
     */
    private final int queueCapacity = 500;

    @Bean
    public ExecutorService ttlExecutorService() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("ttl-threadpool-service-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setKeepAliveSeconds(60);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return TtlExecutors.getTtlExecutorService(executor.getThreadPoolExecutor());
    }

}
