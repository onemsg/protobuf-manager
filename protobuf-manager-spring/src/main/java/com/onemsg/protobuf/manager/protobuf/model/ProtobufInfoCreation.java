package com.onemsg.protobuf.manager.protobuf.model;

import java.util.Objects;

import org.springframework.beans.BeanUtils;

import lombok.ToString;

@ToString
public class ProtobufInfoCreation {

    public String name;
    public String intro;
    public String applicationName;
    public String groupName;
    public String protocol;
    public String creator;

    public static ProtobufInfoCreation create(ProtobufInfoCreationRequest request, String creator) {
        Objects.requireNonNull(request);
        ProtobufInfoCreation o = new ProtobufInfoCreation();
        o.name = request.name;
        o.intro = request.intro;
        o.applicationName = request.applicationName;
        o.groupName = request.groupName;
        o.protocol = request.protocol;
        o.creator = creator;
        return o;
    }
}
