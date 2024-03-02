package com.app.cache.pojo.dto;

import com.app.cache.enums.CacheOperation;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author qiangt
 * @date 2023/9/14
 * @apiNote
 */
@ToString
@Data
@NoArgsConstructor
public class CacheDTO implements Serializable {

    /**
     * 实例ID，标识不同的服务，Caffeine集群同步的时候，本服务不用更新
     */
    private String instanceId;

    /**
     * spring cache的cacheName
     */
    private CacheOperation cacheOperation;

    /**
     * spring cache的cacheName
     */
    private String cacheName;

    /**
     * spring cache的key
     */
    private Object key;

    /**
     * 缓存中的值
     */
    private Object value;
}
