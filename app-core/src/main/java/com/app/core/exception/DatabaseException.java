package com.app.core.exception;

import com.app.core.mvc.result.Code;
import com.app.core.mvc.result.RespCode;

/**
 * 数据库异常
 *
 * @author qiangt
 */
public class DatabaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private RespCode code;

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(Throwable e) {
        super(String.format("%s:%s", Code.A00001.code(), e.getMessage()), e);
        this.code = Code.A00001;
    }

    public DatabaseException(RespCode code, String message) {
        super(String.format("%s:%s", code.code(), message));
        this.code = code;
        if (Code.A00000 == this.code) {
            this.code = Code.A00001;
        }
    }

    public DatabaseException(RespCode code) {
        super(String.format("%s:%s", code.code(), code.message()));
        this.code = code;
        if (Code.A00000 == this.code) {
            this.code = Code.A00001;
        }
    }

    public RespCode getCode() {
        return code;
    }
}
