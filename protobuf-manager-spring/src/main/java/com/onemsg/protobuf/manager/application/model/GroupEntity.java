package com.onemsg.protobuf.manager.application.model;

import java.time.LocalDateTime;

/**
 * ApplicationEntity
 */
public record GroupEntity(
    int id,
    String name,
    String creator,
    LocalDateTime create
) {
}