package com.onemsg.protobuf.manager.model;

import com.onemsg.protobuf.manager.application.ApplicationDetail;
import com.onemsg.protobuf.manager.proto.ProtoDetail;

import io.vertx.core.json.JsonObject;

public class ProtoDetailVo {
    
    private ProtoDetailVo() {}

    public static JsonObject build(ProtoDetail protoDetail, ApplicationDetail applicationDetail) {

        return new JsonObject()
            .put("id", protoDetail.id)
            .put("group", applicationDetail.group)
            .put("application", applicationDetail.name)
            .put("name", protoDetail.name)
            .put("intro", protoDetail.intro)
            .put("currentVersion", protoDetail.getCurrentVersionAsText())
            .put("owner", protoDetail.owner)
            .put("created", protoDetail.created)
            .put("modified", protoDetail.modified);
    }
}
