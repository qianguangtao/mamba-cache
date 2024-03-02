package com.app.core.exception;

import com.app.core.mvc.result.Result;

/**
 * 异常翻译器，用于处理SpringMVC中抛出的异常，应用只需要实现此接口将相应的异常翻译成Result即可实现应用中异常到返回结果的转化
 * @author qiangt
 * @since 2023-06-14
 */
public interface ExceptionTranslator {

    Result<Void> translateException(Exception e);

}
