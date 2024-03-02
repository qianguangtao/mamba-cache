package com.app.core.configuration;

import cn.hutool.core.date.DateUtil;
import com.app.kit.SpringKit;
import com.app.kit.WebUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Objects;

@ConfigurationProperties(prefix = "mamba.common")
@Component
@Setter
@Getter
public class CommonProperties {

    private static CommonProperties INSTANCE;

    /** 当前主机 IP 地址 */
    private String ip = WebUtil.getHostIp();
    /** 当前服务端口 */
    private int port;
    /** 当前环境 */
    private String env;
    /** 项目启动时间 */
    private String startTime;
    /** 是否在应用启动时打印spring容器中所有bean的名称 */
    private boolean printBeanEnabled;
    /** swagger文档开启状态 */
    private boolean swaggerEnabled;

    @SneakyThrows
    public CommonProperties() {
        this.startTime = DateUtil.now();
    }

    public static CommonProperties instance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (CommonProperties.class) {
                if (Objects.isNull(INSTANCE)) {
                    INSTANCE = SpringKit.getAppContext().getBean(CommonProperties.class);
                }
            }
        }
        return INSTANCE;
    }

}
