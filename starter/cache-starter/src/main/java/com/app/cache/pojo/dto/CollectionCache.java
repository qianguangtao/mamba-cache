package com.app.cache.pojo.dto;

import com.app.cache.annotation.CollectionCacheable;
import lombok.Builder;
import lombok.Getter;

/**
 * @author qiangt
 * @date 2023/9/27
 * @apiNote 集合缓存VO，简化方法传参
 */
@Builder
@Getter
public class CollectionCache {
    /**
     * 缓存前缀
     */
    private String prefix;
    /**
     * 缓存后缀
     */
    private String suffix;
    /**
     * 缓存值
     */
    private Object result;
    /**
     * 缓存时间和单位
     */
    private CollectionCacheable cacheable;
}
