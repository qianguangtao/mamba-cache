package com.app.demo.controller;


import com.app.cache.service.CacheService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author qiangt
 * @date 2023/9/13
 * @apiNote
 */
@Api(tags = "caffeine操作")
@RestController
@RequestMapping("/caffeines")
@RequiredArgsConstructor
public class CaffeineController {

    @Resource
    @Qualifier("caffeineCacheService")
    CacheService cacheService;
    @Resource
    @Qualifier("caffeineCacheManager")
    private CacheManager cacheManager;

    @ApiOperation(value = "根据缓存类型查询所有caffeine缓存", notes = "缓存类型参看CacheEnum.names")
    @ApiOperationSupport(order = 1)
    @GetMapping("/{name}")
    public Map<Object, Object> getAllCache(@PathVariable String name) {
        final Cache cache = cacheManager.getCache(name);
        return cacheService.getAllCache(cache);
    }

    @ApiOperation(value = "清空caffeine缓存，慎用")
    @ApiOperationSupport(order = 2)
    @GetMapping("/clear")
    public boolean clear() {
        cacheService.clear();
        return true;
    }

}
