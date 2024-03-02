package com.app.core.mvc.result;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;

import java.util.List;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/10/15 12:23
 * @description: mvc返回封装类
 */
@Data
public class Result<T> {
    private String code;

    private String message;
    /**
     * 当前页
     */
    private long pageCurrent;
    /**
     * 单页总数
     */
    private long pageSize;
    /**
     * 总页数
     */
    private long pageCount;
    /**
     * 总行数
     */
    private long pageTotal;

    private T data;

    public Result(Builder<T> builder) {
        this.code = builder.code;
        this.message = builder.message;
        this.pageCurrent = builder.pageCurrent;
        this.pageSize = builder.pageSize;
        this.pageCount = builder.pageCount;
        this.pageTotal = builder.pageTotal;
        this.data = builder.data;
    }

    private Result() {
    }

    public static <T> Result<Void> success() {
        return Result.<Void>builder().code(Code.A00000.code()).message(Code.A00000.message()).build();
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static <T> Result<T> success(T t) {
        return Result.<T>builder().code(Code.A00000.code()).message(Code.A00000.message()).data(t).build();
    }

    public static <T> Result<T> success(Object o, Class<T> clazz) {
        return Result.<T>builder()
                .code(Code.A00000.code())
                .message(Code.A00000.message())
                .data(BeanUtil.toBean(o, clazz))
                .build();
    }

    public static <T> Result<List<T>> success(List list, Class<T> clazz) {
        return Result.<List<T>>builder()
                .code(Code.A00000.code())
                .message(Code.A00000.message())
                .data(BeanUtil.copyToList(list, clazz))
                .build();
    }

    public static <T> Result<List<T>> success(List<T> t) {
        return Result.<List<T>>builder().code(Code.A00000.code()).message(Code.A00000.message()).data(t).build();
    }

    public static <T> Result<Void> fail() {
        return Result.<Void>builder()
                .code(Code.A00001.code())
                .message(Code.A00001.message())
                .build();
    }

    public static <T> Result<T> fail(RespCode respCode) {
        return Result.<T>builder().code(respCode.code()).message(respCode.message()).build();
    }

    public static <T> Result<T> fail(RespCode respCode, Throwable throwable) {
        return Result.<T>builder()
                .code(respCode.code())
                .message(respCode.message())
                .throwable(throwable)
                .build();
    }

    public static <T> Result<T> fail(RespCode respCode, String exception) {
        return Result.<T>builder()
                .code(respCode.code())
                .message(respCode.message())
                .throwable(new RuntimeException(exception))
                .build();
    }

    public static <T> Result<T> fail(Throwable throwable) {
        return Result.<T>builder()
                .code(Code.A00001.code())
                .message(Code.A00001.message())
                .throwable(throwable)
                .build();
    }

    public static class Builder<T> {
        private String code;

        private String message;
        /**
         * 当前页
         */
        private long pageCurrent;
        /**
         * 单页总数
         */
        private long pageSize;
        /**
         * 总页数
         */
        private long pageCount;
        /**
         * 总行数
         */
        private long pageTotal;
        /**
         * 异常消息
         */
        private Throwable throwable;

        private T data;

        private Builder() {
            super();
        }

        public Builder<T> code(String code) {
            this.code = code;
            return this;
        }

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> pageCurrent(long pageCurrent) {
            this.pageCurrent = pageCurrent;
            return this;
        }

        public Builder<T> pageSize(long pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder<T> pageCount(long pageCount) {
            this.pageCount = pageCount;
            return this;
        }

        public Builder<T> pageTotal(long pageTotal) {
            this.pageTotal = pageTotal;
            return this;
        }

        public Builder<T> throwable(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public Result<T> build() {
            return new Result<>(this);
        }

    }
}
