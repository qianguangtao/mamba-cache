/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.app.core.util;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.app.core.mvc.result.RespCode;
import com.app.kit.PatternUtils;
import com.app.kit.annotation.MethodSupplier;
import lombok.SneakyThrows;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Asserts {

    /**
     * 断言是否为真，如果为 {@code false} 抛出 {@code IllegalArgumentException} 异常<br>
     * @param expression
     * @param respCode
     * @param params
     * @throws IllegalArgumentException
     */
    public static void isTrue(boolean expression, RespCode respCode, Object... params) throws IllegalArgumentException {
        if (!expression) {
            throw new IllegalArgumentException(PatternUtils.formatPlaceholder(respCode.message(), params));
        }
    }

    /**
     * 断言是否为真，如果为 {@code false} 抛出 {@code IllegalArgumentException} 异常<br>
     * @param expression
     * @throws IllegalArgumentException
     */
    public static void isTrue(boolean expression) throws IllegalArgumentException {
        if (!expression) {
            throw new IllegalArgumentException("[Assertion failed] - this expression must be true");
        }
    }

    /**
     * 断言是否为真，如果为 {@code true} 抛出 {@code IllegalArgumentException} 异常<br>
     * @param expression
     * @param respCode
     * @param params
     * @throws IllegalArgumentException
     */
    public static void isFalse(boolean expression, RespCode respCode, Object... params) throws IllegalArgumentException {
        if (expression) {
            throw new IllegalArgumentException(PatternUtils.formatPlaceholder(respCode.message(), params));
        }
    }

    /**
     * 断言是否为真，如果为 {@code true} 抛出 {@code IllegalArgumentException} 异常<br>
     * @param expression
     * @throws IllegalArgumentException
     */
    public static void isFalse(boolean expression) throws IllegalArgumentException {
        if (expression) {
            throw new IllegalArgumentException("[Assertion failed] - this expression must be false");
        }
    }


    /**
     * 断言对象是否为{@code null} ，如果不为{@code null} 抛出{@link IllegalArgumentException} 异常
     *
     * <pre class="code">
     * Assert.isNull(value);
     * </pre>
     * @param object 被检查对象
     * @throws IllegalArgumentException if the object is not {@code null}
     */
    public static void isNull(Object object) throws IllegalArgumentException {
        if (ObjectUtil.isNotNull(object)) {
            throw new IllegalArgumentException("[Assertion failed] - the object argument must be null");
        }
    }

    /**
     * 断言对象是否为{@code null} ，如果不为{@code null} 抛出{@link IllegalArgumentException} 异常
     *
     * <pre class="code">
     * Assert.isNull(value);
     * </pre>
     * @param object 被检查对象
     * @throws IllegalArgumentException if the object is not {@code null}
     */
    public static void isNull(Object object, RespCode respCode, Object... params) throws IllegalArgumentException {
        if (ObjectUtil.isNotNull(object)) {
            throw new IllegalArgumentException(PatternUtils.formatPlaceholder(respCode.message(), params));
        }
    }

    /**
     * 断言对象是否不为{@code null} ，如果为{@code null} 抛出{@link IllegalArgumentException} 异常
     *
     * <pre class="code">
     * Assert.notNull(clazz);
     * </pre>
     * @param object 被检查对象
     * @return 非空对象
     * @throws IllegalArgumentException if the object is {@code null}
     */
    public static void notNull(Object object) throws IllegalArgumentException {
        if (ObjectUtil.isNull(object)) {
            throw new IllegalArgumentException("[Assertion failed] - the object argument must not be null");
        }
    }

    /**
     * 断言对象是否不为{@code null} ，如果为{@code null} 抛出{@link IllegalArgumentException} 异常
     *
     * <pre class="code">
     * Assert.notNull(clazz);
     * </pre>
     * @param object 被检查对象
     * @return 非空对象
     * @throws IllegalArgumentException if the object is {@code null}
     */
    public static void notNull(Object object, RespCode respCode, Object... params) throws IllegalArgumentException {
        if (ObjectUtil.isNull(object)) {
            throw new IllegalArgumentException(PatternUtils.formatPlaceholder(respCode.message(), params));
        }
    }

    /**
     * 断言对象是否不为{@code empty} ，如果为{@code empty} 抛出{@link IllegalArgumentException} 异常
     *
     * <pre class="code">
     * Assert.notEmpty(clazz);
     * </pre>
     * @param object 被检查对象
     * @return 非空对象
     * @throws IllegalArgumentException if the object is {@code empty}
     */
    public static void notEmpty(Object object) throws IllegalArgumentException {
        if (ObjectUtil.isEmpty(object)) {
            throw new IllegalArgumentException("[Assertion failed] - the object argument must not be empty");
        }
    }

    /**
     * 断言对象是否不为{@code empty} ，如果为{@code empty} 抛出{@link IllegalArgumentException} 异常
     *
     * <pre class="code">
     * Assert.notEmpty(clazz);
     * </pre>
     * @param object 被检查对象
     * @return 非空对象
     * @throws IllegalArgumentException if the object is {@code empty}
     */
    public static void notEmpty(Object object, RespCode respCode, Object... params) throws IllegalArgumentException {
        if (ObjectUtil.isEmpty(object)) {
            throw new IllegalArgumentException(PatternUtils.formatPlaceholder(respCode.message(), params));
        }
    }

    /**
     * 断言两个对象是否相等,如果两个对象不相等 抛出IllegalArgumentException 异常
     * <pre class="code">
     *   Assert.equals(obj1,obj2);
     * </pre>
     * @param obj1 对象1
     * @param obj2 对象2
     * @throws IllegalArgumentException obj1 must be equals obj2
     */
    public static void equals(Object obj1, Object obj2) {
        if (ObjectUtil.notEqual(obj1, obj2)) {
            throw new IllegalArgumentException(StrUtil.format("({}) must be equals ({})", obj1, obj2));
        }
    }


    /**
     * 断言两个对象是否相等,如果两个对象不相等 抛出IllegalArgumentException 异常
     * <pre class="code">
     *   Assert.equals(obj1,obj2);
     * </pre>
     * @param obj1 对象1
     * @param obj2 对象2
     * @throws IllegalArgumentException obj1 must be equals obj2
     */
    public static void equals(Object obj1, Object obj2, RespCode respCode, Object... params) {
        if (ObjectUtil.notEqual(obj1, obj2)) {
            throw new IllegalArgumentException(PatternUtils.formatPlaceholder(respCode.message(), params));
        }
    }

    /**
     * 断言两个对象是否不相等,如果两个对象相等 抛出IllegalArgumentException 异常
     * <pre class="code">
     *   Assert.notEquals(obj1,obj2);
     * </pre>
     * @param obj1 对象1
     * @param obj2 对象2
     * @throws IllegalArgumentException obj1 must not be equals obj2
     */
    public static void notEquals(Object obj1, Object obj2, RespCode respCode, Object... params) {
        if (ObjectUtil.equal(obj1, obj2)) {
            throw new IllegalArgumentException(PatternUtils.formatPlaceholder(respCode.message(), params));
        }
    }

    /**
     * 断言两个对象是否不相等,如果两个对象相等 抛出IllegalArgumentException 异常
     * <pre class="code">
     *   Assert.notEquals(obj1,obj2);
     * </pre>
     * @param obj1 对象1
     * @param obj2 对象2
     * @throws IllegalArgumentException obj1 must not be equals obj2
     */
    public static void notEquals(Object obj1, Object obj2) {
        if (ObjectUtil.equal(obj1, obj2)) {
            throw new IllegalArgumentException(StrUtil.format("({}) must not be equals ({})", obj1, obj2));
        }
    }

    /**
     * 校验对象及对象属性是否为空
     * @param p         待校验对象
     * @param suppliers 对象属性的Class::getFeild形式，比如User::getName()
     * @param <T>
     * @param <P>
     */
    @SneakyThrows
    public static <T, P> void notEmpty(P p, MethodSupplier<T, P>... suppliers) {
        if (ObjectUtil.isNull(p)) {
            throw new IllegalArgumentException("The parameter to be verified is empty");
        }
        for (MethodSupplier<T, P> supplier : suppliers) {
            final String methodName = getMethodName(supplier);
            // 通过属性的get方法获取属性名
            final String fieldName = StrUtil.lowerFirst(methodName.replaceFirst("^(get|is)", ""));
            final Class<?> clazz = p.getClass();
            final Field field = clazz.getDeclaredField(fieldName);
            final NotNull annotationNotNull = field.getAnnotation(NotNull.class);
            final NotEmpty annotationNotEmpty = field.getAnnotation(NotEmpty.class);
            String msg = clazz.getSimpleName().concat(".").concat(fieldName).concat(" can not be empty");
            if (ObjectUtil.isNotNull(annotationNotNull)) {
                msg = annotationNotNull.message();
            }
            if (ObjectUtil.isNotNull(annotationNotEmpty)) {
                msg = annotationNotEmpty.message();
            }
            if (ObjectUtil.isEmpty(supplier.get(p))) {
                throw new IllegalArgumentException(msg);
            }
        }

    }

    /**
     * 通过User::getName()获取“name”
     * @param lambda
     * @return 属性名
     */
    @SneakyThrows
    private static <T> String getMethodName(MethodSupplier lambda) {
        Class lambdaClass = lambda.getClass();
        Method method = lambdaClass.getDeclaredMethod("writeReplace");
        // writeReplace是私有方法，需要去掉私有属性
        method.setAccessible(Boolean.TRUE);
        // 手动调用writeReplace()方法，返回一个SerializedLambda对象
        SerializedLambda serializedLambda = (SerializedLambda) method.invoke(lambda);
        // 得到lambda表达式中调用的方法名，如 "User::getSex"，则得到的是"getSex"
        return serializedLambda.getImplMethodName();
    }
}
