package com.onemsg.restdatafaker;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.onemsg.restdatafaker.exception.ExpressionInvalidException;

import io.vertx.core.json.JsonObject;
import net.datafaker.Faker;
import net.datafaker.providers.base.Number;

/**
 * 数据生成器
 */
public interface DataGenerator extends Supplier<JsonObject> {

    public static DataGenerator create(Faker faker, JsonObject expression) throws ExpressionInvalidException {
        if (expression == null) return () -> null;
        Map<String, Supplier<? extends Object>> schema = new LinkedHashMap<>();
        for (var key : expression.fieldNames()) {
            var text = expression.getValue(key);
            if (text == null) {
                schema.put(key, () -> null);
            } else if ( text instanceof Number value ) {
                schema.put(key, () -> value);
            } else if (text instanceof String value) {
                try {
                    faker.expression(value);
                } catch (Exception e) {
                    throw new ExpressionInvalidException(key, value, e.getMessage());
                }
                schema.put(key, () -> tryToNumber(faker.expression(value)));
            } else if (text instanceof JsonObject value) {
                schema.put(key, create(faker, value));
            } else {
                throw new ExpressionInvalidException(key, text.toString(), "暂未支持的表达式");
            }
        }

        return () -> {
            var data = new JsonObject();
            for (var entry : schema.entrySet()) {
                var value = entry.getValue().get();
                data.put(entry.getKey(), value);
            }
            return data;
        };
    }

    private static Object tryToNumber(String value) {
        try {
            return Long.valueOf(value);
        } catch (Exception e) {
            try {
                return Double.parseDouble(value);
            } catch (Exception e2) {
                return value;
            }
        }
    }

}
