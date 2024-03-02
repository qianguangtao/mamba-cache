package com.app.core.mvc.result;

/**
 * 自定义异常:指定返回编码异常，禁止指定Code.A00000， Code.A00000 表示成功
 * @author qiangt
 */
public class CodeException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private RespCode code;

    public RespCode getCode() {
        return code;
    }

    public CodeException(Throwable e) {
        super(String.format("%s:%s", Code.A00001.code(), e.getMessage()), e);
        this.code = Code.A00001;
    }

    public CodeException(RespCode code, String message) {
        super(String.format("%s:%s", code.code(), message));
        this.code = code;
        if (Code.A00000 == this.code) {
            this.code = Code.A00001;
        }
    }
}
