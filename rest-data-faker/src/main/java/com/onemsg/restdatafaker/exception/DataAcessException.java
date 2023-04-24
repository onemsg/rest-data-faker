package com.onemsg.restdatafaker.exception;

/**
 * 数据访问类异常
 */
public sealed class DataAcessException extends Exception permits
    IdNotExistedException, 
    PathAlreadyExistedException, PathNotExistedException {

    public DataAcessException() {
        super();
    }

    public DataAcessException(String message) {
        super(message);
    }

}