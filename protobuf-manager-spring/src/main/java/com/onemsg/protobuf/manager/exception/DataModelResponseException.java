package com.onemsg.protobuf.manager.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolationException;
import lombok.Getter;

/**
 * StatusResponseException
 */
@Getter
public class DataModelResponseException extends RuntimeException {

    /** 认证失败异常 */
    public static final DataModelResponseException AUTHENTICATION_FAILURE = new DataModelResponseException(401, 401, 
        "Authentication failure");

    /** 鉴权失败异常 */
    public static final DataModelResponseException AUTHORIZATION_FAILURE = new DataModelResponseException(403, 403,
            "Authorization failure");

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

    public DataModelResponseException(ConstraintViolationException exception) {
        this.status = 400;
        this.code = 400;
        this.message = "Bad request body";
        var errors = exception.getConstraintViolations().stream()
            .collect(Collectors.toMap(e -> e.getPropertyPath().toString(), e -> e.getMessage()));
        addProperty("errors", errors);
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