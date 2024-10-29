package com.onemsg.protobuf.manager.protobuf.model;

import jakarta.validation.constraints.Positive;

public record GenerateClientJarRequest(
    @Positive
    int protobufId,
    Integer version,
    String groupId,
    String artifactId
) {
    
}
