package com.onemsg.protobuf.manager.web;

import java.util.NoSuchElementException;
import java.util.Objects;

import com.onemsg.protobuf.manager.application.ApplicationDetail;
import com.onemsg.protobuf.manager.application.ApplicationDetailService;
import com.onemsg.protobuf.manager.exception.StatusResponseException;
import com.onemsg.protobuf.manager.model.web.PageableModel;
import com.onemsg.protobuf.manager.util.NumberUtil;
import com.onemsg.protobuf.manager.util.StringUtil;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationRouteHandler {

    public static final String PATH = "/api/application";

    private final ApplicationDetailService service;

    public ApplicationRouteHandler(ApplicationDetailService service) {
        this.service = Objects.requireNonNull(service);
    }

    public static ApplicationRouteHandler create(ApplicationDetailService service) {
        return new ApplicationRouteHandler(service);
    }

    public void mount(Router router) {
        router.get("/api/application/:id").handler(this::get);
        router.get("/api/application").handler(this::getList);
        router.post("/api/application").handler(this::post);
        router.put("/api/application/:id").handler(this::put);
        router.delete("/api/application/:id").handler(this::delete);

        log.info("{} has mounted route /api/application", this);
    }

    private void get(RoutingContext ctx) {
        try {
            long id = Long.parseLong(ctx.pathParam("id"));
            var app = service.get(id);
            ctx.json(app);
        } catch (NumberFormatException e) {
            throw new StatusResponseException(400, "Invalid path param [id]");
        } catch (NoSuchElementException e) {
            throw new StatusResponseException(404, "Resource not existed");
        }
    }

    private void getList(RoutingContext ctx) {
        int pageIndex = NumberUtil.parseInt(ctx.queryParams().get("pageIndex"), 1);
        int pageSize = NumberUtil.parseInt(ctx.queryParams().get("pageSize"), 20);
        var totalable = service.get(pageIndex, pageSize);
        ctx.json(PageableModel.of(totalable, pageIndex, pageSize));
    }

    private void post(RoutingContext ctx) {
        var app = ctx.body().asPojo(ApplicationDetail.class);
        if (!checkApplicationDetail(app)) {
            throw new StatusResponseException(400, "Invalid data");
        }
        String creator = ctx.user().get("name");
        service.create(app.group, app.name, app.intro, creator);
        log.info("Create new {}", app);
        ctx.response().setStatusCode(201).end();
    }

    private void put(RoutingContext ctx) {
        try {
            long id = Long.parseLong(ctx.pathParam("id"));
            if (!ctx.body().asJsonObject().containsKey("intro")) {
                throw new StatusResponseException(400, "Invalid data");
            }
            var intro = ctx.body().asJsonObject().getString("intro");
            service.updateIntro(id, intro);
            ctx.response().setStatusCode(200).end();
        } catch (NumberFormatException e) {
            throw new StatusResponseException(400, "Invalid path param [id]");
        } catch (NoSuchElementException e) {
            throw new StatusResponseException(404, "Resource not existed");
        }

    }

    private void delete(RoutingContext ctx) {
        try {
            long id = Long.parseLong(ctx.pathParam("id"));
            service.delete(id);
            ctx.response().setStatusCode(200).end();
        } catch (NumberFormatException e) {
            throw new StatusResponseException(400, "Invalid path param [id]");
        }
    }

    private static boolean checkApplicationDetail(ApplicationDetail app) {
        return !StringUtil.isBlank(app.group, app.name);
    }

}
