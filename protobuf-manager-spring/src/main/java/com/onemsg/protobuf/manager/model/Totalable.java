package com.onemsg.protobuf.manager.model;

import java.util.List;

public record Totalable<T> (
    List<T> data,
    int total
) {
    
    public static <T> Totalable<T> create(List<T> data, int total) {
        return new Totalable<>(data, total);
    }
}
