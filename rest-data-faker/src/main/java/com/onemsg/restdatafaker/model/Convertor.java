package com.onemsg.restdatafaker.model;

import com.onemsg.restdatafaker.FakerInfo;

import io.vertx.core.json.JsonObject;

/**
 * 对象转换器
 */
public class Convertor {
    
    private Convertor() {}

    public static JsonObject toJson(FakerInfo fakerInfo) {
        return new JsonObject()
            .put("id", fakerInfo.id())
            .put("path", fakerInfo.path())
            .put("name", fakerInfo.name())
            .put("description", fakerInfo.description())
            .put("expression", fakerInfo.expression())
            .put("locale", fakerInfo.locale())
            .put("type", fakerInfo.type())
            .put("delay", fakerInfo.delay().toText())
            .put("createdTime", fakerInfo.createdTime())
            .put("updatedTime", fakerInfo.updatedTime());
    }
}
