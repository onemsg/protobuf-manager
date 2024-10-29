package com.onemsg.protobuf.manager.function;

@FunctionalInterface
public interface CheckedRunnable<E extends Exception> {

    void run() throws E;

}
