package com.onemsg.datafaker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import com.onemsg.datafaker.web.StatusResponseException;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import net.datafaker.Faker;
import net.datafaker.fileformats.Format;

public record DataFaker(
    int id,
    String path,
    String name,
    String intro,
    JsonObject expression,
    Supplier<JsonObject> dataProvider,
    Type type,
    LocalDateTime createdTime
) {
    
    private static final Faker faker = new Faker(new Locale("zh-CN"));

    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);

    public static DataFaker create(String path, String name, String intro, JsonObject expression, Type type)
            throws NullPointerException {
        Objects.requireNonNull(path);
        Objects.requireNonNull(name);
        Objects.requireNonNull(expression);
        var dataProvider = createSupplier(expression);
        return new DataFaker(NEXT_ID.getAndIncrement(), path, name, intro, expression, dataProvider, type,
                LocalDateTime.now());
    }

    /**
     * Evaluation datafaker expressions
     * 
     * @param expression
     * @return the evaluated string expression
     * @throws StatusResponseException if unable to evaluate the expression
     * @see net.datafaker.Faker.expression
     */
    public static String validateExpression(String expression) throws StatusResponseException {
        try {
            return faker.expression(expression);
        } catch (Exception e) {
            throw StatusResponseException.create(400, "Expression {} invalid", expression);
        }
    }

    public static Supplier<JsonObject> createSupplier(JsonObject expression) throws StatusResponseException {
        var json = createFormatJson(expression);
        return () -> (JsonObject) Json.decodeValue(json.generate());
    }

    public static net.datafaker.fileformats.Json createFormatJson(JsonObject expression) throws StatusResponseException {
        var jsonBuilder = Format.toJson();
        for (String field : expression.fieldNames()) {
            Object fieldExpression0 = expression.getValue(field);
            if (fieldExpression0 instanceof JsonObject fieldExpression) {
                var json = createFormatJson(fieldExpression);
                jsonBuilder.set(field, () -> json );
            } else if (fieldExpression0 instanceof String fieldExpression) {
                validateExpression(fieldExpression);
                jsonBuilder.set(field, () -> expression(fieldExpression));
            } else {
                throw StatusResponseException.create(400, "Expression {} invalid", fieldExpression0);
            }
        }
        return jsonBuilder.build();
    }

    public JsonObject generate() {
        return dataProvider.get();
    }

    public JsonObject toVoJson() {
        return new JsonObject()
            .put("id", id)
            .put("path", path)
            .put("name", name)
            .put("intro", intro)
            .put("expression", expression)
            .put("type", type.name())
            .put("createdTime", createdTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    enum Type {
        OBJECT,
        ARRAY
    }


    private static Object expression(String expression) {
        String value = faker.expression(expression);
        try {
            return Json.decodeValue(value);
        } catch (Exception e) {
            return value;
        }
    }


    public static class JsonSupplier implements Supplier<JsonObject> {

        private final Map<String, Supplier<Object>> map;

        public JsonSupplier(Map<String, Supplier<Object>> map) {
            this.map = Objects.requireNonNull(map);
        }

        public static JsonSupplier create(JsonObject expression) throws StatusResponseException {
            Map<String, Supplier<Object>> map = new HashMap<>();

            for (String field : expression.fieldNames()) {
                Object fieldExpression0 = expression.getValue(field);
                if (fieldExpression0 instanceof JsonObject fieldExpression) {
                    map.put(field, create(fieldExpression)::get);
                } else if (fieldExpression0 instanceof String fieldExpression) {
                    validateExpression(fieldExpression);
                    map.put(field, () -> faker.expression(fieldExpression));
                } else {
                    throw StatusResponseException.create(400, "Expression {} invalid", fieldExpression0);
                }
            }
            return new JsonSupplier(map);
        }

        @Override
        public JsonObject get() {
            JsonObject json = new JsonObject();

            for (Entry<String, Supplier<Object>> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value0 = entry.getValue().get();

                if (value0 instanceof String value) {
                    try {
                        json.put(key, Json.decodeValue(value));
                    } catch (Exception e) {
                        json.put(key, value);
                    }
                } else if (value0 instanceof JsonObject value) {
                    json.put(key, value);
                } else {
                    json.put(key, value0.toString());
                }
            }
            return json;
        }

    }
}
