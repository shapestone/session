package com.xlenc.session;

import lombok.Data;

/**
 * User: Michael Williams
 * Date: 2/9/14
 * Time: 12:01 AM
 */
public @Data
class SessionError {

    private int code;
    private String message;
    private Throwable throwable;

    public SessionError(int code) {
        this.code = code;
    }

    public SessionError(String message) {
        this.message = message;
    }

    public SessionError(Throwable throwable) {
        this.throwable = throwable;
    }

    public SessionError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public SessionError(String message, Throwable throwable) {
        this.message = message;
        this.throwable = throwable;
    }

    public SessionError(int code, Throwable throwable) {
        this.code = code;
        this.throwable = throwable;
    }

    public SessionError(int code, String message, Throwable throwable) {
        this.code = code;
        this.message = message;
        this.throwable = throwable;
    }
}
