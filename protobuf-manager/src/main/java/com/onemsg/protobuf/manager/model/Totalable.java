package com.onemsg.protobuf.manager.model;

import java.util.List;

public record Totalable<T> (
        List<T> list,
        long total) {

    public static <T> Totalable<T> of(List<T> list, long total) {
        return new Totalable<>(list, total);
    }

}
