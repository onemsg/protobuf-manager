package com.onemsg.protobuf.manager.exception;

import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;


/**
 * BeanErrorException
 */
public class BeanErrorException extends RuntimeException {

    private final String message;

    private final Set<String> details; 

    public BeanErrorException(String message, Set<String> details){
        super(message + ": " + details);
        this.message = message;
        this.details = details; 
    }

    public static <T> BeanErrorException create(String message, Set<ConstraintViolation<T>> errors){
        Set<String> errorSet = errors.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
        return new BeanErrorException(message, errorSet);
    }

    public String message(){
        return message;
    }

    public Set<String> details(){
        return details;
    }
}