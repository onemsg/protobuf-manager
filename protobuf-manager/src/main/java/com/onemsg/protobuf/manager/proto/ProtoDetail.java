package com.onemsg.protobuf.manager.proto;

import java.time.LocalDateTime;
import java.util.Comparator;

public class ProtoDetail {

    public static final Comparator<ProtoDetail> CPR_ID = (p1, p2) -> (int) (p1.id - p2.id);

    public long id;

    public long applicationId;

    public String name;

    public String intro;

    public int currentVersion; // 当前 proto file version

    public String owner;

    public String lastModifier; // 上次修改人

    public LocalDateTime created; // 创建

    public LocalDateTime modified; // 最新修改时间
    
    public String getCurrentVersionAsText() {
        if (currentVersion == 0) return "";
        return String.join(".", String.valueOf(currentVersion).split(""));
    }
}