package com.onemsg.protobuf.manager.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.onemsg.protobuf.manager.exception.DataModelResponseException;
import com.onemsg.protobuf.manager.exception.NotExistedException;

/**
 * WEB 全局异常处理器
 * 
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler(DataModelResponseException.class)
    public ResponseEntity<Object> handleDataModelResponseException(DataModelResponseException exception){
        return ResponseEntity.status(exception.getStatus()).body(exception.toJson());
    }

    @ExceptionHandler(NotExistedException.class)
    public ResponseEntity<Object> handleNotExistedException(NotExistedException exception) {
        var e = new DataModelResponseException(exception);
        return ResponseEntity.status(e.getStatus()).body(e.toJson());
    }
}
