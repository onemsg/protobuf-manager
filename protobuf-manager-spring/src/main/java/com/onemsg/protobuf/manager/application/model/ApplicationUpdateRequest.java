package com.onemsg.protobuf.manager.application.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class ApplicationUpdateRequest {
    @Positive
    public int applicationId;
    @NotBlank
    public String zhName;
    public String intro;
}
