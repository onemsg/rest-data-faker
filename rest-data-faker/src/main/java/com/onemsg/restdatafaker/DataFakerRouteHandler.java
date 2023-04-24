package com.onemsg.restdatafaker;

import static com.onemsg.restdatafaker.web.WebHandlers.get;
import static com.onemsg.restdatafaker.web.WebHandlers.intQueryParam;
import static com.onemsg.restdatafaker.web.WebHandlers.must;
import static com.onemsg.restdatafaker.web.WebHandlers.requireNonNull;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.onemsg.restdatafaker.exception.StatusResponseException;
import com.onemsg.restdatafaker.model.Convertor;
import com.onemsg.restdatafaker.model.FakerInfoUpdate;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class DataFakerRouteHandler {

    public static final String DEFAULT_LOCALE = DataFakerService.DEFAULT_LOCALE;

    private final DataFakerService service;

    private DataFakerRouteHandler(DataFakerService service) {
        this.service = service;
    }

    public static DataFakerRouteHandler create(DataFakerService dataFakerService ) {
        return new DataFakerRouteHandler(dataFakerService);
    }

    public void mount(Router router) {
        router.post("/api/datafaker/create").handler(this::createFakeObject);
        router.get("/api/datafaker/list").handler(this::listDataFaker);
        router.patch("/api/datafaker/update").handler(this::updateDataFaker);
        router.delete("/api/datafaker/remove").handler(this::deleteDataFaker);
        router.get("/api/datafaker/test-expression").handler(this::testExpression);
        router.get("/api/*").handler(this::getFakeData);
    }

    private void createFakeObject(RoutingContext context) {
        FakerInfo fakerInfo = validateCreation(context.body().asJsonObject());
        service.create(fakerInfo);
        context.response()
            .setStatusCode(201)
            .putHeader("Location", fakerInfo.path())
            .end();
    }

    private void listDataFaker(RoutingContext context) {
        List<JsonObject> data = service.getAll().stream()
                .sorted(Comparator.<FakerInfo>comparingInt(FakerInfo::id).reversed())
                .map(Convertor::toJson)
                .toList();
        context.json(data);
        context.response().putHeader("X-ID", DEFAULT_LOCALE);
    }

    private void deleteDataFaker(RoutingContext context) {
        int id = intQueryParam(context, "id", 0);
        service.remove(id);
        context.response().setStatusCode(204).end();
    }

    private void updateDataFaker(RoutingContext context) {
        int id = intQueryParam(context, "id", 0);
        var updated = validateUpdate(context.body().asJsonObject());
        service.update(id, updated);
        context.end();
    }

    private void testExpression(RoutingContext context) {
        String text = context.queryParams().get("text");
        if (text == null || text.isBlank()) {
            throw StatusResponseException.create(400, "请求参数 [text] 不能为空");
        }
        String value = DataFakerService.validateExpression(text);
        context.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .end(value);
    }

    private void getFakeData(RoutingContext context) {
        String path = context.request().path();
        var dataFaker = service.getByPath(path);
        if (dataFaker == null) {
            context.fail(404);
            return;
        }

        int limit;
        try {
            limit = Integer.parseInt(context.request().getParam("limit", "10"));
        } catch (Exception e) {
            throw StatusResponseException.create(400, "请求参数 [limit] 无效");
        }

        var data = service.generatFakeData(path, limit);
        long delay = dataFaker.delay().next();

        if (delay < 1L) {
            context.json(data);
        } else {
            context.vertx().setTimer(delay, id -> context.json(data));
        }
    }

    private static FakerInfo validateCreation(JsonObject data) throws StatusResponseException {

        if (data == null) {
            throw StatusResponseException.create(400, "请求体不能为空");
        }

        String path = requireNonNull(data.getString("path"), 400, "请求体字段 [path] 必须存在");
        path = path.strip();
        must(path.startsWith("/api/"), 400, "请求体字段 [path] 必须匹配 /api/*");

        String type = data.getString("type", FakerType.OBJECT.name());
        FakerType fakerType = null;
        try {
            fakerType = FakerType.from(type);
        } catch (IllegalArgumentException e) {
            throw StatusResponseException.create(400, "请求体字段 [type] %s 不受支持", type);
        }

        String name = requireNonNull(data.getString("name"), 400, "请求体字段 [name] 必须存在");

        String description = data.getString("description");

        must(data.containsKey("expression"), 400, "请求体字段 [expression] 必须存在");
        JsonObject expression = get(() -> data.getJsonObject("expression"), 400,
                "请求体字段 [expression] 类型必须为 JsonObject");

        String locale = data.getString("locale", DEFAULT_LOCALE);
        must(valideLocale(locale), 400, String.format("请求体字段 [locale] %s 无效", locale));
        
        Delay delay = null;
        try {
            delay = Delay.create(data.getString("delay", "0"));
        } catch (Exception e) {
            throw StatusResponseException.create(400, "请求体字段 [delay] %s 不受支持", data.getString("delay"));
        }

        return new FakerInfo(0, path, name, description, expression, locale, fakerType, delay, null, null);
    }

    private static FakerInfoUpdate validateUpdate(JsonObject data) throws StatusResponseException {

        if (data == null) {
            throw StatusResponseException.create(400, "请求体不能为空");
        }

        var path = data.getString("path", null);
        if (path != null) {
            must(path.startsWith("/api/"), 400, "请求体字段 [path] 必须匹配 /api/*");
        }

        FakerType fakerType = null;
        try {
            String type = data.getString("type", null);
            fakerType = type != null ? FakerType.from(type) : null;
        } catch (IllegalArgumentException e) {
            throw StatusResponseException.create(400, "请求体字段 [type] %s 不受支持", data.getString("type"));
        }

        var name = data.getString("name", null);
        var description = data.getString("description", null);

        JsonObject expression = null;
        if ( data.containsKey("expression") ) {
            expression = get(() -> data.getJsonObject("expression"), 400, "请求体字段 [expression] 类型必须为 JsonObject");
        }

        var locale = data.getString("locale", null);
        if (locale != null) {
            must(valideLocale(locale), 400, String.format("请求体字段 [locale] %s 无效", locale));
        }

        Delay delay = null;
        try {
            delay = data.containsKey("delay") ? Delay.create(data.getString("delay")) : null;
        } catch (Exception e) {
            throw StatusResponseException.create(400, "请求体字段 [delay] %s 不受支持", data.getString("delay"));
        }

        FakerInfoUpdate update = new FakerInfoUpdate();
        update.path = path;
        update.type = fakerType;
        update.name = name;
        update.description = description;
        update.expression = expression;
        update.locale = locale;
        update.delay = delay;
        return update;
    }

    private static final Set<String> ALL_LOCALES = Stream.of(Locale.getAvailableLocales()).map(Locale::toString)
            .collect(Collectors.toSet());

    private static boolean valideLocale(String locale) throws StatusResponseException {
        return ALL_LOCALES.contains(locale);
    }

}
