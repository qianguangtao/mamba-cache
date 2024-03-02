package com.app.kit;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author qiangt
 * @date 2023/9/14
 * @apiNote
 */
public class AopUtil {
    /**
    * @author qiangt
    * @date 2023/9/16
    * @apiNote 获取方法的参数map
     * 比如Student(name, age), method(Student stu, String id), 则返回{"stu":stu, "id":"b"}
    */
    public static Map<String, Object> getParams(JoinPoint joinPoint) {
        Map<String, Object> paramMap = new TreeMap<>();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        // 参数名
        String[] paramNames = methodSignature.getParameterNames();
        if (ObjectUtil.isNotEmpty(paramNames)) {
            // 参数值
            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < paramNames.length; i++) {
                if (args[i] instanceof javax.servlet.http.HttpSession
                        || args[i] instanceof org.springframework.web.multipart.MultipartFile
                        || args[i] instanceof javax.servlet.http.HttpServletResponse
                        || args[i] instanceof javax.servlet.http.HttpServletRequest) {
                    continue;
                }
                paramMap.put(paramNames[i], args[i]);
            }
        }
        return paramMap;
    }
    /**
    * @author qiangt
    * @date 2023/9/16
    * @apiNote 获取方法所有参数的对象map，并按参数名ascii排序，用来生成方法参数的md5
     * 比如Student(name, age), method(Student stu, String id), 则返回{"name":"a", "age":12, "id":"b"}
    */
    public static Map<String, Object> getParamFieldMap(JoinPoint joinPoint) {
        Map<String, Object> paramMap = new TreeMap<>();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        // 参数名
        String[] paramNames = methodSignature.getParameterNames();
        if (ObjectUtil.isNotEmpty(paramNames)) {
            // 参数值
            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < paramNames.length; i++) {
                if (args[i] instanceof javax.servlet.http.HttpSession
                        || args[i] instanceof org.springframework.web.multipart.MultipartFile
                        || args[i] instanceof javax.servlet.http.HttpServletResponse
                        || args[i] instanceof javax.servlet.http.HttpServletRequest) {
                    continue;
                }
                Map<String, Object> argMap = BeanUtil.beanToMap(args[i]);
                if (ObjectUtil.isEmpty(argMap)) {
                    paramMap.put(paramNames[i], args[i]);
                } else {
                    paramMap.putAll(argMap);
                }
            }
        }
        return paramMap;
    }
}
