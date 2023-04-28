package com.onemsg.restdatafaker.exception;

import lombok.Getter;

@Getter
public class ExpressionInvalidException extends Exception{
    
    private final String key;
    private final String expression;
    private final String error;

    public ExpressionInvalidException(String key, String expression, String error) {
        super();
        this.key = key;
        this.expression = expression;
        this.error = error;
    }
}
