package com.onemsg.datafaker;

import com.onemsg.datafaker.web.ExceptionHandler;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.StaticHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        Router router = Router.router(vertx);
        router.route().handler(LoggerHandler.create(LoggerFormat.TINY));
        router.route().handler(BodyHandler.create());

        DataFakerRouteHandler.create().mount(router);

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
