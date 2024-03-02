package com.app.core.mvc.result;


import com.app.kit.PatternUtils;

/**
 * 响应代码枚举接口
 * @author qiangt
 */
public interface RespCode {

    /**
     * 枚举名称
     */
    String code();

    /**
     * 获取响应消息
     */
    String message();

    default CodeException toCodeException() {
        return new CodeException(this, message());
    }

    default CodeException toCodeException(final Exception exception) {
        return new CodeException(this, exception.getMessage());
    }

    /**
     * @param args 正则表达式待填充的值
     * @return CodeException
     */
    default CodeException toCodeExceptionWithArgs(final Object... args) {
        return new CodeException(this, PatternUtils.formatPlaceholder(message(), args));
    }

}
