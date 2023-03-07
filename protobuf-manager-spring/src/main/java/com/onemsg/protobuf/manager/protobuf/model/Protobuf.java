package com.onemsg.protobuf.manager.protobuf.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class Protobuf {

    private Protobuf() {}

    public static class Entity {
        public int id;
        public int applicationId;
        public String name;
        public String intro;
        public String protocol;
        public int currentVersion;
        public String creator;
        public LocalDateTime createdTime;
        public LocalDateTime updatedTime;
    }

    public static class SearchVo {

        public int id;
        public String group;
        public String application;
        public String name;
        public String protocol;
        public int currentVersion;
        public String creator;
        public LocalDateTime updatedTime;

        public static SearchVo create(Entity entity) {
            SearchVo searchVo = new SearchVo();
            searchVo.id = entity.id;
            searchVo.name = entity.name;
            searchVo.protocol = entity.protocol;
            searchVo.currentVersion = entity.currentVersion;
            searchVo.creator = entity.creator;
            searchVo.updatedTime = entity.updatedTime;
            return searchVo; 
        }

        public String getPath() {
            return String.join("/", group, application, name);
        }

        public String getCurrentVersionText() {
            return versionAsText(currentVersion);
        }
    }

    public static class DetailVo {

        public int id;
        public String group;
        public String application;
        public String name;
        public String intro;
        public String protocol;
        public int currentVersion;
        public String creator;
        public LocalDateTime updatedTime;

        public static DetailVo create(Entity entity) {
            DetailVo detailVo = new DetailVo();
            detailVo.id = entity.id;
            detailVo.name = entity.name;
            detailVo.intro = entity.intro;
            detailVo.protocol = entity.protocol;
            detailVo.currentVersion = entity.currentVersion;
            detailVo.creator = entity.creator;
            detailVo.updatedTime = entity.updatedTime;
            return detailVo; 
        }

        public String getPath() {
            return String.join("/", group, application, name);
        }

        public String getCurrentVersionText() {
            return versionAsText(currentVersion);
        }
    }

    public static class Creation {
        @Min(1)
        public int applicationId;
        @NotBlank
        public String name;
        public String intro;
        @NotBlank
        public String protocol;
        @NotBlank
        public String creator;
    }

    public static class UpdateIntro {
        @NotBlank
        public String intro;
    }

    public static class UpdateVersion {
        @Min(101)
        public int version;
    }

    public static String versionAsText(int version) {
        if (version == 0)
            return "";
        return String.join(".", String.valueOf(version).split(""));
    }
}
