package com.onemsg.datafaker;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.onemsg.datafaker.web.StatusResponseException;
import com.onemsg.datafaker.web.WebHandlers;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class DataFakerRouteHandler {
    
    public static final String DEFAULT_LOCALE = DataFaker.DEFAULT_LOCALE;

    private static final Map<String, DataFaker> store = new ConcurrentHashMap<>();

    public static DataFakerRouteHandler create() {
        return new DataFakerRouteHandler();
    }

    public void mount(Router router) {
        router.post("/api/datafaker/create-object").handler(this::createFakeObject);
        router.post("/api/datafaker/create-array").handler(this::createFakeArray);
        router.get("/api/datafaker/list").handler(this::listDataFaker);
        router.delete("/api/datafaker/remove").handler(this::deleteDataFaker);
        router.get("/api/datafaker/test-expression").handler(this::testExpression);
        router.get("/api/*").handler(this::getFakeData);
    }

    /**
     * Create fake object
     * 
     * @param context
     */
    private void createFakeObject(RoutingContext context) {
        var data = context.body().asJsonObject();

        DataFaker dataFaker = createDataFaker(data, DataFaker.Type.OBJECT);
        store.put(dataFaker.path(), dataFaker);

        context.response()
            .setStatusCode(201)
            .putHeader("Location", dataFaker.path())
            .end();
    }

    /**
     * Create fake array
     * 
     * @param context
     */
    private void createFakeArray(RoutingContext context) {
        var data = context.body().asJsonObject();

        DataFaker dataFaker = createDataFaker(data, DataFaker.Type.ARRAY);
        store.put(dataFaker.path(), dataFaker);

        context.response()
                .setStatusCode(201)
                .putHeader("Location", dataFaker.path())
                .end();
    }

    /**
     * List data fakers
     * @param context
     */
    private void listDataFaker(RoutingContext context) {
        List<JsonObject> data = store.values().stream()
            .sorted(Comparator.<DataFaker>comparingInt(DataFaker::id).reversed())
            .map(DataFaker::toVoJson)
            .toList();
        context.json(data);
    }

    /**
     * Delete data fakers
     * @param context
     */
    private void deleteDataFaker(RoutingContext context) {
        
        int id;
        try {
            id = Integer.parseInt(context.queryParams().get("id"));
        } catch (Exception e) {
            throw StatusResponseException.create(400, "请求参数 [id] 无效");
        }

        store.values().stream()
            .filter(o -> o.id() == id)
            .findFirst()
            .ifPresent(o -> store.remove(o.path()));

        context.end();
    }
    

    /**
     * Test expression
     * 
     * @param context
     */
    private void testExpression(RoutingContext context) {
        String text = context.queryParams().get("text");
        if (text == null || text.isBlank()) {
            throw StatusResponseException.create(400, "请求参数 [text] 无效");
        }
        String value = DataFaker.validateExpression(text);
        context.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .end(value);
    }

    /**
     * Get fake data
     * @param context
     */
    private void getFakeData(RoutingContext context) {
        
        String path = context.request().path();
        var dataFaker = store.get(path);
        if (dataFaker == null) {
            context.fail(404);
            return;
        }


        Object data = switch (dataFaker.type()) {
            case ARRAY -> {
                int limit = WebHandlers.get(() -> Integer.parseInt(context.request().getParam("limit", "10")), 
                        400, "请求参数 [limit] 无效");

                JsonArray array = new JsonArray();
                Stream.generate(dataFaker::generate)
                    .limit(limit)
                    .forEach(array::add);
                yield array;
            }    
            default -> dataFaker.generate();
        };

        context.json(data);
    }


    private static DataFaker createDataFaker(JsonObject data, DataFaker.Type type) throws StatusResponseException {
        
        if (data == null) {
            throw StatusResponseException.create(400, "请求体不能为空");
        }

        String path = WebHandlers.requireNonNull(data.getString("path"), 400, "请求体字段 [path] 必须存在");
        path = path.strip();
        WebHandlers.must(path.startsWith("/api/"), 400, "请求体字段 [path] 必须匹配 /api/*");

        String name = WebHandlers.requireNonNull(data.getString("name"), 400, "请求体字段 [name] 必须存在");
        
        String intro = data.getString("intro");

        WebHandlers.requireNonNull(data.getValue("expression"), 400, "请求体字段 [expression] 必须存在");
        JsonObject expression = WebHandlers.get(() -> data.getJsonObject("expression"), 400,
                "请求体字段 [expression] 类型必须为 JsonObject");

        String locale = data.getString("locale", DEFAULT_LOCALE);
        if (!valideLocale(locale)) {
            throw StatusResponseException.create(400, String.format("请求体字段 [locale] %s 不受支持", locale));
        }

        return DataFaker.create(path, name, intro, expression, locale, type);
    }

    private static final Set<String> ALL_LOCALES = Stream.of(Locale.getAvailableLocales()).map(Locale::toString).collect(Collectors.toSet());

    private static boolean valideLocale(String locale) throws StatusResponseException {
        return ALL_LOCALES.contains(locale);
    }

}
