package com.xlenc.session;

import lombok.Data;
import lombok.Getter;

/**
 * User: Michael Williams
 * Date: 2/8/14
 * Time: 11:21 PM
 */
public @Data
class Result<T, K> {

    @Getter
    private boolean success;
    @Getter
    private T data;
    private K error;

    public Result(boolean success) {
        this.success = success;
    }

    public Result(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public Result(boolean success, T data, K error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

}
