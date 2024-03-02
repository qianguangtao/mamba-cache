package com.app.core.redis;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Objects;

public interface RedisCacheKey {

    /** 注释 */
    String comment();

    /**
     * 默认返回枚举名称作为key， 可以通过 @Override 方法覆盖 key 的生成规则
     * @return {@link String}
     */
    default String key() {
        return this.name();
    }

    /** 名字 */
    String name();

    /**
     * 默认返回枚举名称作为key， 可以通过 @Override 方法覆盖 key 的生成规则
     * @param key String
     * @return {@link String}
     */
    default String key(final String key) {
        if (Objects.isNull(key) || Objects.equals("", key)) {
            throw new IllegalArgumentException("缓存 key 不能为空");
        }
        return String.format("%s:%s", this.name(), key);
    }

    /**
     * 默认返回枚举名称作为key， 可以通过 @Override 方法覆盖 key 的生成规则
     * @param args String[]
     * @return {@link String}
     */
    default String key(final String... args) {
        return String.format("%s:%s", this.name(), String.join(":", args));
    }

    /**
     * 长度大于 32 位的 key 使用 md5 缩短长度
     * @param key String
     * @return {@link String}
     */
    default String keyMD5(final String key) {
        if (Objects.isNull(key) || Objects.equals("", key)) {
            throw new IllegalArgumentException("缓存 key 不能为空");
        }
        return String.format("%s:%s", this.name(), DigestUtils.md5Hex(key));
    }

    /**
     * 长度大于 32 位的 key 使用 md5 缩短长度
     * @param args String[]
     * @return {@link String}
     */
    default String keyMD5(final String... args) {
        return String.format("%s:%s", this.name(), DigestUtils.md5Hex(String.join(":", args)));
    }

}
