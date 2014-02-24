package com.xlenc.api.session;

import lombok.Data;

/**
 * User: Michael Williams
 * Date: 2/9/14
 * Time: 12:01 AM
 */
public @Data
class ResultError {

    private int code;
    private String message;
    private Throwable throwable;

    public ResultError(int code) {
        this.code = code;
    }

    public ResultError(String message) {
        this.message = message;
    }

    public ResultError(Throwable throwable) {
        this.throwable = throwable;
    }

    public ResultError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultError(String message, Throwable throwable) {
        this.message = message;
        this.throwable = throwable;
    }

    public ResultError(int code, Throwable throwable) {
        this.code = code;
        this.throwable = throwable;
    }

    public ResultError(int code, String message, Throwable throwable) {
        this.code = code;
        this.message = message;
        this.throwable = throwable;
    }
}
