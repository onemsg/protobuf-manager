package com.onemsg.protobuf.manager.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;

/**
 * StatusResponseException
 */
@Getter
public class DataModelResponseException extends RuntimeException {

    private final int status;
    private final int code;
    private final String message;
    private transient Map<String, Object> properties;

    public DataModelResponseException(int status, int code, String message) {
        this(status, code, message, null);
    }

    public DataModelResponseException(int status, int code, String message, Map<String, Object> properties) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.properties =properties;
    }

    public DataModelResponseException(NotExistedException exception) {
        this.status = 404;
        this.code = 404;
        this.message = exception.getMessage();
        addProperty("resouceId", exception.getId());
    }

    public Map<String, Object> toJson() {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("code", code);
        json.put("message", message);
        if (properties != null) {
            json.putAll(properties);
        }
        return json;
    }

    public DataModelResponseException addProperty(String name, Object value) {
        this.properties = this.properties != null ? this.properties : new LinkedHashMap<>();
        this.properties.put(name, value);
        return this;
    }
}