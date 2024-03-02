package com.app.cache.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.app.cache.annotation.CacheNames;
import com.app.cache.config.AppCacheProperties;
import com.app.kit.SpringKit;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author qiangt
 * @date 2023/9/16
 * @apiNote 扫描当前package前两层包下带@CacheNames注解的枚举类，取name()作为cache中的key前缀
 */
@Slf4j
public class CacheNameSet {

    /**
     * cache中的key前缀set
     */
    private final Set<String> nameSet = new HashSet<>();

    {
        String scanPackage = "com";
        final AppCacheProperties cacheProperties = SpringKit.getBean(AppCacheProperties.class);
        if (ObjectUtil.isNotNull(cacheProperties) && StrUtil.isNotEmpty(cacheProperties.getCacheNamesPackage())) {
            scanPackage = cacheProperties.getCacheNamesPackage();
        }
        log.info("Scan CacheNames package : {}", scanPackage);
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
                .addUrls(ClasspathHelper.forPackage(scanPackage)).addScanners(Scanners.TypesAnnotated);
        Reflections f = new Reflections(configurationBuilder);
        Set<Class<?>> classSet = f.getTypesAnnotatedWith(CacheNames.class);
        log.info("Scan CacheNames result : {}", classSet);
        if (ObjectUtil.isNotEmpty(classSet)) {
            classSet.stream().forEach(c -> {
                final Object[] enumConstants = c.getEnumConstants();
                if (ObjectUtil.isNotEmpty(enumConstants)) {
                    for (Object enumConstant : enumConstants) {
                        if (enumConstant instanceof Enum) {
                            nameSet.add(((Enum) enumConstant).name());
                        }
                    }
                }
            });
        }
    }

    private CacheNameSet() {
    }

    public static Set<String> getAll() {
        final Set<String> nameSet = CacheNameSet.getInstance().nameSet;
        return ObjectUtil.isNotEmpty(nameSet) ? nameSet : Collections.emptySet();
    }

    public static CacheNameSet getInstance() {
        return CacheNameSetHolder.cacheNameSet;
    }

    private static class CacheNameSetHolder {
        static CacheNameSet cacheNameSet = new CacheNameSet();
    }
}
