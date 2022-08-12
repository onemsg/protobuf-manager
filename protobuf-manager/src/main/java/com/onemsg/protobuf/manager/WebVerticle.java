package com.onemsg.protobuf.manager;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onemsg.protobuf.manager.application.ApplicationDetailService;
import com.onemsg.protobuf.manager.exception.BeanErrorException;
import com.onemsg.protobuf.manager.exception.StatusResponseException;
import com.onemsg.protobuf.manager.model.web.ErrorModel;
import com.onemsg.protobuf.manager.proto.ProtoService;
import com.onemsg.protobuf.manager.user.SimpleUserStore;
import com.onemsg.protobuf.manager.util.StringUtil;
import com.onemsg.protobuf.manager.web.ApplicationRouteHandler;
import com.onemsg.protobuf.manager.web.ProtoRouteHandler;
import com.onemsg.protobuf.manager.web.UserAuthHandler;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;

/**
 * WebVerticle
 */
public class WebVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(WebVerticle.class);

    public ApplicationDetailService applicationDetailService;
    public ProtoService protoService;
    public SimpleUserStore simpleUserStore;

    private void checkBeans() throws NullPointerException {
        Objects.requireNonNull(applicationDetailService);
        Objects.requireNonNull(protoService);
        Objects.requireNonNull(simpleUserStore);
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        checkBeans();

        Router router = Router.router(vertx);

        router.route().handler(LoggerHandler.create(LoggerFormat.SHORT));
        router.route().handler(BodyHandler.create());
        router.route("/api/*").handler(UserAuthHandler.create(simpleUserStore));

        ApplicationRouteHandler.create(applicationDetailService).mount(router);
        ProtoRouteHandler.create(protoService, applicationDetailService).mount(router);

        router.route("/api/400").handler(ctx -> {
            throw new StatusResponseException(400, "请求参数错误");
        });

        router.route("/api/401").handler(ctx -> {
            throw new StatusResponseException(401, "未登录");
        });

        router.route("/api/*").failureHandler(WebVerticle::handleFailure);

        router.route().failureHandler(ErrorHandler.create(vertx, true));

        vertx.createHttpServer().requestHandler(router).listen(8888, http -> {
            if (http.succeeded()) {
                startPromise.complete();
                log.info("HTTP server started on port 8888");
            } else {
                startPromise.fail(http.cause());
            }
        });
    }

    private static void handleFailure(RoutingContext ctx){
        Throwable t = ctx.failure();

        if (t instanceof StatusResponseException e){

            JsonObject error = ErrorModel.of(e.status(), e.reason())
                .toJson()
                .put("path", ctx.request().path())
                .put("timestamp", System.currentTimeMillis());
            
            ctx.response().setStatusCode(e.status());
            ctx.json(error);
        } else if (t instanceof BeanErrorException e){

            JsonObject error = ErrorModel.of(400, e.message(), StringUtil.toString(e.details()) )
                    .toJson()
                    .put("path", ctx.request().path())
                    .put("timestamp", System.currentTimeMillis());
            ctx.response().setStatusCode(400);
            ctx.json(error);

        } else {

            HttpServerRequest request = ctx.request();
            log.warn("{} {} 发生了异常", request.method(), request.path(), t);

            int statusCode = ctx.statusCode();
            statusCode = statusCode == -1 ? 500 : statusCode;

            JsonObject error = ErrorModel.of(statusCode, "内部服务错误", t.getMessage())
                    .toJson()
                    .put("path", ctx.request().path())
                    .put("timestamp", System.currentTimeMillis());

            ctx.response().setStatusCode(statusCode);
            ctx.json(error);
        }

    }
}