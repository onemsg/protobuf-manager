package com.onemsg.protobuf.manager.web;

import java.util.List;
import java.util.Objects;

import com.onemsg.protobuf.manager.application.ApplicationDetail;
import com.onemsg.protobuf.manager.application.ApplicationDetailService;
import com.onemsg.protobuf.manager.exception.ResourceNotExistException;
import com.onemsg.protobuf.manager.exception.StatusResponseException;
import com.onemsg.protobuf.manager.model.ProtoDetailVo;
import com.onemsg.protobuf.manager.model.Totalable;
import com.onemsg.protobuf.manager.model.web.PageParam;
import com.onemsg.protobuf.manager.model.web.PageableModel;
import com.onemsg.protobuf.manager.proto.ProtoDetail;
import com.onemsg.protobuf.manager.proto.ProtoFile;
import com.onemsg.protobuf.manager.proto.ProtoService;
import com.onemsg.protobuf.manager.util.NumberUtil;
import com.onemsg.protobuf.manager.util.StringUtil;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * Protobuf HTTP endpoint
 */
public class ProtoRouteHandler {

    private final ProtoService protoService;
    private final ApplicationDetailService applicationService;

    private ProtoRouteHandler(ProtoService protoService, ApplicationDetailService applicationService) {
        this.protoService = Objects.requireNonNull(protoService);
        this.applicationService = Objects.requireNonNull(applicationService);
    }

    public static ProtoRouteHandler create(ProtoService protoService, ApplicationDetailService applicationService) {
        return new ProtoRouteHandler(protoService, applicationService);
    }

    public void mount(Router router) {

        router.get("/api/protobuf/:id").handler(this::handleGet);
        router.get("/api/protobuf").handler(this::handleList);
        router.post("/api/protobuf").handler(this::handlePost);
        router.put("/api/protobuf/:id/currentVersion").handler(this::handlePutCurrentVersion);
        router.delete("/api/protobuf/:id").handler(this::handleDelete);
        router.get("/api/protobuf/:id/file/:version").handler(this::handleFileGet);
        router.get("/api/protobuf/:id/file").handler(this::handleFileList);
        router.post("/api/protobuf/:id/file").handler(this::handleFilePost);
    }

    private void handleGet(RoutingContext ctx) {
        long id = toLong(ctx.pathParam("id"), "路径参数 [id] 无效");
        var protoDetail = protoService.requireExist(id);

        var applicationDetail = applicationService.get(protoDetail.applicationId);
        if (applicationDetail == null) {
            throw StatusResponseException.build(404, "Protobuf [%s]'s application [%s] not found", id,
                    protoDetail.applicationId);
        }

        JsonObject data = ProtoDetailVo.build(protoDetail, applicationDetail);
        ctx.json(data);
    }

    private void handleList(RoutingContext ctx) {

        MultiMap queryParams = ctx.queryParams();
        String search = queryParams.get("search");
        PageParam pageParam = parsePageParam(ctx);

        Totalable<ProtoDetail> totalable = null;

        // 搜索，按照 group、application、name、owner
        if (!StringUtil.isBlank(search)) {
            totalable = protoService.find(proto -> {
                ApplicationDetail applicationDetail = applicationService.get(proto.applicationId);
                return StringUtil.anyContains(search.strip(),
                        applicationDetail.group,
                        applicationDetail.name,
                        proto.name,
                        proto.owner);
            }, pageParam.offset(), pageParam.limit());
        } else {
            totalable = protoService.find(pageParam.offset(), pageParam.limit());
        }
 
        List<JsonObject> list = totalable.list().stream()
            .map(protoDetail -> {
                var applicationDetail = applicationService.get(protoDetail.applicationId);
                if (applicationDetail == null) {
                    throw StatusResponseException.build(404, "Protobuf [%s]'s application [%s] not found", 
                            protoDetail.id,
                            protoDetail.applicationId);
                }
                return ProtoDetailVo.build(protoDetail, applicationDetail);
            }).toList();

        ctx.json(PageableModel.of(Totalable.of(list, totalable.total()), pageParam.pageIndex(), pageParam.pageSize()));
    }

