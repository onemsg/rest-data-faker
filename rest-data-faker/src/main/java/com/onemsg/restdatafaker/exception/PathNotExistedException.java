package com.onemsg.restdatafaker.exception;

public final class PathNotExistedException extends DataAcessException{
    
    private final String path;

    public PathNotExistedException(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
