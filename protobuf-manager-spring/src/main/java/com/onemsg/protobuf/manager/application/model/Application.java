package com.onemsg.protobuf.manager.application.model;

import java.time.LocalDateTime;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class Application {

    public static class Entity {
        public int id;
        public String name;
        public String intro;
        public int groupId;
        public String creator;
        public LocalDateTime createdTime;
        public LocalDateTime updatedTime;
    }

    public static class Model{

        public int id;
        public String name;
        public String intro;
        public int groupId;
        public String groupName;
        public String creator;
        public LocalDateTime createdTime;
        public LocalDateTime updatedTime;

        public static Model create(Entity entity) {
            Model model = new Model();
            model.id = entity.id;
            model.name = entity.name;
            model.intro = entity.intro;
            model.groupId = entity.groupId;
            model.creator = entity.creator;
            model.createdTime = entity.createdTime;
            model.updatedTime = entity.updatedTime;
            return model;
        }
    }

    public record NameVo(
        int id,
        String name
    ) {
        public static NameVo create(Entity entity) {
            return new NameVo(entity.id, entity.name);
        }
    }

    public record Creation(
        @NotBlank
        String name,
        @Nullable
        String intro,
        @Min(1)
        int groupId,
        @NotBlank
        String creator
    ) {
        public Entity toEntity() {
            Entity entity = new Entity();
            entity.name = name;
            entity.intro = intro;
            entity.groupId = groupId;
            entity.creator = creator;
            return entity;
        }
    }

    public record UpdateIntro(
        String intro
    ) { }

}
