package com.onemsg.protobuf.manager.web;

import java.util.Map;

public record DataModel(
    int code,
    String message,
    Object data
) {
    
    public static DataModel ok() {
        return ok("OK");
    }

    public static DataModel createdOK() {
        return ok("Created OK");
    }

    public static DataModel createdOK(int id) {
        return ok("Created OK", Map.of("id", id));
    }

    public static DataModel updatedOK() {
        return ok("Updated OK");
    }

    public static DataModel updatedOK(int id) {
        return ok("Updated OK", Map.of("id", id));
    }

    public static DataModel deletedOK() {
        return ok("Delete OK");
    }

    public static DataModel deletedOK(int id) {
        return ok("Delete OK", Map.of("id", id));
    }

    public static DataModel ok(String message) {
        return ok(message, null);
    }

    public static DataModel ok(Object data) {
        return ok("OK", data);
    }

    public static DataModel ok(String message, Object data) {
        return new DataModel(0, message, data);
    }

    public static DataModel badParams(String message) {
        return new DataModel(400, message, null);
    }

    public static DataModel notExist(String message) {
        return new DataModel(404, message, null);
    }
}
