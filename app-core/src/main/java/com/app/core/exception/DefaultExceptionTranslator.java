package com.app.core.exception;

import com.alibaba.fastjson.JSONException;
import com.app.core.mvc.result.Code;
import com.app.core.mvc.result.CodeException;
import com.app.core.mvc.result.Result;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * @author qiangt
 * @since 2023/6/14 16:19
 */
public class DefaultExceptionTranslator implements ExceptionTranslator {

    @Override
    public Result<Void> translateException(final Exception e) {
        if (e instanceof HttpMediaTypeNotSupportedException) {
            return Result.fail(Code.A00001, e);
        } else if (e instanceof MaxUploadSizeExceededException) {
            return Result.fail(Code.A00001, e);
        } else if (e instanceof IllegalArgumentException) {
            return Result.fail(Code.A00006, e);
        } else if (e instanceof MissingServletRequestParameterException) {
            return Result.fail(Code.A00007, e);
        } else if (e instanceof HttpMessageNotReadableException) {
            return Result.fail(Code.A00007, e);
        } else if (e instanceof NoHandlerFoundException) {
            return Result.fail(Code.A00007, e);
        } else if (e instanceof JSONException) {
            return Result.fail(Code.A00008, e);
        } else if (e instanceof MethodArgumentNotValidException) {
            // 捕捉实体类属性校验异常
            return Result.fail(Code.A00004, e);
        } else if (e instanceof BindException || e instanceof MethodArgumentTypeMismatchException) {
            return Result.fail(Code.A00008, e);
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            return Result.fail(Code.A00009, e);
        } else if (e instanceof ConstraintViolationException) {
            // 捕捉参数校验异常
            return Result.fail(Code.A00004, this.errorDescForConstraintViolationException((ConstraintViolationException) e));
        } else if (e instanceof CodeException) {
            final CodeException ex = (CodeException) e;
            return Result.fail(ex.getCode(), e);
        } else {
            return Result.fail(e);
        }
    }

    private String errorDescForMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        final BindingResult bindingResult = e.getBindingResult();
        final StringBuilder sb = new StringBuilder("校验失败:");
        for (final FieldError fieldError : bindingResult.getFieldErrors()) {
            sb.append(fieldError.getField()).append("：").append(fieldError.getDefaultMessage()).append(", ");
        }
        return sb.toString();
    }

    private String errorDescForConstraintViolationException(final ConstraintViolationException e) {
        return e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(","));
    }

}
