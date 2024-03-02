package com.app.core.configuration;

import com.app.core.exception.DefaultExceptionTranslator;
import com.app.core.exception.ExceptionTranslator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author qiangt
 * @since 2023/6/14 17:12
 */
@Configuration
@Slf4j
public class CommonAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("配置 CommonAutoConfiguration");
    }

    @Bean
    @ConditionalOnMissingBean(ExceptionTranslator.class)
    public ExceptionTranslator exceptionTranslator() {
        return new DefaultExceptionTranslator();
    }


}
