package com.onemsg.protobuf.manager.user;

import java.util.Objects;

import io.vertx.core.json.JsonObject;

/**
 * SimpleUser
 */
public record SimpleUser(
    long id,
    String name, 
    String avatarUrl) {

    public JsonObject toJson() {
        return new JsonObject().
            put("id", id).
            put("name", name)
            .put("avatarUrl", avatarUrl);
    }

    public static SimpleUser fromJson(JsonObject json) {
        Objects.requireNonNull(json);
        return new SimpleUser(json.getLong("id"), json.getString("name"), json.getString("avatarUrl"));
    }
}