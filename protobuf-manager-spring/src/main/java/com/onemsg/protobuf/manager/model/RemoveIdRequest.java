package com.onemsg.protobuf.manager.model;

import jakarta.validation.constraints.Positive;

public class RemoveIdRequest {
    @Positive
    public int id;
}
