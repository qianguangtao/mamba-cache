package com.app.demo.configuration;

import com.app.core.configuration.CommonProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/3/2 10:44
 * @description: swagger
 */
@Configuration
@EnableSwagger2WebMvc
public class Knife4jConfiguration {
    @Bean(value = "dockerBean")
    public Docket dockerBean() {
        if (!CommonProperties.instance().isSwaggerEnabled()) {
            return null;
        }
        // 指定使用Swagger2规范
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        // 描述字段支持Markdown语法
                        .description("# Knife4j RESTful APIs")
                        .termsOfServiceUrl("https://doc.xiaominfo.com/")
                        .contact("xiaoymin@foxmail.com")
                        .version("1.0")
                        .build())
                // 分组名称
                .groupName("用户服务")
                .select()
                // 这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.app.demo.controller"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }
}
