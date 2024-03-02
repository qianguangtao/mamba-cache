package com.app.core.ttl;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 线程上下文工具类
 *
 * @author 10071
 */
@Slf4j
public class TTLThreadLocalUtil {

    private static final TransmittableThreadLocal<HashMap<String, Object>> pool = new TransmittableThreadLocal<HashMap<String, Object>>();

    public static Object get(String key) {
        final Map<?, ?> map = pool.get();
        if (map == null) {
            return null;
        }
        return map.get(key);
    }

    public static <T> T get(String key, Class<T> clazz) {
        final Map<?, ?> map = pool.get();
        if (map == null) {
            return null;
        }
        final T t = (T) map.get(key);
        return t;
    }

    public static void put(String key, Object value) {

        if (pool.get() == null) {
            pool.set(new HashMap<String, Object>());
        }
        final Map<String, Object> map = pool.get();
        map.put(key, value);
    }

    public static Map<String, Object> getMap() {
        return pool.get();
    }

    public static void clear() {
        pool.set(null);
    }

    public static void clear(String key) {
        final Map<?, ?> map = pool.get();
        if (null != map) {
            map.remove(key);
        }
    }

}
