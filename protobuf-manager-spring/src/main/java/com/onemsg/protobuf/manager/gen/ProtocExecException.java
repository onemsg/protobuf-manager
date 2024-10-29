package com.onemsg.protobuf.manager.gen;

import lombok.Getter;

@Getter
public class ProtocExecException extends Exception {

    private final Integer exitStatus;
    private final String errorDetail;

    public ProtocExecException(String message) {
        super(message);
        exitStatus = null;
        errorDetail = null;
    }

    public ProtocExecException(String message, Integer exitStatus, String errorDetail) {
        super(message + ": " + "exitStatus=" + exitStatus + "\n" + errorDetail);
        this.exitStatus = exitStatus;
        this.errorDetail = errorDetail;
    }
}
