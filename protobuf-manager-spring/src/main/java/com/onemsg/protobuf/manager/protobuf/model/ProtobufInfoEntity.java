package com.onemsg.protobuf.manager.protobuf.model;

import java.time.LocalDateTime;

import com.onemsg.protobuf.manager.util.ProtobufInfoUtil;

/**
 * ProtobufInfoEntity
 */
public class ProtobufInfoEntity {

    public int id;
    public String name;
    public String intro;
    public String applicationName;
    public String groupName;
    public String protocol;
    public int currentVersion;
    public String creator;
    public LocalDateTime createdTime;
    public LocalDateTime updatedTime;

    public String getFullName() {
        return groupName + "/" + applicationName + "/" + name;
    }

    public String getCurrentVersionText() {
        return ProtobufInfoUtil.versionAsText(currentVersion);
    }
}