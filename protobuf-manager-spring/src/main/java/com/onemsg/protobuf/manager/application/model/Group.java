package com.onemsg.protobuf.manager.application.model;

import java.time.LocalDateTime;

public class Group {

    public static class Entity {
        public int id;
        public String name;
        public String intro;
        public LocalDateTime createdTime;
        public LocalDateTime updatedTime;  
    }

    public record NameVo(
        int id,
        String name
    ) { 
        public static NameVo create(Entity entity) {
            return new NameVo(entity.id, entity.name);
        }
    }
}
