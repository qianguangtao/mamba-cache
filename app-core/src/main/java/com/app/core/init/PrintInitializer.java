package com.app.core.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/10/17 16:39
 * @description: Spring初始化打印自动装配的Bean
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PrintInitializer implements CommandLineRunner {

    private final ApplicationContext applicationContext;

    @Override
    public void run(String... args) throws Exception {
        log.info("Spring自动装配的Bean:");
        log.info(String.join("\n", applicationContext.getBeanDefinitionNames()));
    }
}
