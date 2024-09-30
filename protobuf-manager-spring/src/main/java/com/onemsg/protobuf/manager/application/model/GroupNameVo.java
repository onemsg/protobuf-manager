package com.onemsg.protobuf.manager.application.model;

public record GroupNameVo(int id, String name) {

    public static GroupNameVo create(GroupEntity entity) {
        return new GroupNameVo(entity.id(), entity.name());
    }
}
