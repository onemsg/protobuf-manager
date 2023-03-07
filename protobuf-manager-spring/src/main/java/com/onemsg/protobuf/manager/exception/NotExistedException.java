package com.onemsg.protobuf.manager.exception;

import lombok.Getter;

/**
 * 资源不存在异常
 */
@Getter
public class NotExistedException extends Exception{
    
    private final String type;
    private final int id;

    public NotExistedException(String type, int id) {
        this.type = type;
        this.id = id;
    }

    @Override
    public String getMessage() {
        return String.format("Resource %s[%s] not existed", type, id);
    }
}
