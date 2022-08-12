package com.onemsg.protobuf.manager.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class ModelValidator {
    
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private ModelValidator(){}

    public static Validator validator(){
        return validator;
    }
}
