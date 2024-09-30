package com.onemsg.protobuf.manager.application.model;

import java.util.Objects;

import org.springframework.beans.BeanUtils;

public class ApplicationCreation {
    public String name;
    public String zhName;
    public String intro;
    public int groupId;
    public String groupName;
    public String creator;

    public String getFullName() {
        return groupName + "/" + name;
    }

    public static ApplicationCreation create(ApplicationCreationRequest request) {
        Objects.requireNonNull(request);
        ApplicationCreation creation = new ApplicationCreation();
        BeanUtils.copyProperties(request, creation);
        return creation;
    }
}
