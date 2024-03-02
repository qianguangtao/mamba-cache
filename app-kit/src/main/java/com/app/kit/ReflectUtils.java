package com.app.kit;

import cn.hutool.core.util.ObjectUtil;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/10/20 15:34
 * @description: 反射工具类
 */
public class ReflectUtils {
    /**
     * 判断类是否实现了接口
     * @param clazz          待校验类型
     * @param superInterface 超类接口
     * @return
     */
    public static boolean checkSubOfInterface(Class<?> clazz, Class<?> superInterface) {
        boolean result = false;
        for (Class<?> parentInterface : clazz.getInterfaces()) {
            if (ObjectUtil.equal(superInterface, parentInterface)) {
                result = true;
                break;
            }
        }
        return result;
    }
}
