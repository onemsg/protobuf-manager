package com.onemsg.protobuf.manager.exception;

/**
 * 资源不存在异常
 */
public class ResourceNotExistException extends RuntimeException {

    private final long resourceId;

    public ResourceNotExistException(String message) {
        this(message, 0);
    }

    public ResourceNotExistException(String message, long resourceId) {
        super(String.format(message, resourceId));
        this.resourceId = resourceId;
    }

    public long resourceId() {
        return resourceId;
    }

    public StatusResponseException toStatusResponseException() {
        return StatusResponseException.build(404, getMessage());
    }

}
