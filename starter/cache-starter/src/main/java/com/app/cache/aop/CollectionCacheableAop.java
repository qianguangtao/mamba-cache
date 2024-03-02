package com.app.cache.aop;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.app.cache.annotation.CollectionCacheable;
import com.app.cache.pojo.dto.CollectionCache;
import com.app.cache.service.CacheService;
import com.app.kit.AopUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author qiangt
 * @date 2023/9/13
 * @apiNote
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CollectionCacheableAop {
    @Qualifier("multiLevelCacheService")
    @Resource
    private CacheService cacheService;

    @Pointcut("@annotation(com.app.cache.annotation.CollectionCacheable)")
    public void pointcut() {

    }

    @Around(value = "pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws JsonProcessingException {
        // 获取@CollectionCacheable的value
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        CollectionCacheable cacheable = method.getAnnotation(CollectionCacheable.class);
        final String prefix = cacheable.value();
        final String suffix = getCacheSuffix(joinPoint);
        final Object cacheObj = cacheService.getCollectionCacheByKey(prefix, suffix, method);
        if (ObjectUtil.isNotEmpty(cacheObj)) {
            return cacheObj;
        }
        try {
            final Object result = joinPoint.proceed();
            cacheService.writeCollectionCache(CollectionCache.builder()
                    .prefix(prefix)
                    .suffix(suffix)
                    .result(result)
                    .cacheable(cacheable)
                    .build());
            return result;
        } catch (Throwable throwable) {
            log.error("Query failed {}", throwable.getMessage(), throwable);
            throw new RuntimeException(throwable.getMessage());
        }
    }

    /**
     * @author qiangt
     * @date 2023/9/15
     * @apiNote 对添加@CollectionCacheable注解方法的参数根据name ascii排序后，md5加密生成缓存key,
     * 如果方法无参数，使用方法名作为缓存key
     */
    private String getCacheSuffix(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String suffix = method.getName();
        Map<String, Object> params = AopUtil.getParamFieldMap(joinPoint);
        String paramStr = params.entrySet().stream().filter(e -> ObjectUtil.isNotEmpty(e.getValue())).map(e -> {
            return e.getKey() + "=" + e.getValue();
        }).collect(Collectors.joining("&"));
        if (ObjectUtil.isNotEmpty(paramStr)) {
            suffix = DigestUtil.md5Hex(paramStr);
        }
        return suffix;
    }


}
