package com.app.cache.utils;

import cn.hutool.core.util.IdUtil;

/**
 * @author qiangt
 * @date 2023/9/27
 * @apiNote 使用枚举做单例生成唯一实例id
 */
public enum InstanceIdGenerator {
    INSTANCE;
    private final String uuid = IdUtil.simpleUUID();

    public String getInstanceId() {
        return uuid;
    }
}
