package com.app.demo.service.impl;

import cn.hutool.core.util.StrUtil;
import com.app.cache.annotation.CollectionCacheable;
import com.app.demo.cache.enums.CacheEnum;
import com.app.demo.entity.User;
import com.app.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    /** 使用map模拟数据库 */
    private Map<String, User> DB = new HashMap<>(16);

    @PostConstruct
    public void init() {
        DB.put("1", new User("1", "John"));
        DB.put("2", new User("2", "Allen"));
    }

    @CachePut(value = CacheEnum.Names.User, key = "#result.id")
    @Override
    public User insert(User user) {
        user.setId("3");
        DB.put("3", user);
        return user;
    }

    @CacheEvict(value = CacheEnum.Names.User, key = "#id")
    @Override
    public void delete(Long id) {
        DB.remove(id);
    }

    @CachePut(value = CacheEnum.Names.User, key = "#user.id")
    @Override
    public User edit(User user) {
        User userInDB = DB.get(user.getId());
        userInDB.setUserName(user.getUserName());
        DB.put(user.getId(), userInDB);
        return userInDB;
    }

    @Cacheable(value = CacheEnum.Names.User, key = "#id"
            , unless = "#result == null"
            , condition = "#id != null")
    @Override
    public User getById(Long id) {
        return DB.get(id);
    }

    @CollectionCacheable(value = CacheEnum.Names.User, timeout = 30, unit = TimeUnit.MINUTES)
    @Override
    public List<User> list(User user) {
        // userName不为空，根据userName模糊查询
        if (StrUtil.isNotBlank(user.getUserName())) {
            return DB.entrySet().stream().map(Map.Entry::getValue)
                    .filter(u -> u.getUserName().indexOf(user.getUserName()) > -1).collect(Collectors.toList());
        }
        return DB.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

}
