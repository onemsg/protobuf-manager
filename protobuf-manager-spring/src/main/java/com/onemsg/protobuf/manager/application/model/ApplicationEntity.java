package com.onemsg.protobuf.manager.application.model;

import java.time.LocalDateTime;

public class ApplicationEntity {
    
    public int id;
    public String name;
    public String zhName;
    public String intro;
    public int groupId;
    public String groupName;
    public String creator;
    public LocalDateTime createdTime;
    public LocalDateTime updatedTime;

    public int getId() {
        return id;
    }
}
