package com.onemsg.protobuf.manager.protobuf.model;

import jakarta.validation.constraints.NotBlank;
import lombok.ToString;

@ToString
public class ProtobufInfoCreationRequest {
    
    @NotBlank
    public String name;
    public String intro;
    @NotBlank
    public String applicationName;
    @NotBlank
    public String groupName;
    @NotBlank
    public String protocol;

}
