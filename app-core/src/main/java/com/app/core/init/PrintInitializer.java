package com.app.core.init;

import com.app.core.configuration.CommonProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;

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
    private final CommonProperties commonProperties;
    private final Environment environment;

    @Override
    public void run(String... args) throws Exception {
        if (this.commonProperties.isPrintBeanEnabled()) {
            log.info("Spring自动装配的Bean:");
            log.info(String.join("\n", applicationContext.getBeanDefinitionNames()));
        }
        String ip = this.commonProperties.getIp();
        int port = this.commonProperties.getPort();
        // 打印本地参数信息
        final StringBuilder config = new StringBuilder()
                .append(String.format("%s【%s】 : %s\n", "craftsman.common.env", "当前环境：[dev:本地|sit:系统集成测试|uat:用户验收测试|prod:生产]", this.commonProperties.getEnv()))
                .append(String.format("%s【%s】 : %s\n", "craftsman.common.ip", "当前服务实例IP", this.commonProperties.getIp()))
                .append(String.format("%s【%s】 : %s\n", "craftsman.common.startTime", "项目启动时间", this.commonProperties.getStartTime()))
                .append(String.format("%s【%s】 : %s\n", "craftsman.common.swaggerEnabled", "swagger文档开关，true：开启，false：关闭", Objects.toString(this.commonProperties.isSwaggerEnabled(), "")));
        log.info("\n┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬ 环境配置 ┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬┬\n{}\n┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴ 环境配置 ┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴┴", config);


        String path = this.environment.getProperty("server.servlet.context-path");
        path = StringUtils.isEmpty(path) ? "" : path;
        log.info("\n\t----------------------------------------------------------\n\t" +
                "Application is running! Access URLs:\n\t" +
                "External  : \thttp://" + ip + ":" + port + path + "/\n\t" +
                "Swagger-ui: \thttp://" + ip + ":" + port + path + "/doc.html\n\t" +
                "----------------------------------------------------------");
    }
}
