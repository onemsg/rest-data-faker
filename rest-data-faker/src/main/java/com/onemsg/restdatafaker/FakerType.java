package com.onemsg.restdatafaker;

public enum FakerType {
    OBJECT,
    ARRAY;

    public static FakerType from(String type) throws IllegalArgumentException{
        for (var t : values()) {
            if (t.name().equalsIgnoreCase(type) ) {
                return t;
            }
        }
        throw new IllegalArgumentException(String.format("[%s] 不能被解析为有效 FakerType", type));
    }
}