package com.onemsg.restdatafaker.exception;

public final class IdNotExistedException extends DataAcessException{
    
    private final long id;

    public IdNotExistedException(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
