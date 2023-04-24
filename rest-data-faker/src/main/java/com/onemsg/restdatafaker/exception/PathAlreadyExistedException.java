package com.onemsg.restdatafaker.exception;

/**
 * path 已存在异常
 */
public final class PathAlreadyExistedException extends DataAcessException {
    
    private final String path;

    public PathAlreadyExistedException(String path) {
        this.path = path;
    }

    public String getPath(){
        return path;
    }
}
