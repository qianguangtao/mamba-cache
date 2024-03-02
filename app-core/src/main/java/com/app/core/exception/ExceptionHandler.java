package com.app.core.exception;

import com.app.core.mvc.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * @author qiangt
 * @since 2019-12-31
 */
@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ExceptionHandler {

    private final ExceptionTranslator exceptionTranslator;

    @org.springframework.web.bind.annotation.ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleException(final Exception e) {
        log.error(e.getMessage(), e);
        return this.exceptionTranslator.translateException(e);
    }


}
