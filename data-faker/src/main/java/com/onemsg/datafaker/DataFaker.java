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
    String locale,
    Supplier<JsonObject> dataProvider,
    Type type,
    LocalDateTime createdTime
) {
    
    public static final String DEFAULT_LOCALE = "zh_CN";

    private static final Faker DEFAULT_FAKER = new Faker(new Locale(DEFAULT_LOCALE));

    private static final Map<String, Faker> fakers = new HashMap<>();

    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);

    /**
     * Create a new DataFaker object
     * 
     * @param path
     * @param name
     * @param intro
     * @param expression
     * @param type
     * @return
     * @throws NullPointerException See method source code
     */
    public static DataFaker create(String path, String name, String intro, JsonObject expression, String locale, Type type)
            throws NullPointerException {
        Objects.requireNonNull(path);
        Objects.requireNonNull(name);
        Objects.requireNonNull(expression);
        Objects.requireNonNull(locale);
        var faker = fakers.computeIfAbsent(locale, l -> new Faker(new Locale(l)));
        var dataProvider = createSupplier(faker, expression);
        return new DataFaker(NEXT_ID.getAndIncrement(), path, name, intro, expression, locale, dataProvider, type,
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
    public static String evaluationExpression(Faker faker, String expression) throws StatusResponseException {
        try {
            return faker.expression(expression);
        } catch (Exception e) {
            throw StatusResponseException.create(400, "Expression {} invalid", expression);
        }
    }

    public static String validateExpression(String expression) throws StatusResponseException {
        return evaluationExpression(DEFAULT_FAKER, expression);
    }


    public static Supplier<JsonObject> createSupplier(Faker faker, JsonObject expression) throws StatusResponseException {
        var json = createFormatJson(faker, expression);
        return () -> (JsonObject) Json.decodeValue(json.generate());
    }

    public static net.datafaker.fileformats.Json createFormatJson(Faker faker, JsonObject expression) throws StatusResponseException {
        var jsonBuilder = Format.toJson();
        for (String field : expression.fieldNames()) {
            Object fieldExpression0 = expression.getValue(field);
            if (fieldExpression0 instanceof JsonObject fieldExpression) {
                var json = createFormatJson(faker, fieldExpression);
                jsonBuilder.set(field, () -> json );
            } else if (fieldExpression0 instanceof String fieldExpression) {
                validateExpression(fieldExpression);
                jsonBuilder.set(field, () -> expression(faker, fieldExpression));
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
            .put("locale", locale)
            .put("type", type.name())
            .put("createdTime", createdTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    enum Type {
        OBJECT,
        ARRAY
    }

    private static Object expression(Faker faker, String expression) {
        String value = faker.expression(expression);
        try {
            return Json.decodeValue(value);
        } catch (Exception e) {
            return value;
        }
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static class JsonSupplier implements Supplier<JsonObject> {

        private final Map<String, Supplier<Object>> map;

        public JsonSupplier(Map<String, Supplier<Object>> map) {
            this.map = Objects.requireNonNull(map);
        }

        public static JsonSupplier create(Faker faker, JsonObject expression) throws StatusResponseException {
            Map<String, Supplier<Object>> map = new HashMap<>();

            for (String field : expression.fieldNames()) {
                Object fieldExpression0 = expression.getValue(field);
                if (fieldExpression0 instanceof JsonObject fieldExpression) {
                    map.put(field, create(faker, fieldExpression)::get);
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
