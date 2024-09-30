package com.onemsg.protobuf.manager.application.model;

public record ApplicationNameVo(
    int id,
    String name
) {
    
    public static ApplicationNameVo create(ApplicationEntity entity) {
        return new ApplicationNameVo(entity.id, entity.name);
    }
}
