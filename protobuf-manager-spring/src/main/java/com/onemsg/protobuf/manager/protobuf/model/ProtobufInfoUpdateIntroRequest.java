package com.onemsg.protobuf.manager.protobuf.model;

import jakarta.validation.constraints.NotBlank;

public class ProtobufInfoUpdateIntroRequest {
    
    @NotBlank
    public String intro;
}
