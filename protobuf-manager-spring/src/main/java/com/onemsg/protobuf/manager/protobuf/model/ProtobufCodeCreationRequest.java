package com.onemsg.protobuf.manager.protobuf.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class ProtobufCodeCreationRequest {
    @Positive
    public int protobufId;
    @NotBlank
    public String code;
}
