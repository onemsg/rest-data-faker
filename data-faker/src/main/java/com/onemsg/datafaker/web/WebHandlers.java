package com.onemsg.datafaker.web;

import java.util.function.Supplier;

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
}
