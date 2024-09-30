package com.onemsg.protobuf.manager.application.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class ApplicationCreationRequest {
    
    @NotBlank
    public String name; 
    public String zhName;
    public String intro;
    @Positive
    public int groupId;
}
