package com.onemsg.protobuf.manager.application.model;

import jakarta.validation.constraints.NotBlank;

public class GroupCreationRequest {
    @NotBlank
    public String name;
}
