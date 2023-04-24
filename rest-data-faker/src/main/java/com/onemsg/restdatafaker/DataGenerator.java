package com.onemsg.restdatafaker;

import java.util.function.Supplier;

import io.vertx.core.json.JsonObject;
import net.datafaker.Faker;

/**
 * 数据生成器
 */
public interface DataGenerator extends Supplier<JsonObject> {
    
    public static DataGenerator create(Faker faker, JsonObject expression) {
        return () -> expression;
    }
}
