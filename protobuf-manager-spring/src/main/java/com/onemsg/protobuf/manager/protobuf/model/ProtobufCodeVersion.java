package com.onemsg.protobuf.manager.protobuf.model;

import java.time.LocalDateTime;

import com.onemsg.protobuf.manager.util.ProtobufInfoUtil;

public class ProtobufCodeVersion {
    
    public int id;
    public int version;
    public boolean isCurrent = false;
    public String creator;
    public LocalDateTime createdTime;

    public String getVersionText() {
        return ProtobufInfoUtil.versionAsText(version);
    }
}
