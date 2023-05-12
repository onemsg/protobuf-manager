package com.onemsg.protobuf.manager.application.model;

import java.time.LocalDateTime;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class Group {

    public static class Entity {
        public int id;
        public String name;
        public String intro;
        public String creator;
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

    public record CreationRequest(
        @NotBlank String name,
        @Nullable String intro) {
    }

    public record UpdateIntroRequest(
        @NotNull String intro) {
    }
}
