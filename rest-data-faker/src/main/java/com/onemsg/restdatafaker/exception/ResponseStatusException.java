package com.onemsg.restdatafaker.exception;

public class ResponseStatusException extends RuntimeException {
    
    private final int status;

    private final String reason;

    public ResponseStatusException(int status) {
        this(status, null);
    }

    public ResponseStatusException(int status, String reason){
        this(status, reason, null);
    }

    public ResponseStatusException(int status, String reason, Throwable t){
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

    public static ResponseStatusException create(int status) {
        return new ResponseStatusException(status);
    }

    public static ResponseStatusException create(int status, String reason, Object...args ) {
        return new ResponseStatusException(status, String.format(reason, args));
    }

    public static ResponseStatusException create(int status, ExpressionInvalidException e) {
        return ResponseStatusException.create(status, "Expression [%s] invalid", e.getError());
    }

}
