package com.onemsg.protobuf.manager.protobuf.model;

import java.util.Objects;

import org.springframework.beans.BeanUtils;

import com.onemsg.protobuf.manager.util.ProtobufInfoUtil;

/**
 * ProtobufCodeCreation
 */
public class ProtobufCodeCreation {
    public int protobufId;
    public String protobufName;
    public String code;
    public int version;
    public String creator;

    public String getVersionText() {
        return ProtobufInfoUtil.versionAsText(version);
    }

    public static ProtobufCodeCreation create(ProtobufCodeCreationRequest request, String creator) {
        Objects.requireNonNull(request);
        ProtobufCodeCreation o = new ProtobufCodeCreation();
        o.protobufId = request.protobufId;
        o.code = request.code;
        o.creator = creator;
        return o;
    }
}