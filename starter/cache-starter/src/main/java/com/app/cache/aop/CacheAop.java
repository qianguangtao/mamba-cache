package com.app.cache.aop;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.app.cache.annotation.CollectionCacheEvict;
import com.app.cache.config.AppCacheProperties;
import com.app.cache.enums.CacheOperation;
import com.app.cache.pojo.dto.CacheDTO;
import com.app.cache.service.CachePublisher;
import com.app.cache.service.CacheService;
import com.app.cache.utils.InstanceIdGenerator;
import com.app.core.util.SpelUtil;
import com.app.kit.AopUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.List;
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
public class CacheAop {
    /** 从返回值中取业务id的spel表达式前缀 */
    private static final String RETURN_SPEL_PREFIX = "result";
    /** spel表达式工具类，用来执行spel获取结果 */
    private final SpelUtil spelUtil;
    private final AppCacheProperties appCacheProperties;
    private final CachePublisher cachePublisher;
    @Qualifier("multiLevelCacheService")
    @Resource
    private CacheService cacheService;

    @Pointcut("@annotation(org.springframework.cache.annotation.CachePut) " +
            "|| @annotation(org.springframework.cache.annotation.CacheEvict)" +
            "|| @annotation(com.app.cache.annotation.CollectionCacheEvict)"
    )
    public void pointcut() {

    }

    @AfterReturning(pointcut = "pointcut()", returning = "returnVal")
    public void afterReturning(JoinPoint joinPoint, Object returnVal) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        CachePut cachePut = method.getAnnotation(CachePut.class);
        CacheEvict cacheEvict = method.getAnnotation(CacheEvict.class);
        CollectionCacheEvict collectionCacheEvict = method.getAnnotation(CollectionCacheEvict.class);
        String cacheName = null;
        String spel = null;
        CacheOperation cacheOperation = null;
        if (ObjectUtil.isNotEmpty(cachePut)) {
            cacheOperation = CacheOperation.PUT;
            cacheName = cachePut.value()[0];
            spel = cachePut.key();
        } else if (ObjectUtil.isNotEmpty(cacheEvict)) {
            cacheOperation = CacheOperation.Evict;
            cacheName = cacheEvict.value()[0];
            spel = cacheEvict.key();
        } else {
            cacheOperation = CacheOperation.CollectionEvict;
            cacheName = collectionCacheEvict.value();
            cacheService.removeBatch(cacheName, (List) joinPoint.getArgs()[0]);
        }
        removeList(cacheName);
        syncCache(joinPoint, spel, cacheName, cacheOperation, returnVal);
    }

    private void removeList(String cacheName) {
        cacheService.removeList(cacheName);
    }

    /**
     * 当使用Caffeine作为CacheManager，需要同步数据到其他服务器
     *
     * @param joinPoint      AOP切面方法的入参，用于获取方法参数名及方法参数值
     * @param spel           spring expression language
     * @param cacheName      spring cache的注解里配的缓存前缀
     * @param cacheOperation 缓存操作枚举
     * @param returnVal      around切面目标方法的返回值
     */
    private void syncCache(JoinPoint joinPoint, String spel, String cacheName, CacheOperation cacheOperation,
                           Object returnVal) {
        if (!appCacheProperties.isSyncEnabled()) {
            return;
        }
        final Map<String, Object> params = AopUtil.getParams(joinPoint);
        Object cacheKey = null;
        if (StrUtil.isNotBlank(spel)) {
            if (spel.indexOf(RETURN_SPEL_PREFIX) < 0) {
                // 更新操作，从入参中获取缓存key
                cacheKey = spelUtil.executeExpression(spel, params);
            } else {
                // 新增操作，从切面方法返回值中获取缓存key
                cacheKey = spelUtil.executeExpression(spel, RETURN_SPEL_PREFIX, returnVal);
            }
        }
        if (ObjectUtil.equals(cacheOperation, CacheOperation.CollectionEvict)) {
            Object arg = joinPoint.getArgs()[0];
            if (arg instanceof List) {
                cacheKey = Convert.toStr(((List) arg).stream().map(Convert::toStr).collect(Collectors.joining(",")));
            }
        }
        final CacheDTO cacheDTO = new CacheDTO();
        cacheDTO.setInstanceId(InstanceIdGenerator.INSTANCE.getInstanceId());
        cacheDTO.setCacheName(cacheName);
        cacheDTO.setKey(cacheKey);
        cacheDTO.setCacheOperation(cacheOperation);
        cacheDTO.setValue(returnVal);
        cachePublisher.publishCacheTopic(cacheDTO);
    }

}
