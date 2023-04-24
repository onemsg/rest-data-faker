package com.onemsg.restdatafaker.web;

import java.util.function.Supplier;

import com.onemsg.restdatafaker.exception.StatusResponseException;

import io.vertx.ext.web.RoutingContext;

public class WebHandlers {
    
    private WebHandlers() {}
    
    public static <T> T requireNonNull(T value, int status, String message) throws StatusResponseException {
        if (value == null)
            throw new StatusResponseException(status, message);
        return value;
    }

    public static <T> T get(Supplier<T> supplier, int status, String message) throws StatusResponseException {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new StatusResponseException(status, message);
        }
    }

    public static void must(boolean yes, int status, String message) throws StatusResponseException {
        if (!yes) {
            throw new StatusResponseException(status, message);
        }
    }

    public static int intQueryParam(RoutingContext context, String name, int defaultValue) throws StatusResponseException {
        try {
            String value = context.queryParams().get(name);
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (Exception e) {
            throw StatusResponseException.create(400, "请求参数 [%s] 无效", name);
        }
    }
}
