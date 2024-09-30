package com.onemsg.protobuf.manager.protobuf.model;

import jakarta.validation.constraints.Positive;

public class ProtobufInfoUpdateVersionRequest {
    
    @Positive
    public int verison;
}
