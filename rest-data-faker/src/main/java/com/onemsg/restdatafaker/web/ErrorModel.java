package com.onemsg.restdatafaker.web;

import io.vertx.core.json.JsonObject;

/**
 * Rest error model
 */
public record ErrorModel(
    int status,
    String message,
    String detail
) {

    public static ErrorModel of(int status, String message){
        return of(status, message, null);
    }

    public static ErrorModel of(int status, String message, String detail) {
        return new ErrorModel(status, message, detail);
    }

    public JsonObject toJson(){
        return new JsonObject()
            .put("status", status)
            .put("message", message)
            .put("detail", detail);
    }
}