    private void handlePost(RoutingContext ctx) {

        JsonObject data = ctx.body().asJsonObject();
        assertProtoDetailCreate(data);

        ProtoDetail protoDetail = new ProtoDetail();
        protoDetail.applicationId = data.getLong("applicationId");
        protoDetail.name = data.getString("name");
        protoDetail.intro = data.getString("intro");
        protoDetail.owner = ctx.user().get("name");
        protoDetail.lastModifier = ctx.user().get("name");

        protoService.create(protoDetail);
        ctx.response()
                .setStatusCode(201)
                .putHeader("Location", "/api/protobuf/" + protoDetail.id);
    }

    private void handlePutCurrentVersion(RoutingContext ctx) {
        long id = toLong(ctx.pathParam("id"), "路径参数 [id] 无效");
        int version = 0;
        try {
            version = ctx.body().asPojo(int.class);
        } catch (Exception e) {
            throw new StatusResponseException(400, "请求体错误");
        }
        protoService.updateCurrentProtoFileVersion(id, version);
        ctx.end();
    }

    private void handleDelete(RoutingContext ctx) {
        long id = toLong(ctx.pathParam("id"), "路径参数 [id] 无效");
        try {
            protoService.delete(id);
        } catch (ResourceNotExistException e) {
            throw e.toStatusResponseException();
        }
        ctx.end();
    }

    private void handleFileGet(RoutingContext ctx) {
        long id = toLong(ctx.pathParam("id"), "路径参数 [id] 无效");
        String version = ctx.pathParam("version");

        ProtoFile protoFile = null;

        if (Objects.equals(version, "current")) {
            protoFile = protoService.findCurrentProtoFile(id);
        } else {
            protoFile = protoService.findProtoFile(id, toInt(version, "请求参数 [version] 无效"));
        }
        ctx.json(protoFile);
    }

    private void handleFileList(RoutingContext ctx) {
        long id = toLong(ctx.pathParam("id"), "路径参数 [id] 无效");
        var list = protoService.findProtoFiles(id);
        ctx.json(list);
    }

    private void handleFilePost(RoutingContext ctx) {
        long id = toLong(ctx.pathParam("id"), "路径参数 [id] 无效");
        JsonObject data = ctx.body().asJsonObject();
        assertProtoFileCreate(data);
        String text = data.getString("text");
        String username = ctx.user().get("name");
        ProtoFile protoFile = protoService.createProtoFile(id, text, username);
        ctx.response().setStatusCode(201);
        ctx.json(protoFile);
    }

    private static PageParam parsePageParam(RoutingContext ctx) {
        MultiMap queryParams = ctx.queryParams();
        int pageIndex = NumberUtil.parseInt(queryParams.get("pageIndex"), 1);
        int pageSize = NumberUtil.parseInt(queryParams.get("pageSize"), 20);

        pageIndex = pageIndex < 1 ? 1 : pageIndex;
        pageSize = pageSize < 1 ? 20 : pageSize;

        return new PageParam(pageIndex, pageSize);
    }

    private void assertProtoDetailCreate(JsonObject data) throws StatusResponseException {
        if (data == null || data.isEmpty()) {
            throw StatusResponseException.build(400, "请求体不能为空");
        }
        if (applicationService.get(data.getInteger("applicationId")) == null) {
            throw StatusResponseException.build(400, "ApplicationId [%s] invalid", data.getInteger("applicationId"));
        }
        if (StringUtil.isBlank(data.getString("name"))) {
            throw StatusResponseException.build(400, "Field [name] invalid");
        }
    }

    private void assertProtoFileCreate(JsonObject data) throws StatusResponseException {
        if (data == null || data.isEmpty()) {
            throw StatusResponseException.build(400, "请求体不能为空");
        }
        if (StringUtil.isBlank(data.getString("text"))) {
            throw StatusResponseException.build(400, "Field [text] invalid");
        }
    }

    private static int toInt(String s, String message) throws StatusResponseException {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new StatusResponseException(400, message);
        }
    }

    private static long toLong(String s, String message) throws StatusResponseException {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new StatusResponseException(400, message);
        }
    }
}
