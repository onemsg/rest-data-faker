package com.onemsg.restdatafaker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.onemsg.restdatafaker.web.ExceptionHandler;
import com.onemsg.restdatafaker.web.TraceHandler;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.StaticHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainVerticle extends AbstractVerticle {

    static {
        JavaTimeModule module = new JavaTimeModule();
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(f));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(f));
        DatabindCodec.mapper().registerModule(module);
        DatabindCodec.prettyMapper().registerModule(module);
        DatabindCodec.mapper().findAndRegisterModules();
        DatabindCodec.prettyMapper().findAndRegisterModules();
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        Router router = Router.router(vertx);
        router.route().handler(LoggerHandler.create(LoggerFormat.TINY));
        router.route().handler(TraceHandler.create());
        router.route().handler(BodyHandler.create());

        DataFakerRouteHandler.create(new DataFakerService()).mount(router);
        router.route("/api/*").failureHandler(ExceptionHandler.create());
        
        router.route().failureHandler(ErrorHandler.create(vertx));
        
        ConfigRetriever.create(vertx).getConfig()
            .onComplete(ar -> configHttpServer(startPromise, router, ar));
    }

    void configHttpServer(Promise<Void> startPromise, Router router, AsyncResult<JsonObject> asyncResult) {

        if (asyncResult.succeeded()) {
            try {
                var config = asyncResult.result();

                String staticPath = config.getJsonObject("server").getString("staticPath");
                router.route().order(1).handler(StaticHandler.create(staticPath));

                int port = config.getJsonObject("server").getInteger("port");
                var server = vertx.createHttpServer();
                server.requestHandler(router)
                        .listen(port, http -> {
                            if (http.succeeded()) {
                                startPromise.complete();
                                log.info("HTTP server started on http://127.0.0.1:" + port);
                            } else {
                                startPromise.fail(http.cause());
                            }
                        });
            } catch (Exception e) {
                startPromise.fail(e);
            }
        } else {
            startPromise.fail(asyncResult.cause());
        }
    }
}
