package com.onemsg.restdatafaker.web;

import java.util.UUID;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class TraceHandler implements Handler<RoutingContext> {

    public static TraceHandler create() {
        return new TraceHandler();
    }

    @Override
    public void handle(RoutingContext context) {

        final String traceId = UUID.randomUUID().toString();
        final long start = System.currentTimeMillis();
        final var response = context.response();

        context.put("traceId", traceId);
        context.addHeadersEndHandler( v -> {
            response.putHeader("X-Trace-Id", traceId);
            response.putHeader("X-Trace-Start", String.valueOf(start));
            response.putHeader("X-Trace-Time", String.valueOf(System.currentTimeMillis() - start));
        });
        context.next();
    }
    

}
