package com.onemsg.protobuf.manager.protobuf.model;

import java.time.LocalDateTime;

import com.onemsg.protobuf.manager.util.ProtobufInfoUtil;

import lombok.ToString;

@ToString
public class ProtobufCodeEntity {

    public int id;
    public int protobufId;
    public String protobufName;
    public String code;
    public int version;
    public String creator;
    public LocalDateTime createdTime;

    public String getVersionText() {
        return ProtobufInfoUtil.versionAsText(version);
    }
}
