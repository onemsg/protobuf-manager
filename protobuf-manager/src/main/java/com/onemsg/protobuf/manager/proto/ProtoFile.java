package com.onemsg.protobuf.manager.proto;

import java.time.LocalDateTime;
import java.util.Comparator;

/**
 * Proto file
 * <p>
 * protoId + version 确定唯一主键
 */
public record ProtoFile (
        long protoId,
        int version, // 101 102 103 ...
        String text, // pb 文本
        String creator,
        LocalDateTime created) {

    public static final Comparator<ProtoFile> comparator = (p1, p2) -> p1.version - p2.version;
}