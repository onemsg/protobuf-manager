package com.onemsg.protobuf.manager.protobuf.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class ProtobufCode {

    private ProtobufCode() {}

    public static class Entity {
        public int id;
        public int protoId;
        public String code;
        public int version;
        public String creator;
        public LocalDateTime createdTime;
    }


    public static class ItemVo {
        public int id;
        public int protoId;
        public int version;
        public boolean isCurrent = false;
        public String creator;
        public LocalDateTime createdTime;

        public String getVersionText() {
            return Protobuf.versionAsText(version);
        }
    }

    public static class DetailVo {
        public int id;
        public int protoId;
        public String code;
        public int version;
        public String creator;
        public LocalDateTime createdTime;

        public String getVersionText() {
            return Protobuf.versionAsText(version);
        }
    }

    public static class Creation {
        @Min(1)
        public int protoId;
        @NotBlank
        public String code;
    }
}
