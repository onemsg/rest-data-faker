package com.onemsg.restdatafaker.exception;

public class StatusResponseException extends RuntimeException{
    
    private final int status;

    private final String reason;

    public StatusResponseException(int status) {
        this(status, null);
    }

    public StatusResponseException(int status, String reason){
        this(status, reason, null);
    }

    public StatusResponseException(int status, String reason, Throwable t){
        super(reason, t);
        this.status = status;
        this.reason = reason;
    }

    public int status(){
        return status;
    }

    public String reason(){
        return reason;
    }

    public static StatusResponseException create(int status) {
        return new StatusResponseException(status);
    }

    public static StatusResponseException create(int status, String reason, Object...args ) {
        return new StatusResponseException(status, String.format(reason, args));
    }
}
