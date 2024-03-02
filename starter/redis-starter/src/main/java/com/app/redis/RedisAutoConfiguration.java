package com.app.redis;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author qiangt
 */
@RequiredArgsConstructor
@EnableConfigurationProperties(RedisProperties.class)
@Configuration
public class RedisAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        return createRedissonClient(redisProperties);
    }

    private RedissonClient createRedissonClient(RedisProperties redisProperties) {
        if (Objects.nonNull(redisProperties.getCluster())) {
            Config config = createClusterServersConfig(redisProperties);
            return Redisson.create(config);
        } else {
            Config config = createSingleServerConfig(redisProperties);
            return Redisson.create(config);
        }
    }

    private Config createClusterServersConfig(RedisProperties redisProperties) {
        final Config config = new Config();
        String addressPrefix = "redis://";
        if (redisProperties.isSsl()) {
            addressPrefix = "rediss://";
        }
        final String finalAddressPrefix = addressPrefix;
        final List<String> clusterNodes = redisProperties.getCluster().getNodes().stream().map((n) -> {
            return finalAddressPrefix.concat(n);
        }).collect(Collectors.toList());
        final ClusterServersConfig clusterServersConfig = config.useClusterServers().addNodeAddress(clusterNodes.toArray(new String[clusterNodes.size()]));
        if (StrUtil.isNotBlank(redisProperties.getPassword())) {
            clusterServersConfig.setPassword(redisProperties.getPassword());
        }
        if (redisProperties.isSsl()) {
            clusterServersConfig.setSslEnableEndpointIdentification(false);
        }
        clusterServersConfig.setClientName("RedisClusterServer");
        clusterServersConfig.setPingConnectionInterval(10000);
        config.setLockWatchdogTimeout(15000L);
        return config;
    }

    private Config createSingleServerConfig(RedisProperties redisProperties) {
        final Config config = new Config();

        String addressPrefix = "redis://";
        if (redisProperties.isSsl()) {
            addressPrefix = "rediss://";
        }
        final SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress(addressPrefix + redisProperties.getHost() + ":" + redisProperties.getPort());
        if (Objects.nonNull(redisProperties.getPassword())) {
            singleServerConfig.setPassword(redisProperties.getPassword());
        }
        if (Objects.nonNull(redisProperties.getDatabase())) {
            singleServerConfig.setDatabase(redisProperties.getDatabase());
        }
        if (redisProperties.isSsl()) {
            singleServerConfig.setSslEnableEndpointIdentification(false);
        }
        singleServerConfig.setClientName("RedisSingleServer");
        singleServerConfig.setPingConnectionInterval(10000);
        config.setLockWatchdogTimeout(15000L);
        return config;
    }

    @Bean
    @ConditionalOnProperty(
            value = {"spring.redis.ssl"},
            havingValue = "true"
    )
    public LettuceClientConfigurationBuilderCustomizer lettuceClientConfigurationBuilderCustomizer() {
        return (clientConfigurationBuilder) -> {
            clientConfigurationBuilder.useSsl().disablePeerVerification();
        };
    }
}
