package com.onemsg.restdatafaker.web;

import com.onemsg.restdatafaker.exception.StatusResponseException;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionHandler implements Handler<RoutingContext> {

    public static final String PROBLEM_CONTENT_TYPE = "application/problem+json";

    public static ExceptionHandler create() {
        return new ExceptionHandler();
    }

    @Override
    public void handle(RoutingContext ctx) {
        Throwable t = ctx.failure();

        if (t instanceof StatusResponseException e) {
            end(ctx, e.status(), e.reason(), null);
        } else if (t != null) {
            HttpServerRequest request = ctx.request();
            log.warn("Handle {} {} happened a exception", request.method(), request.path(), t);
            System.out.println(t);
            int statusCode = ctx.statusCode() == -1 ? 500 : ctx.statusCode();
            end(ctx, statusCode, "服务器内部错误", t.getMessage());
        } else {
            int statusCode = ctx.statusCode() == -1 ? 500 : ctx.statusCode();
            ctx.response().setStatusCode(statusCode).end();
        }
    }


    private static void end(RoutingContext ctx, int statusCode, String message, String detail) {
        JsonObject error = ErrorModel.of(statusCode, message, detail)
                .toJson()
                .put("path", ctx.request().path())
                .put("timestamp", System.currentTimeMillis());

        ctx.response().setStatusCode(statusCode);
        ctx.response().putHeader("Content-Type", PROBLEM_CONTENT_TYPE);
        ctx.json(error);
    }
}
